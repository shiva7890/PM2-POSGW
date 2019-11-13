package com.bcgi.paymgr.posserver.domestic.panama.validator;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPMessageTypeIDConstant;

public class LRPMessageTypeValidator {


	public static String isValidMessageTypeId(String value){

		
		if (!PMISOFieldValidator.isValueExists(value)){
			return ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
			//return ResponseCodeConstant.INITIAL_STATE;
		}

		if (!PMISOFieldValidator.isNumericValue(value)){
			return ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
			//return ResponseCodeConstant.INITIAL_STATE;
		}
		// The Following Code is commented By Sridhar Vemulapalli on 05-Jul-2011  No use in Panama
	/*	boolean isSupportedMTI = (value.equals(LRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)||
				value.equals(LRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID)||
				value.equals(LRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID)||
				value.equals(LRPMessageTypeIDConstant.RECHARGE_INQUIRY_MSG_TYPE_ID)
				);*/
		boolean isSupportedMTI = (value.equals(LRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)||
				value.equals(LRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID)||
				value.equals(LRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID) || value.equals(LRPMessageTypeIDConstant.REVERSAL_RETRY_MSG_TYPE_ID));		

		if (!isSupportedMTI)
		{
		    return ResponseCodeConstant.INITIAL_STATE;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}


    public static String isValidProcessingCode(String mti, String value){

    	

		boolean isSupported = false;
		/**
		 * Modified for columbia Telefonica.
		 * echo service calling eithr processing code is empty or 990000
		 */
		if(!(mti.equals(LRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)))   
		{
			if (!PMISOFieldValidator.isValueExists(value)){
				return ResponseCodeConstant.INVALID_PROCESSING_CODE;
				}
				
			if (!PMISOFieldValidator.isNumericValue(value)){
				return ResponseCodeConstant.INVALID_PROCESSING_CODE;
				}
		}

		if(mti.equals(LRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
			{
			/*if(value.equals(DMMessageTypeIDConstant.ECHO_PROCESSING_CODE_ID)){
				isSupported = true;
			}*/
				isSupported = true;
			}
		else if(mti.equals(LRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID))
			{
				/*commented for columbia implementation
				 * if ((value.equals(DMMessageTypeIDConstant.SYNC_MIN_VALIDATION_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.UNIFIED_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID))){
					isSupported = true;
				}
				*/
			
			   if(value.equals(LRPMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID))
				   isSupported = true;
				
			}
			else if(mti.equals(LRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID))
			{
				/* commented for columbia implementation
				if ((value.equals(DMMessageTypeIDConstant.SYNC_REVERSAL_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_REVERSAL_PROCESSING_CODE_ID)))
				{
					isSupported = true;
				}
				*/
				if(value.equals(LRPMessageTypeIDConstant.SYNC_REVERSAL_PROCESSING_CODE_ID))
						isSupported = true;	
				
			}
			else if(mti.equals(LRPMessageTypeIDConstant.REVERSAL_RETRY_MSG_TYPE_ID))
			{
				if(value.equals(LRPMessageTypeIDConstant.SYNC_REVERSAL_RETRY_PROCESSING_CODE_ID))
						isSupported = true;	
				
			}		
		//Commented by Sridhar Vemulapalli because no use in Panama
/*		else if(mti.equals(LRPMessageTypeIDConstant.RECHARGE_INQUIRY_MSG_TYPE_ID))
			{
				if(value.equals(LRPMessageTypeIDConstant.SYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID)){
					isSupported = true;
				}
			}*/
		/* commented for columbia
		else if(mti.equals(DMMessageTypeIDConstant.REVERSAL_INQUIRY_MSG_TYPE_ID))
		{
			if(value.equals(DMMessageTypeIDConstant.SYNC_REVERSAL_INQUIRY_PROCESSING_CODE_ID)){
				isSupported = true;
			}
		}*/
		if (!isSupported){
			return ResponseCodes.INVALID_PROCESSING_CODE;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}


}
