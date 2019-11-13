package com.bcgi.paymgr.posserver.domestic.ecuador.validator.coupon;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMReqMsgDataValidator;


public class DMBrandCouponValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}

	private static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO)
	{
        String status = "";
		
    	//Subscriber Number
    	String subscriberId = "";
    	String subscriberId1 = posGWAccountRechargeDTO.getSubscriberId1();
/*    	String subscriberId2 = posGWAccountRechargeDTO.getSubscriberId2();
    	String subscriberId3 = posGWAccountRechargeDTO.getSubscriberId3();
*/    	if (subscriberId1 != null){
    		subscriberId = subscriberId1;
    	}
/*    	else if (subscriberId2 != null){
    		subscriberId = subscriberId2;
    	}
    	else if (subscriberId3 != null){
    		subscriberId = subscriberId3;
    	}
*/    	status = DMReqMsgDataValidator.isValidSubscriberNumber(subscriberId);
       	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    	
    	//Transaction Amount
    	String transactionAmount = posGWAccountRechargeDTO.getTransactionAmount();
    	if(transactionAmount !=null && transactionAmount.length()>0)
    	{
    		status = DMReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
    			return status;
    		}
    	}
    	
		//System Trace Audit Number
		String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
    	status = DMReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
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
    	
    	
    	
    	
    	
    	if(posGWAccountRechargeDTO.getMessageDTO()!=null && posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode()!=null)
    	{
    		String originCurrecyCode = posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode();
    		status = DMReqMsgDataValidator.isValidCurrencyCode(originCurrecyCode);
    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
    			return status;
    		}
    	}
    	//Coupon Number
    	
    	/*if(subscriberId!=null &&  subscriberId.length()>0)
    	{*/
	    	String couponNumber = posGWAccountRechargeDTO.getPinNumber();
	    		status = DMReqMsgDataValidator.isValidCouponNumber(couponNumber);
	    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
	    		{
	    			return status;
	    		}
	    	
    	/*} 
    	else
    	{  
    		//given request belongs to pin purchase
    		//coupon number is giving is invalid while doing pin purchase
	    	String couponNumber = posGWAccountRechargeDTO.getPinNumber();
	    	if (couponNumber != null && couponNumber.trim().length() > 0)
	    	{	   
	    		return ResponseCodeConstant.INVALID_FORMAT_FOR_PIN_PURCHASE;
	    	} 
    	}*/
	    		
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
