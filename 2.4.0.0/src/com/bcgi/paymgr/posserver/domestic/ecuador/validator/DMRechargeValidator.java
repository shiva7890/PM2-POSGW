package com.bcgi.paymgr.posserver.domestic.ecuador.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMMessageTypeIDConstant;


public class DMRechargeValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}

	private static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO)
	{
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
    	status = DMReqMsgDataValidator.isValidSubscriberNumber(subscriberId);
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
    	 	
    	
    	String packetCode = posGWAccountRechargeDTO.getPacketCode();
    	System.out.println("packetCode :::::>>>>>"+packetCode);
    	 if(packetCode!= null  &&  packetCode.trim().length()>0){
    		 status = DMReqMsgDataValidator.isValidPacketCode(packetCode,posGWAccountRechargeDTO.getProcessingCode());
    		 if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
    				return status;
    			}
    	 }else{
    		 if(DMMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID.equals(posGWAccountRechargeDTO.getProcessingCode())
    					||DMMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(posGWAccountRechargeDTO.getProcessingCode())){
    			 return ResponseCodeConstant.INVALID_PACKET_CODE;
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
    	
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}



}
