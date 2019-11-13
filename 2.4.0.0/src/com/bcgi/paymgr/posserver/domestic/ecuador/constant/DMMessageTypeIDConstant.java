package com.bcgi.paymgr.posserver.domestic.ecuador.constant;

public class DMMessageTypeIDConstant {

	public static final String ECHO_MSG_TYPE_ID   	= "0800";
	public static final String RECHARGE_MSG_TYPE_ID	= "0200";
	public static final String REVERSAL_MSG_TYPE_ID	= "0400";
	public static final String RECHARGE_INQUIRY_MSG_TYPE_ID	= "0220";
	public static final String REVERSAL_INQUIRY_MSG_TYPE_ID	= "0420";
	
	public static final String ECHO_PROCESSING_CODE_ID	= "990000";
	public static final String SYNC_MIN_VALIDATION_PROCESSING_CODE_ID	= "100000";
	public static final String SYNC_MIN_PKT_VALIDATION_PROCESSING_CODE_ID = "100300";// altamira pkt Recharge Validation
	public static final String SYNC_MIN_PKT_VALIDATION_PROCESSING_CODE_ID_TUENTI = "100301";// tuenti pkt Recharge Validation
	public static final String SYNC_MIN_VALIDATION_PROCESSING_CODE_ID_TUENTI	= "100001"; // TUENTI Recharge Validation
	public static final String SYNC_MIN_RECHARGE_PROCESSING_CODE_ID	= "180000";
	public static final String SYNC_MIN_RECHARGE_PROCESSING_CODE_ID_TUENTI	= "180001";
	public static final String SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID	= "650300";
	public static final String SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI	= "650301";
	public static final String ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID	= "300000"; // phase1 altamira Recharge 
	public static final String ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID_TUENTI	= "300001"; // Tuenti Rechagre async phase 1
	public static final String UNIFIED_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID	= "350000"; // Altamira unified recharge
	public static final String UNIFIED_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID_TUENTI	= "350001"; // Tuenti unified recharge
	public static final String UNIFIED_ASYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID	= "350300";       // altamira PKT Unified Recharge  
	public static final String UNIFIED_ASYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI	= "350301";// Tuenti PKT unified recharge
	public static final String SYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID	= "600000";
	public static final String SYNC_REVERSAL_INQUIRY_PROCESSING_CODE_ID	= "700000";
	public static final String SYNC_REVERSAL_PROCESSING_CODE_ID	= "180000";
	public static final String SYNC_REVERSAL_PROCESSING_CODE_ID_TUENTI	= "180001";
	public static final String ASYNC_REVERSAL_PROCESSING_CODE_ID	= "500000";
	
	public static final String SYNC_PKT_REVERSAL_PROCESSING_CODE_ID	= "650300";
	public static final String SYNC_PKT_REVERSAL_PROCESSING_CODE_ID_TUENTI	= "650301";

	
	
	public static final String RECHARGE_CPN_REDEEMPTION_PROCESSING_ID= "185000";
	public static final String BRAND_CPN_REDEEMPTION_PROCESSING_ID	= "255000";
	public static final String ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSING_ID	= "355000";
	public static final String RECHARGE_CPN_INQUIRY_PROCESSING_ID = "605000";
	
	public static final String CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_ID= "105000";
	public static final String CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_ID= "305000";
	public static final String CPN_BRAND_ASYNC_VALIDATION_PROCESSING_ID= "105100";
	public static final String CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_ID= "305100";

	
	
	
}
