package com.bcgi.paymgr.posserver.domestic.ecuador.delegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.bcgi.paymgr.posserver.common.Services;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.common.util.PropertiesReader;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.rr.DomesticAsyncPolicyObject;
import com.bcgi.paymgr.posserver.domestic.common.rr.DomesticSyncPolicyObject;
import com.bcgi.paymgr.posserver.domestic.common.rr.RRDomesticService;
import com.bcgi.paymgr.posserver.domestic.common.util.Utility;
import com.bcgi.pmcore.dto.OrderRequestDTO;
import com.bcgi.pmcore.dto.OrderResponseDTO;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;
import com.bcgi.pmcore.ejb.domestictransaction.DomesticPOSTransactionFacade;
import com.bcgi.pmcore.ejb.domestictransaction.asynchronous.AsynchronousDomesticPOSFacade;
import com.bcgi.rrobin.exception.MandatoryParameterException;
import com.bcgi.rrobin.policy.LoadBalancePolicy;
import com.bcgi.rrobin.proxy.EJBProxy;

public class DMTransactionDelegate {

	DomesticPOSTransactionFacade facadeBeanRemote = null;
	static LoadBalancePolicy syncLoadBalancePolicy = null;
	static LoadBalancePolicy asyncLoadBalancePolicy = null;
	RRDomesticService rrDomesticService = null;
	ArrayList resourceLst = null;
	EJBProxy ejbProxy = null;
	int syncOrAsyncType = 0;
	AsynchronousDomesticPOSFacade asynchronousDomesticPOSFacade = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(DMTransactionDelegate.class);

	
	
	
	

	public DMTransactionDelegate(int callType, String financialRefNum) throws DelegateException  {
		logger.info(" DMTransactionDelegate Constructor Code Execution Starting ");
		try
		{
			logger.info(" DMTransactionDelegate Constructor Calling - Financial Reference Number ====> "+financialRefNum);
			syncOrAsyncType = callType;
			rrDomesticService = new RRDomesticService();
			ejbProxy = EJBProxy.getInstance();
			if(callType == ServerConfigConstant.RR_SYNCHRONOUS_CALL) {
				if(syncLoadBalancePolicy == null) {

					syncLoadBalancePolicy = DomesticSyncPolicyObject.getInstance();
					try {
						ejbProxy.register(ServerConfigConstant.RR_SYNC_REQUEST_TYPE,syncLoadBalancePolicy);
						ejbProxy.callEJBProxyTimer(rrDomesticService.getPropertyValueToConnectPM("wakeup"));
					} catch (MandatoryParameterException e1) {
						throw new DelegateException(e1.getMessage());
					}
				}
				facadeBeanRemote = (DomesticPOSTransactionFacade)rrDomesticService.getRemoteObject(ServerConfigConstant.RR_SYNC_REQUEST_TYPE, financialRefNum);
				logger.info("facadeBeanRemote==> "+facadeBeanRemote);
			}
			else if(callType == ServerConfigConstant.RR_ASYNCHRONOUS_CALL) {
				if(asyncLoadBalancePolicy == null) {

					asyncLoadBalancePolicy=DomesticAsyncPolicyObject.getInstance();

					try {
						ejbProxy.register(ServerConfigConstant.RR_ASYNC_REQUEST_TYPE,asyncLoadBalancePolicy);
						ejbProxy.callEJBProxyTimer(rrDomesticService.getPropertyValueToConnectPM("wakeup"));
					} catch (MandatoryParameterException e1) {
						throw new DelegateException(e1.getMessage());
					}
				}
				asynchronousDomesticPOSFacade = (AsynchronousDomesticPOSFacade)rrDomesticService.getRemoteObject(ServerConfigConstant.RR_ASYNC_REQUEST_TYPE, financialRefNum);
				logger.info("asynchronousDomesticPOSFacade==> "+asynchronousDomesticPOSFacade);
		}
		}
		catch(Exception ex)
		{
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
			logger.error(" DMTransactionDelegate Constructor Exception "+ex.getMessage());
			logger.error(financialRefNum + "==DMTransactionDelegate EXCEPTION IN CATCH OF STICKY IN EXCEPTION BLOCK:-->>" + ex.getMessage());
			String strExceptionMsg = PropertiesReader.getValue("STICKY_CONNECTION_EXCEPTION_MESSAGE");
			if (strExceptionMsg.contains(ex.getMessage())) {
				if ("Y".equalsIgnoreCase(PropertiesReader.getValue("STICKY_CONNECTION_ENABLE"))) {
					try {
						getStickyConnection(callType);
					} catch (Exception e) {
						logger.error("DMTransactionDelegate EXCEPTION IN CATCH OF STICKY CONN :11:-->>" + e.getMessage());
					}
				}
			} else {
				logger.error("IN DMTransactionDelegate exception while getting sticky connection" + ex.getMessage());
				throw new DelegateException(ex.getMessage());
			}			
			throw new DelegateException(ex.getMessage());
	    }
		logger.info(" DMTransactionDelegate Constructor Code Execution Ending ");
	}

