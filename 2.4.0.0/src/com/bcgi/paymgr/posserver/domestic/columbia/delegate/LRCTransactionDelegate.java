package com.bcgi.paymgr.posserver.domestic.columbia.delegate;

import java.util.Calendar;
import java.util.Properties;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.DateUtil;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.common.util.PropertiesReader;
import com.bcgi.paymgr.posserver.domestic.columbia.rr.LRCSyncPolicyObject;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.rr.RRDomesticService;
import com.bcgi.paymgr.posserver.domestic.common.util.Utility;
import com.bcgi.pmcore.dto.ComponentTrackerDTO;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;
import com.bcgi.pmcore.ejb.domestictransaction.columbia.DomesticColumbiaPOSTransFacade;
import com.bcgi.rrobin.exception.MandatoryParameterException;
import com.bcgi.rrobin.policy.LoadBalancePolicy;
import com.bcgi.rrobin.proxy.EJBProxy;
import java.rmi.UnmarshalException;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.bcgi.paymgr.posserver.domestic.columbia.constant.PM_DMPOSGW_MapConstant;

public class LRCTransactionDelegate {



	DomesticColumbiaPOSTransFacade facadeBeanRemote = null;
	static LoadBalancePolicy lrcSyncLoadBalancePolicy = null;
	RRDomesticService rrDomesticService = null;
	EJBProxy ejbProxy = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRCTransactionDelegate.class);

	public LRCTransactionDelegate(String financialRefNum) throws DelegateException  {

		try
		{
			logger.info(" LRCTransactionDelegate Constructor Calling - Financial Reference Number ====> "+financialRefNum);
			rrDomesticService = new RRDomesticService();
			ejbProxy = EJBProxy.getInstance();
				if(lrcSyncLoadBalancePolicy == null) {

					lrcSyncLoadBalancePolicy = LRCSyncPolicyObject.getInstance();
					try {
						ejbProxy.register(ServerConfigConstant.RR_LR_C_SYNC_REQUEST_TYPE,lrcSyncLoadBalancePolicy);
						ejbProxy.callEJBProxyTimer(rrDomesticService.getPropertyValueToConnectPM("wakeup"));
					} catch (MandatoryParameterException e1) {
						throw new DelegateException(e1.getMessage());
					}
				}
				long starttime = System.currentTimeMillis();
				facadeBeanRemote = (DomesticColumbiaPOSTransFacade)rrDomesticService.getRemoteObject(ServerConfigConstant.RR_LR_C_SYNC_REQUEST_TYPE, financialRefNum);
				long endtime = System.currentTimeMillis();
				long totaltime = endtime - starttime;
				logger.info("Total Time in Miilis ==> "+totaltime);
				logger.info("facadeBeanRemote==> "+facadeBeanRemote);

		}
		catch(Exception ex)
		{
			lrcSyncLoadBalancePolicy = null;
			logger.error(financialRefNum + "==LRCTransactionDelegate EXCEPTION IN CATCH OF STICKY IN EXCEPTION BLOCK:-->>" + ex.getMessage());
			String strExceptionMsg = PropertiesReader.getValue("STICKY_CONNECTION_EXCEPTION_MESSAGE");
			if (strExceptionMsg.contains(ex.getMessage())) {
				if ("Y".equalsIgnoreCase(PropertiesReader.getValue("STICKY_CONNECTION_ENABLE"))) {
					try {
						getStickyConnection();
					} catch (Exception e) {
						logger.error("LRCTransactionDelegate EXCEPTION IN CATCH OF STICKY CONN :11:-->>" + e.getMessage());
					}
				}
			} else {
				logger.error("IN LRCTransactionDelegate exception while getting sticky connection" + ex.getMessage());
				throw new DelegateException(ex.getMessage());
			}
			throw new DelegateException(ex.getMessage());
	    }
	}

	 public String insertRejectedRechargeTransaction(RejectTransactionDTO rejectTransactionDTO) throws DelegateException
	 {
		 	RejectTransactionDTO rejectRespTransactionDTO=null;
            String transactionId = "";
            String transDate="";
            ComponentTrackerDTO  componentTrackerDTO=null;
			try
			{
				logger.error("Before calling insertRejectedRechargeTransaction.....RejectTransactionDTO Details"+rejectTransactionDTO);
				//Commented and changed by sridhar.v on 19-Dec-2011 due to change in return type of insertRejectReason method
				//transactionId = facadeBeanRemote.insertRejectReason(rejectTransactionDTO);

				transDate=DateUtil.getTodaysDateAndTime();

				componentTrackerDTO=new ComponentTrackerDTO();
				componentTrackerDTO.setRrn(rejectTransactionDTO.getRrn());
				componentTrackerDTO.setTerminalId(rejectTransactionDTO.getInitiatorId().trim());
				componentTrackerDTO.setReplenishmentChannelId(rejectTransactionDTO.getReplenishmentChannelId()+"");
				componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_REQ);
				componentTrackerDTO.setTransDate(transDate);
				componentTrackerDTO.setDistrubutorId(rejectTransactionDTO.getDistributorId());
	             facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);

				if(facadeBeanRemote == null)
				{
					Utility.setErrorCode(rejectTransactionDTO);
				} else{
				rejectRespTransactionDTO = facadeBeanRemote.insertRejectReason(rejectTransactionDTO);
			     transDate=DateUtil.getTodaysDateAndTime();
			    componentTrackerDTO=new ComponentTrackerDTO();
			 //   logger.error("Before calling POSGWAY_REJ_RES.....getRrn=="+rejectTransactionDTO.getRrn());
	            componentTrackerDTO.setRrn(rejectTransactionDTO.getRrn());
	            componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_REJ_RES);
	            componentTrackerDTO.setReplenishmentChannelId(rejectTransactionDTO.getReplenishmentChannelId()+"");
	            componentTrackerDTO.setTransDate(transDate);
	            componentTrackerDTO.setTransId(rejectRespTransactionDTO.getTransId());
	             facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
				}

				if(rejectRespTransactionDTO!=null && rejectRespTransactionDTO.getAutorizationId()!=null){
					transactionId = rejectRespTransactionDTO.getAutorizationId();
				}
				//Ends
				logger.error("After calling insertRejectedRechargeTransaction.....RejectTransactionDTO Details"+rejectTransactionDTO);

			}
			catch(Exception ex)
			{
			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}

           return transactionId;
	 }
	 public TransDetDTO processRechargeInquiryTransaction(TransDetDTO transReqDetDTO) throws DelegateException {


		 Calendar startCal = null;
			try
			{
				logger.error("Before calling performSynchRechargeInquriy..... Details"+transReqDetDTO);
				/**
				 * setting the Instance Id and Instace Port numbers
				 */
				logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
				logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

				transReqDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
				transReqDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				startCal = Calendar.getInstance();
				long startTimeinMillis = startCal.getTimeInMillis();
				transReqDetDTO.setReqLandedTimeinPOSGW(startTimeinMillis);

				if(facadeBeanRemote == null)
				{
					Utility.setErrorCode(transReqDetDTO);
				} else{
				transReqDetDTO = facadeBeanRemote.performSynchRechargeInquriy(transReqDetDTO);
				}

				logger.error("After calling performSynchRechargeInquriy..... Details"+transReqDetDTO);

			}
			catch(Exception ex)
			{
			        ejbProxy.clearCache();
			        lrcSyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}
			finally
			{

				if(transReqDetDTO != null) {
//				Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(transReqDetDTO.getTransId(),transReqDetDTO.getAutorizationId(),transReqDetDTO.getFinIstutinalReferenceNumber(), startCal, endCal,"Domestic Recharge Enquiry Transaction");
					if( "0".equals(transReqDetDTO.getErrorId()) )
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_ENQ_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_ENQ_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//				 End of AppMoniter Logging
				}

			}

			return transReqDetDTO;
		}



	public TransDetDTO processMINRechargeTransaction(TransDetDTO transDetDTO) throws DelegateException {

		Calendar startCal = null;
		ComponentTrackerDTO componentTrackerDTO=null;
		String transDate=null;
		try
		{
			logger.error("Before calling processMINRechargeTransaction.....TransDetDTO Details"+transDetDTO);
			/**
			 * setting the Instance Id and Instace Port numbers
			 */
			logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
			logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

			transDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
			transDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			startCal = Calendar.getInstance();
			long startTimeinMillis = startCal.getTimeInMillis();
			transDetDTO.setReqLandedTimeinPOSGW(startTimeinMillis);

            transDate=DateUtil.getTodaysDateAndTime();

            componentTrackerDTO=new ComponentTrackerDTO();
            componentTrackerDTO.setRrn(transDetDTO.getFinIstutinalReferenceNumber());
            componentTrackerDTO.setTerminalId(transDetDTO.getSalesPersonId().trim());
            componentTrackerDTO.setReplenishmentChannelId(transDetDTO.getReplenishmentChannelId()+"");
            componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_REQ);
            componentTrackerDTO.setTransDate(transDate);
            componentTrackerDTO.setPaymentAmount(transDetDTO.getPaymentAmount()+"");
            componentTrackerDTO.setDistrubutorId(transDetDTO.getDistributorId());
            componentTrackerDTO.setCustomerId(transDetDTO.getCustomerId());

			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
                 //logger.info("==LRCTransactionDelegate=sendTransactionTimetoQueue===");
				 facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
				  //logger.info("==END==LRCTransactionDelegate=sendTransactionTimetoQueue===");

			transDetDTO = facadeBeanRemote.rechargeSubscriberThroughCash(transDetDTO);
			     transDate=DateUtil.getTodaysDateAndTime();
			    componentTrackerDTO=new ComponentTrackerDTO();
	            componentTrackerDTO.setRrn(transDetDTO.getFinIstutinalReferenceNumber());
	            componentTrackerDTO.setTerminalId(transDetDTO.getSalesPersonId().trim());
	            componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_RES);
	            componentTrackerDTO.setReplenishmentChannelId(transDetDTO.getReplenishmentChannelId()+"");
	            componentTrackerDTO.setTransDate(transDate);
	            componentTrackerDTO.setTransId(transDetDTO.getTransId());

	            facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
			}

			logger.error("After calling processMINRechargeTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(UnmarshalException ue)
		{
			logger.info("Occured Unmarshal exceptions"+ue);

			PM_DMPOSGW_MapConstant.getPOSGWResponse("0");


			if(ue.toString().indexOf("Read timed out")!=-1)
			{
				logger.info("ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE=="+ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE);

				if("Y".equals(ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE))
				{
					try
					{
						logger.info("Transaction got timedout, getting authorization id from PM");
						transDetDTO = facadeBeanRemote.getAuthorizationDetails(transDetDTO);
						transDetDTO.setErrorId("51");// setting success response,, check the value 51 in properties file
						logger.info("transDetDTO.getAutorizationId()===="+transDetDTO.getAutorizationId());
					}
					catch(Exception e)
					{
						logger.info("Exception in method facadeBeanRemote.getAuthorizationDetails...");
					}
				}
			}

			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;

			if("N".equals(ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE))
				throw new DelegateException(ue.getMessage());

		}
		catch(Exception ex)
		{
			logger.info("Transaction get exception...");
			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}
		finally
		{

			if(transDetDTO != null) {
//			Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(transDetDTO.getTransId(),transDetDTO.getAutorizationId(),transDetDTO.getFinIstutinalReferenceNumber(), startCal, endCal,"Domestic Recharge Transaction");
				if( "0".equals(transDetDTO.getErrorId()) )
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//			 End of AppMoniter Logging
			}

		}

		return transDetDTO;
	}
