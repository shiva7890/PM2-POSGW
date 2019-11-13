package com.bcgi.paymgr.posserver.bcgi.constant;

public class ProcessorConstant {

	//TODO: Replace this with XML file
	public static final String ECHO_PROCESSOR_ID              = "0800_100000";
	public static final String RECHARGE_MIN_PROCESSOR_ID      = "0200_110000";
	public static final String PIN_PURCHASE_PROCESSOR_ID  	  = "0200_120000";
	public static final String ACCOUNT_INQUIRY_PROCESSOR_ID    = "0200_130000";
	public static final String MIN_REVERSAL_PROCESSOR_ID  	   = "0400_140000";
	public static final String PIN_REVERSAL_PROCESSOR_ID   	   = "0400_150000";

	public static final String ECHO_PROCESSOR_CLASS_NAME      = "com.bcgi.paymgr.posserver.bcgi.processor.EchoProcessor";

	public static final String RECHARGE_MIN_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.bcgi.processor.RechargeMINProcessor";
	public static final String PIN_PURCHASE_PROCESSOR_CLASS_NAME  	  = "com.bcgi.paymgr.posserver.bcgi.processor.PinPurchaseProcessor";
	public static final String ACCOUNT_INQUIRY_PROCESSOR_CLASS_NAME    = "com.bcgi.paymgr.posserver.bcgi.processor.AccntStatusInquiryProcessor";
	public static final String MIN_REVERSAL_PROCESSOR_CLASS_NAME  	   = "com.bcgi.paymgr.posserver.bcgi.processor.ReversalMINProcessor";
	public static final String PIN_REVERSAL_PROCESSOR_CLASS_NAME   	   = "com.bcgi.paymgr.posserver.bcgi.processor.ReversalPinProcessor";

	public static final String ECHO_MSG_TYPE_ID   	= "0800";
	public static final String RECHARGE_MSG_TYPE_ID	= "0200";
	public static final String REVERSAL_MSG_TYPE_ID	= "0400";

	public static final String MIN_RECHARGE_PROCESSING_CODE_ID		= "110000";
	public static final String PIN_RECHARGE_PROCESSING_CODE_ID		= "120000";
	public static final String ACCOUNT_INQUIRY_PROCESSING_CODE_ID	= "130000";

	public static final String MIN_REVERSAL_PROCESSING_CODE_ID		= "140000";
	public static final String PIN_REVERSAL_PROCESSING_CODE_ID		= "150000";


}