	 public String insertRejectedRechargeTransaction(RejectTransactionDTO rejectTransactionDTO) throws DelegateException
	 {
            String transactionId = "";
            RejectTransactionDTO rejectRespTransactionDTO=null;
			try
			{
				logger.error("Before calling insertRejectedRechargeTransaction.....RejectTransactionDTO Details"+rejectTransactionDTO);
				//Commented and changed by sridhar.v on 19-Dec-2011 due to change in return type of insertRejectReason method
				//transactionId = facadeBeanRemote.insertRejectReason(rejectTransactionDTO);
				
				if(facadeBeanRemote == null)
				{
					Utility.setErrorCode(rejectTransactionDTO);
				} else{
				rejectRespTransactionDTO = facadeBeanRemote.insertRejectReason(rejectTransactionDTO);
				}
				
				if(rejectRespTransactionDTO!=null && rejectRespTransactionDTO.getAutorizationId()!=null){
					transactionId = rejectRespTransactionDTO.getAutorizationId();
				}				
				
				logger.error("After calling insertRejectedRechargeTransaction.....RejectTransactionDTO Details"+rejectTransactionDTO);

			}
			catch(Exception ex)
			{
			ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}

           return transactionId;
	 }


	 public OrderResponseDTO processSubscriberAccntValidationTransaction(OrderRequestDTO orderRequestDTO) throws DelegateException {

		 OrderResponseDTO orderResponseDTO = null;
		 Calendar startCal = null;

			try
			{
				logger.error("Before calling performSubscriberValidation.....OrderRequestDTO Details"+orderRequestDTO);

				logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
				logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				orderRequestDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
				orderRequestDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

				startCal = Calendar.getInstance();
				if(asynchronousDomesticPOSFacade == null)
				{
					orderResponseDTO = new OrderResponseDTO();
					Utility.setErrorCode(orderResponseDTO);
				} else{
				orderResponseDTO = asynchronousDomesticPOSFacade.performSubscriberValidation(orderRequestDTO);
				}
				 
				logger.error("After calling performSubscriberValidation.....OrderRequestDTO Details"+orderRequestDTO);

			}
			catch(Exception ex)
			{
			        ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}
			finally
			{
				
				if(orderResponseDTO != null) {
//				Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage("","",orderRequestDTO.getRrn(), startCal, endCal,"Domestic Subscriber Validation Transaction");
					if( "0".equals(orderResponseDTO.getErrorId()) )
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.SUB_VALIDATION_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.SUB_VALIDATION_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//				 End of AppMoniter Logging
				}

			}


			return orderResponseDTO;
		}
	 
