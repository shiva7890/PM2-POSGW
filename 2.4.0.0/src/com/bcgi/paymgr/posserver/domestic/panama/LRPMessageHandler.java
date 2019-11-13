package com.bcgi.paymgr.posserver.domestic.panama;

import java.io.IOException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOUtil;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.DMCommandMapper;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountReversalDTO;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPProcessorConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.POSGW_InternalResponseConstant;
import com.bcgi.paymgr.posserver.domestic.panama.processor.LRPReversalMINProcessor;
import com.bcgi.paymgr.posserver.domestic.panama.validator.LRPMessageTypeValidator;
import com.bcgi.paymgr.posserver.fw.processor.DMMessageProcessor;

public class LRPMessageHandler implements Runnable
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRPMessageHandler.class);
	public ISOSource source;
	public ISOMsg requestISOMsg;
	private int messageFormat = 0;
	
	public LRPMessageHandler (ISOSource source, ISOMsg isomessage,int msgFormat){
		super();
		this.source = source;
		this.requestISOMsg = isomessage;
		this.messageFormat = msgFormat;
	}
	
	public void run() {
		ISOMsg responseISOMsg = requestISOMsg;
		try {
			handleMessageHeader(requestISOMsg);
			printRequestMessageToBytes(requestISOMsg);
			
			String status = isSupportedMesageType(requestISOMsg);
			
			if (status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
				status = isSupportedProcessingCode(requestISOMsg);

				logger.info("status====>" + status);

				if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)) {
					String messageTypeID = requestISOMsg.getMTI();
					String processingCodeID = requestISOMsg.getString(LRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
					DMMessageProcessor messageProcesor = getMessageProcessor(messageTypeID,processingCodeID);
					if (messageProcesor != null) {
						responseISOMsg = messageProcesor.execute(requestISOMsg,null);
					}
					boolean isResultReturned = dispatchResponse(source,responseISOMsg);
					/**
					 * code is used to call auto reversal when recharge response is not received by the 
					 * client program.
					 */
					if (!isResultReturned) {
						logger.info("Response MTI : "+responseISOMsg.getMTI());
						//cheking whether the request respone belogns to 
						if ("0210".equals(responseISOMsg.getMTI())) {
							String result = responseISOMsg.getString(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID);
							
							if (ResponseCodeConstant.SUCCESS.equals(result)) {
								String rechargeTransId = responseISOMsg.getString(LRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
								responseISOMsg.setMTI(LRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID);
								
								StringBuffer field90 = new StringBuffer();
								field90.append(LRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID);// REchargeMTI
								field90.append(responseISOMsg.getString(LRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER)); // 37 -Field
								field90.append(responseISOMsg.getString(LRPISOMessageFieldIDConstant.LOCAL_TRANSACTION_DATE)); //13 - Field
								field90.append(responseISOMsg.getString(LRPISOMessageFieldIDConstant.LOCAL_TRANSACTION_TIME)); //12 -Field
								field90.append("00");
								field90.append(responseISOMsg.getString(LRPISOMessageFieldIDConstant.CAPTURE_DATE)); //17 - Field
								field90.append(LRPMessageTypeIDConstant.FIELD_90_FILLER);
								responseISOMsg.set(LRPISOMessageFieldIDConstant.ORIGINAL_DATA, field90.toString());
								
								messageProcesor = new LRPReversalMINProcessor();
								logger.info(rechargeTransId+" : Try to call auto reversal ");
								messageProcesor.execute(responseISOMsg,new POSGWAccountReversalDTO());
								
								if(ResponseCodeConstant.SUCCESS.equals(responseISOMsg.getString(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID)))
									 logger.info(rechargeTransId+"Succesfully Auto Reversed "+responseISOMsg.getString(LRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER));
								else
									 logger.info(rechargeTransId+"Failed to auto Reversed "+responseISOMsg.getString(LRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER));
							}else{
								logger.info(responseISOMsg.getString(LRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER)+" : Ignore auto reversal because tran was failed due to "+result);
							}
						}else{
							logger.info("auto reversal is not calling becoz given reqeust is not belong to recharge service  : "+responseISOMsg.getMTI());
						}
					}
				}else{
					responseISOMsg.set(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.INVALID_PROCESSING_CODE));
					dispatchResponse(source, responseISOMsg); 
				}
			}else{
    		    	 responseISOMsg.set(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT));
		    		 responseISOMsg.set(LRPISOMessageFieldIDConstant.MESSAGE_TYPE_ID,LRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID);
		    		 dispatchResponse(source, responseISOMsg);
			}
		}catch (ISOException isoexp){
			logger.error("RESPONSE MESG - TO CLIENT(Exception in Request processing)"+isoexp.getMessage());
			try{
				responseISOMsg.set (new ISOField (LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR));
				dispatchResponse(source, responseISOMsg);
			}catch(Exception exp){
				exp.printStackTrace();
				logger.error("process() Exception is in catch block of Exception:"+exp.toString());
			}
			//return true;
		}catch (Exception exp){
			logger.error("process() Exception :"+exp.toString());
		}
		//return true;
	}
	
	private boolean dispatchResponse(ISOSource source, ISOMsg reqIsoMsg) {
		boolean isResultReturned = false;
		
		if (source != null && reqIsoMsg != null){
			if (reqIsoMsg != null){
				try{
					String m_responseId= reqIsoMsg.getString(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID);
					if(m_responseId!=null && m_responseId.equalsIgnoreCase(PM_DMPOSGW_MapConstant.getPOSGWResponse("0"))){
						reqIsoMsg.setResponseMTI();
					}else{
						logger.info("The Initial MTI "+reqIsoMsg.getMTI());
						if(reqIsoMsg.getMTI()!=null && reqIsoMsg.getMTI().equalsIgnoreCase(LRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)){
							reqIsoMsg.setMTI(LRPMessageTypeIDConstant.FAILURE_ECHO_MSG_TYPE_ID);							
						}else if(reqIsoMsg.getMTI()!=null && reqIsoMsg.getMTI().equalsIgnoreCase(LRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID)){
							reqIsoMsg.setMTI(LRPMessageTypeIDConstant.FAILURE_RECHARGE_MSG_TYPE_ID);
						}else if(reqIsoMsg.getMTI()!=null && reqIsoMsg.getMTI().equalsIgnoreCase(LRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID)){
							reqIsoMsg.setMTI(LRPMessageTypeIDConstant.FAILURE_REVERSAL_MSG_TYPE_ID);
						}else if(reqIsoMsg.getMTI()!=null && reqIsoMsg.getMTI().equalsIgnoreCase(LRPMessageTypeIDConstant.REVERSAL_RETRY_MSG_TYPE_ID)){
							reqIsoMsg.setMTI(LRPMessageTypeIDConstant.FAILURE_REVERSAL_MSG_TYPE_ID);
						}
					}
					byte[] b1 = reqIsoMsg.pack();
					logger.info("RETURN SERVER LOGGED MESSAGE"+ISOUtil.hexString(b1));
					logger.info("String "+(new String(b1)));
					logger.info("before return result to client");
					source.send(reqIsoMsg);
					isResultReturned = true;
				}catch (IOException ioexp){
	    			logger.error("dispatchResponse(): IOException"+ioexp.getMessage());
				}catch(Exception exception){
					logger.error("Unable to return result to client because client application may be timedout or down"+exception);
				}
			}
		}else{
			logger.error("Unable to send RESPONSE MESG. (Reason: ISOSource is null or ISOMsg is null)");
		}
		return isResultReturned;
	}
	
	
	private void handleMessageHeader(ISOMsg requestISOMsg) {
		byte[] msgheaderb = requestISOMsg.getHeader();
		
		if (msgheaderb == null){
			logger.error("REQ MESG Header - FROM CLIENT: Empty");
			
		}else{
			logger.error("REQ MESG Header - FROM CLIENT: in HexDecimal "+ISOUtil.hexString(msgheaderb));
			logger.error("REQ MESG Header - FROM CLIENT: in String "+(new String(msgheaderb)));
		}
	}
	
	private void printRequestMessageToBytes(ISOMsg requestISOMsg){
		try{
			byte[] messagebytes = requestISOMsg.pack();
		    logger.info("REQ MESG - FROM CLIENT in String......"+(new String(messagebytes)));
			logger.error("REQ MESG - FROM CLIENT in HEX: "+ISOUtil.hexString(messagebytes));
		}catch (ISOException exp){
			logger.error("printRequestMessageToBytes(): ISOException"+exp.getMessage());
		}catch(Exception exception){
			logger.error("Got an exception while printing a request"+exception);
		}
	}
	
	/**
	 *
	 * @param requestISOMsg
	 * @return
	 */
	private String isSupportedMesageType(ISOMsg requestISOMsg) {
		try {
			String mti = requestISOMsg.getMTI();
			String processingCodeID = requestISOMsg.getString(LRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			logger.info("isSupportedMesageType(): mti -"+mti);
			logger.info("isSupportedMesageType(): processingCodeID -"+processingCodeID);
			
			String status = LRPMessageTypeValidator.isValidMessageTypeId(mti);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
		}catch (ISOException exp){
			logger.error("isSupportedMesageType(): ISOException"+exp.getMessage());
			return ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
		}
		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	private String isSupportedProcessingCode(ISOMsg requestISOMsg) {
		try {
			String mti = requestISOMsg.getMTI();
			String processingCodeID = requestISOMsg.getString(LRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			logger.info("isSupportedProcessingCode(): mti -"+mti);
			logger.info("isSupportedProcessingCode(): processingCodeID -"+processingCodeID);
			
			String status = LRPMessageTypeValidator.isValidProcessingCode(mti,processingCodeID);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
		}catch (ISOException exp){
			logger.error("isSupportedProcessingCode(): ISOException"+exp.getMessage());
			return ResponseCodeConstant.INVALID_PROCESSING_CODE;
		}
		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}	
	
	
	private DMMessageProcessor getMessageProcessor(String messageTypeID,String processingID){
		DMMessageProcessor messageProcesor = null;
		String processorID = "";
		
		if(!messageTypeID.equals(LRPProcessorConstant.LRP_ECHO_PROCESSOR_ID))
			processorID = messageTypeID+"_"+processingID;
		else
			processorID = messageTypeID;
		
		/*
		 * Invoking Echo service either proceeing code mentioned or empty
		 */
		if(processingID==null || processingID.length()==0){
			if("0800".equals(messageTypeID))
				processorID = "0800_990000";
		}
		
		logger.error("processorID: "+processorID);
		
		messageProcesor = DMCommandMapper.getInstance(messageFormat).getMessageProcessor(processorID);
		if (messageProcesor == null){
			logger.error("Unable to find Message Processor for ProcessorID: "+processorID);
		}
		
		return messageProcesor;
	}
	
			
}
