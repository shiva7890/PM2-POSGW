package com.bcgi.paymgr.posserver.domestic.ecuador.processor;



import java.util.Calendar;
import java.util.Date;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
//import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.ecuador.delegate.DMTransactionDelegate;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMReversalValidator;
import com.bcgi.paymgr.posserver.common.constant.AppConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.pmcore.dto.TransDetDTO;
import com.bcgi.pmcore.dto.RejectTransactionDTO;


public class DMReversalMINProcessor  extends DMBaseMessageProcessor{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(DMReversalMINProcessor.class);

	public DMReversalMINProcessor()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodeConstant.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = DMReversalValidator.isValidReqData(posGWAccountRechargeDTO);
		}
		else
		{
			status = ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
		}
		logger.info("DMReversalValidator.isValidReqData - Status:"+status);

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
			DMTransactionDelegate rechargeDelegate = new DMTransactionDelegate(ServerConfigConstant.RR_SYNCHRONOUS_CALL, reqTransDetDTO.getFinIstutinalReferenceNumber());
			String subscriberId = reqTransDetDTO.getCustomerId();
			boolean isPin = false;
			if (subscriberId != null && subscriberId.length() > 0)
			{
			  respTransDetDTO = rechargeDelegate.processMINReversalTransaction(reqTransDetDTO);
			}
			else
			{
				isPin = true;
			  respTransDetDTO = rechargeDelegate.processPinReversalTransaction(reqTransDetDTO);
			}
			respPOSGWMessageDTO = unpackFromServerDTO(respTransDetDTO,reqPOSGWMessageDTO,isPin);

		}
		catch (DelegateException exp){
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			respPOSGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE);
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
			DMTransactionDelegate rechargeDelegate = new DMTransactionDelegate(ServerConfigConstant.RR_SYNCHRONOUS_CALL, reqTransDetDTO.getFinIstutinalReferenceNumber());
			String transactionId = rechargeDelegate.insertRejectedRechargeTransaction(reqTransDetDTO);
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = (POSGWAccountRechargeDTO)reqPOSGWMessageDTO;
			respPOSGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
		}
		catch (DelegateException exp){
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			respPOSGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE);
			respPOSGWMessageDTO = respPOSGWAccountRechargeDTO;
			logger.error("DelegateException: "+exp.getMessage());
		}
		return respPOSGWMessageDTO;
	}
	
	 public RejectTransactionDTO packToServerRejectTransDTO(POSGWMessageDTO posGWMessageDTO)
	    {

	    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
	        String customerId = "";
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
	        
	        RejectTransactionDTO transDetDTO = new RejectTransactionDTO();
	    	transDetDTO.setCustomerId(customerId);
	    	transDetDTO.setCustomerType(posGWAccountRechargeDTO.getCustomerType());
	    	transDetDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
	    	transDetDTO.setOperatorId(posGWAccountRechargeDTO.getOperatorID());
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
	    	
	    	 String transInfo[]=null;
	    		transInfo=DataUtil.getTransTypeCategory(posGWAccountRechargeDTO.getProcessingCode());
	    	
	       if(transInfo!=null && transInfo.length>=1)
	       {
	    	
	    	transDetDTO.setTransactionCode(transInfo[0]);
	       }
	    	
	    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getSystemTraceAuditNumber());
	    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
	    	transDetDTO.setCustAccntNumb("0");
	    	transDetDTO.setPaymentTypeId(Integer.parseInt(posGWAccountRechargeDTO.getPaymentTypeId()));
	    	//transDetDTO.setErrorCode(posGWAccountRechargeDTO.getResponseCode());
	    	transDetDTO.setResponseCode(posGWAccountRechargeDTO.getResponseCode());
	    	transDetDTO.setMappedErrorCodes(true);
	    	transDetDTO.setTransactionType(AppConstant.TRANS_REVERSAL+"");
	    	transDetDTO.setAsynchronus(false);
	    	
	    	return transDetDTO;
	    }

	

