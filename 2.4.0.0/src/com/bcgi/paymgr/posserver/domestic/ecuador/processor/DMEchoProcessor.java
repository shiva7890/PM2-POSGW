package com.bcgi.paymgr.posserver.domestic.ecuador.processor;

import java.util.Calendar;
import java.util.Date;

import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWEchoDTO;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.PM_DMPOSGW_MapConstant;
import com.bcgi.paymgr.posserver.domestic.ecuador.delegate.DMTransactionDelegate;
import com.bcgi.paymgr.posserver.domestic.ecuador.validator.DMEchoValidator;
import com.bcgi.paymgr.posserver.fw.processor.DMBaseMessageProcessor;
import com.bcgi.pmcore.dto.TransDetDTO;

public class DMEchoProcessor extends DMBaseMessageProcessor {
	static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(DMEchoProcessor.class);
	
	public DMEchoProcessor() {
	}
	
	public String isValidMessage(POSGWMessageDTO posGWMessageDTO) {
		String status = ResponseCodeConstant.INITIAL_STATE;

		if (posGWMessageDTO != null) {
			logger.info("In the echo processor===>"
					+ posGWMessageDTO.toString());
			status = DMEchoValidator.isValidReqData(posGWMessageDTO);
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

			logger.info("Calling Echo Delegate....");
			DMTransactionDelegate echoDelegate = new DMTransactionDelegate(ServerConfigConstant.RR_SYNCHRONOUS_CALL, reqTransDetDTO.getFinIstutinalReferenceNumber());

			TransDetDTO respTransDetDTO = null;
			respTransDetDTO = echoDelegate.isConnectionAlive(reqTransDetDTO);

			posGWEchoDTO = (POSGWEchoDTO) unpackFromServerDTO(respTransDetDTO);
		} catch (Exception exp) {
			posGWEchoDTO
					.setResponseCode(ResponseCodeConstant.PAYMENTMANAGER_NOT_AVAILABLE);
			logger.error("DelegateException: " + exp.getMessage());
		}
		
		return posGWEchoDTO;
	}

	/**
	 * Unpacks ISO Message to retrieve Authentication related field values.
	 */
	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,
			POSGWMessageDTO posGWMessageDTO) {
		POSGWEchoDTO posGWEchoDTO = new POSGWEchoDTO();
		posGWEchoDTO.setResponseCode(ResponseCodeConstant.INITIAL_STATE);

		String distributorId = requestISOMsg
				.getString(DMISOMessageFieldIDConstant.DISTRIBUTOR_ID);
		logger.info("Distributor id from the ISOMsg::" + distributorId);
		if (distributorId != null) {
			distributorId = distributorId.trim();
		}
		posGWEchoDTO.setInitiatedUser(distributorId); // GenBy
		posGWEchoDTO.setDistributorId(distributorId);

		String storemanagerId = requestISOMsg
				.getString(DMISOMessageFieldIDConstant.STORE_MANAGER_ID);
		if (storemanagerId != null) {
			storemanagerId = storemanagerId.trim();
		}
		posGWEchoDTO.setStoreId(storemanagerId);

		String subagentId = requestISOMsg
				.getString(DMISOMessageFieldIDConstant.SUBAGENT_ID);
		if (subagentId != null) {
			subagentId = subagentId.trim();
		}
		posGWEchoDTO.setSubAgentId(subagentId);

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
			Date todayDate = Calendar.getInstance().getTime();
			
			requestISOMsg.set(DMISOMessageFieldIDConstant.TRANSACTION_TIME,
					ISODate.getTime(todayDate));
			requestISOMsg.set(DMISOMessageFieldIDConstant.TRANSACTION_DATE,
					ISODate.getDate(todayDate));
			requestISOMsg.set(DMISOMessageFieldIDConstant.RESPONSE_CODE_ID,
					((POSGWEchoDTO) posGWMessageDTO).getResponseCode());
		} catch (ISOException isoexp) {
			logger
					.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "
							+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){
		
		logger.info("EchoProcessor- execute()........");
		ISOMsg responseISOMsg = requestISOMsg;

		POSGWEchoDTO posGWEchoDTO = (POSGWEchoDTO) unpackISOMsg(requestISOMsg,
				posGWMessageDTO);

		Calendar startCal = Calendar.getInstance();
		try {
			String status = isValidMessage(posGWEchoDTO);

			if (status.equals(ResponseCodeConstant.INTERNAL_SUCCESS_STATUS)) {
				String salesPersonId = requestISOMsg
						.getString(DMISOMessageFieldIDConstant.STORE_MANAGER_ID);
			logger.info("SalesPersonId: "+salesPersonId);
			posGWEchoDTO = (POSGWEchoDTO) processMessage(posGWEchoDTO);
			logger.info(posGWEchoDTO.getErrorCode());
			} else
				posGWEchoDTO.setResponseCode(status);

			responseISOMsg = packISOMsg(requestISOMsg, posGWEchoDTO);
			
			//	Logging the RoundTrip Time using AppMoniter
			Calendar endCal = Calendar.getInstance();
			long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
			String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage("","","", startCal, endCal,"Domestic ECHO");
			AppMonitorWrapper.logEvent(AppMoniterEventConstants.ECHO_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
			// End of AppMoniter Logging
		}
		catch(Exception exp)
		{
			try
			{
				//	Logging the RoundTrip Time using AppMoniter
				Calendar endCal = Calendar.getInstance();
				long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
				String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage("","","", startCal, endCal,"Domestic ECHO");
				AppMonitorWrapper.logEvent(AppMoniterEventConstants.ECHO_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
				// End of AppMoniter Logging
				
				responseISOMsg.set(new ISOField (DMISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodeConstant.POSGATEWAY_SYSTEM_ERROR));
				logger.error("IRPEchoProcessor-Exception at execute(): "+exp.getMessage());
			}
			catch(Exception ex){
				logger.error("IRPEchoProcessor-Exception at execute(): "+exp.getMessage());
				
			}
		}
		
		String responseCode = requestISOMsg
				.getString(DMISOMessageFieldIDConstant.RESPONSE_CODE_ID);
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

		System.out
				.println("In packToServerDTO Before packing in ServerDTO -posGWEchoDTO.toString(): "
						+ posGWEchoDTO.toString());
		TransDetDTO transDetDTO = new TransDetDTO();

		transDetDTO.setDistributorId(posGWEchoDTO.getDistributorId());
		transDetDTO.setSubDistributorId(posGWEchoDTO.getSubAgentId());

		return transDetDTO;
}

	public POSGWMessageDTO unpackFromServerDTO(TransDetDTO reqTransDetDTO) {
		logger.error("unpackFromServerDTO.........");
		POSGWEchoDTO posGWEchoDTO = new POSGWEchoDTO();

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
