package com.bcgi.paymgr.posserver.domestic.columbia.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;


public class LRCSubAccntValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}
	
	public static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		    String status = "";
			
	    	//Subscriber Number
	    	String subscriberId = "";
	    	String subscriberId1 = posGWAccountRechargeDTO.getSubscriberId1();
	    	String subscriberId2 = posGWAccountRechargeDTO.getSubscriberId2();
	    	String subscriberId3 = posGWAccountRechargeDTO.getSubscriberId3();
	    	if (subscriberId1 != null){
	    		subscriberId = subscriberId1;
	    	}
	    	else if (subscriberId2 != null){
	    		subscriberId = subscriberId2;
	    	}
	    	else if (subscriberId3 != null){
	    		subscriberId = subscriberId3;
	    	}
	    	status = LRCReqMsgDataValidator.isValidSubscriberNumberExists(subscriberId);
	       	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
	    		    	    	
			//System Trace Audit Number
			String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
	    	status = LRCReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			//Transaction Time
			String transactionTime = posGWAccountRechargeDTO.getTransactionTime();
			//Modified on 20Feb08 for making Mandatory field
				status = LRCReqMsgDataValidator.isValidTime(transactionTime);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
			
			//Transaction Date
			String transactionDate = posGWAccountRechargeDTO.getTransactionDate();
			//Modified on 20Feb08 for making Mandatory field
				status = LRCReqMsgDataValidator.isValidDate(transactionDate);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					return status;
				}
		    
			//Distributor Id
	    	String distributorId = posGWAccountRechargeDTO.getDistributorId();
	    	status = LRCReqMsgDataValidator.isValidDistributorId(distributorId);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			
			//SalesPerson Id
	    	String salesPersonId = posGWAccountRechargeDTO.getStoreId();
	    	status = LRCReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
	    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
	    	
	    	//subdistributor Id 
	    	//Below subDistributor Id validation is added for Defects: #17078 and #17080
	    	String subDistributorId = posGWAccountRechargeDTO.getSubAgentId();
	    	if (subDistributorId != null && subDistributorId.trim().length() > 0){
	 		   
	    	status = LRCReqMsgDataValidator.isValidSubDistributorId(subDistributorId);
	    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
	    	}   	
           //Validation  Amount field is added for the Async Phase III on date 23/09/2009
	    	String transactionAmount = posGWAccountRechargeDTO.getTransactionAmount();
	    	status = LRCReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}  	
			return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

	


}
