package com.bcgi.paymgr.posserver.domestic.ecuador.processor.coupon;

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
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.delegate.DMTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.coupon.DMBrandCouponValidator;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.coupon.*;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.pmcore.dto.RejectTransactionDTO;
import com.bcgi.pmcore.dto.TransDetDTO;

public class DMBrandCouponRedemption extends DMBaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(DMBrandCouponRedemption.class);

	public DMBrandCouponRedemption()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodeConstant.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = DMBrandCouponValidator.isValidReqData(posGWAccountRechargeDTO);
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
			if (reqTransDetDTO != null){
				logger.info("reqTransDetDTO is not null");	
			}
			else{
				logger.info("reqTransDetDTO is null");
			}
			logger.info("Calling Recharge Delegate....");
			DMTransactionDelegate rechargeDelegate = new DMTransactionDelegate(ServerConfigConstant.RR_SYNCHRONOUS_CALL, reqTransDetDTO.getFinIstutinalReferenceNumber());
			respTransDetDTO = rechargeDelegate.processBrandCouponRedemption(reqTransDetDTO);
			respPOSGWMessageDTO = unpackFromServerDTO(respTransDetDTO,reqPOSGWMessageDTO);

		}
		catch (DelegateException exp){
			//TODO Based on the exception
			//Assign Payment manager system error or Payment Manager not available error
			POSGWAccountRechargeDTO respPOSGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
			//Changed response code from 98 to 99 for Defect #17285, By Srinivas P on 27 Oct 2006.
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
			logger.info("Calling Recharge Delegate....insertRejectedRechargeTransaction");
			DMTransactionDelegate rechargeDelegate = new DMTransactionDelegate(ServerConfigConstant.RR_SYNCHRONOUS_CALL, reqTransDetDTO.getFinIstutinalReferenceNumber());
			String transactionId = rechargeDelegate.insertRejectedRechargeTransaction(reqTransDetDTO);
			logger.info("After insertRejectedRechargeTransaction - transactionId"+transactionId);
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
	    	System.out.println("In packToServerRejectTransDTO Before packing in Reject Reason ServerDTO -posGWAccountRechargeDTO.toString(): "+posGWAccountRechargeDTO.toString());
			 
	        String customerId = "";
	    	String subscriberId1 = posGWAccountRechargeDTO.getSubscriberId1();
	        if (subscriberId1 != null){
	        	customerId = subscriberId1;
	        }
	        
	        RejectTransactionDTO transDetDTO = new RejectTransactionDTO();
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
	    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getSystemTraceAuditNumber());
	    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
	    	transDetDTO.setPaymentTypeId(Integer.parseInt(posGWAccountRechargeDTO.getPaymentTypeId()));
	    	transDetDTO.setCustAccntNumb("0");
	    	transDetDTO.setResponseCode(posGWAccountRechargeDTO.getResponseCode());
	    	transDetDTO.setMappedErrorCodes(true);
	    	
	    	
	    	return transDetDTO;
	    }

	



	//For logging into AppMoniter previous data is lost and new data is setting in the previous method
	//since we are creating a new object
	public POSGWMessageDTO unpackFromServerDTO(TransDetDTO reqTransDetDTO,POSGWMessageDTO posGWMessageDTO)
	{
		logger.error("unpackFromServerDTO.........");
		POSGWAccountRechargeDTO posGWAccountRechargeDTO =  (POSGWAccountRechargeDTO)posGWMessageDTO;
		if (reqTransDetDTO!= null)
		{
//			Commented by sridhar.v on 19-Dec-2011
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
		    String CoupRedeemTransID = reqTransDetDTO.getCouponRedeemTransID();
			logger.error("transactionId: "+transactionId);
			logger.error("errorId: "+errorId);
			logger.error("errorMsg: "+errorMsg);
			logger.error("expiryDate: "+expiryDate);
			logger.error("paymentAmount: "+paymentAmount);
			logger.error("taxAmount: "+taxAmount);
			logger.error("taxICE: "+taxICE);
			logger.error("taxIVA: "+taxIVA);
			//logger.error("pinNumber: "+pinNumber);
			//logger.error("decrypted pin: "+DataUtil.getDecryptedPin(pinNumber));

			//getting client response code
			String posgwErrorId = PM_DMPOSGW_MapConstant.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "+posgwErrorId);
			posGWAccountRechargeDTO.setResponseCode(posgwErrorId);
                        posGWAccountRechargeDTO.setAuthorizationNumber(transactionId);
			posGWAccountRechargeDTO.setCouponRedeemTransId(reqTransDetDTO.getCouponRedeemTransID());
			posGWAccountRechargeDTO.setCouponBenefitType(reqTransDetDTO.getPromType());
			posGWAccountRechargeDTO.setCouponBenefitValue(reqTransDetDTO.getPromValue());
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
        String customerId = "";
    	String subscriberId1 = posGWAccountRechargeDTO.getSubscriberId1();
    	if (subscriberId1 != null){
        	customerId = subscriberId1;
        }
        System.out.println("In packToServerDTO Before packing in ServerDTO -posGWAccountRechargeDTO.toString(): "+posGWAccountRechargeDTO.toString());
    	TransDetDTO transDetDTO = new TransDetDTO();
    	transDetDTO.setCustomerId(customerId);
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
    	transDetDTO.setTopUpId(posGWAccountRechargeDTO.getTopUpId());
    	transDetDTO.setGenBy(posGWAccountRechargeDTO.getInitiatedUser());
    	String replChannelId = posGWAccountRechargeDTO.getReplenishmentChannelId();
    	System.out.println("replChannelId:"+replChannelId);
    	
    	if(replChannelId==null ||replChannelId.length()==0)
    		replChannelId = "6";
    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(replChannelId));
    	transDetDTO.setOverRideFraud(posGWAccountRechargeDTO.getOverrideFraudId());
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
    	
    	transDetDTO.setPinNo(posGWAccountRechargeDTO.getPinNumber());
   	    transDetDTO.setCouponRedeemTransID(posGWAccountRechargeDTO.getCouponRedeemTransId());
    	transDetDTO.setPromType(posGWAccountRechargeDTO.getCouponBenefitType());
    	transDetDTO.setProductCode(posGWAccountRechargeDTO.getProductCode());
    	transDetDTO.setQuantity(posGWAccountRechargeDTO.getQuantity());
    	return transDetDTO;

	}


	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.INITIAL_STATE);
		

		try
		{
		    
		    String customerType = "1";
            String topUpId = "1";
            String replenishmentChannelId = "6";
            String overrideFraudId = "off";
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

			
			String distributorId = requestISOMsg.getString(DMISOMessageFieldIDConstant.DISTRIBUTOR_ID);
			logger.info("Distributor id from the ISOMsg::"+distributorId);
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
			logger.info("subscriber id from the ISOMsg::"+subscriberId1);
			if (subscriberId1 != null){
				subscriberId1 = subscriberId1.trim();
			}
			posGWAccountRechargeDTO.setSubscriberId1(subscriberId1);
			// 46 th field  PRoduct id
			String productcode  = requestISOMsg.getString(DMISOMessageFieldIDConstant.PRODUCT_ID);
			logger.info("productcode  from the ISOMsg::"+productcode);
			if (productcode != null){
				productcode = productcode.trim();
			}
			posGWAccountRechargeDTO.setProductCode(productcode);
			
			// 47 th field  quantity
			String quantity  = requestISOMsg.getString(DMISOMessageFieldIDConstant.QUANTITY);
			logger.info("quantity  from the ISOMsg::"+quantity);
			if (quantity != null){
				quantity = quantity.trim();
			}
			posGWAccountRechargeDTO.setQuantity(quantity);
			
//			newly added for costa rica 49
			String originCurrencyCode = requestISOMsg.getString(DMISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null && originCurrencyCode.length()>0){
				originCurrencyCode = originCurrencyCode.trim();
				
			POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
			posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
			posGWAccountRechargeDTO.setMessageDTO(posGWIRMessageDTO);
			
			}
			String couponPIN = requestISOMsg.getString(DMISOMessageFieldIDConstant.PIN_NUMBER1);
			//logger.info("Coupon number from the ISOMsg::"+couponPIN);
			if (couponPIN != null){
				couponPIN = couponPIN.trim();
			}
			
			//Coupon Pin for redemption of Coupon through Recharge
			posGWAccountRechargeDTO.setPinNumber(couponPIN);
			String reqChannelId = requestISOMsg.getString(DMISOMessageFieldIDConstant.CHANNEL_ID);
			if(reqChannelId!=null&&reqChannelId.trim().length()>0)
				replenishmentChannelId = reqChannelId;
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
				requestISOMsg.set(DMISOMessageFieldIDConstant.AUTHORIZATION_NUMBER,posGWAccountRechargeDTO.getAuthorizationNumber());
				requestISOMsg.set(DMISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());
			    String responseCode = posGWAccountRechargeDTO.getResponseCode();
			    
			    if (responseCode != null)
			    {
			    	if (responseCode.equals(ResponseCodeConstant.SUCCESS)) {
				     	requestISOMsg.set(DMISOMessageFieldIDConstant.REDEEMPTION_ID, posGWAccountRechargeDTO.getCouponRedeemTransId());
				     	requestISOMsg.set(DMISOMessageFieldIDConstant.COUPON_BENEFIT_TYPE, posGWAccountRechargeDTO.getCouponBenefitType());
				     	
				     	String cpnBenefitAmt =  posGWAccountRechargeDTO.getCouponBenefitValue();
				     	
				    // 	cpnBenefitAmt = DataUtil.convertToISOAmount(cpnBenefitAmt);
				     //	requestISOMsg.set(DMISOMessageFieldIDConstant.CPN_BENEFIT_AMT, cpnBenefitAmt);
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

		logger.info("Brand Coupon Redemption Processor- execute()........");
		ISOMsg responseISOMsg = requestISOMsg;
		
        try{
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
					
			  }
		}

		responseISOMsg = packISOMsg(requestISOMsg,posGWAccountRechargeDTO);

			//Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWAccountRechargeDTO.getSubscriberId1(),posGWAccountRechargeDTO.getAuthorizationNumber(),posGWAccountRechargeDTO.getSystemTraceAuditNumber(), startCal, endCal,"Brand Coupon Redeemption Transaction");
				if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.BRAND_CPN_REDEEMPTION_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				else
					AppMonitorWrapper.logEvent(AppMoniterEventConstants.BRAND_CPN_REDEEMPTION_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
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
