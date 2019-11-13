package com.bcgi.paymgr.posserver.domestic.ecuador.validator.coupon;

import com.bcgi.paymgr.posserver.common.util.DateUtil;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMReqMsgDataValidator;


public class DMCpnRechargeSubAccntValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}
	
	public static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		    String status = "";
			
	    	//Subscriber Number
	    	String subscriberId = "";
	    	subscriberId = posGWAccountRechargeDTO.getSubscriberId1();
	    	
	    	
	    	//subscriber number
	    	status = DMReqMsgDataValidator.isValidSubscriberNumberExists(subscriberId);
	       	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
	    		    	    	
			//System Trace Audit Number
			String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
	    	status = DMReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			//Transaction Time -12 Field 
			String transactionTime = posGWAccountRechargeDTO.getTransactionTime();
			//Modified on 20Feb08 for making Mandatory field
				status = DMReqMsgDataValidator.isValidTime(transactionTime);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			
			//Transaction Date  -13 Field 
			String transactionDate = posGWAccountRechargeDTO.getTransactionDate();
			System.out.println("transactionDate----->"+transactionDate);
			String year = DateUtil.getYear(transactionDate);
			System.out.println("year----->"+year);
			String yeartransactionDate = year+transactionDate;
			System.out.println("yeartransactionDate----->"+yeartransactionDate);
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
           //Validation  Amount field is added for the Async Phase III on date 23/09/2009
	    	String transactionAmount = posGWAccountRechargeDTO.getTransactionAmount();
	    	status = DMReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}  	
			
			//coupon number validation
			
			String couponCode = posGWAccountRechargeDTO.getPinNumber();
			status = DMReqMsgDataValidator.isValidCouponNumber(couponCode);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}			
			
			
			return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

	


}
