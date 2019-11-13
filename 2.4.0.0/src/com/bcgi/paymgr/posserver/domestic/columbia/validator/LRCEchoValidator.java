package com.bcgi.paymgr.posserver.domestic.columbia.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWEchoDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;


public class LRCEchoValidator {

	public static String isValidReqData(POSGWMessageDTO posGWEchoDTO){
		return isValidData((POSGWEchoDTO)posGWEchoDTO);
	}

	private static String isValidData(POSGWEchoDTO posGWEchoDTO)
	{
		String status = "";
		
		
		//newly added for columbia, system trace number
		String sysTraceNum = posGWEchoDTO.getSystemTraceAuditNumber();
		status = LRCReqMsgDataValidator.isValidSystemTraceAuditNumber(sysTraceNum);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		
		//Distributor Id,not modified for clombia, using same
    	String distributorId = posGWEchoDTO.getDistributorId();
    	status = LRCReqMsgDataValidator.isValidDistributorId(distributorId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		// newly added for columbia,Network Management Information Code
		String nwMgtInfoCode = posGWEchoDTO.getNetworkMgmtInfoCode();
    	status = LRCReqMsgDataValidator.isValidNetworkInfoCode(nwMgtInfoCode);
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