package  com.bcgi.paymgr.posserver.domestic.panama.constant;;

public class LRPProcessorConstant {
	
	public static final String LRP_ECHO_PROCESSOR_ID                        =  "0800_990000";
	public static final String LRP_RECHARGE_MIN_PROCESSOR_ID                =  "0200_650000";  // rec altamira
	public static final String LRP_RECHARGE_MIN_PROCESSOR_ID_TUENTI         =  "0200_650001";  // rec tuenti
	public static final String LRP_PKT_RECHARGE_MIN_PROCESSOR_ID            =  "0200_650300";  // pkt altamira
	public static final String LRP_PKT_RECHARGE_MIN_PROCESSOR_ID_TUENTI     =  "0200_650301";  // pkt Tuenti
	public static final String LRP_MIN_REVERSAL_PROCESSOR_ID  	            =  "0420_650000";
	public static final String LRP_MIN_REVERSAL_RETRY_PROCESSOR_ID  	    =  "0421_650000";
	
	//Processor to process the ISO message
	public static final String LRP_ECHO_PROCESSOR_CLASS_NAME      			= "com.bcgi.paymgr.posserver.domestic.panama.processor.LRPEchoProcessor";
	public static final String LRP_RECHARGE_MIN_PROCESSOR_CLASS_NAME     	= "com.bcgi.paymgr.posserver.domestic.panama.processor.LRPRechargeMINProcessor";
	public static final String LRP_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME    = "com.bcgi.paymgr.posserver.domestic.panama.processor.LRPRechargePKTMINProcessor";
	public static final String LRP_MIN_REVERSAL_PROCESSOR_CLASS_NAME  	   	= "com.bcgi.paymgr.posserver.domestic.panama.processor.LRPReversalMINProcessor";
	public static final String LRP_MIN_REVERSAL_RETRY_PROCESSOR_CLASS_NAME  = "com.bcgi.paymgr.posserver.domestic.panama.processor.LRPReversalMINProcessor";

}
