package com.bcgi.paymgr.posserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RMISecurityManager;
import java.rmi.server.RMISocketFactory;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jpos.util.SimpleLogListener;
import org.jpos.util.SystemMonitor;

import com.bcgi.paymgr.posserver.common.RRServiceLocator;
import com.bcgi.paymgr.posserver.common.constant.MessageFormatConstant;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.common.dto.ServerConfigDTO;
import com.bcgi.paymgr.posserver.common.util.PMISOFieldValidator;
import com.bcgi.paymgr.posserver.common.util.PropertiesReader;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPMessageTypeIDConstant;




public class POSServerMgr {

	private static Logger logger = null;
	private ServerConfigDTO serverConfigDTO = null;
	public static Hashtable ht = null;
	public ClassLoader parentClassLoader = POSServerMgr.class.getClassLoader();
	
	private static com.bcgi.paymgr.posserver.common.util.ThreadPool isoRespProcessThreadPool = null;
	
	private static int killThreadValueInSeconds = 2;
	private static boolean isThreadFuncReq = false;
	
	public POSServerMgr() {

		if (System.getSecurityManager() == null)
		{
			 System.setSecurityManager(new RMISecurityManager());
	    }
	}
	
	public void setParentClassLoader(ClassLoader parentClassLoader) {
	       this.parentClassLoader = parentClassLoader;
	}
	
	public static void main(String[] args) {

		POSServerMgr serverMgr  = new POSServerMgr();
		serverMgr.start();

	}

	public void start()
	{
		boolean isValid = false;
		configLogger();

		if (isConfigFileExists())
		{
			serverConfigDTO = readConfigFile();
			initializeFields();
			isValid = isConfigFilePropertiesValid(serverConfigDTO);
		}

		logger.info("Server Config File - isValid:"+isValid);
		
		startSystemMonitor(serverConfigDTO.getSystemMonitorFrequency());
		
		setSocketTimeOut(serverConfigDTO);
		
		if(isValid)
		{
			isValid = isPMServerAccessConfigFileValid();

			if(isValid)
			{
				PMISOServer pmISOServer = new PMISOServer(serverConfigDTO);
				createAppStatusFile();
				checkAppStatusFile();
				backupLogFile();
				pmISOServer.start();
			}
			else
			{
			   logger.error ("PM Server Config File invalid !. POS server will not be started.");
			}
		}
	    else
	    {
	 	   logger.error ("Server Config File does not exist!. POS server will not be started.");
	    }
	}
	
	public void stop()
	{
	    	System.exit(0);
	}
	 