// coupon Recharge Redemption Transaction
	public TransDetDTO processCouponRechargeTransaction(TransDetDTO transDetDTO) throws DelegateException {

		Calendar startCal = null;
		ComponentTrackerDTO componentTrackerDTO=null;
		String transDate=null;
		try
		{
			logger.error("Before calling processCouponRechargeTransaction.....TransDetDTO Details"+transDetDTO);
			/**
			 * setting the Instance Id and Instace Port numbers
			 */
			logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
			logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

			transDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
			transDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			startCal = Calendar.getInstance();
			long startTimeinMillis = startCal.getTimeInMillis();
			transDetDTO.setReqLandedTimeinPOSGW(startTimeinMillis);

            transDate=DateUtil.getTodaysDateAndTime();

            componentTrackerDTO=new ComponentTrackerDTO();
            componentTrackerDTO.setRrn(transDetDTO.getFinIstutinalReferenceNumber());
            componentTrackerDTO.setTerminalId(transDetDTO.getSalesPersonId().trim());
            componentTrackerDTO.setReplenishmentChannelId(transDetDTO.getReplenishmentChannelId()+"");
            componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_REQ);
            componentTrackerDTO.setTransDate(transDate);
            componentTrackerDTO.setPaymentAmount(transDetDTO.getPaymentAmount()+"");
            componentTrackerDTO.setDistrubutorId(transDetDTO.getDistributorId());
            componentTrackerDTO.setCustomerId(transDetDTO.getCustomerId());
            logger.info("facadeBeanRemote : delete : "+facadeBeanRemote);
			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
                 //logger.info("==LRCTransactionDelegate=sendTransactionTimetoQueue===");
				 facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
				  //logger.info("==END==LRCTransactionDelegate=sendTransactionTimetoQueue===");

			    transDetDTO = facadeBeanRemote.couponRechargeSubscriberThroughCash(transDetDTO);
			     transDate=DateUtil.getTodaysDateAndTime();
			    componentTrackerDTO=new ComponentTrackerDTO();
	            componentTrackerDTO.setRrn(transDetDTO.getFinIstutinalReferenceNumber());
	            componentTrackerDTO.setTerminalId(transDetDTO.getSalesPersonId().trim());
	            componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_RES);
	            componentTrackerDTO.setReplenishmentChannelId(transDetDTO.getReplenishmentChannelId()+"");
	            componentTrackerDTO.setTransDate(transDate);
	            componentTrackerDTO.setTransId(transDetDTO.getTransId());

	            facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
			}

			logger.error("After calling processMINRechargeTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(UnmarshalException ue)
		{
			logger.info("Occured Unmarshal exceptions"+ue);

			PM_DMPOSGW_MapConstant.getPOSGWResponse("0");


			if(ue.toString().indexOf("Read timed out")!=-1)
			{
				logger.info("ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE=="+ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE);

				if("Y".equals(ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE))
				{
					try
					{
						logger.info("Transaction got timedout, getting authorization id from PM");
						transDetDTO = facadeBeanRemote.getAuthorizationDetails(transDetDTO);
						transDetDTO.setErrorId("51");// setting success response,, check the value 51 in properties file
						logger.info("transDetDTO.getAutorizationId()===="+transDetDTO.getAutorizationId());
					}
					catch(Exception e)
					{
						logger.info("Exception in method facadeBeanRemote.getAuthorizationDetails...");
					}
				}
			}

			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;

			if("N".equals(ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE))
				throw new DelegateException(ue.getMessage());

		}
		catch(Exception ex)
		{
			logger.info("Transaction get exception...");
			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}
		finally
		{

			if(transDetDTO != null) {
//			Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(transDetDTO.getTransId(),transDetDTO.getAutorizationId(),transDetDTO.getFinIstutinalReferenceNumber(), startCal, endCal,"Domestic Recharge Transaction");
				if( "0".equals(transDetDTO.getErrorId()) )
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//			 End of AppMoniter Logging
			}

		}

		return transDetDTO;
	}
