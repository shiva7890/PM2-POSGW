package com.bcgi.paymgr.posserver.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;

public class PropertiesReader {

	public static String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH + ServerConfigConstant.CONFIG_FILE_NAME;
	public static Properties prop = new Properties();

	static {
		try {
			prop.load(new FileInputStream(configfilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getValue(String key) {
		String value = "";
		try {
			value = prop.getProperty(key);
		} catch (Exception exception) {
			// Log.error("Exception in PropertyReader ::getValue(String s)=>");
		}
		return value.trim();
	}
}
