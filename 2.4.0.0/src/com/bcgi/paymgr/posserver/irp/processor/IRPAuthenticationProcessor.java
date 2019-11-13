package com.bcgi.paymgr.posserver.irp.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;


import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;
import com.aircls.common.pos.dto.POSAuthenticationDTO;
import com.aircls.common.pos.serverconstants.ResponseCodes;

import com.aircls.common.pos.serverconstants.RequestConstants;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.aircls.common.pos.dto.IRMessageDTO;
import com.bcgi.paymgr.posserver.common.constant.AppConstant;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.delegate.AuthDelegate;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.fw.processor.BaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.constant.IRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPTransactionTypeConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAuthenticationDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.irp.validator.IRPReqMsgDataValidator;


public class IRPAuthenticationProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(IRPAuthenticationProcessor.class);
	static String originCurrencyCode;
	public IRPAuthenticationProcessor()
	{

	}


	public String isValidMessage(POSGWMessageDTO posMessageDTO)
	{
		String status = ResponseCodes.INITIAL_STATE;
		if (posMessageDTO != null)
		{
			POSGWAuthenticationDTO posAuthenticationDTO = (POSGWAuthenticationDTO) posMessageDTO;
			logger.info(posAuthenticationDTO.toString());
System.out.println("####IN VALID MESSAGE==");
			String transactionType = posAuthenticationDTO.getTransactionType();
     		String distributorId = posAuthenticationDTO.getDistributorId();
     		status = IRPReqMsgDataValidator.isValidDistributorId(distributorId);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}


			if (!transactionType.equals(IRPTransactionTypeConstant.ECHO_TRANSACTION_TYPE))
			{

				String subAgentId = posAuthenticationDTO.getSubAgentId();
				String storeId = posAuthenticationDTO.getStoreId();


			    status = IRPReqMsgDataValidator.isValidSubAgentId(subAgentId);
				if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					return status;
				}


				status = IRPReqMsgDataValidator.isValidStoreId(storeId);
				if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					return status;
				}

				status = IRPReqMsgDataValidator.isValidIRPSubscriberNumber(posAuthenticationDTO.getSubscriberMIN());
				if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					return status;
				}

			}
		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}

		return status;
	}


	public POSGWMessageDTO processMessage(POSGWMessageDTO reqPOSGWMessageDTO)
	{

		POSGWMessageDTO respPOSGWMessageDTO = null;
		try
		{

			POSMessageDTO posMessageDTO = packToServerDTO(reqPOSGWMessageDTO);
			logger.info("Calling Auth Delegate....");
			AuthDelegate authDelegate = new AuthDelegate();
			POSMessageDTO respPOSMessageDTO = authDelegate.validateIRPOSPartner(posMessageDTO);
			logger.info("Before unpackFromServerDTO....");
			respPOSGWMessageDTO = unpackFromServerDTO(respPOSMessageDTO);
			logger.info("After unpackFromServerDTO....");

		}
		catch (DelegateException exp){
			POSGWAuthenticationDTO respPOSGWAuthenticationDTO = new POSGWAuthenticationDTO();
			respPOSGWAuthenticationDTO.setResponseCode(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE);
			respPOSGWMessageDTO = respPOSGWAuthenticationDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}

		return respPOSGWMessageDTO;
	}

	public POSGWMessageDTO unpackFromServerDTO(POSMessageDTO posMessageDTO)
	{

		logger.info("In unpackFromServerDTO....");
		POSGWAuthenticationDTO posGWAuthenticationDTO = new POSGWAuthenticationDTO();
    	POSAuthenticationDTO posAuthenticationDTO = (POSAuthenticationDTO)posMessageDTO;

    	String transactionType = posAuthenticationDTO.getTransactionType();
    	String distributorId = posAuthenticationDTO.getDistributorId();
    	String responseCode = posAuthenticationDTO.getResponseCode();
    	posGWAuthenticationDTO.setTransactionType(transactionType);
    	posGWAuthenticationDTO.setDistributorId(distributorId);
    	posGWAuthenticationDTO.setResponseCode(responseCode);
    	logger.info("In unpackFromServerDTO before if....");
    	if (!transactionType.equals(IRPTransactionTypeConstant.ECHO_TRANSACTION_TYPE))
		{

		  	String carrierId = posAuthenticationDTO.getCarrierId();
		    posGWAuthenticationDTO.setCarrierId(carrierId);

		    String initiatedUser = posAuthenticationDTO.getInitiatedUser();
    	   	posGWAuthenticationDTO.setInitiatedUser(initiatedUser);

            IRMessageDTO irMessageDTO = posAuthenticationDTO.getIrMessageDTO();
    		String destCountryCode = irMessageDTO.getDestinationCountryCode();

    		POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
    		posGWIRMessageDTO.setDestinationCountryCode(destCountryCode);
    		posGWAuthenticationDTO.setIrMessageDTO(posGWIRMessageDTO);
		}



		return posGWAuthenticationDTO;
	}

    public POSMessageDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
    {
    	logger.info("packToServerDTO(POSGWAuthenticationDTO->POSAuthenticationDTO)");

    	POSAuthenticationDTO posAuthenticationDTO = new POSAuthenticationDTO();
    	POSGWAuthenticationDTO posGWAuthenticationDTO =  (POSGWAuthenticationDTO)posGWMessageDTO;
    	logger.info(posGWAuthenticationDTO.toString());

    	String transactionType = posGWAuthenticationDTO.getTransactionType();
    	String distributorId = posGWAuthenticationDTO.getDistributorId();
       	posAuthenticationDTO.setTransactionType(transactionType);
    	posAuthenticationDTO.setDistributorId(distributorId);

    	if (!transactionType.equals(IRPTransactionTypeConstant.ECHO_TRANSACTION_TYPE)){


    		String messageFormatType = posGWAuthenticationDTO.getMessageFormatType();
    		String subAgentId = posGWAuthenticationDTO.getSubAgentId();
    		String storeId = posGWAuthenticationDTO.getStoreId();

    		posAuthenticationDTO.setMessageFormatType(messageFormatType);
    		posAuthenticationDTO.setSubAgentId(subAgentId);
    		posAuthenticationDTO.setStoreId(storeId);

    		String subscriberNum = posGWAuthenticationDTO.getSubscriberMIN();

    		String countrycode = subscriberNum.substring(IRPMessageDataConstant.COUNTRYCODE_START_POSITION,IRPMessageDataConstant.COUNTRYCODE_END_POSITION);


    		IRMessageDTO irMessageDTO = new IRMessageDTO();
    		irMessageDTO.setDestinationCountryCode(countrycode);
    		irMessageDTO.setOriginCurrencyCode(originCurrencyCode);
    		posAuthenticationDTO.setSubscriberMIN(subscriberNum);
    		posAuthenticationDTO.setReferenceNumber(posGWAuthenticationDTO.getReferenceNumber());
    		posAuthenticationDTO.setTransactionAmount(posGWAuthenticationDTO.getTransactionAmount());
    		posAuthenticationDTO.setIrMessageDTO(irMessageDTO);
    	}
    	logger.info(posAuthenticationDTO.toString());

    	return posAuthenticationDTO;

	}

	/**
	 * Unpacks ISO Message to retrieve Authentication related field values.
	 */
	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg)
	{
		POSGWAuthenticationDTO posAuthenticationDTO = new POSGWAuthenticationDTO();
		posAuthenticationDTO.setResponseCode(ResponseCodes.INITIAL_STATE);

		try
		{
			String transactionType = getIRPTransactionType(requestISOMsg.getMTI());
			posAuthenticationDTO.setTransactionType(transactionType);

			String distributorID = requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			if (distributorID != null)
			{
				distributorID = distributorID.trim();
			}
			posAuthenticationDTO.setDistributorId(distributorID);

			if (!requestISOMsg.getMTI().equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
			{
				posAuthenticationDTO.setMessageFormatType(MessageFormatConstant.IRP);

				String subagentId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBAGENT_ID);
				if (subagentId != null){
					subagentId = subagentId.trim();
				}
				posAuthenticationDTO.setSubAgentId(subagentId);

				String storemanagerId  = requestISOMsg.getString(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID);
				if (storemanagerId != null){
					storemanagerId = storemanagerId.trim();
				}
				posAuthenticationDTO.setStoreId(storemanagerId);

				String subscriberMIN = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER);
				if (subscriberMIN != null){
					subscriberMIN = subscriberMIN.trim();
				}
				posAuthenticationDTO.setSubscriberMIN(subscriberMIN);
				posAuthenticationDTO.setReferenceNumber(requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1));

				originCurrencyCode = requestISOMsg.getString(IRPISOMessageFieldIDConstant.CURRENCY_CODE);

				posAuthenticationDTO.setTransactionAmount(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT));
				String min =  subscriberMIN.substring(25,30);

    		}

		}
		catch(ISOException isoexp){
			posAuthenticationDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		logger.info("unpackISOMsg() After unpack : "+ posAuthenticationDTO.toString());
		return posAuthenticationDTO;
	}



	public POSGWAuthenticationDTO execute(ISOMsg requestISOMsg)
	{
		POSGWAuthenticationDTO posGWAuthenticationDTO = null;
		try
		{

			 posGWAuthenticationDTO =  (POSGWAuthenticationDTO) unpackISOMsg(requestISOMsg);

			if (posGWAuthenticationDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
			{
				  String status = isValidMessage(posGWAuthenticationDTO);
				  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
				  {
					  posGWAuthenticationDTO = (POSGWAuthenticationDTO)processMessage(posGWAuthenticationDTO);
	      		   }
				  else
				  {
					  posGWAuthenticationDTO.setResponseCode(status);
				  }
			}
		}
		catch(Exception exp){
			posGWAuthenticationDTO = new POSGWAuthenticationDTO();
			posGWAuthenticationDTO.setResponseCode(ResponseCodes.POSGATEWAY_SYSTEM_ERROR);
			logger.error("IRPAuthenticationProcessor-Exception at execute(): "+exp.getMessage());

		}
		return posGWAuthenticationDTO;
	}

	public String getIRPTransactionType(String requestTypeId)
	{
		String transactionType = null;

		if(requestTypeId.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
		{
			transactionType = IRPTransactionTypeConstant.ECHO_TRANSACTION_TYPE;
		}
		else if(requestTypeId.equals(IRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID))
		{
			transactionType = IRPTransactionTypeConstant.RECHARGE_TRANSACTION_TYPE;

		}
		else if(requestTypeId.equals(IRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID))
		{
			transactionType = IRPTransactionTypeConstant.REVERSAL_TRANSACTION_TYPE;
		}
		else if(requestTypeId.equals(IRPMessageTypeIDConstant.TRANSACTION_STATUS_INQUIRY_MSG_TYPE_ID))
		{
			transactionType = IRPTransactionTypeConstant.TRANSACTION_STATUS_INQUIRY_TYPE;

		}
		else if(requestTypeId.equals(IRPMessageTypeIDConstant.ASYNC_RECHARGE_INQUIRY_TRANS_ID))
		{
			transactionType = IRPTransactionTypeConstant.TRANSACTION_STATUS_INQUIRY_TYPE;

		}
		return transactionType;
	}
}
