package com.bcgi.paymgr.posserver.domestic.columbia.validator;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
public class LRCMessageTypeValidator {


	public static String isValidMessageTypeId(String value){

		
		if (!PMISOFieldValidator.isValueExists(value)){
			return ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
			//return ResponseCodeConstant.INITIAL_STATE;
		}

		if (!PMISOFieldValidator.isNumericValue(value)){
			return ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
			//return ResponseCodeConstant.INITIAL_STATE;
		}

		boolean isSupportedMTI = (value.equals(LRCMessageTypeIDConstant.ECHO_MSG_TYPE_ID)||
				value.equals(LRCMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID)||
				value.equals(LRCMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID)||
				value.equals(LRCMessageTypeIDConstant.RECHARGE_INQUIRY_MSG_TYPE_ID)
				);

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
		if(!(mti.equals(LRCMessageTypeIDConstant.ECHO_MSG_TYPE_ID)))   
		{
			if (!PMISOFieldValidator.isValueExists(value)){
				return ResponseCodeConstant.INVALID_PROCESSING_CODE;
				}
				
			if (!PMISOFieldValidator.isNumericValue(value)){
				return ResponseCodeConstant.INVALID_PROCESSING_CODE;
				}
			if (PMISOFieldValidator.isAllZeroes(value)){
				return ResponseCodeConstant.INVALID_PROCESSING_CODE;
			}			
		}
		/*Following lines of code commented by Sridhar.Vemulapalli on-Sep-2011 to Avoid processing
		 * code evaluation
		 */
		isSupported = true;
		//if(mti.equals(LRCMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
			//{
			/*if(value.equals(DMMessageTypeIDConstant.ECHO_PROCESSING_CODE_ID)){
				isSupported = true;
			}*/
				//isSupported = true;
			//}
		//else if(mti.equals(LRCMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID))
			//{
				/*commented for columbia implementation
				 * if ((value.equals(DMMessageTypeIDConstant.SYNC_MIN_VALIDATION_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.UNIFIED_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID))){
					isSupported = true;
				}
				*/
			
			   //if(value.equals(LRCMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID))
				   //isSupported = true;
				
			//}
			//else if(mti.equals(LRCMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID))
			//{
				/* commented for columbia implementation
				if ((value.equals(DMMessageTypeIDConstant.SYNC_REVERSAL_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_REVERSAL_PROCESSING_CODE_ID)))
				{
					isSupported = true;
				}
				*/
				//if(value.equals(LRCMessageTypeIDConstant.SYNC_REVERSAL_PROCESSING_CODE_ID))
						//isSupported = true;	
				
			//}
		//else if(mti.equals(LRCMessageTypeIDConstant.RECHARGE_INQUIRY_MSG_TYPE_ID))
			//{
				//if(value.equals(LRCMessageTypeIDConstant.SYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID)){
					//isSupported = true;
				//}
			//}
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
