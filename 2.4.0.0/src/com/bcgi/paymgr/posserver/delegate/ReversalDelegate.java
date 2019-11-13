package com.bcgi.paymgr.posserver.delegate;

import com.aircls.common.pos.dto.POSAccountReversalDTO;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.airejb.reversalfacade.ReversalFacade;
import com.bcgi.async.ejb.AsyncReversalFacade;
import com.bcgi.paymgr.posserver.common.RRServiceLocator;
import com.bcgi.paymgr.posserver.common.ServiceLocatorException;
import com.bcgi.paymgr.posserver.common.Services;

public class ReversalDelegate {

	ReversalFacade facadeBeanRemote = null;
	AsyncReversalFacade asyncReversalFacadeRemote = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(ReversalDelegate.class);

	public ReversalDelegate() throws DelegateException  {

		try
		{
			RRServiceLocator serviceLocator = RRServiceLocator.getInstance();
			facadeBeanRemote = (ReversalFacade) serviceLocator.getRemoteUsingServerList(Services.REVERSAL_FACADE_EJB_ID);
			asyncReversalFacadeRemote = (AsyncReversalFacade) serviceLocator.getRemoteUsingServerList(Services.ASYNC_REVERSAL_FACADE_EJB_ID);
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


	/**
	 * The following are the business methods proxied to the
	 * EJB Session Bean. If any  exception is encountered,
	 * these methods convert them into application exceptions
	 */
	public POSAccountReversalDTO revertMINRechargeTransaction(POSAccountReversalDTO accountReversalDTO)throws DelegateException {

		POSAccountReversalDTO responseAccountReversalDTO = null;
		try
		{
			logger.info("Before Procesing in CM: "+accountReversalDTO.toString());
			responseAccountReversalDTO = facadeBeanRemote.revertMINRechargeTransaction(accountReversalDTO);
			logger.info("After Procesing in CM: "+responseAccountReversalDTO.toString());
		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
		}

		return responseAccountReversalDTO;
	}

	public POSAccountReversalDTO revertIRMINRechargeTransaction(POSAccountReversalDTO accountReversalDTO)throws DelegateException {

		POSAccountReversalDTO responseAccountReversalDTO = null;
		try
		{
			logger.info("Before Procesing in CM: "+accountReversalDTO.toString());
			responseAccountReversalDTO = facadeBeanRemote.revertIRMINRechargeTransaction(accountReversalDTO);
			logger.info("After Procesing in CM: "+responseAccountReversalDTO.toString());
		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
		}

		return responseAccountReversalDTO;
	}
	
	public POSMessageDTO revertRechargeProcess(POSMessageDTO reqPOSMessageDTO) throws DelegateException {
		POSAccountReversalDTO responseAccountReversalDTO = null;
		try {
			System.out.println("RAM IN SYNC Reversal delegate = = revertRechargeProcess");
			POSAccountReversalDTO requestAccountReversalDTO = (POSAccountReversalDTO)reqPOSMessageDTO;
			System.out.println("BEFORE LOOKUP"); 
			responseAccountReversalDTO = asyncReversalFacadeRemote.revertRechargeProcess(requestAccountReversalDTO);
			System.out.println("RAM AFTER SYNC Reversal delegate == processSyncSubscriberValidation: IR Transaction"+responseAccountReversalDTO.toString());
		} catch(Exception ex) {
			System.out.println("RAM IN Reversal delegate exception =="+ex);
			ex.printStackTrace();
			throw new DelegateException(ex.getMessage());
		}
		return responseAccountReversalDTO;
	}	
	
	public POSAccountReversalDTO revertPinTransaction(POSAccountReversalDTO accountReversalDTO) throws DelegateException
	{
		POSAccountReversalDTO responseAccountReversalDTO = null;
		try
		{
			logger.info("Before Procesing in CM: "+accountReversalDTO.toString());
			responseAccountReversalDTO = facadeBeanRemote.revertPinTransaction(accountReversalDTO);
			logger.info("After Procesing in CM: "+responseAccountReversalDTO.toString());
		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
		}

		return responseAccountReversalDTO;
	}

	 public void updateSendMsgStatus(POSMessageDTO posMessageDTO)
	 {

			try
			{
				POSAccountReversalDTO posAccountReversalDTO = (POSAccountReversalDTO)posMessageDTO;
				logger.info("updateSendMsgStatus "+posAccountReversalDTO.toString());
				facadeBeanRemote.resetTransactionStatus(posAccountReversalDTO);

			}
			catch(Exception ex)
			{
				logger.error("Exception at Reversal Delegate - updateSendMsgStatus():"+ex.getMessage());
			}


	 }


}
