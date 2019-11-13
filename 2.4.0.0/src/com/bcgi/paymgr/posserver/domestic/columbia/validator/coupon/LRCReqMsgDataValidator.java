package com.bcgi.paymgr.posserver.domestic.columbia.validator.coupon;


import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;

public class LRCReqMsgDataValidator {
	
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRCReqMsgDataValidator.class);
	
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
		if (!PMISOFieldValidator.isAlphaNumeric(cardAcceptorName)) {
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

	/*Following method is used to validate the 48 Field in the input Request..
	 * It will checks only the value exist or Not
	 * No other validation is going to be performed... Added by Sridhar.Vemulapalli on 22-Feb-2012
	 * According to new Requirement*/
	
	public static String isCardAcceptorNameValid(String cardAcceptorName)
	{
		logger.info("Card Acceptor Name :"+cardAcceptorName);
		if (!PMISOFieldValidator.isValueExists(cardAcceptorName)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_CARD_ACCEPTOR_NAME;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}	
	/*Ends*/
	public static String isValidModality(String modality)
	{

		if (!PMISOFieldValidator.isValueExists(modality)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_MODALITY;
		}

		if (!PMISOFieldValidator.isNumericValue(modality)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_MODALITY;
		}
		
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
			
			return ResponseCodeConstant.TRANSACTION_DATE_REQUIRED;
		}
	
		if (!PMISOFieldValidator.isValidMMDDDate(captureDate))
			{
				return ResponseCodeConstant.INVALID_TRANSACTION_DATE;
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
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

/*	commented for columbia , system trace number may be zero's
 	if (!PMISOFieldValidator.isPositiveValue(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}
*/		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidRRN1(String referenceNumber1)
	{
		if (!PMISOFieldValidator.isValueExists(referenceNumber1)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}
		
		if (!PMISOFieldValidator.isAlphaNumeric(referenceNumber1)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SALES_PERSON_ID;
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


		if (!PMISOFieldValidator.isAlphaNumeric(authorizationNumber)){
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
		if (!PMISOFieldValidator.isAlphaNumeric(salesPersonId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SALES_PERSON_ID;
		}
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

	public static String isValidSubDistributorIdForRechargeORReversal(String subDistributorId)
	{
		if (!PMISOFieldValidator.isValueExists(subDistributorId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUB_DISTRIBUTOR_ID;
		}
		if (PMISOFieldValidator.isAllZeroes(subDistributorId)){
			logger.info("===============Subdistributor ID is all Zeros ==============>");
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUB_DISTRIBUTOR_ID;
		}
		if (!PMISOFieldValidator.isAlphaNumeric(subDistributorId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUB_DISTRIBUTOR_ID;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidCouponNumber(String couponNumber)
	{

		if (!PMISOFieldValidator.isValueExists(couponNumber)){
			return ResponseCodeConstant.INVALID_COUPON;
		}
		if (!PMISOFieldValidator.isAlphaNumeric(couponNumber)){
			return ResponseCodeConstant.INVALID_COUPON;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	public static String isValidProductCode(String productCode)
	{
		if (!PMISOFieldValidator.isValueExists(productCode)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_PRODUCT_CODE;
		}

		if (!PMISOFieldValidator.isAlphaNumeric(productCode)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_PRODUCT_CODE;
		}

		if (PMISOFieldValidator.isAllZeroes(productCode)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_PRODUCT_CODE;
		}
		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

	}	
	public static String isValidQuantity(String quantity)
	{
		
		if (!PMISOFieldValidator.isValueExists(quantity)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_QUANTITY;
		}

		if (!PMISOFieldValidator.isNumericValue(quantity)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_QUANTITY;
		}

		if (PMISOFieldValidator.isAllZeroes(quantity)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_QUANTITY;
		}
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

	}

}
