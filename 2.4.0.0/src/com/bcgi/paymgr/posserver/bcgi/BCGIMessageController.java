package com.bcgi.paymgr.posserver.bcgi;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;
import org.jpos.util.ThreadPool;
import java.io.IOException;
import com.aircls.common.pos.serverconstants.ResponseCodes;
import com.bcgi.paymgr.posserver.*;
import com.bcgi.paymgr.posserver.bcgi.constant.ISOMessageFieldIDConstant;
import com.bcgi.paymgr.posserver.bcgi.processor.AuthenticationProcessor;
import com.bcgi.paymgr.posserver.bcgi.processor.MessageTypeValidator;
import com.bcgi.paymgr.posserver.fw.controller.MessageController;
import com.bcgi.paymgr.posserver.fw.processor.MessageProcessor;
import com.bcgi.paymgr.posserver.bcgi.constant.ProcessorConstant;
import com.aircls.common.pos.serverconstants.TransactionStates;

public class BCGIMessageController implements MessageController
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(BCGIMessageController.class);
	private int messageFormat = 0;
	protected static ThreadPool pool;
	private int workerThreadMinSize = 0;
	private int workerThreadMaxSize = 0;
	
	public BCGIMessageController(int msgFormat) {
    	messageFormat = msgFormat;
    }
	
	public void setWorkerThreadSize(int minsize , int maxsize){
		this.workerThreadMinSize = minsize;
		this.workerThreadMaxSize = maxsize;
	}
	
	public boolean process(ISOSource source, ISOMsg requestISOMsg)
    {
		  if (pool == null)
		   {
		      pool = new ThreadPool(workerThreadMinSize, workerThreadMaxSize);
		   }
		        
		   pool.execute(new BCGIMessageHandler(source,requestISOMsg,messageFormat));
		   return true;
	}

}

