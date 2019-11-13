package com.bcgi.paymgr.posserver.delegate;


import com.airejb.statusinquiry.StatusInquiry;

import com.aircls.common.pos.dto.POSAccountStatusDTO;
import com.aircls.common.pos.dto.POSAccountRechargeDTO;
import com.bcgi.paymgr.posserver.common.ServiceLocatorException;
import com.bcgi.paymgr.posserver.common.RRServiceLocator;
import com.bcgi.paymgr.posserver.common.Services;

public class StatusInquiryDelegate {

	StatusInquiry facadeBeanRemote = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(StatusInquiryDelegate.class);

	public StatusInquiryDelegate() throws DelegateException  {

		try
		{
			RRServiceLocator serviceLocator = RRServiceLocator.getInstance();
			facadeBeanRemote = (StatusInquiry) serviceLocator.getRemoteUsingServerList(Services.STATUS_ENQUIRY_EJB_ID);
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
	public POSAccountStatusDTO getSubscriberAccountStatus(POSAccountStatusDTO accountDet) throws DelegateException
	{
	    POSAccountStatusDTO responseAccountStatusDTO = null;
		try
		{
			logger.info("Before Procesing in CM getSubscriberAccountStatus: "+accountDet.toString());
			responseAccountStatusDTO = facadeBeanRemote.getSubscriberAccountStatus(accountDet);
			logger.info("After Procesing in CM getSubscriberAccountStatus: "+responseAccountStatusDTO.toString());
		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
		}

		return responseAccountStatusDTO;
	}

	public POSAccountRechargeDTO getRechargeTransactionStatus(POSAccountRechargeDTO accountDet) throws DelegateException
	{
		POSAccountRechargeDTO responseAccountRechargeDTO = null;
		try
		{
			logger.info("Before Procesing in App Server getTransactionStatus: "+accountDet.toString());
			responseAccountRechargeDTO = facadeBeanRemote.getRechargeTransactionStatus(accountDet);
			logger.info("After Procesing in App Server getTransactionStatus: "+responseAccountRechargeDTO.toString());
		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
		}

		return responseAccountRechargeDTO;
	}

}
