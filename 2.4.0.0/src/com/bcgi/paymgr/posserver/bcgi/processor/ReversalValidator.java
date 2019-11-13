package com.bcgi.paymgr.posserver.bcgi.processor;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.aircls.common.pos.dto.POSAccountReversalDTO;

public class ReversalValidator {

	public static String isValidReqData(POSAccountReversalDTO posAccountReversalDTO, boolean isMIN){
		
		if (isMIN)
		{	
			String subscriberMIN = posAccountReversalDTO.getSubscriberMIN();
			String status = ReqMsgDataValidator.isValidSubscriberNumber(subscriberMIN);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
		}	
		return isValidData(posAccountReversalDTO);	
				
		
	}
	
	private static String isValidData(POSAccountReversalDTO posAccountReversalDTO){
			
			
		String transactionAmount = posAccountReversalDTO.getTransactionAmount();
		String status = ReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String referenceNumber = posAccountReversalDTO.getReferenceNumber();
		status = ReqMsgDataValidator.isValidReferenceNumer(referenceNumber);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		
		String transactionTime = posAccountReversalDTO.getTransactionTime();
		status = ReqMsgDataValidator.isValidTransactionTime(transactionTime);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String transactionDate = posAccountReversalDTO.getTransactionDate();
		status = ReqMsgDataValidator.isValidTransactionDate(transactionDate);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		StringBuffer strBuff =new StringBuffer();
		strBuff.append(transactionDate);
		strBuff.append(transactionTime);

		status = ReqMsgDataValidator.isValidDateTime(strBuff.toString());

		System.out.println("status after Date validations====>"+status);

		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}



		String rechargeReferenceNumber = posAccountReversalDTO.getRechargeReferenceNumber();
		status = ReqMsgDataValidator.isValidRechargeReferenceNumer(rechargeReferenceNumber);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
						
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}
	
	
	
}
