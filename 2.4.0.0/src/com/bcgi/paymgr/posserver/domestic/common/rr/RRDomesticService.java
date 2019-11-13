/**
 * 
 */
package com.bcgi.paymgr.posserver.domestic.common.rr;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.EJBHome;
import javax.ejb.EJBObject;

import org.apache.log4j.Logger;

import com.bcgi.paymgr.posserver.common.ServiceLocatorException;
import com.bcgi.paymgr.posserver.common.Services;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.rrobin.dto.EJBRequestDTO;
import com.bcgi.rrobin.proxy.EJBProxy;
import com.bcgi.rrobin.proxy.ProxyInterface;
import com.bcgi.rrobin.wrapper.RRResponseWrapper;

/**
 * @author venugopalp
 *
 */
public class RRDomesticService {

	private static Logger logger	= Logger.getLogger(RRDomesticService.class);

	private String readServerConfigFile() throws Exception
	{
		StringBuffer serString = new StringBuffer();
		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.SERVER_LIST_CONFIG_FILE_NAME;

		logger.info("PM Server Access Config File Path: "+configfilePath);

		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(configfilePath));
			if (properties != null){

				Set servernameSet = properties.keySet();

				Iterator serNameSetIter = servernameSet.iterator();

				while (serNameSetIter.hasNext()) {

					String serName = (String) (serNameSetIter.next());
					String serStatus = (String)(properties.get(serName));
					serName = serName.replace(',',':');
					logger.info("SerName from Properties :" + serName);
					if(serStatus.equalsIgnoreCase("ACTIVE"))
						serString.append(serName+",");
				}	
				logger.info("SerNames from Properties :" + serString);
			}


		}
		catch (Exception ex){
			logger.error("readServerConfigFile() - Exception occurred while reading Server Config File. "+ ex.getMessage());
			throw ex;
		}
		return serString.toString();	

	}
	private Properties readPMServerAccessConfigFile() throws Exception
	{

		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
		ServerConfigConstant.PMSERVER_ACCESS_CONFIG_FILE_NAME;
		logger.info("PM Server Access Config File Path: "+configfilePath);

		System.out.println("PM Server Access Config File Path: "+configfilePath);

		Properties properties = new Properties();

		try
		{
			properties.load(new FileInputStream(configfilePath));

			String naming_factory_initial = properties.getProperty(ServerConfigConstant.NAMING_FACTORY_INITIAL);
			String naming_factory_url_pkgs = properties.getProperty(ServerConfigConstant.NAMING_FACTORY_URL_PKG);
			String jnp_disableDiscovery = properties.getProperty(ServerConfigConstant.JNP_DISABLE_DISCOVERY);


//			logger.info("naming_provider_url======>"+naming_provider_url);

			boolean isExists = true;

			if (!((naming_factory_initial != null) && (naming_factory_initial.trim().length() > 0))){
				logger.error("Naming Factory Initial Property is not set in PM Server Access Configuration file.");
				isExists = false;
			}

			/*if (!((naming_provider_url != null) && (naming_provider_url.trim().length() > 0))){
				logger.error("Naming Provider URL Property is not set in PM Server Access Configuration file.");
				isExists = false;
			}
			else
				properties.put(ServerConfigConstant.NAMING_PROVIDER_URL, naming_provider_url);*/

			if (!((naming_factory_url_pkgs != null) && (naming_factory_url_pkgs.trim().length() > 0))){
				logger.error("Naming Factory URL Package Property is not set in PM Server Access Configuration file.");
				isExists = false;
			}

			if (!((jnp_disableDiscovery != null) && (jnp_disableDiscovery.trim().length() > 0))){
				logger.error("JNP Disable Discovery Property is not set in PM Server Access Configuration file.");
				isExists = false;
			}

			if (!isExists){
				throw new ServiceLocatorException("Invalid PM Server Access Config File");
			}
		}
		catch (Exception ex){
			logger.error("readPMServerAccessConfigFile() - Exception occurred while reading PM Server Access Config File. "+ ex.getMessage());
			throw ex;
		}
		return properties;
	}

