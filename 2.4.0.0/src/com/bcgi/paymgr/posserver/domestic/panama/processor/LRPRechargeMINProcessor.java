package com.bcgi.paymgr.posserver.domestic.panama.processor;

import java.util.Calendar;

import org.jboss.logging.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.bcgi.paymgr.posserver.common.constant.AppConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.POSGW_InternalResponseConstant;
import com.bcgi.paymgr.posserver.domestic.panama.delegate.LRPTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.panama.validator.LRPRechargeValidator;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;
import com.bcgi.pmcore.vo.ConnectionVO;

public class LRPRechargeMINProcessor extends DMBaseMessageProcessor{
	static Logger logger	= Logger.getLogger(LRPRechargeMINProcessor.class);

	public LRPRechargeMINProcessor()
	{

	}
	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){
		logger.info("Domestic Panama Recharge MIN Processor- execute()........"+posGWMessageDTO);
		ISOMsg responseISOMsg = requestISOMsg;
		
        try{
			Calendar startCal = Calendar.getInstance();
			POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
			if (posGWAccountRechargeDTO.getResponseCode().equals(ResponseCodeConstant.INITIAL_STATE)){
				  String status = isValidMessage(posGWAccountRechargeDTO);
	
				  if (status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
					  posGWAccountRechargeDTO = (POSGWAccountRechargeDTO)processMessage(posGWAccountRechargeDTO);
				  }else{
					  posGWAccountRechargeDTO.setResponseCode(status);
					  posGWAccountRechargeDTO = (POSGWAccountRechargeDTO)processRejectedTransaction(posGWAccountRechargeDTO);
					  
					  //Resetting Domestic Error code as per Panama
					  String posgwErrorCode = POSGW_InternalResponseConstant.getPOSGWResponse(posGWAccountRechargeDTO.getResponseCode());
					  posGWAccountRechargeDTO.setResponseCode(posgwErrorCode);
				  }
			}
	
			responseISOMsg = packISOMsg(requestISOMsg,posGWAccountRechargeDTO);
	
				//Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWAccountRechargeDTO.getPinNumber(),posGWAccountRechargeDTO.getAuthorizationNumber(),posGWAccountRechargeDTO.getSystemTraceAuditNumber(), startCal, endCal,"Domestic Recharge Transaction");
					if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
					else
						AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				// End of AppMoniter Logging
        }catch(Exception exp){
        	exp.printStackTrace();
			try{
				responseISOMsg.set(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID, POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR));
				logger.error("Domestic RechargeMINProcessor-Exception at execute(): "+exp.getMessage());
			}catch(Exception ex){
				logger.error("IRPRechargeMINProcessor-Exception at execute(): "+exp.getMessage());
			}
		}
		return responseISOMsg;
	}
	
	/*
     * Amount	                 				04
     * Transmission Date & Time	 				07
     * System Trace Audit Number 				11
     * Local Transaction Time    				12
     * Local Transaction Date    				13
     * Settlement Date			 				15
     * Capture Date				 				17
     * Acquiring Institution Identification Code32
     * Track2Data								35
     * Retrieval Reference Number				37
     * Terminal ID								41
     * Card Acceptor Name/Location   			43 
     * Additional Data                  		48
     * Transaction Currency Code				49
     * ATM Terminal Data						60
     * Card Issuer and Authorizer(a) 			61
     * Receiving Institution Identification Code100
     * Account Identification 					102 
     * MIN										126
     * Token 									126
     */
	
	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.INITIAL_STATE);


		try{
            String customerType = "1";  //Residential Subscriber
            String topUpId = "1";      //Topup id
            String replenishmentChannelId = "6";//channel POS
            String overrideFraudId = "off";   //do fraud checking in pm
            String paymentTypeId = "1";     //cash

            
            String processingCode=requestISOMsg.getString(DMISOMessageFieldIDConstant.PROCESSING_CODE_ID);
            if(processingCode!=null){
            	processingCode=processingCode.trim();
            }
            posGWAccountRechargeDTO.setProcessingCode(processingCode);
            posGWAccountRechargeDTO.setOperatorID(DataUtil.getMappedOperatorId(processingCode));
            //logger.info("Processing code mapped with operatorIdentity from the ISOMsg::"+posGWAccountRechargeDTO.getMvneId());
            logger.info("processingCode::::::"+processingCode);
           logger.info("DataUtil.getMappedOperatorId(processingCode)::"+DataUtil.getMappedOperatorId(processingCode));
		
            
			// Start Amount Field -  4  
            String amount = requestISOMsg.getString(LRPISOMessageFieldIDConstant.TRANSACTION_AMNT);
			if (amount != null){
				amount = amount.trim();
			}
			posGWAccountRechargeDTO.setTransactionAmount(amount);
			// End Amount Field -  4  
			
			//  Start Transmission Date and Time Field - 7 
			String transactionDateTime = requestISOMsg.getString(LRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME);
			if (transactionDateTime != null){
				transactionDateTime = transactionDateTime.trim();
			}
			posGWAccountRechargeDTO.setTransactionDateTime(transactionDateTime);
			//  End Transmission Date and Time - 7 
			
			//Start System Trace Audit Number Field - 11
			String systemTraceAuditNum = requestISOMsg.getString(LRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER);
			if (systemTraceAuditNum != null){
				systemTraceAuditNum = systemTraceAuditNum.trim();
			}
			posGWAccountRechargeDTO.setSystemTraceAuditNumber(systemTraceAuditNum);
			//End System Trace Audit Number Field - 11			

			//Start Local Transaction Time  Field - 12
			String localTransTime = requestISOMsg.getString(LRPISOMessageFieldIDConstant.LOCAL_TRANSACTION_TIME);
			if (localTransTime != null){
				localTransTime = localTransTime.trim();
			}
			logger.info("The Data of Field no :12 ======>"+localTransTime);
			posGWAccountRechargeDTO.setTransactionTime(localTransTime);
			//End Local Transaction Time  Field - 12
			
			//Start Local Transaction Date  Field - 13
			String localTransDate = requestISOMsg.getString(LRPISOMessageFieldIDConstant.LOCAL_TRANSACTION_DATE);
			if (localTransDate != null){
				localTransDate = localTransDate.trim();
			}
			posGWAccountRechargeDTO.setTransactionDate(localTransDate);
			//End Local Transaction Date  Field - 13			
			
			//Start Capture Date Field - 17 
			String captureDate = requestISOMsg.getString(LRPISOMessageFieldIDConstant.CAPTURE_DATE);
			if (captureDate != null){
				captureDate = captureDate.trim();
			}
			posGWAccountRechargeDTO.setCaptureDate(captureDate);
			//End Capture Date Field - 17 


			//Start Distributor Field - 32			
			String distributorId = requestISOMsg.getString(LRPISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			logger.info("Distributor id from the ISOMsg::"+distributorId);
			if (distributorId != null){
				distributorId = distributorId.trim();
			}
			posGWAccountRechargeDTO.setInitiatedUser(distributorId); //GenBy
			posGWAccountRechargeDTO.setDistributorId(distributorId);
			//End Distributor Field - 32			

			// Start Track2Data Field - 35
			String track2Data = requestISOMsg.getString(LRPISOMessageFieldIDConstant.TRACK2_DATA);
			logger.info("Track2Data::"+track2Data);
			if (track2Data != null){
				track2Data = track2Data.trim();
			}
			posGWAccountRechargeDTO.setTrack2Data(track2Data);
			//End Track2Data Field - 35			
			
			//Start Retrieval Reference Number Filed -37
			String referenceNumber = requestISOMsg.getString(LRPISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER);
			if (referenceNumber != null){
				referenceNumber = referenceNumber.trim();
			}
			posGWAccountRechargeDTO.setReferenceNumber(referenceNumber);
			//End Retrieval Reference Number Filed -37

			// Starts Card Acceptor Terminal Identification Field - 41
			String storemanagerId  = requestISOMsg.getString(LRPISOMessageFieldIDConstant.STORE_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posGWAccountRechargeDTO.setStoreId(storemanagerId);
			// Ends Card Acceptor Terminal Identification Field - 41

			//Subscriber Number is a part of Field no : 126
			String subscriberNum = requestISOMsg.getString(LRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER);
			if (subscriberNum != null){
				subscriberNum = subscriberNum.trim();
				subscriberNum = DataUtil.getRequiredSubscriberNumber(subscriberNum, LRPMessageTypeIDConstant.SUBSCRIBER_NUMBER_START_POSITION, LRPMessageTypeIDConstant.SUBSCRIBER_NUMBER_END_POSITION);
			}
			logger.info("Subscriber Number =======>"+subscriberNum);
			posGWAccountRechargeDTO.setSubscriberMIN(subscriberNum);
			//End

			//Start Card Acceptor Name Location Field - 43 
			String cardAcceptorName = requestISOMsg.getString(LRPISOMessageFieldIDConstant.CARD_ACCEPTOR_NAME);
			if (cardAcceptorName != null){
				cardAcceptorName = cardAcceptorName.trim();
			}
			posGWAccountRechargeDTO.setCardAcceptorName(cardAcceptorName);
			//End Card Acceptor Name Location Field - 43
			
			// Start Additional Data Field - 48
			String AdditionalData = requestISOMsg.getString(LRPISOMessageFieldIDConstant.ADDITIONAL_DATA);
			if (AdditionalData != null){
				AdditionalData = AdditionalData.trim();
			}
			posGWAccountRechargeDTO.setAdditionalData(AdditionalData);			
			// End Additional Data Field - 48
			
			//Starts Currency code Field - 49
			String originCurrencyCode = requestISOMsg.getString(LRPISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null){
				originCurrencyCode = originCurrencyCode.trim();
			}
			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
			posGWAccountRechargeDTO.setMessageDTO(posGWIRMessageDTO);
			//End Currency code Field - 49
			
			// Start ATM Terminal Data Field - 60
			String ATMTerminalData = requestISOMsg.getString(LRPISOMessageFieldIDConstant.ATM_TERMINAL_DATA);
			if (ATMTerminalData != null){
				ATMTerminalData = ATMTerminalData.trim();
			}
			posGWAccountRechargeDTO.setAtmTerminalData(ATMTerminalData);			
			// End ATM Terminal Data Field - 60
			
			// Start Card Issuer and Authorizer(a) Field - 61
			String CardIssuer = requestISOMsg.getString(LRPISOMessageFieldIDConstant.CARD_ISSUER);
			if (CardIssuer != null){
				CardIssuer = CardIssuer.trim();
			}
			posGWAccountRechargeDTO.setCardIssuer(CardIssuer);			
			// End Card Issuer and Authorizer(a) Field - 61
			
			// Start Receiving Institution Identification Code Field - 100 -- Due to No Setter and Getter
			String ReceivingInst = requestISOMsg.getString(LRPISOMessageFieldIDConstant.RECVING_INSTITUTION_CODE);
			if (ReceivingInst != null){
				ReceivingInst = ReceivingInst.trim();
			}
			posGWAccountRechargeDTO.setReceivingInstitutionCode(ReceivingInst);					
			// End Receiving Institution Identification Code Field - 100 -- Due to No Setter and Getter
			
			// Start Account Identification 1 Field - 102 -- Due to No Setter and Getter
			String AccntIdentification = requestISOMsg.getString(LRPISOMessageFieldIDConstant.ACCNT_ID);
			if (AccntIdentification != null){
				AccntIdentification = AccntIdentification.trim();
			}
			posGWAccountRechargeDTO.setAccountIdentification(AccntIdentification);					
			// End Account Identification 1 Field - 102 -- Due to No Setter and Getter
			
			// Start Token Field - 126 -- Due to No Setter and Getter
			String Token = requestISOMsg.getString(LRPISOMessageFieldIDConstant.TOKEN);
			if (Token != null){
				Token = Token.trim();
			}
			posGWAccountRechargeDTO.setToken(Token);					
			// End Token Field - 126 -- Due to No Setter and Getter			
			
			posGWAccountRechargeDTO.setCustomerType(customerType);
			posGWAccountRechargeDTO.setTopUpId(topUpId);
			posGWAccountRechargeDTO.setReplenishmentChannelId(replenishmentChannelId);
			posGWAccountRechargeDTO.setOverrideFraudId(overrideFraudId);
			posGWAccountRechargeDTO.setPaymentTypeId(paymentTypeId);


		}catch(Exception exp){
			posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ exp.getMessage());
		}
		return posGWAccountRechargeDTO;
	}
	
	public TransDetDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO){
		logger.debug ("packToServerDTO(POSGWMessageDTO posGWMessageDTO)....");
        if (posGWMessageDTO != null){
        	logger.info ("posGWMessageDTO is not null....");
        }else{
        	logger.info ("posGWMessageDTO is null");
        }
        
        POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;

    	TransDetDTO transDetDTO = new TransDetDTO();
    	transDetDTO.setCustomerType(posGWAccountRechargeDTO.getCustomerType());
    	transDetDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
    	String transAmt = posGWAccountRechargeDTO.getTransactionAmount();

    	double dTransAmount = 0.0;
    	if (transAmt != null){
    		if (PMISOFieldValidator.isNumericDoubleValue(transAmt)){
    			String doubleStringAmount = DataUtil.getValidPaymentAmount(transAmt);
    			dTransAmount = DataUtil.getDoubleValue(doubleStringAmount);
    			transDetDTO.setPaymentAmount(dTransAmount);
	    	}
	    }else{
	    	transDetDTO.setPaymentAmount(dTransAmount);
	    }
	    	
	    String referenceNum = posGWAccountRechargeDTO.getReferenceNumber();
	    if (PMISOFieldValidator.isAllZeroes(referenceNum)){
	    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getReferenceNumber());
	    }else{
	    	transDetDTO.setFinIstutinalReferenceNumber(referenceNum);
	    }
		    
	    transDetDTO.setSalesPersonId(posGWAccountRechargeDTO.getStoreId());
	    transDetDTO.setTopUpId(posGWAccountRechargeDTO.getTopUpId());
    	transDetDTO.setGenBy(posGWAccountRechargeDTO.getInitiatedUser());
    	String replChannelId = posGWAccountRechargeDTO.getReplenishmentChannelId();
    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(replChannelId));
	    transDetDTO.setOverRideFraud(posGWAccountRechargeDTO.getOverrideFraudId());
	    transDetDTO.setPaymentTypeId(posGWAccountRechargeDTO.getPaymentTypeId());
	    transDetDTO.setUSSDRequest(true);
	    //Subagent concept is not used in Panama requirement
	    String subDistId = posGWAccountRechargeDTO.getSubAgentId();
	    if (PMISOFieldValidator.isAllZeroes(subDistId)){
	    	//Resetting sub dist id to empty because, pm will check subdist info if this is not empty
	    	transDetDTO.setSubDistributorId("");
	    }else{
	    	transDetDTO.setSubDistributorId(subDistId);
	    	//transDetDTO.setGenBy(subDistId);
	    }

		transDetDTO.setCustomerId(posGWAccountRechargeDTO.getSubscriberMIN());
		transDetDTO.setForeignCurrencyId(Integer.parseInt(posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode()));
		
		// need to Get More infromation about the below lines....
		transDetDTO.setAmtInForeignCurrency(Double.parseDouble(transAmt)/100);
		ConnectionVO connectionVO = new ConnectionVO();
		connectionVO.setFileFormatId(2010);
		transDetDTO.setConnectionSer(connectionVO);
		transDetDTO.setOperatorID(posGWAccountRechargeDTO.getOperatorID());
    	return transDetDTO;
	}
	   
	public String isValidMessage(POSGWMessageDTO posGWMessageDTO){
		String status = ResponseCodeConstant.INITIAL_STATE;
		if (posGWMessageDTO != null){
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = LRPRechargeValidator.isValidReqData(posGWAccountRechargeDTO);
		}else{
			status = ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
		}
		return status;
	}

	public POSGWMessageDTO processMessage(POSGWMessageDTO reqPOSGWMessageDTO){
		POSGWMessageDTO respPOSGWMessageDTO = null;
		TransDetDTO respTransDetDTO = null;
		try{
			TransDetDTO reqTransDetDTO = packToServerDTO(reqPOSGWMessageDTO);

			LRPTransactionDelegate rechargeDelegate = new LRPTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());
			respTransDetDTO = rechargeDelegate.processMINRechargeTransaction(reqTransDetDTO);
			respPOSGWMessageDTO = unpackFromServerDTO(respTransDetDTO,reqPOSGWMessageDTO,false);
		}catch (Exception exp){
			//Assign Payment manager system error or Payment Manager not available error
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			//Changed response code from 98 to 99 for Defect #17285, By Srinivas P on 27 Oct 2006.
			respPOSGWAccountRechargeDTO.setResponseCode(POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE));
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	//For logging into AppMoniter previous data is lost and new data is setting in the previous method
	//since we are creating a new object
	public POSGWMessageDTO unpackFromServerDTO(TransDetDTO reqTransDetDTO,POSGWMessageDTO posGWMessageDTO, boolean isPin)
	{
		logger.error("unpackFromServerDTO.........");
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
		if (reqTransDetDTO!= null)
		{
			/*Following code is modified by sridhar.V on 19-Dec-2011 to show the Autherization Number(6 Digit)
			 * insted of Transaction Number (16 Digit)
			*/
			//String transactionId = reqTransDetDTO.getTransId();			
			String transactionId = reqTransDetDTO.getAutorizationId();
			//Ends
			String errorId = reqTransDetDTO.getErrorId();
			String errorMsg = reqTransDetDTO.getErrorMsg();
			/* commented for columbia			
			String expiryDate = reqTransDetDTO.getExpiryDate();
			double paymentAmount = reqTransDetDTO.getPaymentAmount();

			String taxAmount = reqTransDetDTO.getTaxAmount();
			
			String taxICE = reqTransDetDTO.getTaxICE();
			String taxIVA = reqTransDetDTO.getTaxIVA();
			String pinNumber = reqTransDetDTO.getPinNo();
			 */			
			logger.info("transactionId: "+transactionId);
			logger.info("errorId: "+errorId);
			logger.info("errorMsg: "+errorMsg);
			/*	commented for columbia		
			logger.error("expiryDate: "+expiryDate);
			logger.error("paymentAmount: "+paymentAmount);
			logger.error("taxICE: "+taxICE);
			logger.error("taxIVA: "+taxIVA);
			 */			
			//logger.error("pinNumber: "+pinNumber);
			//logger.error("decrypted pin: "+DataUtil.getDecryptedPin(pinNumber));

			//getting client response code
			String posgwErrorId = PM_DMPOSGW_MapConstant.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "+posgwErrorId);
			posGWAccountRechargeDTO.setResponseCode(posgwErrorId);

			posGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			/* commented for columbia
			posGWAccountRechargeDTO.setTransactionAmount(String.valueOf(paymentAmount));
			
			 * posGWAccountRechargeDTO.setPinNumber(DataUtil.getDecryptedPin(pinNumber));
			 */
			posGWAccountRechargeDTO.setPin(isPin);
		}
		return posGWAccountRechargeDTO;
	}

	public POSGWMessageDTO processRejectedTransaction(POSGWMessageDTO reqPOSGWMessageDTO)
	{

		POSGWMessageDTO respPOSGWMessageDTO = null;

		try
		{
			RejectTransactionDTO reqTransDetDTO = packToServerRejectTransDTO(reqPOSGWMessageDTO);
			logger.info("Calling Recharge Delegate....insertRejectedRechargeTransaction");
			
			
			
			LRPTransactionDelegate rechargeDelegate = new LRPTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());
			String transactionId = rechargeDelegate.insertRejectedRechargeTransaction(reqTransDetDTO);
			logger.info("After insertRejectedRechargeTransaction - transactionId"+transactionId);
			
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = (POSGWAccountRechargeDTO)reqPOSGWMessageDTO;
			respPOSGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			
			logger.info("*****SUCCESS*****************Rejected from payment manager.... ********************");
		}
		catch (Exception exp){
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			respPOSGWAccountRechargeDTO.setResponseCode(POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE));
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	 public RejectTransactionDTO packToServerRejectTransDTO(POSGWMessageDTO posGWMessageDTO){
	    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
	    	System.out.println("In packToServerRejectTransDTO Before packing in Reject Reason ServerDTO -posGWAccountRechargeDTO.toString(): "+posGWAccountRechargeDTO.toString());

	        String customerId = "";
	        /*
	         * commented for columbia, because subscribr id gets from only one field
	    	String subscriberId1 = posGWAccountRechargeDTO.getSubscriberId1();
	        String subscriberId2 = posGWAccountRechargeDTO.getSubscriberId2();
	        String subscriberId3 = posGWAccountRechargeDTO.getSubscriberId3();
	        if (subscriberId1 != null){
	        	customerId = subscriberId1;
	        }
	        else if (subscriberId2 != null){
	        	customerId = subscriberId2;
	        }
	        else if (subscriberId3 != null){
	        	customerId = subscriberId3;
	        }
	        */
	 	    /* as per IRP notation
			String countrycode = subscriberNum.substring(IRPMessageDataConstant.COUNTRYCODE_START_POSITION,IRPMessageDataConstant.COUNTRYCODE_END_POSITION);
			String cellNumber = subscriberNum.substring(IRPMessageDataConstant.COUNTRYCODE_END_POSITION,subscriberNum.length());
			*/

	        RejectTransactionDTO transDetDTO = new RejectTransactionDTO();

	        String subscriberNum = posGWAccountRechargeDTO.getSubscriberMIN();
	    //	customerId = subscriberNum.substring(32,40);
			transDetDTO.setCustomerId(subscriberNum);

	    	transDetDTO.setCustomerType(posGWAccountRechargeDTO.getCustomerType());
	    	transDetDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
	    	String transAmt = posGWAccountRechargeDTO.getTransactionAmount();
	    	transDetDTO.setTransactionType(AppConstant.TRANS_RECHARGE+"");
	    	double dTransAmount = 0.0;
	    	if (transAmt != null)
	    	{
	    		if (PMISOFieldValidator.isNumericValue(transAmt))
	    		{
	    			String doubleStringAmount = DataUtil.getValidPaymentAmount(transAmt);
	    			dTransAmount = DataUtil.getDoubleValue(doubleStringAmount);
	    			transDetDTO.setPayAmount(dTransAmount);
	    		}
	    	}
	    	else
	    	{
	    	   transDetDTO.setPayAmount(dTransAmount);
	    	}

	    	/* commented for columbia,beacuer system trace number is not considering
	    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getSystemTraceAuditNumber());
	    	*/

	    	
	    	String transInfo[]=null;
    		transInfo=DataUtil.getTransTypeCategory(posGWAccountRechargeDTO.getProcessingCode());
    	
          if(transInfo!=null && transInfo.length>=1)
           {
    	
    	   transDetDTO.setTransactionCode(transInfo[0]);
          }

		    String referenceNum1 = posGWAccountRechargeDTO.getReferenceNumber1();
		    if (PMISOFieldValidator.isAllZeroes(referenceNum1)){
		    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getReferenceNumber2());
		    }
		    else{
		    	transDetDTO.setFinIstutinalReferenceNumber(referenceNum1);
		    }

	    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
	    	transDetDTO.setPaymentTypeId(Integer.parseInt(posGWAccountRechargeDTO.getPaymentTypeId()));
	    	transDetDTO.setCustAccntNumb("0");
	    	transDetDTO.setResponseCode(posGWAccountRechargeDTO.getResponseCode());
	    	transDetDTO.setMappedErrorCodes(true);


	    	return transDetDTO;
	    } 

 	   
	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO)
	{
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = null;

		try
		{
			if (posMessageDTO != null)
			{
				posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posMessageDTO;
				requestISOMsg.set(LRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,posGWAccountRechargeDTO.getAuthorizationNumber());
				requestISOMsg.set(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());
			    /* commented for columbai, because there is not field number 12 in the reqeust or response
			     * so , skipping this block
			    String responseCode = posGWAccountRechargeDTO.getResponseCode();
			    if (responseCode != null)
			    {
			    	if (!responseCode.equals(ResponseCodeConstant.SUCCESS))
			    	{
			    		Date todayDate = Calendar.getInstance().getTime();
				     	requestISOMsg.set(DMISOMessageFieldIDConstant.TRANSACTION_TIME, ISODate.getTime(todayDate));

			    	}
			    }
			    */

			  // Condition added for : if pin number present then only GW sends the pin number in Field no.43 or 57.By Srinivas P on 17th Oct 2006.
			 //   if(posGWAccountRechargeDTO.getPinNumber() != null && posGWAccountRechargeDTO.getPinNumber().trim().length() > 0)
			   // {
				
				/* commented for columbia
			    	if (posGWAccountRechargeDTO.isPin())
			    	{
			    		String distributorId = requestISOMsg.getString(DMISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			    		if (distributorId.equals(DMMessageDataConstant.SPECIAL_DISTRIBUTOR_ID)){
			    			requestISOMsg.set(DMISOMessageFieldIDConstant.PIN_NUMBER2, DMMessageDataConstant.SPECIAL_PIN_PREFIX+posGWAccountRechargeDTO.getPinNumber());
			    		}
			    		else
			    		{
			    			requestISOMsg.set(DMISOMessageFieldIDConstant.PIN_NUMBER1, posGWAccountRechargeDTO.getPinNumber());
			    		}
			    	}
			    */	
			  //  }
			}
			else
			{
				requestISOMsg.set(LRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,"");
				requestISOMsg.set(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());

			}
		}
		catch(ISOException isoexp){
			logger.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "+ isoexp.getMessage());
		}
		return requestISOMsg;
	}



	public ISOMsg packResponseMessage(ISOMsg requestISOMsg){

		return requestISOMsg;
	}



    public void updateSendMsgStatus(String transactionID,String status){

	}

}
