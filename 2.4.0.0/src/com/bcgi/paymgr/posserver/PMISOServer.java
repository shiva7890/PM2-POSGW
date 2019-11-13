package com.bcgi.paymgr.posserver;

import java.util.Properties;

import org.apache.log4j.Priority;
import org.jpos.core.SimpleConfiguration;
import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ServerChannel;
import org.jpos.iso.header.BaseHeader;
import org.jpos.util.Logger;
import org.jpos.util.ThreadPool;

import com.bcgi.paymgr.posserver.bcgi.BCGIMessageController;
import com.bcgi.paymgr.posserver.bcgi.msgpackager.BCGIISO87APackager;
import com.bcgi.paymgr.posserver.bcgi.msgpackager.BCGIISO87BPackager;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.dto.ServerConfigDTO;
import com.bcgi.paymgr.posserver.common.util.Log4JListener;
import com.bcgi.paymgr.posserver.domestic.columbia.LRCMessageController;
import com.bcgi.paymgr.posserver.domestic.columbia.msgpackager.LRC_ISO87APackager;
import com.bcgi.paymgr.posserver.domestic.ecuador.DMMessageController;
import com.bcgi.paymgr.posserver.domestic.ecuador.msgpackager.LRISO87BPackager;
import com.bcgi.paymgr.posserver.domestic.panama.LRPMessageController;
import com.bcgi.paymgr.posserver.domestic.panama.msgpackager.LRP_ISO87APackager;
import com.bcgi.paymgr.posserver.fw.controller.MessageController;
import com.bcgi.paymgr.posserver.irp.IRPMessageController;
import com.bcgi.paymgr.posserver.irp.msgpackager.IRPISO87APackager;
import com.bcgi.paymgr.posserver.irp.msgpackager.IRPISO87BPackager;


public class PMISOServer
{
	private static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(PMISOServer.class);
	private ISOServer isoServer = null;
	private ServerConfigDTO serverConfigDTO = null;

	public PMISOServer() {

	}

	public PMISOServer(ServerConfigDTO svrConfigDTO) {
		serverConfigDTO = svrConfigDTO;
	}

	public void shutdown()
	{
		try
		{
			if (isoServer != null)
			{
				isoServer.shutdown();
				logger.info("ISO Server stopped ");
		    }

		}
		catch(Exception exp)
		{
			logger.error("Exception at shutdown"+exp.getMessage());
		}
	}


