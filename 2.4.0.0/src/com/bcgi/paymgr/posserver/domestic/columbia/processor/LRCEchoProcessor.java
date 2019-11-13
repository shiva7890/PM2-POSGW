package com.bcgi.paymgr.posserver.domestic.columbia.processor;

import java.util.Calendar;

import org.jboss.logging.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;

import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.DataUtil;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.POSGW_InternalResponseConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.delegate.LRCTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.columbia.validator.LRCEchoValidator;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWEchoDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.pmcore.dto.TransDetDTO;

public class LRCEchoProcessor extends DMBaseMessageProcessor {
	static Logger logger = Logger
			.getLogger(LRCEchoProcessor.class);
	
	public LRCEchoProcessor() {
	}
	
	public String isValidMessage(POSGWMessageDTO posGWMessageDTO) {
		String status = ResponseCodeConstant.INITIAL_STATE;

		if (posGWMessageDTO != null) {
			logger.info("In the echo processor===>"
					+ posGWMessageDTO.toString());
			status = LRCEchoValidator.isValidReqData(posGWMessageDTO);
		} else {
			status = ResponseCodeConstant.REQUEST_MESSAGE_NOT_FORMATTED_CORRECT;
		}

		logger.info("DMEchoValidator.isValidReqData - Status:" + status);

    	return status;
	}

	public POSGWMessageDTO processMessage(POSGWMessageDTO posGWMessageDTO) {

		POSGWEchoDTO posGWEchoDTO = null;

		try {
			posGWEchoDTO = new POSGWEchoDTO();

			TransDetDTO reqTransDetDTO = packToServerDTO(posGWMessageDTO);

			if (reqTransDetDTO != null) {
				logger.info("reqTransDetDTO is not null");
			} else {
				logger.info("reqTransDetDTO is null");
			}
			//logger.info("Calling Echo Delegate....");
			
			LRCTransactionDelegate echoDelegate = new LRCTransactionDelegate(reqTransDetDTO.getFinIstutinalReferenceNumber());

			TransDetDTO respTransDetDTO = null;
			respTransDetDTO = echoDelegate.isConnectionAlive(reqTransDetDTO);
			
			posGWEchoDTO = (POSGWEchoDTO) unpackFromServerDTO(respTransDetDTO,posGWMessageDTO,false);
		} catch (Exception exp) {
			posGWEchoDTO
					.setResponseCode(POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE));
			
			logger.error("DelegateException: " + exp.getMessage());
		}
		
