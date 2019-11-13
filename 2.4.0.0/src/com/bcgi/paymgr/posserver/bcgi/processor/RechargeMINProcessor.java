package com.bcgi.paymgr.posserver.bcgi.processor;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.aircls.common.pos.dto.POSAccountRechargeDTO;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.delegate.RechargeDelegate;
import com.bcgi.paymgr.posserver.fw.processor.BaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;

public class RechargeMINProcessor extends BaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(RechargeMINProcessor.class);
	public RechargeMINProcessor() {
	}
	public String isValidMessage(POSMessageDTO posMessageDTO)
	{
		String status = ResponseCodes.INITIAL_STATE;
		if (posMessageDTO != null)
		{
			POSAccountRechargeDTO posAccountRechargeDTO = (POSAccountRechargeDTO) posMessageDTO;
			logger.info(posAccountRechargeDTO.toString());
			boolean isMIN = true;
			status = RechargeValidator.isValidReqData(posAccountRechargeDTO,isMIN);

			System.out.println("======status=recharge=="+status);
		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}

		System.out.println("======status=recharge1111=="+status);

		return status;
	}


	public POSMessageDTO processMessage(POSMessageDTO posMessageDTO)
	{
		POSAccountRechargeDTO posAccountRechargeDTO = (POSAccountRechargeDTO)posMessageDTO;
		String amount = "";
		try
		{
			if(posAccountRechargeDTO.getTransactionAmount() != null)
			{
				amount      = posAccountRechargeDTO.getTransactionAmount();
				logger.info("processMessage() amount before deducting last 2 digits: "+amount);
				amount		= amount.substring(0,amount.length()-2);
				posAccountRechargeDTO.setTransactionAmount(amount);
				logger.info("processMessage() amount after deducting last 2 digits: "+amount);
			}

			logger.info("Calling Recharge Delegate....");
			RechargeDelegate rechargeDelegate = new RechargeDelegate();
			posAccountRechargeDTO = rechargeDelegate.processMINRechargeTransaction(posAccountRechargeDTO);


		}
		catch (DelegateException exp){
			posAccountRechargeDTO.setResponseCode(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE);
			logger.error("DelegateException: "+exp.getMessage());
		}
		logger.error("at the end of processMessage");
		return posAccountRechargeDTO;
	}

	/**
	 * Unpacks ISO Message to retrieve Authentication related field values.
	 */
	public POSMessageDTO unpackISOMsg(ISOMsg requestISOMsg)
	{
		POSAccountRechargeDTO posAccountRechargeDTO = new POSAccountRechargeDTO();
		posAccountRechargeDTO.setResponseCode(ResponseCodes.INITIAL_STATE);


		try
		{
			posAccountRechargeDTO.setRequestType(requestISOMsg.getMTI());
			posAccountRechargeDTO.setSubscriberMIN(requestISOMsg.getString(ISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER));
			posAccountRechargeDTO.setProcessingCode(requestISOMsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID));
			posAccountRechargeDTO.setTransactionAmount(requestISOMsg.getString(ISOMessageFieldIDConstant.TRANSACTION_AMNT));
			posAccountRechargeDTO.setReferenceNumber(requestISOMsg.getString(ISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER));
			posAccountRechargeDTO.setTransactionTime(requestISOMsg.getString(ISOMessageFieldIDConstant.LOCAL_TRANSACTION_TIME));
			posAccountRechargeDTO.setTransactionDate(requestISOMsg.getString(ISOMessageFieldIDConstant.LOCAL_TRANSACTION_DATE));
			posAccountRechargeDTO.setDistributorId(requestISOMsg.getString(ISOMessageFieldIDConstant.DISTRIBUTOR_ID));

			String subagentId = requestISOMsg.getString(ISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
			}
			posAccountRechargeDTO.setSubAgentId(subagentId);

			String storemanagerId  = requestISOMsg.getString(ISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posAccountRechargeDTO.setStoreId(storemanagerId);
			posAccountRechargeDTO.setCarrierId(requestISOMsg.getString(ISOMessageFieldIDConstant.CARRIER_ID));
			posAccountRechargeDTO.setTransactionType(requestISOMsg.getString(ISOMessageFieldIDConstant.TRANSACTION_TYPE));
			posAccountRechargeDTO.setInitiatedUser(requestISOMsg.getString(ISOMessageFieldIDConstant.INIATED_USER));
			// Setting the message format type whether it is BCGI (Y) or IR (N)
			posAccountRechargeDTO.setMessageFormatType(MessageFormatConstant.DOMESTIC);


			posAccountRechargeDTO.setPaymentType(ServerConfigConstant.CASH+"");


		}
		catch(ISOException isoexp){
			posAccountRechargeDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		return posAccountRechargeDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO)
	{
		POSAccountRechargeDTO posAccountRechargeDTO = null;

		try
		{
			if (posMessageDTO != null)
			{
				posAccountRechargeDTO = (POSAccountRechargeDTO) posMessageDTO;
				requestISOMsg.set(ISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,posAccountRechargeDTO.getAuthorizationNumber());
				requestISOMsg.set(ISOMessageFieldIDConstant.RESPONSE_CODE_ID,posAccountRechargeDTO.getResponseCode());

				logger.info("posAccountRechargeDTO.getResponseCode()()===="+posAccountRechargeDTO.getResponseCode());
				logger.info("posAccountRechargeDTO.getTransactionId()===="+posAccountRechargeDTO.getTransactionId());
				//added to set the transaction id to reset the transaction at PM upon sending the response
				requestISOMsg.set(ISOMessageFieldIDConstant.TRANSACTION_ID,posAccountRechargeDTO.getTransactionId());

				if(posAccountRechargeDTO.getResponseCode().equals(ResponseCodes.SUCCESS))
				{
					requestISOMsg.set(ISOMessageFieldIDConstant.ADDITIONAL_AMOUNT,posAccountRechargeDTO.getPrepaidAccBalance());
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

		POSAccountRechargeDTO posAccountRechargeDTO =  (POSAccountRechargeDTO) unpackISOMsg(requestISOMsg);


		if (posAccountRechargeDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
		{
			  String status = isValidMessage(posAccountRechargeDTO);

			  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
			  {
				  logger.error("Before  processMessage.......");

				  posAccountRechargeDTO = (POSAccountRechargeDTO)processMessage(posAccountRechargeDTO);

				  logger.error("After processMessage====>"+posAccountRechargeDTO);
			  }
			  else
			  {
				  posAccountRechargeDTO.setResponseCode(status);
			  }

			}

		responseISOMsg = packISOMsg(requestISOMsg,posAccountRechargeDTO);

		return responseISOMsg;
	}

	public void updateSendMsgStatus(String transactionID,String status){

		POSAccountRechargeDTO posAccountRechargeDTO = new POSAccountRechargeDTO();
		posAccountRechargeDTO.setTransactionId(transactionID);
		posAccountRechargeDTO.setTransactionStatus(status);

		try{

			RechargeDelegate rechargeDelegate = new RechargeDelegate();
			rechargeDelegate.updateSendMsgStatus(posAccountRechargeDTO);
		}
		catch(Exception exp){
			exp.printStackTrace();
			logger.error("RechargeMINProcessor-Exception at updateSendMsgStatus(): "+exp.getMessage());

		}
	}
}
