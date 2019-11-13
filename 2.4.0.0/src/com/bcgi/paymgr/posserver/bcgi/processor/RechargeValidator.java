package com.bcgi.paymgr.posserver.bcgi.processor;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.aircls.common.pos.dto.POSAccountRechargeDTO;

public class RechargeValidator {

	public static String isValidReqData(POSAccountRechargeDTO posAccountRechargeDTO, boolean isMIN){

		if (isMIN)
		{
			String subscriberMIN = posAccountRechargeDTO.getSubscriberMIN();
			String status = ReqMsgDataValidator.isValidSubscriberNumber(subscriberMIN);

			System.out.println("status===isvalidreddata==>"+status);

			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}

		}
		return isValidData(posAccountRechargeDTO);


	}

	private static String isValidData(POSAccountRechargeDTO posAccountRechargeDTO)
	{

		String transactionAmount = posAccountRechargeDTO.getTransactionAmount();

		System.out.println("transactionAmount===isValidData==>"+transactionAmount);

		String status = ReqMsgDataValidator.isValidTransactionAmount(transactionAmount);

		System.out.println("status===isValidData==>"+status);

		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}

		String referenceNumber = posAccountRechargeDTO.getReferenceNumber();
		status = ReqMsgDataValidator.isValidReferenceNumer(referenceNumber);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}


		String transactionTime = posAccountRechargeDTO.getTransactionTime();
		status = ReqMsgDataValidator.isValidTransactionTime(transactionTime);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}

		String transactionDate = posAccountRechargeDTO.getTransactionDate();
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


		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}



}
