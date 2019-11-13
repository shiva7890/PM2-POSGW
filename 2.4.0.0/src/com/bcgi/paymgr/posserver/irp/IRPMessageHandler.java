package com.bcgi.paymgr.posserver.irp;

import java.io.IOException;
import java.util.TimeZone;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOUtil;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.aircls.common.pos.serverconstants.TransactionStates;
import com.bcgi.paymgr.posserver.IRCommandMapper;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.DateUtil;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.fw.controller.MessageController;
import com.bcgi.paymgr.posserver.fw.processor.IRPMessageProcessor;
import com.bcgi.paymgr.posserver.irp.constant.IRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.PlaceHolderDataConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAuthenticationDTO;
import com.bcgi.paymgr.posserver.irp.logger.IRPDistLogger;
import com.bcgi.paymgr.posserver.irp.processor.IRPAuthenticationProcessor;
import com.bcgi.paymgr.posserver.irp.validator.IRPMessageTypeValidator;
import com.bcgi.paymgr.posserver.POSServerMgr;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import org.apache.log4j.Logger;


public class IRPMessageHandler implements Runnable
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(MessageController.class);
    private int messageFormat = 0;
    public ISOSource source;
	public ISOMsg requestISOMsg;
	Logger logs = IRPDistLogger.getIRPLogger(ServerConfigConstant.INVALID_DIST_LOG_FILENAME);
	public IRPMessageHandler(ISOSource source, ISOMsg isomessage,int msgFormat)
	{
		super();
		this.source = source;
		this.requestISOMsg = isomessage;
		this.messageFormat = msgFormat;
	}
	
	public void run(){
		long currentTime = 0;
		long processTime = 0;
		String endDateTime = "";
		String startDateTime = "";
		long startTime = 0;
		long processTime1 = 0;
		String endDateTime1 = "";
		String startDateTime1 = "";
		String transType = "";
		String transStatus = "";
		String carrierId = "";
		ISOMsg responseISOMsg = requestISOMsg;
		startTime = System.currentTimeMillis();
		String posTimeZone = (String)POSServerMgr.getPosTimeZone().get(ServerConfigConstant.POS_TIME_ZONE);
		System.out.println("The pos Time zone in IRP Message Handler is ------------------->"+posTimeZone);
		
		startDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		startDateTime1 = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		String messageTypeID = "";
		try
		{
			
			
			handleMessageHeader(requestISOMsg);

			printRequestMessageToBytes(requestISOMsg);

			String status = isSupportedMesageType(requestISOMsg);

			if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
			{
				currentTime = System.currentTimeMillis();
				startDateTime1 = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
				  status = isSupportedProcessingCode(requestISOMsg);

				  logger.info("status====>"+status);

				  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
				  {
					  currentTime = System.currentTimeMillis();
					  startDateTime1 = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
					startTime = System.currentTimeMillis();

				    //authenticate  any incoming message
				    IRPAuthenticationProcessor authenticationProcessor = new IRPAuthenticationProcessor();
				    POSGWAuthenticationDTO posGWAuthResponseDTO = authenticationProcessor.execute(requestISOMsg);

				    if("300000".equals(requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID)) && "47".equals(posGWAuthResponseDTO.getResponseCode())){
				   	 posGWAuthResponseDTO.setResponseCode(ResponseCodes.SUCCESS);
				    }
				    String authStatus = posGWAuthResponseDTO.getResponseCode();

				    carrierId = posGWAuthResponseDTO.getCarrierId();
				    logger.error("authStatus: "+authStatus);


				    if (authStatus.equals(ResponseCodes.SUCCESS))
				     {
						startDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
						currentTime = System.currentTimeMillis();
						
				       messageTypeID = requestISOMsg.getMTI();
				    	 String processingCodeID = requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
				    	System.out.println("IRPMESSAGEHANDLER 3 processingCodeID="+processingCodeID);
				    	 posGWAuthResponseDTO.setProcessingCode(processingCodeID);
						 IRPMessageProcessor messageProcesor = getMessageProcessor(messageTypeID,processingCodeID);
						 System.out.println("IRPMESSAGEHANDLER 4="+messageProcesor);
						 logger.info("messageProcesor====>"+messageProcesor);

					     if (messageProcesor != null)
					     {
					        responseISOMsg = messageProcesor.execute(requestISOMsg,posGWAuthResponseDTO);
					     }
					    processTime += System.currentTimeMillis() - currentTime;
						endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
					
					    
					    
						
					 }
				    else
				    {

				    	 responseISOMsg.set(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,authStatus);
						  messageTypeID = requestISOMsg.getMTI();
				    	 if (!messageTypeID.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)){
				    		 responseISOMsg.set(IRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,PlaceHolderDataConstant.AUTHORIZATION_NUMBER);
				    	 }

					 }

				    dispatchResponse(source, responseISOMsg);
				    processTime += System.currentTimeMillis() - currentTime;
			    	endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
				    processTime1 += System.currentTimeMillis() - currentTime;
				    endDateTime1 = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
				    logger.info("Time took to Process==in Seconds=======>"+( (System.currentTimeMillis() -  startTime) / 1000 ) );
				 }
				 else
				 {
					 startDateTime1 = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
					currentTime = System.currentTimeMillis();
				    	
					 //Invalid  Processing code
						responseISOMsg.set(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,status);
						 messageTypeID = requestISOMsg.getMTI();
				    	 if (!messageTypeID.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)){
				    		 responseISOMsg.set(IRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,PlaceHolderDataConstant.AUTHORIZATION_NUMBER);
				    	 }
						dispatchResponse(source, responseISOMsg);
				    	processTime += System.currentTimeMillis() - currentTime;
				    	endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
				    	processTime1 += System.currentTimeMillis() - currentTime;
				    	endDateTime1 = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
						//return true;
				  }


			}


		}
		catch (ISOException isoexp)
		{
			
			logger.error("RESPONSE MESG - TO CLIENT(Exception in Request processing)"+isoexp.getMessage());
			try{
				startDateTime1 = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
				currentTime = System.currentTimeMillis();
				responseISOMsg.set (new ISOField (IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT));
				messageTypeID = requestISOMsg.getMTI();
		    	 if (!messageTypeID.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)){
		    		 responseISOMsg.set(IRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,PlaceHolderDataConstant.AUTHORIZATION_NUMBER);
		    	 }
		    		
				dispatchResponse(source, responseISOMsg);
				processTime1 += System.currentTimeMillis() - currentTime;
		    	endDateTime1 = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		    	processTime += System.currentTimeMillis() - startTime;
		    	endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
			}
			catch(Exception exp)
			{
				exp.printStackTrace();
				logger.error("process() Exception is in catch block of Exception:"+exp.toString());
			}
			//return true;
		}
		finally
		{
			
			/*
			 * Modified the sources for seperate logging for invalid distributor.
			 * Surendra : August 26th 2008 
                           last modified : October 3rd 2008
			 */
			System.out.println("the response code to pos is ----->"+responseISOMsg.getString(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID));
			logger.error("the response code to pos is ----->"+responseISOMsg.getString(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID));
			String responseCode = responseISOMsg.getString(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID);
			
			if(responseCode!= null && responseCode.trim().length()>0 && (responseCode.equals(ResponseCodes.INVALID_DISTRIBUTOR)||responseCode.equals(ResponseCodes.INVALID_SUB_DISTRIBUTOR)||responseCode.equals(ResponseCodes.INVALID_STORE) ))
			{
				if(logs!=null)
					if(responseCode.equals(ResponseCodes.INVALID_DISTRIBUTOR))
					logs.info("Invalid distributor =====>"+responseISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID));
					else if(responseCode.equals(ResponseCodes.INVALID_SUB_DISTRIBUTOR))
						logs.info("Invalid sub distributor =====>"+responseISOMsg.getString(IRPISOMessageFieldIDConstant.SUBAGENT_ID));
					else if(responseCode.equals(ResponseCodes.INVALID_STORE))
						logs.info("Invalid store =====>"+responseISOMsg.getString(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID));
				else
					logger.error("Exception occured while writing invalid logs");
			}
			
			/*
			 * Modified the sources for not throwing the Exceptions to Echo Message
			 * Surendra : March 14th 2008 
			 */
			 if (!messageTypeID.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
			{	
				AppMonitorWrapper.logEvent("POSGW_REQUEST_TOTAL_TIME",
						  	"|"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER).substring(26).trim()
						   +"|"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1).trim()
						   +"|"+startDateTime
						   +"|"+endDateTime
						   +"|"+processTime+"|",(int)processTime);
				
					/*if(requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID).trim().equals("180000"))
						transType = "RECHARGE";
					else if(requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID).trim().equals("200000"))
						transType = "REVERSAL";
					else if(requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID).trim().equals("660000"))
						transType = "STATUS_INQUIRY";
					
					
					if(responseISOMsg.getString(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID).trim().equals("00"))
						transStatus = "SUCCESS";
					else
						transStatus = "FAILURE";
			*/
        	/*AppMonitorWrapper.logEvent("POSGW_SERVICE_RESPONSE",
				  	"|"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER).substring(26).trim()
				   +"|"+transType
				   +"|"+transStatus
				   +"|"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1).trim()
				   +"|"+carrierId
				   +"|"+startDateTime1
				   +"|"+endDateTime1
				   +"|"+processTime1+"|",(int)processTime1);
			*/	   
			}
		
		}
	 //return true;
	}
	
	private void dispatchResponse(ISOSource source, ISOMsg reqIsoMsg)

	{
	     System.out.println("dispatchResponse......");
		if (source != null && reqIsoMsg != null)

		{
			String reqMessageTypeID = "";
			String processingCodeID = "";
			String transactionID = "";
			boolean isSendSuccess = false;
			ISOMsg respIsoMsg = null;
			try
			{
					reqMessageTypeID = reqIsoMsg.getMTI();
					processingCodeID = reqIsoMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
					transactionID = reqIsoMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_ID);
					respIsoMsg = packResponseMessage(reqIsoMsg);
	}
	catch (ISOException exp)
	{
				logger.error("dispatchResponse(): ISOException"+exp.getMessage());
	}

		if (respIsoMsg != null)
		{
		try

		{

			System.out.println("dispatchResponse()... - transactionID: "+transactionID);



					source.send(respIsoMsg);

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

		}


	}

	else

	{

	logger.error("Unable to send RESPONSE MESG. (Reason: ISOSource is null or ISOMsg is null)");

	}



	}


		private ISOMsg packResponseMessage(ISOMsg reqIsoMsg)
		{
			String reqMessageTypeID = "";
			String processingCodeID = "";
			String transactionID = "";

			try
			{
	            System.out.println("packResponseMessage......");
				reqMessageTypeID = reqIsoMsg.getMTI();
				processingCodeID = reqIsoMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);

				reqIsoMsg.setResponseMTI();
				reqIsoMsg.unset(IRPISOMessageFieldIDConstant.TRANSACTION_ID);
				reqIsoMsg.recalcBitMap();
				         if (processingCodeID != null && processingCodeID.length() > 0)
	            {
					 IRPMessageProcessor messageProcesor = getMessageProcessor(reqMessageTypeID,processingCodeID);
				     if (messageProcesor != null)
				     {
				    	 reqIsoMsg = messageProcesor.packResponseMessage(reqIsoMsg);
				     }
	            }
	            else{
	            	
	            	if (reqMessageTypeID.equals(IRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID)) {
	            		reqIsoMsg = packRechargeResponseMessage(reqIsoMsg);
	            	}
	            	else if (reqMessageTypeID.equals(IRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID)) {
	            		reqIsoMsg = packReversalResponseMessage(reqIsoMsg);
	            	}
	            	else if (reqMessageTypeID.equals(IRPMessageTypeIDConstant.TRANSACTION_STATUS_INQUIRY_MSG_TYPE_ID)) {
	            		reqIsoMsg = packTransactionStatusInqResponseMessage(reqIsoMsg);
	            	}
	            }
				byte[] messagebytes = reqIsoMsg.pack();
				logger.error("RESPONSE MESG - TO CLIENT in HEX "+ISOUtil.hexString(messagebytes));
				   
			}
			catch (ISOException exp)
			{
				logger.error("dispatchResponse(): ISOException"+exp.getMessage());
			}
			return reqIsoMsg;
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
			    String processingCodeID = requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
				logger.info("isSupportedMesageType(): mti -"+mti);
				logger.info("isSupportedMesageType(): processingCodeID -"+processingCodeID);

				String status = IRPMessageTypeValidator.isValidMessageTypeId(mti);
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
			    String processingCodeID = requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
				logger.info("isSupportedMesageType(): mti -"+mti);
				logger.info("isSupportedMesageType(): processingCodeID -"+processingCodeID);

				String status = IRPMessageTypeValidator.isValidProcessingCode(mti,processingCodeID);
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

		private IRPMessageProcessor getMessageProcessor(String messageTypeID,String processingCodeID)
		{
			     IRPMessageProcessor messageProcesor = null;
			     String processorID = "";

			     //call appropriate Message Processor based on MessageTypes and Processing code
		    	 if (!messageTypeID.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)){
			       processorID = messageTypeID+"_"+processingCodeID;
		    	 }
		    	 else{
		    		processorID= messageTypeID;
		    	 }
		    	 logger.error("processorID: "+processorID);

		    	 messageProcesor = IRCommandMapper.getInstance(messageFormat).getMessageProcessor(processorID);
			     if (messageProcesor == null)
			     {
			    	logger.error("Unable to find Message Processor for ProcessorID: "+processorID);
			     }

			     return messageProcesor;
		}

		private void updateSendStatus(String messageTypeID,
				                      String processingCodeID,
				                      String transactionID,
				                      String status)
		{
			if(transactionID != null && transactionID.length() > 0){

			 IRPMessageProcessor messageProcesor = getMessageProcessor(messageTypeID,processingCodeID);

		     if (messageProcesor != null)
		     {
		        messageProcesor.updateSendMsgStatus(transactionID,status);
		     }
			}
		}
		
		public ISOMsg packRechargeResponseMessage(ISOMsg requestISOMsg){
			ISOMsg respISOMsg = null;
			try
			{

				respISOMsg = requestISOMsg;

				if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID,PlaceHolderDataConstant.PROCESSING_CODE_ID);
				}
				
				if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT,PlaceHolderDataConstant.TRANSACTION_AMNT);
				}
				
	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT)))
	  			{
	            	System.out.println("Inside DEST_TRANSACTION_AMNT..... ");
	                respISOMsg.set(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT,PlaceHolderDataConstant.DEST_TRANSACTION_AMNT);
	            }

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME,PlaceHolderDataConstant.TRANSACTION_DATE_TIME);
				}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER,PlaceHolderDataConstant.SYSTEM_TRACE_AUDIT_NUMBER);
	     		}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CAPTURE_DATE))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.CAPTURE_DATE,PlaceHolderDataConstant.CAPTURE_DATE);
	     		}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.POS_ENTRY_MODE))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.POS_ENTRY_MODE,PlaceHolderDataConstant.POS_ENTRY_MODE);
	     		}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.MODALITY))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.MODALITY,PlaceHolderDataConstant.MODALITY);
	     		}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID))){
	              	respISOMsg.set(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID,PlaceHolderDataConstant.DISTRIBUTOR_ID);

				}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1,PlaceHolderDataConstant.RETRIEVAL_REFERENCE_NUMBER_1);
	     		}

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID,PlaceHolderDataConstant.STORE_MANAGER_ID);
	    		}

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBAGENT_ID))){
	           	respISOMsg.set(IRPISOMessageFieldIDConstant.SUBAGENT_ID,PlaceHolderDataConstant.SUBAGENT_ID);
	   		    }

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER))){
	              	respISOMsg.set(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER,PlaceHolderDataConstant.SUBSSCRIBER_NUMBER);
	      		}

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CARD_ACCEPTOR_NAME))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.CARD_ACCEPTOR_NAME,PlaceHolderDataConstant.CARD_ACCEPTOR_NAME);
	     		}

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CURRENCY_CODE))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.CURRENCY_CODE,PlaceHolderDataConstant.CURRENCY_CODE);
	    		}

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CONVERSION_CURRENCY_CODE))){
	           	respISOMsg.set(IRPISOMessageFieldIDConstant.CONVERSION_CURRENCY_CODE,PlaceHolderDataConstant.CONVERSION_CURRENCY_CODE);
	   		   }

				byte[] messagebytes = respISOMsg.pack();
				logger.error("RESPONSE MESG - TO CLIENT in HEX "+ISOUtil.hexString(messagebytes));


			}
			catch (ISOException exp)
			{
				logger.error("packResponseMessage(): ISOException"+exp.getMessage());
			}
			return respISOMsg;
		}
		
		public ISOMsg packReversalResponseMessage(ISOMsg requestISOMsg){
			ISOMsg respISOMsg = null;
			try
			{

				
				respISOMsg = requestISOMsg;

				if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID,PlaceHolderDataConstant.PROCESSING_CODE_ID);
				}
				
				if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT,PlaceHolderDataConstant.TRANSACTION_AMNT);
				}
				if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT))){
	                        respISOMsg.set(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT,PlaceHolderDataConstant.DEST_TRANSACTION_AMNT);
	            }


	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME,PlaceHolderDataConstant.TRANSACTION_DATE_TIME);
				}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER,PlaceHolderDataConstant.SYSTEM_TRACE_AUDIT_NUMBER);
	     		}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CAPTURE_DATE))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.CAPTURE_DATE,PlaceHolderDataConstant.CAPTURE_DATE);
	     		}


	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.MODALITY))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.MODALITY,PlaceHolderDataConstant.MODALITY);
	     		}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID))){
	              	respISOMsg.set(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID,PlaceHolderDataConstant.DISTRIBUTOR_ID);

				}

	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1,PlaceHolderDataConstant.RETRIEVAL_REFERENCE_NUMBER_1);
	     		}

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID,PlaceHolderDataConstant.STORE_MANAGER_ID);
	    		}

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBAGENT_ID))){
	           	respISOMsg.set(IRPISOMessageFieldIDConstant.SUBAGENT_ID,PlaceHolderDataConstant.SUBAGENT_ID);
	   		    }

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER))){
	              	respISOMsg.set(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER,PlaceHolderDataConstant.SUBSSCRIBER_NUMBER);
	      		}


	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CURRENCY_CODE))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.CURRENCY_CODE,PlaceHolderDataConstant.CURRENCY_CODE);
	    		}

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CONVERSION_CURRENCY_CODE))){
	           	respISOMsg.set(IRPISOMessageFieldIDConstant.CONVERSION_CURRENCY_CODE,PlaceHolderDataConstant.CONVERSION_CURRENCY_CODE);
	   		   }

	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.RECHARGE_REFERENCE_NUMBER))){
	              	respISOMsg.set(IRPISOMessageFieldIDConstant.RECHARGE_REFERENCE_NUMBER,PlaceHolderDataConstant.RECHARGE_REFERENCE_NUMBER);
	      		}

				byte[] messagebytes = respISOMsg.pack();
				logger.error("RESPONSE MESG - TO CLIENT in HEX "+ISOUtil.hexString(messagebytes));


			}
			catch (ISOException exp)
			{
				logger.error("packResponseMessage(): ISOException"+exp.getMessage());
			}
			return respISOMsg;
		}
		
		public ISOMsg packTransactionStatusInqResponseMessage(ISOMsg requestISOMsg){
			ISOMsg respISOMsg = null;
			try
			{
				
				
				respISOMsg = requestISOMsg;
						
				if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID,PlaceHolderDataConstant.PROCESSING_CODE_ID);
				}
				
				if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT,PlaceHolderDataConstant.TRANSACTION_AMNT);
				}
				
	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME,PlaceHolderDataConstant.TRANSACTION_DATE_TIME);
				}
	            
	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER,PlaceHolderDataConstant.SYSTEM_TRACE_AUDIT_NUMBER);
	     		}
	            
	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CAPTURE_DATE))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.CAPTURE_DATE,PlaceHolderDataConstant.CAPTURE_DATE);
	     		}
	            
	                       
	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.MODALITY))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.MODALITY,PlaceHolderDataConstant.MODALITY);
	     		}
				
	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID))){
	              	respISOMsg.set(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID,PlaceHolderDataConstant.DISTRIBUTOR_ID);
	                	
				}
	            
	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1,PlaceHolderDataConstant.RETRIEVAL_REFERENCE_NUMBER_1);
	     		}
	            
	            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.RECHARGE_TRANSACTION_RESPONSE_CODE_ID))){
	             	respISOMsg.set(IRPISOMessageFieldIDConstant.RECHARGE_TRANSACTION_RESPONSE_CODE_ID,PlaceHolderDataConstant.RECHARGE_TRANSACTION_RESPONSE_CODE_ID);
	     		}
	          
	           
	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID,PlaceHolderDataConstant.STORE_MANAGER_ID);
	    		}
	           
	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBAGENT_ID))){
	           	respISOMsg.set(IRPISOMessageFieldIDConstant.SUBAGENT_ID,PlaceHolderDataConstant.SUBAGENT_ID);
	   		    }
	           
	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER))){
	              	respISOMsg.set(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER,PlaceHolderDataConstant.SUBSSCRIBER_NUMBER);
	      		}
	                      			
	           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.CURRENCY_CODE))){
	            	respISOMsg.set(IRPISOMessageFieldIDConstant.CURRENCY_CODE,PlaceHolderDataConstant.CURRENCY_CODE);
	    		}
	                    			
				byte[] messagebytes = respISOMsg.pack();
				logger.error("RESPONSE MESG - TO CLIENT in HEX "+ISOUtil.hexString(messagebytes));
				
				
			}
			catch (ISOException exp)
			{
				logger.error("packResponseMessage(): ISOException"+exp.getMessage());
			}
			return respISOMsg;
		}

}
