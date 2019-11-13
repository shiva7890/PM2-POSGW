package com.bcgi.paymgr.posserver.domestic.ecuador.validator;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.common.util.PropertiesReader;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMMessageTypeIDConstant;
public class DMMessageTypeValidator {


	public static String isValidMessageTypeId(String value){

		
		if (!PMISOFieldValidator.isValueExists(value)){
			return ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
			//return ResponseCodeConstant.INITIAL_STATE;
		}

		if (!PMISOFieldValidator.isNumericValue(value)){
			return ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
			//return ResponseCodeConstant.INITIAL_STATE;
		}

		boolean isSupportedMTI = (value.equals(DMMessageTypeIDConstant.ECHO_MSG_TYPE_ID)||
				value.equals(DMMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID)||
				value.equals(DMMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID)||
				value.equals(DMMessageTypeIDConstant.RECHARGE_INQUIRY_MSG_TYPE_ID)||
				value.equals(DMMessageTypeIDConstant.REVERSAL_INQUIRY_MSG_TYPE_ID));

		if (!isSupportedMTI)
		{
		    return ResponseCodeConstant.INITIAL_STATE;
		}

		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}


   /* public static String isValidProcessingCode(String mti, String value){

    	

		boolean isSupported = false;
              
		if (!PMISOFieldValidator.isValueExists(value)){
			return ResponseCodeConstant.INVALID_PROCESSING_CODE;
			}
			
		if (!PMISOFieldValidator.isNumericValue(value)){
			return ResponseCodeConstant.INVALID_PROCESSING_CODE;
			}

		if(mti.equals(DMMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
			{
			if(value.equals(DMMessageTypeIDConstant.ECHO_PROCESSING_CODE_ID)){
				isSupported = true;
			}
				isSupported = true;
			}
		else if(mti.equals(DMMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID))
			{
				if ((value.equals(DMMessageTypeIDConstant.SYNC_MIN_VALIDATION_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.UNIFIED_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||


					(value.equals(DMMessageTypeIDConstant.RECHARGE_CPN_REDEEMPTION_PROCESSING_ID))||
					(value.equals(DMMessageTypeIDConstant.BRAND_CPN_REDEEMPTION_PROCESSING_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSING_ID))||
					// 2 -step
					(value.equals(DMMessageTypeIDConstant.CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_ID ))||
					(value.equals(DMMessageTypeIDConstant.CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_ID ))||
					(value.equals(DMMessageTypeIDConstant.CPN_BRAND_ASYNC_VALIDATION_PROCESSING_ID ))||
					(value.equals(DMMessageTypeIDConstant.CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_ID )))
				{
					isSupported = true;
				}
				
			}
			else if(mti.equals(DMMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID))
			{
				if ((value.equals(DMMessageTypeIDConstant.SYNC_REVERSAL_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_REVERSAL_PROCESSING_CODE_ID)))
				{
					isSupported = true;
				}
				
			}
		else if(mti.equals(DMMessageTypeIDConstant.RECHARGE_INQUIRY_MSG_TYPE_ID))
			{
			if((value.equals(DMMessageTypeIDConstant.SYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.RECHARGE_CPN_INQUIRY_PROCESSING_ID)))
	                {
					isSupported = true;
				}
			}
		else if(mti.equals(DMMessageTypeIDConstant.REVERSAL_INQUIRY_MSG_TYPE_ID))
		{
			if(value.equals(DMMessageTypeIDConstant.SYNC_REVERSAL_INQUIRY_PROCESSING_CODE_ID)){
				isSupported = true;
			}
		}
		if (!isSupported){
			return ResponseCodes.INVALID_PROCESSING_CODE;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}*/

    
    // GTQ Introducing 
    public static String isValidProcessingCode(String mti, String value){

    	

		boolean isSupported = false;
              
		if (!PMISOFieldValidator.isValueExists(value)){
			return ResponseCodeConstant.INVALID_PROCESSING_CODE;
			}
			
		if (!PMISOFieldValidator.isNumericValue(value)){
			return ResponseCodeConstant.INVALID_PROCESSING_CODE;
			}

		String mtiprocessingcodes=PropertiesReader.getValue(mti);
		System.out.println("mtiprocessingcodes::from Property file::"+mtiprocessingcodes);
		if(mtiprocessingcodes.contains(value))
			isSupported = true;
		
		/*if(mti.equals(DMMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
			{
			
				isSupported = true;
			}
		else if(mti.equals(DMMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID))
			{
				if ((value.equals(DMMessageTypeIDConstant.SYNC_MIN_VALIDATION_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.UNIFIED_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.RECHARGE_CPN_REDEEMPTION_PROCESSING_ID))||
					(value.equals(DMMessageTypeIDConstant.BRAND_CPN_REDEEMPTION_PROCESSING_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_RECHARGE_CPN_REDEEMPTION_PROCESSING_ID))||
					// 2 -step
					(value.equals(DMMessageTypeIDConstant.CPN_RECHARGE_ASYNC_VALIDATION_PROCESSING_ID))||
					(value.equals(DMMessageTypeIDConstant.CPN_RECHARGE_ASYNC_REEDEMPTION_PROCESSING_ID))||
					(value.equals(DMMessageTypeIDConstant.CPN_BRAND_ASYNC_VALIDATION_PROCESSING_ID))||
					//added for Sim transaction
					(value.equals(DMMessageTypeIDConstant.SYNC_SIM_RECHARGE_MIN_PROCESSOR_ID))||
					(value.equals(DMMessageTypeIDConstant.SIM_ASYNC_VALIDATION_PROCESSING_ID))||
					(value.equals(DMMessageTypeIDConstant.SIM_ASYNC_RECHARGE_PROCESSING_ID))||
					(value.equals(DMMessageTypeIDConstant.SIM_ASYNC_UNIFIED_PROCESSING_ID))||
					//end
					(value.equals(DMMessageTypeIDConstant.CPN_BRAND_ASYNC_REEDEMPTION_PROCESSING_ID))
				)
			
				//set conditions
				
				
				
			}
			else if(mti.equals(DMMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID))
			{
				if ((value.equals(DMMessageTypeIDConstant.SYNC_REVERSAL_PROCESSING_CODE_ID))||
					(value.equals(DMMessageTypeIDConstant.ASYNC_REVERSAL_PROCESSING_CODE_ID)))
				{
					isSupported = true;
	}

			}
		else if(mti.equals(DMMessageTypeIDConstant.RECHARGE_INQUIRY_MSG_TYPE_ID))
			{
				if((value.equals(DMMessageTypeIDConstant.SYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID))||
				(value.equals(DMMessageTypeIDConstant.RECHARGE_CPN_INQUIRY_PROCESSING_ID)))
{
					isSupported = true;
				}
			}
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
