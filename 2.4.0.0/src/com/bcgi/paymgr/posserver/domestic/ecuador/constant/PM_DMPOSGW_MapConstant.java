package com.bcgi.paymgr.posserver.domestic.ecuador.constant;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;

public class PM_DMPOSGW_MapConstant {
	
private static HashMap responseCodeMap = null;
static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(PM_DMPOSGW_MapConstant.class);
	static 
	{
		try{
			/*Reading the Default Error Code from the properties file
			 * Added by Sridhar.vemulapalli on 23-Jan-2012*/
			String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.CONFIG_FILE_NAME;
			Properties properties = new Properties();
			properties.load(new FileInputStream(configfilePath));
			ServerConfigConstant.COSTARICA_DEFAULT_ERROR=properties.getProperty("COSTARICA_DEFAULT_RESP_CODE");
			/*Ends*/
		responseCodeMap = new HashMap();
		responseCodeMap.put("0","00");
		responseCodeMap.put("1","01");
		responseCodeMap.put("2","11");
		responseCodeMap.put("3","86");
		responseCodeMap.put("4","97");
		responseCodeMap.put("5","25");
		responseCodeMap.put("6","20");
		responseCodeMap.put("8","11");
		responseCodeMap.put("9","21");
		//responseCodeMap.put("10","98");
		responseCodeMap.put("11","18");
		responseCodeMap.put("12","98");
//changed error code 13 mapping from 70 to 71 for defect no:#17089 by Srinivas P. on 12th oct 2006
//revetback this change in regression testing for the defect #17958
		responseCodeMap.put("13","70");
		responseCodeMap.put("14","71");
		responseCodeMap.put("15","71");
		responseCodeMap.put("16","72");
		responseCodeMap.put("17","73");
		responseCodeMap.put("18","01");
		responseCodeMap.put("19","23");
		responseCodeMap.put("20","74");
		responseCodeMap.put("21","75");
		responseCodeMap.put("22","17");
		responseCodeMap.put("23","11");
		responseCodeMap.put("26","25");
		responseCodeMap.put("27","80");
		responseCodeMap.put("28","84");
		responseCodeMap.put("30","02");
		responseCodeMap.put("31","26");
		responseCodeMap.put("32","23");
		responseCodeMap.put("33","20");
		responseCodeMap.put("34","23");
		responseCodeMap.put("36","95");
		responseCodeMap.put("37","05");
		responseCodeMap.put("38","81");
        responseCodeMap.put("39","70");
		responseCodeMap.put("40","24");
		responseCodeMap.put("41","83");
		responseCodeMap.put("42","82");
		responseCodeMap.put("49","98");
		responseCodeMap.put("99","98");
		responseCodeMap.put("-1","98");
		
		
		responseCodeMap.put("44","64");
		responseCodeMap.put("45","65");
		responseCodeMap.put("46","66");
		responseCodeMap.put("47","71");
		responseCodeMap.put("48","90");
		responseCodeMap.put("53","88");
		responseCodeMap.put("54","60");
		responseCodeMap.put("55","62");
		responseCodeMap.put("56","66");
		responseCodeMap.put("59", "10");//added for duplicate transaction
		responseCodeMap.put("60", "27");//added altamira PE Status
		responseCodeMap.put("61","56");
		responseCodeMap.put("62","57");
		responseCodeMap.put("63","58");
		responseCodeMap.put("35","36");
		responseCodeMap.put("83","98");
		}catch(Exception e ){
			logger.error("Error in reading the Default Error code from properties file");
		}
	}
	
	public static String getPOSGWResponse(String pmErrorCode)
	{
		String responseCode = "";
		Object respObj = responseCodeMap.get(pmErrorCode);
		if (respObj != null){
			responseCode = (String)respObj;
		}
		else{
			responseCode = ServerConfigConstant.COSTARICA_DEFAULT_ERROR;
		}
		return responseCode;
	}

}
