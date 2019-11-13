package com.bcgi.paymgr.posserver.fw.processor;

import org.jpos.iso.ISOMsg;
import com.aircls.common.pos.dto.POSMessageDTO;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPTransactionTypeConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;

public abstract class BaseMessageProcessor implements MessageProcessor
{
  	public abstract String isValidMessage(POSMessageDTO posMessageDTO);
	public abstract POSMessageDTO processMessage(POSMessageDTO posMessageDTO);
	public abstract POSMessageDTO unpackISOMsg(ISOMsg requestISOMsg);
	public abstract ISOMsg packISOMsg(ISOMsg requestISOMsg,POSMessageDTO posMessageDTO);
	public abstract ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO);
	public void updateSendMsgStatus(String transactionID,String status){

	}
	
	public int getTransactionType(int messageFormatType, String requestTypeId,String processingCode)
	{
		int transactionType = 0;
		switch(messageFormatType)
		{
		   
		   case MessageFormatConstant.BCGI_MESSAGE_FORMAT_ID:
		   {
			   transactionType = getTransactionType(requestTypeId,processingCode);
			   break;
		   }
		}
		
		return transactionType;
	}
	
	public int getTransactionType(String requestTypeId,String processingCode)
	{
		int transactionType = 0;
		return transactionType;
	}
	
	public String getIRPTransactionType(String requestTypeId)
	{
		String transactionType = "";
		
		if(requestTypeId.equals(IRPMessageTypeIDConstant.ECHO_MSG_TYPE_ID))
		{
			transactionType = IRPTransactionTypeConstant.ECHO_TRANSACTION_TYPE;
		}
		else if(requestTypeId.equals(IRPMessageTypeIDConstant.RECHARGE_MSG_TYPE_ID))
		{
			transactionType = IRPTransactionTypeConstant.RECHARGE_TRANSACTION_TYPE;
					
		}
		else if(requestTypeId.equals(IRPMessageTypeIDConstant.REVERSAL_MSG_TYPE_ID))
		{
			transactionType = IRPTransactionTypeConstant.REVERSAL_TRANSACTION_TYPE;
		}
		else if(requestTypeId.equals(IRPMessageTypeIDConstant.TRANSACTION_STATUS_INQUIRY_MSG_TYPE_ID))
		{
			transactionType = IRPTransactionTypeConstant.TRANSACTION_STATUS_INQUIRY_TYPE;
	
		}
		
		return transactionType;
	}
	
	
}
