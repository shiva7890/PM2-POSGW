package com.bcgi.paymgr.posserver.domestic.columbia;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOUtil;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.DMCommandMapper;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCProcessorConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.POSGW_InternalResponseConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.processor.LRCRechargeMINProcessor;
import com.bcgi.paymgr.posserver.domestic.columbia.processor.LRCReversalMINProcessor;
import com.bcgi.paymgr.posserver.domestic.columbia.validator.LRCMessageTypeValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountReversalDTO;
import com.bcgi.paymgr.posserver.fw.processor.DMMessageProcessor;

public class LRCMessageHandler implements Runnable
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRCMessageHandler.class);
	public ISOSource source;
	public ISOMsg requestISOMsg;
	private int messageFormat = 0;
	
	public LRCMessageHandler (ISOSource source, ISOMsg isomessage,int msgFormat){
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
			String connectionId=null;
			
			if (status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
				 status = isSupportedProcessingCode(requestISOMsg);
				  logger.info("status====>"+status);

				  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				            String messageTypeID = requestISOMsg.getMTI();
					    String processingCodeID = requestISOMsg.getString(LRCISOMessageFieldIDConstant.PROCESSING_CODE_ID);
						DMMessageProcessor messageProcesor = getMessageProcessor(messageTypeID,processingCodeID);
						logger.info("messageProcesor:::::::RAJJJJJ::::::: "+messageProcesor);
				if (messageProcesor != null) {
					
					
					responseISOMsg = messageProcesor.execute(requestISOMsg,null);
				}
				boolean isResultReturned = dispatchResponse(source,responseISOMsg);
				/**
				 * code is used to call auto reversal when recharge response is
				 * not received by the client program.
				 */
					 if (!isResultReturned) {
					logger.info("Response MTI : "+responseISOMsg.getMTI());
						 String result = responseISOMsg.getString(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID);						 
						//calling auto reversal only auto reversal flag is enable 
						//and iso response should be success Implemented by Sridhar.V on 06-Jan-2012 to full
						//fill the Clients new Requirement.
						 if("Y".equals(DataUtil.getAutoReversalFlag())){						 
					//cheking whether the request respone belogns to 
					if ("0210".equals(responseISOMsg.getMTI())) {
						if (ResponseCodeConstant.SUCCESS.equals(result)) {
							
							connectionId=((LRCRechargeMINProcessor)messageProcesor).getConnectionId();
							logger.info("ConnectionId====>"+connectionId);
							if(DataUtil.isAutoReversalSupported(connectionId))//newly added code
							{
							String rechargeTransId = responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
							responseISOMsg.setMTI("0400");
							messageProcesor = new LRCReversalMINProcessor();
							logger.info(rechargeTransId+" : Try to call auto reversal ");
							messageProcesor.execute(responseISOMsg,new POSGWAccountReversalDTO());
							
							if(ResponseCodeConstant.SUCCESS.equals(responseISOMsg.getString(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID)))
								 logger.info(rechargeTransId+"Succesfully Auto Reversed "+responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER));
							else
								 logger.info(rechargeTransId+"Failed to auto Reversed "+responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER));
							}else
								logger.info("AutoReversal is not supported for the ConnectionId  :: "+connectionId);

						} else {
							logger.info(responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER)+" : Ignore auto reversal because tran was failed due to "+result);
						}
					} else {
						logger.info("auto reversal is not calling becoz given reqeust is not belong to recharge service  : "+responseISOMsg.getMTI());
					}
						 }else{
							 logger.info("Auto Reversal is not calling becoz the DOMESTIC_AUTO_REVERSAL_FLAG is Set to ==>"+DataUtil.getAutoReversalFlag());
						 }
					 }
			  } else{
					     responseISOMsg.set(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID,POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.INVALID_PROCESSING_CODE));
				  dispatchResponse(source, responseISOMsg); }
			} else {
    		    	 responseISOMsg.set(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID,POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT));
		    		 responseISOMsg.set(LRCISOMessageFieldIDConstant.MESSAGE_TYPE_ID,LRCMessageTypeIDConstant.ECHO_MSG_TYPE_ID);
		    		 dispatchResponse(source, responseISOMsg);
			}

		} catch (ISOException isoexp) {
			logger.error("RESPONSE MESG - TO CLIENT(Exception in Request processing)"+isoexp.getMessage());
			try{
				responseISOMsg.set (new ISOField (LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR));
				dispatchResponse(source, responseISOMsg);
				
			} catch (Exception exp) {
				exp.printStackTrace();
				logger.error("process() Exception is in catch block of Exception:"+exp.toString());
			}
			//return true;
		} catch (Exception exp) {
			logger.error("process() Exception :"+exp.toString());
		}
		//return true;
	}
	
	private boolean dispatchResponse(ISOSource source, ISOMsg reqIsoMsg)
	
	{
		boolean isResultReturned = false;
		
		//System.out.println("dispatchResponse......");
		if (source != null && reqIsoMsg != null)
    	{
			//System.out.println("dispatchResponse..if (source != null && reqIsoMsg != null)");
			
			if (reqIsoMsg != null)
			{
				try
				
				{
					reqIsoMsg.setResponseMTI();
					reqIsoMsg.set(LRCISOMessageFieldIDConstant.TRANSACTION_DATE_TIME,DataUtil.convertToValidDateTime(null));
					byte[] b1 = reqIsoMsg.pack();
					logger.info("RETURN SERVER LOGGED MESSAGE"+ISOUtil.hexString(b1));
					logger.info("String "+(new String(b1)));
					logger.info("before return result to client");
					source.send(reqIsoMsg);
					isResultReturned = true;
					
				}
				
				/*catch (ISOException exp)
				
				{
					logger.error("dispatchResponse(): ISOException"+exp.getMessage());
					
				}*/
				
				catch (IOException ioexp)
				
				{
	    			logger.error("dispatchResponse(): IOException"+ioexp.getMessage());
					
				}
				catch(Exception exception)
				{
					logger.error("Unable to return result to client because client application may be timedout or down"+exception);
				}
				
			}
			
			
		}
		
		else
			
		{
			//System.out.println("dispatchResponse..else");
			logger.error("Unable to send RESPONSE MESG. (Reason: ISOSource is null or ISOMsg is null)");
		}
		
		
		return isResultReturned;
	}
	
	
	
	
	private void handleMessageHeader(ISOMsg requestISOMsg) {
		byte[] msgheaderb = requestISOMsg.getHeader();
		
		if (msgheaderb == null){
			logger.error("REQ MESG Header - FROM CLIENT: Empty");
			
		}
		else{
			logger.error("REQ MESG Header - FROM CLIENT: in HexDecimal "+ISOUtil.hexString(msgheaderb));
			logger.error("REQ MESG Header - FROM CLIENT: in String "+(new String(msgheaderb)));
		}
	}
	
	private void printRequestMessageToBytes(ISOMsg requestISOMsg){
		try
		{
			byte[] messagebytes = requestISOMsg.pack();
		    logger.info("REQ MESG - FROM CLIENT in String......"+(new String(messagebytes)));
			logger.error("REQ MESG - FROM CLIENT in HEX: "+ISOUtil.hexString(messagebytes));
		}
		catch (ISOException exp)
		{
			logger.error("printRequestMessageToBytes(): ISOException"+exp.getMessage());
		}
		catch(Exception exception)
		{
			logger.error("Got an exception while printing a request"+exception);
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
			String processingCodeID = requestISOMsg.getString(LRCISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			logger.info("isSupportedMesageType(): mti -"+mti);
			logger.info("isSupportedMesageType(): processingCodeID -"+processingCodeID);
			
			String status = LRCMessageTypeValidator.isValidMessageTypeId(mti);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			
		}
		catch (ISOException exp)
		{
			logger.error("isSupportedMesageType(): ISOException"+exp.getMessage());
			return ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
		}
		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
		
	}
	
	private String isSupportedProcessingCode(ISOMsg requestISOMsg)
	{
		
		try
		{
			String mti = requestISOMsg.getMTI();
			String processingCodeID = requestISOMsg.getString(LRCISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			logger.info("isSupportedProcessingCode(): mti -"+mti);
			logger.info("isSupportedProcessingCode(): processingCodeID -"+processingCodeID);
			
			String status = LRCMessageTypeValidator.isValidProcessingCode(mti,processingCodeID);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
		}
		catch (ISOException exp)
		{
			logger.error("isSupportedProcessingCode(): ISOException"+exp.getMessage());
			return ResponseCodeConstant.INVALID_PROCESSING_CODE;
		}
		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
		
	}	
	
	
	private DMMessageProcessor getMessageProcessor(String messageTypeID,String processingID)
	{
		DMMessageProcessor messageProcesor = null;
		/*The Following lines of code modified (Commented and Added) By Sridhar.Vemulapalli on 04-Oct-2011
		 * Because to Eliminate the processing code validation 
		 * */		
		/*String processorID = "";
		
		if(!messageTypeID.equals(LRCProcessorConstant.LR_ECHO_PROCESSOR_ID))
			processorID = messageTypeID+"_"+processingID;
		else
			processorID = messageTypeID;*/
		
		/*
		 * Invoking Echo service either proceeing code mentioned or empty
		 */
		/*if(processingID==null || processingID.length()==0)
		{
			if("0800".equals(messageTypeID))
				processorID = "0800_990000";
		}*/
		//Ends
		logger.error("processorID::::: "+processingID);
		if(LRCProcessorConstant.LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_ID.equalsIgnoreCase(messageTypeID+"_"+processingID) ||LRCProcessorConstant.LR_BRAND_CPN_REDEEMPTION_PROCESSOR_ID.equalsIgnoreCase(messageTypeID+"_"+processingID)){
		if(processingID!=null &&processingID.length()>0)
			messageTypeID=messageTypeID+"_"+processingID;
	}
		logger.error("processorID: "+messageTypeID);
		
		messageProcesor = DMCommandMapper.getInstance(messageFormat).getMessageProcessor(messageTypeID);
		if (messageProcesor == null)
		{
			logger.error("Unable to find Message Processor for ProcessorID: "+messageTypeID);
		}
		
		return messageProcesor;
	}
	static{
		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.CONFIG_FILE_NAME;
		Properties properties = new Properties();
		try {			
			properties.load(new FileInputStream(configfilePath));
			/*Below Code is read and assign values from properties files which is 
			 * used to specify the Character to Suppress from the 41 and 42 Fields
			 * And to specify the weather we need to start suppress/Not from the 41 and 42 Fields
			 * Changed and Added by Sridhar.Vemulapalli on 23-Nov-2011 to full fill the REQ 1083 
			 */
			LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_FLAG=properties.getProperty("COLUMBIA_FIELD_SUPRESS_FLAG");
			LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR=properties.getProperty("COLUMBIA_FIELD_SUPRESS_CHAR");
		}catch(Exception ee){
			//Distributor_Id_ATH= "025";					
			logger.error("Unable to supressing of zero flag in the configuration file");
		}			
	}
}
