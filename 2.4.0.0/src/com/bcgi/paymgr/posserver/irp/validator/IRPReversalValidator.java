package com.bcgi.paymgr.posserver.irp.validator;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageDataConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWAccountReversalDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.common.util.DateUtil;

public class IRPReversalValidator {

	public static String isValidReqData(POSGWAccountReversalDTO posGWAccountReversalDTO){
			
	
		return isValidData(posGWAccountReversalDTO);


	}

	private static String isValidData(POSGWAccountReversalDTO posGWAccountReversalDTO)
	{

		String transactionAmount = posGWAccountReversalDTO.getTransactionAmount();
    	String status = IRPReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}

		
		String transactionDateTime = posGWAccountReversalDTO.getTransactionDateTime();
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
		
		String systemTraceAuditNum = posGWAccountReversalDTO.getSystemTraceAuditNumber();
    	status = IRPReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String captureMonthDay = posGWAccountReversalDTO.getCaptureDate();
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
				
		
		String modality = posGWAccountReversalDTO.getModality();
    	status = IRPReqMsgDataValidator.isValidModality(modality);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
						
		POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountReversalDTO.getIrMessageDTO();
		String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();
		status = IRPReqMsgDataValidator.isValidOriginCurrencyCode(originCurrencyCode);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String referenceNumber1 = posGWAccountReversalDTO.getReferenceNumber1();
		String referenceNumber2 = posGWAccountReversalDTO.getReferenceNumber2();
				
		status = IRPReqMsgDataValidator.isValidReferenceNumber(referenceNumber1,referenceNumber2);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		String rechargeReferenceNumber = posGWAccountReversalDTO.getRechargeReferenceNumber();
		String rechargeAuthorizationNumber = posGWAccountReversalDTO.getRechargeAuthorizationNumber();
				
		String orderId = posGWAccountReversalDTO.getOrderId();
		String validationId = posGWAccountReversalDTO.getValidationId();
		
		status = IRPReqMsgDataValidator.isValidReferenceNumberAuthorizatonNumber(rechargeReferenceNumber,rechargeAuthorizationNumber);
		if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		if(IRPMessageTypeIDConstant.IRP_ASYNC_MIN_REVERSAL_PROCESSING_CODE_ID.equals(posGWAccountReversalDTO.getProcessingCode())){
			if(orderId != null){
				status = IRPReqMsgDataValidator.isValidOrderID(orderId);
				if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					return status;
				}				
			}
			if(validationId != null){
				status = IRPReqMsgDataValidator.isValidValidationID(validationId);
				if (!status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					return status;
				}				
			}			
			}
		return ResponseCodes.INTERNAL_SUCCESS_STATUS;
	}



}
