package com.bcgi.paymgr.posserver.domestic.panama.processor;



import java.util.Calendar;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.bcgi.paymgr.posserver.common.constant.AppConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.POSGW_InternalResponseConstant;
import com.bcgi.paymgr.posserver.domestic.panama.delegate.LRPTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.panama.validator.LRPReversalValidator;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;
import com.bcgi.pmcore.vo.ConnectionVO;


public class LRPReversalMINProcessor  extends DMBaseMessageProcessor{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRPReversalMINProcessor.class);

	public LRPReversalMINProcessor()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodeConstant.INITIAL_STATE;
		if (posGWMessageDTO != null){
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = LRPReversalValidator.isValidReqData(posGWAccountRechargeDTO);
		}else{
			status = ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
		}
		logger.info("DMRechargeValidator.isValidReqData - Status:"+status);

		return status;
	}


	public POSGWMessageDTO processMessage(POSGWMessageDTO reqPOSGWMessageDTO)
	{

		POSGWMessageDTO respPOSGWMessageDTO = null;
		TransDetDTO respTransDetDTO = null;
		try
		{
			TransDetDTO reqTransDetDTO = packToServerDTO(reqPOSGWMessageDTO);
			logger.info("Calling Recharge Delegate....");
			LRPTransactionDelegate rechargeDelegate = new LRPTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());
			respTransDetDTO = rechargeDelegate.processMINReversalTransaction(reqTransDetDTO);
			respPOSGWMessageDTO = unpackFromServerDTO(respTransDetDTO,reqPOSGWMessageDTO,false);

		}catch (DelegateException exp){
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			respPOSGWAccountRechargeDTO.setResponseCode(POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE));
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	public POSGWMessageDTO processRejectedTransaction(POSGWMessageDTO reqPOSGWMessageDTO){

		POSGWMessageDTO respPOSGWMessageDTO = null;
		try{
			RejectTransactionDTO reqTransDetDTO = packToServerRejectTransDTO(reqPOSGWMessageDTO);
			logger.info("Calling Recharge Delegate....");
			LRPTransactionDelegate rechargeDelegate = new LRPTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());
			String transactionId = rechargeDelegate.insertRejectedRechargeTransaction(reqTransDetDTO);
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = (POSGWAccountRechargeDTO)reqPOSGWMessageDTO;
			respPOSGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
		}catch (DelegateException exp){
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			respPOSGWAccountRechargeDTO.setResponseCode(POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE));
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	 public RejectTransactionDTO packToServerRejectTransDTO(POSGWMessageDTO posGWMessageDTO){
		 POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
		 String customerId = "";
		 
		 RejectTransactionDTO transDetDTO = new RejectTransactionDTO();
		 customerId = posGWAccountRechargeDTO.getSubscriberMIN();
		 transDetDTO.setCustomerId(customerId);
		 transDetDTO.setCustomerType(posGWAccountRechargeDTO.getCustomerType());
		 transDetDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
		 String transAmt = posGWAccountRechargeDTO.getTransactionAmount();
		 double dTransAmount = 0.0;
		 if (transAmt != null){
			 if (PMISOFieldValidator.isNumericDoubleValue(transAmt)){
				 String doubleStringAmount = DataUtil.getValidPaymentAmount(transAmt);
				 dTransAmount = DataUtil.getDoubleValue(doubleStringAmount);
				 transDetDTO.setPayAmount(dTransAmount);
			 }
		 }else{
			 transDetDTO.setPayAmount(dTransAmount);
		 }
	    	
		 String referenceNum = posGWAccountRechargeDTO.getReferenceNumber();
		 if (PMISOFieldValidator.isAllZeroes(referenceNum)){
			 transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getReferenceNumber2());
		 }else{
			 transDetDTO.setFinIstutinalReferenceNumber(referenceNum);
		 }
		 transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
		 transDetDTO.setCustAccntNumb("0");
		 transDetDTO.setPaymentTypeId(Integer.parseInt(posGWAccountRechargeDTO.getPaymentTypeId()));
		 transDetDTO.setResponseCode(posGWAccountRechargeDTO.getResponseCode());
		 transDetDTO.setMappedErrorCodes(true);
		 transDetDTO.setTransactionType(AppConstant.TRANS_REVERSAL+"");
		 transDetDTO.setAsynchronus(false);
		 transDetDTO.setTransactionStatus(posGWAccountRechargeDTO.getTransactionStatus());
		 
		 return transDetDTO;
	 }

	//	For logging into AppMoniter previous data is lost and new data is setting in the previous method
	//since we are creating a new object
	public POSGWMessageDTO unpackFromServerDTO(TransDetDTO reqTransDetDTO,POSGWMessageDTO posGWMessageDTO, boolean isPin){
		logger.error("unpackFromServerDTO.........");
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
		if (reqTransDetDTO!= null){
			/*Following code is modified by sridhar.V on 19-Dec-2011 to show the Autherization Number(6 Digit)
			 * insted of Transaction Number (16 Digit)
			*/
			//String transactionId = reqTransDetDTO.getTransId();			
			String transactionId = reqTransDetDTO.getAutorizationId();
			//Ends
			String errorId = reqTransDetDTO.getErrorId();
			String errorMsg = reqTransDetDTO.getErrorMsg();
			String expiryDate = reqTransDetDTO.getExpiryDate();
			double paymentAmount = reqTransDetDTO.getPaymentAmount();
			String taxAmount = reqTransDetDTO.getTaxAmount();
			String taxICE = reqTransDetDTO.getTaxICE();
			String taxIVA = reqTransDetDTO.getTaxIVA();
			String pinNumber = reqTransDetDTO.getPinNo();
			logger.error("transactionId: "+transactionId);
			logger.error("errorId: "+errorId);
			logger.error("errorMsg: "+errorMsg);
			logger.error("expiryDate: "+expiryDate);
			logger.error("paymentAmount: "+paymentAmount);
			logger.error("taxAmount: "+taxAmount);
			logger.error("taxICE: "+taxICE);
			logger.error("taxIVA: "+taxIVA);
			logger.error("pinNumber: "+pinNumber);
			//getting client response code
			String posgwErrorId = PM_DMPOSGW_MapConstant.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "+posgwErrorId);
			posGWAccountRechargeDTO.setResponseCode(posgwErrorId);

			posGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			posGWAccountRechargeDTO.setTransactionAmount(String.valueOf(paymentAmount));
			/* commented for columbia implementation
			posGWAccountRechargeDTO.setPinNumber(pinNumber);
			posGWAccountRechargeDTO.setPin(isPin);
			*/
		}
		return posGWAccountRechargeDTO;
	}

    public TransDetDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO){

    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;

        String customerId = "";
    	TransDetDTO transDetDTO = new TransDetDTO();

    	//transDetDTO.setCustomerId(customerId);
    	//Customer Type field is required for Reject screen So, removed the comment for below line: By Srinivas P. on 06 Oct 2006.
    	transDetDTO.setCustomerType(posGWAccountRechargeDTO.getCustomerType());
    	transDetDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
    	String transAmt = posGWAccountRechargeDTO.getTransactionAmount();
    	double dTransAmount = 0.0;
    	if (transAmt != null)
    	{
    		if (PMISOFieldValidator.isNumericDoubleValue(transAmt))
    		{
    			String doubleStringAmount = DataUtil.getValidPaymentAmount(transAmt);
    			dTransAmount = DataUtil.getDoubleValue(doubleStringAmount);
    			transDetDTO.setPaymentAmount(dTransAmount);
    		}
    	}
    	else
    	{
    	   transDetDTO.setPaymentAmount(dTransAmount);
    	}

	    String referenceNum = posGWAccountRechargeDTO.getReferenceNumber();
	    if (PMISOFieldValidator.isAllZeroes(referenceNum)){
	    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getReferenceNumber2());
	    }else{
	    	transDetDTO.setFinIstutinalReferenceNumber(referenceNum);
	    }
	    
	    String finInstRefNumber = transDetDTO.getFinIstutinalReferenceNumber();
	    if(PMISOFieldValidator.isAllZeroes(finInstRefNumber))
	    	transDetDTO.setFinIstutinalReferenceNumber("");
	    

    	transDetDTO.setSalesPersonId(posGWAccountRechargeDTO.getStoreId());
    	transDetDTO.setGenBy(posGWAccountRechargeDTO.getInitiatedUser());
    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
    	transDetDTO.setPaymentTypeId(posGWAccountRechargeDTO.getPaymentTypeId());
    	transDetDTO.setUSSDRequest(true);
	    String subDistId = posGWAccountRechargeDTO.getSubAgentId();
	   	if (PMISOFieldValidator.isAllZeroes(subDistId)){
	   		 //Resetting sub dist id to empty because, pm will check subdist info if this is not empty
	   		 transDetDTO.setSubDistributorId("");
	   	}
	   	else
	   		 transDetDTO.setSubDistributorId(subDistId);

	   	customerId = posGWAccountRechargeDTO.getSubscriberMIN();
		transDetDTO.setCustomerId(customerId);
		
		/*Following code is modified by sridhar.V on 19-Dec-2011 to input the Autherization Number(6 Digit)
		 * insted of Transaction Number (16 Digit)
		*/		
		/*if (PMISOFieldValidator.isAllZeroes(posGWAccountRechargeDTO.getAuthorizationNumber()))
			transDetDTO.setTransId("");
		else
			transDetDTO.setTransId(posGWAccountRechargeDTO.getAuthorizationNumber());*/
		
		if (PMISOFieldValidator.isAllZeroes(posGWAccountRechargeDTO.getAuthorizationNumber()))
			transDetDTO.setRechargeAuthorizationID("");
		else
			transDetDTO.setRechargeAuthorizationID(posGWAccountRechargeDTO.getAuthorizationNumber());		

		//Ends		

		String originTransactionCode = posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode();
		if(originTransactionCode!=null && originTransactionCode.length()>0) {
			transDetDTO.setForeignCurrencyId(Integer.parseInt(originTransactionCode));
		}

		transAmt = (transAmt==null ||transAmt.length()==0)?"0":transAmt;
		transDetDTO.setAmtInForeignCurrency(Double.parseDouble(transAmt)/100);

		ConnectionVO connectionVO = new ConnectionVO();
		connectionVO.setFileFormatId(2010);
		transDetDTO.setConnectionSer(connectionVO);
		
		transDetDTO.setTransactionStatus(posGWAccountRechargeDTO.getTransactionStatus());

    	logger.debug("transDetDTO==>" + transDetDTO.toString());

    	return transDetDTO;

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
     * Response Code 							39
     * Authorization Number						38
     * Terminal ID								41
     * Card Acceptor Name/Location   			43 
     * Transaction Currency Code				49
     * ATM Terminal Data						60
     * Card Issuer and Authorizer(a) 			61
     * Original Data Elements					90
     * Account Identification 					102 
     * MIN										126
     * Token 									126
     */

	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.INITIAL_STATE);
		try
		{
		    //change below code to have replenishmentid,custtype and paytypeid in request msg.
			String customerType = "1";
           //String topUpId = "1";
            String replenishmentChannelId = "6";
           // String overrideFraudId = "off";
            String paymentTypeId = "1";

            // Start Amount Field -  4
			String amount = requestISOMsg.getString(LRPISOMessageFieldIDConstant.TRANSACTION_AMNT);
			if (amount != null){
				amount = amount.trim();
			}
			posGWAccountRechargeDTO.setTransactionAmount(amount);
			//End Amount Field -  4  

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
			posGWAccountRechargeDTO.setTransactionTime(localTransTime);
			//End Local Transaction Time  Field - 12
			
			//Start Local Transaction Time  Field - 13
			String localTransDate = requestISOMsg.getString(LRPISOMessageFieldIDConstant.LOCAL_TRANSACTION_DATE);
			if (localTransDate != null){
				localTransDate = localTransDate.trim();
			}
			posGWAccountRechargeDTO.setTransactionDate(localTransDate);
			//End Local Transaction Time  Field - 13
			
			//Start Settlement Date  Field - 15
			String settlementDate = requestISOMsg.getString(LRPISOMessageFieldIDConstant.SETTLEMENT_DATE);
			if (settlementDate != null){
				settlementDate = settlementDate.trim();
			}
			posGWAccountRechargeDTO.setSettlmentDate(settlementDate);
			//End Settlement Date  Field - 15			
			
			
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

			//Start Authorization Identification Response Field- 38
			String authorizationNumber = requestISOMsg.getString(LRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
			if (authorizationNumber != null){
				authorizationNumber = authorizationNumber.trim();
			}
			posGWAccountRechargeDTO.setAuthorizationNumber(authorizationNumber);
			//End Authorization Identification Response Field- 38
			
			// Starts Response Code Field -39
			String responseCode = requestISOMsg.getString(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID);
			if (responseCode != null){
				responseCode = responseCode.trim();
			}
			posGWAccountRechargeDTO.setResponceCodeOfRecharge(responseCode);			
			//End Response Code Field -39
			
			// Starts Card Acceptor Terminal Identification Field - 41
			String storemanagerId  = requestISOMsg.getString(LRPISOMessageFieldIDConstant.STORE_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posGWAccountRechargeDTO.setStoreId(storemanagerId);
			// Ends Card Acceptor Terminal Identification Field - 41

			//Start Card Acceptor Name Location Field - 43 
			String cardAcceptorName = requestISOMsg.getString(LRPISOMessageFieldIDConstant.CARD_ACCEPTOR_NAME);
			if (cardAcceptorName != null){
				cardAcceptorName = cardAcceptorName.trim();
			}
			posGWAccountRechargeDTO.setCardAcceptorName(cardAcceptorName);
			//End Card Acceptor Name Location Field - 43

			//Subscriber Number is a part of Field no : 126
			String subscriberNum = requestISOMsg.getString(LRPISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER);
			if (subscriberNum != null){
				subscriberNum = subscriberNum.trim(); 
				subscriberNum = DataUtil.getRequiredSubscriberNumber(subscriberNum, LRPMessageTypeIDConstant.SUBSCRIBER_NUMBER_START_POSITION, LRPMessageTypeIDConstant.SUBSCRIBER_NUMBER_END_POSITION);
			}
			logger.info("Subscriber Number =======>"+subscriberNum);
			posGWAccountRechargeDTO.setSubscriberMIN(subscriberNum);
			//End

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
			
			//Start Original Data Elements Field - 90
			String orginalData = requestISOMsg.getString(LRPISOMessageFieldIDConstant.ORIGINAL_DATA);
			if (orginalData != null){
				orginalData = orginalData.trim();
			}
			posGWAccountRechargeDTO.setOriginalDataEleemnts(orginalData);			
			// End Original Data Elements Field - 90

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
			
			logger.info("==================>Unpack Is completed  =======>");
			posGWAccountRechargeDTO.setCustomerType(customerType);

			//posGWAccountRechargeDTO.setTopUpId(topUpId);
			posGWAccountRechargeDTO.setReplenishmentChannelId(replenishmentChannelId);
			//posGWAccountRechargeDTO.setOverrideFraudId(overrideFraudId);
			posGWAccountRechargeDTO.setPaymentTypeId(paymentTypeId);

		}
		catch(Exception exp){
			posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ exp.getMessage());
		}
		return posGWAccountRechargeDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO)
	{
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = null;
		String transId = "";

		try
		{
			if (posMessageDTO != null)
			{
				posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posMessageDTO;

				transId = posGWAccountRechargeDTO.getAuthorizationNumber();
				//Below condition is added by Srinivas P.
				//Reason: if Transaction Id is present then only Response message contains 38 field
				//otherwise no need to set 38 field with blank ( this blank case occurs in Reversal success case).
				//if(transId != null && transId.trim().length() > 0)
				//{
				requestISOMsg.set(LRPISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,transId);
				//}
				requestISOMsg.set(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());

				/* commented for columbia, becauser 12 field is not using
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
			}
		}
		catch(ISOException isoexp){
			logger.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){

		logger.info("Domestic MIN Reversal MIN Processor- execute()........"+posGWMessageDTO);
		ISOMsg responseISOMsg = requestISOMsg;
		boolean isAutoReversalFlag = false;
        try{
        	if(posGWMessageDTO!=null){
        		logger.info("Auto reversal initiated....");
        		isAutoReversalFlag = true;
        		posGWMessageDTO = null;
        	}
        	
       	
        	Calendar startCal = Calendar.getInstance();
        	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
		
			if(isAutoReversalFlag){
				posGWAccountRechargeDTO.setTransactionStatus(ServerConfigConstant.AUTOREVERSAL);
			}

			logger.info("posGWAccountRechargeDTO.getResponseCode()===>" + posGWAccountRechargeDTO.getResponseCode());

			if (posGWAccountRechargeDTO.getResponseCode().equals(ResponseCodeConstant.INITIAL_STATE)){
			  String status = isValidMessage(posGWAccountRechargeDTO);

			  logger.info("status==>" + status);

			  if (status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)){
				  posGWAccountRechargeDTO = (POSGWAccountRechargeDTO)processMessage(posGWAccountRechargeDTO);
			  }
			  else
			  {
				  posGWAccountRechargeDTO.setResponseCode(status);
				  posGWAccountRechargeDTO = (POSGWAccountRechargeDTO)processRejectedTransaction(posGWAccountRechargeDTO);
				  
				  //Resetting Domestic Error code as per columbia
				  String posgwErrorCode = POSGW_InternalResponseConstant.getPOSGWResponse(posGWAccountRechargeDTO.getResponseCode());
				  posGWAccountRechargeDTO.setResponseCode(posgwErrorCode);
			  }
		}

		responseISOMsg = packISOMsg(requestISOMsg,posGWAccountRechargeDTO);

			//	Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
			/* commented for columbia implementation
			if(posGWAccountRechargeDTO.isPin()){
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWAccountRechargeDTO.getPinNumber(),posGWAccountRechargeDTO.getAuthorizationNumber(),posGWAccountRechargeDTO.getSystemTraceAuditNumber(), startCal, endCal,"Domestic PIN Reversal Transaction");
				if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.PIN_REVERSAL_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.PIN_REVERSAL_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
			}
			else
			{
			*/
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWAccountRechargeDTO.getSubscriberId1(),posGWAccountRechargeDTO.getAuthorizationNumber(),posGWAccountRechargeDTO.getSystemTraceAuditNumber(), startCal, endCal,"Domestic Reversal Transaction");
				if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
			//}
			// End of AppMoniter Logging

        }
        catch(Exception exp){
        	exp.printStackTrace();
			try
			{
		    responseISOMsg.set(LRPISOMessageFieldIDConstant.RESPONSE_CODE_ID, POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR));

			logger.error("LRPReversalMINProcessor-Exception at execute(): "+exp.getMessage());
			}
			catch(Exception ex){
				logger.error("LRPReversalMINProcessor-Exception at execute(): "+exp.getMessage());

			}
		}
		return responseISOMsg;
	}

	public ISOMsg packResponseMessage(ISOMsg requestISOMsg){

		return requestISOMsg;
	}



    public void updateSendMsgStatus(String transactionID,String status){

	}

}
