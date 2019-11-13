package com.bcgi.paymgr.posserver.irp.processor;

import java.util.TimeZone;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

import com.aircls.common.pos.dto.IRMessageDTO;
import com.aircls.common.pos.dto.POSAccountRechargeDTO;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.DateUtil;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.delegate.StatusInquiryDelegate;
import com.bcgi.paymgr.posserver.fw.processor.BaseMessageProcessor;
import com.bcgi.paymgr.posserver.fw.processor.IRPBaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.constant.IRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;
import com.bcgi.paymgr.posserver.irp.constant.PlaceHolderDataConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAccountStatusDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAuthenticationDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.irp.validator.IRPTranStatusInqValidator;
import com.bcgi.paymgr.posserver.POSServerMgr;

public class IRPTransStatusInquiryProcessor  extends IRPBaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(IRPTransStatusInquiryProcessor.class);
	
	public IRPTransStatusInquiryProcessor() 
	{
	
	}
	
	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodes.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountStatusDTO posGWAccountStatusDTO = (POSGWAccountStatusDTO) posGWMessageDTO;
			logger.info(posGWAccountStatusDTO.toString());
			status = IRPTranStatusInqValidator.isValidReqData(posGWAccountStatusDTO);
		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}
		logger.info("IRPStatusValidator.isValidReqData - Status:"+status);

		return status;
	}


	public POSGWMessageDTO processMessage(POSGWMessageDTO reqPOSGWMessageDTO)
	{
		
		POSGWMessageDTO respPOSGWMessageDTO = null;
		
		try
		{
			POSMessageDTO reqPosMessageDTO = packToServerDTO(reqPOSGWMessageDTO);
			logger.info("Calling Status Delegate....");
			StatusInquiryDelegate statusInquiryDelegate = new StatusInquiryDelegate();
			POSAccountRechargeDTO requestAccountRechargeDTO = (POSAccountRechargeDTO)reqPosMessageDTO;
			
			POSMessageDTO respPOSMessageDTO = statusInquiryDelegate.getRechargeTransactionStatus(requestAccountRechargeDTO);
			respPOSGWMessageDTO = unpackFromServerDTO(respPOSMessageDTO);

		}
		catch (DelegateException exp){
			POSGWAccountStatusDTO respPOSGWAccountStatusDTO = new POSGWAccountStatusDTO();
			respPOSGWAccountStatusDTO.setResponseCode(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE);
			respPOSGWMessageDTO = respPOSGWAccountStatusDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	public POSGWMessageDTO unpackFromServerDTO(POSMessageDTO posMessageDTO)
	{
		
		POSGWAccountStatusDTO posGWAccountStatusDTO = new POSGWAccountStatusDTO();
		POSAccountRechargeDTO posAccountRechargeDTO = (POSAccountRechargeDTO)posMessageDTO;
		posGWAccountStatusDTO.setResponseCode(posAccountRechargeDTO.getResponseCode());    	
		posGWAccountStatusDTO.setTransactionStatus(posAccountRechargeDTO.getTransactionStatus());
		return posGWAccountStatusDTO;
	}
	
    public POSMessageDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
    {
    	
    	POSGWAccountStatusDTO posGWAccountStatusDTO =  (POSGWAccountStatusDTO)posGWMessageDTO;
   	
    	POSAccountRechargeDTO posAccountRechargeDTO = new POSAccountRechargeDTO();
    	
    	posAccountRechargeDTO.setCarrierId(posGWAccountStatusDTO.getCarrierId());
    	posAccountRechargeDTO.setInitiatedUser(posGWAccountStatusDTO.getInitiatedUser());
    	
		
    	posAccountRechargeDTO.setMessageFormatType(posGWAccountStatusDTO.getMessageFormatType());
	    posAccountRechargeDTO.setTransactionType(posGWAccountStatusDTO.getTransactionType());
		
	    String referenceNum1 = posGWAccountStatusDTO.getReferenceNumber1();
	    if (PMISOFieldValidator.isAllZeroes(referenceNum1)){
	    	posAccountRechargeDTO.setReferenceNumber(posGWAccountStatusDTO.getReferenceNumber2());
	    }
	    else{
	      	posAccountRechargeDTO.setReferenceNumber(referenceNum1);
	    }
    	  
	    posAccountRechargeDTO.setAuthorizationNumber(posGWAccountStatusDTO.getAuthorizationNumber());
	    posAccountRechargeDTO.setDistributorId(posGWAccountStatusDTO.getDistributorId());
	    posAccountRechargeDTO.setStoreId(posGWAccountStatusDTO.getStoreId());
	    posAccountRechargeDTO.setSubAgentId(posGWAccountStatusDTO.getSubAgentId());
	    posAccountRechargeDTO.setSubscriberMIN(posGWAccountStatusDTO.getSubscriberMIN());
	    String transactionDateTime = posGWAccountStatusDTO.getTransactionDateTime();
	    String monthday = transactionDateTime.substring(0,IRPMessageDataConstant.TRANSACTION_DATE_END_POSITION);
	    String year = DateUtil.getYear(monthday);
	    posAccountRechargeDTO.setTransactionDate(year+monthday);
	    String time = transactionDateTime.substring(IRPMessageDataConstant.TRANSACTION_TIME_START_POSITION,transactionDateTime.length());
	    posAccountRechargeDTO.setTransactionTime(time);
		
	    String subscriberNum = posAccountRechargeDTO.getSubscriberMIN();
		String countrycode = subscriberNum.substring(IRPMessageDataConstant.COUNTRYCODE_START_POSITION,IRPMessageDataConstant.COUNTRYCODE_END_POSITION);
		String mobileNumber = subscriberNum.substring(IRPMessageDataConstant.SUBSCRIBERMIN_START_POSITION,subscriberNum.length());
		mobileNumber = mobileNumber.trim();
	    
		IRMessageDTO irMessageDTO = new IRMessageDTO();
	    irMessageDTO.setDestinationCountryCode(countrycode);
	    irMessageDTO.setDestSubscriberMIN(mobileNumber);
	    posAccountRechargeDTO.setIrMessageDTO(irMessageDTO);
	    
    	return posAccountRechargeDTO;
    	
	}

	
	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		POSGWAccountStatusDTO posGWAccountStatusDTO = new POSGWAccountStatusDTO();
		posGWAccountStatusDTO.setResponseCode(ResponseCodes.INITIAL_STATE);
		POSGWAuthenticationDTO posGWAuthenticationDTO = (POSGWAuthenticationDTO)posGWMessageDTO;
		
		try
		{
			
			
			posGWAccountStatusDTO.setCarrierId(posGWAuthenticationDTO.getCarrierId());
			posGWAccountStatusDTO.setInitiatedUser(posGWAuthenticationDTO.getInitiatedUser());
						
			posGWAccountStatusDTO.setMessageFormatType(MessageFormatConstant.IRP);
			String transactionType = getIRPTransactionType(requestISOMsg.getMTI());
			posGWAccountStatusDTO.setTransactionType(transactionType);
			
			String processingCode = requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			if (processingCode != null){
				processingCode = processingCode.trim();
			}
			posGWAccountStatusDTO.setProcessingCode(processingCode);
			
			String amount = requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT);
			if (amount != null){
				amount = amount.trim();
			}
			posGWAccountStatusDTO.setTransactionAmount(amount);
			
			String transactionDateTime = requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME);
			if (transactionDateTime != null){
				transactionDateTime = transactionDateTime.trim();
			}
			posGWAccountStatusDTO.setTransactionDateTime(transactionDateTime);
			
			String systemTraceAuditNum = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER);
			if (systemTraceAuditNum != null){
				systemTraceAuditNum = systemTraceAuditNum.trim();
			}
			posGWAccountStatusDTO.setSystemTraceAuditNumber(systemTraceAuditNum);
		
			String captureDate = requestISOMsg.getString(IRPISOMessageFieldIDConstant.CAPTURE_DATE);
			if (captureDate != null){
				captureDate = captureDate.trim();
			}
			posGWAccountStatusDTO.setCaptureDate(captureDate);
		
						
			String modality = requestISOMsg.getString(IRPISOMessageFieldIDConstant.MODALITY);
			if (modality != null){
				modality = modality.trim();
			}
			posGWAccountStatusDTO.setModality(modality);
			
			String distributorId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			if (distributorId != null){
				distributorId = distributorId.trim();
			}
			posGWAccountStatusDTO.setDistributorId(distributorId);
			
			String referenceNumber1 = requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1);
			if (referenceNumber1 != null){
				referenceNumber1 = referenceNumber1.trim();
			}
			posGWAccountStatusDTO.setReferenceNumber1(referenceNumber1);
			
			String authorizationNumber = requestISOMsg.getString(IRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
			if (authorizationNumber != null){
				authorizationNumber = authorizationNumber.trim();
			}
			posGWAccountStatusDTO.setAuthorizationNumber(authorizationNumber);
			
			String storemanagerId  = requestISOMsg.getString(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posGWAccountStatusDTO.setStoreId(storemanagerId);
			
			String subagentId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
			}
			posGWAccountStatusDTO.setSubAgentId(subagentId);
			
			String subscriberNum = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER);
			if (subscriberNum != null){
				subscriberNum = subscriberNum.trim();
			}
			posGWAccountStatusDTO.setSubscriberMIN(subscriberNum);
									
			String originCurrencyCode = requestISOMsg.getString(IRPISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null){
				originCurrencyCode = originCurrencyCode.trim();
			}
			
			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
			posGWAccountStatusDTO.setIrMessageDTO(posGWIRMessageDTO);
			
			String referenceNumber2 = requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_2);
			if (referenceNumber2 != null){
				referenceNumber2 = referenceNumber2.trim();
			}
			posGWAccountStatusDTO.setReferenceNumber2(referenceNumber2);
			
		
		}
		catch(ISOException isoexp){
			posGWAccountStatusDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		return posGWAccountStatusDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO)
	{
		POSGWAccountStatusDTO posGWAccountStatusDTO = null;

		try
		{
			if (posMessageDTO != null)
			{
				posGWAccountStatusDTO = (POSGWAccountStatusDTO) posMessageDTO;
				requestISOMsg.set(IRPISOMessageFieldIDConstant.RECHARGE_TRANSACTION_RESPONSE_CODE_ID,posGWAccountStatusDTO.getTransactionStatus());
				requestISOMsg.set(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountStatusDTO.getResponseCode());
			}
		}
		catch(ISOException isoexp){
			logger.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){
		long startTime = 0;
		long processTime = 0;
		String carrierId = "";
		String endDateTime = "";
		String startDateTime = "";
		String transStatus = "";
		String posTimeZone = (String)POSServerMgr.getPosTimeZone().get(ServerConfigConstant.POS_TIME_ZONE);
		logger.info("IRP Status MIN Processor- execute()........");
		ISOMsg responseISOMsg = requestISOMsg;
        startTime = System.currentTimeMillis();
        startDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		try{
		POSGWAccountStatusDTO posGWAccountStatusDTO =  (POSGWAccountStatusDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
		carrierId = posGWAccountStatusDTO.getCarrierId();
		endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		if (posGWAccountStatusDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
		{
			startTime = System.currentTimeMillis();
			  String status = isValidMessage(posGWAccountStatusDTO);

			  
				
			  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
			  {
				  processTime += (System.currentTimeMillis() - startTime);
				  endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
				  posGWAccountStatusDTO = (POSGWAccountStatusDTO)processMessage(posGWAccountStatusDTO);
			
			  }
			  else
			  {
				  posGWAccountStatusDTO.setResponseCode(status);
			  }
		}

		
		
		responseISOMsg = packISOMsg(requestISOMsg,posGWAccountStatusDTO);
	
		if(posGWAccountStatusDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.SUCCESS))
			transStatus = "SUCCESS";
		/*else if(posGWAccountStatusDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE) 
				|| posGWAccountStatusDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.SYSTEM_ERROR))
			transStatus = "FAILURE";
			*/
		else 
			transStatus = "REJECTED";
		
		        }
        catch(Exception exp){
        	exp.printStackTrace();
			try
			{
				processTime += (System.currentTimeMillis() - startTime);
				endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		    responseISOMsg.set(new ISOField (IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodes.POSGATEWAY_SYSTEM_ERROR));
			responseISOMsg.set(new ISOField (IRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER, PlaceHolderDataConstant.AUTHORIZATION_NUMBER));
			
			logger.error("IRPStatusMINProcessor-Exception at execute(): "+exp.getMessage());
			}
			catch(Exception ex){
				logger.error("IRPStatusMINProcessor-Exception at execute(): "+exp.getMessage());
					
			}
		}
        finally
        {
        	AppMonitorWrapper.logEvent("POSGW_SERVICE_REQUEST",
				  	"|"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER).substring(26).trim()
				   +"|"+"STATUS_INQUIRY"
				   +"|"+transStatus
				   +"|"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1).trim()
				   +"|"+carrierId
				   +"|"+startDateTime
				   +"|"+endDateTime
				   +"|"+processTime+"|",(int)processTime);
        }

		return responseISOMsg;
	}
	
	public ISOMsg packResponseMessage(ISOMsg requestISOMsg){
		ISOMsg respISOMsg = null;
		try
		{
			
			//requestISOMsg.setResponseMTI();
			respISOMsg = requestISOMsg;
					
						
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