	private void startSystemMonitor(int freq)
	{
		try
		{
			String monitorlogFilePath = ServerConfigConstant.LOG_FILE_FOLDER_PATH +ServerConfigConstant.SYSTEM_MONITOR_LOG_FILE_NAME;
			logger.debug ("Monitor Log File Path: "+monitorlogFilePath);
	
			PrintStream sysMonOutputFileStream = new PrintStream (new FileOutputStream (monitorlogFilePath));
			org.jpos.util.Logger sysMonLogger = new org.jpos.util.Logger();
			sysMonLogger.addListener (new SimpleLogListener (sysMonOutputFileStream));
	
			new SystemMonitor(freq,sysMonLogger,"system-monitor");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void createAppStatusFile(){
		try {
	        File file = new File("appstatusfile.txt");
	        file.createNewFile();

	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	

	    try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("appstatusfile.txt"));
	        out.write("RUNNING");
	        out.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    
	}
	
	private void checkAppStatusFile()
	{
		int delay = 5000;   // delay for 5 sec.
 	    int period = 10000;  // repeat every 10 sec.
 	    Timer timer = new Timer();
 	    
 	    timer.scheduleAtFixedRate(new TimerTask() {
 	            public void run() {
 	              	
 	           	    Runnable runnable = new CheckAppStatusFileThread();
 	        	    Thread thread = new Thread(runnable);
 	        	    thread.start(); 
 	        	    
 	        	    
 	            }
 	        }, delay, period);
 	    
	}
	
	private void backupLogFile()
	{
		String logfileName = serverConfigDTO.getIsoMsgLogFileName();
		String logfilePath = ServerConfigConstant.LOG_FILE_FOLDER_PATH +serverConfigDTO.getIsoMsgLogFileName();
			    
	    File logfile = new File(logfilePath);
	    boolean exists = logfile.exists();
	    if (exists) {
	    	
	    	int index = logfileName.lastIndexOf(".");
	    	if (index == -1){
	    		index = logfileName.length();
	    	}
	    	String logNewfileName 	= logfileName.substring(0,index);
	    	
	    	Format formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	        String currentDateTimeStr = formatter.format(new Date());

	    	logNewfileName = logNewfileName+"_"+currentDateTimeStr+logfileName.substring(index,logfileName.length());
	    	logNewfileName = ServerConfigConstant.LOG_FILE_FOLDER_PATH  +logNewfileName;
	    	
	    	File newNameLogFile = new File(logNewfileName);
			   
		    boolean success = logfile.renameTo(newNameLogFile);
		    if (!success) {
		       	copy(logfile,newNameLogFile);
		    }
	    } 
	    
	}
	
	public static void setSocketTimeOut(ServerConfigDTO serverConfigDTO)
	{
		try
		{
			logger.info("Configuring RMI Socket Factory....for Handling Timeout values");
			
			final int readTimeout = Integer.parseInt(serverConfigDTO.getConnReadTimeOut());
			final int connectionTimeout = Integer.parseInt(serverConfigDTO.getConnTimeOut());
			
			logger.info("RMI read Timeout : "+readTimeout);
			logger.info("RMI Connection Timeout : "+connectionTimeout);
			RMISocketFactory.setSocketFactory(new RMISocketFactory() {

				public Socket createSocket(String host, int port) throws IOException {
					//log.debug("Calling IP address & Port number : "+ host+ " :: "+port);
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(host, port), connectionTimeout);
					socket.setSoTimeout(readTimeout);
					socket.setSoLinger(false, 0);
					return socket;
				}

				public ServerSocket createServerSocket(int port) throws IOException {
					return new ServerSocket(port);
				}
			});
		}
		catch(Exception exp) {
			logger.error("Exception occured while creating socket factory...."+exp);
		}
	}

	
	private void copy(File src, File dst)  {
		try
		{
	        InputStream in = new FileInputStream(src);
	        OutputStream out = new FileOutputStream(dst);
	    
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
		}
		catch(Exception ex){
			System.out.println("Exception occured at copying log file"+ex.getMessage());
		}
    }

	private boolean isPMServerAccessConfigFileValid()
	{
		boolean exists = false;
		boolean serverlistConfigFileExists = false;
        boolean serverAccessConfigFileExists = false;

        serverlistConfigFileExists = isPMServerAccessConfigFileExists();
        serverAccessConfigFileExists = isPMServerListConfigFileExists();

		if(serverAccessConfigFileExists)
		{
			if (serverlistConfigFileExists){

				try
				{
					//Initializing Service Locator
					RRServiceLocator.getInstance();
				}
				catch (Exception ex)
				{
					logger.error("Exception occurred while reading PM Server Access Config File. "+ ex.getMessage());
				}
				exists = true;
			}
		}

		return exists;
	}

	private boolean isPMServerAccessConfigFileExists()
	{
        boolean exists = false;

		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
                                ServerConfigConstant.PMSERVER_ACCESS_CONFIG_FILE_NAME;
        logger.info("PM Server Access Config File Path: "+configfilePath);

		File configFile = new File(configfilePath);

		exists = configFile.exists();

		if (!exists){
			logger.error("PM Server Access Config File does not exist !");
		}
		return exists;
	}

	private boolean isPMServerListConfigFileExists()
	{
        boolean exists = false;

		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
                                ServerConfigConstant.SERVER_LIST_CONFIG_FILE_NAME;
        logger.info("PM Server List Config File Path: "+configfilePath);

		File configFile = new File(configfilePath);

		exists = configFile.exists();

		if (!exists){
			logger.error("PM Server List Config File does not exist !");
		}
		return exists;
	}





	private void configLogger()
	{

	    String log4jfile = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
	    					   ServerConfigConstant.LOG_FILE_NAME;
	    System.out.println("Log4j File Path:"+log4jfile);

	    PropertyConfigurator.configure(log4jfile);
        logger	= Logger.getLogger(POSServerMgr.class);

	}

	private boolean isConfigFileExists()
	{

		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
                                ServerConfigConstant.CONFIG_FILE_NAME;
        logger.info("Config File Path: "+configfilePath);

		File configFile = new File(configfilePath);

		return configFile.exists();
	}

	public static Hashtable getPosTimeZone()
	{
		return ht;
	}

	public static void assignProcessToThreadPool(Runnable runnable)
	{
		isoRespProcessThreadPool.assign(runnable);
	}

	public static int getThreadValueInSeconds()
	{
		return killThreadValueInSeconds;
	}

	public static boolean isThreadFunctReq()
	{
		return isThreadFuncReq;
	}

	

	private ServerConfigDTO readConfigFile()
	{

		ServerConfigDTO configurationDTO = null;

	
	    try
	    {
	    	configurationDTO = new ServerConfigDTO();
	    	ht = new Hashtable();
	      
	        String portNumber = PropertiesReader.getValue(ServerConfigConstant.SERVER_PORT);

	        int port = 0;

	        if (portNumber != null && PMISOFieldValidator.isValueExists(portNumber) && PMISOFieldValidator.isNumericValue(portNumber) )
	        {
	         	port = Integer.parseInt(portNumber);
	        }

	        String isoMsgLogFileName 	= PropertiesReader.getValue(ServerConfigConstant.ISOMSG_LOG_FILENAME);
	        String messageHeader 		= PropertiesReader.getValue(ServerConfigConstant.ISO_MSG_HEADER);
	       // String msgPackagerFormat 	= PropertiesReader.getValue(ServerConfigConstant.ISO_MSG_PACKAGER_FORMAT);
	        String msgPackagerFormat 	= "";
	        String threadPoolMinSize 	= PropertiesReader.getValue(ServerConfigConstant.THREAD_POOL_MIN_SIZE);
	        String threadPoolMaxSize 	= PropertiesReader.getValue(ServerConfigConstant.THREAD_POOL_MAX_SIZE);
	        String workerThreadPoolMinSize 	= PropertiesReader.getValue(ServerConfigConstant.WORKER_THREAD_POOL_MIN_SIZE);
	        String workerThreadPoolMaxSize 	= PropertiesReader.getValue(ServerConfigConstant.WORKER_THREAD_POOL_MAX_SIZE);
	        String systemMonitorFreq 	= PropertiesReader.getValue(ServerConfigConstant.SYSTEM_MONITOR_FREQ);
		      
	        String posTimeZone			= PropertiesReader.getValue(ServerConfigConstant.POS_TIME_ZONE);
	        System.out.println("The configurable pos time zone is ----------------->1"+posTimeZone);
	        String instanceId         = PropertiesReader.getValue(ServerConfigConstant.POS_GW_INSTANCE_ID);
	        String instancePortNumber = portNumber; //PropertiesReader.getValue(ServerConfigConstant.SERVER_PORT);
	        //The Following code is added By Sridhar.Vemulapalli on 12-Aug-2011
	        String UseOldNACChannel = PropertiesReader.getValue(ServerConfigConstant.USE_OLD_NAC_CHANNEL);
	        //Ends	       
	        ServerConfigConstant.GW_INSTANCE_ID = instanceId;
	        ServerConfigConstant.GW_INSTANCE_PORT_NUMBER = instancePortNumber;
	        
	        configurationDTO.setConnTimeOut(PropertiesReader.getValue(ServerConfigConstant.CONNECTION_TIMEOUT_TO_PM));
	        configurationDTO.setConnReadTimeOut(PropertiesReader.getValue(ServerConfigConstant.CONNECTION_READ_TIMEOUT_TO_PM));
	        	        
	        logger.info("Instance Id: "+instanceId);
	        logger.info("Instance Port Number: "+instancePortNumber);
	        
	        logger.info("ram conn timeout: "+configurationDTO.getConnTimeOut());
	        logger.info("ram conn read timeout: "+configurationDTO.getConnReadTimeOut());	        
	        
	        if(posTimeZone!=null && posTimeZone.length()>0)
	        {
	        	ht.put(ServerConfigConstant.POS_TIME_ZONE,posTimeZone);
	        
	        }
	        else if(posTimeZone ==null || posTimeZone.equals("") || posTimeZone.trim().length()==0)
	        {
	        	ht.put(ServerConfigConstant.POS_TIME_ZONE,ServerConfigConstant.DEFAULT_TIME_ZONE);
	        }
	        
	        
	        String messageFormat        = PropertiesReader.getValue(ServerConfigConstant.ISO_MSG_FORMAT);

	        int poolMinSize = 15;

	        if (threadPoolMinSize != null && PMISOFieldValidator.isValueExists(threadPoolMinSize) && PMISOFieldValidator.isNumericValue(threadPoolMinSize) )
	        {
	         	poolMinSize  = Integer.parseInt(threadPoolMinSize);
	        }


	        int poolMaxSize = 30;

	        if (threadPoolMaxSize != null && PMISOFieldValidator.isValueExists(threadPoolMaxSize) && PMISOFieldValidator.isNumericValue(threadPoolMaxSize) )
	        {
	         	poolMaxSize  = Integer.parseInt(threadPoolMaxSize);
	        }
	        
	        
	        int workerThPoolMinSize = 15;

	        if (workerThreadPoolMinSize != null && PMISOFieldValidator.isValueExists(workerThreadPoolMinSize) && PMISOFieldValidator.isNumericValue(workerThreadPoolMinSize) )
	        {
	        	workerThPoolMinSize  = Integer.parseInt(workerThreadPoolMinSize);
	        }


	        int workerThPoolMaxSize = 30;

	        if (workerThreadPoolMaxSize != null && PMISOFieldValidator.isValueExists(workerThreadPoolMaxSize) && PMISOFieldValidator.isNumericValue(workerThreadPoolMaxSize) )
	        {
	        	workerThPoolMaxSize  = Integer.parseInt(workerThreadPoolMaxSize);
	        }

	        int messageFormatId = 0;

	        if (messageFormat != null && PMISOFieldValidator.isValueExists(messageFormat) )
	        {
	            if (messageFormat.equals(MessageFormatConstant.IRP_MESSAGE_FORMAT))
				{
					messageFormatId = MessageFormatConstant.IRP_MESSAGE_FORMAT_ID;
					msgPackagerFormat = ServerConfigConstant.ASCII_PACKAGER_FORMAT;
				}
				else if(messageFormat.equals(MessageFormatConstant.BCGI_MESSAGE_FORMAT))
				{
					messageFormatId = MessageFormatConstant.BCGI_MESSAGE_FORMAT_ID;
					msgPackagerFormat = ServerConfigConstant.ASCII_PACKAGER_FORMAT;
				}
				else if (messageFormat.equals(MessageFormatConstant.LR_MESSAGE_FORMAT))
	         	{
	         		messageFormatId = MessageFormatConstant.LR_MESSAGE_FORMAT_ID;
	         		msgPackagerFormat = ServerConfigConstant.BINARY_PACKAGER_FORMAT;
	         	}
				else if(messageFormat.equals(MessageFormatConstant.LR_MESSAGE_FORMAT_COLUMBIA)) {
		        	msgPackagerFormat = ServerConfigConstant.ASCII_PACKAGER_FORMAT;
		        	messageFormatId = MessageFormatConstant.LR_MESSAGE_FORMAT_COLUMBIA_ID;
				}
				else if(messageFormat.equals(MessageFormatConstant.LR_MESSAGE_FORMAT_PANAMA)) {
		        	msgPackagerFormat = ServerConfigConstant.ASCII_PACKAGER_FORMAT;
		        	messageFormatId = MessageFormatConstant.LR_MESSAGE_FORMAT_PANAMA_ID;
				}	            
	        }

	        int threadPoolSize = 1;
	        String threadPoolSizeStr = PropertiesReader.getValue("THREAD_POOL_SIZE");
	        
	        logger.info("threadPoolSizeStr=="+threadPoolSizeStr);
	        
	        
	        if (threadPoolSizeStr != null && PMISOFieldValidator.isValueExists(threadPoolSizeStr) && PMISOFieldValidator.isNumericValue(threadPoolSizeStr) )
	        {
	        	threadPoolSize = Integer.parseInt(threadPoolSizeStr);
	        }
	        
	        logger.info("threadPoolSize=="+threadPoolSize);
	        
	        String threadValInSeconds = PropertiesReader.getValue("THREAD_VALUE_SECONDS");
	        if (threadValInSeconds != null && PMISOFieldValidator.isValueExists(threadValInSeconds) && PMISOFieldValidator.isNumericValue(threadValInSeconds) )
	        {
	        	killThreadValueInSeconds = Integer.parseInt(threadValInSeconds);
	        }
	        
	        //logger.info("killThreadValueInSeconds=="+killThreadValueInSeconds);
	        
	        String threadFuncReq = PropertiesReader.getValue("THREAD_FUNCT_REQ");
	        
	        if(threadFuncReq!=null && threadFuncReq.trim().length()>0)
	        {	        
	        	isoRespProcessThreadPool = new com.bcgi.paymgr.posserver.common.util.ThreadPool(threadPoolSize);
	        	isThreadFuncReq = true;
	        }
	        
	        int sysMonitorFreq = 60*60*1000; //1 hour

	        if (systemMonitorFreq != null && PMISOFieldValidator.isValueExists(systemMonitorFreq) && PMISOFieldValidator.isNumericValue(systemMonitorFreq) )
	        {
	        	sysMonitorFreq  = Integer.parseInt(systemMonitorFreq);
	        }

	        configurationDTO.setPort(port);
	        configurationDTO.setIsoMsgLogFileName(isoMsgLogFileName);
	        configurationDTO.setISOMessageHeader(messageHeader);
	        configurationDTO.setMsgPackagerFormat(msgPackagerFormat);
	        configurationDTO.setThreadPoolMinSize(poolMinSize);
	        configurationDTO.setThreadPoolMaxSize(poolMaxSize);
	        configurationDTO.setMessageFormat(messageFormatId);
	        configurationDTO.setWorkerThreadPoolMinSize(workerThPoolMinSize);
	        configurationDTO.setWorkerThreadPoolMaxSize(workerThPoolMaxSize);
	        configurationDTO.setSystemMonitorFrequency(sysMonitorFreq);
	        //The following code is kept by Sridhar.Vemulapalli on 12-Aug-2011
	        configurationDTO.setUseOldNACChannel(UseOldNACChannel);
	        
	        
	        // 
	        configurationDTO.setAppThreadPoolSize(threadPoolSize);
	        
	        
	        //ends	        
	        logger.info("portNumber: "+portNumber);
	        logger.info("isoMsgLogFileName: "+isoMsgLogFileName);
	        logger.info("messageheader: "+messageHeader);
	        logger.info("messageFormat: "+messageFormat);
	        logger.info("msgPackagerFormat: "+msgPackagerFormat);
	        logger.info("threadPoolMinSize: "+threadPoolMinSize);
	        logger.info("threadPoolMaxSize: "+threadPoolMaxSize);
	        logger.info("workerThreadPoolMinSize: "+workerThPoolMinSize);
	        logger.info("Use Ole NAC Channel Value: "+UseOldNACChannel);	        
	        logger.info("workerThreadPoolMaxSize: "+workerThPoolMaxSize);
	        logger.info("systemMonitorFreq: "+sysMonitorFreq);
	        logger.info("Instance Id: "+sysMonitorFreq);
	        logger.info("Instance Port Number: "+sysMonitorFreq);
	        logger.info("Thread Pool Size: "+threadPoolSize);
	        logger.info("Thread waiting time: "+killThreadValueInSeconds);
	        logger.info("Thread func req: "+threadFuncReq);       

	    }
	    catch (Exception ex)
	    {
	    	logger.error("Exception at readConfigFile()"+ex.getMessage());
	    }

	    return configurationDTO;
	}

	private boolean isConfigFilePropertiesValid(ServerConfigDTO serverConfigDTO)
	{
		boolean isValid = true;

		if (serverConfigDTO != null)
		{
			int port = serverConfigDTO.getPort();

			String isoMsgLogFileName 	= serverConfigDTO.getIsoMsgLogFileName();
			int isoMsgFormatId 	    = serverConfigDTO.getMessageFormat();
			//String msgPackagerFormat 	= serverConfigDTO.getMsgPackagerFormat();
			int    threadPoolMinSize 	= serverConfigDTO.getThreadPoolMinSize();
			int    threadPoolMaxSize 	= serverConfigDTO.getThreadPoolMaxSize();
		    int    workerThPoolMinSize 	= serverConfigDTO.getWorkerThreadPoolMinSize();
			int    workerThPoolMaxSize 	= serverConfigDTO.getWorkerThreadPoolMaxSize();
			int    systemMonitorFrequency 	= serverConfigDTO.getSystemMonitorFrequency();
			//The following code is kept by Sridhar.Vemulapalli on 12-Aug-2011
			String useOldNACChannel = serverConfigDTO.getUseOldNACChannel();
			//Ends

			if (port == 0)
			{
				logger.error("Port number is not set in Server Configuration file.");
				System.out.println("Port number is not set in Server Configuration file.");
				isValid = false;
			}

			if (port < 0)
			{
				logger.error("Invalid Port number in Server Configuration file.");
				System.out.println("Invalid Port number in Server Configuration file.");
				isValid = false;
			}

			if (!((isoMsgLogFileName != null) && (isoMsgLogFileName.trim().length() > 0)))
			{
				logger.error("Iso Msg Log FileName is not set in Server Configuration file.");
				System.out.println("Iso Msg Log FileName is not set in Server Configuration file.");

				isValid = false;
			}

			if (isoMsgFormatId == 0)
			{
				logger.error("Message Format is not set in Server Configuration file.");
				System.out.println("Message Format is not set in Server Configuration file.");

				isValid = false;
			}
			if (isoMsgFormatId == -1)
			{
				logger.error("Invalid Message Format in Server Configuration file.");
				System.out.println("Invalid Message Format in Server Configuration file.");
				isValid = false;
			}

			/*if ((msgPackagerFormat != null) && (msgPackagerFormat.trim().length() > 0))
			{
				if (msgPackagerFormat.equalsIgnoreCase(ServerConfigConstant.ASCII_PACKAGER_FORMAT))
				{
		    	}
		    	else
		    	{
		    		logger.error("Unsupported Msg Packager Format is  set in Server Configuration file. Supported Msg Packager Format are ASCII and BINARY)");
		    		System.out.println("Unsupported Msg Packager Format is  set in Server Configuration file. Supported Msg Packager Format are ASCII and BINARY)");

		    		isValid = false;
		    	}
			}
			else
			{
				logger.error("Msg Packager Format is not set in Server Configuration file.");
				System.out.println("Msg Packager Format is not set in Server Configuration file.");

				isValid = false;
			}
*/
			if (threadPoolMinSize < 1 || threadPoolMaxSize < 1)
			{
				logger.error("Invalid Listener Thread Pool Minimum size in Server Configuration file. Listener Thread Pool Minimum and Maximum size should be >= 1.");
				System.out.println("Invalid Listener Thread Pool Minimum size in Server Configuration file. Listener Thread Pool Minimum and Maximum size should be >= 1.");

				isValid = false;
			}

			if (threadPoolMaxSize <  threadPoolMinSize)
			{
				logger.error("Invalid Listener Thread Pool Maximum size in Server Configuration file. Listener Thread Pool Maximum size should be greater than or equal to Listener Thread Pool Minimum size.");
				System.out.println("Invalid Listener Thread Pool Maximum size in Server Configuration file. Listener Thread Pool Maximum size should be greater than or equal to Listener Thread Pool Minimum size.");

				isValid = false;
			}
			
			if (workerThPoolMinSize < 1 || workerThPoolMaxSize < 1)
			{
				logger.error("Invalid Worker Thread Pool Minimum size in Server Configuration file. Worker Thread Pool Minimum and Maximum size should be >= 1.");
				System.out.println("Invalid Worker Thread Pool Minimum size in Server Configuration file. Worker Thread Pool Minimum and Maximum size should be >= 1.");

				isValid = false;
			}
			
			if (workerThPoolMaxSize <  workerThPoolMinSize)
			{
				logger.error("Invalid Worker Thread Pool Maximum size in Server Configuration file. Worker Thread Pool Maximum size should be greater than or equal to Worker Thread Pool Minimum size.");
				System.out.println("Invalid Worker Thread Pool Maximum size in Server Configuration file. Worker Thread Pool Maximum size should be greater than or equal to Worker Thread Pool Minimum size.");

				isValid = false;
			}

			if (workerThPoolMinSize <  threadPoolMinSize)
			{
				logger.error("Invalid Worker Thread Pool Minimum size in Server Configuration file. Worker Thread Pool Minimum size should be greater than or equal to Listener Thread Pool Minimum size.");
				System.out.println("Invalid Worker Thread Pool Minimum size in Server Configuration file. Worker Thread Pool Minimum size should be greater than or equal to Listener Thread Pool Minimum size.");

				isValid = false;
			}
			
			if (workerThPoolMaxSize <  threadPoolMaxSize)
			{
				logger.error("Invalid Worker Thread Pool Maximum size in Server Configuration file. Worker Thread Pool Maximum size should be greater than or equal to Listener Thread Pool Maximum size.");
				System.out.println("Invalid Worker Thread Pool Maximum size in Server Configuration file. Worker Thread Pool Maximum size should be greater than or equal to Listener Thread Pool Maximum size.");

				isValid = false;
			}
			
			/*
			int minFreq = (5 * 60 * 1000);
			if (systemMonitorFrequency <  minFreq)
			{
				logger.error("Invalid System Monitor Frequency in Server Configuration file. System Monitor Frequency should be greater than or equal to "+minFreq+" milliseconds (5 minutes).");
				System.out.println("Invalid System Monitor Frequency in Server Configuration file. System Monitor Frequency should be greater than or equal to "+minFreq+" milliseconds (5 minutes).");
						
				isValid = false;
			}
			*/
			//The Following code is added by Sridhar.Vemulapalli on 12-Aug-2011
			if (!((useOldNACChannel != null) && (useOldNACChannel.trim().length() > 0 && ("YES".equalsIgnoreCase(useOldNACChannel) || "NO".equalsIgnoreCase(useOldNACChannel)))))
			{
				logger.error("Use Old NAC Channel is not set in Server Configuration file.");
				System.out.println("Use Old NAC Channel is not set in Server Configuration file.");

				isValid = false;
			}			
			//Ends
		}

		return isValid;
	}
	
class CheckAppStatusFileThread implements Runnable {
	    
		private String status ="";

	    public void run() {
	    		    	
	    	 String str="";
	    	
	    	   try {
	    	        BufferedReader in = new BufferedReader(new FileReader("appstatusfile.txt"));
	    	       
	    	        while ((str = in.readLine()) != null) {
	    	        	
	    	        	status = str;
	    	        }
	    	        in.close();
	    	    } catch (IOException e) {
	    	    	
	    	    }
	    	
	    	    if (status.equals("STOP")){
	    	     stop();
	    	    }
	    	
	    }
	}
	private void initializeFields(){
	
		try{
			LRPMessageTypeIDConstant.SUBSCRIBER_NUMBER_START_POSITION  = PropertiesReader.getValue(ServerConfigConstant.PANAMA_SUBSCRIBER_START_POSITION);
			LRPMessageTypeIDConstant.SUBSCRIBER_NUMBER_END_POSITION  = PropertiesReader.getValue(ServerConfigConstant.PANAMA_SUBSCRIBER_END_POSITION);
			logger.info("Start  Position of Subscriber"+LRPMessageTypeIDConstant.SUBSCRIBER_NUMBER_START_POSITION );
			logger.info("End POsition of Subscriber"+LRPMessageTypeIDConstant.SUBSCRIBER_NUMBER_END_POSITION);
			
		}catch(Exception exception){
			logger.error("initializeFields() - Exception occurred while Intializing the values from posservercfg.properties file "+ exception.getMessage());
		}		
	}

}
