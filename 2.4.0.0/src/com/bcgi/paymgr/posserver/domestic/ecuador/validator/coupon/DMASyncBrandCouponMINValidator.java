package com.bcgi.paymgr.posserver.domestic.ecuador.validator.coupon;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMReqMsgDataValidator;

public class DMASyncBrandCouponMINValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}
	public static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
	    String status = "";
		
    	//Subscriber Number
    	String subscriberId = posGWAccountRechargeDTO.getSubscriberId1();
	    
	    	status = DMReqMsgDataValidator.isValidSubscriberNumberExists(subscriberId);
       	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
       	
      //Transaction Amount
    	String transactionAmount = posGWAccountRechargeDTO.getTransactionAmount();
    	status = DMReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    		    	    	
		//System Trace Audit Number
		String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
    	status = DMReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//Transaction Time
		String transactionTime = posGWAccountRechargeDTO.getTransactionTime();
		//Modified on 20Feb08 for making Mandatory field
			status = DMReqMsgDataValidator.isValidTime(transactionTime);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
		
		//Transaction Date
		String transactionDate = posGWAccountRechargeDTO.getTransactionDate();
		//Modified on 20Feb08 for making Mandatory field
			status = DMReqMsgDataValidator.isValidDate(transactionDate);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
	    
		//Distributor Id
    	String distributorId = posGWAccountRechargeDTO.getDistributorId();
    	status = DMReqMsgDataValidator.isValidDistributorId(distributorId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//SalesPerson Id
    	String salesPersonId = posGWAccountRechargeDTO.getStoreId();
    	status = DMReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    	
    	//subdistributor Id 
    	//Below subDistributor Id validation is added for Defects: #17078 and #17080
    	String subDistributorId = posGWAccountRechargeDTO.getSubAgentId();
    	if (subDistributorId != null && subDistributorId.trim().length() > 0){
    	status = DMReqMsgDataValidator.isValidSubDistributorId(subDistributorId);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    	}  
    	
    	//Coupon Number
    	String couponNumber = posGWAccountRechargeDTO.getPinNumber();
    		status = DMReqMsgDataValidator.isValidCouponNumber(couponNumber);
    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
    		{
    			return status;
    		}
    	
    	
       // Validation Id
			String validationId = posGWAccountRechargeDTO.getValidationId();
	    	status = DMReqMsgDataValidator.isValidValidationID(validationId);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}	
    		
    	
			//REQ:1344 Added By Naveen
    		String productCode = posGWAccountRechargeDTO.getProductCode();
    		System.out.println("productCode"+productCode);
    		status = DMReqMsgDataValidator.isValidProductCode(productCode);
    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
    		{
    			System.out.println("Invalid ProductCode::"+status);
    			return status;
    		}
    		String quantity = posGWAccountRechargeDTO.getQuantity();
    		System.out.println("quantity"+quantity);
    		status = DMReqMsgDataValidator.isValidQuantity(quantity);
    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
    		{
    			System.out.println("Invalid Quantity::"+status);
    			return status;
    		}
    		System.out.println("DMBrandCouponValidator::isValidData(-)::END ");	
			
    	
    	
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
}




}
