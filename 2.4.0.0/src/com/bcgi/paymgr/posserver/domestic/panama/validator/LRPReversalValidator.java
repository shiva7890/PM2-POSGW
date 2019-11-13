package com.bcgi.paymgr.posserver.domestic.panama.validator;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;

public class LRPReversalValidator {
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRPReversalValidator.class);
	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		
		return isValidData(posGWAccountRechargeDTO);
		
	}

	private static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO)
	{
        String status = "";
		
    	//Subscriber Number
        /* 
         * commented for columbia
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
    	*/
        
        /*
    	status = DMReqMsgDataValidator.isValidSubscriberNumber(subscriberId);
       	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		*/
    	
    	//Transaction Amount (4 - Field)
    	String transactionAmount = posGWAccountRechargeDTO.getTransactionAmount();
    	status = LRPReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    	// End
    	
		//Transaction Date and Time (7 - Field)
		String transDateTime = posGWAccountRechargeDTO.getTransactionDateTime();
		status = LRPReqMsgDataValidator.isValidDateTime(transDateTime);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    	//End
		
		//System Trace Audit Number (11 - field)
		String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
    	status = LRPReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
		
		//start changes by ayub panama
		//Transaction Time (12 - Field)
		String transactionTime = posGWAccountRechargeDTO.getTransactionTime();
		status = LRPReqMsgDataValidator.isValidTime(transactionTime);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		
		//Transaction Date(13 - Field)
		String transactionDate = posGWAccountRechargeDTO.getTransactionDate();
		status = LRPReqMsgDataValidator.isValidDate(transactionDate);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}			
		//End
	
		//settlement date (15 field)
    	String settlmentDate = posGWAccountRechargeDTO.getSettlmentDate();
    	status = LRPReqMsgDataValidator.isValidSetlmentDate(settlmentDate);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
    	//end changes by ayub panama
    	
		//Capture Date (17 -Field)
		String captureDate = posGWAccountRechargeDTO.getCaptureDate();
		status = LRPReqMsgDataValidator.isValidCaptureDate(captureDate);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
		
		//start changes by ayub panama
		//Distributor ID (32 field)
		String distributorId = posGWAccountRechargeDTO.getDistributorId();
    	status = LRPReqMsgDataValidator.isValidDistributorId(distributorId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//end changes by ayub panama
		
		//Modality
		//Commneted by Sridhar Vemulapalli on 05-jul-2011 -- No use in Panama 
/*		String modality = posGWAccountRechargeDTO.getModality();
		if(modality!=null && modality.length() > 0) {
	    	status = LRPReqMsgDataValidator.isValidModality(modality);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
		}*/
		
		//start changes by ayub panama
		//Track 2 data (35 field)
		String track2Data = posGWAccountRechargeDTO.getTrack2Data();
    	status = LRPReqMsgDataValidator.isValidTrack2Data(track2Data);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
		
		//Retrieval Reference Number(37 field)
		String referenceNumber = posGWAccountRechargeDTO.getReferenceNumber();
		status = LRPReqMsgDataValidator.isValidRRN1(referenceNumber);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//end changes by ayub panama
		
		//start changes by ayub panama
		//Authorization Identification Response(38 field)
		String authorizationNum = posGWAccountRechargeDTO.getAuthorizationNumber();
		logger.info("Autherization Number in Validator" + authorizationNum);
    	status = LRPReqMsgDataValidator.isValidAuthorizationNum(authorizationNum);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
			}
		//End
		
		//Response Code(39 field)
		String responseCode = posGWAccountRechargeDTO.getResponceCodeOfRecharge();
	    	status = LRPReqMsgDataValidator.isValidResponseCode(responseCode);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
		
		//Card Acceptor Terminal Identification (41 field)
		//SalesPerson Id[as per ISO Doc,Terminal ID is considering as Salespersion id]
		String salesPersonId = posGWAccountRechargeDTO.getStoreId();
		status = LRPReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
		     return status;
		   }
		//end changes by ayub panama 	
    	
    	//Card Acceptor Name/Location (43 field)
		String cardAcceptorName = posGWAccountRechargeDTO.getCardAcceptorName();
		status = LRPReqMsgDataValidator.isValidCardAcceptorName(cardAcceptorName);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
		
		// Currency Code (49 field)
		POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountRechargeDTO.getMessageDTO();
		String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();
		status = LRPReqMsgDataValidator.isValidOriginCurrencyCode(originCurrencyCode);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
		
		//ATM Terminal Data (60 Field)
		String atmTerminalData = posGWAccountRechargeDTO.getAtmTerminalData();
		status = LRPReqMsgDataValidator.isValidatmTerminalData(atmTerminalData);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
		
		//Card Issuer and Authorizer(a) (61 Field)
		String cardIssuer = posGWAccountRechargeDTO.getCardIssuer();
		status = LRPReqMsgDataValidator.isValidcardIssuer(cardIssuer);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
		
		//Original Data Elements (90 field)
		String dataElements = posGWAccountRechargeDTO.getOriginalDataEleemnts();
		status = LRPReqMsgDataValidator.isValidDataElement(dataElements);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//End
	
		//Account Identification 1 (102 Field)
		String acctId = posGWAccountRechargeDTO.getAccountIdentification();
		status = LRPReqMsgDataValidator.isValidAccountIdentification(acctId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}
		//end
		
		//Token (126 Field)
		String token = posGWAccountRechargeDTO.getToken();
		status = LRPReqMsgDataValidator.isValidToken(token);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}			
	   	//End    	
		return ResponseCodeConstant.INTERNAL_SUCCESS_STATUS;
	}

}
