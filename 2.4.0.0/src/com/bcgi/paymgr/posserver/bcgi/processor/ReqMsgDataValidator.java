package com.bcgi.paymgr.posserver.bcgi.processor;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import  com.bcgi.paymgr.posserver.common.constant.MessageDataConstant;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;

public class ReqMsgDataValidator {


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

		if (!PMISOFieldValidator.isSubscriberNumValidLength(subscriberMIN,MessageDataConstant.SUBSCRIBER_NUM_LENGTH))
		{
			return ResponseCodes.INVALID_SUBSCRIBER_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(subscriberMIN))
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

		if (!PMISOFieldValidator.isTransAmountWholeNumber(transactionAmount)){
			return ResponseCodes.TRANSACTION_AMOUNT_NOT_WHOLE_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(transactionAmount)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_AMOUNT;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidReferenceNumer(String referenceNumber)
	{
		if (!PMISOFieldValidator.isValueExists(referenceNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(referenceNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(referenceNumber)){
			return ResponseCodes.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidTransactionTime(String transactionTime)
	{
		if (!PMISOFieldValidator.isValueExists(transactionTime)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_TIME;
		}

		if (!PMISOFieldValidator.isNumericValue(transactionTime)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_TIME;
		}

		if (!PMISOFieldValidator.isValidTime(transactionTime)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_TIME;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidTransactionDate(String transactionDate)
	{

		if (!PMISOFieldValidator.isValueExists(transactionDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATE;
		}

		if (!PMISOFieldValidator.isNumericValue(transactionDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATE;
		}

		if (!PMISOFieldValidator.isValidDate(transactionDate)){
			return ResponseCodes.INVALID_FORMAT_FOR_TRANSACTION_DATE;
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

	public static String isValidSubAgentId(String subAgentId)
	{
		if (!PMISOFieldValidator.isValueExists(subAgentId)){
			return ResponseCodes.INVALID_FORMAT_FOR_SUBAGENT_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(subAgentId)){
			return ResponseCodes.INVALID_FORMAT_FOR_SUBAGENT_ID;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidStoreId(String storeId)
	{
		if (!PMISOFieldValidator.isValueExists(storeId)){
			return ResponseCodes.INVALID_FORMAT_FOR_STORE_ID;
		}

		if (PMISOFieldValidator.isAllZeroes(storeId)){
			return ResponseCodes.INVALID_FORMAT_FOR_STORE_ID;
		}

	   return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}

	public static String isValidRechargeReferenceNumer(String referenceNumber)
	{
		System.out.println("===referenceNumber@@@@======"+referenceNumber);
		if (!PMISOFieldValidator.isValueExists(referenceNumber)){
			System.out.println("===referenceNumber111======"+referenceNumber);
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.isNumericValue(referenceNumber)){
			System.out.println("===referenceNumber222======"+referenceNumber);
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_REFERENCE_NUMBER;
		}

		if (PMISOFieldValidator.isAllZeroes(referenceNumber)){
			System.out.println("===referenceNumber33======"+referenceNumber);
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_REFERENCE_NUMBER;
		}

		if (!PMISOFieldValidator.is1_12AllZeroes(referenceNumber)){
			System.out.println("===referenceNumber44======"+referenceNumber);
			return ResponseCodes.INVALID_FORMAT_FOR_RECHARGE_REFERENCE_NUMBER;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}


}