		return posGWEchoDTO;
	}

	/**
	 * Unpacks ISO Message to retrieve Authentication related field values.
	 */
	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,
			POSGWMessageDTO posGWMessageDTO) {
		System.out.println("this line added for testing purpose by ayub");
		POSGWEchoDTO posGWEchoDTO = new POSGWEchoDTO();
		posGWEchoDTO.setResponseCode(ResponseCodeConstant.INITIAL_STATE);
		
		//newly added for columbia.Trans Date Time
		String transDateTime = requestISOMsg.getString(LRCISOMessageFieldIDConstant.TRANSACTION_DATE_TIME);
		if(transDateTime!=null) {
			transDateTime = transDateTime.trim();
		}
		posGWEchoDTO.setTransactionDateTime(transDateTime);
			
		
		//newly added for columbia
		String sysmtemTraceNum = requestISOMsg.getString(LRCISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER);
		if (sysmtemTraceNum != null) {
			sysmtemTraceNum = sysmtemTraceNum.trim();
		}
		posGWEchoDTO.setSystemTraceAuditNumber(sysmtemTraceNum);
	

		//not modified for coloumbia,using 
		String distributorId = requestISOMsg.getString(LRCISOMessageFieldIDConstant.DISTRIBUTOR_ID);
		logger.info("Distributor id from the ISOMsg::" + distributorId);
		if (distributorId != null) {
			distributorId = distributorId.trim();
		}
		posGWEchoDTO.setInitiatedUser(distributorId); // GenBy
		posGWEchoDTO.setDistributorId(distributorId);
		
		
		//newly added for 
		String  nwMgtInfoCode = requestISOMsg.getString(LRCISOMessageFieldIDConstant.NETWORK_MANAGEMENT_CODE);
		if (nwMgtInfoCode != null) {
			nwMgtInfoCode = nwMgtInfoCode.trim();
		}
		posGWEchoDTO.setNetworkMgmtInfoCode(nwMgtInfoCode);
		

		/* columbia is not using this field
		String storemanagerId = requestISOMsg
				.getString(DMISOMessageFieldIDConstant.STORE_MANAGER_ID);
		if (storemanagerId != null) {
			storemanagerId = storemanagerId.trim();
		}
		posGWEchoDTO.setStoreId(storemanagerId);
		*/
		
		/* columbia is not using this field
		String subagentId = requestISOMsg
				.getString(DMISOMessageFieldIDConstant.SUBAGENT_ID);
		if (subagentId != null) {
			subagentId = subagentId.trim();
		}
		posGWEchoDTO.setSubAgentId(subagentId);
		*/

		return posGWEchoDTO;
	}

	public POSGWMessageDTO unpackISOMsg(TransDetDTO respTransDetDTO,
			POSGWMessageDTO posGWMessageDTO) {
		
		logger.error("unpackFromServerDTO.........");
		return posGWMessageDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,
			POSGWMessageDTO posGWMessageDTO) {
		
		try {
			//The following code added by sridhar.Vemulapalli on 28-Sep-2011 according to client Requirement
			requestISOMsg.set(LRCISOMessageFieldIDConstant.TRANSACTION_DATE_TIME,DataUtil.convertToValidDateTime(null));
			//Ends			
			requestISOMsg.set(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID,((POSGWEchoDTO) posGWMessageDTO).getResponseCode());
		} catch (ISOException isoexp) {
			logger
					.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "
							+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){
		
		logger.info("EchoProcessor- execute()........"+posGWMessageDTO);
		ISOMsg responseISOMsg = requestISOMsg;

		POSGWEchoDTO posGWEchoDTO = (POSGWEchoDTO) unpackISOMsg(requestISOMsg,
				posGWMessageDTO);

		Calendar startCal = Calendar.getInstance();
		try {
			String status = isValidMessage(posGWEchoDTO);

			if (status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
				
				/* commented for columbia
				String salesPersonId = requestISOMsg
						.getString(DMISOMessageFieldIDConstant.STORE_MANAGER_ID);
			logger.info("SalesPersonId: "+salesPersonId);
			*/
					posGWEchoDTO = (POSGWEchoDTO) processMessage(posGWEchoDTO);
					logger.info(posGWEchoDTO.getErrorCode());
			} else
			{
				  //Resetting Domestic Error code as per columbia
				  String posgwErrorCode = POSGW_InternalResponseConstant.getPOSGWResponse(status);
				  posGWEchoDTO.setResponseCode(posgwErrorCode);
			}

			responseISOMsg = packISOMsg(requestISOMsg, posGWEchoDTO);
			
			//	Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
			String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWEchoDTO.getDistributorId(),"","", startCal, endCal,"Domestic ECHO");
						if(posGWEchoDTO!=null && "00".equals(posGWEchoDTO.getResponseCode()))
				AppMonitorWrapper.logEvent(AppMoniterEventConstants.ECHO_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
			else
				AppMonitorWrapper.logEvent(AppMoniterEventConstants.ECHO_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);	

			// End of AppMoniter Logging
		}
		catch(Exception exp)
		{
			try
			{
				//	Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWEchoDTO.getDistributorId(),"","", startCal, endCal,"Domestic ECHO");
				AppMonitorWrapper.logEvent(AppMoniterEventConstants.ECHO_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				// End of AppMoniter Logging
				
				responseISOMsg.set(new ISOField (LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID, POSGW_InternalResponseConstant.getPOSGWResponse(ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR)));
				logger.error("IRPEchoProcessor-Exception at execute(): "+exp.getMessage());
			}
			catch(Exception ex){
				logger.error("IRPEchoProcessor-Exception at execute(): "+exp.getMessage());
				
			}
		}
		
		String responseCode = requestISOMsg
				.getString(LRCISOMessageFieldIDConstant.RESPONSE_CODE_ID);
		logger.info("responseCode: "+responseCode);
		
		return responseISOMsg;
	}
	
	public ISOMsg packResponseMessage(ISOMsg requestISOMsg) {
		ISOMsg respISOMsg = null;
		return respISOMsg;
	}

	public TransDetDTO packToServerDTO(POSGWMessageDTO posGWMessageDTO) {

		POSGWEchoDTO posGWEchoDTO = (POSGWEchoDTO) posGWMessageDTO;

		logger.info("packToServerDTO(POSGWMessageDTO posGWMessageDTO)....");
		if (posGWEchoDTO != null) {
			logger.info("posGWEchoDTO is not null....");
		} else {
			logger.info("posGWEchoDTO is null");
		}

		//System.out.println("In packToServerDTO Before packing in ServerDTO -posGWEchoDTO.toString(): "+ posGWEchoDTO.toString());
		TransDetDTO transDetDTO = new TransDetDTO();

		transDetDTO.setDistributorId(posGWEchoDTO.getDistributorId());
		/*
		 * commented for columbia, not sending sub dist id
		transDetDTO.setSubDistributorId(posGWEchoDTO.getSubAgentId());
		*/

		return transDetDTO;
}

	public POSGWMessageDTO unpackFromServerDTO(TransDetDTO reqTransDetDTO,POSGWMessageDTO posGWMessageDTO, boolean isPin) {
		//logger.error("unpackFromServerDTO.........");
		//POSGWEchoDTO posGWEchoDTO = new POSGWEchoDTO();
		POSGWEchoDTO posGWEchoDTO =(POSGWEchoDTO)posGWMessageDTO;

		if (reqTransDetDTO != null) {
			String errorId = reqTransDetDTO.getErrorId();
			String errorMsg = reqTransDetDTO.getErrorMsg();
			logger.error("errorId: " + errorId);
			logger.error("errorMsg: " + errorMsg);
			String posgwErrorId = PM_DMPOSGW_MapConstant
					.getPOSGWResponse(errorId);
			logger.error("After Retrieving from Map - posgwResponseId: "
					+ posgwErrorId);
			posGWEchoDTO.setResponseCode(posgwErrorId);
}

		return posGWEchoDTO;
	}
}
