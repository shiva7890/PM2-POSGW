package com.bcgi.paymgr.posserver.common.constant;

public class ServerConfigConstant {

    //Current Directory 
	public final static String CURRENT_PATH = System.getProperty("user.dir");
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	public final static String CONFIG_FILE_FOLDER_NAME = "configfile";
	public final static String LOG_FILE_NAME = "Log4j.properties";
	public final static String INOUT_LOG_FILE_NAME = "Log4j.xml";	
	public final static String SYSTEM_MONITOR_LOG_FILE_NAME = "system_monitor_log.txt";
	public final static String CONFIG_FILE_NAME = "posservercfg.properties";
	public final static String PIN_CONFIG_FILE = "pincfg.properties";
	public final static String PMSERVER_ACCESS_CONFIG_FILE_NAME = "jnp.properties";
	public final static String LOG_FILE_FOLDER_NAME = "log";
	public final static String APPMONITOR_FILE_FOLDER_NAME = "appmonitorconf";
	public final static String CONFIG_FILE_FOLDER_PATH = CURRENT_PATH +
						                                 FILE_SEPARATOR +
						                                 CONFIG_FILE_FOLDER_NAME+
														 FILE_SEPARATOR;
	public final static String LOG_FILE_FOLDER_PATH = CURRENT_PATH +
												      FILE_SEPARATOR +
												      LOG_FILE_FOLDER_NAME+
												      FILE_SEPARATOR;

	public final static String APPMONITOR_FILE_FOLDER_PATH = CURRENT_PATH +
													    FILE_SEPARATOR +
													    APPMONITOR_FILE_FOLDER_NAME+
													    FILE_SEPARATOR;
	//Config File Constants
	public final static String SERVER_PORT = "SERVER_PORT";
	public final static String ISOMSG_LOG_FILENAME = "ISOMSG_LOG_FILENAME";
	public final static String ISO_MSG_HEADER = "ISO_MSG_HEADER";
	public final static String ISO_MSG_FORMAT = "ISO_MSG_FORMAT";
	public final static String ISO_MSG_PACKAGER_FORMAT = "ISO_MSG_PACKAGER_FORMAT";
	public final static String ASCII_PACKAGER_FORMAT = "ASCII";
	public final static String BINARY_PACKAGER_FORMAT = "BINARY";
	public final static String NAMING_FACTORY_INITIAL = "java.naming.factory.initial";
	public final static String NAMING_PROVIDER_URL = "java.naming.provider.url";
	public final static String NAMING_FACTORY_URL_PKG = "java.naming.factory.url.pkgs";
	public final static String JNP_DISABLE_DISCOVERY = "jnp.disableDiscovery";
	public final static String THREAD_POOL_MIN_SIZE = "LISTENER_THREAD_POOL_MIN_SIZE";
	public final static String THREAD_POOL_MAX_SIZE = "LISTENER_THREAD_POOL_MAX_SIZE";
	public final static String WORKER_THREAD_POOL_MIN_SIZE = "WORKER_THREAD_POOL_MIN_SIZE";
	public final static String WORKER_THREAD_POOL_MAX_SIZE = "WORKER_THREAD_POOL_MAX_SIZE";
	public final static String SYSTEM_MONITOR_FREQ = "SYSTEM_MONITOR_FREQUENCY";
	public final static String MOBILE_NUMBER_LENGTH = "MOBILE_NUMBER_LENGTH";
	public final static String NO_OF_NODES = "NO_OF_NODES";
	public final static String NODE 	   = "NODE";
	public final static String POS_TIME_ZONE = "POS_TIME_ZONE";
	public final static String DEFAULT_TIME_ZONE = "GMT";
	public final static String POS_GW_INSTANCE_ID = "POS_GW_INSTANCE_ID";
	//added by phani anumolu. 18 jul,2006
	public static final short CASH = 1;
    public final static String SERVER_LIST_CONFIG_FILE_NAME = "serverconfig.properties";
	public final static String NAMING_PROVIDER_URL_STARTVAL = "jnp://";
	public final static String 	RR_SYNC_REQUEST_TYPE = "PM_SYN";
	public final static String 	RR_ASYNC_REQUEST_TYPE = "PM_ASYN";
	
	
	public final static String 	RR_LR_C_SYNC_REQUEST_TYPE = "LR_C_PM_SYN";
	
