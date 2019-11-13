package com.bcgi.paymgr.posserver.domestic.columbia.constant;

public class LRCISOMessageFieldIDConstant {
	public static final int MESSAGE_TYPE_ID = 0;
	public static final int TRANSACTION_AMNT = 4;
	public static final int SYSTEM_TRACE_AUDIT_NUMBER = 11;
	public static final int AUTHORIZATION_NUMBER = 38;
	public static final int RESPONSE_CODE_ID = 39;
	public static final int STORE_MANAGER_ID = 41;
	public static final int PROCESSING_CODE_ID = 3;
	public static final int DEST_TRANSACTION_AMNT = 5;
	public static final int TRANSACTION_DATE_TIME = 7;
	public static final int EXPIRATION_DATE = 14;
	public static final int CAPTURE_DATE = 17;
	public static final int POS_ENTRY_MODE = 22;
	public static final int MODALITY = 25;
	public static final int DISTRIBUTOR_ID = 32;
	public static final int RETRIEVAL_REFERENCE_NUMBER_1 = 37;
	public static final int RECHARGE_TRANSACTION_RESPONSE_CODE_ID = 40;
	public static final int SUBAGENT_ID = 42;
	public static final int SUBSSCRIBER_NUMBER = 43;
	public static final int PRODUCT_ID= 46;
	public static final int QUANTITY=47;
	/*
	 Allows the identification of a region, country, and Sale Point branch.
		For Mundo Movistar a fixed value with this structure
		1-3  Length prefix
		4-29 Address
		30-42 City
		43-44 State
		45-47 Country
	 */
	public static final int CARD_ACCEPTOR_NAME = 48;
	
	/*
	 Trans Initiated/originated Currency Code
     The standard ISO4217:2004 must be used
	 */
	public static final int CURRENCY_CODE = 49;
	
	public static final int CONVERSION_CURRENCY_CODE = 50;
	public static final int PIN_NUMBER = 54;
	public static final int RETRIEVAL_REFERENCE_NUMBER_2 = 57;
	public static final int RECHARGE_REFERENCE_NUMBER = 61;
	public static final int PACKET_CODE=60;
	public static final int RECHARGE_AUTH_NUMBER = 62;
	public static final int NETWORK_MANAGEMENT_CODE = 70;
	public static final int TRANSACTION_ID = 100;
	public static final int MAC = 128;
	

	/*Below Code is used to specify the Character to Suppress from the 41 and 42 Fields
	 * And to specify the wether we need to supress/Not from the 41 and 42 Fields
	 * Changed and Added by Sridhar.Vemulapalli on 23-Nov-2011 to full fill the REQ 1083 
	 */
		public static String  COLUMBIA_FIELD_SUPRESS_CHAR="0";
		public static String  COLUMBIA_FIELD_SUPRESS_FLAG="NO";
	//Ends
		
 public static final int PIN_NUMBER1=888;	
}
