package com.bcgi.paymgr.posserver.irp.processor;


import java.util.TimeZone;

import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import com.aircls.common.pos.dto.IRMessageDTO;
import com.aircls.common.pos.dto.POSAccountRechargeDTO;
import com.aircls.common.pos.dto.POSAccountReversalDTO;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.aircls.common.pos.dto.POSAccountReversalDTO;
import com.bcgi.paymgr.posserver.POSServerMgr;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import  com.bcgi.paymgr.posserver.common.constant.MessageDataConstant;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.DateUtil;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.delegate.RechargeDelegate;
import com.bcgi.paymgr.posserver.delegate.ReversalDelegate;
import com.bcgi.paymgr.posserver.delegate.ReversalDelegate;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.fw.processor.BaseMessageProcessor;
import com.bcgi.paymgr.posserver.fw.processor.IRPBaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.constant.IRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.PlaceHolderDataConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAccountReversalDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAuthenticationDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.irp.validator.IRPReversalValidator;
import com.bcgi.paymgr.posserver.irp.validator.IRPReversalValidator;

public class IRPReversalMINProcessor  extends IRPBaseMessageProcessor{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(IRPReversalMINProcessor.class);

	public IRPReversalMINProcessor()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodes.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountReversalDTO posGWAccountReversalDTO = (POSGWAccountReversalDTO) posGWMessageDTO;
			logger.info(posGWAccountReversalDTO.toString());
			status = IRPReversalValidator.isValidReqData(posGWAccountReversalDTO);
		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}
		logger.info("IRPReversalValidator.isValidReqData - Status:"+status);

		return status;
	}


	public POSGWMessageDTO processMessage(POSGWMessageDTO reqPOSGWMessageDTO)
	{

		POSGWMessageDTO respPOSGWMessageDTO = null;

		try
		{
			POSMessageDTO reqPosMessageDTO = packToServerDTO(reqPOSGWMessageDTO);
			
			ReversalDelegate reversalDelegate = new ReversalDelegate();
			POSAccountReversalDTO posAccountReversalDTO = (POSAccountReversalDTO)reqPosMessageDTO;
			logger.info("Calling Reversal Delegate...."+posAccountReversalDTO.getProcessingCode());
			System.out.println("Calling Reversal Delegate...."+posAccountReversalDTO.getProcessingCode());
			POSMessageDTO respPOSMessageDTO = null; 
			if(IRPMessageTypeIDConstant.IRP_ASYNC_MIN_REVERSAL_PROCESSING_CODE_ID.equals(posAccountReversalDTO.getProcessingCode())){
				respPOSMessageDTO = reversalDelegate.revertRechargeProcess(posAccountReversalDTO); // PID:500000
			}else{
			   respPOSMessageDTO = reversalDelegate.revertIRMINRechargeTransaction(posAccountReversalDTO);
			}
			respPOSGWMessageDTO = unpackFromServerDTO(respPOSMessageDTO);

		}
		catch (DelegateException exp){
			POSGWAccountReversalDTO respPOSGWAccountReversalDTO = new POSGWAccountReversalDTO();
			respPOSGWAccountReversalDTO.setResponseCode(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE);
			respPOSGWMessageDTO = respPOSGWAccountReversalDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	public POSGWMessageDTO unpackFromServerDTO(POSMessageDTO posMessageDTO)
	{

		POSGWAccountReversalDTO posGWAccountReversalDTO = new POSGWAccountReversalDTO();
		POSAccountReversalDTO posAccountReversalDTO = (POSAccountReversalDTO)posMessageDTO;
		posGWAccountReversalDTO.setAuthorizationNumber(posAccountReversalDTO.getAuthorizationNumber());
		posGWAccountReversalDTO.setResponseCode(posAccountReversalDTO.getResponseCode());
		posGWAccountReversalDTO.setTransactionId(posAccountReversalDTO.getTransactionId());
		posGWAccountReversalDTO.setOrderId(posAccountReversalDTO.getOrderId());
		IRMessageDTO irMessageDTO = posAccountReversalDTO.getIrMessageDTO();

		if (irMessageDTO != null)
		{
			String destinationCurrencyCode = irMessageDTO.getDestinationCurrencyCode();
			String destinationAmount = irMessageDTO.getDestAmount();

			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setDestinationCurrencyCode(destinationCurrencyCode);
			posGWIRMessageDTO.setDestAmount(destinationAmount);
			posGWAccountReversalDTO.setIrMessageDTO(posGWIRMessageDTO);
		}

		return posGWAccountReversalDTO;
	}

    public POSMessageDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
    {

    	POSGWAccountReversalDTO posGWAccountReversalDTO =  (POSGWAccountReversalDTO)posGWMessageDTO;

    	POSAccountReversalDTO posAccountReversalDTO = new POSAccountReversalDTO();

    	posAccountReversalDTO.setCarrierId(posGWAccountReversalDTO.getCarrierId());
    	posAccountReversalDTO.setInitiatedUser(posGWAccountReversalDTO.getInitiatedUser());

    	posAccountReversalDTO.setMessageFormatType(posGWAccountReversalDTO.getMessageFormatType());
	    posAccountReversalDTO.setTransactionType(posGWAccountReversalDTO.getTransactionType());

	    String referenceNum1 = posGWAccountReversalDTO.getReferenceNumber1();
	    if (PMISOFieldValidator.isAllZeroes(referenceNum1)){
	    	posAccountReversalDTO.setReferenceNumber(posGWAccountReversalDTO.getReferenceNumber2());
	    }
	    else{
	      	posAccountReversalDTO.setReferenceNumber(referenceNum1);
	    }
		 posAccountReversalDTO.setProcessingCode(posGWAccountReversalDTO.getProcessingCode());
	    posAccountReversalDTO.setDistributorId(posGWAccountReversalDTO.getDistributorId());
	    posAccountReversalDTO.setStoreId(posGWAccountReversalDTO.getStoreId());
	    posAccountReversalDTO.setSubAgentId(posGWAccountReversalDTO.getSubAgentId());
	    posAccountReversalDTO.setSubscriberMIN(posGWAccountReversalDTO.getSubscriberMIN());
	    String amount = posGWAccountReversalDTO.getTransactionAmount();
	    posAccountReversalDTO.setTransactionAmount(DataUtil.getAmountWithoutPrefixZeroes(amount));
	    String transactionDateTime = posGWAccountReversalDTO.getTransactionDateTime();
	    String monthday = transactionDateTime.substring(0,IRPMessageDataConstant.TRANSACTION_DATE_END_POSITION);
	    String year = DateUtil.getYear(monthday);
	    posAccountReversalDTO.setTransactionDate(year+monthday);
	    String time = transactionDateTime.substring(IRPMessageDataConstant.TRANSACTION_TIME_START_POSITION,transactionDateTime.length());
	    posAccountReversalDTO.setTransactionTime(time);

	    posAccountReversalDTO.setCaptureDate(posGWAccountReversalDTO.getCaptureDate());

	    posAccountReversalDTO.setRechargeReferenceNumber(posGWAccountReversalDTO.getRechargeReferenceNumber());
	    posAccountReversalDTO.setRechargeAuthorizationNumber(posGWAccountReversalDTO.getRechargeAuthorizationNumber());

	    posAccountReversalDTO.setOrderId(posGWAccountReversalDTO.getOrderId());
	    posAccountReversalDTO.setValidationId(posGWAccountReversalDTO.getValidationId());
	    
	    String subscriberNum = posAccountReversalDTO.getSubscriberMIN();
		String countrycode = subscriberNum.substring(IRPMessageDataConstant.COUNTRYCODE_START_POSITION,IRPMessageDataConstant.COUNTRYCODE_END_POSITION);
		String cellNumber = subscriberNum.substring(IRPMessageDataConstant.COUNTRYCODE_END_POSITION,subscriberNum.length());
		cellNumber = cellNumber.trim();

		POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountReversalDTO.getIrMessageDTO();
		String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();

	    IRMessageDTO irMessageDTO = new IRMessageDTO();
	    irMessageDTO.setDestinationCountryCode(countrycode);
	    irMessageDTO.setDestSubscriberMIN(cellNumber);
	    irMessageDTO.setOriginCurrencyCode(originCurrencyCode);
	    posAccountReversalDTO.setIrMessageDTO(irMessageDTO);

    	return posAccountReversalDTO;

	}


	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		POSGWAccountReversalDTO posGWAccountReversalDTO = new POSGWAccountReversalDTO();
		posGWAccountReversalDTO.setResponseCode(ResponseCodes.INITIAL_STATE);
		POSGWAuthenticationDTO posGWAuthenticationDTO = (POSGWAuthenticationDTO)posGWMessageDTO;

		try
		{

			posGWAccountReversalDTO.setCarrierId(posGWAuthenticationDTO.getCarrierId());
			posGWAccountReversalDTO.setInitiatedUser(posGWAuthenticationDTO.getInitiatedUser());
			posGWAccountReversalDTO.setTransactionId(posGWAuthenticationDTO.getTransactionId());

			posGWAccountReversalDTO.setMessageFormatType(MessageFormatConstant.IRP);
			String transactionType = getIRPTransactionType(requestISOMsg.getMTI());
			posGWAccountReversalDTO.setTransactionType(transactionType);

			String processingCode = requestISOMsg.getString(IRPISOMessageFieldIDConstant.PROCESSING_CODE_ID);
			if (processingCode != null){
				processingCode = processingCode.trim();
			}
			posGWAccountReversalDTO.setProcessingCode(processingCode);

			String amount = requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_AMNT);
			if (amount != null){
				amount = amount.trim();
			}
			posGWAccountReversalDTO.setTransactionAmount(amount);

			String transactionDateTime = requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME);
			if (transactionDateTime != null){
				transactionDateTime = transactionDateTime.trim();
			}
			posGWAccountReversalDTO.setTransactionDateTime(transactionDateTime);

			String systemTraceAuditNum = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER);
			if (systemTraceAuditNum != null){
				systemTraceAuditNum = systemTraceAuditNum.trim();
			}
			posGWAccountReversalDTO.setSystemTraceAuditNumber(systemTraceAuditNum);

			String captureDate = requestISOMsg.getString(IRPISOMessageFieldIDConstant.CAPTURE_DATE);
			if (captureDate != null){
				captureDate = captureDate.trim();
			}
			posGWAccountReversalDTO.setCaptureDate(captureDate);


			String modality = requestISOMsg.getString(IRPISOMessageFieldIDConstant.MODALITY);
			if (modality != null){
				modality = modality.trim();
			}
			posGWAccountReversalDTO.setModality(modality);

			String distributorId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			if (distributorId != null){
				distributorId = distributorId.trim();
			}
			posGWAccountReversalDTO.setDistributorId(distributorId);

			String referenceNumber1 = requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1);
			if (referenceNumber1 != null){
				referenceNumber1 = referenceNumber1.trim();
			}
			posGWAccountReversalDTO.setReferenceNumber1(referenceNumber1);

			String storemanagerId  = requestISOMsg.getString(IRPISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posGWAccountReversalDTO.setStoreId(storemanagerId);

			String subagentId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
			}
			posGWAccountReversalDTO.setSubAgentId(subagentId);

			String subscriberNum = requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER);
			if (subscriberNum != null){
				subscriberNum = subscriberNum.trim();
			}
			posGWAccountReversalDTO.setSubscriberMIN(subscriberNum);

			String rechargeReferenceNumber = requestISOMsg.getString(IRPISOMessageFieldIDConstant.RECHARGE_REFERENCE_NUMBER);
			if (rechargeReferenceNumber != null){
				rechargeReferenceNumber = rechargeReferenceNumber.trim();
			}
			posGWAccountReversalDTO.setRechargeReferenceNumber(rechargeReferenceNumber);

			String rechargeAuthNumber = requestISOMsg.getString(IRPISOMessageFieldIDConstant.RECHARGE_AUTH_NUMBER);
			if (rechargeAuthNumber != null){
				rechargeAuthNumber = rechargeAuthNumber.trim();
			}
			posGWAccountReversalDTO.setRechargeAuthorizationNumber(rechargeAuthNumber);

			String originCurrencyCode = requestISOMsg.getString(IRPISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null){
				originCurrencyCode = originCurrencyCode.trim();
			}

			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
			posGWAccountReversalDTO.setIrMessageDTO(posGWIRMessageDTO);

			String referenceNumber2 = requestISOMsg.getString(IRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_2);
			if (referenceNumber2 != null){
				referenceNumber2 = referenceNumber2.trim();
			}
			posGWAccountReversalDTO.setReferenceNumber2(referenceNumber2);
			String orderId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.ORDER_ID);
			if (orderId != null){
				orderId = orderId.trim();
			}
			posGWAccountReversalDTO.setOrderId(orderId);

			String validationId = requestISOMsg.getString(IRPISOMessageFieldIDConstant.VALIDATION_ID);
			if (validationId != null){
				validationId = validationId.trim();
			}
			posGWAccountReversalDTO.setValidationId(validationId);		
			

		}
		catch(ISOException isoexp){
			posGWAccountReversalDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		return posGWAccountReversalDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO)
	{
		POSGWAccountReversalDTO posGWAccountReversalDTO = null;

		try
		{
			if (posMessageDTO != null)
			{
				posGWAccountReversalDTO = (POSGWAccountReversalDTO) posMessageDTO;
				requestISOMsg.set(IRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,posGWAccountReversalDTO.getAuthorizationNumber());
				requestISOMsg.set(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountReversalDTO.getResponseCode());
				requestISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_ID,posGWAccountReversalDTO.getTransactionId());
				POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountReversalDTO.getIrMessageDTO();
				if(posGWAccountReversalDTO.getValidationId() != null)
					requestISOMsg.set(IRPISOMessageFieldIDConstant.VALIDATION_ID,posGWAccountReversalDTO.getValidationId());
				if(posGWAccountReversalDTO.getOrderId() != null)
					requestISOMsg.set(IRPISOMessageFieldIDConstant.ORDER_ID,posGWAccountReversalDTO.getOrderId());

				if(IRPMessageTypeIDConstant.IRP_ASYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID.equals(posGWAccountReversalDTO.getProcessingCode()) || IRPMessageTypeIDConstant.IRP_ASYNC_REVERSAL_INQUIRY_PROCESSING_CODE_ID.equals(posGWAccountReversalDTO.getProcessingCode())){
					requestISOMsg.set(IRPISOMessageFieldIDConstant.RECHARGE_TRANSACTION_RESPONSE_CODE_ID,posGWAccountReversalDTO.getTransactionStatus()); // PID:600000 or PID:700000 
				}
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

		logger.info("IRP Reversal MIN Processor- execute()........");
		ISOMsg responseISOMsg = requestISOMsg;
		long startTime = 0;
		long processTime = 0;
		String carrierId = "";
		String transStatus = "";
		String endDateTime = "";
		String startDateTime = "";
		String posTimeZone = (String)POSServerMgr.getPosTimeZone().get(ServerConfigConstant.POS_TIME_ZONE);
        startTime = System.currentTimeMillis();
        startDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		try{
        
		POSGWAccountReversalDTO posGWAccountReversalDTO =  (POSGWAccountReversalDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
		carrierId = posGWAccountReversalDTO.getCarrierId();
		endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
		if (posGWAccountReversalDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
		{
			  String status = isValidMessage(posGWAccountReversalDTO);
			  System.out.println("IN REVERESAL MIN PROCESSOR VALIDATE====="+status);
			  if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
			  {
				//  processTime += System.currentTimeMillis() - startTime;
				startTime = System.currentTimeMillis();
				  processTime += System.currentTimeMillis() - startTime;
				endDateTime = DateUtil.getTodaysDate(TimeZone.getTimeZone(posTimeZone),"MM/dd/yyyy HH:mm:ss.S");
							
				  posGWAccountReversalDTO = (POSGWAccountReversalDTO)processMessage(posGWAccountReversalDTO);
		//		  startTime = System.currentTimeMillis();
			  }
			  else
			  {
				  posGWAccountReversalDTO.setAuthorizationNumber(PlaceHolderDataConstant.AUTHORIZATION_NUMBER);
				  posGWAccountReversalDTO.setResponseCode(status);
			  }
		}

		responseISOMsg = packISOMsg(requestISOMsg,posGWAccountReversalDTO);
		
		if(posGWAccountReversalDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.SUCCESS))
			transStatus = "SUCCESS";
		/*else if(posGWAccountReversalDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.CHANNEL_MANAGER_UNAVAILABLE) 
				|| posGWAccountReversalDTO.getResponseCode().equalsIgnoreCase(ResponseCodes.SYSTEM_ERROR))
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

			logger.error("IRPReversalMINProcessor-Exception at execute(): "+exp.getMessage());
			}
			catch(Exception ex){
				logger.error("IRPReversalMINProcessor-Exception at execute(): "+exp.getMessage());

			}
		}
        finally{
        	AppMonitorWrapper.logEvent("POSGW_SERVICE_REQUEST",
				  	"|"+requestISOMsg.getString(IRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER).substring(26).trim()
				   +"|"+"REVERSAL"
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

			//respISOMsg.unset(IRPISOMessageFieldIDConstant.TRANSACTION_ID);
			//respISOMsg.recalcBitMap();

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
        	logger.error("IRPReversalMINProcessor-Exception at updateSendMsgStatus(): "+exp.getMessage());

        }
	}
}
