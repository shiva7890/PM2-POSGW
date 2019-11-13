package com.bcgi.paymgr.posserver.domestic.columbia.constant;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;

public class PM_DMPOSGW_MapConstant {
	static Logger logger	= Logger.getLogger(PM_DMPOSGW_MapConstant.class);


private static Properties mappingProperties    = new Properties();

	static
	{
		try
		{
			mappingProperties = new Properties();
			
			String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
			ServerConfigConstant.PM_POS_GW_ERROR_MAPPING;
			mappingProperties.load(new FileInputStream(configfilePath));
			
			/*Reading the Default Error Code from the properties file
			 * Added by Sridhar.vemulapalli on 23-Jan-2012*/
			configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.CONFIG_FILE_NAME;
			Properties properties = new Properties();
			properties.load(new FileInputStream(configfilePath));
			ServerConfigConstant.COLUMBIA_DEFAULT_ERROR=properties.getProperty("COLUMBIA_DEFAULT_RESP_CODE");
			ServerConfigConstant.TRANS_TIMEOUT_SUCCESS_RESPONSE=properties.getProperty("TRANS_TIMEOUT_SUCCESS_RESPONSE");

			/*Ends*/			
		}
		catch(Exception exp)
		{
			logger.error("Got an exception while loading pm and gateway error codes/the Default Error code from properties file "+exp);
		}
	}

	public static String getPOSGWResponse(String pmErrorCode)
	{
		String responseCode = "";
		pmErrorCode = pmErrorCode!=null?pmErrorCode.trim():pmErrorCode;
		Object respObj = mappingProperties.get(pmErrorCode);
		if (respObj != null){
			responseCode = (String)respObj;
		}
		else{
			responseCode = ServerConfigConstant.COLUMBIA_DEFAULT_ERROR; //Transaction Rejected, Filling unhandled case
		}
		logger.debug(pmErrorCode+" : PM and POSGW Error Mapping :"+responseCode);
		return responseCode;
	}

}
