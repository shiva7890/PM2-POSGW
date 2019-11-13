package com.bcgi.paymgr.posserver;

import java.util.Map;
import java.util.HashMap;

import com.bcgi.paymgr.posserver.bcgi.constant.ProcessorConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPProcessorConstant;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;


public class CommandMap {

	  private Map processorMap = new HashMap();
	  private static CommandMap mapInstance;

	  public synchronized static CommandMap getInstance(int msgFormat) {
	    if (mapInstance == null) {
	    	mapInstance = new CommandMap(msgFormat);
	    }
	    return mapInstance;
	  }

	  public Map getMap()
	  {
	     return processorMap;
	  }


	  private CommandMap(int msgFormat) {
	    initializeMap(msgFormat);
	  }

	  private void initializeMap(int msgFormat) {
		  System.out.println("initializeMap.....");
		  System.out.println("msgFormat: "+msgFormat);
		  switch (msgFormat)
			{
					case MessageFormatConstant.BCGI_MESSAGE_FORMAT_ID:
					{
						  processorMap.put ( ProcessorConstant.ECHO_PROCESSOR_ID, ProcessorConstant.ECHO_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( ProcessorConstant.RECHARGE_MIN_PROCESSOR_ID, ProcessorConstant.RECHARGE_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( ProcessorConstant.PIN_PURCHASE_PROCESSOR_ID, ProcessorConstant.PIN_PURCHASE_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( ProcessorConstant.ACCOUNT_INQUIRY_PROCESSOR_ID, ProcessorConstant.ACCOUNT_INQUIRY_PROCESSOR_CLASS_NAME);
					      processorMap.put ( ProcessorConstant.MIN_REVERSAL_PROCESSOR_ID, ProcessorConstant.MIN_REVERSAL_PROCESSOR_CLASS_NAME);
					      processorMap.put ( ProcessorConstant.PIN_REVERSAL_PROCESSOR_ID, ProcessorConstant.PIN_REVERSAL_PROCESSOR_CLASS_NAME);
	                	  break;
					}
					case MessageFormatConstant.IRP_MESSAGE_FORMAT_ID:
					{
						  processorMap.put ( IRPProcessorConstant.IRP_ECHO_PROCESSOR_ID, IRPProcessorConstant.IRP_ECHO_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( IRPProcessorConstant.IRP_RECHARGE_MIN_PROCESSOR_ID, IRPProcessorConstant.IRP_RECHARGE_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( IRPProcessorConstant.IRP_IRP_TRANS_STATUS_INQUIRY_PROCESSOR_ID, IRPProcessorConstant.IRP_IRP_TRANS_STATUS_INQUIRY_PROCESSOR_CLASS_NAME);
					      processorMap.put ( IRPProcessorConstant.IRP_MIN_REVERSAL_PROCESSOR_ID, IRPProcessorConstant.IRP_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
					      break;
					}
			}


	  }


	}