//new 
	/*public EJBObject getRemoteObject(String requestId, String finRefNum)
	{	 
		logger.info(" RRDomesticService : getRemoteObject() : Starting ");
		logger.info(" RRDomesticService : getRemoteObject() : Financial Reference Number ====> "+finRefNum);
		EJBObject remoteObj      = null;
		ProxyInterface ejbProxy 		 =null;
		EJBHome homeObj = null;
		Class  homeClass = null;
		Method homeMethod = null;
		Object obj = null;
		RRResponseWrapper responseWrapper=null;
		int failOverAttempts = getPropertyValueToConnectPM(ServerConfigConstant.NO_OF_UNSUCCESSFUL_FAILOVER_ATTEMPTS);
		int retryAttempts = getPropertyValueToConnectPM(ServerConfigConstant.UNSUCCESSFUL_CONNECTION_RETRY_ATTEMPTS_TO_PM);
		try{
			// Read from properties
			ejbProxy =  EJBProxy.getInstance();
			responseWrapper = new RRResponseWrapper(); 
			//int failOverAttempts = getPropertyValueToConnectPM(ServerConfigConstant.NO_OF_UNSUCCESSFUL_FAILOVER_ATTEMPTS);
			//int retryAttempts = getPropertyValueToConnectPM(ServerConfigConstant.UNSUCCESSFUL_CONNECTION_RETRY_ATTEMPTS_TO_PM);
			logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() - Failover Attempts ===> "+failOverAttempts+" Retry Attempts ===>"+retryAttempts);
			responseWrapper = (RRResponseWrapper)ejbProxy.chooseTarget(requestId, failOverAttempts, retryAttempts, finRefNum);

			if (responseWrapper != null) {
				if (responseWrapper.getEjbRequestDTO() != null) {
					logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() ===> EJB Remote Home IP Address ===> "+responseWrapper.getEjbRequestDTO().getIpAddress());
					logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() ===> EJB Remote Home Port No ===> "+responseWrapper.getEjbRequestDTO().getPortNo());
				}
				homeObj = (EJBHome)responseWrapper.getRemoteObject();
				logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Home Object======>"+homeObj);
				if (homeObj == null) {
					return remoteObj;
				}else {
			homeClass  = homeObj.getClass();
			homeMethod = homeClass.getMethod("create",null);
			obj = homeMethod.invoke(homeObj,null);
					remoteObj = (EJBObject)obj;

				}
			}

			logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Object=======>"+remoteObj);

		}catch(Exception e)
		{
			logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() Exception ===> Stale Connection Retry Mechanism Calling ");
			logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() Exception occured in getRemote=====>"+e.getMessage());
			
			try{
			homeObj = (EJBHome)ejbProxy.callEJBProxyRemote(getPropertyValueToConnectPM("noOfIterations"), finRefNum, true);

				//To be Needed, When requirement is coming in future.
			   responseWrapper = new RRResponseWrapper(); 
				 responseWrapper = (RRResponseWrapper)ejbProxy.chooseTarget(requestId, failOverAttempts, retryAttempts, finRefNum);
				 if (responseWrapper != null) {
				 
					homeObj = (EJBHome)responseWrapper.getRemoteObject();
					logger.info("RRDomesticService.getRemoteObject() EJB Remote Home Object======>"+homeObj);
					if (homeObj == null) {
						return remoteObj;
					}else {
			homeClass  = homeObj.getClass();
			homeMethod = homeClass.getMethod("create",null);
						obj = homeMethod.invoke(homeObj,null);
						remoteObj = (EJBObject)obj;

					}
				}

				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Home Object Exception======>"+homeObj);

				homeClass  = homeObj.getClass();
				homeMethod = homeClass.getMethod("create",null);
			obj = homeMethod.invoke(homeObj,null);
			remoteObj = (EJBObject)obj;

				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Object Exception=======>"+remoteObj);
			}catch(Exception ex) {
				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() Exception occured in getRemoteException=====>"+e.getMessage());
			ejbProxy.clearCache();
		}
		}

		return remoteObj;
	}*/
	//perforce copy 2.2.5.0
	/*public EJBObject getRemoteObject(String requestId, String finRefNum)
	{	 
		logger.info(" RRDomesticService : getRemoteObject() : Starting ");
		logger.info(" RRDomesticService : getRemoteObject() : Financial Reference Number ====> "+finRefNum);
		EJBObject remoteObj      = null;
		ProxyInterface ejbProxy 		 =null;
		EJBHome homeObj = null;
		Class  homeClass = null;
		Method homeMethod = null;
		Object obj = null;
		RRResponseWrapper responseWrapper=null;

		try{
			// Read from properties
			ejbProxy =  EJBProxy.getInstance();
			responseWrapper = new RRResponseWrapper(); 
			int failOverAttempts = getPropertyValueToConnectPM(ServerConfigConstant.NO_OF_UNSUCCESSFUL_FAILOVER_ATTEMPTS);
			int retryAttempts = getPropertyValueToConnectPM(ServerConfigConstant.UNSUCCESSFUL_CONNECTION_RETRY_ATTEMPTS_TO_PM);
			logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() - Failover Attempts ===> "+failOverAttempts+" Retry Attempts ===>"+retryAttempts);
			responseWrapper = (RRResponseWrapper)ejbProxy.chooseTarget(requestId, failOverAttempts, retryAttempts, finRefNum);

			if (responseWrapper != null) {
				if (responseWrapper.getEjbRequestDTO() != null) {
					logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() ===> EJB Remote Home IP Address ===> "+responseWrapper.getEjbRequestDTO().getIpAddress());
					logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() ===> EJB Remote Home Port No ===> "+responseWrapper.getEjbRequestDTO().getPortNo());
				}
				homeObj = (EJBHome)responseWrapper.getRemoteObject();
				logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Home Object======>"+homeObj);
				if (homeObj == null) {
					return remoteObj;
				}else {
			homeClass  = homeObj.getClass();
			homeMethod = homeClass.getMethod("create",null);
			obj = homeMethod.invoke(homeObj,null);
					remoteObj = (EJBObject)obj;

				}
			}

			logger.info(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Object=======>"+remoteObj);

		}catch(Exception e)
		{
			logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() Exception ===> Stale Connection Retry Mechanism Calling ");
			logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() Exception occured in getRemote=====>"+e.getMessage());
			
			try{
			homeObj = (EJBHome)ejbProxy.callEJBProxyRemote(getPropertyValueToConnectPM("noOfIterations"), finRefNum, true);

				//To be Needed, When requirement is coming in future.
			   responseWrapper = new RRResponseWrapper(); 
				 responseWrapper = (RRResponseWrapper)ejbProxy.chooseTarget(requestId, failOverAttempts, retryAttempts, finRefNum));
				 if (responseWrapper != null) {
				 
					homeObj = (EJBHome)responseWrapper.getRemoteObject();
					logger.info("RRDomesticService.getRemoteObject() EJB Remote Home Object======>"+homeObj);
					if (homeObj == null) {
						return remoteObj;
					}else {
			homeClass  = homeObj.getClass();
			homeMethod = homeClass.getMethod("create",null);
						obj = homeMethod.invoke(homeObj,null);
						remoteObj = (EJBObject)obj;

					}
				}

				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Home Object Exception======>"+homeObj);

				homeClass  = homeObj.getClass();
				homeMethod = homeClass.getMethod("create",null);
			obj = homeMethod.invoke(homeObj,null);
			remoteObj = (EJBObject)obj;

				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Object Exception=======>"+remoteObj);
			}catch(Exception ex) {
				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() Exception occured in getRemoteException=====>"+e.getMessage());
			ejbProxy.clearCache();
		}
		}

		return remoteObj;
				}*/

	
