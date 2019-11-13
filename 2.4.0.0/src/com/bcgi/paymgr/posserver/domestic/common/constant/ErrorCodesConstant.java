package com.bcgi.paymgr.posserver.domestic.common.constant;
import java.util.HashMap;
public class ErrorCodesConstant {

		
		//DOMESTIC ERROR CODES used to display on the Rejected Transaction screen.	
		public static final String INVALID_TRANSACTION_AMOUNT 							= "E-350";
		public static final String SUSPENDED_SUBSCRIBER_NUMBER							= "E-352";
		public static final String INVALID_SUBSCRIBER_NUMBER							= "E-450";
		public static final String SUBSCRIBER_NOT_EXISTS								= "E-451";
		public static final String SUBSCRIBER_FRAUD_DETECTED							= "E-452";
		public static final String RECHARGE_SYSTEM_NOT_AVAILABLE 						= "E-453";
		public static final String GENERAL_ERROR_INCORRECT_MESSAGE						= "E-454";
		public static final String ALL_MANDATORY_FIELDS_NOT_PRESENT						= "E-455";
		public static final String INVALID_DISTRIBUTOR_ID								= "E-456";
		public static final String REVERSAL_TRANSACTION_NOT_WITH_IN_TIMELIMITS			= "E-457";
		public static final String ORIGINAL_TRANSACTION_DOES_NOT_EXIST					= "E-458";
		public static final String TRANSACTION_ALREADY_REVERSED 						= "E-459";
		public static final String INVALID_MERCHANT_ID			 						= "E-460";
		
		public static final String SUCCESS										= "00";
		public static final String PHONE_NOT_EXIST								= "01";
		public static final String INACTIVE_PHONE								= "02";
		public static final String INACTIVE_DENOM								= "05";
		public static final String DUPLICATE_TRANSACTION						= "10";
		public static final String INCORRECT_VALUE								= "11";
		public static final String TRANS_NOT_EXIST_FOR_REVERSING				= "20";
		public static final String TRANS_PREVIOUSLY_REVESED						= "21";
		public static final String MNTS_ALREADY_CONSUMED_CANNOT_BE_REVERSED		= "22";
		//public static final String GENERAL_ERROR_INCORRECT_MESSAGE				= "98";
		public static final String GENERAL_ERROR_SYSTEM_NOT_AVAILABLE			= "99";
		public static final String PARTNER_INVALID								= "97";
		public static final String SUBSCRIBER_SUSPENDED							= "23";
		public static final String AIRERS_FRAUD_DETECTED						= "25";
		public static final String REJECTED										= "07";

		private static ErrorCodesConstant constant = null;
		private HashMap constantMap = new HashMap();
		
		private ErrorCodesConstant()
		{
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			constantMap.put("","");
			
		}
		
		public static ErrorCodesConstant getInstance(){
			if (constant == null){
				constant = new ErrorCodesConstant();
			}
			
			return constant;
		}
	

}
