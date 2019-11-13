package com.bcgi.paymgr.posserver.domestic.columbia.processor;

import java.util.Calendar;

import org.jboss.logging.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.bcgi.paymgr.posserver.POSServerMgr;
import com.bcgi.paymgr.posserver.common.constant.AppConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.POSGW_InternalResponseConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.delegate.LRCTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.columbia.validator.LRCPKTRechargeValidator;
import com.bcgi.paymgr.posserver.domestic.columbia.validator.LRCRechargeValidator;
import com.bcgi.paymgr.posserver.domestic.common.AppMonitorInitiatedThread;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;
import com.bcgi.pmcore.vo.ConnectionVO;

public class LRCRechargePKTMINProcessor extends DMBaseMessageProcessor
{
	
	
	static Logger logger	= Logger.getLogger(LRCRechargePKTMINProcessor.class);
	private String connectionId=null;

	public LRCRechargePKTMINProcessor()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodeConstant.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = LRCPKTRechargeValidator.isValidReqData(posGWAccountRechargeDTO);
		}
		else
		{
			status = ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
		}

		return status;
	}


	public POSGWMessageDTO processMessage(POSGWMessageDTO reqPOSGWMessageDTO)
	{

		POSGWMessageDTO respPOSGWMessageDTO = null;
		TransDetDTO respTransDetDTO = null;
		try
		{
			TransDetDTO reqTransDetDTO = packToServerDTO(reqPOSGWMessageDTO);
			
			LRCTransactionDelegate rechargeDelegate = new LRCTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());
			respTransDetDTO = rechargeDelegate.processMINRechargeTransaction(reqTransDetDTO);
			respPOSGWMessageDTO = unpackFromServerDTO(respTransDetDTO,reqPOSGWMessageDTO,false);
			

		}
		catch (Exception exp){
			//TODO Based on the exception
			//Assign Payment manager system error or Payment Manager not available error
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			//Changed response code from 98 to 99 for Defect #17285, By Srinivas P on 27 Oct 2006.
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
			logger.info("Calling Recharge Delegate....insertRejectedRechargeTransaction");
			
			LRCTransactionDelegate rechargeDelegate = new LRCTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());
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

	 public RejectTransactionDTO packToServerRejectTransDTO(POSGWMessageDTO posGWMessageDTO)
	    {


	    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
	    	System.out.println("In packToServerRejectTransDTO Before packing in Reject Reason ServerDTO -posGWAccountRechargeDTO.toString(): "+posGWAccountRechargeDTO.toString());

	        String customerId = "";

	        RejectTransactionDTO transDetDTO = new RejectTransactionDTO();

	        String subscriberNum = posGWAccountRechargeDTO.getSubscriberMIN();

	    	customerId = DataUtil.getSubscriberNumber(subscriberNum);
			transDetDTO.setCustomerId(customerId);

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

		    String referenceNum1 = posGWAccountRechargeDTO.getReferenceNumber1();
		    if (PMISOFieldValidator.isAllZeroes(referenceNum1)){
		    	transDetDTO.setRrn(posGWAccountRechargeDTO.getReferenceNumber2());
		    }
		    else{
		    	transDetDTO.setRrn(referenceNum1);
		    	
		    	
		    }

	    	  logger.info("setFinIstutinalReferenceNumber : "+transDetDTO.getRrn());
	    	  logger.info("setsalespersonid : "+posGWAccountRechargeDTO.getStoreId());
	    	 transDetDTO.setInitiatorId(posGWAccountRechargeDTO.getStoreId());

	    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
	    	transDetDTO.setPaymentTypeId(Integer.parseInt(posGWAccountRechargeDTO.getPaymentTypeId()));
	    	transDetDTO.setCustAccntNumb("0");
	    	transDetDTO.setOperatorId(posGWAccountRechargeDTO.getOperatorID());
	    	transDetDTO.setResponseCode(posGWAccountRechargeDTO.getResponseCode());
	    	transDetDTO.setMappedErrorCodes(true);

	    	  logger.info("Internal error code : "+posGWAccountRechargeDTO.getResponseCode());
			  String posgwErrorCode = POSGW_InternalResponseConstant.getPOSGWResponse(posGWAccountRechargeDTO.getResponseCode());
			  logger.info("mapping : "+posgwErrorCode);
			  //posGWAccountRechargeDTO.setResponseCode(posgwErrorCode);
			  transDetDTO.setGwErrCode(posgwErrorCode);
			

	    	return transDetDTO;
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
			
			logger.info("transactionId: "+transactionId);
			logger.info("errorId: "+errorId);
			logger.info("errorMsg: "+errorMsg);
			logger.info("Date and Time of the Transaction: "+reqTransDetDTO.getTransDateAndTime());

			//getting client response code
			String posgwErrorId = PM_DMPOSGW_MapConstant.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "+posgwErrorId);
			posGWAccountRechargeDTO.setResponseCode(posgwErrorId);

			posGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			posGWAccountRechargeDTO.setPin(isPin);
			posGWAccountRechargeDTO.setSettlmentDate(reqTransDetDTO.getTransDateAndTime());
			//added for Component tracking of all channels
			posGWAccountRechargeDTO.setTransactionId(reqTransDetDTO.getTransId());
			posGWAccountRechargeDTO.setConnectionId(reqTransDetDTO.getConnectionId()==0?null:reqTransDetDTO.getConnectionId()+"");
		}
		return posGWAccountRechargeDTO;
	}

    public TransDetDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
    {
    	logger.debug ("packToServerDTO(POSGWMessageDTO posGWMessageDTO)....");
        if (posGWMessageDTO != null){
        	 logger.info ("posGWMessageDTO is not null....");
        }
        else{
        	logger.info ("posGWMessageDTO is null");
        }
    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;

    	TransDetDTO transDetDTO = new TransDetDTO();
    	//transDetDTO.setCustomerId(customerId);
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
	    

    	transDetDTO.setSalesPersonId(posGWAccountRechargeDTO.getStoreId());
    	transDetDTO.setTopUpId(posGWAccountRechargeDTO.getTopUpId());
    	transDetDTO.setGenBy(posGWAccountRechargeDTO.getInitiatedUser());
    	String replChannelId = posGWAccountRechargeDTO.getReplenishmentChannelId();
    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(replChannelId));
    	transDetDTO.setOverRideFraud(posGWAccountRechargeDTO.getOverrideFraudId());
    	transDetDTO.setPaymentTypeId(posGWAccountRechargeDTO.getPaymentTypeId());
    	//The following code is added by Sridhar.Vemulapalli on 30-Sep-2011
    	transDetDTO.setPOSColumbiaRequest(true);
    	//Ends
    	String subDistId = posGWAccountRechargeDTO.getSubAgentId();
    	//Following code is commented bY Sridhar.Vemulapalli on 12-Aug-2011
    	 //if (PMISOFieldValidator.isAllZeroes(subDistId)){
    		 //Resetting sub dist id to empty because, pm will check subdist info if this is not empty
    		 //transDetDTO.setSubDistributorId("");
    	 //}
    	 //else{
    		 transDetDTO.setSubDistributorId(subDistId);
    		 transDetDTO.setGenBy(subDistId);
    	 //}
    	//Ends
 	    String subscriberNum = posGWAccountRechargeDTO.getSubscriberMIN();

 	    String customerId = DataUtil.getSubscriberNumber(subscriberNum);
		transDetDTO.setCustomerId(customerId);
		

		transDetDTO.setForeignCurrencyId(Integer.parseInt(posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode()));
		
		transDetDTO.setAmtInForeignCurrency(Double.parseDouble(transAmt)/100);

		ConnectionVO connectionVO = new ConnectionVO();
		connectionVO.setFileFormatId(2010);
		transDetDTO.setConnectionSer(connectionVO);
		transDetDTO.setOperatorID(posGWAccountRechargeDTO.getOperatorID());
		transDetDTO.setPacketCode(posGWAccountRechargeDTO.getPacketCode());
    	return transDetDTO;

	}

    /*
     * Amount	                 04
     * Transmission Date & Time	 07
     * System Trace Audit Number	11
     * Expiration Date	14
     * Capture Date	17
     * Modality	25
     * Acquiring Institution Identification Code	32
     * Retrieval Reference Number	37
     * Terminal ID	41
     * Merchant ID	42
     * MIN	43
     * Card Acceptor Name/Location
    	48
    Transaction Currency Code	49
    Retrieval Reference Number	57
     */
	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.INITIAL_STATE);


		try
		{
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
		
            
			   // not modified,Amount Field same,4
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
			//logger.debug(" 11 systemTraceAuditNum from ISO Msg : "+systemTraceAuditNum);


			// ******   skipping expiry date because in IRP also not using 14 *****

			//start newly added for columbia 17
			String captureDate = requestISOMsg.getString(LRCISOMessageFieldIDConstant.CAPTURE_DATE);
			if (captureDate != null){
				captureDate = captureDate.trim();
			}
			posGWAccountRechargeDTO.setCaptureDate(captureDate);
			//logger.debug(" 17 captureDate from ISO Msg : "+captureDate);
			//end new ly added for columbia 17

			//start new ly added for columbia 25
			String modality = requestISOMsg.getString(LRCISOMessageFieldIDConstant.MODALITY);
			if (modality != null){
				modality = modality.trim();
			}
			posGWAccountRechargeDTO.setModality(modality);
			//logger.debug(" 25 captureDate from ISO Msg : "+modality);
			//end new ly added for columbia 25


			//Modified for columbia  32 from 24
			String distributorId = requestISOMsg.getString(LRCISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			//logger.debug("Distributor id from the ISOMsg::"+distributorId);
			if (distributorId != null){
				distributorId = distributorId.trim();
			}
			posGWAccountRechargeDTO.setInitiatedUser(distributorId); //GenBy
			posGWAccountRechargeDTO.setDistributorId(distributorId);
			//logger.debug(" 32 distributorId from ISO Msg : "+distributorId);

			//newly added for filed 37
			String referenceNumber1 = requestISOMsg.getString(LRCISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_1);
			if (referenceNumber1 != null){
				referenceNumber1 = referenceNumber1.trim();
			}
			posGWAccountRechargeDTO.setReferenceNumber1(referenceNumber1);
			//end
			//logger.debug(" 37 referenceNumber1 from ISO Msg : "+referenceNumber1);

			// not modified, direclty using field 41
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
			//logger.debug(" 41 storemanagerId from ISO Msg : "+storemanagerId);


//			 not modified, direclty using field 42
			// Note: Columbia must send 0's
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

			//newly added for columbia 43
			//Note: No multiple fileds for subcriber number
			String subscriberNum = requestISOMsg.getString(LRCISOMessageFieldIDConstant.SUBSSCRIBER_NUMBER);
			if (subscriberNum != null){
				subscriberNum = subscriberNum.trim();
			}
			posGWAccountRechargeDTO.setSubscriberMIN(subscriberNum);
			//logger.debug(" 43 subscriberNum from ISO Msg : "+subscriberNum);

			//newly added for columbia 48
			String cardAcceptorName = requestISOMsg.getString(LRCISOMessageFieldIDConstant.CARD_ACCEPTOR_NAME);
			if (cardAcceptorName != null){
				cardAcceptorName = cardAcceptorName.trim();
			}
			posGWAccountRechargeDTO.setCardAcceptorName(cardAcceptorName);
			//logger.debug(" 48 cardAcceptorName from ISO Msg : "+cardAcceptorName);


			//newly added for columbia 49
			String originCurrencyCode = requestISOMsg.getString(LRCISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null){
				originCurrencyCode = originCurrencyCode.trim();
			}

			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
			posGWAccountRechargeDTO.setMessageDTO(posGWIRMessageDTO);
			//logger.debug(" 48 originCurrencyCode from ISO Msg : "+originCurrencyCode);

			//newly added for columbia 57
			String referenceNumber2 = requestISOMsg.getString(LRCISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_2);
			if (referenceNumber2 != null){
				referenceNumber2 = referenceNumber2.trim();
			}
			posGWAccountRechargeDTO.setReferenceNumber2(referenceNumber2);
			//logger.debug(" 57 referenceNumber2 from ISO Msg : "+referenceNumber2);

			
			String packetCode=requestISOMsg.getString(LRCISOMessageFieldIDConstant.PACKET_CODE);
			logger.info(" 60 packet Code  from the ISOMsg::"+packetCode);
			if (packetCode != null){
				packetCode = packetCode.trim();
			}	
			
			posGWAccountRechargeDTO.setPacketCode(packetCode);
			posGWAccountRechargeDTO.setCustomerType(customerType);
			posGWAccountRechargeDTO.setTopUpId(topUpId);
			posGWAccountRechargeDTO.setReplenishmentChannelId(replenishmentChannelId);
			posGWAccountRechargeDTO.setOverrideFraudId(overrideFraudId);
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

		try
		{
			if (posMessageDTO != null)
			{
				posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posMessageDTO;
				requestISOMsg.set(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,posGWAccountRechargeDTO.getAuthorizationNumber());
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

			}
			else
			{
				requestISOMsg.set(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,"");
				//requestISOMsg.set(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());

			}
		}
		catch(ISOException isoexp){
			logger.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){

		logger.info("Domestic Columbia Recharge MIN Processor- execute()........"+posGWMessageDTO);
		ISOMsg responseISOMsg = requestISOMsg;

        try{
		Calendar startCal = Calendar.getInstance();
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
		if (posGWAccountRechargeDTO.getResponseCode().equals(ResponseCodeConstant.INITIAL_STATE))
		{
			  String status = isValidMessage(posGWAccountRechargeDTO);
			  logger.info("Validation Request Status::"+status);

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

			connectionId=posGWAccountRechargeDTO.getConnectionId();
			logger.info("ConnectionId::"+connectionId);
			
		responseISOMsg = packISOMsg(requestISOMsg,posGWAccountRechargeDTO);

		
		if(POSServerMgr.isThreadFunctReq())
		{
		logger.info("Appmonitor logging is handling from thread ......"+posGWAccountRechargeDTO.getSubscriberMIN());
		Calendar endCal = Calendar.getInstance();
		AppMonitorInitiatedThread ait = new AppMonitorInitiatedThread(posGWAccountRechargeDTO,startCal,endCal);
		POSServerMgr.assignProcessToThreadPool(ait);
			logger.info("After the Appmonitor logging......");
		}	
		else
			logger.info("Skipping the appmonitor logging. bcoz THREAD_FUNCT_REQ is defined 'N' in properties file.");
	
/*		
			//Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
			//added for Component tracking of all channels
			String tmpRefNum = posGWAccountRechargeDTO.getReferenceNumber1();
			if (PMISOFieldValidator.isAllZeroes(tmpRefNum)){
				tmpRefNum = posGWAccountRechargeDTO.getReferenceNumber2();
			}
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWAccountRechargeDTO.getTransactionId(),posGWAccountRechargeDTO.getAuthorizationNumber(),tmpRefNum, startCal, endCal,"Domestic Recharge Transaction");
				if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
			// End of AppMoniter Logging
*/
        }
        catch(Exception exp){
        	exp.printStackTrace();
			try
			{
		    responseISOMsg.set(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID, POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR));

			logger.error("Domestic RechargeMINProcessor-Exception at execute(): "+exp.getMessage());
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

	public String getConnectionId()
	{
		return connectionId;
	}


    public void updateSendMsgStatus(String transactionID,String status){

	}

}
