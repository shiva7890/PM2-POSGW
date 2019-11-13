package com.bcgi.paymgr.posserver.domestic.panama.validator;


import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPMessageTypeIDConstant;

public class LRPReqMsgDataValidator {
	
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRPReqMsgDataValidator.class);
	
	public static String isValidReferenceNumber(String referenceNumber1,String referenceNumber2)
	{
		if (PMISOFieldValidator.isValueExists(referenceNumber1))
		{
		
	    	if (PMISOFieldValidator.isAllZeroes(referenceNumber1))
            {
	    		if (!PMISOFieldValidator.isValueExists(referenceNumber2))  {
	    			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
	    		}
	    		else{
	    			if (!PMISOFieldValidator.isNumericValue(referenceNumber2)){
	    				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
	    			}
	    			
	    			if (PMISOFieldValidator.isAllZeroes(referenceNumber2)){
	    				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
	    			}
	    			
	    		}
	    	}
	    	else
	    	{
    			if (!PMISOFieldValidator.isAlphaNumeric(referenceNumber1)){
    				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
    			}

	    	}
		}
	 	else
    	{
    		return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

    	

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidReferenceNumberForOptional(String referenceNumber1,String referenceNumber2)
	{
	
		//I param exists or not
		if (PMISOFieldValidator.isValueExists(referenceNumber1))
		{
	    	if (PMISOFieldValidator.isAllZeroes(referenceNumber1))
            {
	    		if (PMISOFieldValidator.isValueExists(referenceNumber2)) {
	    			
					if (!PMISOFieldValidator.isNumericValue(referenceNumber2)){
						return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
					}
	    		}
	    	}
	    	else  {
	    			if (!PMISOFieldValidator.isAlphaNumeric(referenceNumber1)) {
	    				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
	    			}
	    	  }
		}
	 	else if (PMISOFieldValidator.isValueExists(referenceNumber2))   {
			if (!PMISOFieldValidator.isNumericValue(referenceNumber2)){
				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
			}
	 		
		}
    	

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}	
	
	public static String isValidOriginCurrencyCode(String originCurrencyCode)
	{
		if (!PMISOFieldValidator.isValueExists(originCurrencyCode)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_CURRENCY_CODE;
		}

		if (!PMISOFieldValidator.isNumericValue(originCurrencyCode)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_CURRENCY_CODE;
		}
	
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

	}

	public static String isValidCardAcceptorName(String cardAcceptorName)
	{
		logger.info("Card Acceptor Name :"+cardAcceptorName);
		if (!PMISOFieldValidator.isValueExists(cardAcceptorName)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_CARD_ACCEPTOR_NAME;
		}
		/*
		 if (!PMISOFieldValidator.isAlphaNumeric(cardAcceptorName)) {
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
		}
		*/
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidAdditionalData(String p_AdditionalData)
	{
		logger.info("Addtional Data :"+p_AdditionalData);
		if (!PMISOFieldValidator.isValueExists(p_AdditionalData)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_ADDITIONAL_DATA;
		}
		/*		
		 if (!PMISOFieldValidator.isAlphaNumeric(p_AdditionalData)) {
			return ResponseCodeConstant.INVALID_FORMAT_FOR_ADDITIONAL_DATA;
		}
		*/
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidatmTerminalData(String p_atmTerminalData)
	{
		logger.info("ATM Terminal Data :"+p_atmTerminalData);
		if (!PMISOFieldValidator.isValueExists(p_atmTerminalData)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_ATM_TERMINAL_DATA;
		}
		/*		
		if (!PMISOFieldValidator.isAlphaNumeric(p_atmTerminalData)) {
			return ResponseCodeConstant.INVALID_FORMAT_FOR_ATM_TERMINAL_DATA;
		}
		*/
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}	

	public static String isValidcardIssuer(String p_cardIssuer)
	{
		logger.info("Card Issuer :"+p_cardIssuer);
		if (!PMISOFieldValidator.isValueExists(p_cardIssuer)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_CARD_ISSUER_DATA;
		}
		/*		
		if (!PMISOFieldValidator.isAlphaNumeric(p_cardIssuer)) {
			return ResponseCodeConstant.INVALID_FORMAT_FOR_CARD_ISSUER_DATA;
		}
		*/
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidReceivingInstitution(String p_ReceivingInstitution)
	{
		if (!PMISOFieldValidator.isValueExists(p_ReceivingInstitution)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_RECEIVING_INSTITUTION_DATA;
		}

		if (!PMISOFieldValidator.isNumericValue(p_ReceivingInstitution)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_RECEIVING_INSTITUTION_DATA;
		}
	
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidAccountIdentification(String p_AccountIdentification)
	{
		logger.info("AccountIdentification :"+p_AccountIdentification);
		if (!PMISOFieldValidator.isValueExists(p_AccountIdentification)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_ACCOUNT_IDENTIFICATION;
		}
		/*		
		if (!PMISOFieldValidator.isAlphaNumeric(p_AccountIdentification)) {
			return ResponseCodeConstant.INVALID_FORMAT_FOR_ACCOUNT_IDENTIFICATION;
		}
		*/
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidToken(String p_Token)
	{
		logger.info("Token :"+p_Token);
		if (!PMISOFieldValidator.isValueExists(p_Token)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TOKEN;
		}
		/*		
		if (!PMISOFieldValidator.isAlphaNumeric(p_Token)) {
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TOKEN;
		}
		*/
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidDate(String date)
	{
		logger.info("date===============:"+date);
		//newly added
		if(!PMISOFieldValidator.isValueExists(date)) {
			
			return ResponseCodeConstant.TRANSACTION_DATE_REQUIRED;
		}
	
		if (!PMISOFieldValidator.isValidMMDDDate(date))
			{
				return ResponseCodeConstant.INVALID_TRANSACTION_DATE;
			}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	
		
	}
	public static String isValidDateTime(String dateTime)
	{
		logger.info("date===============:"+dateTime);
		//newly added
		if(!PMISOFieldValidator.isValueExists(dateTime)) {
			
			return ResponseCodeConstant.INVALID_TRANSMISSION_DATE_TIME;
		}
	
		if (!PMISOFieldValidator.isValidDateTime(dateTime))
			{
				return ResponseCodeConstant.INVALID_TRANSMISSION_DATE_TIME;
			}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
		
	}
	
	public static String isValidCaptureDate(String captureDate)
	{
		logger.info("date===============:"+captureDate);
		//newly added
		if(!PMISOFieldValidator.isValueExists(captureDate)) {
			
			return ResponseCodeConstant.INVALID_FORMAT_FOR_CAPTURE_DATE;
		}
	
		if (!PMISOFieldValidator.isValidMMDDDate(captureDate))
			{
				return ResponseCodeConstant.INVALID_FORMAT_FOR_CAPTURE_DATE;
			}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
		
	}
	
	public static String isValidTime(String time)
	{
		logger.info("time==============:"+time);
		//newly added
		if(!PMISOFieldValidator.isValueExists(time)) {
		
			return ResponseCodeConstant.TRANSACTION_TIME_REQUIRED;
		}
		
		if (!PMISOFieldValidator.isValidTime(time))
			{
				return ResponseCodeConstant.INVALID_TRANSACTION_TIME;
			}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
			
    //Domestic
	public static String isValidSubscriberNumber(String subscriberMIN)
	{
		logger.info("subscriberMIN:"+subscriberMIN);
		if (PMISOFieldValidator.isValueExists(subscriberMIN))
		{
			logger.info("inside if subscriberMIN:"+subscriberMIN);
			if (!PMISOFieldValidator.isAlphaNumeric(subscriberMIN))
			{
				logger.info("isNumericValue:"+subscriberMIN);
				return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
			}
						
	
			if (PMISOFieldValidator.isAllZeroes(subscriberMIN))
			{
				logger.info("isAllZeroes:"+subscriberMIN);
				return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
			}
			
			if (!PMISOFieldValidator.isPositiveValue(subscriberMIN))
			{
				logger.info("isPositiveValue:"+subscriberMIN);
				return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
			}
		}
		else
		{
			return ResponseCodeConstant.INVALID_SUBSCRIBER_NUMBER;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidSubscriberNumberExists(String subscriberMIN)
	{
		logger.info("subscriberMIN:"+subscriberMIN);
		if (PMISOFieldValidator.isValueExists(subscriberMIN))
		{
			logger.info("inside if subscriberMIN:"+subscriberMIN);
			if (!PMISOFieldValidator.isNumericValue(subscriberMIN))
			{
				logger.info("isNumericValue:"+subscriberMIN);
				return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
			}
						
	
			if (PMISOFieldValidator.isAllZeroes(subscriberMIN))
			{
				logger.info("isAllZeroes:"+subscriberMIN);
				return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
			}
			
			if (!PMISOFieldValidator.isPositiveValue(subscriberMIN))
			{
				logger.info("isPositiveValue:"+subscriberMIN);
				return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
			}
		}
		else {
			logger.info("isValueExists:"+subscriberMIN);
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	
	 /*
	  * 1. should exist because it is mandatory
	  * 2. should be numeric
	  * 3. should be positive
	  * 4. should not be 0's
	  */
	public static String isValidTransactionAmount(String transactionAmount)
	{

		if (!PMISOFieldValidator.isValueExists(transactionAmount)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}

		if (!PMISOFieldValidator.isNumericValue(transactionAmount)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}

		if (!PMISOFieldValidator.isPositiveValue(transactionAmount)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}

		if (PMISOFieldValidator.isAllZeroes(transactionAmount)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	/*
	  * 1. should exist because it is mandatory
	  * 2. should be numeric
     */
	public static String isValidSystemTraceAuditNumber(String referenceNumber)
	{

		if (!PMISOFieldValidator.isValueExists(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SYSTEM_TRACE_AUDIT_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SYSTEM_TRACE_AUDIT_NUMBER;
		}

		if (!PMISOFieldValidator.isPositiveValue(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SYSTEM_TRACE_AUDIT_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SYSTEM_TRACE_AUDIT_NUMBER;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidRRN1(String referenceNumber1)
	{
		logger.info("Retrival Reference Number ==================:"+referenceNumber1);
		if (!PMISOFieldValidator.isValueExists(referenceNumber1)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}
		
		if (!PMISOFieldValidator.isAlphaNumeric(referenceNumber1)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}
		

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	//New method added  by RajenderReddy on 06-01-08 for making the RRN field as optional for AsynchReversal Request
	public static String isValidAsynchReversalSystemTraceAuditNumber(String referenceNumber)
	{
		
			logger.info("isValidAsynchReversalSystemTraceAuditNumber=======>referenceNumber"+referenceNumber);
			
			
			/*if (!PMISOFieldValidator.isNumericValue(referenceNumber)){
				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
			}*/
	
			if (!PMISOFieldValidator.isPositiveValue(referenceNumber)){
				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
			}
	
			/*if (PMISOFieldValidator.isAllZeroes(referenceNumber)){
				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
			}*/
		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	
	public static String isValidValidationID(String validationId)
	{

		if (!PMISOFieldValidator.isValueExists(validationId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_VALIDATION_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(validationId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_VALIDATION_ID;
		}

		if (!PMISOFieldValidator.isPositiveValue(validationId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_VALIDATION_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(validationId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_VALIDATION_ID;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidOrderID(String orderId)
	{

		if (!PMISOFieldValidator.isValueExists(orderId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_RECHARGE_ORDER_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(orderId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_RECHARGE_ORDER_ID;
		}

		if (!PMISOFieldValidator.isPositiveValue(orderId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_RECHARGE_ORDER_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(orderId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_RECHARGE_ORDER_ID;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidReversalOrderID(String orderId)
	{

		if (!PMISOFieldValidator.isValueExists(orderId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REVERSAL_ORDER_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(orderId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REVERSAL_ORDER_ID;
		}

		if (!PMISOFieldValidator.isPositiveValue(orderId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REVERSAL_ORDER_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(orderId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REVERSAL_ORDER_ID;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidAuthorizationNumber(String authorizationNumber)
	{
		logger.info("Autherization Field ======>:" + authorizationNumber);
		if (!PMISOFieldValidator.isValueExists(authorizationNumber)){
			logger.info("Autherization Field Status======>:" + ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER);
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}

		if (!PMISOFieldValidator.isAlphaNumeric(authorizationNumber)){
			logger.info("Autherization Field Status======>:"+ ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER);
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}
/*
		if (!PMISOFieldValidator.isPositiveValue(authorizationNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}
		

		if (PMISOFieldValidator.isAllZeroes(authorizationNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}
*/		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
		
	public static String isValidDistributorId(String distributorId)
	{

		if (!PMISOFieldValidator.isValueExists(distributorId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_DISTRIBUTOR_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(distributorId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_DISTRIBUTOR_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(distributorId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_DISTRIBUTOR_ID;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

	}
			
	public static String isValidNetworkInfoCode(String nwInfoCode)
	{

		if (!PMISOFieldValidator.isValueExists(nwInfoCode)){
			return ResponseCodeConstant.INVALID_NETWORK_INFO_CODE;
		}

		if (!PMISOFieldValidator.isNumericValue(nwInfoCode)){
			return ResponseCodeConstant.INVALID_NETWORK_INFO_CODE;
		}

		if (PMISOFieldValidator.isAllZeroes(nwInfoCode)){
			return ResponseCodeConstant.INVALID_NETWORK_INFO_CODE;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

	}

	public static String isValidSalesPersonId(String salesPersonId)
	{
		if (!PMISOFieldValidator.isValueExists(salesPersonId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SALES_PERSON_ID;
		}
		/*
		 if (!PMISOFieldValidator.isAlphaNumeric(salesPersonId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SALES_PERSON_ID;
		}
		*/
	   return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidSubDistributorId(String subDistributorId)
	{
		if (!PMISOFieldValidator.isValueExists(subDistributorId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUB_DISTRIBUTOR_ID;
		}

		if (!PMISOFieldValidator.isAlphaNumeric(subDistributorId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUB_DISTRIBUTOR_ID;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidTrack2Data(String p_Track2Data){
		logger.info("Track2 Data =============:"+p_Track2Data);
		if (!PMISOFieldValidator.isValueExists(p_Track2Data)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TRACK2DATA;
		}

/*		if (!PMISOFieldValidator.isNumericDoubleValue(p_Track2Data)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TRACK2DATA;
		}*/
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	//start changes by ayub panama
	//Settlement Date (field 15)
	public static String isValidSetlmentDate(String setlmentDate){
		logger.info("SettleMent Date===============:"+setlmentDate);
		if(!PMISOFieldValidator.isValueExists(setlmentDate)) {
			
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SETTLEMENT_DATE;
		}
	
		if (!PMISOFieldValidator.isValidMMDDDate(setlmentDate))
			{
				return ResponseCodeConstant.INVALID_FORMAT_FOR_SETTLEMENT_DATE;
			}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
		
	}	

	//start changes by ayub panama
	public static String isValidResponseCode(String p_responseCode)
	{
		logger.info("Response Code=============:"+p_responseCode);
		if (!PMISOFieldValidator.isValueExists(p_responseCode)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_RESPONSE_CODE;
		}
		
		if (!PMISOFieldValidator.isAlphaNumeric(p_responseCode)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_RESPONSE_CODE;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidDataElement(String p_dataElement)
	{
		logger.info("Original Data Elements==================:"+p_dataElement);
		if (!PMISOFieldValidator.isValueExists(p_dataElement)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_ORIGINAL_DATA_ELEMENTS;
		}
		if (!PMISOFieldValidator.isNumericDoubleValue(p_dataElement)) {
			logger.info("In the If Statement of Numeric check ===============:");
			return ResponseCodeConstant.INVALID_FORMAT_FOR_ORIGINAL_DATA_ELEMENTS;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}	
	public static String isValidAuthorizationNum(String authorizationNumber)
	{
		logger.info("Autherization Field ======>:" + authorizationNumber);
		if (!PMISOFieldValidator.isValueExists(authorizationNumber)){
			logger.info("Autherization Field Status======>:" + ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER);
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericDoubleValue(authorizationNumber)){
			logger.info("Autherization Field Status======>:"+ ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER);
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}
/*
		if (!PMISOFieldValidator.isPositiveValue(authorizationNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}
		

		if (PMISOFieldValidator.isAllZeroes(authorizationNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}
*/		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}	
	
	
	public static String isValidPacketCode(String packetCode,String processingCode)
	{
		System.out.println("packetCode:::::and ProcessingCode :>>>>"+processingCode);
		if( LRPMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID.equals(processingCode)
				|| LRPMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)
				)
		{
			if((!PMISOFieldValidator.isValueExists(packetCode))) 
			{
			   return ResponseCodeConstant.INVALID_PACKET_CODE;
		    }
			if (!PMISOFieldValidator.isAlphaNumeric_GTQ(packetCode)){
				return ResponseCodeConstant.INVALID_PACKET_CODE;
			}
		}
			return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
}
