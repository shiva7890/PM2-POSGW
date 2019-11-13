package com.bcgi.paymgr.posserver.fw.processor;

import org.jpos.iso.ISOMsg; 
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWMessageDTO;
public interface DMMessageProcessor {
	
	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO);
	public void updateSendMsgStatus(String transactionID,String status);
	public ISOMsg packResponseMessage(ISOMsg reqISOMsg);
}
