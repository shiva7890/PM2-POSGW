package com.bcgi.paymgr.posserver.delegate;


import com.aircls.common.pos.dto.POSAccountRechargeDTO;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.airejb.rechargefacade.RechargeFacade;
import com.bcgi.async.ejb.AsyncRechargeFacade;
import com.bcgi.paymgr.posserver.common.RRServiceLocator;
import com.bcgi.paymgr.posserver.common.ServiceLocatorException;
import com.bcgi.paymgr.posserver.common.Services;
 
public class RechargeDelegate {

	RechargeFacade facadeBeanRemote = null;
	AsyncRechargeFacade asyncFacadeBeanRemote = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(RechargeDelegate.class);

	public RechargeDelegate() throws DelegateException  {

		try
		{
			RRServiceLocator serviceLocator = RRServiceLocator.getInstance();
			facadeBeanRemote = (RechargeFacade) serviceLocator.getRemoteUsingServerList(Services.RECHARGE_FACADE_EJB_ID);
			asyncFacadeBeanRemote = (AsyncRechargeFacade) serviceLocator.getRemoteUsingServerList(Services.ASYNC_RECHARGE_FACADE_EJB_ID);			
		}
		catch (ServiceLocatorException ex)
		{
			throw new DelegateException("ServiceLocatorException",ex);
		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
	    }


	}

	 public void updateSendMsgStatus(POSMessageDTO posMessageDTO)
	 {

			try
			{
				POSAccountRechargeDTO posAccountRechargeDTO = (POSAccountRechargeDTO)posMessageDTO;
				logger.info("updateSendMsgStatus "+posAccountRechargeDTO.toString());
				facadeBeanRemote.resetTransactionStatus(posAccountRechargeDTO);

			}
			catch(Exception ex)
			{
				logger.error("Exception at Recharge Delegate - updateSendMsgStatus():"+ex.getMessage());
			}


	 }


	/**
	 * The following are the business methods proxied to the
	 * EJB Session Bean. If any  exception is encountered,
	 * these methods convert them into application exceptions
	 */
	public POSAccountRechargeDTO processMINRechargeTransaction(POSAccountRechargeDTO rechargeDTO) throws DelegateException {

		POSAccountRechargeDTO responseAccountRechargeDTO = null;
		try
		{
			logger.info("Before Procesing in CM: "+rechargeDTO.toString());
			responseAccountRechargeDTO = facadeBeanRemote.processMINRechargeTransaction(rechargeDTO);
			logger.info("After Procesing in CM: "+responseAccountRechargeDTO.toString());
		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
		}

		return responseAccountRechargeDTO;
	}

	public POSMessageDTO processIRMINRechargeTransaction(POSMessageDTO reqPOSMessageDTO) throws DelegateException {

		POSAccountRechargeDTO responseAccountRechargeDTO = null;
		try
		{
			System.out.println("IN Recharge delegate == processIRMINRechargeTransaction");
			POSAccountRechargeDTO requestAccountRechargeDTO = (POSAccountRechargeDTO)reqPOSMessageDTO;
			responseAccountRechargeDTO = facadeBeanRemote.processIRMINRechargeTransaction(requestAccountRechargeDTO);
			System.out.println("After Procesing in CM: IR Transaction"+responseAccountRechargeDTO.toString());
			logger.info("AFTER Recharge delegate == processIRMINRechargeTransaction: IR Transaction"+responseAccountRechargeDTO.toString());


		}
		catch(Exception ex)
		{
			System.out.println("IN Recharge delegate exception =="+ex);
			ex.printStackTrace();
			throw new DelegateException(ex.getMessage());
		}

		return responseAccountRechargeDTO;
	}
	
