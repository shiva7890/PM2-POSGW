package com.bcgi.paymgr.posserver.domestic.ecuador.validator;


import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import  com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;

public class DMReqMsgDataValidator {
	
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(DMReqMsgDataValidator.class);

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
	
	
	 //Domestic
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
		//In domestic GW amount should be in integral values no fraction part values are allowed 
		//(ex: $5.50 is not allowed in Domestic GW) Added by Srinivas P on 17th Oct 2006.
		/*
		 * Changed for the Recharge-Decimal-support RAMP#5247 Note: According to this Domestic should support 2 decimal points in
		 * the amount, So validation removed By Srinivas P on 10th May 2007.
		 *  if (! PMISOFieldValidator.isValidPaymentAmount(transactionAmount)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}*/
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidSystemTraceAuditNumber(String referenceNumber)
	{

		if (!PMISOFieldValidator.isValueExists(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.isPositiveValue(referenceNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(referenceNumber)){
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

		if (!PMISOFieldValidator.isValueExists(authorizationNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(authorizationNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}

		if (!PMISOFieldValidator.isPositiveValue(authorizationNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(authorizationNumber)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER;
		}
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
			

	public static String isValidSalesPersonId(String salesPersonId)
	{
		if (!PMISOFieldValidator.isValueExists(salesPersonId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SALES_PERSON_ID;
		}
		//Below alphanumeric validation is added for Defects: #17061 
		if (!PMISOFieldValidator.isAlphaNumeric(salesPersonId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SALES_PERSON_ID;
		}
	   return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	
	public static String isValidSubDistributorId(String subDistributorId)
	{

		if (!PMISOFieldValidator.isAlphaNumeric(subDistributorId)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_SUB_DISTRIBUTOR_ID;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
	public static String isValidCurrencyCode(String currencyCode)
	{
		if (!PMISOFieldValidator.isAlphaNumeric(currencyCode)){
			return ResponseCodeConstant.INVALID_FORMAT_FOR_CURRENCY_CODE;
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
	
	public static String isValidPacketCode(String packetCode,String processingCode)
	{
		System.out.println("packetCode:::::and ProcessingCode :>>>>"+processingCode);
		/*if(DMMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID.equals(processingCode)
				||DMMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)
				||DMMessageTypeIDConstant.UNIFIED_ASYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID.equals(processingCode)
				||DMMessageTypeIDConstant.UNIFIED_ASYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)
				|| DMMessageTypeIDConstant.SYNC_MIN_PKT_VALIDATION_PROCESSING_CODE_ID.equals(processingCode)
				|| DMMessageTypeIDConstant.SYNC_MIN_PKT_VALIDATION_PROCESSING_CODE_ID_TUENTI.equals(processingCode))
		{*/
			if((!PMISOFieldValidator.isValueExists(packetCode))) 
			{
			   return ResponseCodeConstant.INVALID_PACKET_CODE;
		    }
			if (!PMISOFieldValidator.isAlphaNumeric_GTQ(packetCode)){
				return ResponseCodeConstant.INVALID_PACKET_CODE;
			}
		//}
			return ResponseCodes.INTERNAL_SUCCESS_STATUS;
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
