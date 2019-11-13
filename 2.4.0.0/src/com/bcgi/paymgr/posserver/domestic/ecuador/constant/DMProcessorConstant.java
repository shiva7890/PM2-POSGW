package com.bcgi.paymgr.posserver.domestic.ecuador.constant;

public class DMProcessorConstant {


	public static final String LR_ECHO_PROCESSOR_ID                        =  "0800";
	public static final String LR_RECHARGE_MIN_PROCESSOR_ID                =  "0200_180000"; // Altamira Topup Recharge 
	public static final String LR_MIN_REVERSAL_PROCESSOR_ID  	           =  "0400_180000"; // Altamira Topup Reversal
	public static final String LR_RECHARGE_MIN_PROCESSOR_ID_TUENTI         =  "0200_180001"; // Tuenti Recharge Topup 
	public static final String LR_MIN_REVERSAL_PROCESSOR_ID_TUENTI         =  "0400_180001"; // Tuenti Reversal Topup 
	
	// REQ 1778  Packet Sale POS
	public static final String LR_RECHARGE_MIN_PROCESSOR_ID_PKT                =  "0200_650300";   // Altamira Recharge pkt
	public static final String LR_MIN_REVERSAL_PROCESSOR_ID_PKT  	           =  "0400_650300";   // Altamira Reversal pkt
	public static final String LR_RECHARGE_MIN_PROCESSOR_ID_PKT_TUENTI         =  "0200_650301";   // Tuenti Recharge  pkt
	public static final String LR_MIN_REVERSAL_PROCESSOR_ID_PKT_TUENTI         =  "0400_650301";   // Tuenti Reversal  pkt
	
	
	public static final String LR_ASYNC_RECHARGE_MIN_PROCESSOR_ID          =  "0200_300000";
	public static final String LR_ASYNC_UNIFIED_RECHARGE_MIN_PROCESSOR_ID       =  "0200_350000"; // Altamira  Unified Recharge Recharge
	public static final String LR_ASYNC_UNIFIED_RECHARGE_MIN_PROCESSOR_ID_TUENTI="0200_350001";   //  Tuenti Unified Recharge Recharge
	
	public static final String LR_ASYNC_UNIFIED_RECHARGE_MIN_PROCESSOR_ID_PKT       =  "0200_350300"; // Altamira  Unified PKT Recharge
	public static final String LR_ASYNC_UNIFIED_RECHARGE_MIN_PROCESSOR_ID_PKT_TUENTI="0200_350301";   //  Tuenti Unified PKT Recharge 
	
	
	public static final String LR_SUBSCRIBER_ACCOUNT_VALIDATION_PROCESSOR_ID     =  "0200_100000";
	public static final String LR_ASYNC_MIN_REVERSAL_PROCESSOR_ID  	       =  "0400_500000";
	public static final String LR_ASYNC_MIN_REVERSAL_PROCESSOR_ID_PKT 	 =  "0400_500300";
	public static final String LR_ASYNC_MIN_REVERSAL_PROCESSOR_ID_TUENTI  	 =  "0400_500001";
	public static final String LR_ASYNC_MIN_REVERSAL_PROCESSOR_ID_PKT_TUENTI =  "0400_500301";
	public static final String LR_RECHARGE_INQUIRY_PROCESSOR_ID            =  "0220_600000";
	public static final String LR_REVERSAL_INQUIRY_PROCESSOR_ID  	       =  "0420_700000";

	// newly added for coupon  module  mergining 
	public static final String LR_BRAND_CPN_REDEEMPTION_PROCESSOR_ID       = "0200_255000";
	public static final String LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_ID       = "0200_185000";
	public static final String LR_ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSOR_ID       = "0200_355000";
	public static final String LR_RECHARGE_CPN_INQUIRY_PROCESSOR_ID            =  "0220_605000";
	
	public static final String LR_CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_ID= "0200_105000";
	public static final String LR_CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_ID= "0200_305000";
	public static final String LR_CPN_BRAND_ASYNC_VALIDATION_PROCESSING_ID= "0200_105100";
	public static final String LR_CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_ID= "0200_305100";
	
		
	
	public static final String LR_ECHO_PROCESSOR_CLASS_NAME      = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMEchoProcessor";
	public static final String LR_RECHARGE_MIN_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMRechargeMINProcessor";
	public static final String LR_MIN_REVERSAL_PROCESSOR_CLASS_NAME  	   = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMReversalMINProcessor";
	//GTQ TUENTI
	public static final String LR_RECHARGE_MIN_PROCESSOR_PKT_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMRechargePKTMINProcessor";
	public static final String LR_MIN_REVERSAL_PROCESSOR_PKT_CLASS_NAME  	   = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMReversalPKTMINProcessor";
	
	public static final String LR_SYNC_SUBSCRIBER_ACCOUNT_VALIDATION_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMSubAccntValidationProcessor";
	public static final String LR_ASYNC_RECHARGE_MIN_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMASyncRechargeMINProcessor";
	public static final String LR_UNIFIED_ASYNC_RECHARGE_MIN_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMUnifiedASyncRechargeMINProcessor";
	public static final String LR_UNIFIED_ASYNC_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMUnifiedASyncRechargePKTMINProcessor";
	public static final String LR_ASYNC_MIN_REVERSAL_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMASyncReversalMINProcessor";
	public static final String LR_RECHARGE_INQUIRY_PROCESSOR_CLASS_NAME       =  "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMRechargeInquiryProcessor";
	public static final String LR_REVERSAL_INQUIRY_PROCESSOR_CLASS_NAME  	  =  "com.bcgi.paymgr.posserver.domestic.ecuador.processor.DMReversalInquiryProcessor";

	// newly added for coupon  module  mergining 
	public static final String LR_BRAND_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon.DMBrandCouponRedemption";
	public static final String LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon.DMRechargeCouponRedemption";
	public static final String LR_ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME = "com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon.DMASyncCouponRedemption";
	public static final String LR_RECHARGE_CPN_INQUIRY_PROCESSOR_CLASS_NAME =  "com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon.DMRechargeCouponInquiryProcessor";
	public static final String LR_CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_CLASS_NAME= "com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon.DMRechargeCpnSubValidationProcessor";
	public static final String LR_CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_CLASS_NAME= "com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon.DMASyncRechargeCpnMINProcessor";
	public static final String LR_CPN_BRAND_ASYNC_VALIDATION_PROCESSING_CLASS_NAME= "com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon.DMBrandCouponSubValidationProcessor";
	public static final String LR_CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_CLASS_NAME= "com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon.DMASyncBrandCouponMINProcessor";

	
	
	

}
