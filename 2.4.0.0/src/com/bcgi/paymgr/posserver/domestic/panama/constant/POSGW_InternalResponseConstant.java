package com.bcgi.paymgr.posserver.domestic.panama.constant;

import java.io.FileInputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;

public class POSGW_InternalResponseConstant {
	static Logger logger	= Logger.getLogger(POSGW_InternalResponseConstant.class);

	private static Properties mappingProperties = null;

	static{
		try{
			mappingProperties = new Properties();
			String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.POS_GW_PANAMA_INTERNAL_ERROR_MAPPING;
			mappingProperties.load(new FileInputStream(configfilePath));
		}catch(Exception exp){
			logger.error("Got an exception while loading  gateway internal error codes "+exp);
		}
	}

	public static String getPOSGWResponse(String pmErrorCode) {
		String responseCode = "";
		pmErrorCode = pmErrorCode!=null?pmErrorCode.trim():pmErrorCode;
		Object respObj = mappingProperties.get(pmErrorCode);
		if (respObj != null){
			responseCode = (String)respObj;
		}else{
			responseCode = ServerConfigConstant.PANAMA_DEFAULT_ERROR;
		}
		logger.debug(pmErrorCode+" : POS GW Internal Error Mapping :"+responseCode);
		return responseCode;
	}

}
