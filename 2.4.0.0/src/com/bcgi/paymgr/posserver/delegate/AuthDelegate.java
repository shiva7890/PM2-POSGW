package com.bcgi.paymgr.posserver.delegate;


import com.airejb.partner.Partner;

import com.aircls.common.pos.dto.IRMessageDTO;
import com.aircls.common.pos.dto.POSAuthenticationDTO;
import com.bcgi.paymgr.posserver.common.ServiceLocatorException;

import com.bcgi.paymgr.posserver.common.RRServiceLocator;
import com.bcgi.paymgr.posserver.common.Services;
import com.aircls.common.pos.dto.POSMessageDTO;

public class AuthDelegate {

	Partner partnerBeanRemote = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(AuthDelegate.class);

	public AuthDelegate() throws DelegateException  {

		try
		{
			RRServiceLocator serviceLocator = RRServiceLocator.getInstance();
			partnerBeanRemote = (Partner) serviceLocator.getRemoteUsingServerList(Services.PARTNER_EJB_ID);
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
	public POSAuthenticationDTO validateDomesticPartner(POSAuthenticationDTO authenticationDTO) throws DelegateException
	{
		POSAuthenticationDTO responseAuthenticationDTO = null;
		try
		{
			logger.info("Before Procesing in CM: "+authenticationDTO.toString());

			logger.info("authenticationDTO.getSubAgentId(): "+authenticationDTO.getSubAgentId());
			logger.info("authenticationDTO.getStoreId(): "+authenticationDTO.getStoreId());
			logger.info("authenticationDTO.getDistributorId(): "+authenticationDTO.getDistributorId());

			responseAuthenticationDTO = partnerBeanRemote.validatePOSPartner(authenticationDTO);

			logger.info("After Procesing in CM: "+responseAuthenticationDTO.getResponseCode());
			logger.info("authenticationDTO.getCarrierId(): "+authenticationDTO.getCarrierId());

		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
		}

		return responseAuthenticationDTO;
	}

	/**
	 * The following are the business methods proxied to the
	 * EJB Session Bean. If any  exception is encountered,
	 * these methods convert them into application exceptions
	 */
	public POSMessageDTO validateIRPOSPartner(POSMessageDTO posSMessageDTO) throws DelegateException
	{
		POSAuthenticationDTO responsePOSAuthenticationDTO = null;
		POSAuthenticationDTO requestPOSAuthenticationDTO = (POSAuthenticationDTO)posSMessageDTO;

		try
		{
			logger.info("validateIRPOSPartner() Before Procesing in AppServer : "+requestPOSAuthenticationDTO.toString());


			responsePOSAuthenticationDTO = partnerBeanRemote.validateIRPOSPartner(requestPOSAuthenticationDTO);

			logger.info("After Processing in AppServer: "+responsePOSAuthenticationDTO.toString());

		}
		catch(Exception ex)
		{
			throw new DelegateException(ex.getMessage());
		}

		return responsePOSAuthenticationDTO;
	}

}
