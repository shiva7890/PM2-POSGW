package com.bcgi.paymgr.posserver.irp.validator;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAccountStatusDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.common.util.DateUtil;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;

public class IRPTranStatusInqValidator {

	public static String isValidReqData(POSGWAccountStatusDTO posGWAccountStatusDTO){
			
	
		return isValidData(posGWAccountStatusDTO);


	}

	private static String isValidData(POSGWAccountStatusDTO posGWAccountStatusDTO)
	{

		String transactionAmount = posGWAccountStatusDTO.getTransactionAmount();
    	String status = IRPReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}

		
		String transactionDateTime = posGWAccountStatusDTO.getTransactionDateTime();
        status = IRPReqMsgDataValidator.isValidTransactionDateTime(transactionDateTime);
		System.out.println("step1");
        if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
		{
        	System.out.println("step2");
			String monthday = transactionDateTime.substring(0,IRPMessageDataConstant.TRANSACTION_DATE_END_POSITION);
			String year = DateUtil.getYear(monthday);
			String transactionDate = year+monthday;
			System.out.println("step3");
			System.out.println("transactionDate"+transactionDate);
			status = IRPReqMsgDataValidator.isValidTransactionDate(transactionDate);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			System.out.println("step4");
			String transactionTime = transactionDateTime.substring(IRPMessageDataConstant.TRANSACTION_TIME_START_POSITION,transactionDateTime.length());
			System.out.println("transactionTime:"+transactionTime);
			status = IRPReqMsgDataValidator.isValidTransactionTime(transactionTime);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
		}
		else{
			return status;
		}
		
		String systemTraceAuditNum = posGWAccountStatusDTO.getSystemTraceAuditNumber();
    	status = IRPReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String captureMonthDay = posGWAccountStatusDTO.getCaptureDate();
    	status = IRPReqMsgDataValidator.isValidCaptureMonthDay(captureMonthDay);
		if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS))
		{
			String year = DateUtil.getYear(captureMonthDay);
			String captureDate = year+captureMonthDay;
			
			status = IRPReqMsgDataValidator.isValidCaptureDate(captureDate);
			if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
		}
		else
		{
			return status;
		}
				
		
		String modality = posGWAccountStatusDTO.getModality();
    	status = IRPReqMsgDataValidator.isValidModality(modality);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
			
		
				
		POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountStatusDTO.getIrMessageDTO();
		String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();
		status = IRPReqMsgDataValidator.isValidOriginCurrencyCode(originCurrencyCode);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String referenceNumber1 = posGWAccountStatusDTO.getReferenceNumber1();
		String referenceNumber2 = posGWAccountStatusDTO.getReferenceNumber2();
		String authorizationNumber = posGWAccountStatusDTO.getAuthorizationNumber();
		status = IRPReqMsgDataValidator.isValidReferenceNumberAuthorizatonNumber(referenceNumber1,referenceNumber2,authorizationNumber);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
	
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}



}