	 public OrderResponseDTO processASyncMINRechargeTransaction(OrderRequestDTO orderRequestDTO) throws DelegateException {

		 OrderResponseDTO orderResponseDTO = null;
		 Calendar startCal = null;

			try
			{
				logger.error("Before calling performAsynchRecharge.....OrderRequestDTO Details"+orderRequestDTO);
				/**
				 * setting the Instance Id and Instace Port numbers
				 */
				logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
				logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				orderRequestDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
				orderRequestDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

				startCal = Calendar.getInstance();

				if(asynchronousDomesticPOSFacade == null)
				{
					orderResponseDTO = new OrderResponseDTO();
					Utility.setErrorCode(orderResponseDTO);
				} else{
				orderResponseDTO = asynchronousDomesticPOSFacade.performAsynchRecharge(orderRequestDTO);
				}
				 
				logger.error("After calling performAsynchRecharge.....OrderRequestDTO Details"+orderRequestDTO);

			}
			catch(Exception ex)
			{
			        ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}
			finally
			{
				
				if(orderResponseDTO != null) {
//				Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage("","",orderRequestDTO.getRrn(), startCal, endCal,"Domestic Async Recharge Transaction");
					if( "0".equals(orderResponseDTO.getErrorId()) )
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.ASYNC_RECHARGE_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.ASYNC_RECHARGE_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//				 End of AppMoniter Logging
				}
			}
		 			

			return orderResponseDTO;
		}
	 
	 
	 public OrderResponseDTO processUnifiedASyncMINRechargeTransaction(OrderRequestDTO orderRequestDTO) throws DelegateException {

		 OrderResponseDTO orderResponseDTO = null;
		 Calendar startCal = null;

			try
			{
				logger.error("Before calling performAsynchUnifiedRecharge.....OrderRequestDTO Details"+orderRequestDTO);

				/**
				 * setting the Instance Id and Instace Port numbers
				 */
				logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
				logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				orderRequestDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
				orderRequestDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				startCal = Calendar.getInstance();
				logger.info("DMTransactionDelegate :: processUnifiedASyncMINRechargeTransaction  :: Request Initiating Time in POSGW to PM  :: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));			
				if(asynchronousDomesticPOSFacade==null)
				{
					orderResponseDTO = new OrderResponseDTO();
					Utility.setErrorCode(orderResponseDTO);
				} else{
				orderResponseDTO = asynchronousDomesticPOSFacade.performAsynchUnifiedRecharge(orderRequestDTO);
				}
				 
				logger.error("After calling performAsynchUnifiedRecharge.....OrderRequestDTO Details"+orderRequestDTO);

			}
			catch(Exception ex)
			{
			        ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}
			finally
			{
				
				if(orderResponseDTO != null) {
//				Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage("","",orderRequestDTO.getRrn(), startCal, endCal,"Domestic Unified Recharge Transaction");
					if( "0".equals(orderResponseDTO.getErrorId()) )
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.UNIFIED_RECHARGE_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.UNIFIED_RECHARGE_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//				 End of AppMoniter Logging
				}
			}
		 			

			return orderResponseDTO;
		}
	 
	 public OrderResponseDTO processASyncReversalTransaction(OrderRequestDTO orderRequestDTO) throws DelegateException {

		 OrderResponseDTO orderResponseDTO = null;
		 Calendar startCal = null;

			try
			{
				logger.error("Before calling performAsynchReversal.....OrderRequestDTO Details"+orderRequestDTO);

				/**
				 * setting the Instance Id and Instace Port numbers
				 */
				logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
				logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				orderRequestDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
				orderRequestDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				 startCal = Calendar.getInstance();
				
			 	if(asynchronousDomesticPOSFacade==null)
				{
			 		orderResponseDTO = new OrderResponseDTO();
					Utility.setErrorCode(orderResponseDTO);
				} else{
				orderResponseDTO = asynchronousDomesticPOSFacade.performAsynchReversal(orderRequestDTO);
				}
				 
				logger.error("After calling performAsynchReversal.....OrderRequestDTO Details"+orderRequestDTO);

			}
			catch(Exception ex)
			{
			        ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}
			finally
			{
				
				if(orderResponseDTO != null) {
//				Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage("","",orderRequestDTO.getRrn(), startCal, endCal,"Domestic Async Reversal Transaction");
					if( "0".equals(orderResponseDTO.getErrorId()) )
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.ASYNC_REVERSAL_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.ASYNC_REVERSAL_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//				 End of AppMoniter Logging
				}
			}

			return orderResponseDTO;
		}

	 
	 
	 public OrderResponseDTO processRechargeInquiryTransaction(OrderRequestDTO orderRequestDTO) throws DelegateException {

		 OrderResponseDTO orderResponseDTO = null;

			try
			{
				logger.error("Before calling performSynchRechargeInquriy.....OrderRequestDTO Details"+orderRequestDTO);

				/**
				 * setting the Instance Id and Instace Port numbers
				 */
				logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
				logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				orderRequestDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
				orderRequestDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				if(asynchronousDomesticPOSFacade==null)
				{
					orderResponseDTO = new OrderResponseDTO();
					Utility.setErrorCode(orderResponseDTO);
				} else{
				orderResponseDTO = asynchronousDomesticPOSFacade.performSynchRechargeInquriy(orderRequestDTO);
				}
				 
				logger.error("After calling performSynchRechargeInquriy.....OrderRequestDTO Details"+orderRequestDTO);

			}
			catch(Exception ex)
			{
			        ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}

			return orderResponseDTO;
		}
	 
	 public OrderResponseDTO processReversalInquiryTransaction(OrderRequestDTO orderRequestDTO) throws DelegateException {

		 OrderResponseDTO orderResponseDTO = null;

			try
			{
				logger.error("Before calling performSynchReversalInquriy.....OrderRequestDTO Details"+orderRequestDTO);

				/**
				 * setting the Instance Id and Instace Port numbers
				 */
				logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
				logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				orderRequestDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
				orderRequestDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
				
				if(asynchronousDomesticPOSFacade==null)
				{
					orderResponseDTO = new OrderResponseDTO();
					Utility.setErrorCode(orderResponseDTO);
				} else{
				orderResponseDTO = asynchronousDomesticPOSFacade.performSynchReversalInquriy(orderRequestDTO);
				}
				 
				logger.error("After calling performSynchReversalInquriy.....OrderRequestDTO Details"+orderRequestDTO);

			}
			catch(Exception ex)
			{
			        ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
				throw new DelegateException(ex.getMessage());
			}

			return orderResponseDTO;
		}
// GTQ method 
	public TransDetDTO processMINRechargeTransaction(TransDetDTO transDetDTO) throws DelegateException {

		 Calendar startCal = null;
		 

		try
		{
			logger.error("Before calling processMINPKTRechargeTransaction.....TransDetDTO Details(GTQ)"+transDetDTO);
			/**
			 * setting the Instance Id and Instace Port numbers
			 */
			logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
			logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			
			transDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
			transDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			 startCal = Calendar.getInstance();
			long startTimeinMillis = System.currentTimeMillis();
			transDetDTO.setReqLandedTimeinPOSGW(startTimeinMillis);

			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
			transDetDTO = facadeBeanRemote.rechargeSubscriberThroughCash(transDetDTO);
			}

			logger.error("After calling processMINPKTRechargeTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(Exception ex)
		{
			ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
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

	public TransDetDTO processPinPurchaseTransaction(TransDetDTO transDetDTO) throws DelegateException {

		 Calendar startCal = null;
		 
		 

		try
		{
			logger.error("Before calling processPinPurchaseTransaction.....TransDetDTO Details"+transDetDTO);
			/**
			 * setting the Instance Id and Instace Port numbers
			 */
			logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
			logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);

			transDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
			transDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			
			
			startCal = Calendar.getInstance();	
			
			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
			transDetDTO = facadeBeanRemote.pinPurchaseThroughCash(transDetDTO);
			}
			
			logger.error("After calling processPinPurchaseTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(Exception ex)
		{
			ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}
		finally
		{
			
			if(transDetDTO != null) {
//			Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(transDetDTO.getTransId(),transDetDTO.getAutorizationId(),transDetDTO.getFinIstutinalReferenceNumber(), startCal, endCal,"Domestic Pin purchase Transaction");
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

			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
			transDetDTO = facadeBeanRemote.rechargeReversal(transDetDTO);
			}
			
			logger.error("After calling processMINReversalTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(Exception ex)
		{
			ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}
		finally {
			if(transDetDTO != null) {
//				Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(transDetDTO.getTransId(),transDetDTO.getAutorizationId(),transDetDTO.getFinIstutinalReferenceNumber(), startCal, endCal,"Domestic REVERSAL Transaction");
					if( "0".equals(transDetDTO.getErrorId()) )
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//				 End of AppMoniter Logging
				}
			}


		return transDetDTO;
	}

	public TransDetDTO processPinReversalTransaction(TransDetDTO transDetDTO) throws DelegateException {
		 Calendar startCal = null;
		 
		 

		try
		{
			logger.error("Before calling processPinReversalTransaction.....TransDetDTO Details"+transDetDTO);
			/**
			 * setting the Instance Id and Instace Port numbers
			 */
			logger.info("POSGW InstanceIdvalue before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_ID);
			logger.info("POSGW InstancePortNumber before sent to PM==>"+ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			
			transDetDTO.setGatewayInstanceId(ServerConfigConstant.GW_INSTANCE_ID);
			transDetDTO.setGatewayInstancePortNumber(ServerConfigConstant.GW_INSTANCE_PORT_NUMBER);
			startCal = Calendar.getInstance();
			
			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
			transDetDTO = facadeBeanRemote.pinPurchaseReversal(transDetDTO);
			}
			
			logger.error("After calling processPinReversalTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(Exception ex)
		{
			ejbProxy.clearCache();
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}
		finally {
			if(transDetDTO != null) {
//				Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(transDetDTO.getTransId(),transDetDTO.getAutorizationId(),transDetDTO.getFinIstutinalReferenceNumber(), startCal, endCal,"Domestic  PIN REVERSAL Transaction");
					if( "0".equals(transDetDTO.getErrorId()) )
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_SEG_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_SEG_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
//				 End of AppMoniter Logging
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
			syncLoadBalancePolicy = null;
			asyncLoadBalancePolicy = null;
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
		
		
		 public OrderResponseDTO processASyncCouponRedemption(OrderRequestDTO orderRequestDTO) throws DelegateException {

			 OrderResponseDTO orderResponseDTO = null;

				try
				{
					logger.error("Before calling performAsynchUnifiedRecharge.....OrderRequestDTO Details"+orderRequestDTO);

					orderResponseDTO = asynchronousDomesticPOSFacade.performAsynchUnifiedCouponRecharge(orderRequestDTO);
					 
					logger.error("After calling performAsynchUnifiedRecharge.....OrderRequestDTO Details"+orderRequestDTO);

				}
				catch(Exception ex)
				{
				        ejbProxy.clearCache();
				syncLoadBalancePolicy = null;
				asyncLoadBalancePolicy = null;
					throw new DelegateException(ex.getMessage());
				}

				return orderResponseDTO;
			}
		 
		 
		//newly added  for couponing 2 -step
		 public OrderResponseDTO performRechargeCpnAndSubscriberValidation(OrderRequestDTO orderRequestDTO) throws DelegateException {

			 OrderResponseDTO orderResponseDTO = null;

				try
				{
					logger.error(" Before performRechargeCpnAndSubscriberValidation.....OrderRequestDTO Details"+orderRequestDTO);

				orderResponseDTO = asynchronousDomesticPOSFacade.performRechargeCpnAndSubscriberValidation(orderRequestDTO);
					 
					logger.error("After calling performRechargeCpnAndSubscriberValidation.....OrderRequestDTO Details"+orderRequestDTO);

				}
				catch(Exception ex)
				{
				        ejbProxy.clearCache();
				syncLoadBalancePolicy = null;
				asyncLoadBalancePolicy = null;
					throw new DelegateException(ex.getMessage());
				}

				return orderResponseDTO;
			}
		 
		 //newly added for couponing 2 -step
		 public OrderResponseDTO performAsyncRechargeCpnRedeemption(OrderRequestDTO orderRequestDTO) throws DelegateException {

			 OrderResponseDTO orderResponseDTO = null;

				try
				{
					logger.error(" before performAsyncRechargeCpnRedeemption.....OrderRequestDTO Details"+orderRequestDTO);

					orderResponseDTO = asynchronousDomesticPOSFacade.performAsyncRechargeCpnRedeemption(orderRequestDTO);
					 
					logger.error(" After performAsyncRechargeCpnRedeemption.....OrderRequestDTO Details"+orderRequestDTO);

				}
				catch(Exception ex)
				{
				        ejbProxy.clearCache();
				syncLoadBalancePolicy = null;
				asyncLoadBalancePolicy = null;
					throw new DelegateException(ex.getMessage());
				}

				return orderResponseDTO;
			}
		 
		 
//		newly added for couponing 2 -step
		 public OrderResponseDTO performBrandCpnAndSubscriberValidation(OrderRequestDTO orderRequestDTO) throws DelegateException {

			 OrderResponseDTO orderResponseDTO = null;

				try
				{
					logger.error(" Before performBrandCpnAndSubscriberValidation.....OrderRequestDTO Details"+orderRequestDTO);

				orderResponseDTO = asynchronousDomesticPOSFacade.performBrandCpnAndSubscriberValidation(orderRequestDTO);
					 
					logger.error(" After performBrandCpnAndSubscriberValidation.....OrderRequestDTO Details"+orderRequestDTO);

				}
				catch(Exception ex)
				{
				        ejbProxy.clearCache();
				syncLoadBalancePolicy = null;
				asyncLoadBalancePolicy = null;
					throw new DelegateException(ex.getMessage());
				}

				return orderResponseDTO;
			}
		 
		 
//		newly added for couponing 2 -step
		 public OrderResponseDTO performAsyncBrandCpnRedeemption(OrderRequestDTO orderRequestDTO) throws DelegateException {

			 OrderResponseDTO orderResponseDTO = null;

				try
				{
					logger.error(" Before performAsyncBrandCpnRedeemption.....OrderRequestDTO Details"+orderRequestDTO);

					orderResponseDTO = asynchronousDomesticPOSFacade.performAsyncBrandCpnRedeemption(orderRequestDTO);
					 
					logger.error(" After performAsyncBrandCpnRedeemption.....OrderRequestDTO Details"+orderRequestDTO);

				}
				catch(Exception ex)
				{
				        ejbProxy.clearCache();
				syncLoadBalancePolicy = null;
				asyncLoadBalancePolicy = null;
					throw new DelegateException(ex.getMessage());
				}

				return orderResponseDTO;
			}
		 		
		 public OrderResponseDTO processRechargeCouponInquiry(OrderRequestDTO orderRequestDTO) throws DelegateException {

			 OrderResponseDTO orderResponseDTO = null;

				try
				{
					logger.error("Before calling performSynchRechargeInquriy.....OrderRequestDTO Details"+orderRequestDTO);

					orderResponseDTO = asynchronousDomesticPOSFacade.performSynchCouponRechargeInquriy(orderRequestDTO);
					 
					logger.error("After calling performSynchRechargeInquriy.....OrderRequestDTO Details"+orderRequestDTO);

				}
				catch(Exception ex)
				{
				        ejbProxy.clearCache();
				syncLoadBalancePolicy = null;
				asyncLoadBalancePolicy = null;
					throw new DelegateException(ex.getMessage());
				}

				return orderResponseDTO;
			}
		 
		 public TransDetDTO processRechargeCouponRedemption(TransDetDTO transDetDTO) throws DelegateException {


				try
				{
					logger.error("Before calling processRechargeCouponRedemption.....TransDetDTO Details"+transDetDTO);

					transDetDTO = facadeBeanRemote.couponRechargeSubscriberThroughCash(transDetDTO);

					logger.error("After calling processRechargeCouponRedemption.....TransDetDTO Details"+transDetDTO);

				}
				catch(Exception ex)
				{
					ejbProxy.clearCache();
					syncLoadBalancePolicy = null;
					asyncLoadBalancePolicy = null;
					throw new DelegateException(ex.getMessage());
				}

				return transDetDTO;
			}
			
			public TransDetDTO processBrandCouponRedemption(TransDetDTO transDetDTO) throws DelegateException {


				try
				{
					logger.error("Before calling processBrandCouponRedemption.....TransDetDTO Details"+transDetDTO);

				transDetDTO = facadeBeanRemote.doBrandCouponRedemption(transDetDTO);

					logger.error("After calling processBrandCouponRedemption.....TransDetDTO Details"+transDetDTO);

				}
				catch(Exception ex)
				{
					ejbProxy.clearCache();
					syncLoadBalancePolicy = null;
					asyncLoadBalancePolicy = null;
					throw new DelegateException(ex.getMessage());
				}

				return transDetDTO;
			}
			private void getStickyConnection(int callType) {
				try {
					logger.info(" **************IN POS inside sticky connection method*******");
		
					Properties p = new Properties();
		
					p.put(Context.INITIAL_CONTEXT_FACTORY, PropertiesReader.getValue("INITIAL_CONTEXT_FACTORY_STICKY"));
					p.put(Context.PROVIDER_URL, PropertiesReader.getValue("PROVIDER_URL_STICKY"));
					p.put(Context.URL_PKG_PREFIXES, PropertiesReader.getValue("URL_PKG_PREFIXES_STICKY"));
					InitialContext ctx = new InitialContext(p);
		
					if (callType == ServerConfigConstant.RR_SYNCHRONOUS_CALL) {
						facadeBeanRemote = (DomesticPOSTransactionFacade) ctx.lookup(PropertiesReader.getValue("DM_TRANS_FACADE_EJB_NAME"));
					} else if (callType == ServerConfigConstant.RR_ASYNCHRONOUS_CALL) {
						asynchronousDomesticPOSFacade = (AsynchronousDomesticPOSFacade) ctx.lookup(PropertiesReader.getValue("DM_TRANS_ASYNC_FACADE_EJB_NAME"));
					}
				} catch (Exception ex) {
					logger.error("DMTransactionDelegate EXCEPTION IN CATCH OF STICKY CONN :22:-->>" + ex.getMessage());
				}
			}		
}
