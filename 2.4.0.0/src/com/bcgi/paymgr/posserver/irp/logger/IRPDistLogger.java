package com.bcgi.paymgr.posserver.irp.logger;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;

/*
 * This class provides the DailyRollingFileAppender log. Using this class we can write our own logs.
 * These logs Rollover at midnight each day.
 * Author : Surendra
 * Date   : 26th August 2008
 */
public class IRPDistLogger {
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(IRPDistLogger.class);
	
	
	public static Logger getIRPLogger(String propertyFileName)
	{
		Properties properties = null;
		Logger log			  = null;
		DailyRollingFileAppender  dailyRollAppend = null;
		String datePattern   = "";
		String fileName      ="";
		String conversionPattern="";
		try{
				properties = new Properties();
				properties.load(new FileInputStream(System.getProperty("user.dir")+System.getProperty("file.separator")+ServerConfigConstant.CONFIG_FILE_FOLDER_NAME+System.getProperty("file.separator")+propertyFileName));
				System.out.println(System.getProperty("user.dir"));
				datePattern = properties.getProperty("DATE_PATTERN");
				fileName    = properties.getProperty("FILE_NAME");
				conversionPattern = properties.getProperty("PATTERN");
				String path=System.getProperty("user.dir")+System.getProperty("file.separator")+ServerConfigConstant.LOG_FILE_FOLDER_NAME+System.getProperty("file.separator");
				log = Logger.getLogger("IRPMessageHandler.class");
						//FileAppender a1 = new FileAppender(new SimpleLayout(), "a1.log");
				PatternLayout pattern = new PatternLayout();
				pattern.setConversionPattern(conversionPattern);
				dailyRollAppend = new DailyRollingFileAppender(pattern, path+fileName,datePattern);
						/*RollingFileAppender a = new RollingFileAppender(new SimpleLayout(),"all.log",true);
						Properties properties = new Properties();
						properties.load(new FileInputStream(System.getProperty("user.dir")+System.getProperty("file.separator")+"Invalid_Dist.properties"));
						String fileSize = properties.getProperty("MAX_FILE_SIZE");
						System.out.println("File size is --------------->"+fileSize);
						System.out.println("user Directory is --------------->"+System.getProperty("user.dir"));
						a.setMaxFileSize(fileSize);
						*/
				dailyRollAppend.setImmediateFlush(true);
				log.removeAllAppenders();
				log.addAppender(dailyRollAppend);
				log.setAdditivity(false);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.error("Exception occured while creating seperate logs for invalid distributors"+e);
		}
		return log;
	}

}
