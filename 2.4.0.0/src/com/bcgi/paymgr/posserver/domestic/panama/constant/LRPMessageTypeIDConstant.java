package  com.bcgi.paymgr.posserver.domestic.panama.constant;;

public class LRPMessageTypeIDConstant {

	//Message Types 
	public static final String ECHO_MSG_TYPE_ID   	= "0800";
	public static final String RECHARGE_MSG_TYPE_ID	= "0200";
	public static final String REVERSAL_MSG_TYPE_ID	= "0420";
	public static final String REVERSAL_RETRY_MSG_TYPE_ID = "0421";
	
	//Failure or Rejected Transaction MTI
	public static final String FAILURE_ECHO_MSG_TYPE_ID   	= "9800";
	public static final String FAILURE_RECHARGE_MSG_TYPE_ID	= "9200";
	public static final String FAILURE_REVERSAL_MSG_TYPE_ID	= "9420";
	
	// Processing Codes 
	public static final String ECHO_PROCESSING_CODE_ID	= "990000";
	public static final String SYNC_MIN_RECHARGE_PROCESSING_CODE_ID	= "650000";// Altamira Recharge
	public static final String SYNC_MIN_RECHARGE_PROCESSING_CODE_ID_TUENTI	= "650001";// Tuneti  Recharge
	public static final String SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID	= "650300";// Altamira Pkt 
	public static final String SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI	= "650301";// Tuneti  Pkt
		
	
	public static final String SYNC_REVERSAL_PROCESSING_CODE_ID	= "650000";
	public static final String SYNC_REVERSAL_RETRY_PROCESSING_CODE_ID	= "650000";	
	public static final String SYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID	= "650000";
	
	//Subscriber Number Start and End positions.
	public static String SUBSCRIBER_NUMBER_START_POSITION	= "";
	public static String SUBSCRIBER_NUMBER_END_POSITION	= "";
	
	public static final String FIELD_90_FILLER	= "0000000000";
}
