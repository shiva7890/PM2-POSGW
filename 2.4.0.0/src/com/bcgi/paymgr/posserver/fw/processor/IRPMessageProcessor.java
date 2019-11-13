package com.bcgi.paymgr.posserver.fw.processor;

import org.jpos.iso.ISOMsg; 
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;
public interface IRPMessageProcessor {
	
	public ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO);
	public void updateSendMsgStatus(String transactionID,String status);
	public ISOMsg packResponseMessage(ISOMsg reqISOMsg);
}
