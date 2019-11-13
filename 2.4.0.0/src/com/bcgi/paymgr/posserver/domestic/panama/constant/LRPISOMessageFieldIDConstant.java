package com.bcgi.paymgr.posserver.domestic.panama.constant;

public class LRPISOMessageFieldIDConstant {
	public static final int MESSAGE_TYPE_ID = 0;
	public static final int SUBSCRIBER_ID1 = 2;
	public static final int PROCESSING_CODE_ID = 3;
	public static final int TRANSACTION_AMNT = 4;	
	public static final int TRANSACTION_DATE_TIME = 7;
	public static final int SYSTEM_TRACE_AUDIT_NUMBER = 11;
	public static final int LOCAL_TRANSACTION_TIME = 12;
	public static final int LOCAL_TRANSACTION_DATE = 13;
	public static final int SETTLEMENT_DATE = 15;
	public static final int CAPTURE_DATE = 17;
	public static final int DISTRIBUTOR_ID = 32;
	public static final int TRACK2_DATA = 35;
	public static final int RETRIEVAL_REFERENCE_NUMBER = 37;
	public static final int AUTHORIZATION_NUMBER = 38;	
	public static final int RESPONSE_CODE_ID = 39;	
	public static final int STORE_ID = 41;	
	public static final int CARD_ACCEPTOR_NAME = 43;
	public static final int ATM_ADDITIONAL_RESP_DATA = 44;	
	public static final int PACKET_CODE=46;
	public static final int ADDITIONAL_DATA = 48;
	public static final int CURRENCY_CODE = 49;
	public static final int ATM_TERMINAL_DATA = 60;
	public static final int CARD_ISSUER = 61;	
	public static final int NETWORK_MANAGEMENT_CODE = 70;
	public static final int ORIGINAL_DATA = 90;	
	public static final int RECVING_INSTITUTION_CODE = 100;
	public static final int ACCNT_ID = 102;	
	public static final int TOKEN = 126;
	// Subscriber Number is available in Field 126 Need to Cut the from that Field
	public static final int SUBSSCRIBER_NUMBER = 126;	
}
