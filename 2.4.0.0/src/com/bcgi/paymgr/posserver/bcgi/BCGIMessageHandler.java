package com.bcgi.paymgr.posserver.bcgi;

import java.io.IOException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOUtil;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.aircls.common.pos.serverconstants.TransactionStates;
import com.bcgi.paymgr.posserver.CommandMapper;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.bcgi.constant.ProcessorConstant;
import com.bcgi.paymgr.posserver.bcgi.processor.AuthenticationProcessor;
import com.bcgi.paymgr.posserver.bcgi.processor.MessageTypeValidator;
import com.bcgi.paymgr.posserver.fw.controller.MessageController;
import com.bcgi.paymgr.posserver.fw.processor.MessageProcessor;

public class BCGIMessageHandler implements Runnable
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(MessageController.class);
    private int messageFormat = 0;
    public ISOSource source;
	public ISOMsg requestISOMsg;
	
	public BCGIMessageHandler(ISOSource source, ISOMsg isomessage,int msgFormat){
		super();
		this.source = source;
		this.requestISOMsg = isomessage;
		this.messageFormat = msgFormat;
	}
	
	public void run()
	{
		ISOMsg responseISOMsg = requestISOMsg;
		try
		{

			handleMessageHeader(requestISOMsg);

			printRequestMessageToBytes(requestISOMsg);

			String status = isSupportedMesageType(requestISOMsg);

			if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
			{
				  status = isSupportedProcessingCode(requestISOMsg);

				  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
				  {
					  long startTime = System.currentTimeMillis();

				    //authenticate  any incoming message
				    AuthenticationProcessor authenticationProcessor = new AuthenticationProcessor();
				    responseISOMsg = authenticationProcessor.execute(requestISOMsg,null);
				    String authStatus = responseISOMsg.getString(ISOMessageFieldIDConstant.RESPONSE_CODE_ID);

				    if (authStatus.equals(ResponseCodes.SUCCESS))
				     {
				 	    //call appropriate Message Processor based on MessageTypes and Processing code
				    	 String messageTypeID = requestISOMsg.getMTI();
					     String processingCodeID = requestISOMsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID);
					     String processorID = messageTypeID+"_"+processingCodeID;
					     logger.error("processorID: "+processorID);
					     MessageProcessor messageProcesor = CommandMapper.getInstance(messageFormat).getMessageProcessor(processorID);
					     if (messageProcesor != null)
					     {
					        responseISOMsg = messageProcesor.execute(requestISOMsg,null);
					     }
					     else{
					    	logger.error("Unable to find Processor for ProcessorID: "+processorID);
					     }
					 }

				    dispatchResponse(source, responseISOMsg);

				    logger.info("Time took to Process==in Seconds=======>"+( (System.currentTimeMillis() -  startTime) / 1000 ) );
				 }
				 else
				 {
					 //Invalid  Processing code
						responseISOMsg.set(ISOMessageFieldIDConstant.RESPONSE_CODE_ID,status);
						dispatchResponse(source, responseISOMsg);
						//return true;
				  }


			}
			else
			{
				// Invalid Type of Message
				responseISOMsg.set(ISOMessageFieldIDConstant.RESPONSE_CODE_ID,status);
				dispatchInvalidMsgTypeResponse(source, responseISOMsg);
				//return true;

			}
		}
		catch (ISOException isoexp)
		{
			logger.error("RESPONSE MESG - TO CLIENT(Exception in Request processing)"+isoexp.getMessage());
			try{

				responseISOMsg.set (new ISOField (ISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT));
				dispatchResponse(source, responseISOMsg);

			}
			catch(Exception exp)
			{
				exp.printStackTrace();
				logger.error("process() Exception is in catch block of Exception:"+exp.toString());
			}
			//return true;
		}
	 //return true;
	}
	private void dispatchResponse(ISOSource source, ISOMsg isomsg){


		try
		{
			if (source != null && isomsg != null){

				String reqMessageTypeID = "";
				String processingCodeID = "";
				String transactionID = "";
				boolean isSendSuccess = false;

				try
				{
					reqMessageTypeID = isomsg.getMTI();
					processingCodeID = isomsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID);
					transactionID    = isomsg.getString(ISOMessageFieldIDConstant.TRANSACTION_ID);
				}
				catch (ISOException exp)
				{
					logger.error("dispatchResponse(): ISOException"+exp.getMessage());
				}

				isomsg.setResponseMTI();
				isomsg.unset(ISOMessageFieldIDConstant.CARRIER_ID);
				isomsg.unset(ISOMessageFieldIDConstant.TRANSACTION_TYPE);
				isomsg.unset(ISOMessageFieldIDConstant.INIATED_USER);
				isomsg.recalcBitMap();
				byte[] messagebytes = isomsg.pack();
				logger.error("RESPONSE MESG - TO CLIENT in HEX "+ISOUtil.hexString(messagebytes));

		if(isomsg != null)
		{

			try
			{
				System.out.println("dispatchResponse()... - transactionID: "+transactionID);
				source.send(isomsg);
				isSendSuccess = true;
			}
			catch (ISOException exp)
			{

				isSendSuccess = false;
				logger.error("dispatchResponse(): ISOException"+exp.getMessage());

			}
			catch (IOException ioexp)
			{
				isSendSuccess = false;
				logger.error("dispatchResponse(): IOException"+ioexp.getMessage());
			}

			if (isSendSuccess){

			updateSendStatus(reqMessageTypeID,processingCodeID,transactionID,TransactionStates.COMPLETED);

			}
			else
			{
				updateSendStatus(reqMessageTypeID,processingCodeID,transactionID,TransactionStates.SEND_FAILED);
			}

		}//if(isomsg != null)


			}
			else {
				logger.error("Unable to send RESPONSE MESG. (Reason: ISOSource is null or ISOMsg is null)");
			}
		}
		catch (ISOException exp)
		{
			logger.error("dispatchResponse(): ISOException"+exp.getMessage());
		}
	
	}

	private void dispatchInvalidMsgTypeResponse(ISOSource source, ISOMsg isomsg){
		try
		{
			if (source != null && isomsg != null){
				isomsg.setMTI(ResponseCodes.INVALID_MESG_TYPE_ID_RESPONSE);
				isomsg.unset(ISOMessageFieldIDConstant.CARRIER_ID);
				isomsg.unset(ISOMessageFieldIDConstant.TRANSACTION_TYPE);
				isomsg.unset(ISOMessageFieldIDConstant.INIATED_USER);
				isomsg.recalcBitMap();
				byte[] messagebytes = isomsg.pack();
				logger.error("RESPONSE MESG - TO CLIENT in HEX "+ISOUtil.hexString(messagebytes));
				source.send(isomsg);
			}
			else {
				logger.error("Unable to send RESPONSE MESG. (Reason: ISOSource is null or ISOMsg is null)");
			}
		}
		catch (ISOException exp)
		{
			logger.error("dispatchResponse(): ISOException"+exp.getMessage());
		}
		catch (IOException ioexp)
		{
			logger.error("dispatchResponse(): IOException"+ioexp.getMessage());
		}

	}

	private void handleMessageHeader(ISOMsg requestISOMsg) {
		byte[] msgheaderb = requestISOMsg.getHeader();

		if (msgheaderb == null){
			logger.error("REQ MESG Header - FROM CLIENT: Empty");

		}
		else{
			logger.error("REQ MESG Header - FROM CLIENT: "+ISOUtil.hexString(msgheaderb));
		}
	}

	private void printRequestMessageToBytes(ISOMsg requestISOMsg){
		try
		{
			byte[] messagebytes = requestISOMsg.pack();
			logger.error("REQ MESG - FROM CLIENT in HEX: "+ISOUtil.hexString(messagebytes));
		}
		catch (ISOException exp)
		{
			logger.error("printRequestMessageToBytes(): ISOException"+exp.getMessage());
		}
	}

	/**
	 *
	 * @param requestISOMsg
	 * @return
	 */
	private String isSupportedMesageType(ISOMsg requestISOMsg)
	{

		try
		{
			String mti = requestISOMsg.getMTI();
		    String processingCodeID = requestISOMsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			logger.info("isSupportedMesageType(): mti -"+mti);
			logger.info("isSupportedMesageType(): processingCodeID -"+processingCodeID);

			String status = MessageTypeValidator.isValidMessageTypeId(mti);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}


		}
		catch (ISOException exp)
		{
	     	logger.error("isSupportedMesageType(): ISOException"+exp.getMessage());
			return ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}
	private String isSupportedProcessingCode(ISOMsg requestISOMsg)
	{

		try
		{
			String mti = requestISOMsg.getMTI();
		    String processingCodeID = requestISOMsg.getString(ISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			logger.info("isSupportedMesageType(): mti -"+mti);
			logger.info("isSupportedMesageType(): processingCodeID -"+processingCodeID);

			String status = MessageTypeValidator.isValidProcessingCode(mti,processingCodeID);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}

		}
		catch (ISOException exp)
		{
	     	logger.error("isSupportedMesageType(): ISOException"+exp.getMessage());
			return ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}

	private void updateSendStatus(String messageTypeID,
				                      String processingCodeID,
				                      String transactionID,
				                      String status)
	{

			if(transactionID != null && transactionID.length() > 0){

			 MessageProcessor messageProcesor = getMessageProcessor(messageTypeID,processingCodeID);

		     if (messageProcesor != null)
		     {
		        messageProcesor.updateSendMsgStatus(transactionID,status);
		     }

			}
	}


	// This will return all processors except the ECHO and Account Inquiry
	private MessageProcessor getMessageProcessor(String messageTypeID,String processingCodeID)
	{
			 MessageProcessor messageProcesor = null;
			 String processorID = "";

			 //call appropriate Message Processor based on MessageTypes and Processing code
			 if (!messageTypeID.equals(ProcessorConstant.ECHO_MSG_TYPE_ID)){
			   processorID = messageTypeID+"_"+processingCodeID;
			 }
			 else{
				processorID= messageTypeID;
			 }
			 logger.error("processorID: "+processorID);

			 if(!processorID.equals(ProcessorConstant.ACCOUNT_INQUIRY_PROCESSOR_ID))
			 	messageProcesor = CommandMapper.getInstance(messageFormat).getMessageProcessor(processorID);
			 else
			 {
				logger.info("No processors for resetting the POS transaction at PM");
			 }

			 if (messageProcesor == null)
			 {
				logger.error("Unable to find Message Processor for ProcessorID: "+processorID);
			 }

			 return messageProcesor;
	}


}
