package com.bcgi.paymgr.posserver.domestic.ecuador.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;


public class DMReversalInquiryValidator {

	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}
	
	public static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		    String status = "";
			
	    	
		    
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
	    	
	    	
	    	//Modified by Vamsi K on 28th January'08 to make Order Id as optional in Phase II(Asynchronous)
			String orderId = posGWAccountRechargeDTO.getOrderId();
			if(orderId != null && orderId.length() !=0){
	    	status = DMReqMsgDataValidator.isValidReversalOrderID(orderId);
			}
			else
				status = ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	    	
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			// END <--
			
			//Subscriber Validation Id: Added by Vamsi K on 23rd January'08: Phase II(Asynchronous): Start -->
			String validationId = posGWAccountRechargeDTO.getValidationId();
	    	if(validationId.length() != 0){
				status = DMReqMsgDataValidator.isValidValidationID(validationId);	    		
	    	}
	    	else
	    		status = ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;

			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
			//End <-- 
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
