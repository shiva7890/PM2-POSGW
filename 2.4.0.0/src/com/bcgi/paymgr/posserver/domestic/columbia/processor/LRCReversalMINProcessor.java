package com.bcgi.paymgr.posserver.domestic.columbia.processor;



import java.util.Calendar;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.bcgi.paymgr.posserver.common.constant.AppConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.POSGW_InternalResponseConstant;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.delegate.LRCTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.domestic.columbia.validator.LRCReversalValidator;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;
import com.bcgi.pmcore.vo.ConnectionVO;


public class LRCReversalMINProcessor  extends DMBaseMessageProcessor{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRCReversalMINProcessor.class);

	public LRCReversalMINProcessor()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodeConstant.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = LRCReversalValidator.isValidReqData(posGWAccountRechargeDTO);
		}
		else
		{
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
			LRCTransactionDelegate rechargeDelegate = new LRCTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());
			 respTransDetDTO = rechargeDelegate.processMINReversalTransaction(reqTransDetDTO);
			respPOSGWMessageDTO = unpackFromServerDTO(respTransDetDTO,reqPOSGWMessageDTO,false);
		}
		catch (DelegateException exp){
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			respPOSGWAccountRechargeDTO.setResponseCode(POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE));
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	public POSGWMessageDTO processRejectedTransaction(POSGWMessageDTO reqPOSGWMessageDTO)
	{

		POSGWMessageDTO respPOSGWMessageDTO = null;

		try
		{
			RejectTransactionDTO reqTransDetDTO = packToServerRejectTransDTO(reqPOSGWMessageDTO);
			logger.info("Calling Recharge Delegate....");
			LRCTransactionDelegate rechargeDelegate = new LRCTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());
			String transactionId = rechargeDelegate.insertRejectedRechargeTransaction(reqTransDetDTO);
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = (POSGWAccountRechargeDTO)reqPOSGWMessageDTO;
			respPOSGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
		}
		catch (DelegateException exp){
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			respPOSGWAccountRechargeDTO.setResponseCode(POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE));
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}

	 public RejectTransactionDTO packToServerRejectTransDTO(POSGWMessageDTO posGWMessageDTO)
	    {

	    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
	        String customerId = "";

	        RejectTransactionDTO transDetDTO = new RejectTransactionDTO();
	 	    customerId = DataUtil.getSubscriberNumber(posGWAccountRechargeDTO.getSubscriberMIN());
			transDetDTO.setCustomerId(customerId);
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
	    			transDetDTO.setPayAmount(dTransAmount);
	    		}
	    	}
	    	else
	    	{
	    	   transDetDTO.setPayAmount(dTransAmount);
	    	}
	    	
		    String referenceNum1 = posGWAccountRechargeDTO.getReferenceNumber1();
		    if (PMISOFieldValidator.isAllZeroes(referenceNum1)){
		    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getReferenceNumber2());
		    }
		    else{
		    	transDetDTO.setFinIstutinalReferenceNumber(referenceNum1);
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
			logger.error("Transaction Date and Time :"+reqTransDetDTO.getTransDateAndTime());
			//getting client response code
			String posgwErrorId = PM_DMPOSGW_MapConstant.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "+posgwErrorId);
			posGWAccountRechargeDTO.setResponseCode(posgwErrorId);

			posGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			posGWAccountRechargeDTO.setTransactionAmount(String.valueOf(paymentAmount));
			posGWAccountRechargeDTO.setSettlmentDate(reqTransDetDTO.getTransDateAndTime());
			//added  for Component tracking of all channels
			posGWAccountRechargeDTO.setTransactionId(reqTransDetDTO.getTransId());
		}
		return posGWAccountRechargeDTO;
	}

    public TransDetDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
    {

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
    	/*
		   	 commented for columbia, because system trace number will get in the form always 0's,
		   	 so we are not considerig field # 11
    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getSystemTraceAuditNumber());
    	*/
	    String referenceNum1 = posGWAccountRechargeDTO.getReferenceNumber1();
	    if (PMISOFieldValidator.isAllZeroes(referenceNum1)){
	    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getReferenceNumber2());
	    }
	    else{
	    	transDetDTO.setFinIstutinalReferenceNumber(referenceNum1);
	    }
	    
	    String finInstRefNumber = transDetDTO.getFinIstutinalReferenceNumber();
	    if(PMISOFieldValidator.isAllZeroes(finInstRefNumber))
	    	transDetDTO.setFinIstutinalReferenceNumber("");
	    

    	transDetDTO.setSalesPersonId(posGWAccountRechargeDTO.getStoreId());
    	transDetDTO.setGenBy(posGWAccountRechargeDTO.getInitiatedUser());
    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
    	transDetDTO.setPaymentTypeId(posGWAccountRechargeDTO.getPaymentTypeId());

	    String subDistId = posGWAccountRechargeDTO.getSubAgentId();
	  //Following code is commented bY Sridhar.Vemulapalli on 12-Aug-2011
	   	//if (PMISOFieldValidator.isAllZeroes(subDistId)){
	   		 //Resetting sub dist id to empty because, pm will check subdist info if this is not empty
	   		 //transDetDTO.setSubDistributorId("");
	   	//}
	   	//else
	   		 transDetDTO.setSubDistributorId(subDistId);
	   	//Ends
 	    customerId = DataUtil.getSubscriberNumber(posGWAccountRechargeDTO.getSubscriberMIN());
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
    Amount	                    04	12
    Transmission Date & Time	07	10
    System Trace Audit Number	11	06
    Expiration Date	            14	04
    Capture Date	            17	04
    Modality	                25	02
    Acquiring Institution Identification Code	32	11
    Retrieval Reference Number	37	12
    Authorization Number	38	06
    Terminal ID	41	16
    Merchant ID	42	15
    MIN	43	40
    Card Acceptor Name/Location	48	47
    Transaction Currency Code	49	03
    Retrieval Reference Number	57	20
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

            //not modified,Amount Field same,4
			String amount = requestISOMsg.getString(LRCISOMessageFieldIDConstant.TRANSACTION_AMNT);
			if (amount != null){
				amount = amount.trim();
			}
			posGWAccountRechargeDTO.setTransactionAmount(amount);


			//  start newly added for columbia,7
			String transactionDateTime = requestISOMsg.getString(LRCISOMessageFieldIDConstant.TRANSACTION_DATE_TIME);
			if (transactionDateTime != null){
				transactionDateTime = transactionDateTime.trim();
			}
			posGWAccountRechargeDTO.setTransactionDateTime(transactionDateTime);
			//  end newly added for columbia

			//already exists,11 and ignoring 11 field, because he sends 0's
			String systemTraceAuditNum = requestISOMsg.getString(LRCISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER);
			if (systemTraceAuditNum != null){
				systemTraceAuditNum = systemTraceAuditNum.trim();
			}
			posGWAccountRechargeDTO.setSystemTraceAuditNumber(systemTraceAuditNum);

			// ******   skipping expiry date because in IRP also not using 14 *****

			//start new ly added for columbia 17
			String captureDate = requestISOMsg.getString(LRCISOMessageFieldIDConstant.CAPTURE_DATE);
			if (captureDate != null){
				captureDate = captureDate.trim();
			}
			posGWAccountRechargeDTO.setCaptureDate(captureDate);
			//end new ly added for columbia 17

			//start new ly added for columbia 25
			String modality = requestISOMsg.getString(LRCISOMessageFieldIDConstant.MODALITY);
			if (modality != null){
				modality = modality.trim();
			}
			posGWAccountRechargeDTO.setModality(modality);
			//end new ly added for columbia 25


