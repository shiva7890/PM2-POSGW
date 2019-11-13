package com.bcgi.paymgr.posserver.domestic.columbia.validator.coupon;

import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMReqMsgDataValidator;


public class LRCBrandCouponValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}

	private static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO)
	{
		String status = "";
       	
       
		
		//Transaction Date and Time
		String transDateTime = posGWAccountRechargeDTO.getTransactionDateTime();
		status = LRCReqMsgDataValidator.isValidDateTime(transDateTime);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//System Trace Audit Number
		// Modified for columbia, because they send always 0's
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
		String modality = posGWAccountRechargeDTO.getModality();
    	status = LRCReqMsgDataValidator.isValidModality(modality);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		
		//Distributor Id
    	String distributorId = posGWAccountRechargeDTO.getDistributorId();
    	status = LRCReqMsgDataValidator.isValidDistributorId(distributorId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//SalesPerson Id[as per ISO Doc,Terminal ID is considering as Salespersion id(41 field#)]
    	String salesPersonId = posGWAccountRechargeDTO.getStoreId();
    	status = LRCReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}

    	//subdistributor Id [as per ISO doc, subdist id is considering as Merchant ID(42 field #)]
    	String subDistributorId = posGWAccountRechargeDTO.getSubAgentId();
    	status = LRCReqMsgDataValidator.isValidSubDistributorIdForRechargeORReversal(subDistributorId);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
    	}
    	
    	//Subscriber Id(43 field #)
    	String subscriberId = posGWAccountRechargeDTO.getSubscriberMIN();
    	status = LRCReqMsgDataValidator.isValidSubscriberNumber(subscriberId);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
    	}
    	
    	
    	//REQ:1344 Added By Naveen
		String productCode = posGWAccountRechargeDTO.getProductCode();
		System.out.println("productCode"+productCode);
		status = LRCReqMsgDataValidator.isValidProductCode(productCode);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
		{
			System.out.println("Invalid ProductCode::"+status);
			return status;
		}
		String quantity = posGWAccountRechargeDTO.getQuantity();
		System.out.println("quantity"+quantity);
		status = LRCReqMsgDataValidator.isValidQuantity(quantity);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
		{
			System.out.println("Invalid Quantity::"+status);
			return status;
		}
    	
    	
    	
    	
    	//Card Acceptor Name/Location (48 field #)
		String cardAcceptorName = posGWAccountRechargeDTO.getCardAcceptorName();
		/*Commented by sridhar.vemulapalli on 22-Feb-2012 because Columbia Dist will send
		 * Some special characters but the below method will not accept those chars.
		 * the new method isCardAcceptorNameValid will do only null check.*/
		//status = LRCReqMsgDataValidator.isValidCardAcceptorName(cardAcceptorName);
		status = LRCReqMsgDataValidator.isCardAcceptorNameValid(cardAcceptorName);
		/*Ends*/
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//Origin Currency Code (49 field #)
		POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountRechargeDTO.getMessageDTO();
		String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();
		status = LRCReqMsgDataValidator.isValidOriginCurrencyCode(originCurrencyCode);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//Retrieval Reference Number(37 field)
		String referenceNumber1 = posGWAccountRechargeDTO.getReferenceNumber1();
		//Retrieval Reference Number(57 field)
		String referenceNumber2 = posGWAccountRechargeDTO.getReferenceNumber2();
		 
		status = LRCReqMsgDataValidator.isValidReferenceNumber(referenceNumber1,referenceNumber2);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    	// coupon pin
 //		Coupon Number
    	
		 
    	    String couponNumber = posGWAccountRechargeDTO.getPinNumber();
    		status = LRCReqMsgDataValidator.isValidCouponNumber(couponNumber);
    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
    		{
			
    			return status;
    		}
		
		
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS; 
	}

}



