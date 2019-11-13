package com.bcgi.paymgr.posserver.domestic.columbia;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.util.ThreadPool;

import com.bcgi.paymgr.posserver.fw.controller.MessageController;


public class LRCMessageController implements MessageController
{
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(LRCMessageController.class);
	private int messageFormat = 0;
	protected static ThreadPool pool;
	private int workerThreadMinSize = 0;
	private int workerThreadMaxSize = 0;
		
	public LRCMessageController(int msgFormat)
	{
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
	    
		
	   pool.execute(new LRCMessageHandler(source,requestISOMsg,messageFormat));
	   return true;
		
	}
	
}

