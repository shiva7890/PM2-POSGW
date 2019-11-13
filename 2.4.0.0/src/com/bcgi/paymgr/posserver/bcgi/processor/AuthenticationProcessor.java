package com.bcgi.paymgr.posserver.bcgi.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.aircls.common.pos.dto.POSAuthenticationDTO;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.bcgi.constant.ProcessorConstant;
import com.bcgi.paymgr.posserver.bcgi.constant.TransactionTypeConstant;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.aircls.common.pos.serverconstants.RequestConstants;
import com.bcgi.paymgr.posserver.delegate.AuthDelegate;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.fw.processor.BaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;

public class AuthenticationProcessor extends BaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(AuthenticationProcessor.class);
	
	public AuthenticationProcessor() {
		
	}
	public String isValidMessage(POSMessageDTO posMessageDTO)
	{
		String status = ResponseCodes.INITIAL_STATE;
		if (posMessageDTO != null)
		{
			POSAuthenticationDTO posAuthenticationDTO = (POSAuthenticationDTO) posMessageDTO;
			logger.info(posAuthenticationDTO.toString());

			String requestTypeID = posAuthenticationDTO.getRequestType();

			if (requestTypeID.equals(RequestConstants.ECHO_MSG_TYPE_ID)){
				String distributorId = posAuthenticationDTO.getDistributorId();
				return ReqMsgDataValidator.isValidDistributorId(distributorId);

			}
			else if(requestTypeID.equals(RequestConstants.RECHARGE_AND_ACNT_INQUIRY_MSG_TYPE_ID) || requestTypeID.equals(RequestConstants.REVERSAL_MSG_TYPE_ID)){

				String distributorId = posAuthenticationDTO.getDistributorId();
				String subAgentId = posAuthenticationDTO.getSubAgentId();
				String storeId = posAuthenticationDTO.getStoreId();

				status = ReqMsgDataValidator.isValidDistributorId(distributorId);
				if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					return status;
				}

			    status = ReqMsgDataValidator.isValidSubAgentId(subAgentId);
				if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
				logger.info("storeId: "+storeId);
				status = ReqMsgDataValidator.isValidStoreId(storeId);
				logger.info("storeId status: "+status);
				if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}
			else{
				status = ResponseCodes.UNSUPPORTED_MESSAGE_TYPE;
			}

		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}

		return status;
	}


	public POSMessageDTO processMessage(POSMessageDTO posMessageDTO)
	{
		POSAuthenticationDTO posAuthenticationDTO = (POSAuthenticationDTO)posMessageDTO;
		try
		{
			logger.info("Calling Auth Delegate....");
			AuthDelegate authDelegate = new AuthDelegate();
			posAuthenticationDTO = authDelegate.validateDomesticPartner(posAuthenticationDTO);

		}
		catch (DelegateException exp){
			posAuthenticationDTO.setResponseCode(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE);
			logger.error("DelegateException: "+exp.getMessage());
		}
		logger.error("at the end of processMessage");
		return posAuthenticationDTO;
	}

	/**
	 * Unpacks ISO Message to retrieve Authentication related field values.
	 */
	public POSMessageDTO unpackISOMsg(ISOMsg requestISOMsg)
	{
		POSAuthenticationDTO posAuthenticationDTO = new POSAuthenticationDTO();
		posAuthenticationDTO.setResponseCode(ResponseCodes.INITIAL_STATE);

		try
		{
			posAuthenticationDTO.setRequestType(requestISOMsg.getMTI());

			String transactionType = getDomesticTransactionType(requestISOMsg.getMTI(),requestISOMsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID));
			posAuthenticationDTO.setTransactionType(transactionType);

			posAuthenticationDTO.setProcessingCode(requestISOMsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID));
			posAuthenticationDTO.setDistributorId((requestISOMsg.getString(ISOMessageFieldIDConstant.DISTRIBUTOR_ID)));

			String subagentId = requestISOMsg.getString(ISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
			}
			posAuthenticationDTO.setSubAgentId(subagentId);

			String storemanagerId  = requestISOMsg.getString(ISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posAuthenticationDTO.setStoreId(storemanagerId);

		}
		catch(ISOException isoexp){
			posAuthenticationDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		logger.info("unpackISOMsg() After unpack : "+ posAuthenticationDTO.toString());
		return posAuthenticationDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO)
	{
		POSAuthenticationDTO posAuthenticationDTO = null;

		try
		{
			if (posMessageDTO != null)
			{
				posAuthenticationDTO = (POSAuthenticationDTO) posMessageDTO;
				requestISOMsg.set(ISOMessageFieldIDConstant.RESPONSE_CODE_ID,posAuthenticationDTO.getResponseCode());
				if (posAuthenticationDTO.getResponseCode().equals(ResponseCodes.SUCCESS))
				{
				  requestISOMsg.set(ISOMessageFieldIDConstant.CARRIER_ID,posAuthenticationDTO.getCarrierId());
				  requestISOMsg.set(ISOMessageFieldIDConstant.TRANSACTION_TYPE,posAuthenticationDTO.getTransactionType());
				  requestISOMsg.set(ISOMessageFieldIDConstant.INIATED_USER,posAuthenticationDTO.getInitiatedUser());
				}
			}
		}
		catch(ISOException isoexp){
			logger.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){

		ISOMsg responseISOMsg = requestISOMsg;

		POSAuthenticationDTO posAuthenticationDTO =  (POSAuthenticationDTO) unpackISOMsg(requestISOMsg);

		if (posAuthenticationDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
		{
			  String status = isValidMessage(posAuthenticationDTO);
			  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
				{
				  logger.error("Before  processMessage");
				  posAuthenticationDTO = (POSAuthenticationDTO)processMessage(posAuthenticationDTO);
				  logger.error("After processMessage");
				}
			  else
			  {
				  posAuthenticationDTO.setResponseCode(status);
			  }

		}

		responseISOMsg = packISOMsg(requestISOMsg,posAuthenticationDTO);

		return responseISOMsg;
	}
	public String getDomesticTransactionType(String requestTypeId,String processingId)
	{
			String transactionType = null;

			if(requestTypeId.equals(ProcessorConstant.ECHO_MSG_TYPE_ID))
			{
				transactionType = TransactionTypeConstant.ECHO_TRANSACTION_TYPE;
			}
			else if(requestTypeId.equals(ProcessorConstant.RECHARGE_MSG_TYPE_ID))
			{
				if(processingId.equals(ProcessorConstant.ACCOUNT_INQUIRY_PROCESSING_CODE_ID))
					transactionType = TransactionTypeConstant.ACCOUNT_INQUIRY_TYPE;
				else
				if(processingId.equals(ProcessorConstant.MIN_RECHARGE_PROCESSING_CODE_ID))
					transactionType = TransactionTypeConstant.RECHARGE_TRANSACTION_TYPE;
				else
				if(processingId.equals(ProcessorConstant.PIN_RECHARGE_PROCESSING_CODE_ID))
					 transactionType = TransactionTypeConstant.PIN_PURCHASE;

			}
			else if(requestTypeId.equals(ProcessorConstant.REVERSAL_MSG_TYPE_ID))
			{
				if(processingId.equals(ProcessorConstant.MIN_REVERSAL_PROCESSING_CODE_ID))
					transactionType = TransactionTypeConstant.REVERSAL_TRANSACTION_TYPE;
				else
				if(processingId.equals(ProcessorConstant.PIN_REVERSAL_PROCESSING_CODE_ID))
					transactionType = TransactionTypeConstant.PIN_REVERSAL;
			}

			return transactionType;
	}
}
