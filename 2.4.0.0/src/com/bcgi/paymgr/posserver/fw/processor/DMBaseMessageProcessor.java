package com.bcgi.paymgr.posserver.fw.processor;
import org.jpos.iso.ISOMsg;

import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;

public abstract class DMBaseMessageProcessor implements DMMessageProcessor
{
  	public abstract String isValidMessage(POSGWMessageDTO posMessageDTO);
	public abstract POSGWMessageDTO processMessage(POSGWMessageDTO posMessageDTO);
	public abstract POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO);
	public abstract ISOMsg packISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO);
	public abstract ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO);
	public abstract ISOMsg packResponseMessage(ISOMsg reqISOMsg);
	public void updateSendMsgStatus(String transactionID,String status){
		
	}
	
	
	
	
}

