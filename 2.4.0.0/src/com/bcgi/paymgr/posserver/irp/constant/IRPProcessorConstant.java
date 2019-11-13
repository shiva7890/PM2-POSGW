package com.bcgi.paymgr.posserver.irp.constant;

public class IRPProcessorConstant {


	public static final String IRP_ECHO_PROCESSOR_ID              =           "0800";
	public static final String IRP_RECHARGE_MIN_PROCESSOR_ID      =           "0200_180000";
	public static final String IRP_IRP_TRANS_STATUS_INQUIRY_PROCESSOR_ID  =   "0420_660000";
	public static final String IRP_MIN_REVERSAL_PROCESSOR_ID  	   =          "0400_200000";

	// For Async changes
	
	public static final String IRP_SYNC_RECHARGE_VALIDATOR_PROCESSOR_ID = "0200_100000";
	public static final String IRP_ASYNC_RECHARGE_PROCESSOR_ID          = "0200_300000";
	public static final String IRP_ASYNC_RECHARGE_UNIFIED_PROCESSOR_ID  = "0200_350000";
	public static final String IRP_ASYNC_REVERSAL_PROCESSOR_ID  	     = "0400_500000";
	public static final String IRP_ASYNC_RECHARGE_INQUIRY_ID  	        = "0220_600000";
	public static final String IRP_ASYNC_REVERSAL_INQUIRY_ID  	        = "0420_700000";	
	
	//public static final String IRP_ASYNC_RECHARGE_UNIFIED_PROCESSOR_CLASS  = "com.bcgi.paymgr.posserver.irp.processor.IRPAsyncRechargeMINProcessor";
	
	public static final String IRP_ECHO_PROCESSOR_CLASS_NAME      = "com.bcgi.paymgr.posserver.irp.processor.IRPEchoProcessor";
	public static final String IRP_RECHARGE_MIN_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.irp.processor.IRPRechargeMINProcessor";
	public static final String IRP_IRP_TRANS_STATUS_INQUIRY_PROCESSOR_CLASS_NAME    = "com.bcgi.paymgr.posserver.irp.processor.IRPTransStatusInquiryProcessor";
	public static final String IRP_MIN_REVERSAL_PROCESSOR_CLASS_NAME  	   = "com.bcgi.paymgr.posserver.irp.processor.IRPReversalMINProcessor";

}
