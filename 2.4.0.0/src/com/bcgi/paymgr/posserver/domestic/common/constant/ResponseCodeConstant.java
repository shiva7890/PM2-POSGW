package com.bcgi.paymgr.posserver.domestic.common.constant;
public class ResponseCodeConstant {
	
	//FL
	public static final String INITIAL_STATE = "-1";
	
	public static final String SUCCESS  = "00";
	public static final String INVALID_SUBSCRIBER_NUMBER = "01";
	
	//FL
	public static final String REQUEST_MESSAGE_NOT_FORMATTED_CORRECT = "03";
   //FL
	public static final String POSGATEWAY_SYSTEM_ERROR  = "04";
	
	public static final String INVALID_PROCESSING_CODE  = "06";
	//FL
	public static final String INVALID_FORMAT_FOR_TRANSACTION_AMOUNT = "11";
	//FL
	public static final String INVALID_FORMAT_FOR_SUBSCRIBER_NUMBER = "16";
	//FL
	public static final String INVALID_FORMAT_FOR_AUTHORIZATION_NUMBER= "19";
	//FL
	public static final String INVALID_FORMAT_FOR_REFERENCE_NUMBER = "31";
	
	public static final String INVALID_FORMAT_FOR_RECHARGE_ORDER_ID = "61";
	
	public static final String INVALID_FORMAT_FOR_REVERSAL_ORDER_ID = "63";
	
	public static final String INVALID_FORMAT_FOR_VALIDATION_ID = "64";
		//FL
	public static final String INVALID_FORMAT_FOR_SALES_PERSON_ID = "92";
	//FL
	public static final String INVALID_FORMAT_FOR_DISTRIBUTOR_ID = "96";
//	FL
	public static final String INVALID_FORMAT_FOR_SUB_DISTRIBUTOR_ID = "94";
	//FL
	public static final String PAYMENTMANAGER_SYSTEM_ERROR =  "98";
	
    //FL
	public static final String PAYMENTMANAGER_NOT_AVAILABLE =  "99";
	//FL
	public static final String INTERNAL_SUCCESS_STATUS = "100";
	
	public static final String INVALID_TRANSACTION_DATE = "46";
	
	public static final String INVALID_TRANSACTION_TIME = "47";
	//ADDED on 22-2-08 for rev optional field validation
	public static final String ALL_REV_OPTIONAL_FILEDS_NOT_PRESENT="67";
	
	public static final String TRANSACTION_DATE_REQUIRED="45";
	
	public static final String TRANSACTION_TIME_REQUIRED="49";
	
	//newly added for Columbia
	public static final String INVALID_FORMAT_FOR_MODALITY = "07";
	public static final String INVALID_FORMAT_FOR_CARD_ACCEPTOR_NAME = "09";
	public static final String INVALID_FORMAT_FOR_CURRENCY_CODE = "36";
	public static final String INVALID_NETWORK_INFO_CODE = "08";
	public static final String INVALID_TRANSMISSION_DATE_TIME  = "48";
	
	// Newly Added For Panama on 05-Jul-2011
	public static final String INVALID_FORMAT_FOR_SYSTEM_TRACE_AUDIT_NUMBER  = "66";
	public static final String INVALID_FORMAT_FOR_CAPTURE_DATE  = "65";
	public static final String INVALID_FORMAT_FOR_SETTLEMENT_DATE  = "51";
	public static final String INVALID_FORMAT_FOR_ACQUIRING_INST_IDENTIFICATION_CODE  = "52";
	public static final String INVALID_FORMAT_FOR_RESPONSE_CODE  = "53";
	public static final String INVALID_FORMAT_FOR_ORIGINAL_DATA_ELEMENTS  = "54";
	public static final String INVALID_FORMAT_FOR_RECEIVING_INSTITUTION_DATA = "55";	
	public static final String INVALID_FORMAT_FOR_ADDITIONAL_DATA = "56";	
	public static final String INVALID_FORMAT_FOR_TRACK2DATA = "57";	
	public static final String INVALID_FORMAT_FOR_ATM_TERMINAL_DATA = "58";
	public static final String INVALID_FORMAT_FOR_CARD_ISSUER_DATA = "59";
	public static final String INVALID_FORMAT_FOR_ACCOUNT_IDENTIFICATION = "60";
	public static final String INVALID_FORMAT_FOR_TOKEN = "62";
	
	
	// newly added for coupon module mergining
    public static final String INVALID_COUPON="35";
	public static final String INVALID_FORMAT_FOR_PIN_PURCHASE="76";
	public static final String INVALID_PACKET_CODE="50";
	public static final String INVALID_FORMAT_FOR_PRODUCT_CODE = "68";
	public static final String INVALID_FORMAT_FOR_QUANTITY = "69";
}
