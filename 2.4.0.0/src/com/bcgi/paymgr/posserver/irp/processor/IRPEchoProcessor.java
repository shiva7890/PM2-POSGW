package com.bcgi.paymgr.posserver.irp.processor;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;


import com.aircls.common.pos.dto.POSMessageDTO;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.delegate.DelegateException;
import com.bcgi.paymgr.posserver.delegate.RechargeDelegate;
import com.bcgi.paymgr.posserver.fw.processor.IRPBaseMessageProcessor;
import com.bcgi.paymgr.posserver.fw.processor.IRPMessageProcessor;
import com.bcgi.paymgr.posserver.irp.constant.IRPISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.PlaceHolderDataConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.irp.dto.POSGWEchoDTO;
import com.bcgi.paymgr.posserver.irp.validator.IRPEchoValidator;

public class IRPEchoProcessor extends IRPBaseMessageProcessor
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(IRPRechargeMINProcessor.class);
	
	public IRPEchoProcessor(){
	}
	
	
	public String isValidMessage(POSGWMessageDTO posGWMessageDTO)
	{
		String status = ResponseCodes.INITIAL_STATE;
		if (posGWMessageDTO != null)
		{
			POSGWEchoDTO posGWEchoDTO = (POSGWEchoDTO) posGWMessageDTO;
			logger.info(posGWEchoDTO.toString());
			status = IRPEchoValidator.isValidReqData(posGWEchoDTO);
    		logger.info("IRPEchoValidator.isValidReqData:"+status);
		}
		else
		{
			status = ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT;
		}
		logger.info("IRPEchoValidator.isValidReqData:"+status);
		return status;
	}


	public POSGWMessageDTO processMessage(POSGWMessageDTO posGWMessageDTO)
	{
		
		return posGWMessageDTO;
	}

	/**
	 * Unpacks ISO Message to retrieve Authentication related field values.
	 */
	public POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		POSGWEchoDTO posGWEchoDTO = new POSGWEchoDTO();
		posGWEchoDTO.setResponseCode(ResponseCodes.INITIAL_STATE);


		try
		{
			
			posGWEchoDTO.setRequestType(requestISOMsg.getMTI());
			posGWEchoDTO.setTransactionDateTime(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME));
			posGWEchoDTO.setSystemTraceAuditNumber(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER));
			posGWEchoDTO.setDistributorId(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID));
			posGWEchoDTO.setNetworkMgmtInfoCode(requestISOMsg.getString(IRPISOMessageFieldIDConstant.NETWORK_MANAGEMENT_CODE));
			

		}
		catch(ISOException isoexp){
			posGWEchoDTO.setResponseCode(ResponseCodes.REQUEST_MESSAGE_NOT_CORRECT);
			logger.error("unpackISOMsg(ISOMsg requestISOMsg): Exception occured during unpacking ISOMessage "+ isoexp.getMessage());
		}
		return posGWEchoDTO;
	}

	public ISOMsg packISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO)
	{
		
		try
		{
			if (posGWMessageDTO != null)
			{
				POSGWEchoDTO posGWEchoDTO =(POSGWEchoDTO)posGWMessageDTO;
        		requestISOMsg.set(IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID,posGWEchoDTO.getResponseCode());
			}
		}
		catch(ISOException isoexp){
			logger.error("packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO): Exception occured during packing ISOMessage "+ isoexp.getMessage());
		}
		return requestISOMsg;
	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO){
		
		logger.info("IRPEchoProcessor- execute()........");
		ISOMsg responseISOMsg = requestISOMsg;
		try
		{
			POSGWEchoDTO posGWEchoDTO =  (POSGWEchoDTO) unpackISOMsg(requestISOMsg,posGWMessageDTO);
			
			
			if (posGWEchoDTO.getResponseCode().equals(ResponseCodes.INITIAL_STATE))
			{
				String status = isValidMessage(posGWEchoDTO);
				if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
					status = ResponseCodes.SUCCESS;
				}
				posGWEchoDTO.setResponseCode(status);
			}
			
			responseISOMsg = packISOMsg(requestISOMsg,posGWEchoDTO);
		}
		catch(Exception exp)
		{
			try
			{
				responseISOMsg.set(new ISOField (IRPISOMessageFieldIDConstant.RESPONSE_CODE_ID, ResponseCodes.POSGATEWAY_SYSTEM_ERROR));
				logger.error("IRPEchoProcessor-Exception at execute(): "+exp.getMessage());
			}
			catch(Exception ex){
				logger.error("IRPEchoProcessor-Exception at execute(): "+exp.getMessage());
				
			}
		}
		
		return responseISOMsg;
	}
	
	public ISOMsg packResponseMessage(ISOMsg requestISOMsg){
		ISOMsg respISOMsg = null;
		try
		{
			
			// requestISOMsg.setResponseMTI();

			respISOMsg = requestISOMsg;

            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME))){
            	respISOMsg.set(IRPISOMessageFieldIDConstant.TRANSACTION_DATE_TIME,PlaceHolderDataConstant.TRANSACTION_DATE_TIME);
			}
            
            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER))){
             	respISOMsg.set(IRPISOMessageFieldIDConstant.SYSTEM_TRACE_AUDIT_NUMBER,PlaceHolderDataConstant.SYSTEM_TRACE_AUDIT_NUMBER);
     		}
			
            if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID))){
              	respISOMsg.set(IRPISOMessageFieldIDConstant.DISTRIBUTOR_ID,PlaceHolderDataConstant.DISTRIBUTOR_ID);
                	
			}
            
           if (!PMISOFieldValidator.isValueExists(requestISOMsg.getString(IRPISOMessageFieldIDConstant.NETWORK_MANAGEMENT_CODE))){
             	respISOMsg.set(IRPISOMessageFieldIDConstant.NETWORK_MANAGEMENT_CODE,PlaceHolderDataConstant.NETWORK_MANAGEMENT_CODE);
                
			}
			
			byte[] messagebytes = respISOMsg.pack();
			logger.error("RESPONSE MESG - TO CLIENT in HEX "+ISOUtil.hexString(messagebytes));
			
			
		}
		catch (ISOException exp)
		{
			logger.error("packResponseMessage(): ISOException"+exp.getMessage());
		}
		return respISOMsg;
	}
}