/*	public POSGWMessageDTO unpackFromServerDTO(TransDetDTO reqTransDetDTO, boolean isPin)
	{
        logger.error("unpackFromServerDTO.........");
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		if (reqTransDetDTO!= null)
		{
			String transactionId = reqTransDetDTO.getTransId();
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
			
			//	getting client response code	
			String posgwErrorId = PM_DMPOSGW_MapConstant.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "+posgwErrorId);
			posGWAccountRechargeDTO.setResponseCode(posgwErrorId);
			
			posGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			posGWAccountRechargeDTO.setTransactionAmount(String.valueOf(paymentAmount));
			posGWAccountRechargeDTO.setPinNumber(pinNumber);
			posGWAccountRechargeDTO.setPin(isPin);
		}
		return posGWAccountRechargeDTO;
	}*/

	//	For logging into AppMoniter previous data is lost and new data is setting in the previous method
	//since we are creating a new object
	public POSGWMessageDTO unpackFromServerDTO(TransDetDTO reqTransDetDTO,POSGWMessageDTO posGWMessageDTO, boolean isPin)
	{
		logger.error("unpackFromServerDTO.........");
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
		if (reqTransDetDTO!= null)
		{
			//Commented by Sridhar.V on 19-Dec-2011
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
			posGWAccountRechargeDTO.setPinNumber(pinNumber);
			posGWAccountRechargeDTO.setPin(isPin);
			//added for component tracking of all channels
			posGWAccountRechargeDTO.setTransactionId(reqTransDetDTO.getTransId());
		}
		return posGWAccountRechargeDTO;
	}

    public TransDetDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
    {

    	POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
        String customerId = "";
    	String subscriberId1 = posGWAccountRechargeDTO.getSubscriberId1();
        String subscriberId3 = posGWAccountRechargeDTO.getSubscriberId3();
        if (subscriberId1 != null){
        	customerId = subscriberId1;
        }
        else if (subscriberId3 != null){
        	customerId = subscriberId3;
        }
        
    	TransDetDTO transDetDTO = new TransDetDTO();
    	transDetDTO.setCustomerId(customerId);
    	//Customer Type field is required for Reject screen So, removed the comment for below line: By Srinivas P. on 06 Oct 2006.
    	transDetDTO.setCustomerType(posGWAccountRechargeDTO.getCustomerType());
    	transDetDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
    	transDetDTO.setOperatorID(posGWAccountRechargeDTO.getOperatorID());
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
    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getSystemTraceAuditNumber());
    	transDetDTO.setSalesPersonId(posGWAccountRechargeDTO.getStoreId());
    	//transDetDTO.setTopUpId(posGWAccountRechargeDTO.getTopUpId());
    	transDetDTO.setGenBy(posGWAccountRechargeDTO.getInitiatedUser());
    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
    	//transDetDTO.setOverRideFraud(posGWAccountRechargeDTO.getOverrideFraudId());
    	transDetDTO.setPaymentTypeId(posGWAccountRechargeDTO.getPaymentTypeId());
    	transDetDTO.setSubDistributorId(posGWAccountRechargeDTO.getSubAgentId());
    	
    	if(posGWAccountRechargeDTO.getMessageDTO()!=null && posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode()!=null)
    	{
    		String originCurrecncyCode = posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode();
    		originCurrecncyCode = (originCurrecncyCode==null ||originCurrecncyCode.trim().length()==0)?"0":originCurrecncyCode.trim();
    		transDetDTO.setForeignCurrencyId(Integer.parseInt(originCurrecncyCode));
    	}
    	else
    		transDetDTO.setForeignCurrencyId(0);

    	
    	logger.debug("transDetDTO==>" + transDetDTO.toString());
    	transDetDTO.setTransactionStatus(posGWAccountRechargeDTO.getTransactionStatus());
    	
    	return transDetDTO;

	}


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
           
            String processingCode=requestISOMsg.getString(DMISOMessageFieldIDConstant.PROCESSING_CODE_ID);
            if(processingCode!=null){
            	processingCode=processingCode.trim();
            }
            posGWAccountRechargeDTO.setProcessingCode(processingCode);
            posGWAccountRechargeDTO.setOperatorID(DataUtil.getMappedOperatorId(processingCode));
            //logger.info("Processing code mapped with operatorIdentity from the ISOMsg::"+posGWAccountRechargeDTO.getMvneId());
            logger.info("processingCode::::::"+processingCode);
           logger.info("DataUtil.getMappedOperatorId(processingCode)::"+DataUtil.getMappedOperatorId(processingCode));
           		
			String amount = requestISOMsg.getString(DMISOMessageFieldIDConstant.TRANSACTION_AMNT);
			if (amount != null){
				amount = amount.trim();
			}
			posGWAccountRechargeDTO.setTransactionAmount(amount);

			
			String systemTraceAuditNum = requestISOMsg.getString(DMISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER);
			if (systemTraceAuditNum != null){
				systemTraceAuditNum = systemTraceAuditNum.trim();
			}
			posGWAccountRechargeDTO.setSystemTraceAuditNumber(systemTraceAuditNum);
			String transactionDate = requestISOMsg.getString(DMISOMessageFieldIDConstant.TRANSACTION_DATE);
			if (transactionDate != null){
				transactionDate = transactionDate.trim();
			}
			logger.info("transactionDate: "+transactionDate);
			
			posGWAccountRechargeDTO.setTransactionDate(transactionDate);
			String transactionTime = requestISOMsg.getString(DMISOMessageFieldIDConstant.TRANSACTION_TIME);	
			if (transactionTime != null){
				transactionTime = transactionTime.trim();
			}
			posGWAccountRechargeDTO.setTransactionTime(transactionTime);
			logger.info("transactionTime: "+transactionTime);

			
			String distributorId = requestISOMsg.getString(DMISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			if (distributorId != null){
				distributorId = distributorId.trim();
			}
			posGWAccountRechargeDTO.setInitiatedUser(distributorId); //GenBy
			posGWAccountRechargeDTO.setDistributorId(distributorId);
			
			String storemanagerId  = requestISOMsg.getString(DMISOMessageFieldIDConstant.STORE_MANAGER_ID);
			if (storemanagerId != null){
				storemanagerId = storemanagerId.trim();
			}
			posGWAccountRechargeDTO.setStoreId(storemanagerId);

			String subagentId = requestISOMsg.getString(DMISOMessageFieldIDConstant.SUBAGENT_ID);
			if (subagentId != null){
				subagentId = subagentId.trim();
			}
			posGWAccountRechargeDTO.setSubAgentId(subagentId);

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
			
			String authorizationNumber = requestISOMsg.getString(DMISOMessageFieldIDConstant.AUTHORIZATION_NUMBER);
			if (authorizationNumber != null){
				authorizationNumber = authorizationNumber.trim();
			}
			posGWAccountRechargeDTO.setAuthorizationNumber(authorizationNumber);
			
			
//			newly added for costa rica 49
			String originCurrencyCode = requestISOMsg.getString(DMISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null && originCurrencyCode.length()>0){
				originCurrencyCode = originCurrencyCode.trim();
				
			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
			posGWAccountRechargeDTO.setMessageDTO(posGWIRMessageDTO);
			
			}
			
			//newly added for costa rica 63 for receiving channel id
			String reqChannelId = requestISOMsg.getString(DMISOMessageFieldIDConstant.CHANNEL_ID);
			if(reqChannelId!=null&&reqChannelId.trim().length()>0)
				replenishmentChannelId = reqChannelId;
			
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
				requestISOMsg.set(DMISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,transId);
				//}
				requestISOMsg.set(DMISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());
			    String responseCode = posGWAccountRechargeDTO.getResponseCode();
			    
			    if (responseCode != null)
			    {
			    	if (!responseCode.equals(ResponseCodeConstant.SUCCESS))
			    	{
			    		Date todayDate = Calendar.getInstance().getTime();
				     	requestISOMsg.set(DMISOMessageFieldIDConstant.TRANSACTION_TIME, ISODate.getTime(todayDate));
						
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

		logger.info("IRP Recharge MIN Processor- execute()........");
		ISOMsg responseISOMsg = requestISOMsg;
		
		boolean isAutoReversalFlag = false;
		
        try{
			Calendar startCal = Calendar.getInstance();
        	if(posGWMessageDTO!=null)
        	{
        		logger.info("Auto reversal initiated....");
        		isAutoReversalFlag = true;
        		posGWMessageDTO = null;
        	}

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
					
			  }
		}

		responseISOMsg = packISOMsg(requestISOMsg,posGWAccountRechargeDTO);

			//	Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
			String tmpStr="";
			if(posGWAccountRechargeDTO.isPin()){
				//added for component tracking of all channels
				tmpStr=posGWAccountRechargeDTO.getTransactionId();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(tmpStr,posGWAccountRechargeDTO.getAuthorizationNumber(),posGWAccountRechargeDTO.getSystemTraceAuditNumber(), startCal, endCal,"Domestic PIN Reversal Transaction");
				if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.PIN_REVERSAL_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.PIN_REVERSAL_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
			}
			else
			{
				tmpStr=posGWAccountRechargeDTO.getTransactionId();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(tmpStr,posGWAccountRechargeDTO.getAuthorizationNumber(),posGWAccountRechargeDTO.getSystemTraceAuditNumber(), startCal, endCal,"Domestic Reversal Transaction");
				if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.REVERSAL_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
			}
			// End of AppMoniter Logging

        }
        catch(Exception exp){
        	exp.printStackTrace();
			try
			{
		    responseISOMsg.set(DMISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR);
		
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
