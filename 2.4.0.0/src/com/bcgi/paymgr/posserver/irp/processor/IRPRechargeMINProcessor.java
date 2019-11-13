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
import com.bcgi.paymgr.posserver.POSServerMgr;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.DateUtil;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.delegate.RechargeDelegate;
import com.bcgi.paymgr.posserver.fw.processor.IRPBaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.constant.IRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.PlaceHolderDataConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAuthenticationDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.irp.validator.IRPRechargeValidator;


public class IRPRechargeMINProcessor extends IRPBaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(IRPRechargeMINProcessor.class);

	public IRPRechargeMINProcessor()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodes.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = IRPRechargeValidator.isValidReqData(posGWAccountRechargeDTO);
		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}
		logger.info("IRPRechargeValidator.isValidReqData - Status:"+status);

		return status;
	}


	public POSGWMessageDTO processMessage(POSGWMessageDTO reqPOSGWMessageDTO)
	{

		POSGWMessageDTO respPOSGWMessageDTO = null;

		try
		{
			POSMessageDTO reqPosMessageDTO = packToServerDTO(reqPOSGWMessageDTO);
			logger.info("IRPRechargeMINProcessor Calling Recharge Delegate......");
			POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)reqPOSGWMessageDTO;
			System.out.println("POSGW  IRPRechargeMINProcessor Calling Recharge Delegate...."+posGWAccountRechargeDTO.getProcessingCode());
			RechargeDelegate rechargeDelegate = new RechargeDelegate();
			POSMessageDTO respPOSMessageDTO = null;
			if(IRPMessageTypeIDConstant.IRP_ASYNC_UNIFIED_RECHARGE_PROCESSING_CODE_ID.equals(posGWAccountRechargeDTO.getProcessingCode())){
				respPOSMessageDTO = rechargeDelegate.processAsyncUnifiedRecharge(reqPosMessageDTO); // PID:350000
			}else if(IRPMessageTypeIDConstant.IRP_SYNC_MIN_VALIDATION_PROCESSING_CODE_ID.equals(posGWAccountRechargeDTO.getProcessingCode())){
				respPOSMessageDTO = rechargeDelegate.processSyncSubscriberValidation(reqPosMessageDTO); // PID:100000
			}else if(IRPMessageTypeIDConstant.IRP_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID.equals(posGWAccountRechargeDTO.getProcessingCode())){
				respPOSMessageDTO = rechargeDelegate.processAsyncSubscriberRecharge(reqPosMessageDTO); // PID:300000
			}else if(IRPMessageTypeIDConstant.IRP_ASYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID.equals(posGWAccountRechargeDTO.getProcessingCode()) || IRPMessageTypeIDConstant.IRP_ASYNC_REVERSAL_INQUIRY_PROCESSING_CODE_ID.equals(posGWAccountRechargeDTO.getProcessingCode())){
				respPOSMessageDTO = rechargeDelegate.orderStatusInquriy(reqPosMessageDTO); // PID:600000 or PID:700000 
			}else {
			   respPOSMessageDTO = rechargeDelegate.processIRMINRechargeTransaction(reqPosMessageDTO);
			}
			System.out.println("BEFORE SENDING RESPONSE BACK TO THE POS");
			respPOSGWMessageDTO = unpackFromServerDTO(respPOSMessageDTO);

		}
		catch (DelegateException exp){
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			respPOSGWAccountRechargeDTO.setResponseCode(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE);
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	public POSGWMessageDTO unpackFromServerDTO(POSMessageDTO posMessageDTO)
	{

		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		POSAccountRechargeDTO posAccountRechargeDTO = (POSAccountRechargeDTO)posMessageDTO;
		posGWAccountRechargeDTO.setTransactionId(posAccountRechargeDTO.getTransactionId());
		posGWAccountRechargeDTO.setAuthorizationNumber(posAccountRechargeDTO.getAuthorizationNumber());
		posGWAccountRechargeDTO.setResponseCode(posAccountRechargeDTO.getResponseCode());
		posGWAccountRechargeDTO.setOrderId(posAccountRechargeDTO.getOrderId());
		posGWAccountRechargeDTO.setValidationId(posAccountRechargeDTO.getValidationId());
		posGWAccountRechargeDTO.setTransactionId(posAccountRechargeDTO.getTransactionId());
		posGWAccountRechargeDTO.setTransactionStatus(posAccountRechargeDTO.getTransactionStatus());
		posGWAccountRechargeDTO.setProcessingCode(posAccountRechargeDTO.getProcessingCode());
		System.out.println("unpackFromServerDTO======="+posAccountRechargeDTO.getValidationId()+","+posAccountRechargeDTO.getOrderId()+","+posAccountRechargeDTO.getTransactionStatus());
		IRMessageDTO irMessageDTO = posAccountRechargeDTO.getIrMessageDTO();

		if (irMessageDTO != null)
		{
			String destinationCurrencyCode = irMessageDTO.getDestinationCurrencyCode();
			String destinationAmount = irMessageDTO.getDestAmount();


			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setDestinationCurrencyCode(destinationCurrencyCode);
			posGWIRMessageDTO.setDestAmount(destinationAmount);
			posGWAccountRechargeDTO.setIrMessageDTO(posGWIRMessageDTO);
		}

		return posGWAccountRechargeDTO;
	}

    public POSMessageDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
    {

    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;

    	POSAccountRechargeDTO posAccountRechargeDTO = new POSAccountRechargeDTO();

    	posAccountRechargeDTO.setCarrierId(posGWAccountRechargeDTO.getCarrierId());
    	posAccountRechargeDTO.setInitiatedUser(posGWAccountRechargeDTO.getInitiatedUser());
    	posAccountRechargeDTO.setTransactionId(posGWAccountRechargeDTO.getTransactionId());

    	posAccountRechargeDTO.setMessageFormatType(posGWAccountRechargeDTO.getMessageFormatType());
	    posAccountRechargeDTO.setTransactionType(posGWAccountRechargeDTO.getTransactionType());

	    String referenceNum1 = posGWAccountRechargeDTO.getReferenceNumber1();
	    if (PMISOFieldValidator.isAllZeroes(referenceNum1)){
	    	posAccountRechargeDTO.setReferenceNumber(posGWAccountRechargeDTO.getReferenceNumber2());
	    }
	    else{
	      	posAccountRechargeDTO.setReferenceNumber(referenceNum1);
	    }

	    posAccountRechargeDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
	    posAccountRechargeDTO.setStoreId(posGWAccountRechargeDTO.getStoreId());
	    posAccountRechargeDTO.setSubAgentId(posGWAccountRechargeDTO.getSubAgentId());
	    posAccountRechargeDTO.setSubscriberMIN(posGWAccountRechargeDTO.getSubscriberMIN());
	    String amount = posGWAccountRechargeDTO.getTransactionAmount();
	    posAccountRechargeDTO.setTransactionAmount(DataUtil.getAmountWithoutPrefixZeroes(amount));
	    String transactionDateTime = posGWAccountRechargeDTO.getTransactionDateTime();
	    String monthday = transactionDateTime.substring(0,IRPMessageDataConstant.TRANSACTION_DATE_END_POSITION);
	    String year = DateUtil.getYear(monthday);
	    posAccountRechargeDTO.setTransactionDate(year+monthday);
	    String time = transactionDateTime.substring(IRPMessageDataConstant.TRANSACTION_TIME_START_POSITION,transactionDateTime.length());
	    posAccountRechargeDTO.setTransactionTime(time);

	    posAccountRechargeDTO.setCaptureDate(posGWAccountRechargeDTO.getCaptureDate());

	    String subscriberNum = posAccountRechargeDTO.getSubscriberMIN();
		String countrycode = subscriberNum.substring(IRPMessageDataConstant.COUNTRYCODE_START_POSITION,IRPMessageDataConstant.COUNTRYCODE_END_POSITION);
		String cellNumber = subscriberNum.substring(IRPMessageDataConstant.COUNTRYCODE_END_POSITION,subscriberNum.length());
		cellNumber = cellNumber.trim();

		POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountRechargeDTO.getIrMessageDTO();
		String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();

	    IRMessageDTO irMessageDTO = new IRMessageDTO();
	    irMessageDTO.setDestinationCountryCode(countrycode);
	    irMessageDTO.setDestSubscriberMIN(cellNumber);
	    irMessageDTO.setOriginCurrencyCode(originCurrencyCode);
	    posAccountRechargeDTO.setProcessingCode(posGWAccountRechargeDTO.getProcessingCode());
	    posAccountRechargeDTO.setIrMessageDTO(irMessageDTO);
	    posAccountRechargeDTO.setValidationId(posGWAccountRechargeDTO.getValidationId());
	    posAccountRechargeDTO.setOrderId(posGWAccountRechargeDTO.getOrderId());
    	return posAccountRechargeDTO;

	}


	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		posGWAccountRechargeDTO.setResponseCode(ResponseCodes.INITIAL_STATE);
		POSGWAuthenticationDTO posGWAuthenticationDTO = (POSGWAuthenticationDTO)posGWMessageDTO;

		try
		{


			posGWAccountRechargeDTO.setCarrierId(posGWAuthenticationDTO.getCarrierId());
			posGWAccountRechargeDTO.setInitiatedUser(posGWAuthenticationDTO.getInitiatedUser());
			posGWAccountRechargeDTO.setTransactionId(posGWAuthenticationDTO.getTransactionId());

			posGWAccountRechargeDTO.setMessageFormatType(MessageFormatConstant.IRP);
			String transactionType = getIRPTransactionType(requestISOMsg.getMTI());
			posGWAccountRechargeDTO.setTransactionType(transactionType);

			String processingCode = requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			if (processingCode != null){
				processingCode = processingCode.trim();
			}
			posGWAccountRechargeDTO.setProcessingCode(processingCode);

			String amount = requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT);
			if (amount != null){
				amount = amount.trim();
			}
			posGWAccountRechargeDTO.setTransactionAmount(amount);

			String transactionDateTime = requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME);
			if (transactionDateTime != null){
				transactionDateTime = transactionDateTime.trim();
			}
			posGWAccountRechargeDTO.setTransactionDateTime(transactionDateTime);

			String systemTraceAuditNum = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER);
			if (systemTraceAuditNum != null){
				systemTraceAuditNum = systemTraceAuditNum.trim();
			}
			posGWAccountRechargeDTO.setSystemTraceAuditNumber(systemTraceAuditNum);

			String captureDate = requestISOMsg.getString(IRPISOMessageFieldIDConstant.CAPTURE_DATE);
			if (captureDate != null){
				captureDate = captureDate.trim();
			}
			posGWAccountRechargeDTO.setCaptureDate(captureDate);

			String posEntryMode = requestISOMsg.getString(IRPISOMessageFieldIDConstant.POS_ENTRY_MODE);
			if (posEntryMode != null){
				posEntryMode = posEntryMode.trim();
			}
			posGWAccountRechargeDTO.setPosEntryMode(posEntryMode);

			String modality = requestISOMsg.getString(IRPISOMessageFieldIDConstant.MODALITY);
			if (modality != null){
				modality = modality.trim();
			}
			posGWAccountRechargeDTO.setModality(modality);

			String distributorId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			if (distributorId != null){
				distributorId = distributorId.trim();
			}
			posGWAccountRechargeDTO.setDistributorId(distributorId);

			String referenceNumber1 = requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1);
			if (referenceNumber1 != null){
				referenceNumber1 = referenceNumber1.trim();
			}
			posGWAccountRechargeDTO.setReferenceNumber1(referenceNumber1);

			String storemanagerId  = requestISOMsg.getString(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posGWAccountRechargeDTO.setStoreId(storemanagerId);

			String subagentId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
			}
			posGWAccountRechargeDTO.setSubAgentId(subagentId);

			String subscriberNum = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER);
			if (subscriberNum != null){
				subscriberNum = subscriberNum.trim();
			}
			posGWAccountRechargeDTO.setSubscriberMIN(subscriberNum);

			String cardAcceptorName = requestISOMsg.getString(IRPISOMessageFieldIDConstant.CARD_ACCEPTOR_NAME);
			if (cardAcceptorName != null){
				cardAcceptorName = cardAcceptorName.trim();
			}
			posGWAccountRechargeDTO.setCardAcceptorName(cardAcceptorName);

			String originCurrencyCode = requestISOMsg.getString(IRPISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null){
				originCurrencyCode = originCurrencyCode.trim();
			}
			String validationId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.VALIDATION_ID);
			if (validationId != null){
				validationId = validationId.trim();
			}
			String orderId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.ORDER_ID);
			if (orderId != null){
				orderId = orderId.trim();
			}			
			posGWAccountRechargeDTO.setOrderId(orderId);
			posGWAccountRechargeDTO.setValidationId(validationId);
			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
			posGWAccountRechargeDTO.setIrMessageDTO(posGWIRMessageDTO);

			String referenceNumber2 = requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_2);
			if (referenceNumber2 != null){
				referenceNumber2 = referenceNumber2.trim();
			}
			posGWAccountRechargeDTO.setReferenceNumber2(referenceNumber2);


		}
		catch(ISOException isoexp){
			posGWAccountRechargeDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		return posGWAccountRechargeDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO)
	{
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = null;

		try
		{
			if (posMessageDTO != null)
			{
				posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posMessageDTO;
				requestISOMsg.set(IRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,posGWAccountRechargeDTO.getAuthorizationNumber());
				requestISOMsg.set(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());
				requestISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_ID,posGWAccountRechargeDTO.getTransactionId());
				System.out.println("IN PACKISO=="+posGWAccountRechargeDTO.getOrderId()+","+posGWAccountRechargeDTO.getValidationId());
				if(posGWAccountRechargeDTO.getValidationId() != null)
					requestISOMsg.set(IRPISOMessageFieldIDConstant.VALIDATION_ID,posGWAccountRechargeDTO.getValidationId());
				if(posGWAccountRechargeDTO.getOrderId() != null)
					requestISOMsg.set(IRPISOMessageFieldIDConstant.ORDER_ID,posGWAccountRechargeDTO.getOrderId());

				if(IRPMessageTypeIDConstant.IRP_ASYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID.equals(posGWAccountRechargeDTO.getProcessingCode()) || IRPMessageTypeIDConstant.IRP_ASYNC_REVERSAL_INQUIRY_PROCESSING_CODE_ID.equals(posGWAccountRechargeDTO.getProcessingCode())){
					requestISOMsg.set(IRPISOMessageFieldIDConstant.RECHARGE_TRANSACTION_RESPONSE_CODE_ID,posGWAccountRechargeDTO.getTransactionStatus()); // PID:600000 or PID:700000 
				}
				
				POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountRechargeDTO.getIrMessageDTO();
				if (posGWIRMessageDTO != null){
				String destinationCurrencyCode = posGWIRMessageDTO.getDestinationCurrencyCode();
				String destinationAmount = posGWIRMessageDTO.getDestAmount();
				requestISOMsg.set(IRPISOMessageFieldIDConstant.CONVERSION_CURRENCY_CODE,destinationCurrencyCode);
				requestISOMsg.set(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT,destinationAmount);
				}
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
		String startDateTime = "";
		String endDateTime = "";
		String transStatus = "";
		
		logger.info("IRP Recharge MIN Processor- execute()........");
		ISOMsg responseISOMsg = requestISOMsg;
        startTime = System.currentTimeMillis();
        String posTimeZone = (String)POSServerMgr.getPosTimeZone().get(ServerConfigConstant.POS_TIME_ZONE);
        startDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");       
 	        
 try{
       
        
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
		carrierId = posGWAccountRechargeDTO.getCarrierId();
		endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		if (posGWAccountRechargeDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
		{
			  String status = isValidMessage(posGWAccountRechargeDTO);

			  	processTime += System.currentTimeMillis() - startTime;
				endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
				
			  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
			  {	  
				
				  posGWAccountRechargeDTO = (POSGWAccountRechargeDTO)processMessage(posGWAccountRechargeDTO);
				  startTime = System.currentTimeMillis();
			  }
			  else
			  {
				  posGWAccountRechargeDTO.setAuthorizationNumber(PlaceHolderDataConstant.AUTHORIZATION_NUMBER);
				  posGWAccountRechargeDTO.setResponseCode(status);
			  }
		}

		
		responseISOMsg = packISOMsg(requestISOMsg,posGWAccountRechargeDTO);
	
		if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.SUCCESS))
			transStatus = "SUCCESS";
		/*else if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE) 
				|| posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.SYSTEM_ERROR))
			transStatus = "FAILURE";
			*/
		else 
			transStatus = "REJECTED";
		
		
        }
        catch(Exception exp){
        	exp.printStackTrace();
			try
			{
		    
				processTime += System.currentTimeMillis() - startTime;
				endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
				
		    responseISOMsg.set(new ISOField (IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodes.POSGATEWAY_SYSTEM_ERROR));
			responseISOMsg.set(new ISOField (IRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER, PlaceHolderDataConstant.AUTHORIZATION_NUMBER));

			logger.error("IRPRechargeMINProcessor-Exception at execute(): "+exp.getMessage());
			
			}
			catch(Exception ex){
				logger.error("IRPRechargeMINProcessor-Exception at execute(): "+exp.getMessage());

			}
		}
        finally
        {
        	AppMonitorWrapper.logEvent("POSGW_SERVICE_REQUEST",
				  	"|"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER).substring(26).trim()
				   +"|"+"RECHARGE"
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

			 System.out.println(" /n/nRecharge packResponseMessage...."+requestISOMsg);
			//requestISOMsg.setResponseMTI();
			respISOMsg = requestISOMsg;

			System.out.println(" Recharge packResponseMessage....Step 1");
			//respISOMsg.unset(IRPISOMessageFieldIDConstant.TRANSACTION_ID);
			//respISOMsg.recalcBitMap();

			System.out.println(" Recharge packResponseMessage....Step 2");
			if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT))){
            	respISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT,PlaceHolderDataConstant.TRANSACTION_AMNT);
			}
			System.out.println(" Recharge packResponseMessage....Step 3");
            System.out.println("requestISOMsg.getString(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT):"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT));
            System.out.println("PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT):"+PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT)));
      		
            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DEST_TRANSACTION_AMNT)))
  			{
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
           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.RECHARGE_REFERENCE_NUMBER))){
             	respISOMsg.set(IRPISOMessageFieldIDConstant.RECHARGE_REFERENCE_NUMBER,PlaceHolderDataConstant.RECHARGE_REFERENCE_NUMBER);
     		   }
           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.RECHARGE_AUTH_NUMBER))){
             	respISOMsg.set(IRPISOMessageFieldIDConstant.RECHARGE_AUTH_NUMBER,PlaceHolderDataConstant.RECHARGE_AUTH_NUMBER);
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
        	logger.error("IRPRechargeMINProcessor-Exception at updateSendMsgStatus(): "+exp.getMessage());

        }
	}

}
