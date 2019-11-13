package  com.bcgi.paymgr.posserver.domestic.columbia.constant;;

public class LRCProcessorConstant {

	/*The Following lines of code modified (Commented and Added) By Sridhar.Vemulapalli on 04-Oct-2011
	 * Because to Eliminate the processing code validation 
	 * */
	/*	public static final String LR_ECHO_PROCESSOR_ID                        =  "0800_990000";
	public static final String LR_RECHARGE_MIN_PROCESSOR_ID                =  "0200_180000";
	public static final String LR_MIN_REVERSAL_PROCESSOR_ID  	           =  "0400_200000";
	public static final String LR_RECHARGE_INQUIRY_PROCESSOR_ID            =  "0420_660000";*/
	public static final String LR_ECHO_PROCESSOR_ID                        =  "0800";
	public static final String LR_RECHARGE_MIN_PROCESSOR_ID                =  "0200";
	public static final String LR_RECHARGE_MIN_PROCESSOR_ID_TUENTI              =  "0200_180001";
	public static final String LR_RECHARGE_PKT_MIN_PROCESSOR_ID                =  "0200";
	public static final String LR_RECHARGE_PKT_MIN_PROCESSOR_ID_TUENTI          ="0200_650301";
	public static final String LR_MIN_REVERSAL_PROCESSOR_ID  	           =  "0400";
	public static final String LR_RECHARGE_INQUIRY_PROCESSOR_ID            =  "0420";	
	//Ends
	public static final String LR_ECHO_PROCESSOR_CLASS_NAME      = "com.bcgi.paymgr.posserver.domestic.columbia.processor.LRCEchoProcessor";
	public static final String LR_RECHARGE_MIN_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.columbia.processor.LRCRechargeMINProcessor";
	public static final String LR_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME     = "com.bcgi.paymgr.posserver.domestic.columbia.processor.LRCRechargePKTMINProcessor";
	public static final String LR_MIN_REVERSAL_PROCESSOR_CLASS_NAME  	   = "com.bcgi.paymgr.posserver.domestic.columbia.processor.LRCReversalMINProcessor";
	public static final String LR_RECHARGE_INQUIRY_PROCESSOR_CLASS_NAME       =  "com.bcgi.paymgr.posserver.domestic.columbia.processor.LRCRechargeInquiryProcessor";
	
	
	// newly added for coupon  module  mergining 
	public static final String LR_BRAND_CPN_REDEEMPTION_PROCESSOR_ID           = "0200_255000";
	public static final String LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_ID        = "0200_185000";
	public static final String LR_ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSOR_ID  = "0200_355000";
	public static final String LR_RECHARGE_CPN_INQUIRY_PROCESSOR_ID            =  "0220_605000";
	
	public static final String LR_CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_ID  = "0200_105000";
	public static final String LR_CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_ID = "0200_305000";
	public static final String LR_CPN_BRAND_ASYNC_VALIDATION_PROCESSING_ID     = "0200_105100";
	public static final String LR_CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_ID    = "0200_305100";
	
	// newly added for coupon  module  mergining 
	public static final String LR_BRAND_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME = "com.bcgi.paymgr.posserver.domestic.columbia.processor.coupon.LRCBRandCouponRedemption";
	public static final String LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME = "com.bcgi.paymgr.posserver.domestic.columbia.processor.coupon.LRCRechargeCouponRedemption";
	public static final String LR_ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME = "com.bcgi.paymgr.posserver.domestic.columbia.processor.coupon.DMASyncCouponRedemption";
	public static final String LR_RECHARGE_CPN_INQUIRY_PROCESSOR_CLASS_NAME =  "com.bcgi.paymgr.posserver.domestic.columbia.processor.coupon.DMRechargeCouponInquiryProcessor";
	public static final String LR_CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_CLASS_NAME= "com.bcgi.paymgr.posserver.domestic.columbia.processor.coupon.DMRechargeCpnSubValidationProcessor";
	public static final String LR_CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_CLASS_NAME= "com.bcgi.paymgr.posserver.domestic.columbia.processor.coupon.DMASyncRechargeCpnMINProcessor";
	public static final String LR_CPN_BRAND_ASYNC_VALIDATION_PROCESSING_CLASS_NAME= "com.bcgi.paymgr.posserver.domestic.columbia.processor.coupon.DMBrandCouponSubValidationProcessor";
	public static final String LR_CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_CLASS_NAME= "com.bcgi.paymgr.posserver.domestic.columbia.processor.coupon.DMASyncBrandCouponMINProcessor";

	
}