	public final static String 	WAKEUP_TIME_TO_CONNECT_PM = "WAKEUP_TIME_TO_CONNECT_PM";
	public final static String 	NO_OF_ITERATIONS_TO_CONNECT_PM = "NO_OF_ITERATIONS_TO_CONNECT_PM";
	public final static String SUBSCRIBER_START_POSITION="SUBSCRIBER_START_POSITION";
	public final static String SUBSCRIBER_END_POSITION="SUBSCRIBER_END_POSITION";
	public final static String DOMESTIC_AUTO_REVERSAL_FLAG="DOMESTIC_AUTO_REVERSAL_FLAG";
	
	//Following fields Added By Sridhar Vemulapalli on 30-Jun-2011
	public final static String PANAMA_SUBSCRIBER_START_POSITION="PANAMA_SUBSCRIBER_START_POSITION";
	public final static String PANAMA_SUBSCRIBER_END_POSITION="PANAMA_SUBSCRIBER_END_POSITION";	
	
	public final static int RR_SYNCHRONOUS_CALL = 1;
	public final static int RR_ASYNCHRONOUS_CALL = 2;
	public final static int RR_LRC_SYNCHRONOUS_CALL = 3;
	
	//Adeed by surendra on 26th August 2008 (RAMP#5391)
	public final static String INVALID_DIST_LOG_FILENAME="Invalid_Dist.properties";
	public final static String PM_POS_GW_ERROR_MAPPING="PM_POSGW_ErrorMapping.properties";
	public final static String POS_GW_INTERNAL_ERROR_MAPPING="POSGW_Internal_ErrorMapping.properties";
	public final static String AUTOREVERSAL="AUTOREVERSAL";
	public static String COLUMBIA_DEFAULT_ERROR="41";
	public static String TRANS_TIMEOUT_SUCCESS_RESPONSE="N";
	// Added by Sridhar Vemulapalli on 05-Jul-2011 ---- Panama requirement
	public final static String PANAMA_DEFAULT_ERROR="41";
	public final static String POS_GW_PANAMA_INTERNAL_ERROR_MAPPING="PANAMA_POSGW_Internal_ErrorMapping.properties";
	public final static String PANAMA_PM_POS_GW_ERROR_MAPPING="PANAMA_PM_POSGW_ErrorMapping.properties";
	//The Following code is added by Sridhar.Vemulapalli on 12-Aug-2011
	public final static String 	USE_OLD_NAC_CHANNEL = "USE_OLD_NAC_CHANNEL";
	//Ends
	
	/** 
	 * these values will be filled in POSServerMgr class these value brings from the property (posservercfg.properties)
	 * these value will be set in delegate of the ejb calls to PM 
	 */
	public static String GW_INSTANCE_ID = "";
	public static String GW_INSTANCE_PORT_NUMBER = "";
	/*##DEFAULT RESPONSE CODE TO COSTARICA ( It should not be an Empty value)
		##END USER WILL GET THE FOLLOWING CODE WHEN EVER GW IS NOT ABLE TO FIND MAPPING FOR PM RESPONSE
	*/
	public static String COSTARICA_DEFAULT_ERROR="41";
	
	//Connection and Read time out for POSGW to PM	
	public static String CONNECTION_TIMEOUT_TO_PM = "CONNECTION_TIMEOUT_TO_PM";
	public static String CONNECTION_READ_TIMEOUT_TO_PM = "CONNECTION_READ_TIMEOUT_TO_PM";
	public static String NO_OF_UNSUCCESSFUL_FAILOVER_ATTEMPTS = "NO_OF_UNSUCCESSFUL_FAILOVER_ATTEMPTS";
	public static String PM_SERVER_DOWN="83";
	public static String UNSUCCESSFUL_CONNECTION_RETRY_ATTEMPTS_TO_PM = "UNSUCCESSFUL_CONNECTION_RETRY_ATTEMPTS_TO_PM";
		
	public static final String POSGWAY_REQ= "POSGWAY_REQ";
	public static final String POSGWAY_RES= "POSGWAY_RES";
	public static final String POSGWAY_REJ_RES= "POSGWAY_REJ_RES";
	
	public  static String DEFAULT_ERROR="";
	
	
	
	
		
}