	public void start()
	{

		try
		{
		    if (serverConfigDTO != null)
	         {
					/*String serverlogFilePath = ServerConfigConstant.LOG_FILE_FOLDER_PATH +serverConfigDTO.getIsoMsgLogFileName();
					PrintStream outputFileStream = new PrintStream (new FileOutputStream (serverlogFilePath));
					Logger isoserverlogger = new Logger ();
					isoserverlogger.addListener (new SimpleLogListener (outputFileStream));
					logger.debug ("ISOMsg Log File Path: "+serverlogFilePath);*/

		    		/*Following Block of code is added by sridhar.v on 23-Dec-2011 to control inoutmsg.log file size*/
					String serverlogFilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.INOUT_LOG_FILE_NAME;
					logger.debug ("ISOMsg Log File Path: "+serverlogFilePath);
					Logger isoserverlogger = new Logger ();
					Properties properties = new Properties();
					properties.put("config",serverlogFilePath);
					properties.put("priority",new Integer(Priority.DEBUG_INT));
					properties.put("watch",new Long(1000*60*60*60));
					SimpleConfiguration simpleConfiguration = new  SimpleConfiguration(properties);
					Log4JListener log4JListener = new Log4JListener();
					log4JListener.setConfiguration(simpleConfiguration);
					isoserverlogger.addListener(log4JListener);
					logger.info("listener added ");		
				       
					/*Ends*/
				    int    port 			 = serverConfigDTO.getPort();
				    String messageHeader 	 = serverConfigDTO.getISOMessageHeader();
				    String msgPackagerFormat = serverConfigDTO.getMsgPackagerFormat();
				    System.out.println("msgPackagerFormat::::"+msgPackagerFormat);
				    int    threadPoolMinSize = serverConfigDTO.getThreadPoolMinSize();
				    int    threadPoolMaxSize = serverConfigDTO.getThreadPoolMaxSize();
				    int    workerthreadPoolMinSize = serverConfigDTO.getWorkerThreadPoolMinSize();
				    int    workerthreadPoolMaxSize = serverConfigDTO.getWorkerThreadPoolMaxSize();
				    int     messageFormat    = serverConfigDTO.getMessageFormat();

				    //ISOPackager isoPackager  = getISOPackager(msgPackagerFormat,messageFormat);
				    //NACChannel  channel 	 = createNACChannel(messageHeader,isoPackager);
				    //The following coded is added by Sridhar.Vemulapalli on 12-Aug-2011
				    String useOldNACChannel = serverConfigDTO.getUseOldNACChannel();
				    //Ends
				    ISOPackager isoPackager  = getISOPackager(msgPackagerFormat,messageFormat);
				    BaseChannel channel = null;
				    if(useOldNACChannel!=null && "YES".equalsIgnoreCase(useOldNACChannel))
				    	channel 	 = createOldNACChannel(messageHeader,isoPackager);
				    else if (useOldNACChannel!=null && "NO".equalsIgnoreCase(useOldNACChannel))
				    	channel 	 = createNewNACChannel(messageHeader,isoPackager);
				    //Ends
				    
				    //Added for Defect 192 Added below line after the change of jpos jar from 1.3 to 1.6 
				    //inorder to print the send and receive messages in inoutisomsg.log file.  
				    channel.setLogger(isoserverlogger, "iso-server-channel");

				    if (channel != null)
				    {
				        ThreadPool threadPool = new ThreadPool (threadPoolMinSize, threadPoolMaxSize);

						logger.debug("Server Port: "+port);
						logger.debug("ThreadPoolMinSize: "+threadPoolMinSize);
						logger.debug("ThreadPoolMaxSize: "+threadPoolMaxSize);

						MessageController messageController = getMessageController(messageFormat);
						messageController.setWorkerThreadSize(workerthreadPoolMinSize,workerthreadPoolMaxSize);
			        	isoServer = new ISOServer (port, (ServerChannel) channel, threadPool);
						//isoServer.setConfiguration (new SimpleConfiguration ());
			        	isoServer.setConfiguration (simpleConfiguration);
						isoServer.setLogger (isoserverlogger, "iso-server");
						isoServer.addISORequestListener (messageController);
						new Thread (isoServer).start();

						logger.debug("ISOServer Started.........");
				    }
            }
		    else{
		    	logger.debug("Server Configuration Details does not exist. ISOServer NOT started. ");
		    }

		}
		catch (Exception exp)
		{
			logger.error("Exception at start"+exp.getMessage());
		}
	}
	
	private MessageController getMessageController(int messageFormat)
	{
		MessageController controller = null;
		switch (messageFormat) 
		{
				case MessageFormatConstant.BCGI_MESSAGE_FORMAT_ID:
				{
					controller = new BCGIMessageController(messageFormat);
					break;
				}
				case MessageFormatConstant.IRP_MESSAGE_FORMAT_ID:
				{
					controller = new IRPMessageController(messageFormat);
					logger.info("controller======>"+controller);
					break;
				}
				case MessageFormatConstant.LR_MESSAGE_FORMAT_ID:
				{
					controller = new DMMessageController(messageFormat);
					break;
				}

              case  MessageFormatConstant.LR_MESSAGE_FORMAT_COLUMBIA_ID:
                {
                     controller = new LRCMessageController(messageFormat);
                     break;
                 }
              case  MessageFormatConstant.LR_MESSAGE_FORMAT_PANAMA_ID:
              {
                   controller = new LRPMessageController(messageFormat);
                   break;
               }                
		}
		
		
		
		return controller;
	}
	//The Following methods added by Sridhar.Vemulapalli on 12-Aug-2011
	private org.jpos.iso.channel.NACChannel createNewNACChannel(String messageHeader,ISOPackager isoPackager ) {
		logger.info("****************Created New Channel**********************");
		org.jpos.iso.channel.NACChannel channel = null;
	  try {

		 if (isoPackager != null) {
			 if (messageHeader != null && messageHeader.trim().length() > 0)
			    {
				 	if(serverConfigDTO!=null && MessageFormatConstant.LR_MESSAGE_FORMAT_PANAMA_ID==serverConfigDTO.getMessageFormat()){
				 		BaseHeader bh = new BaseHeader(messageHeader.getBytes());
				 		channel = new org.jpos.iso.channel.NACChannel(isoPackager,bh.pack());
				 	}else{
			    	channel = new org.jpos.iso.channel.NACChannel(isoPackager,ISOUtil.hex2byte (messageHeader));
			    }
			    }
			    else
			    {
			    	channel = new org.jpos.iso.channel.NACChannel(isoPackager,null);
			    }
			 }

	  }
	  catch (Exception exp)
	  {
			logger.error("Exception at start"+exp.getMessage());
	  }

		return channel;
	}
	
