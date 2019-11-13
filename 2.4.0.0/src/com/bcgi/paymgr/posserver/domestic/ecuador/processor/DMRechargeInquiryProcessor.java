package com.bcgi.paymgr.posserver.domestic.ecuador.processor;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.bcgi.paymgr.posserver.common.constant.AppConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.PlaceHolderDataConstant;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.DelegateException;
import com.bcgi.paymgr.posserver.domestic.ecuador.delegate.DMTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWIRMessageDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMRechargeInquiryValidator;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.pmcore.dto.OrderRequestDTO;
import com.bcgi.pmcore.dto.OrderResponseDTO;
import com.bcgi.pmcore.dto.RejectTransactionDTO;

public class DMRechargeInquiryProcessor extends DMBaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(DMRechargeInquiryProcessor.class);

	public DMRechargeInquiryProcessor()
	{

	}

	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodeConstant.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWAccountRechargeDTO posGWAccountRechargeDTO = (POSGWAccountRechargeDTO) posGWMessageDTO;
			logger.info(posGWAccountRechargeDTO.toString());
			status = DMRechargeInquiryValidator.isValidReqData(posGWAccountRechargeDTO);
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
		OrderRequestDTO orderRequestDTO = null;
		OrderResponseDTO orderResponseDTO = null;
		
		try
		{
			orderRequestDTO = packToServerDTO(reqPOSGWMessageDTO);
			if (orderRequestDTO != null){
				logger.info("orderRequestDTO is not null");	
			}
			else{
				logger.info("orderRequestDTO is null");
			}
			logger.info("Calling DMTransaction Delegate....");
			DMTransactionDelegate dmTransactionDelegate = new DMTransactionDelegate(ServerConfigConstant.RR_ASYNCHRONOUS_CALL, orderRequestDTO.getFinancialInstNum());
			orderResponseDTO = dmTransactionDelegate.processRechargeInquiryTransaction(orderRequestDTO);
			logger.info(orderResponseDTO.toString());
			
			respPOSGWMessageDTO = unpackFromServerDTO(orderResponseDTO);

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
			//added by Rajender Reddy on 31/01/08 to avoid the generating 
			//of TxID for recharge Inquiry for both synch and Asynch
			reqTransDetDTO.setAsynchronus(true);
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
	    	transDetDTO.setFinIstutinalReferenceNumber(posGWAccountRechargeDTO.getSystemTraceAuditNumber());
	    	transDetDTO.setReplenishmentChannelId(Integer.parseInt(posGWAccountRechargeDTO.getReplenishmentChannelId()));
	    	transDetDTO.setPaymentTypeId(Integer.parseInt(posGWAccountRechargeDTO.getPaymentTypeId()));
	    	transDetDTO.setCustAccntNumb("0");
	    	transDetDTO.setResponseCode(posGWAccountRechargeDTO.getResponseCode());
	    	transDetDTO.setMappedErrorCodes(true);
	    	transDetDTO.setTransactionType(AppConstant.TRANS_RECHARGE+"");
	    	transDetDTO.setAsynchronus(false);
	    	
	    	
	    	return transDetDTO;
	    }

	

	public POSGWMessageDTO unpackFromServerDTO(OrderResponseDTO orderResponseDTO)
	{
        logger.error("unpackFromServerDTO.........");
		POSGWAccountRechargeDTO posGWAccountRechargeDTO = new POSGWAccountRechargeDTO();
		if (orderResponseDTO!= null)
		{
						
		    boolean transactionSuccess = orderResponseDTO.isTransactionSucess();
		    String errorId = "";
		    if (transactionSuccess){
		    	errorId = "0";	
		    }
		    else
		    {
		    	errorId = orderResponseDTO.getErrorId();
		    }
			
			//getting client response code
			String posgwErrorId = PM_DMPOSGW_MapConstant.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "+posgwErrorId);
			posGWAccountRechargeDTO.setResponseCode(posgwErrorId);
			
		    int orderstatus = orderResponseDTO.getOrderStatus();
		    posGWAccountRechargeDTO.setOrderStatus(String.valueOf(orderstatus));
			
		}
		return posGWAccountRechargeDTO;
	}

    public OrderRequestDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO)
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
    	//TransactionDate and TransactionTime are both optional fields and will be used for auditing purposes;
        //Only if both fields are set with data, concatentation of date and time data is sent to PM else empty string to PM.
        String clientdatetime = "";
        if (posGWAccountRechargeDTO.getTransactionDate() != null && posGWAccountRechargeDTO.getTransactionTime() != null)
        {
        	clientdatetime = posGWAccountRechargeDTO.getTransactionDate() + posGWAccountRechargeDTO.getTransactionTime();
        }
      
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();

        orderRequestDTO.setDistributorId(posGWAccountRechargeDTO.getDistributorId());
        orderRequestDTO.setSubDistributorId(posGWAccountRechargeDTO.getSubAgentId());
        orderRequestDTO.setTerminalId(posGWAccountRechargeDTO.getStoreId());
        // Modified by Vamsi K on 28th January'08: Asynchronous Phase II: Start -->
        String orderID = posGWAccountRechargeDTO.getOrderId();
        orderID = (orderID==null ||orderID.length() == 0)?(PlaceHolderDataConstant.EMPTY_FIELD_ID):orderID;
        orderRequestDTO.setOrderId(Integer.parseInt(orderID));
        // End <--
        // Added by Vamsi K on 22nd January'08: Asynchronous Phase II: Start -->
        String validationID = posGWAccountRechargeDTO.getValidationId();
        validationID = (validationID==null ||validationID.length() == 0)?(PlaceHolderDataConstant.EMPTY_FIELD_ID):validationID;
        orderRequestDTO.setSubscriberValidationId(Integer.parseInt(validationID));
        // End <--
        
    	if(posGWAccountRechargeDTO.getMessageDTO()!=null && posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode()!=null)
    	{
    		String originCurrecncyCode = posGWAccountRechargeDTO.getMessageDTO().getOriginCurrencyCode();
    		originCurrecncyCode = (originCurrecncyCode==null ||originCurrecncyCode.trim().length()==0)?"0":originCurrecncyCode.trim();
    		orderRequestDTO.setCurrencyId(Integer.parseInt(originCurrecncyCode));
    	}
    	else
    		 orderRequestDTO.setCurrencyId(0);
        
        return orderRequestDTO;
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
            String validationId = ""; // Added by Vamsi K on 22nd January'08: Phase II (Asynchronous) 
						
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

			String orderid = requestISOMsg.getString(DMISOMessageFieldIDConstant.ORDER_ID);
			if (orderid != null){
				orderid = orderid.trim();
			}
			
			// Added by Vamsi K on 22nd January'08: Phase II (Asynchronous): Start -->
			validationId = requestISOMsg.getString(DMISOMessageFieldIDConstant.VALIDATION_ID);
			if(validationId!=null && validationId.trim().length() != 0)
			{
				validationId = validationId.trim();
			}
			else{
				validationId = "";
			}
			// End <--
	
			//newly added for costa rica 49
			String originCurrencyCode = requestISOMsg.getString(DMISOMessageFieldIDConstant.CURRENCY_CODE);
			if (originCurrencyCode != null && originCurrencyCode.length()>0){
				originCurrencyCode = originCurrencyCode.trim();
				
				POSGWIRMessageDTO posGWIRMessageDTO = new POSGWIRMessageDTO();
				posGWIRMessageDTO.setOriginCurrencyCode(originCurrencyCode);
				posGWAccountRechargeDTO.setMessageDTO(posGWIRMessageDTO);
				
			}
			
	
			posGWAccountRechargeDTO.setOrderId(orderid);
			posGWAccountRechargeDTO.setCustomerType(customerType);
			posGWAccountRechargeDTO.setTopUpId(topUpId);
			posGWAccountRechargeDTO.setReplenishmentChannelId(replenishmentChannelId);
			posGWAccountRechargeDTO.setOverrideFraudId(overrideFraudId);
			posGWAccountRechargeDTO.setPaymentTypeId(paymentTypeId);
			posGWAccountRechargeDTO.setValidationId(validationId); // Added by Vamsi K on 22nd January'08
		}
		catch(Exception exp){
			//posGWAccountRechargeDTO.setResponseCode(ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT);
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
				requestISOMsg.set(DMISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWAccountRechargeDTO.getResponseCode());
			    String responseCode = posGWAccountRechargeDTO.getResponseCode();
			    
			    if (responseCode != null)
			    {
			    	if (responseCode.equals(ResponseCodeConstant.SUCCESS))
			    	{
			    		String orderstatus = posGWAccountRechargeDTO.getOrderStatus();
			    		//this code modified by krishna d on date 13/11/2007	
			    		String posgworderstatus = "";
			    			
			    		if ("49".equalsIgnoreCase(orderstatus) )
			    		{
			    			posgworderstatus = "11";	
			    		}
			    		else if ("50".equalsIgnoreCase(orderstatus) )
			    		{
			    			posgworderstatus = "12";	
			    		}
			    		else if ("51".equalsIgnoreCase(orderstatus) )
			    		{
			    			posgworderstatus = "13";	
			    		}
			    		else if ("52".equalsIgnoreCase(orderstatus) )
			    		{
			    			posgworderstatus = "14";	
			    		}
			    		else
			    		posgworderstatus = "10";
			    					    		
			    		logger.debug("posgworderstatus in Recharge Inquiry:"+ posgworderstatus);
			    		logger.debug("orderstatus in Recharge Inquiry:"+ orderstatus);
			    		requestISOMsg.set(DMISOMessageFieldIDConstant.RECHARGE_REQUEST_STATUS,posgworderstatus);
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

		logger.info("DMSubAccntValidation Processor - execute()........");
		ISOMsg responseISOMsg = requestISOMsg;
		
        try
        {
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
