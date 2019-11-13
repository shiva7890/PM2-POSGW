package com.bcgi.paymgr.posserver.domestic.panama.validator;

import org.jboss.logging.Logger;

import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.domestic.panama.processor.LRPRechargeMINProcessor;


public class LRPRechargeValidator {
	static Logger logger	= Logger.getLogger(LRPRechargeValidator.class);
	public static String isValidReqData(POSGWAccountRechargeDTO posGWAccountRechargeDTO){
		return isValidData(posGWAccountRechargeDTO);
	}

	private static String isValidData(POSGWAccountRechargeDTO posGWAccountRechargeDTO)
	{
		String status = "";
	  	//Subscriber Number
	    /*
	     * Note: commented for columbia ,because there is no multiple fields for subscribers
	     *  
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
	    
       	
       	//Transaction Amount (4 - Field)
    	String transactionAmount = posGWAccountRechargeDTO.getTransactionAmount();
    	status = LRPReqMsgDataValidator.isValidTransactionAmount(transactionAmount);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  4:"+ status);
			return status;
		}
		// End
		
		//Transaction Date and Time (7 - Field)
		String transDateTime = posGWAccountRechargeDTO.getTransactionDateTime();
		status = LRPReqMsgDataValidator.isValidDateTime(transDateTime);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  7:"+ status);
			return status;
		}
		// End
		
		//System Trace Audit Number (11 - Field)
		String systemTraceAuditNum = posGWAccountRechargeDTO.getSystemTraceAuditNumber();
    	status = LRPReqMsgDataValidator.isValidSystemTraceAuditNumber(systemTraceAuditNum);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  11:"+ status);
			return status;
		}
		
		//Transaction Time (12 - Field)
		String transactionTime = posGWAccountRechargeDTO.getTransactionTime();
		logger.info("The Status of Field 12 :"+ transactionTime);
			status = LRPReqMsgDataValidator.isValidTime(transactionTime);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				logger.info("The Status of Field 12 :"+ status);
				return status;
			}
		//End
		
		//Transaction Date(13 - Field)
		String transactionDate = posGWAccountRechargeDTO.getTransactionDate();
			status = LRPReqMsgDataValidator.isValidDate(transactionDate);
			if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				logger.info("The Status of Field  13:"+ status);
				return status;
			}
		//End

        //Skipping validation of Settlement Date
		
		//Capture Date (17 Field)
		String captureDate = posGWAccountRechargeDTO.getCaptureDate();
		status = LRPReqMsgDataValidator.isValidCaptureDate(captureDate);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  17:"+ status);
			return status;
		}
		//End
	    
		//Distributor Id (32-Field)
    	String distributorId = posGWAccountRechargeDTO.getDistributorId();
    	status = LRPReqMsgDataValidator.isValidDistributorId(distributorId);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  32:"+ status);
			return status;
		}
		//End

		//Track2Data (35 - Field)
    	String track2Data = posGWAccountRechargeDTO.getTrack2Data();
    	status = LRPReqMsgDataValidator.isValidTrack2Data(track2Data);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field 35 :"+ status);
			return status;
		}		
		//End
		
		//Retrieval Reference Number(37 field)
		String referenceNumber = posGWAccountRechargeDTO.getReferenceNumber();
		status = LRPReqMsgDataValidator.isValidRRN1(referenceNumber);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  37:"+ status);
			return status;
		}
		//End
		
		//SalesPerson Id[as per ISO Doc,Terminal ID is considering as Salespersion id(41 field#)]
    	String salesPersonId = posGWAccountRechargeDTO.getStoreId();
    	status = LRPReqMsgDataValidator.isValidSalesPersonId(salesPersonId);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
    		logger.info("The Status of Field  41:"+ status);
			return status;
		}
    	// End
		
    	//Commneted By Sridhar Vemulapalli -on 05-Jul-2011-- Not Used in Panama
	    	/*//subdistributor Id [as per ISO doc, subdist id is considering as Merchant ID(42 field #)]
	    	String subDistributorId = posGWAccountRechargeDTO.getSubAgentId();
	    	status = LRPReqMsgDataValidator.isValidSubDistributorId(subDistributorId);
	    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				return status;
	    	}*/
    	//End
    	
    	//Subscriber Number is a part of the 126 field
    	String subscriberId = posGWAccountRechargeDTO.getSubscriberMIN();
    	status = LRPReqMsgDataValidator.isValidSubscriberNumber(subscriberId);
    	if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
    		logger.info("The Status of Field  126:"+ status);
			return status;
    	}
    	// Ends
    	
    	//Card Acceptor Name/Location (43 field #)
		String cardAcceptorName = posGWAccountRechargeDTO.getCardAcceptorName();
		status = LRPReqMsgDataValidator.isValidCardAcceptorName(cardAcceptorName);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  43:"+ status);
			return status;
		}
		//Ends
		
    	//AdditionalData (48 field #)
		String additionalData = posGWAccountRechargeDTO.getAdditionalData();
		status = LRPReqMsgDataValidator.isValidAdditionalData(additionalData);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  48:"+ status);
			return status;
		}
		//Ends		
		
		//Origin Currency Code (49 field #)
		POSGWIRMessageDTO posGWIRMessageDTO = posGWAccountRechargeDTO.getMessageDTO();
		String originCurrencyCode = posGWIRMessageDTO.getOriginCurrencyCode();
		status = LRPReqMsgDataValidator.isValidOriginCurrencyCode(originCurrencyCode);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			logger.info("The Status of Field  49:"+ status);
			return status;
		}
		//End
    	
    	//ATM Terminal Data (60 Field)
		String atmTerminalData = posGWAccountRechargeDTO.getAtmTerminalData();
		status = LRPReqMsgDataValidator.isValidatmTerminalData(atmTerminalData);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}		
		//Ends
		
		//Card Issuer and Authorizer(a) (61 Field)
		String cardIssuer = posGWAccountRechargeDTO.getCardIssuer();
		status = LRPReqMsgDataValidator.isValidcardIssuer(cardIssuer);
		if (!status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
			return status;
		}			
		//End
		
		//Receiving Institution Identification Code (100 Field)
		String recvInstitution = posGWAccountRechargeDTO.getReceivingInstitutionCode();
		status = LRPReqMsgDataValidator.isValidReceivingInstitution(recvInstitution);
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
		//End	
		
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
