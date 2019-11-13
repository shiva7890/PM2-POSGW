package com.bcgi.paymgr.posserver.irp.validator;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import  com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;


public class IRPReqMsgDataValidator {
	
	public static String isValidTransactionTime(String transactionTime)
	{
		if (!PMISOFieldValidator.isValueExists(transactionTime)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATETIME;
		}

		if (!PMISOFieldValidator.isNumericValue(transactionTime)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATETIME;
		}

		if (!PMISOFieldValidator.isValidTime(transactionTime)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATETIME;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidCaptureMonthDay(String captureDate)
	{
		if (!PMISOFieldValidator.isValueExists(captureDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_CAPTURE_DATE;
		}

		if (!PMISOFieldValidator.isNumericValue(captureDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_CAPTURE_DATE;
		}
	
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	
	
	public static String isValidSystemTraceAuditNumber(String sysTraceAuditNum)
	{
		if (!PMISOFieldValidator.isValueExists(sysTraceAuditNum)){
			return ResponseCodes.INVALID_FORMAT_FOR_SYTEM_TRACE_AUDIT_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(sysTraceAuditNum)){
			return ResponseCodes.INVALID_FORMAT_FOR_SYTEM_TRACE_AUDIT_NUMBER;
		}
	

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidCardAcceptorName(String cardAcceptorName)
	{
		if (!PMISOFieldValidator.isValueExists(cardAcceptorName)){
			return ResponseCodes.INVALID_FORMAT_FOR_CARD_ACCEPTOR_NAME;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidSubscriberNumber(String subscriberMIN)
	{
		System.out.println("subscriberMIN====>"+subscriberMIN);

		if (!PMISOFieldValidator.isValueExists(subscriberMIN))
		{
			return ResponseCodes.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
		}

		System.out.println("subscriberMIN==111==>"+subscriberMIN);

		if (!PMISOFieldValidator.isNumericValue(subscriberMIN))
		{
			return ResponseCodes.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
		}

		/*
		if (!PMISOFieldValidator.isSubscriberNumValidLength(subscriberMIN,IRPMessageDataConstant.SUBSCRIBER_NUM_LENGTH))
		{
			return ResponseCodes.INVALID_SUBSCRIBER_NUMBER;
		}
		*/

		if (PMISOFieldValidator.isAllZeroes(subscriberMIN))
		{
			return ResponseCodes.INVALID_SUBSCRIBER_NUMBER;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidIRPSubscriberNumber(String subscriberMIN)
	{
		System.out.println("subscriberMIN=1===>"+subscriberMIN);

		if (!PMISOFieldValidator.isValueExists(subscriberMIN))
		{
			return ResponseCodes.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
		}

		System.out.println("subscriberMIN==2==>"+subscriberMIN);

		if (!PMISOFieldValidator.isNumericValue(subscriberMIN))
		{
			return ResponseCodes.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
		}
		System.out.println("subscriberMIN==3==>"+subscriberMIN);

		if (!PMISOFieldValidator.isSubscriberNumMinimumValidLength(subscriberMIN,IRPMessageDataConstant.MIN_IRP_SUBSCRIBER_NUM_LENGTH))
		{
			return ResponseCodes.INVALID_SUBSCRIBER_NUMBER;
		}
		System.out.println("subscriberMIN==4==>"+subscriberMIN);

		if (PMISOFieldValidator.isAllZeroes(subscriberMIN))
		{
			return ResponseCodes.INVALID_SUBSCRIBER_NUMBER;
		}
		System.out.println("subscriberMIN==5==>"+subscriberMIN);

		String digitsBeforeCountryCode = subscriberMIN.substring(0,IRPMessageDataConstant.COUNTRYCODE_START_POSITION);
		if (!PMISOFieldValidator.isAllZeroes(digitsBeforeCountryCode))
		{
			return ResponseCodes.INVALID_SUBSCRIBER_NUMBER;
		}
		

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidTransactionAmount(String transactionAmount)
	{

		if (!PMISOFieldValidator.isValueExists(transactionAmount)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}

		if (!PMISOFieldValidator.isNumericValue(transactionAmount)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}

		if (!PMISOFieldValidator.isPositiveValue(transactionAmount)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}

		if (PMISOFieldValidator.isAllZeroes(transactionAmount)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidReferenceNumber(String referenceNumber1,String referenceNumber2)
	{
		if (PMISOFieldValidator.isValueExists(referenceNumber1))
		{
		
	    	if (PMISOFieldValidator.isAllZeroes(referenceNumber1))
            {
	    		if (!PMISOFieldValidator.isValueExists(referenceNumber2)){
	    			return ResponseCodes.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
	    		}
	    		else{
	    			if (!PMISOFieldValidator.isNumericValue(referenceNumber2)){
	    				return ResponseCodes.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
	    			}
	    		}
	    	}
		}
	 	else
    	{
    		return ResponseCodes.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

    	

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidReferenceNumberAuthorizatonNumber(String referenceNumber1,String referenceNumber2,String authorizationNumber)
	{
		if (PMISOFieldValidator.isValueExists(referenceNumber1))
		{
		
	    	if (PMISOFieldValidator.isAllZeroes(referenceNumber1))
            {
	    		if (!PMISOFieldValidator.isValueExists(referenceNumber2)){
	    			if (!PMISOFieldValidator.isValueExists(authorizationNumber)){
	    				return ResponseCodes.INVALID_FORMAT_FOR_IRP_RECHARGE_REFERENCE_NUMBER;
	    			}
	    	    			
	    		}
	    		else{
	    			if (!PMISOFieldValidator.isNumericValue(referenceNumber2)){
	    				return ResponseCodes.INVALID_FORMAT_FOR_IRP_RECHARGE_REFERENCE_NUMBER;
	    			}
	    		}
	    	}
		}
	 	else
    	{
    		return ResponseCodes.INVALID_FORMAT_FOR_IRP_RECHARGE_REFERENCE_NUMBER;
		}

    	

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidReferenceNumberAuthorizatonNumber(String referenceNumber,String authorizationNumber)
	{
		if (PMISOFieldValidator.isValueExists(referenceNumber))
		{
		
	    	if (PMISOFieldValidator.isAllZeroes(referenceNumber))
            {
	    		if (!PMISOFieldValidator.isValueExists(authorizationNumber))
	    		{
	    				return ResponseCodes.INVALID_FORMAT_FOR_IRP_RECHARGE_REFERENCE_NUMBER;
	      		}
	    		
	    	}
		}
	 	else
    	{
    		return ResponseCodes.INVALID_FORMAT_FOR_IRP_RECHARGE_REFERENCE_NUMBER;
		}

    	

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	
	
	public static String isValidTransactionDateTime(String transactionDateTime)
	{
		if (!PMISOFieldValidator.isValueExists(transactionDateTime)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATETIME;
		}

		if (!PMISOFieldValidator.isNumericValue(transactionDateTime)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATETIME;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidTransactionDate(String transactionDate)
	{
		
		if (!PMISOFieldValidator.isValueExists(transactionDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATETIME;
		}
		
		if (!PMISOFieldValidator.isNumericValue(transactionDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATETIME;
		}
		
		if (!PMISOFieldValidator.isValidDate(transactionDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATETIME;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}
	
	public static String isValidCaptureDate(String captureDate)
	{

		if (!PMISOFieldValidator.isValueExists(captureDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_CAPTURE_DATE;
		}

		if (!PMISOFieldValidator.isNumericValue(captureDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_CAPTURE_DATE;
		}

		if (!PMISOFieldValidator.isValidDate(captureDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_CAPTURE_DATE;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}
	public static String isValidDateTime(String transactionDateTime)
	{
		if (!PMISOFieldValidator.isBelowSysDate(transactionDateTime,"yyMMddHHmmss")){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATE;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}


	public static String isValidDistributorId(String distributorId)
	{

		if (!PMISOFieldValidator.isValueExists(distributorId)){
			return ResponseCodes.INVALID_FORMAT_FOR_DISTRIBUTOR_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(distributorId)){
			return ResponseCodes.INVALID_FORMAT_FOR_DISTRIBUTOR_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(distributorId)){
			return ResponseCodes.INVALID_FORMAT_FOR_DISTRIBUTOR_ID;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}
	
	public static String isValidNetworkMangementInfoCode(String networkMgmtCode)
	{

		if (!PMISOFieldValidator.isValueExists(networkMgmtCode)){
			return ResponseCodes.INVALID_FORMAT_FOR_NETWORK_MGMT_CODE;
		}

		if (!PMISOFieldValidator.isNumericValue(networkMgmtCode)){
			return ResponseCodes.INVALID_FORMAT_FOR_NETWORK_MGMT_CODE;
		}

		
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}
	
	public static String isValidPOSEntryMode(String posEntryMode)
	{
		if (!PMISOFieldValidator.isValueExists(posEntryMode)){
			return ResponseCodes.INVALID_FORMAT_FOR_POS_ENTRY_MODE;
		}

		if (!PMISOFieldValidator.isNumericValue(posEntryMode)){
			return ResponseCodes.INVALID_FORMAT_FOR_POS_ENTRY_MODE;
		}
	
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}
	
	public static String isValidOriginCurrencyCode(String originCurrencyCode)
	{
		if (!PMISOFieldValidator.isValueExists(originCurrencyCode)){
			return ResponseCodes.INVALID_FORMAT_FOR_CURRENCY_CODE;
		}

		if (!PMISOFieldValidator.isNumericValue(originCurrencyCode)){
			return ResponseCodes.INVALID_FORMAT_FOR_CURRENCY_CODE;
		}
	
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}
	
	public static String isValidRechargeReferenceNumber(String rechargeRefNumber)
	{
		if (!PMISOFieldValidator.isValueExists(rechargeRefNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_IRP_RECHARGE_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(rechargeRefNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_IRP_RECHARGE_REFERENCE_NUMBER;
		}
	
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidModality(String modality)
	{

		if (!PMISOFieldValidator.isValueExists(modality)){
			return ResponseCodes.INVALID_FORMAT_FOR_MODALITY;
		}

		if (!PMISOFieldValidator.isNumericValue(modality)){
			return ResponseCodes.INVALID_FORMAT_FOR_MODALITY;
		}
		
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;

	}


	public static String isValidSubAgentId(String subAgentId)
	{
		if (!PMISOFieldValidator.isValueExists(subAgentId)){
			return ResponseCodes.INVALID_FORMAT_FOR_SUBAGENT_ID;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidStoreId(String storeId)
	{
		if (!PMISOFieldValidator.isValueExists(storeId)){
			return ResponseCodes.INVALID_FORMAT_FOR_STORE_ID;
		}
	   return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidRechargeReferenceNumer(String referenceNumber)
	{
		if (!PMISOFieldValidator.isValueExists(referenceNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(referenceNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_REFERENCE_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(referenceNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.is1_12AllZeroes(referenceNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_REFERENCE_NUMBER;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	public static String isValidValidationID(String validationId)
	{

		if (!PMISOFieldValidator.isValueExists(validationId)){
			return ResponseCodes.INVALID_FORMAT_FOR_VALIDATION_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(validationId)){
			return ResponseCodes.INVALID_FORMAT_FOR_VALIDATION_ID;
		}

		if (!PMISOFieldValidator.isPositiveValue(validationId)){
			return ResponseCodes.INVALID_FORMAT_FOR_VALIDATION_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(validationId)){
			return ResponseCodes.INVALID_FORMAT_FOR_VALIDATION_ID;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidOrderID(String orderId)
	{

		if (!PMISOFieldValidator.isValueExists(orderId)){
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_ORDER_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(orderId)){
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_ORDER_ID;
		}

		if (!PMISOFieldValidator.isPositiveValue(orderId)){
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_ORDER_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(orderId)){
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_ORDER_ID;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidReversalOrderID(String orderId)
	{

		if (!PMISOFieldValidator.isValueExists(orderId)){
			return ResponseCodes.INVALID_FORMAT_FOR_REVERSAL_ORDER_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(orderId)){
			return ResponseCodes.INVALID_FORMAT_FOR_REVERSAL_ORDER_ID;
		}

		if (!PMISOFieldValidator.isPositiveValue(orderId)){
			return ResponseCodes.INVALID_FORMAT_FOR_REVERSAL_ORDER_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(orderId)){
			return ResponseCodes.INVALID_FORMAT_FOR_REVERSAL_ORDER_ID;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

}
