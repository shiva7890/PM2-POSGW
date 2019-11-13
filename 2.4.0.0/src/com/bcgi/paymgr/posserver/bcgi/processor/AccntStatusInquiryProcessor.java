package com.bcgi.paymgr.posserver.bcgi.processor;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.aircls.common.pos.dto.POSAccountStatusDTO;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.delegate.StatusInquiryDelegate;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.fw.processor.BaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;

public class AccntStatusInquiryProcessor extends BaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(AccntStatusInquiryProcessor.class);
	public AccntStatusInquiryProcessor()
	{
	}

	public String isValidMessage(POSMessageDTO posMessageDTO)
	{

		String status = ResponseCodes.INITIAL_STATE;
		if (posMessageDTO != null)
		{
			POSAccountStatusDTO posAccountStatusDTO = (POSAccountStatusDTO) posMessageDTO;
			logger.info(posAccountStatusDTO.toString());
			status = AccntStatusInquiryValidator.isValidReqData(posAccountStatusDTO);
		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}

		return status;


	}


	public POSMessageDTO processMessage(POSMessageDTO posMessageDTO)
	{
		POSAccountStatusDTO posAccountStatusDTO = (POSAccountStatusDTO)posMessageDTO;
		try
		{
			logger.info("Calling Status Inquiry Delegate....");
			StatusInquiryDelegate inquiryDelegate = new StatusInquiryDelegate();
			posAccountStatusDTO = inquiryDelegate.getSubscriberAccountStatus(posAccountStatusDTO);
		}
		catch (DelegateException exp){
			posAccountStatusDTO.setResponseCode(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE);
			logger.error("DelegateException: "+exp.getMessage());
		}
		logger.error("at the end of processMessage");
		return posAccountStatusDTO;
	}

	/**
	 * Unpacks ISO Message to retrieve Authentication related field values.
	 */
	public POSMessageDTO unpackISOMsg(ISOMsg requestISOMsg)
	{
		POSAccountStatusDTO posAccountStatusDTO = new POSAccountStatusDTO();
		posAccountStatusDTO.setResponseCode(ResponseCodes.INITIAL_STATE);

		try
		{
			posAccountStatusDTO.setRequestType(requestISOMsg.getMTI());
			posAccountStatusDTO.setSubscriberMIN(requestISOMsg.getString(ISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER));
			posAccountStatusDTO.setProcessingCode(requestISOMsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID));
			posAccountStatusDTO.setReferenceNumber(requestISOMsg.getString(ISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER));
			posAccountStatusDTO.setTransactionTime(requestISOMsg.getString(ISOMessageFieldIDConstant.LOCAL_TRANSACTION_TIME));
			posAccountStatusDTO.setTransactionDate(requestISOMsg.getString(ISOMessageFieldIDConstant.LOCAL_TRANSACTION_DATE));
			posAccountStatusDTO.setDistributorId(requestISOMsg.getString(ISOMessageFieldIDConstant.DISTRIBUTOR_ID));

			String subagentId = requestISOMsg.getString(ISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
			}
			posAccountStatusDTO.setSubAgentId(subagentId);

			String storemanagerId  = requestISOMsg.getString(ISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posAccountStatusDTO.setStoreId(storemanagerId);
			posAccountStatusDTO.setCarrierId(requestISOMsg.getString(ISOMessageFieldIDConstant.CARRIER_ID));
			posAccountStatusDTO.setTransactionType(requestISOMsg.getString(ISOMessageFieldIDConstant.TRANSACTION_TYPE));
			posAccountStatusDTO.setInitiatedUser(requestISOMsg.getString(ISOMessageFieldIDConstant.INIATED_USER));

			// Setting the message format type whether it is BCGI (Y) or IR (N)
			posAccountStatusDTO.setMessageFormatType(MessageFormatConstant.DOMESTIC);



		}
		catch(ISOException isoexp){
			posAccountStatusDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		return posAccountStatusDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO)
	{
		POSAccountStatusDTO posAccountStatusDTO = null;

		try
		{
			if (posMessageDTO != null)
			{
				posAccountStatusDTO = (POSAccountStatusDTO) posMessageDTO;
				requestISOMsg.set(ISOMessageFieldIDConstant.RESPONSE_CODE_ID,posAccountStatusDTO.getResponseCode());
				if (posAccountStatusDTO.getResponseCode().equals(ResponseCodes.SUCCESS))
				{
					requestISOMsg.set(ISOMessageFieldIDConstant.ADDITIONAL_AMOUNT,posAccountStatusDTO.getAccountBalance());
					requestISOMsg.set(ISOMessageFieldIDConstant.ACCOUNT_STATUS_CODE,posAccountStatusDTO.getAccountStatusCode());
	 		        requestISOMsg.set(ISOMessageFieldIDConstant.EXPIRATION_DATE,posAccountStatusDTO.getAccountExpiryDate());
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

		POSAccountStatusDTO posAccountStatusDTO =  (POSAccountStatusDTO) unpackISOMsg(requestISOMsg);
		if (posAccountStatusDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
		{
			  String status = isValidMessage(posAccountStatusDTO);

			  System.out.println("status====>"+status);

			  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
				{
				  logger.error("Before  processMessage");
				  posAccountStatusDTO = (POSAccountStatusDTO)processMessage(posAccountStatusDTO);
				  logger.error("After processMessage");
				}
			  else{
				  posAccountStatusDTO.setResponseCode(status);
			  }

		}

		responseISOMsg = packISOMsg(requestISOMsg,posAccountStatusDTO);

		return responseISOMsg;
	}
}


