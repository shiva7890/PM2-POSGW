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
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.POSGW_InternalResponseConstant;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.delegate.LRCTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.domestic.columbia.validator.LRCRechargeInquiryValidator;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.paymgr.posserver.irp.dto.POSGWIRMessageDTO;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;

public class LRCRechargeInquiryProcessor extends DMBaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRCRechargeInquiryProcessor.class);
	public LRCRechargeInquiryProcessor()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodeConstant.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = LRCRechargeInquiryValidator.isValidReqData(posGWAccountRechargeDTO);
		}
		/*else
		{
			status = ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
		}*/
		logger.info("DMRechargeValidator.isValidReqData - Status:"+status);

		return status;
	}


	public POSGWMessageDTO processMessage(POSGWMessageDTO reqPOSGWMessageDTO)
	{

		POSGWMessageDTO respPOSGWMessageDTO = null;
		TransDetDTO transDetDTO =null;
		
		try
		{
			transDetDTO = packToServerDTO(reqPOSGWMessageDTO);
			if (transDetDTO != null){
				logger.info("transReqDetDTO is not null");	
			}
			else{
				logger.info("transReqDetDTO is null");
			}
			logger.info("Calling DMTransaction Delegate....");
			LRCTransactionDelegate dmTransactionDelegate = new LRCTransactionDelegate(transDetDTO.getFinIstutinalReferenceNumber());
			transDetDTO = dmTransactionDelegate.processRechargeInquiryTransaction(transDetDTO);
			logger.info(transDetDTO.toString());
			
			respPOSGWMessageDTO = unpackFromServerDTO(transDetDTO,reqPOSGWMessageDTO,false);

		}
		catch (DelegateException exp){
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
			//added by Rajender Reddy on 31/01/08 to avoid the generating 
			//of TxID for recharge Inquiry for both synch and Asynch
			reqTransDetDTO.setAsynchronus(true);
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
	    	System.out.println("In packToServerRejectTransDTO Before packing in Reject Reason ServerDTO -posGWAccountRechargeDTO.toString(): "+posGWAccountRechargeDTO.toString());
			 
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
	    	
		    String referenceNum1 = posGWAccountRechargeDTO.getReferenceNumber1();
		    if (referenceNum1 ==null || referenceNum1.length()==0 || PMISOFieldValidator.isAllZeroes(referenceNum1)){
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
	    	transDetDTO.setTransactionType(AppConstant.TRANS_RECHARGE+"");
	    	transDetDTO.setAsynchronus(false);
	    	
	    	
	    	return transDetDTO;
	    }

	

	public POSGWMessageDTO unpackFromServerDTO(TransDetDTO transDetDTO,POSGWMessageDTO posGWMessageDTO, boolean isPin)
	{
        logger.error("unpackFromServerDTO.........");
		//POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
		if (transDetDTO!= null)
		{
						
		    String errorId = "";
		    errorId = transDetDTO.getErrorId();
			
			//getting client response code
			String posgwErrorId = PM_DMPOSGW_MapConstant.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "+posgwErrorId);
			logger.info("Date and Time of the Transaction: "+transDetDTO.getTransDateAndTime());
			posGWAccountRechargeDTO.setResponseCode(posgwErrorId);
			posGWAccountRechargeDTO.setSettlmentDate(transDetDTO.getTransDateAndTime());
			//added for component tracking of all channels
			posGWAccountRechargeDTO.setTransactionId(transDetDTO.getTransId());
		}
		return posGWAccountRechargeDTO;
	}

    public TransDetDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
    {
        logger.info ("packToServerDTO(POSGWMessageDTO posGWMessageDTO)....");
        if (posGWMessageDTO != null){
        	 logger.info ("posGWMessageDTO is not null....");	
        }
        else{
        	logger.info ("posGWMessageDTO is null");	
        }
    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
       
        System.out.println("In packToServerDTO Before packing in ServerDTO -posGWAccountRechargeDTO.toString(): "+posGWAccountRechargeDTO.toString());
      
        TransDetDTO transDetDTO = new TransDetDTO();
        
	    String transAmt = posGWAccountRechargeDTO.getTransactionAmount();
    	if (transAmt != null)
    	{
    		if (PMISOFieldValidator.isNumericDoubleValue(transAmt))
    		{
    			String strDoubleResult = DataUtil.getValidPaymentAmount(transAmt);
    			strDoubleResult = (strDoubleResult==null || strDoubleResult.length()==0)?"0":strDoubleResult.trim();
    			transDetDTO.setPaymentAmount(Double.parseDouble(strDoubleResult));
    		}
    	}
    	else
    	{
    		transDetDTO.setPaymentAmount(0);
    	}
        
	    String referenceNum1 = posGWAccountRechargeDTO.getReferenceNumber1();
	    if (  referenceNum1==null ||referenceNum1.length()==0 || PMISOFieldValidator.isAllZeroes(referenceNum1) ){
	    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getReferenceNumber2());
	    }
	    else{
	    	transDetDTO.setFinIstutinalReferenceNumber(referenceNum1);
	    }
	    
	    String finRefNum = transDetDTO.getFinIstutinalReferenceNumber();
	    if( PMISOFieldValidator.isAllZeroes(finRefNum))
	    	transDetDTO.setFinIstutinalReferenceNumber("");
	    
	    String autorizationNumber = posGWAccountRechargeDTO.getAuthorizationNumber();
	    autorizationNumber = (autorizationNumber==null || autorizationNumber.length()==0)?"":autorizationNumber;
	    
		/*Following code is modified by sridhar.V on 19-Dec-2011 to input the Autherization Number(6 Digit)
		 * insted of Transaction Number (16 Digit)
	     */
	    /*if( PMISOFieldValidator.isAllZeroes(autorizationNumber))
	    	transDetDTO.setTransId("");
	    else
	    	transDetDTO.setTransId(autorizationNumber);*/
	    
	    if( PMISOFieldValidator.isAllZeroes(autorizationNumber))
	    	transDetDTO.setRechargeAuthorizationID("");
	    else
	    	transDetDTO.setRechargeAuthorizationID(autorizationNumber);
	    
	    transDetDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
        
	    String subDistId = posGWAccountRechargeDTO.getSubAgentId();
	   	if (PMISOFieldValidator.isAllZeroes(subDistId)){
	   		 //Resetting sub dist id to empty because, pm will check subdist info if this is not empty
	   		transDetDTO.setSubDistributorId("");
	   	}
	   	else
	   		transDetDTO.setSubDistributorId(subDistId);

	   	transDetDTO.setTerminalId(posGWAccountRechargeDTO.getStoreId());
 	    String customerId = DataUtil.getSubscriberNumber(posGWAccountRechargeDTO.getSubscriberMIN());
 	   transDetDTO.setCustomerId(customerId);
        return transDetDTO;
	}

/*
	Amount	                    04
	Transmission Date & Time	07
	System Trace Audit Number	11
	Expiration Date	            14
	Capture Date	            17
	Modality	                25
	Acquiring Institution Identification Code	32
	Retrieval Reference Number	37
	Authorization Number	    38
	Terminal ID	                41
	Merchant ID	                42
	MIN	                        43
	Card Acceptor Name/Location	48
	Transaction Currency Code	49
	Retrieval Reference Number	57
 */    

	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.INITIAL_STATE);
		
		try
		{
            String customerType           = "1";
            String topUpId                = "1";
            String replenishmentChannelId = "6";
            String overrideFraudId        = "off";
            String paymentTypeId          = "1";
            
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
			
			//******   skipping expiry date because in IRP also not using 14 *****
			
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
			
            
			//Modified for columbia  32 from 24			
			String distributorId = requestISOMsg.getString(LRCISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			logger.info("Distributor id from the ISOMsg::"+distributorId);
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
			logger.info("Reference Number 1== >"+referenceNumber1);
			//end
			
			//Not modified filed # 38
			String authorizationNumber = requestISOMsg.getString(LRCISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
			if (authorizationNumber != null){
				authorizationNumber = authorizationNumber.trim();
			}
			posGWAccountRechargeDTO.setAuthorizationNumber(authorizationNumber);

			
//			Not modified filed # 41
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
			
//			Not modified filed # 42
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
			posGWAccountRechargeDTO.setIrMessageDTO(posGWIRMessageDTO);

			//newly added for columbia 57
			String referenceNumber2 = requestISOMsg.getString(LRCISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER_2);
			if (referenceNumber2 != null){
				referenceNumber2 = referenceNumber2.trim();
			}
			posGWAccountRechargeDTO.setReferenceNumber2(referenceNumber2);
			logger.info("Reference Number == >"+referenceNumber2);
			posGWAccountRechargeDTO.setCustomerType(customerType);
			posGWAccountRechargeDTO.setTopUpId(topUpId);
			posGWAccountRechargeDTO.setReplenishmentChannelId(replenishmentChannelId);
			posGWAccountRechargeDTO.setOverrideFraudId(overrideFraudId);
			posGWAccountRechargeDTO.setPaymentTypeId(paymentTypeId);
		}
		catch(Exception exp){
			posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR);
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
			}
		}
		catch(ISOException isoexp){
			logger.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){

		logger.info("DM Recharge Enquiry Processor - execute()........"+posGWMessageDTO);
		ISOMsg responseISOMsg = requestISOMsg;
		
        try
        {
        Calendar startCal = Calendar.getInstance();
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
		if (posGWAccountRechargeDTO.getResponseCode().equals(ResponseCodeConstant.INITIAL_STATE))
		{
			  String status = isValidMessage(posGWAccountRechargeDTO);

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
		//added for component tracking of all channels
		//Logging the RoundTrip Time using AppMoniter
		Calendar endCal = Calendar.getInstance();
		long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
		//the bellow line of code is modified for Component tracking of all channels
		String tmpRefNum = posGWAccountRechargeDTO.getReferenceNumber1();
		if (PMISOFieldValidator.isAllZeroes(tmpRefNum)){
			tmpRefNum = posGWAccountRechargeDTO.getReferenceNumber2();
		}
		String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWAccountRechargeDTO.getTransactionId(),posGWAccountRechargeDTO.getAuthorizationNumber(),tmpRefNum, startCal, endCal,"Domestic Recharge inquiry Transaction");
			if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
				AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_INQUIRY_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
			else
				AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_INQUIRY_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
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
