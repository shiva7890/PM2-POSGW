package com.bcgi.paymgr.posserver.domestic.ecuador.validator.coupon;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMReqMsgDataValidator;


public class DMRechargeCouponInquiryValidator {

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
	    	
			String orderId = posGWAccountRechargeDTO.getOrderId();
			status = DMReqMsgDataValidator.isValidOrderID(orderId);
			
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}

			//newly added for REQ#1442  Equador 63 for Validating channel id.
        	/*String channelID = posGWAccountRechargeDTO.getReplenishmentChannelId();
			if (channelID != null && channelID.trim().length() > 0) {
	
				status = DMReqMsgDataValidator.isValidChannelId(channelID);
				if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
					return status;
				}
	
			}*/
						return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

}
