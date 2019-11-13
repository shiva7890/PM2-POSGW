package com.bcgi.paymgr.posserver.domestic.panama.delegate;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.PropertiesReader;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.common.rr.RRDomesticService;
import com.bcgi.paymgr.posserver.domestic.common.util.Utility;
import com.bcgi.paymgr.posserver.domestic.panama.rr.LRPSyncPolicyObject;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;
import com.bcgi.pmcore.ejb.domestictransaction.columbia.DomesticColumbiaPOSTransFacade;
import com.bcgi.rrobin.exception.MandatoryParameterException;
import com.bcgi.rrobin.policy.LoadBalancePolicy;
import com.bcgi.rrobin.proxy.EJBProxy;

public class LRPTransactionDelegate {

	
	
	DomesticColumbiaPOSTransFacade facadeBeanRemote = null;
	static LoadBalancePolicy lrcSyncLoadBalancePolicy = null;
	RRDomesticService rrDomesticService = null;
	EJBProxy ejbProxy = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRPTransactionDelegate.class);

	public LRPTransactionDelegate(String financialRefNum) throws DelegateException  {

		try
		{
			logger.info(" LRPTransactionDelegate Constructor Calling - Financial Reference Number ====> "+financialRefNum);
			rrDomesticService = new RRDomesticService();
			ejbProxy = EJBProxy.getInstance();
				if(lrcSyncLoadBalancePolicy == null) {

					lrcSyncLoadBalancePolicy = LRPSyncPolicyObject.getInstance();
					try {
						ejbProxy.register(ServerConfigConstant.RR_LR_C_SYNC_REQUEST_TYPE,lrcSyncLoadBalancePolicy);
						ejbProxy.callEJBProxyTimer(rrDomesticService.getPropertyValueToConnectPM("wakeup"));
					} catch (MandatoryParameterException e1) {
						throw new DelegateException(e1.getMessage());
					}
				}
				facadeBeanRemote = (DomesticColumbiaPOSTransFacade)rrDomesticService.getRemoteObject(ServerConfigConstant.RR_LR_C_SYNC_REQUEST_TYPE, financialRefNum);
				logger.info("facadeBeanRemote==> "+facadeBeanRemote);
	
		}
		catch(Exception ex)
		{
			lrcSyncLoadBalancePolicy = null;
			logger.error(financialRefNum + "==LRPTransactionDelegate EXCEPTION IN CATCH OF STICKY IN EXCEPTION BLOCK:-->>" + ex.getMessage());
			String strExceptionMsg = PropertiesReader.getValue("STICKY_CONNECTION_EXCEPTION_MESSAGE");
			if (strExceptionMsg.contains(ex.getMessage())) {
				if ("Y".equalsIgnoreCase(PropertiesReader.getValue("STICKY_CONNECTION_ENABLE"))) {
					try {
						getStickyConnection();
					} catch (Exception e) {
						logger.error("LRPTransactionDelegate EXCEPTION IN CATCH OF STICKY CONN :11:-->>" + e.getMessage());
					}
				}
			} else {
				logger.error("IN DMTransactionDelegate exception while getting sticky connection" + ex.getMessage());
				throw new DelegateException(ex.getMessage());
			}
			throw new DelegateException(ex.getMessage());
	    }
	}

	 public String insertRejectedRechargeTransaction(RejectTransactionDTO rejectTransactionDTO) throws DelegateException
	 {
		 	RejectTransactionDTO rejectRespTransactionDTO=null;
            String transactionId = "";
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
				//emds
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

			return transReqDetDTO;
		}
	 
	

	public TransDetDTO processMINRechargeTransaction(TransDetDTO transDetDTO) throws DelegateException {


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

			if(facadeBeanRemote == null)
			{
				Utility.setErrorCode(transDetDTO);
			} else{
			transDetDTO = facadeBeanRemote.rechargeSubscriberThroughCash(transDetDTO);
			}

			logger.error("After calling processMINRechargeTransaction.....TransDetDTO Details"+transDetDTO);

		}
		catch(Exception ex)
		{
			ejbProxy.clearCache();
			lrcSyncLoadBalancePolicy = null;
			logger.error("After calling setError.....TransDetDTO Details"+transDetDTO);
			throw new DelegateException(ex.getMessage());
		}

		return transDetDTO;
	}

	

   public TransDetDTO processMINReversalTransaction(TransDetDTO transDetDTO) throws DelegateException {


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
			lrcSyncLoadBalancePolicy = null;
			throw new DelegateException(ex.getMessage());
		}

		return transDetDTO;
	}

	

		public TransDetDTO isConnectionAlive(TransDetDTO transDetDTO) throws DelegateException {

		try
		{
			logger.error("Before calling isConnectionAlive.....TransDetDTO Details"+transDetDTO);

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
