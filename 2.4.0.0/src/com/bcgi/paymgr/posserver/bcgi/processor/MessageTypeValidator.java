package com.bcgi.paymgr.posserver.bcgi.processor;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.aircls.common.pos.serverconstants.RequestConstants;
import com.aircls.common.pos.serverconstants.ResponseCodes;

public class MessageTypeValidator {


	public static String isValidMessageTypeId(String value){

		if (!PMISOFieldValidator.isValueExists(value)){
			return ResponseCodes.INVALID_FORMAT_FOR_MESSAGETYPE_ID;
		}

		if (!PMISOFieldValidator.isNumericValue(value)){
			return ResponseCodes.INVALID_FORMAT_FOR_MESSAGETYPE_ID;
		}

		boolean isSupportedMTI = (value.equals(RequestConstants.ECHO_MSG_TYPE_ID)||
				value.equals(RequestConstants.RECHARGE_AND_ACNT_INQUIRY_MSG_TYPE_ID)||
				value.equals(RequestConstants.REVERSAL_MSG_TYPE_ID));

		if (!isSupportedMTI)
		{
		    return ResponseCodes.UNSUPPORTED_MESSAGE_TYPE;
		}

		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}


    public static String isValidProcessingCode(String mti, String value){

    	if (!PMISOFieldValidator.isValueExists(value)){
			return ResponseCodes.INVALID_FORMAT_FOR_PROCESSING_CODE;
		}

		if (!PMISOFieldValidator.isNumericValue(value)){
			return ResponseCodes.INVALID_FORMAT_FOR_PROCESSING_CODE;
		}

		boolean isSupported = false;
		if(mti.equals(RequestConstants.ECHO_MSG_TYPE_ID))
			{
                if(value.equals(RequestConstants.ECHO_PROCESSING_CODE_ID)){
                	isSupported = true;
                }
			}
		else if(mti.equals(RequestConstants.RECHARGE_AND_ACNT_INQUIRY_MSG_TYPE_ID))
			{
				if(value.equals(RequestConstants.MIN_RECHARGE_PROCESSING_CODE_ID)){
                	isSupported = true;
                }
				else if(value.equals(RequestConstants.PIN_RECHARGE_PROCESSING_CODE_ID)){
					isSupported = true;
				}
				else if(value.equals(RequestConstants.ACCOUNT_INQUIRY_PROCESSING_CODE_ID)){
					isSupported = true;
				}
			}
		else if(mti.equals(RequestConstants.REVERSAL_MSG_TYPE_ID))
			{
				if(value.equals(RequestConstants.MIN_REVERSAL_PROCESSING_CODE_ID)){
                	isSupported = true;
                }
				else if(value.equals(RequestConstants.PIN_REVERSAL_PROCESSING_CODE_ID)){
					isSupported = true;
				}
			}

		if (!isSupported){
			return ResponseCodes.INVALID_PROCESSING_CODE;
		}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}


}
