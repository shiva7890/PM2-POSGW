package com.bcgi.paymgr.posserver.domestic.ecuador.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWEchoDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;


public class DMEchoValidator {

	public static String isValidReqData(POSGWMessageDTO posGWEchoDTO){
		//System.out.println("posGWEchoDTO==>" + posGWEchoDTO);
		return isValidData((POSGWEchoDTO)posGWEchoDTO);
	}

	private static String isValidData(POSGWEchoDTO posGWEchoDTO)
	{
        //System.out.println("Entered into the isValidData......");
		String status = "";
		
		//Distributor Id
    	String distributorId = posGWEchoDTO.getDistributorId();
    	status = DMReqMsgDataValidator.isValidDistributorId(distributorId);
    	
    	//System.out.println("Distributor Validated==>" + status);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//SalesPerson Id
    	String salesPersonId = posGWEchoDTO.getStoreId();
    	status = DMReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
    	//System.out.println("Salesperson Validated==>" + status);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    	
    	//subdistributor Id 
    	//Below subDistributor Id validation is added for Defects: #17078 and #17080
    	String subDistributorId = posGWEchoDTO.getSubAgentId();
    	if (subDistributorId != null && subDistributorId.trim().length() > 0){
 		   
    		status = DMReqMsgDataValidator.isValidSubDistributorId(subDistributorId);
    	//System.out.println("Subdistributor Validated==>" + status);
    		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
    			return status;
    		}
    	}   	
    	  	
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}
}