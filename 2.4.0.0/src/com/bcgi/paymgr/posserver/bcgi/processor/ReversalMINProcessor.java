package com.bcgi.paymgr.posserver.bcgi.processor;


import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;


import com.aircls.common.pos.dto.POSMessageDTO;
import com.aircls.common.pos.dto.POSAccountReversalDTO;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import  com.bcgi.paymgr.posserver.common.constant.MessageDataConstant;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.delegate.ReversalDelegate;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.fw.processor.BaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;

public class ReversalMINProcessor extends BaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(ReversalMINProcessor.class);
	public ReversalMINProcessor() {

	}
	public String isValidMessage(POSMessageDTO posMessageDTO)
	{
		String status = ResponseCodes.INITIAL_STATE;
		if (posMessageDTO != null)
		{
			POSAccountReversalDTO posAccountReversalDTO = (POSAccountReversalDTO) posMessageDTO;
			logger.info(posAccountReversalDTO.toString());
			boolean isMIN = true;
			status = ReversalValidator.isValidReqData(posAccountReversalDTO,isMIN);
		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}

		return status;

	}


	public POSMessageDTO processMessage(POSMessageDTO posMessageDTO)
	{
		POSAccountReversalDTO posAccountReversalDTO = (POSAccountReversalDTO)posMessageDTO;
		String amount = "";
		try
		{
			//Truncating the padding zeroes at the beginning of Recharge Ref Number
			if (posAccountReversalDTO != null) {
				String rechargeRefNumber = posAccountReversalDTO.getRechargeReferenceNumber();
				int len = rechargeRefNumber.length();
				if (len == MessageDataConstant.RECHARGE_REF_NUM_FIELD_LENGTH){
				  String tempnumber = rechargeRefNumber.substring(MessageDataConstant.RECHARGE_REF_NUM_START_POS,rechargeRefNumber.length());
				  posAccountReversalDTO.setRechargeReferenceNumber(tempnumber);
				}
			}

			if(posAccountReversalDTO.getTransactionAmount() != null)
			{
				amount      = posAccountReversalDTO.getTransactionAmount();

				amount		= amount.substring(0,amount.length()-2);
				posAccountReversalDTO.setTransactionAmount(amount);

			}

			logger.info("Calling Reversal Delegate....");
			ReversalDelegate reversalDelegate = new ReversalDelegate();
			posAccountReversalDTO = reversalDelegate.revertMINRechargeTransaction(posAccountReversalDTO);


		}
		catch (DelegateException exp){
			posAccountReversalDTO.setResponseCode(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE);
			logger.error("DelegateException: "+exp.getMessage());
		}
		logger.error("at the end of processMessage");
		return posAccountReversalDTO;
	}

	/**
	 * Unpacks ISO Message to retrieve Authentication related field values.
	 */
	public POSMessageDTO unpackISOMsg(ISOMsg requestISOMsg)
	{
		POSAccountReversalDTO posAccountReversalDTO = new POSAccountReversalDTO();
		posAccountReversalDTO.setResponseCode(ResponseCodes.INITIAL_STATE);


		try
		{
			posAccountReversalDTO.setRequestType(requestISOMsg.getMTI());
			posAccountReversalDTO.setSubscriberMIN(requestISOMsg.getString(ISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER));
			posAccountReversalDTO.setProcessingCode(requestISOMsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID));
			posAccountReversalDTO.setTransactionAmount(requestISOMsg.getString(ISOMessageFieldIDConstant.TRANSACTION_AMNT));
			posAccountReversalDTO.setReferenceNumber(requestISOMsg.getString(ISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER));
			posAccountReversalDTO.setTransactionTime(requestISOMsg.getString(ISOMessageFieldIDConstant.LOCAL_TRANSACTION_TIME));
			posAccountReversalDTO.setTransactionDate(requestISOMsg.getString(ISOMessageFieldIDConstant.LOCAL_TRANSACTION_DATE));
			posAccountReversalDTO.setDistributorId(requestISOMsg.getString(ISOMessageFieldIDConstant.DISTRIBUTOR_ID));
			String subagentId = requestISOMsg.getString(ISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
			}
			posAccountReversalDTO.setSubAgentId(subagentId);

			String storemanagerId  = requestISOMsg.getString(ISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posAccountReversalDTO.setStoreId(storemanagerId);	posAccountReversalDTO.setRechargeReferenceNumber(requestISOMsg.getString(ISOMessageFieldIDConstant.RECHARGE_REFERENCE_NUMBER));
			posAccountReversalDTO.setCarrierId(requestISOMsg.getString(ISOMessageFieldIDConstant.CARRIER_ID));
			posAccountReversalDTO.setTransactionType(requestISOMsg.getString(ISOMessageFieldIDConstant.TRANSACTION_TYPE));
			posAccountReversalDTO.setInitiatedUser(requestISOMsg.getString(ISOMessageFieldIDConstant.INIATED_USER));

			// Setting the message format type whether it is BCGI (Y) or IR (N)
			posAccountReversalDTO.setMessageFormatType(MessageFormatConstant.DOMESTIC);


		}
		catch(ISOException isoexp){
			posAccountReversalDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		return posAccountReversalDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO)
	{
		POSAccountReversalDTO posAccountReversalDTO = null;

		try
		{
			if (posMessageDTO != null)
			{
				posAccountReversalDTO = (POSAccountReversalDTO) posMessageDTO;
				requestISOMsg.set(ISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,posAccountReversalDTO.getAuthorizationNumber());
				requestISOMsg.set(ISOMessageFieldIDConstant.RESPONSE_CODE_ID,posAccountReversalDTO.getResponseCode());

				logger.info("posAccountReversalDTO.getTransactionId()===="+posAccountReversalDTO.getTransactionId());

				//added to set the transaction id to reset the transaction at PM upon sending the response
				requestISOMsg.set(ISOMessageFieldIDConstant.TRANSACTION_ID,posAccountReversalDTO.getTransactionId());

				if (posAccountReversalDTO.getResponseCode().equals(ResponseCodes.SUCCESS))
				{
				  requestISOMsg.set(ISOMessageFieldIDConstant.ADDITIONAL_AMOUNT,posAccountReversalDTO.getAccountBalance());
				}

			}
		}
		catch(ISOException isoexp){
			logger.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){

		logger.info("Recharge MIN Processor- execute()........");
		ISOMsg responseISOMsg = requestISOMsg;

		POSAccountReversalDTO posAccountReversalDTO =  (POSAccountReversalDTO) unpackISOMsg(requestISOMsg);
		if (posAccountReversalDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
		{
			  String status = isValidMessage(posAccountReversalDTO);
			  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
				{
				  logger.debug("Before  processMessage:"+posAccountReversalDTO.toString());
				  posAccountReversalDTO = (POSAccountReversalDTO)processMessage(posAccountReversalDTO);
				  logger.debug("After processMessage:"+posAccountReversalDTO.toString());
				}
			  else{
				  posAccountReversalDTO.setResponseCode(status);
			  }

		}

		responseISOMsg = packISOMsg(requestISOMsg,posAccountReversalDTO);

		return responseISOMsg;
	}

	public void updateSendMsgStatus(String transactionID,String status)
	{
		POSAccountReversalDTO posAccountReversalDTO = new POSAccountReversalDTO();
		posAccountReversalDTO.setTransactionId(transactionID);
		posAccountReversalDTO.setTransactionStatus(status);
    	try{
    		ReversalDelegate reversalDelegate = new ReversalDelegate();
    		reversalDelegate.updateSendMsgStatus(posAccountReversalDTO);
    	}
        catch(Exception exp){
        	exp.printStackTrace();
        	logger.error("ReversalMINProcessor-Exception at updateSendMsgStatus(): "+exp.getMessage());

        }
	}
}
