package com.bcgi.paymgr.posserver;

import java.util.HashMap;
import java.util.Map;

import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCProcessorConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMProcessorConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPProcessorConstant;



public class DMCommandMap {

	  private Map processorMap = new HashMap();
	  private static DMCommandMap mapInstance;

	  public synchronized static DMCommandMap getInstance(int msgFormat) {
	    if (mapInstance == null) {
	    	mapInstance = new DMCommandMap(msgFormat);
	    }
	    return mapInstance;
	  }

	  public Map getMap()
	  {
	     return processorMap;
	  }


	  private DMCommandMap(int msgFormat) {
	    initializeMap(msgFormat);
	  }

	  private void initializeMap(int msgFormat) {
		  System.out.println("msgFormat: "+msgFormat);
		  switch (msgFormat) 
			{
		  			//The Following Case is added for Panama Requirement on 11-Jul-2011
					case MessageFormatConstant.LR_MESSAGE_FORMAT_PANAMA_ID:
					{
						processorMap.put ( LRPProcessorConstant.LRP_ECHO_PROCESSOR_ID, LRPProcessorConstant.LRP_ECHO_PROCESSOR_CLASS_NAME);
						processorMap.put ( LRPProcessorConstant.LRP_RECHARGE_MIN_PROCESSOR_ID, LRPProcessorConstant.LRP_RECHARGE_MIN_PROCESSOR_CLASS_NAME);
						processorMap.put ( LRPProcessorConstant.LRP_RECHARGE_MIN_PROCESSOR_ID_TUENTI, LRPProcessorConstant.LRP_RECHARGE_MIN_PROCESSOR_CLASS_NAME);
						processorMap.put ( LRPProcessorConstant.LRP_PKT_RECHARGE_MIN_PROCESSOR_ID, LRPProcessorConstant.LRP_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME);
						processorMap.put ( LRPProcessorConstant.LRP_PKT_RECHARGE_MIN_PROCESSOR_ID_TUENTI, LRPProcessorConstant.LRP_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME);
						processorMap.put ( LRPProcessorConstant.LRP_MIN_REVERSAL_PROCESSOR_ID, LRPProcessorConstant.LRP_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
						processorMap.put ( LRPProcessorConstant.LRP_MIN_REVERSAL_RETRY_PROCESSOR_ID, LRPProcessorConstant.LRP_MIN_REVERSAL_RETRY_PROCESSOR_CLASS_NAME);
						break;
					}
					
					case MessageFormatConstant.LR_MESSAGE_FORMAT_COLUMBIA_ID:
					{
						  processorMap.put ( LRCProcessorConstant.LR_ECHO_PROCESSOR_ID, LRCProcessorConstant.LR_ECHO_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( LRCProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_ID, LRCProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( LRCProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_ID_TUENTI, LRCProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( LRCProcessorConstant.LR_RECHARGE_PKT_MIN_PROCESSOR_ID, LRCProcessorConstant.LR_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( LRCProcessorConstant.LR_RECHARGE_PKT_MIN_PROCESSOR_ID_TUENTI, LRCProcessorConstant.LR_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( LRCProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_ID, LRCProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( LRCProcessorConstant.LR_RECHARGE_INQUIRY_PROCESSOR_ID, LRCProcessorConstant.LR_RECHARGE_INQUIRY_PROCESSOR_CLASS_NAME);
					  	  
					  	  
					  	  
					  	  processorMap.put ( LRCProcessorConstant.LR_BRAND_CPN_REDEEMPTION_PROCESSOR_ID, LRCProcessorConstant.LR_BRAND_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME);
						  processorMap.put ( LRCProcessorConstant.LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_ID, LRCProcessorConstant.LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME);
					     
					  	  
					  	  
					      break;
					}
                 case MessageFormatConstant.LR_MESSAGE_FORMAT_ID:
					{
						  processorMap.put ( DMProcessorConstant.LR_ECHO_PROCESSOR_ID, DMProcessorConstant.LR_ECHO_PROCESSOR_CLASS_NAME);
					  	 
						
						//GTQ   TUENTI
					  	  processorMap.put ( DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_ID, DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_ID, DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_ID_TUENTI, DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_ID_TUENTI, DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
					  	  
					  	 
					  	  
					  	  processorMap.put ( DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_ID, DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_CLASS_NAME); // Altamira Topup
					  	  processorMap.put ( DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_ID, DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_CLASS_NAME); // Altamira  Topup  Reversal
					  	  processorMap.put ( DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_ID_TUENTI, DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_CLASS_NAME); // Tuenti Topup Recharge 
					  	  processorMap.put ( DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_ID_TUENTI, DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_CLASS_NAME); // Tuenti Topup Reversal 
					  	  
					  	  
					  	  processorMap.put (DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_ID_PKT ,DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_PKT_CLASS_NAME);  // Altamira Pkt Recharge
					  	  processorMap.put ( DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_ID_PKT,DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_PKT_CLASS_NAME);  // Altamira Pkt Recharge
					  	  processorMap.put ( DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_ID_PKT_TUENTI,DMProcessorConstant.LR_RECHARGE_MIN_PROCESSOR_PKT_CLASS_NAME );  // Tuenti pkt recharge
					      processorMap.put ( DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_ID_PKT_TUENTI,DMProcessorConstant.LR_MIN_REVERSAL_PROCESSOR_PKT_CLASS_NAME );  // Tuenti pkt Reversal
					      
					      
					      
					      
					  	  processorMap.put ( DMProcessorConstant.LR_ASYNC_RECHARGE_MIN_PROCESSOR_ID, DMProcessorConstant.LR_ASYNC_RECHARGE_MIN_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( DMProcessorConstant.LR_ASYNC_UNIFIED_RECHARGE_MIN_PROCESSOR_ID, DMProcessorConstant.LR_UNIFIED_ASYNC_RECHARGE_MIN_PROCESSOR_CLASS_NAME); // Altamira  unified Recharge
					      processorMap.put ( DMProcessorConstant.LR_ASYNC_UNIFIED_RECHARGE_MIN_PROCESSOR_ID_TUENTI, DMProcessorConstant.LR_UNIFIED_ASYNC_RECHARGE_MIN_PROCESSOR_CLASS_NAME);  // Tuenti unified Recharge
					      
					      processorMap.put ( DMProcessorConstant.LR_ASYNC_UNIFIED_RECHARGE_MIN_PROCESSOR_ID_PKT, DMProcessorConstant.LR_UNIFIED_ASYNC_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME); // Altamira  unified pkt Recharge
					      
					      processorMap.put ( DMProcessorConstant.LR_ASYNC_UNIFIED_RECHARGE_MIN_PROCESSOR_ID_PKT_TUENTI, DMProcessorConstant.LR_UNIFIED_ASYNC_RECHARGE_PKT_MIN_PROCESSOR_CLASS_NAME); // Tuenti unified pkt  Recharge
					      
					  	  processorMap.put ( DMProcessorConstant.LR_SUBSCRIBER_ACCOUNT_VALIDATION_PROCESSOR_ID, DMProcessorConstant.LR_SYNC_SUBSCRIBER_ACCOUNT_VALIDATION_PROCESSOR_CLASS_NAME);
				   	  	  processorMap.put ( DMProcessorConstant.LR_ASYNC_MIN_REVERSAL_PROCESSOR_ID, DMProcessorConstant.LR_ASYNC_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
				   	  	  processorMap.put ( DMProcessorConstant.LR_ASYNC_MIN_REVERSAL_PROCESSOR_ID_PKT, DMProcessorConstant.LR_ASYNC_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
				   	  	  processorMap.put ( DMProcessorConstant.LR_ASYNC_MIN_REVERSAL_PROCESSOR_ID_TUENTI, DMProcessorConstant.LR_ASYNC_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
				   	  	  processorMap.put ( DMProcessorConstant.LR_ASYNC_MIN_REVERSAL_PROCESSOR_ID_PKT_TUENTI, DMProcessorConstant.LR_ASYNC_MIN_REVERSAL_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( DMProcessorConstant.LR_RECHARGE_INQUIRY_PROCESSOR_ID, DMProcessorConstant.LR_RECHARGE_INQUIRY_PROCESSOR_CLASS_NAME);
					  	  processorMap.put ( DMProcessorConstant.LR_REVERSAL_INQUIRY_PROCESSOR_ID, DMProcessorConstant.LR_REVERSAL_INQUIRY_PROCESSOR_CLASS_NAME);
					  	  
					  	  
					  	  processorMap.put ( DMProcessorConstant.LR_BRAND_CPN_REDEEMPTION_PROCESSOR_ID, DMProcessorConstant.LR_BRAND_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME);
						  processorMap.put ( DMProcessorConstant.LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_ID, DMProcessorConstant.LR_RECHARGE_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME);
						  processorMap.put ( DMProcessorConstant.LR_ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSOR_ID, DMProcessorConstant.LR_ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSOR_CLASS_NAME);
						  processorMap.put ( DMProcessorConstant.LR_RECHARGE_CPN_INQUIRY_PROCESSOR_ID, DMProcessorConstant.LR_RECHARGE_CPN_INQUIRY_PROCESSOR_CLASS_NAME);
						  processorMap.put ( DMProcessorConstant.LR_CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_ID, DMProcessorConstant.LR_CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_CLASS_NAME);
						  processorMap.put ( DMProcessorConstant.LR_CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_ID, DMProcessorConstant.LR_CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_CLASS_NAME);
						  processorMap.put ( DMProcessorConstant.LR_CPN_BRAND_ASYNC_VALIDATION_PROCESSING_ID, DMProcessorConstant.LR_CPN_BRAND_ASYNC_VALIDATION_PROCESSING_CLASS_NAME);
						  processorMap.put ( DMProcessorConstant.LR_CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_ID,DMProcessorConstant.LR_CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_CLASS_NAME);
						  
					      break;
					}
			}
		  
		   
	  }


	}