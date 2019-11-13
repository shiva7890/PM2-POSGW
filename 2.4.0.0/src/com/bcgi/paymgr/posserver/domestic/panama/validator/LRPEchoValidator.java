package com.bcgi.paymgr.posserver.domestic.panama.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWEchoDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;


public class LRPEchoValidator {

	public static String isValidReqData(POSGWMessageDTO posGWEchoDTO){
		return isValidData((POSGWEchoDTO)posGWEchoDTO);
	}

	private static String isValidData(POSGWEchoDTO posGWEchoDTO)
	{
		String status = "";
		
		//Transaction Date and Time (7 - Field)
		String transDateTime = posGWEchoDTO.getTransactionDateTime();
		status = LRPReqMsgDataValidator.isValidDateTime(transDateTime);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		// End
		
		//System Trace Audit Number (11 - Field)
		String sysTraceNum = posGWEchoDTO.getSystemTraceAuditNumber();
		status = LRPReqMsgDataValidator.isValidSystemTraceAuditNumber(sysTraceNum);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//Distributor Id (32 - Field)
    	String distributorId = posGWEchoDTO.getDistributorId();
    	status = LRPReqMsgDataValidator.isValidDistributorId(distributorId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//Network Management Information Code (Field -70)
		String nwMgtInfoCode = posGWEchoDTO.getNetworkMgmtInfoCode();
    	status = LRPReqMsgDataValidator.isValidNetworkInfoCode(nwMgtInfoCode);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		
		//SalesPerson Id
		/*
		 *  columbia is not using thie field
    	String salesPersonId = posGWEchoDTO.getStoreId();
    	status = DMReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
    	//System.out.println("Salesperson Validated==>" + status);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		*/
    	
    	//subdistributor Id
		/*
		columbia is not using thie field
    	String subDistributorId = posGWEchoDTO.getSubAgentId();
    	if (subDistributorId != null && subDistributorId.trim().length() > 0){
 		   
    		status = DMReqMsgDataValidator.isValidSubDistributorId(subDistributorId);
    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
    			return status;
    		}
    	} 
    	*/  	
    	  	
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
}