// Brand Coupon Redemption 
	public TransDetDTO processBrandCouponRedemption(TransDetDTO transDetDTO) throws DelegateException {

		Calendar startCal = null;
		ComponentTrackerDTO componentTrackerDTO=null;
		String transDate=null;
		try
		{
			logger.error("Before calling processBrandCouponRedemption.....TransDetDTO Details"+transDetDTO);
			/**
			 * setting the Instance Id and Instace Port numbers
			 */
			logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
			logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

			transDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
			transDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			startCal = Calendar.getInstance();
			long startTimeinMillis = startCal.getTimeInMillis();
			transDetDTO.setReqLandedTimeinPOSGW(startTimeinMillis);

            transDate=DateUtil.getTodaysDateAndTime();

            componentTrackerDTO=new ComponentTrackerDTO();
            componentTrackerDTO.setRrn(transDetDTO.getFinIstutinalReferenceNumber());
            componentTrackerDTO.setTerminalId(transDetDTO.getSalesPersonId().trim());
            componentTrackerDTO.setReplenishmentChannelId(transDetDTO.getReplenishmentChannelId()+"");
            componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_REQ);
            componentTrackerDTO.setTransDate(transDate);
            componentTrackerDTO.setPaymentAmount(transDetDTO.getPaymentAmount()+"");
            componentTrackerDTO.setDistrubutorId(transDetDTO.getDistributorId());
            componentTrackerDTO.setCustomerId(transDetDTO.getCustomerId());
           
			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
                 //logger.info("==LRCTransactionDelegate=sendTransactionTimetoQueue===");
				 facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
				  //logger.info("==END==LRCTransactionDelegate=sendTransactionTimetoQueue===");

			     transDetDTO = facadeBeanRemote.doBrandCouponRedemption(transDetDTO);
			     transDate=DateUtil.getTodaysDateAndTime();
			    componentTrackerDTO=new ComponentTrackerDTO();
	            componentTrackerDTO.setRrn(transDetDTO.getFinIstutinalReferenceNumber());
	            componentTrackerDTO.setTerminalId(transDetDTO.getSalesPersonId().trim());
	            componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_RES);
	            componentTrackerDTO.setReplenishmentChannelId(transDetDTO.getReplenishmentChannelId()+"");
	            componentTrackerDTO.setTransDate(transDate);
	            componentTrackerDTO.setTransId(transDetDTO.getTransId());

	            facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
			}

			logger.error("After calling processMINRechargeTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(UnmarshalException ue)
		{
			logger.info("Occured Unmarshal exceptions"+ue);

			PM_DMPOSGW_MapConstant.getPOSGWResponse("0");


			if(ue.toString().indexOf("Read timed out")!=-1)
			{
				logger.info("ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE=="+ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE);

				if("Y".equals(ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE))
				{
					try
					{
						logger.info("Transaction got timedout, getting authorization id from PM");
						transDetDTO = facadeBeanRemote.getAuthorizationDetails(transDetDTO);
						transDetDTO.setErrorId("51");// setting success response,, check the value 51 in properties file
						logger.info("transDetDTO.getAutorizationId()===="+transDetDTO.getAutorizationId());
					}
					catch(Exception e)
					{
						logger.info("Exception in method facadeBeanRemote.getAuthorizationDetails...");
					}
				}
			}

			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;

			if("N".equals(ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE))
				throw new DelegateException(ue.getMessage());

		}
		catch(Exception ex)
		{
			logger.info("Transaction get exception...");
			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}
		finally
		{

			if(transDetDTO != null) {
//			Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(transDetDTO.getTransId(),transDetDTO.getAutorizationId(),transDetDTO.getFinIstutinalReferenceNumber(), startCal, endCal,"Domestic Recharge Transaction");
				if( "0".equals(transDetDTO.getErrorId()) )
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//			 End of AppMoniter Logging
			}

		}

		return transDetDTO;
	}

	
	
	
	
	
	
	
	
	



   public TransDetDTO processMINReversalTransaction(TransDetDTO transDetDTO) throws DelegateException {

	   Calendar startCal = null;
	   ComponentTrackerDTO componentTrackerDTO=null;
	   String transDate=null;
		try
		{
			logger.error("Before calling processMINReversalTransaction.....TransDetDTO Details"+transDetDTO);
			/**
			 * setting the Instance Id and Instace Port numbers
			 */
			logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
			logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

			transDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
			transDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			startCal = Calendar.getInstance();
			long startTimeinMillis = startCal.getTimeInMillis();
			transDetDTO.setReqLandedTimeinPOSGW(startTimeinMillis);
            transDate=DateUtil.getTodaysDateAndTime();
            componentTrackerDTO=new ComponentTrackerDTO();
            componentTrackerDTO.setRrn(transDetDTO.getFinIstutinalReferenceNumber());
            componentTrackerDTO.setTerminalId(transDetDTO.getSalesPersonId().trim());
            componentTrackerDTO.setReplenishmentChannelId(transDetDTO.getReplenishmentChannelId()+"");
            componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_REQ);
            componentTrackerDTO.setTransDate(transDate);
            componentTrackerDTO.setPaymentAmount(transDetDTO.getPaymentAmount()+"");
            componentTrackerDTO.setDistrubutorId(transDetDTO.getDistributorId());
            componentTrackerDTO.setCustomerId(transDetDTO.getCustomerId());

			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{

			     facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
			transDetDTO = facadeBeanRemote.rechargeReversal(transDetDTO);
			      transDate=DateUtil.getTodaysDateAndTime();
				 componentTrackerDTO=new ComponentTrackerDTO();
				componentTrackerDTO.setTransId(transDetDTO.getTransId());
				componentTrackerDTO.setRrn(transDetDTO.getFinIstutinalReferenceNumber());
				componentTrackerDTO.setTerminalId(transDetDTO.getSalesPersonId().trim());
				componentTrackerDTO.setService(ServerConfigConstant.POSGWAY_RES);
				componentTrackerDTO.setReplenishmentChannelId(transDetDTO.getReplenishmentChannelId()+"");
				transDate=DateUtil.getTodaysDateAndTime();
				componentTrackerDTO.setTransDate(transDate);
				 componentTrackerDTO.setTransId(transDetDTO.getTransId());
				facadeBeanRemote.sendTransactionTimetoQueue(componentTrackerDTO);
			}

			logger.error("After calling processMINReversalTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(UnmarshalException ue)
		{
			logger.info("Occured Unmarshal exceptions"+ue);

			PM_DMPOSGW_MapConstant.getPOSGWResponse("0");


			if(ue.toString().indexOf("Read timed out")!=-1)
			{
				logger.info("ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE=="+ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE);

				if("Y".equals(ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE))
				{
					try
					{
						logger.info("Transaction got timedout, getting authorization id from PM");
						transDetDTO = facadeBeanRemote.getAuthorizationDetails(transDetDTO);
						transDetDTO.setErrorId("51");// setting success response,, check the value 51 in properties file
						logger.info("transDetDTO.getAutorizationId()===="+transDetDTO.getAutorizationId());
					}
					catch(Exception e)
					{
						logger.info("Exception in method facadeBeanRemote.getAuthorizationDetails...");
					}
				}
			}

			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;

			if("N".equals(ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE))
				throw new DelegateException(ue.getMessage());

		}
		catch(Exception ex)
		{
			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}
		finally {
		if(transDetDTO != null) {
//			Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(transDetDTO.getTransId(),transDetDTO.getAutorizationId(),transDetDTO.getFinIstutinalReferenceNumber(), startCal, endCal,"Domestic REVERSAL Transaction");
				if( "0".equals(transDetDTO.getErrorId()) )
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//			 End of AppMoniter Logging
			}
		}

		return transDetDTO;
	}



		public TransDetDTO isConnectionAlive(TransDetDTO transDetDTO) throws DelegateException {
			   Calendar startCal = null;
		try
		{
			logger.error("Before calling isConnectionAlive.....TransDetDTO Details"+transDetDTO);

			transDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
			transDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

			startCal = Calendar.getInstance();
			long startTimeinMillis = startCal.getTimeInMillis();
			transDetDTO.setReqLandedTimeinPOSGW(startTimeinMillis);

			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
			transDetDTO = facadeBeanRemote.isConnectionAlive(transDetDTO);
			}

			logger.error("After calling processPinReversalTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(Exception ex)
		{
			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}
		finally {
			if(transDetDTO != null) {
//				Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage("","",transDetDTO.getDistributorId(), startCal, endCal,"Domestic ECHO Transaction");
					if( "0".equals(transDetDTO.getErrorId()) )
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.ECHO_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.ECHO_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//				 End of AppMoniter Logging
				}
			}

		return transDetDTO;
	}
		 private void getStickyConnection() {
				try {
					logger.info(" **************IN POS inside sticky connection method*******");

					Properties p = new Properties();

					p.put(Context.INITIAL_CONTEXT_FACTORY, PropertiesReader.getValue("INITIAL_CONTEXT_FACTORY_STICKY"));
					p.put(Context.PROVIDER_URL, PropertiesReader.getValue("PROVIDER_URL_STICKY"));
					p.put(Context.URL_PKG_PREFIXES, PropertiesReader.getValue("URL_PKG_PREFIXES_STICKY"));
					InitialContext ctx = new InitialContext(p);
					facadeBeanRemote = (DomesticColumbiaPOSTransFacade)ctx.lookup(PropertiesReader.getValue("DMC_TRANS_FACADE_EJB_NAME"));
				} catch (Exception ex) {
					logger.error("LRPTransactionDelegate EXCEPTION IN CATCH OF STICKY CONN :22:-->>" + ex.getMessage());
				}
			}
}
