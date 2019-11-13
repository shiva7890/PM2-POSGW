package com.bcgi.paymgr.posserver.bcgi.processor;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.fw.processor.MessageProcessor;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;
import com.bcgi.paymgr.posserver.fw.processor.BaseMessageProcessor;

public class EchoProcessor implements MessageProcessor{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(ReversalMINProcessor.class);

	public EchoProcessor() {

	}

	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO) {
		logger.info("EchoProcessor - execute()........");
		ISOMsg responseISOMsg = requestISOMsg;
		try
		{
			String status = isValidReferenceNum(requestISOMsg.getString(ISOMessageFieldIDConstant.RETRIEVAL_REFERENCE_NUMBER));
			if (status.equals(ResponseCodes.INTERNAL_SUCCESS_STATUS)){
				status = ResponseCodes.SUCCESS;
			}
			responseISOMsg.set(ISOMessageFieldIDConstant.RESPONSE_CODE_ID,status);
		}
		catch(ISOException isoexp){
			logger.error("execute(ISOMsg requestISOMsg): Exception occured during execute() "+ isoexp.getMessage());
		}
		return responseISOMsg;
	}
	private String isValidReferenceNum(String refNum){
		String status = ReqMsgDataValidator.isValidReferenceNumer(refNum);
		return status;
	}

	public void updateSendMsgStatus(String transactionID,String status){

	}


}
