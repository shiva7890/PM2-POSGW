package com.bcgi.paymgr.posserver;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import com.bcgi.paymgr.posserver.irp.processor.*;
import com.bcgi.paymgr.posserver.fw.processor.MessageProcessor;
import com.bcgi.paymgr.posserver.fw.processor.IRPMessageProcessor;
public class IRCommandMapper {
	
	  private Map msgMap = new HashMap();
	  private static IRCommandMapper mapperInstance = null;
	  static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(CommandMapper.class);
	 /// private static int messsgeFormat = 0;
	  public synchronized static IRCommandMapper getInstance(int msgFormat) {
		  System.out.println("CommandMapper getInstance..."+msgFormat);
		 
	    if (mapperInstance == null) {
	    	//messsgeFormat = msgFormat;  
	    	mapperInstance = new IRCommandMapper(msgFormat);
	    }
	    return mapperInstance;
	  }

	  public IRPMessageProcessor getMessageProcessor(String messageId) 
	  {
		  IRPMessageProcessor msgProcessor = null;
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
				 logger.info("cls...");
				 logger.info("cls:"+cls);
				 msgProcessor = (IRPMessageProcessor) cls.newInstance();
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

	
	  private IRCommandMapper(int msgFormat) {
	    initializeMessageMap(msgFormat);
	  }

	  private void initializeMessageMap(int msgFormat) {
		  System.out.println("initializeMessageMap....");
		  msgMap = IRCommandMap.getInstance(msgFormat).getMap();
	 }

	  
	}