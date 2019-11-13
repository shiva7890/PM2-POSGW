package com.bcgi.paymgr.posserver.domestic.columbia.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;


public class LRCRechargeInquiryValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}
	
	public static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		    String status = "";
			
	    	//Transaction Amount
	        /*
	         * Note: Transaction Amt is optional. Validate trans amt only when amt is given
	         * Modified for columbia, as per doc amt field is optional
	         */
	    	String transactionAmount = posGWAccountRechargeDTO.getTransactionAmount();
	    	if(transactionAmount!=null && transactionAmount.length() > 0) {
		    	status = LRCReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
	    	}
	    	
			//Transaction Date and Time
			//Newly added for columbia
			String transDateTime = posGWAccountRechargeDTO.getTransactionDateTime();
			status = LRCReqMsgDataValidator.isValidDateTime(transDateTime);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			//System Trace Audit Number
			//Modified for columbia, because they send always 0's
			String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
	    	status = LRCReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			 //Skipping validation of Expiry Date
			
			//Capture Date
			//Newly addeed for columbia
			String captureDate = posGWAccountRechargeDTO.getCaptureDate();
			status = LRCReqMsgDataValidator.isValidCaptureDate(captureDate);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}

			//Modality
			//Newly added for columbia
			String modality = posGWAccountRechargeDTO.getModality();
			if(modality!=null && modality.length() > 0) {
		    	status = LRCReqMsgDataValidator.isValidModality(modality);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}


			//Distributor Id,not modified, using same
	    	String distributorId = posGWAccountRechargeDTO.getDistributorId();
	    	status = LRCReqMsgDataValidator.isValidDistributorId(distributorId);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			//Authorization Number
			String authorizationNum = posGWAccountRechargeDTO.getAuthorizationNumber();
			if(authorizationNum!=null && authorizationNum.length() > 0) {
		    	status = LRCReqMsgDataValidator.isValidAuthorizationNumber(authorizationNum);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}

			
			//SalesPerson Id
	    	String salesPersonId = posGWAccountRechargeDTO.getStoreId();
		    	if(salesPersonId!=null && salesPersonId.length()>0 ) {
		    	status = LRCReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
		    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
	    	}
	    	
	    	//subdistributor Id 
	    	String subDistributorId = posGWAccountRechargeDTO.getSubAgentId();
	    	if (subDistributorId != null && subDistributorId.trim().length() > 0){
		    	status = LRCReqMsgDataValidator.isValidSubDistributorId(subDistributorId);
		    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
	    	}
	    	
	    	//Subscriber Id(43 field #)
	    	String subscriberId = posGWAccountRechargeDTO.getSubscriberMIN();
	    	status = LRCReqMsgDataValidator.isValidSubscriberNumber(subscriberId);
	    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
	    	}
	    	
	    	//Card Acceptor Name/Location (48 field #)
			String cardAcceptorName = posGWAccountRechargeDTO.getCardAcceptorName();
			if(cardAcceptorName!=null && cardAcceptorName.length() > 0) {
				/*Commented by sridhar.vemulapalli on 22-Feb-2012 because Columbia Dist will send
				 * Some special characters but the below method will not accept those chars.
				 * the new method isCardAcceptorNameValid will do only null check.*/				
				//status = LRCReqMsgDataValidator.isValidCardAcceptorName(cardAcceptorName);
				status = LRCReqMsgDataValidator.isCardAcceptorNameValid(cardAcceptorName);
				/*Ends*/
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}

			//Origin Currency Code (49 field #)
			POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountRechargeDTO.getIrMessageDTO();
			String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();
			if(originCurrencyCode!=null && originCurrencyCode.length()>0) {
				status = LRCReqMsgDataValidator.isValidOriginCurrencyCode(originCurrencyCode);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}
			
			//Retrieval Reference Number(37 field)
			String referenceNumber1 = posGWAccountRechargeDTO.getReferenceNumber1();
			//Retrieval Reference Number(57 field)
			String referenceNumber2 = posGWAccountRechargeDTO.getReferenceNumber2();
			
			status = LRCReqMsgDataValidator.isValidReferenceNumberForOptional(referenceNumber1,referenceNumber2);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

}
