package com.bcgi.paymgr.posserver.domestic.ecuador;

import java.io.IOException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOUtil;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.DMCommandMapper;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.PropertiesReader;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountReversalDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMProcessorConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMASyncReversalMINProcessor;
import com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMReversalMINProcessor;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMMessageTypeValidator;
import com.bcgi.paymgr.posserver.fw.controller.MessageController;
import com.bcgi.paymgr.posserver.fw.processor.DMMessageProcessor;

public class DMMessageHandler implements Runnable
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(MessageController.class);
	public ISOSource source;
	public ISOMsg requestISOMsg;
	private int messageFormat = 0;
	
	public DMMessageHandler (ISOSource source, ISOMsg isomessage,int msgFormat){
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
			
			if (status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
			{
				 status = isSupportedProcessingCode(requestISOMsg);

				  logger.info("status====>"+status);

				  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
				  {
				String messageTypeID = requestISOMsg.getMTI();
						String processingCodeID = requestISOMsg.getString(DMISOMessageFieldIDConstant.PROCESSING_CODE_ID);
						
						//DMMessageProcessor messageProcesor = getMessageProcessor(messageTypeID,processingCodeID);
						
						DMMessageProcessor messageProcesor =(DMMessageProcessor)DataUtil.getRequiredObject(PropertiesReader.getValue(messageTypeID+"_"+processingCodeID)) ;
				if (messageProcesor != null)
				{
					responseISOMsg = messageProcesor.execute(requestISOMsg,null);
				}
				logger.info("Before dispatch Response.......");
					boolean isResultReturned = dispatchResponse(source,responseISOMsg);
					
					/**
					 * code is used to call auto reversal when recharge response is not received by the 
					 * client program.
					 * Phani G on 26-May-2010;
					 * Reason: Auto reversal functionality for costa rica
					 */
					if(!isResultReturned )
					{
						String result = responseISOMsg.getString(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID);
						//calling auto reversal only auto reversal flag is enable 
						//and iso response should be success
						if("Y".equals(DataUtil.getAutoReversalFlag()) && ResponseCodeConstant.SUCCESS.equals(result))
						{
							//domestic sync recahrge[it trys to call domestic sync reversal]
							if("0210".equals(responseISOMsg.getMTI()) && DMMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID.equals(requestISOMsg.getString("3")))
							{
								
								String rechargeTransId = responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
								responseISOMsg.setMTI(DMMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID);
								messageProcesor = new DMReversalMINProcessor();
								logger.info(rechargeTransId+" : Try to call auto reversal ");
								responseISOMsg = messageProcesor.execute(responseISOMsg,new POSGWAccountReversalDTO());
								
								if(ResponseCodeConstant.SUCCESS.equals(responseISOMsg.getString(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID)))
									 logger.info(rechargeTransId+"Succesfully Auto Reversed "+responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER));
								else
									 logger.info(rechargeTransId+"Failed to auto Reversed "+responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER));
								
							}
							//domestic async recahrge or unified recharge [it trys to call async domestic reversal]
							/*commented because no need to call for auto reversal for async trans
							 * 
							 * else if("0210".equals(responseISOMsg.getMTI()) && 
									 (DMMessageTypeIDConstant.ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID.equals(requestISOMsg.getString("3")) ||
									  DMMessageTypeIDConstant.UNIFIED_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID.equals(requestISOMsg.getString("3") )
									 ))
									 
							{
								
								String rechargeTransId = responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
								responseISOMsg.setMTI(DMMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID);
								responseISOMsg.set("3",DMMessageTypeIDConstant.ASYNC_REVERSAL_PROCESSING_CODE_ID);
								messageProcesor = new DMASyncReversalMINProcessor();
								logger.info(rechargeTransId+" : Try to call auto reversal ");
								responseISOMsg = messageProcesor.execute(responseISOMsg,new POSGWAccountReversalDTO());
								
								if(ResponseCodeConstant.SUCCESS.equals(responseISOMsg.getString(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID)))
									 logger.info(rechargeTransId+"Succesfully Auto Reversed async "+responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER));
								else
									 logger.info(rechargeTransId+"Failed to auto Reversed  async "+responseISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER));
								
							}*/
							else
							{
								logger.info("auto reversal is not calling becoz given reqeust is not belongs to recharge");
							}
							
						}
						else
						{
							logger.info("auto reversal is not calling becoz given reqeust is faild  : "+responseISOMsg.getMTI());
						}
					}
				
				  }
				  else{
					     responseISOMsg.set(DMISOMessageFieldIDConstant.RESPONSE_CODE_ID,ResponseCodeConstant.INVALID_PROCESSING_CODE);
			    		 dispatchResponse(source, responseISOMsg); 
			}
			}
			else
			{
     		    	 responseISOMsg.set(DMISOMessageFieldIDConstant.RESPONSE_CODE_ID,ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT);
		    		 responseISOMsg.set(DMISOMessageFieldIDConstant.MESSAGE_TYPE_ID,DMMessageTypeIDConstant.ECHO_MSG_TYPE_ID);
		    		 dispatchResponse(source, responseISOMsg);
			}

			
			
			
		}
		catch (ISOException isoexp)
		{
			logger.error("RESPONSE MESG - TO CLIENT(Exception in Request processing)"+isoexp.getMessage());
			try{
				
				responseISOMsg.set (new ISOField (DMISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR));
				dispatchResponse(source, responseISOMsg);
				
			}
			catch(Exception exp)
			{
				exp.printStackTrace();
				logger.error("process() Exception is in catch block of Exception:"+exp.toString());
			}
			//return true;
		}
		catch (Exception exp)
		{
			logger.error("process() Exception :"+exp.toString());
		}
		//return true;
	}
	
	private boolean dispatchResponse(ISOSource source, ISOMsg reqIsoMsg)
	
	{
		boolean isResultReturned = false;
		System.out.println("dispatchResponse......");
		if (source != null && reqIsoMsg != null)
    	{
			System.out.println("dispatchResponse..if (source != null && reqIsoMsg != null)");
			
			if (reqIsoMsg != null)
			{
				try
				
				{
					reqIsoMsg.setResponseMTI();
					byte[] b1 = reqIsoMsg.pack();
					logger.error("RETURN SERVER LOGGED MESSAGE"+ISOUtil.hexString(b1));
					System.out.println("RETURN SERVER LOGGED MESSAGE"+ISOUtil.hexString(b1));
					
					source.send(reqIsoMsg);
					isResultReturned = true;
				}
				
				catch (ISOException exp)
				
				{
					logger.error("dispatchResponse(): ISOException"+exp.getMessage());
					
				}
				
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
			System.out.println("dispatchResponse..else");
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
			String processingCodeID = requestISOMsg.getString(DMISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			logger.info("isSupportedMesageType(): mti -"+mti);
			logger.info("isSupportedMesageType(): processingCodeID -"+processingCodeID);
			
			String status = DMMessageTypeValidator.isValidMessageTypeId(mti);
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
			String processingCodeID = requestISOMsg.getString(DMISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			logger.info("isSupportedProcessingCode(): mti -"+mti);
			logger.info("isSupportedProcessingCode(): processingCodeID -"+processingCodeID);
			
			String status = DMMessageTypeValidator.isValidProcessingCode(mti,processingCodeID);
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
		String processorID = "";
		
		if(!messageTypeID.equals(DMProcessorConstant.LR_ECHO_PROCESSOR_ID))
			processorID = messageTypeID+"_"+processingID;
		else
			processorID = messageTypeID;
		
		logger.error("processorID: "+processorID);
		
		messageProcesor = DMCommandMapper.getInstance(messageFormat).getMessageProcessor(processorID);
		if (messageProcesor == null)
		{
			logger.error("Unable to find Message Processor for ProcessorID: "+processorID);
		}
		
		return messageProcesor;
	}
	
}
