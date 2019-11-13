package com.bcgi.paymgr.posserver.fw.processor;

import org.jpos.iso.ISOMsg;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.irp.constant.IRPTransactionTypeConstant;
import com.bcgi.paymgr.posserver.irp.dto.POSGWMessageDTO;

public abstract class IRPBaseMessageProcessor implements IRPMessageProcessor
{
  	public abstract String isValidMessage(POSGWMessageDTO posMessageDTO);
	public abstract POSGWMessageDTO processMessage(POSGWMessageDTO posMessageDTO);
	public abstract POSGWMessageDTO unpackISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO);
	public abstract ISOMsg packISOMsg(ISOMsg requestISOMsg,POSGWMessageDTO posMessageDTO);
	public abstract ISOMsg execute(ISOMsg requestISOMsg,POSGWMessageDTO posGWMessageDTO);
	public abstract ISOMsg packResponseMessage(ISOMsg reqISOMsg);
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
		String transactionType = "0";
		
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
