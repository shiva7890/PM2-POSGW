package com.bcgi.paymgr.posserver;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import com.bcgi.paymgr.posserver.fw.processor.DMMessageProcessor;
public class DMCommandMapper {
	
	  private Map msgMap = new HashMap();
	  private static DMCommandMapper mapperInstance = null;
	  static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(DMCommandMapper.class);
	 /// private static int messsgeFormat = 0;
	  public synchronized static DMCommandMapper getInstance(int msgFormat) {
		 
	    if (mapperInstance == null) {
	    	//messsgeFormat = msgFormat;  
	    	mapperInstance = new DMCommandMapper(msgFormat);
	    }
	    return mapperInstance;
	  }

	  public DMMessageProcessor getMessageProcessor(String messageId) 
	  {
		  DMMessageProcessor msgProcessor = null;
		 Iterator it = msgMap.keySet().iterator();
		    while (it.hasNext()) {
		        // Get key
		        Object key = it.next();
		        logger.info("msgMap key.toString():"+key.toString());
		    }
		    
		    // Iterate over the values in the map
		    it = msgMap.values().iterator();
		    while (it.hasNext()) {
		        // Get value
		        Object value = it.next();
		        logger.info("msgMap value.toString():"+value.toString());
		    }

		 System.out.println("messageId: "+messageId);
		 Object mesgProcessorNameObj = msgMap.get(messageId);
		 
		 if (mesgProcessorNameObj != null)
		 {
			 logger.info("mesgProcessorNameObj: "+mesgProcessorNameObj.toString());
		 }
		 else
		 {
			 logger.info("mesgProcessorNameObj is null"); 
		 }
		 
		 try
		 {
			 if (mesgProcessorNameObj != null){
				 String className = (String)mesgProcessorNameObj;
				 logger.info("className:"+className);
				 Class cls = Class.forName(className);
				 logger.info("cls:"+cls);
				 msgProcessor = (DMMessageProcessor) cls.newInstance();
				 logger.info("msgProcessor...");
			 }
		 }
		 catch (Exception ex){
			 ex.printStackTrace();
			 logger.error("getMessageProcessor(): Message Id: "+messageId );
			 logger.error("getMessageProcessor(): Exception: "+ ex.getMessage());
		 }
	     return msgProcessor;
	  }

	
	  private DMCommandMapper(int msgFormat) {
	    initializeMessageMap(msgFormat);
	  }

	  private void initializeMessageMap(int msgFormat) {
		  msgMap = DMCommandMap.getInstance(msgFormat).getMap();
	 }

	  
	}