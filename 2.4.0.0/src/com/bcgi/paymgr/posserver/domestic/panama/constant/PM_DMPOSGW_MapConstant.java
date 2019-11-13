package com.bcgi.paymgr.posserver.domestic.panama.constant;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;

public class PM_DMPOSGW_MapConstant {
	static Logger logger	= Logger.getLogger(PM_DMPOSGW_MapConstant.class);
	private static Properties mappingProperties    = new Properties();

	static{
		try{
			mappingProperties = new Properties();
			String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.PANAMA_PM_POS_GW_ERROR_MAPPING;
			mappingProperties.load(new FileInputStream(configfilePath));
		}catch(Exception exp){
			logger.error("Got an exception while loading pm and gateway error codes "+exp);
		}
	}

	public static String getPOSGWResponse(String pmErrorCode){
		String responseCode = "";
		pmErrorCode = pmErrorCode!=null?pmErrorCode.trim():pmErrorCode;
		Object respObj = mappingProperties.get(pmErrorCode);
		if (respObj != null){
			responseCode = (String)respObj;
		}else{
			responseCode = ServerConfigConstant.PANAMA_DEFAULT_ERROR; //Transaction Rejected, Filling unhandled case
		}
		logger.debug(pmErrorCode+" : PM and POSGW Error Mapping :"+responseCode);
		return responseCode;
	}

}