	public POSMessageDTO processAsyncUnifiedRecharge(POSMessageDTO reqPOSMessageDTO) throws DelegateException {
		POSAccountRechargeDTO responseAccountRechargeDTO = null;
		try {
			System.out.println("IN ASYNC Recharge delegate == processIRMINRechargeTransaction");
			POSAccountRechargeDTO requestAccountRechargeDTO = (POSAccountRechargeDTO)reqPOSMessageDTO;
			responseAccountRechargeDTO = asyncFacadeBeanRemote.processAsyncUnifiedRecharge(requestAccountRechargeDTO);
			System.out.println("AFTER ASYNC Recharge delegate == processIRMINRechargeTransaction: IR Transaction"+responseAccountRechargeDTO.toString());
		} catch(Exception ex) {
			System.out.println("IN Recharge delegate exception =="+ex);
			ex.printStackTrace();
			throw new DelegateException(ex.getMessage());
		}
		return responseAccountRechargeDTO;
	}	
	
	public POSMessageDTO processSyncSubscriberValidation(POSMessageDTO reqPOSMessageDTO) throws DelegateException {
		POSAccountRechargeDTO responseAccountRechargeDTO = null;
		try {
			System.out.println("IN SYNC Recharge delegate = = processSyncSubscriberValidation");
			POSAccountRechargeDTO requestAccountRechargeDTO = (POSAccountRechargeDTO)reqPOSMessageDTO;
			responseAccountRechargeDTO = asyncFacadeBeanRemote.processSyncSubscriberValidation(requestAccountRechargeDTO);
			System.out.println("AFTER SYNC Recharge delegate == processSyncSubscriberValidation: IR Transaction"+responseAccountRechargeDTO.toString());
		} catch(Exception ex) {
			System.out.println("IN Recharge delegate exception =="+ex);
			ex.printStackTrace();
			throw new DelegateException(ex.getMessage());
		}
		return responseAccountRechargeDTO;
	}	
		
	public POSMessageDTO processAsyncSubscriberRecharge(POSMessageDTO reqPOSMessageDTO) throws DelegateException {
		POSAccountRechargeDTO responseAccountRechargeDTO = null;
		try {
			System.out.println("IN SYNC Recharge delegate = = processSyncSubscriberValidation");
			POSAccountRechargeDTO requestAccountRechargeDTO = (POSAccountRechargeDTO)reqPOSMessageDTO;
			System.out.println("BEFORE LOOKUP");
			responseAccountRechargeDTO = asyncFacadeBeanRemote.processAsyncSubscriberRecharge(requestAccountRechargeDTO);
			System.out.println("AFTER SYNC Recharge delegate == processSyncSubscriberValidation: IR Transaction"+responseAccountRechargeDTO.toString());
		} catch(Exception ex) {
			System.out.println("IN Recharge delegate exception =="+ex);
			ex.printStackTrace();
			throw new DelegateException(ex.getMessage());
		}
		return responseAccountRechargeDTO;
	}
	
	public POSMessageDTO orderStatusInquriy(POSMessageDTO reqPOSMessageDTO) throws DelegateException {
		POSAccountRechargeDTO responseAccountRechargeDTO = null;
		try {
			System.out.println("IN ORDERSTATUSENQUIRY");
			POSAccountRechargeDTO orderDTO = (POSAccountRechargeDTO)reqPOSMessageDTO;
			responseAccountRechargeDTO = asyncFacadeBeanRemote.orderStatusInquriy(orderDTO);
			System.out.println("AFTER IN ORDERSTATUSENQUIRY"+responseAccountRechargeDTO.toString());
		} catch(Exception ex) {
			System.out.println("IN ORDERSTATUSENQUIRY =="+ex);
			ex.printStackTrace();
			throw new DelegateException(ex.getMessage());
		}
		return responseAccountRechargeDTO;
	}	
	
	public POSAccountRechargeDTO processPinPurchaseTransaction(POSAccountRechargeDTO pinRechargeDTO) throws DelegateException {

		POSAccountRechargeDTO responsePinRechargeDTO = null;
		try
		{
			logger.info("Before Procesing in CM for PIN Transaction: "+pinRechargeDTO.toString());
			responsePinRechargeDTO = facadeBeanRemote.processPinPurchaseTransaction(pinRechargeDTO);
			logger.info("After Procesing in CM for PIN Transaction:"+responsePinRechargeDTO.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new DelegateException(ex.getMessage());
		}

		return responsePinRechargeDTO;
	}

}
