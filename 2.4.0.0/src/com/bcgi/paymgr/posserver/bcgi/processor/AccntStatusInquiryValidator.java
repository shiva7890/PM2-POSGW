package com.bcgi.paymgr.posserver.bcgi.processor;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.aircls.common.pos.dto.POSAccountStatusDTO;

public class AccntStatusInquiryValidator {

	public static String isValidReqData(POSAccountStatusDTO posAccountStatusDTO ){


			String subscriberMIN = posAccountStatusDTO.getSubscriberMIN();
			String status = ReqMsgDataValidator.isValidSubscriberNumber(subscriberMIN);

			System.out.println("status====>"+status);

			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}

			String referenceNumber = posAccountStatusDTO.getReferenceNumber();
			status = ReqMsgDataValidator.isValidReferenceNumer(referenceNumber);

			System.out.println("status====>"+status);

			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}


			String transactionTime = posAccountStatusDTO.getTransactionTime();

			status = ReqMsgDataValidator.isValidTransactionTime(transactionTime);

			System.out.println("status====>"+status);

			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}

			String transactionDate = posAccountStatusDTO.getTransactionDate();
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
