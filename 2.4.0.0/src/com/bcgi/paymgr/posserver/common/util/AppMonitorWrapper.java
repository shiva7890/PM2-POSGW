package com.bcgi.paymgr.posserver.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import net.bcgi.common.jmon.monitor.BCGLog;
import net.bcgi.common.jmon.monitor.BCGLogger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;

 /**
  * @author vijayabhaskarg
  * @date Nov. 7, 2007
  * @description This class is used for Logging messages and event peg counts using AppMonitor
  */
public class AppMonitorWrapper {
	private static BCGLogger bcgLogger = null;
	private static Logger logger = null;
	private static String isAppMonitorEnabled = "false";
	private static String APPMONITOR_PROPS = "appmonitor.properties";
		
	public AppMonitorWrapper(){
	}
	
	/**
	 * Reads config parameters from appmonitor.properties file and Initializes AppMonitor
	 */
	public static void initialize(){
		configLogger();
		String configfilePath = ServerConfigConstant.APPMONITOR_FILE_FOLDER_PATH+APPMONITOR_PROPS;
		Properties props = loadProperties(configfilePath);
		isAppMonitorEnabled = props.getProperty("ENABLE_APPMONITOR");
		if(isAppMonitorEnabled != null && isAppMonitorEnabled.equalsIgnoreCase("true")){
	        try{
	           if(BCGLog.isReady()){
	            	BCGLog.addAppMonitorUser();
	            } else {
	            	/*String serverName = props.getProperty("MBEAN_SERVER");
	                if(serverName != null && serverName.length() > 0)
	                	BCGLog.setMbeanServerName(serverName);*/
	                
	                String configDir = props.getProperty("CONFIG_DIR");
	                String configFile = props.getProperty("CONFIG_FILE");
	                String appName = props.getProperty("APP_NAME");
	                
	                BCGLog.initializeBCGLog(configDir, configFile, appName);
	            }	
	            String moduleName = props.getProperty("MODULE_NAME");
	            if(moduleName != null && moduleName.length() > 0)
	            	bcgLogger = BCGLogger.getBCGLogger(moduleName);
	            else
	            	bcgLogger = BCGLogger.getBCGLogger("Default");
	            logger.info("AppMonitor is Enabled and Initialized.");
	          	            
	        }catch(Exception e){
	        	logger.info("BCGLog initialization failed: " + e.getMessage());
	        }
		}else{
				logger.info("AppMonitor is not Enabled.");
		}
	}

	private static Properties loadProperties(String fileName){
		Properties prop = new Properties();
		try {
			prop.load((new FileInputStream(fileName)));
			} catch (IOException e) {
				System.out.println("Error loading properties from appmonitor property file! "+e);
				e.printStackTrace();
		}catch(Exception e){
				System.out.println("Error loading properties from appmonitor property file! "+e);
				e.printStackTrace();
		}
		return prop;
	}
	  
	/**
	 * Decrements the AppMonitorUser count. When this count becomes zero
	 * AppMonitor will shutdown gracefully.
	 */
	public static void shutdown(){
        try{
            BCGLog.removeAppMonitorUser();
        }catch(Exception e) { 
        	logger.error("AppMonitor Failed to Shutdown properly: "+e.getMessage());
        }
	}
	
	/**
	 * This method Logs Event and Increments peg count
	 * @param eventName Name of the event
	 * @param message Descriptive message for the event
	 * @param transactionTime Turn-around time for the event
	 * @param carrierCode Carrier Code for which the event occured. Could be used during filtering.
	 */ 
	public static void logEvent(String eventName, String message, int transactionTime, String carrierCode){
		 String key[] = null;
		 String value[] = null;
		
		if(isAppMonitorEnabled.equalsIgnoreCase("true") && bcgLogger != null){
			bcgLogger.logEvent(eventName, message, transactionTime, carrierCode, key, value);
		}
	}	
	
	/**
	 * This method Logs Event and Increments peg count
	 * @param eventName Name of the event
	 * @param message Descriptive message for the event
	 * @param transactionTime Turn-around time for the event
	 */ 
	public static void logEvent(String eventName, String message, int transactionTime){
		
		if(isAppMonitorEnabled.equalsIgnoreCase("true") && bcgLogger != null){
			bcgLogger.logEvent(eventName, message, transactionTime);
		}
	}	
	private static void configLogger()
	{

	    String log4jfile = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH + ServerConfigConstant.LOG_FILE_NAME;
	 
	    PropertyConfigurator.configure(log4jfile);
        logger	= Logger.getLogger(AppMonitorWrapper.class);

	}
}
