package com.bcgi.paymgr.posserver.domestic.panama.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;


public class LRPRechargeInquiryValidator {

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
		    	status = LRPReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
	    	}
	    	
			//Transaction Date and Time
			//Newly added for columbia
			String transDateTime = posGWAccountRechargeDTO.getTransactionDateTime();
			status = LRPReqMsgDataValidator.isValidDateTime(transDateTime);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			//System Trace Audit Number
			//Modified for columbia, because they send always 0's
			String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
	    	status = LRPReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			 //Skipping validation of Expiry Date
			
			//Capture Date
			//Newly addeed for columbia
			String captureDate = posGWAccountRechargeDTO.getCaptureDate();
			status = LRPReqMsgDataValidator.isValidCaptureDate(captureDate);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}

			//Modality
			//Commented by Sridhar Vemulapalli on 05-Jul-2011 -- No use in Panama
/*			String modality = posGWAccountRechargeDTO.getModality();
			if(modality!=null && modality.length() > 0) {
		    	status = LRPReqMsgDataValidator.isValidModality(modality);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}*/


			//Distributor Id,not modified, using same
	    	String distributorId = posGWAccountRechargeDTO.getDistributorId();
	    	status = LRPReqMsgDataValidator.isValidDistributorId(distributorId);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			//Authorization Number
			String authorizationNum = posGWAccountRechargeDTO.getAuthorizationNumber();
			if(authorizationNum!=null && authorizationNum.length() > 0) {
		    	status = LRPReqMsgDataValidator.isValidAuthorizationNumber(authorizationNum);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}

			
			//SalesPerson Id
	    	String salesPersonId = posGWAccountRechargeDTO.getStoreId();
		    	if(salesPersonId!=null && salesPersonId.length()>0 ) {
		    	status = LRPReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
		    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
	    	}
	    	
	    	//subdistributor Id 
	    	String subDistributorId = posGWAccountRechargeDTO.getSubAgentId();
	    	if (subDistributorId != null && subDistributorId.trim().length() > 0){
		    	status = LRPReqMsgDataValidator.isValidSubDistributorId(subDistributorId);
		    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
	    	}
	    	
	    	//Subscriber Id(43 field #)
	    	String subscriberId = posGWAccountRechargeDTO.getSubscriberMIN();
	    	status = LRPReqMsgDataValidator.isValidSubscriberNumber(subscriberId);
	    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
	    	}
	    	
	    	//Card Acceptor Name/Location (48 field #)
			String cardAcceptorName = posGWAccountRechargeDTO.getCardAcceptorName();
			if(cardAcceptorName!=null && cardAcceptorName.length() > 0) {
				status = LRPReqMsgDataValidator.isValidCardAcceptorName(cardAcceptorName);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}

			//Origin Currency Code (49 field #)
			POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountRechargeDTO.getIrMessageDTO();
			String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();
			if(originCurrencyCode!=null && originCurrencyCode.length()>0) {
				status = LRPReqMsgDataValidator.isValidOriginCurrencyCode(originCurrencyCode);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			}
			
			//Retrieval Reference Number(37 field)
			String referenceNumber1 = posGWAccountRechargeDTO.getReferenceNumber1();
			//Retrieval Reference Number(57 field)
			String referenceNumber2 = posGWAccountRechargeDTO.getReferenceNumber2();
			
			status = LRPReqMsgDataValidator.isValidReferenceNumberForOptional(referenceNumber1,referenceNumber2);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			

			/*String orderId = posGWAccountRechargeDTO.getOrderId();
			if(orderId != null && orderId.length() !=0){
	    	status = DMReqMsgDataValidator.isValidOrderID(orderId);
			}
			else
				status = ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	    	
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			*/
			//End <--

			//Subscriber Validation Id: Added by Vamsi K on 22nd January'08: Phase II(Asynchronous): Start -->
			/*String validationId = posGWAccountRechargeDTO.getValidationId();
	    	if(validationId.length() != 0){
				status = DMReqMsgDataValidator.isValidValidationID(validationId);	    		
	         }
	    	else
	    		status = ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			//End <--
			 
			 */
			
	
			return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

}