//old
	public EJBObject getRemoteObject(String requestId, String finRefNum)
	  {
	    logger.info(" RRDomesticService : getRemoteObject() : Starting ");
	    logger.info(" RRDomesticService : getRemoteObject() : Financial Reference Number ====> " + finRefNum);
	    EJBObject remoteObj = null;
	    ProxyInterface ejbProxy = null;
	    EJBHome homeObj = null;
	    Class homeClass = null;
	    Method homeMethod = null;
	    Object obj = null;
	    RRResponseWrapper responseWrapper = null;
	    try    {
	      ejbProxy = EJBProxy.getInstance();
	      responseWrapper = new RRResponseWrapper();
	      int failOverAttempts = getPropertyValueToConnectPM(ServerConfigConstant.NO_OF_UNSUCCESSFUL_FAILOVER_ATTEMPTS);
	      int retryAttempts = getPropertyValueToConnectPM(ServerConfigConstant.UNSUCCESSFUL_CONNECTION_RETRY_ATTEMPTS_TO_PM);
	      logger.info(finRefNum + "===> RRDomesticService.getRemoteObject() - Failover Attempts ===> " + failOverAttempts + " Retry Attempts ===>" + retryAttempts);
	      responseWrapper = (RRResponseWrapper)ejbProxy.chooseTarget(requestId, failOverAttempts, retryAttempts, finRefNum);

	      if (responseWrapper != null) {
	        if (responseWrapper.getEjbRequestDTO() != null) {
	          logger.info(finRefNum + "===> RRDomesticService.getRemoteObject() ===> EJB Remote Home IP Address ===> " + responseWrapper.getEjbRequestDTO().getIpAddress());
	          logger.info(finRefNum + "===> RRDomesticService.getRemoteObject() ===> EJB Remote Home Port No ===> " + responseWrapper.getEjbRequestDTO().getPortNo());
	        }
	        homeObj = (EJBHome)responseWrapper.getRemoteObject();
	        logger.info(finRefNum + "===> RRDomesticService.getRemoteObject() EJB Remote Home Object======>" + homeObj);
	        if (homeObj == null) {
	          return remoteObj;
	        }
	        homeClass = homeObj.getClass();
	        homeMethod = homeClass.getMethod("create", null);
	        obj = homeMethod.invoke(homeObj, null);
	        remoteObj = (EJBObject)obj;
	      }

	      logger.info(finRefNum + "===> RRDomesticService.getRemoteObject() EJB Remote Object=======>" + remoteObj);

	    }    catch (Exception e)
	    { 
	      logger.error(finRefNum + "===> RRDomesticService.getRemoteObject() Exception ===> Stale Connection Retry Mechanism Calling ");
	      logger.error(finRefNum + "===> RRDomesticService.getRemoteObject() Exception occured in getRemote=====>" + e.getMessage());
			
	      try      {
	        homeObj = (EJBHome)ejbProxy.callEJBProxyRemote(getPropertyValueToConnectPM("noOfIterations"), finRefNum, true);

				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Home Object Exception======>"+homeObj);

				homeClass  = homeObj.getClass();
				homeMethod = homeClass.getMethod("create",null);
			obj = homeMethod.invoke(homeObj,null);
			remoteObj = (EJBObject)obj;

				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() EJB Remote Object Exception=======>"+remoteObj);
			}catch(Exception ex) {
				logger.error(finRefNum+"===> RRDomesticService.getRemoteObject() Exception occured in getRemoteException=====>"+e.getMessage());
			ejbProxy.clearCache();
		}
		}

		return remoteObj;
	}


	
	public ArrayList getEJBProperties(boolean isSync)
	{
		ArrayList ejbPropsLst = new ArrayList();
		EJBRequestDTO  ejbRequestDto = null;
		Properties env = null;
		String tempUrls = "";
		try {
			String naming_provider_url = readServerConfigFile();
			StringTokenizer tokenizer = new StringTokenizer(naming_provider_url,",");
			while(tokenizer.hasMoreElements()){
				env = new Properties();
				ejbRequestDto = new EJBRequestDTO();
				env = readPMServerAccessConfigFile();
				tempUrls = (String)tokenizer.nextElement();
				ejbRequestDto.setIpAddress(tempUrls.substring(0,tempUrls.indexOf(":")));
				ejbRequestDto.setPortNo(Integer.parseInt(tempUrls.substring(tempUrls.indexOf(":")+1)));
				env.put(ServerConfigConstant.NAMING_PROVIDER_URL, ServerConfigConstant.NAMING_PROVIDER_URL_STARTVAL + ejbRequestDto.getIpAddress() + ":" + ejbRequestDto.getPortNo());
				if(isSync){
					ejbRequestDto.setHomeClass(Services.DM_TRANS_FACADE_EJB_NAME + "." +Services.DM_TRANS_FACADE_EJB_HOME);
					ejbRequestDto.setServiceName(Services.DM_TRANS_FACADE_EJB_NAME);
				}
				else {
					ejbRequestDto.setHomeClass(Services.DM_ASYNC_TRANS_FACADE_EJB_NAME + "." +Services.DM_ASYNC_TRANS_FACADE_EJB_HOME);
					ejbRequestDto.setServiceName(Services.DM_ASYNC_TRANS_FACADE_EJB_NAME);
				}

				ejbRequestDto.setProperties(env);
				ejbPropsLst.add(ejbRequestDto);
			}
		} catch (Exception e) {
			logger.error("getEJBProperties() - Exception occurred while preparing the EJB Request DTO "+ e.getMessage());
		}

		return ejbPropsLst;
	}
	
	public ArrayList getLRCProperties()
	{
		ArrayList ejbPropsLst = new ArrayList();
		EJBRequestDTO  ejbRequestDto = null;
		Properties env = null;
		String tempUrls = "";
		try {
			String naming_provider_url = readServerConfigFile();
			StringTokenizer tokenizer = new StringTokenizer(naming_provider_url,",");
			while(tokenizer.hasMoreElements()){
				env = new Properties();
				ejbRequestDto = new EJBRequestDTO();
				env = readPMServerAccessConfigFile();
				tempUrls = (String)tokenizer.nextElement();
				ejbRequestDto.setIpAddress(tempUrls.substring(0,tempUrls.indexOf(":")));
				ejbRequestDto.setPortNo(Integer.parseInt(tempUrls.substring(tempUrls.indexOf(":")+1)));
				env.put(ServerConfigConstant.NAMING_PROVIDER_URL, ServerConfigConstant.NAMING_PROVIDER_URL_STARTVAL + ejbRequestDto.getIpAddress() + ":" + ejbRequestDto.getPortNo());
				ejbRequestDto.setHomeClass(Services.LRC_SYNC_FACADE_EJB_NAME + "." +Services.LRC_SYNC_FACADE_EJB_HOME);
				ejbRequestDto.setServiceName(Services.LRC_SYNC_FACADE_EJB_NAME);
				ejbRequestDto.setProperties(env);
				ejbPropsLst.add(ejbRequestDto);
			}
		} catch (Exception e) {
			logger.error("getEJBProperties() - Exception occurred while preparing the EJB Request DTO "+ e.getMessage());
		}

		return ejbPropsLst;
	}

	public int getPropertyValueToConnectPM(String property)
	{
		int propValue = 0;
		Properties properties    = new Properties();
		try{
			String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
									ServerConfigConstant.CONFIG_FILE_NAME;
			properties.load(new FileInputStream(configfilePath));
			if("wakeup".equalsIgnoreCase(property)) {
			String wakeupInterval  = properties.getProperty(ServerConfigConstant.WAKEUP_TIME_TO_CONNECT_PM);
			if(wakeupInterval != null && wakeupInterval.length() > 0)
				propValue = Integer.parseInt(wakeupInterval.trim());
			} else if("noOfIterations".equalsIgnoreCase(property)) {
				String noOfIterations  = properties.getProperty(ServerConfigConstant.NO_OF_ITERATIONS_TO_CONNECT_PM);
				if(noOfIterations != null && noOfIterations.length() > 0)
					propValue = Integer.parseInt(noOfIterations.trim());
			} else if("CONNECTION_TIMEOUT_TO_PM".equalsIgnoreCase(property)) {
				String timeOut  = properties.getProperty(ServerConfigConstant.CONNECTION_TIMEOUT_TO_PM);
				if(timeOut != null && timeOut.length() > 0)
					propValue = Integer.parseInt(timeOut.trim());
			} else if("CONNECTION_READ_TIMEOUT_TO_PM".equalsIgnoreCase(property)) {
				String readTimeOut  = properties.getProperty(ServerConfigConstant.CONNECTION_READ_TIMEOUT_TO_PM);
				if(readTimeOut != null && readTimeOut.length() > 0)
					propValue = Integer.parseInt(readTimeOut.trim());
			} else if("NO_OF_UNSUCCESSFUL_FAILOVER_ATTEMPTS".equalsIgnoreCase(property)) {
				String failOverAttempts  = properties.getProperty(ServerConfigConstant.NO_OF_UNSUCCESSFUL_FAILOVER_ATTEMPTS);
				if(failOverAttempts != null && failOverAttempts.length() > 0)
					propValue = Integer.parseInt(failOverAttempts.trim());
			}else if("UNSUCCESSFUL_CONNECTION_RETRY_ATTEMPTS_TO_PM".equalsIgnoreCase(property)) {
				String retryAttempts  = properties.getProperty(ServerConfigConstant.UNSUCCESSFUL_CONNECTION_RETRY_ATTEMPTS_TO_PM);
				if(retryAttempts != null && retryAttempts.length() > 0)
					propValue = Integer.parseInt(retryAttempts.trim());
			}
			
		}
		catch(Exception e) {
			logger.error("getPropertyValueToConnectPM() - Exception occurred while reading the wakeup time from posservercfg.properties file "+ e.getMessage());
			return propValue;
		}
		return propValue;
	}

}