//			Modified for columbia to field # 32 from field #24
			String distributorId = requestISOMsg.getString(LRCISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			if (distributorId != null){
				distributorId = distributorId.trim();
			}
			posGWAccountRechargeDTO.setInitiatedUser(distributorId); //GenBy
			posGWAccountRechargeDTO.setDistributorId(distributorId);


			//newly added for filed 37
			String referenceNumber1 = requestISOMsg.getString(LRCISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1);
			if (referenceNumber1 != null){
				referenceNumber1 = referenceNumber1.trim();
			}
			posGWAccountRechargeDTO.setReferenceNumber1(referenceNumber1);
			//end

			//Not modified filed # 38
			String authorizationNumber = requestISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
			if (authorizationNumber != null){
				authorizationNumber = authorizationNumber.trim();
			}
			posGWAccountRechargeDTO.setAuthorizationNumber(authorizationNumber);

			//Not modified filed # 41
			String storemanagerId  = requestISOMsg.getString(LRCISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
				if(LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_FLAG!=null && LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_FLAG.equalsIgnoreCase("YES")){
					if(LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR!=null && LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR.trim().length()>0)
						storemanagerId = DataUtil.supressZeros(storemanagerId,LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR);
				}				
			}
			//The Following line is commented By Sridhar Vemulapalli on 10-Aug-2011 
			//posGWAccountRechargeDTO.setStoreId(storemanagerId);			
			logger.info("Store Manager Id == >"+storemanagerId);
			posGWAccountRechargeDTO.setSubAgentId(storemanagerId);
			
			//Not modified filed # 42
			String subagentId = requestISOMsg.getString(LRCISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
				if(LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_FLAG!=null && LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_FLAG.equalsIgnoreCase("YES")){
					if(LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR!=null && LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR.trim().length()>0)					
						subagentId = DataUtil.supressZeros(subagentId,LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR);
				}				
			}
			//The Following line is commented By Sridhar Vemulapalli on 10-Aug-2011			
			//posGWAccountRechargeDTO.setSubAgentId(subagentId);
			logger.info("Sub Agent Id == >"+subagentId);
			posGWAccountRechargeDTO.setStoreId(subagentId);

			/*
			 * commented for columbia, columbia is not using these field ids
			String subscriberId1 = requestISOMsg.getString(DMISOMessageFieldIDConstant.SUBSCRIBER_ID1);
			if (subscriberId1 != null){
				subscriberId1 = subscriberId1.trim();
			}
			posGWAccountRechargeDTO.setSubscriberId1(subscriberId1);

			String subscriberId3 = requestISOMsg.getString(DMISOMessageFieldIDConstant.SUBSCRIBER_ID3);
			if (subscriberId3 != null){
				subscriberId3 = subscriberId3.trim();
			}
			posGWAccountRechargeDTO.setSubscriberId3(subscriberId3);
			*/

			//newly added for columbia 43
			//Note: No multiple fileds for subcriber number
			String subscriberNum = requestISOMsg.getString(LRCISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER);
			if (subscriberNum != null){
				subscriberNum = subscriberNum.trim();
			}
			posGWAccountRechargeDTO.setSubscriberMIN(subscriberNum);


			//newly added for columbia 48
			String cardAcceptorName = requestISOMsg.getString(LRCISOMessageFieldIDConstant.CARD_ACCEPTOR_NAME);
			if (cardAcceptorName != null){
				cardAcceptorName = cardAcceptorName.trim();
			}
			posGWAccountRechargeDTO.setCardAcceptorName(cardAcceptorName);


			//newly added for columbia 49
			String originCurrencyCode = requestISOMsg.getString(LRCISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null){
				originCurrencyCode = originCurrencyCode.trim();
			}
			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
			posGWAccountRechargeDTO.setMessageDTO(posGWIRMessageDTO);

			//newly added for columbia 57
			String referenceNumber2 = requestISOMsg.getString(LRCISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_2);
			if (referenceNumber2 != null){
				referenceNumber2 = referenceNumber2.trim();
			}
			posGWAccountRechargeDTO.setReferenceNumber2(referenceNumber2);



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
				requestISOMsg.set(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,transId);
				//}
				requestISOMsg.set(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());
				//Following code is implemented by Sridhar.Vemulapalli on 29-Nov-2011 to Fill updated 41,42 Field
				if(LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_FLAG!=null && LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_FLAG.equalsIgnoreCase("YES")){
					if(LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR!=null && LRCISOMessageFieldIDConstant.COLUMBIA_FIELD_SUPRESS_CHAR.trim().length()>0){
						requestISOMsg.set(LRCISOMessageFieldIDConstant.STORE_MANAGER_ID,posGWAccountRechargeDTO.getSubAgentId());
						requestISOMsg.set(LRCISOMessageFieldIDConstant.SUBAGENT_ID,posGWAccountRechargeDTO.getStoreId());
					}
				}
				//Ends				
				//The following code added by sridhar.Vemulapalli on 28-Sep-2011 according to client Requirement
				if(posGWAccountRechargeDTO!=null ){
					if(posGWAccountRechargeDTO.getSettlmentDate()!=null && posGWAccountRechargeDTO.getSettlmentDate().trim().length()>0){
						requestISOMsg.set(LRCISOMessageFieldIDConstant.TRANSACTION_DATE_TIME,DataUtil.convertToValidDateTime(posGWAccountRechargeDTO.getSettlmentDate()));
					}else{
						requestISOMsg.set(LRCISOMessageFieldIDConstant.TRANSACTION_DATE_TIME,DataUtil.convertToValidDateTime(null));
					}
				}				
				//Ends
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
        	
        	if(posGWMessageDTO!=null)
        	{
        		logger.info("Auto reversal initiated....");
        		isAutoReversalFlag = true;
        		posGWMessageDTO = null;
        	}
        	
        		
        	
			Calendar startCal = Calendar.getInstance();
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
		
		if(isAutoReversalFlag)
		{
			posGWAccountRechargeDTO.setTransactionStatus(ServerConfigConstant.AUTOREVERSAL);
		}

		logger.debug("posGWAccountRechargeDTO.getResponseCode()===>" + posGWAccountRechargeDTO.getResponseCode());

		if (posGWAccountRechargeDTO.getResponseCode().equals(ResponseCodeConstant.INITIAL_STATE))
		{
			  String status = isValidMessage(posGWAccountRechargeDTO);

			  logger.debug("status==>" + status);

			  if (status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS))
			  {
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
			//added  for Component tracking of all channels
			String tmpRefNum = posGWAccountRechargeDTO.getReferenceNumber1();
			if (PMISOFieldValidator.isAllZeroes(tmpRefNum)){
				tmpRefNum = posGWAccountRechargeDTO.getReferenceNumber2();
			}
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWAccountRechargeDTO.getTransactionId(),posGWAccountRechargeDTO.getAuthorizationNumber(),tmpRefNum, startCal, endCal,"Domestic Reversal Transaction");
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
		    responseISOMsg.set(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID, POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR));

			logger.error("IRPRechargeMINProcessor-Exception at execute(): "+exp.getMessage());
			}
			catch(Exception ex){
				logger.error("IRPRechargeMINProcessor-Exception at execute(): "+exp.getMessage());

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
