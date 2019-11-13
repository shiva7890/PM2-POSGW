package com.bcgi.paymgr.posserver.irp.validator;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;
import com.aircls.common.pos.serverconstants.ResponseCodes;

public class IRPMessageTypeValidator {


	public static String isValidMessageTypeId(String value){

		if (!PMISOFieldValidator.isValueExists(value)){
			return ResponseCodes.INVALID_FORMAT_FOR_MESSAGETYPE_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(value)){
			return ResponseCodes.INVALID_FORMAT_FOR_MESSAGETYPE_ID;
		}

		boolean isSupportedMTI = (value.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID)||
				value.equals(IRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID)||
				value.equals(IRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID)||
				value.equals(IRPMessageTypeIDConstant.ASYNC_RECHARGE_INQUIRY_TRANS_ID)||
				value.equals(IRPMessageTypeIDConstant.TRANSACTION_STATUS_INQUIRY_MSG_TYPE_ID));

		if (!isSupportedMTI)
		{
		    return ResponseCodes.UNSUPPORTED_MESSAGE_TYPE;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}


    public static String isValidProcessingCode(String mti, String value){

    	

		boolean isSupported = false;
		if(mti.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
			{
                 	isSupported = true;
              
			}
		else {
			
			if (!PMISOFieldValidator.isValueExists(value)){
				return ResponseCodes.INVALID_FORMAT_FOR_PROCESSING_CODE;
			}

			if (!PMISOFieldValidator.isNumericValue(value)){
				return ResponseCodes.INVALID_FORMAT_FOR_PROCESSING_CODE;
			}
			
			if(mti.equals(IRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID))
				{				 
				if(value.equals(IRPMessageTypeIDConstant.IRP_MIN_RECHARGE_PROCESSING_CODE_ID)){
					isSupported = true;
				}else if(value.equals(IRPMessageTypeIDConstant.IRP_ASYNC_UNIFIED_RECHARGE_PROCESSING_CODE_ID)){
					isSupported = true;
				}else if(value.equals(IRPMessageTypeIDConstant.IRP_SYNC_MIN_VALIDATION_PROCESSING_CODE_ID)){
					isSupported = true;
				}else if(value.equals(IRPMessageTypeIDConstant.IRP_ASYNC_MIN_RECHARGE_PROCESSING_CODE_ID)){
					isSupported = true;
				}
				
			}
			else if(mti.equals(IRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID))
			{
				if(value.equals(IRPMessageTypeIDConstant.IRP_MIN_REVERSAL_PROCESSING_CODE_ID)){
					isSupported = true;
				}else if(value.equals(IRPMessageTypeIDConstant.IRP_ASYNC_MIN_REVERSAL_PROCESSING_CODE_ID)){
					isSupported = true;
				}
				
			}
			else if(mti.equals(IRPMessageTypeIDConstant.TRANSACTION_STATUS_INQUIRY_MSG_TYPE_ID))
			{
				if(value.equals(IRPMessageTypeIDConstant.IRP_TRANS_STATUS_INQUIRY_PROCESSING_CODE_ID)){
					isSupported = true;
				}else if(value.equals(IRPMessageTypeIDConstant.IRP_ASYNC_REVERSAL_INQUIRY_PROCESSING_CODE_ID)){
					isSupported = true;
				}
			}
			else if(mti.equals(IRPMessageTypeIDConstant.ASYNC_RECHARGE_INQUIRY_TRANS_ID))
			{
				if(value.equals(IRPMessageTypeIDConstant.IRP_ASYNC_RECHARGE_INQUIRY_PROCESSING_CODE_ID)){
					isSupported = true;
				}
			}
		}

		if (!isSupported){
			return ResponseCodes.INVALID_PROCESSING_CODE;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}


}
