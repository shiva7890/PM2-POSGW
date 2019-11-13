package com.bcgi.paymgr.posserver;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import com.bcgi.paymgr.posserver.fw.processor.MessageProcessor;

public class CommandMapper {
	
	  private Map msgMap = new HashMap();
	  private static CommandMapper mapperInstance = null;
	  static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(CommandMapper.class);
	   
	  public synchronized static CommandMapper getInstance(int msgFormat) {
		  System.out.println("CommandMapper getInstance..."+msgFormat);
	    if (mapperInstance == null) {
	    	mapperInstance = new CommandMapper(msgFormat);
	    }
	    return mapperInstance;
	  }

	  public MessageProcessor getMessageProcessor(String messageId) 
	  {
		 MessageProcessor msgProcessor = null;
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
		 try
		 {
			 if (mesgProcessorNameObj != null){
				 String className = (String)mesgProcessorNameObj;
				 Class cls = Class.forName(className);
				 msgProcessor = (MessageProcessor) cls.newInstance();
				
			 }
		 }
		 catch (Exception ex){
			 logger.error("getMessageProcessor(): Message Id: "+messageId );
			 logger.error("getMessageProcessor(): Exception: "+ ex.getMessage());
		 }
	     return msgProcessor;
	  }

	
	  private CommandMapper(int msgFormat) {
	    initializeMessageMap(msgFormat);
	  }

	  private void initializeMessageMap(int msgFormat) {
		  System.out.println("initializeMessageMap....");
		  msgMap = CommandMap.getInstance(msgFormat).getMap();
	 }

	  
	}