package com.bcgi.paymgr.posserver.domestic.ecuador.validator;

import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;

public class DMASyncReversalMINValidator {

	public static String isValidReqData(
			POSGWAccountRechargeDTO posGWAccountRechargeDTO) {
		return isValidData(posGWAccountRechargeDTO);
	}

	public static String isValidData(
			POSGWAccountRechargeDTO posGWAccountRechargeDTO) {
		String status = "";

		//Subscriber Number
		String subscriberId = "";
		String subscriberId1 = posGWAccountRechargeDTO.getSubscriberId1();
		String subscriberId2 = posGWAccountRechargeDTO.getSubscriberId2();
		String subscriberId3 = posGWAccountRechargeDTO.getSubscriberId3();
		if (subscriberId1 != null) {
			subscriberId = subscriberId1;
		} else if (subscriberId2 != null) {
			subscriberId = subscriberId2;
		} else if (subscriberId3 != null) {
			subscriberId = subscriberId3;
		}
		status = DMReqMsgDataValidator
				.isValidSubscriberNumberExists(subscriberId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}

		//	      Transaction Amount
		String transactionAmount = posGWAccountRechargeDTO
				.getTransactionAmount();
		status = DMReqMsgDataValidator
				.isValidTransactionAmount(transactionAmount);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}

		//System Trace Audit Number
		String systemTraceAuditNum = posGWAccountRechargeDTO
				.getSystemTraceAuditNumber();
		//status = DMReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
		int refNumber = 0;

		if (systemTraceAuditNum != null	&& systemTraceAuditNum.trim().length() > 0) {
			
			
			if(PMISOFieldValidator.isAllZeroes(systemTraceAuditNum)) {
				
				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
			}
			else if(PMISOFieldValidator.isNumericValue(systemTraceAuditNum)) {
			refNumber = Integer.parseInt(systemTraceAuditNum);
			}
			else {
				return ResponseCodeConstant.INVALID_FORMAT_FOR_REFERENCE_NUMBER;
			}
		}
		if (refNumber > 0) {
			status = DMReqMsgDataValidator
					.isValidAsynchReversalSystemTraceAuditNumber(systemTraceAuditNum);
		}
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}

		//Transaction Time
		String transactionTime = posGWAccountRechargeDTO.getTransactionTime();
		//Modified on 20Feb08 for making Mandatory field

		status = DMReqMsgDataValidator.isValidTime(transactionTime);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}

		//Transaction Date
		String transactionDate = posGWAccountRechargeDTO.getTransactionDate();
		//Modified on 20Feb08 for making Mandatory field
		status = DMReqMsgDataValidator.isValidDate(transactionDate);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}

		//Distributor Id
		String distributorId = posGWAccountRechargeDTO.getDistributorId();
		status = DMReqMsgDataValidator.isValidDistributorId(distributorId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}

		//SalesPerson Id
		String salesPersonId = posGWAccountRechargeDTO.getStoreId();
		status = DMReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}

		//subdistributor Id 
		//Below subDistributor Id validation is added for Defects: #17078 and #17080
		String subDistributorId = posGWAccountRechargeDTO.getSubAgentId();
		if (subDistributorId != null && subDistributorId.trim().length() > 0) {

			status = DMReqMsgDataValidator
					.isValidSubDistributorId(subDistributorId);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
				return status;
			}
		}

		//Order Id and Subscriber Validation Id: Added by Vamsi K on 25th January'08: Phase II(Asynchronous): Start -->
		String orderId = "";
		if (posGWAccountRechargeDTO.getOrderId() != null)
			orderId = posGWAccountRechargeDTO.getOrderId();

		if (orderId != null && orderId.length() != 0) {
			status = DMReqMsgDataValidator.isValidOrderID(orderId);
		} else
			status = ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}
		//End <--

		String validationId = "";

		if (posGWAccountRechargeDTO.getValidationId() != null)
			validationId = posGWAccountRechargeDTO.getValidationId();

		if (validationId.length() != 0) {
			status = DMReqMsgDataValidator.isValidValidationID(validationId);
		} else
			status = ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
			return status;
		}
		//End <--
		
                if (refNumber == 0 && validationId == null && orderId == null) {
			return status = ResponseCodeConstant.ALL_REV_OPTIONAL_FILEDS_NOT_PRESENT;

		} else if (refNumber == 0 && validationId.trim().length() == 0 && orderId.trim().length() == 0) {
			return status = ResponseCodeConstant.ALL_REV_OPTIONAL_FILEDS_NOT_PRESENT;
		} 

        if(posGWAccountRechargeDTO.getMessageDTO()!=null && posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode()!=null)
        {
        	String originCurrecyCode = posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode();
        	status = DMReqMsgDataValidator.isValidCurrencyCode(originCurrecyCode);
        	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
        		return status;
        	}
        }
       
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

}
