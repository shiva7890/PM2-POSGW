package com.bcgi.paymgr.posserver.irp.validator;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.common.util.DateUtil;

public class IRPRechargeValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
			
	
		return isValidData(posGWAccountRechargeDTO);


	}

	private static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO)
	{

		String transactionAmount = posGWAccountRechargeDTO.getTransactionAmount();
    	String status = IRPReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}

		
		String transactionDateTime = posGWAccountRechargeDTO.getTransactionDateTime();
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
		
		String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
    	status = IRPReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String captureMonthDay = posGWAccountRechargeDTO.getCaptureDate();
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
		
		String posEntryMode = posGWAccountRechargeDTO.getPosEntryMode();
    	status = IRPReqMsgDataValidator.isValidPOSEntryMode(posEntryMode);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String modality = posGWAccountRechargeDTO.getModality();
    	status = IRPReqMsgDataValidator.isValidModality(modality);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		
		
		String cardAcceptorName = posGWAccountRechargeDTO.getCardAcceptorName();
		status = IRPReqMsgDataValidator.isValidCardAcceptorName(cardAcceptorName);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
				
		POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountRechargeDTO.getIrMessageDTO();
		String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();
		status = IRPReqMsgDataValidator.isValidOriginCurrencyCode(originCurrencyCode);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String referenceNumber1 = posGWAccountRechargeDTO.getReferenceNumber1();
		String referenceNumber2 = posGWAccountRechargeDTO.getReferenceNumber2();
		
		status = IRPReqMsgDataValidator.isValidReferenceNumber(referenceNumber1,referenceNumber2);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
	
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}



}