	private com.bcgi.paymgr.posserver.channel.NACChannel createOldNACChannel(String messageHeader,ISOPackager isoPackager ) {
		logger.info("****************Created Old NAC Channel **********************");
		com.bcgi.paymgr.posserver.channel.NACChannel channel = null;
	  try {

		 if (isoPackager != null) {
			 if (messageHeader != null && messageHeader.trim().length() > 0)
			    {
			    	channel = new com.bcgi.paymgr.posserver.channel.NACChannel(isoPackager,ISOUtil.hex2byte (messageHeader));
			    }
			    else
			    {
			    	channel = new com.bcgi.paymgr.posserver.channel.NACChannel(isoPackager,null);
			    }
			 }

	  }
	  catch (Exception exp)
	  {
			logger.error("Exception at start"+exp.getMessage());
	  }

		return channel;
	}
	//Ends
	private ISOPackager getISOPackager(String msgPackagerFormat,int messageFormat){
		ISOPackager isoPackager = null;
		
		switch (messageFormat) 
		{
				case MessageFormatConstant.BCGI_MESSAGE_FORMAT_ID:
				{
					if (msgPackagerFormat != null && msgPackagerFormat.trim().length() > 0)
					{
						if (msgPackagerFormat.equalsIgnoreCase(ServerConfigConstant.ASCII_PACKAGER_FORMAT)){
							isoPackager = new BCGIISO87APackager();
						}
						else if (msgPackagerFormat.equalsIgnoreCase(ServerConfigConstant.BINARY_PACKAGER_FORMAT)) {
							isoPackager = new BCGIISO87BPackager();
						}
						
					}
					break;
				} 
				case MessageFormatConstant.IRP_MESSAGE_FORMAT_ID:
				{
					if (msgPackagerFormat != null && msgPackagerFormat.trim().length() > 0)
					{
						if (msgPackagerFormat.equalsIgnoreCase(ServerConfigConstant.ASCII_PACKAGER_FORMAT)){
							isoPackager = new IRPISO87APackager();
						}
						else if (msgPackagerFormat.equalsIgnoreCase(ServerConfigConstant.BINARY_PACKAGER_FORMAT)) {
							isoPackager = new IRPISO87BPackager();
						}
						
					}
					break;
				} 
				case MessageFormatConstant.LR_MESSAGE_FORMAT_ID:
				{
					
					
					if (msgPackagerFormat != null && msgPackagerFormat.trim().length() > 0)
					{
					   if (msgPackagerFormat.equalsIgnoreCase(ServerConfigConstant.BINARY_PACKAGER_FORMAT)) {
						   
						   System.out.println(":::::::creating packager object:::::");
							isoPackager = new LRISO87BPackager();
						}
						
					}
					break;
				} 
                               case  MessageFormatConstant.LR_MESSAGE_FORMAT_COLUMBIA_ID:
                                {
                                         if (msgPackagerFormat != null && msgPackagerFormat.trim().length() > 0) {
		if (msgPackagerFormat.equalsIgnoreCase(ServerConfigConstant.ASCII_PACKAGER_FORMAT)){
			isoPackager = new LRC_ISO87APackager();
		}
                                         }
                                         break;
                                 }
                case  MessageFormatConstant.LR_MESSAGE_FORMAT_PANAMA_ID:
                {
                	if (msgPackagerFormat != null && msgPackagerFormat.trim().length() > 0) {
                		if (msgPackagerFormat.equalsIgnoreCase(ServerConfigConstant.ASCII_PACKAGER_FORMAT)){
                			isoPackager = new LRP_ISO87APackager();
                		}
                	}
                	break;
                }                
		}
		return isoPackager;
	}
}
