package com.bcgi.paymgr.posserver.irp.constant;

public class IRPMessageTypeIDConstant {

	public static final String ECHO_MSG_TYPE_ID   	= "0800";
	public static final String RECHARGE_MSG_TYPE_ID	= "0200";
	public static final String REVERSAL_MSG_TYPE_ID	= "0400";
	public static final String TRANSACTION_STATUS_INQUIRY_MSG_TYPE_ID	= "0420";
	
	// NEW
	public static final String ASYNC_RECHARGE_INQUIRY_TRANS_ID	= "0220";
		
	public static final String IRP_MIN_RECHARGE_PROCESSING_CODE_ID	= "180000";
	public static final String IRP_MIN_REVERSAL_PROCESSING_CODE_ID	= "200000";
	public static final String IRP_TRANS_STATUS_INQUIRY_PROCESSING_CODE_ID	= "660000";
	
	public static final String IRP_SYNC_MIN_VALIDATION_PROCESSING_CODE_ID	= "100000";
	public static final String IRP_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID	= "300000";
	public static final String IRP_ASYNC_UNIFIED_RECHARGE_PROCESSING_CODE_ID	= "350000";
	public static final String IRP_ASYNC_MIN_REVERSAL_PROCESSING_CODE_ID	= "500000";	
	public static final String IRP_ASYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID	= "600000";
	public static final String IRP_ASYNC_REVERSAL_INQUIRY_PROCESSING_CODE_ID	= "700000";	
}
