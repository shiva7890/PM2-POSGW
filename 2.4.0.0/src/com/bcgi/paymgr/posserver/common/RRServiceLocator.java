package com.bcgi.paymgr.posserver.common;

import javax.naming.*;
import javax.rmi.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import javax.ejb.*;
import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;



import java.lang.reflect.Method;

public class RRServiceLocator {

	private InitialContext initialContext = null;
	private static Map cache = null;
	private static RRServiceLocator servlocatorInstance = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(ServiceLocator.class);
	private static Properties env = null;

	private static ArrayList serverlist = null;
	private static int currentServerIndex = 0;
	private static Map servercache = null;
	private static Map serverStatus = null;
	private static Map beanHomeMapCache = null;

    private RRServiceLocator() throws ServiceLocatorException
    {
	        try
	        {
	        	  /*
	    		  Properties env = new Properties();
	    		  env.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
	    		  env.put("java.naming.provider.url","jnp://"+MACHINE_NAME+":1099");
	    	      env.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
	    	      env.put("jnp.disableDiscovery", "true");
	    	      */
	        	System.out.println("servercache:"+servercache);
	              if(servercache == null)
	              {
	            	serverStatus = new HashMap();
	            	servercache = Collections.synchronizedMap(new HashMap());
	            	serverlist = readServerConfigFile();

					env   = readPMServerAccessConfigFile();

	              	cache = Collections.synchronizedMap(new HashMap());
				  }
	        }
	        catch (NamingException ne)
	        {
	            throw new ServiceLocatorException(ne);
	        }
	        catch (Exception e) {
	            throw new ServiceLocatorException(e);
	        }
	    }

    public static int getNumberOfServers(){
    	int num = 0;
    	if (serverlist != null){
    	   num = serverlist.size();
    	}
    	return num;
    }

    private ArrayList readServerConfigFile() throws Exception
	 {
    	ArrayList serlist = new ArrayList();
		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.SERVER_LIST_CONFIG_FILE_NAME;

		System.out.println("PM Server Access Config File Path: "+configfilePath);

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
						System.out.println("SerName from Properties :" + serName);

						System.out.println("SerName status from Properties :" + serStatus);

						serlist.add(serName);
						serverStatus.put(serName,serStatus);

						beanHomeMapCache = Collections.synchronizedMap(new HashMap());
						servercache.put(serName,beanHomeMapCache);

				}
		        }


	    }
	    catch (Exception ex){
			logger.error("readServerConfigFile() - Exception occurred while reading Server Config File. "+ ex.getMessage());
			throw ex;
	    }
		return serlist;

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
	    	//String naming_provider_url = properties.getProperty(ServerConfigConstant.NAMING_PROVIDER_URL);
	    	String naming_factory_url_pkgs = properties.getProperty(ServerConfigConstant.NAMING_FACTORY_URL_PKG);
	    	String jnp_disableDiscovery = properties.getProperty(ServerConfigConstant.JNP_DISABLE_DISCOVERY);

	    	String jnp_partitionName  = properties.getProperty("jnp.partitionName");
	    	String jnp_discoveryGroup = properties.getProperty("jnp.discoveryGroup");
	    	String jnp_discoveryPort  = properties.getProperty("jnp.discoveryPort");

			logger.info("jnp_disableDiscovery======>"+jnp_disableDiscovery);
			logger.info("jnp_discoveryGroup======>"+jnp_discoveryGroup);
			logger.info("jnp_discoveryPort======>"+jnp_discoveryPort);

	    	boolean isExists = true;

	    	if (!((naming_factory_initial != null) && (naming_factory_initial.trim().length() > 0))){
				logger.error("Naming Factory Initial Property is not set in PM Server Access Configuration file.");
				isExists = false;
			}

	    	/*
	    	if (!((naming_provider_url != null) && (naming_provider_url.trim().length() > 0))){
				logger.error("Naming Provider URL Property is not set in PM Server Access Configuration file.");
				isExists = false;
			}
			*/
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
    public synchronized static RRServiceLocator getInstance() throws ServiceLocatorException {
    	try
    	{
		    if (servlocatorInstance == null) {
		    	servlocatorInstance = new RRServiceLocator();
		    }
    	}
    	catch(ServiceLocatorException ex){
    		throw new ServiceLocatorException(ex);
    	}
	    return servlocatorInstance;
	  }

	/**
	 * Lookup a remote home given the JNDI name for the remote home
	 * @param jndiHomeName
	 * @param homeClass
	 * @return
	 * @throws ServiceLocatorException
	 */
	public EJBHome getRemoteHome(String jndiHomeName, Class homeClass,String ipAddress)throws ServiceLocatorException
	{
	        EJBHome remoteHome = null;


	        try
	        {
	            if (cache.containsKey(jndiHomeName))
	            {
	                remoteHome = (EJBHome) cache.get(jndiHomeName);
	                logger.info("Getting from cache");
	            }
	            else
	            {

					logger.info("looking up...."+jndiHomeName);

	                env.put(ServerConfigConstant.NAMING_PROVIDER_URL,ipAddress);

	                initialContext = new InitialContext(env);
	                Object objref  =  initialContext.lookup(jndiHomeName);

	                logger.info("objref..."+objref);

	                Object obj = PortableRemoteObject.narrow(objref, homeClass);

	                logger.info("obj..."+obj);

	                remoteHome = (EJBHome)obj;

	                logger.info("remoteHome..."+remoteHome);

	                cache.put(jndiHomeName, remoteHome);

	                logger.info("cache..size.."+cache.size());
	            }
	        }
	        catch (NamingException nex)
	        {
	            logger.error("Lookup failed on machine:"+ipAddress+" for Object:"+jndiHomeName);
	        }
	        catch (Exception ex)
	        {
	            logger.error("Exception occured in getRemoteHome() method while trying to lookup on machine:"+ipAddress+" for Object:"+jndiHomeName);
	        }

	        finally
	        {
				try
				{
					if(initialContext != null)
						initialContext.close();
				}
				catch (NamingException nex)
				{
					logger.error("Exception occured while trying to close Context. Machine:"+ipAddress+" for Object:"+jndiHomeName);
				}
			}
	        return remoteHome;
	    }


	 /**
	  *  Gets the EJBHome for the given service using the
	  *  JNDI name and the Class for the EJBHome
	  */
     public EJBHome getHome( int service,String ipAddress) throws ServiceLocatorException {

       EJBHome home = null;

       try
       {
    	  	 Object obj  = getRemoteHome(getServiceName(service),getServiceClass(service),ipAddress) ;
             home = (EJBHome)obj;
       }
       catch( Exception ex )
       {
           logger.error("Exception occured in getHome() method for Node:"+ipAddress);
           //throw new ServiceLocatorException(ex.getMessage());
       }
       return home;
     }




	 /**
	  * Returns the Class for the required service
	  * @param service
	  * @return
	  */

	 private  static Class getServiceClass(int serviceID)
	 {
		 Class serviceClass = null;

	      switch(serviceID)
	      {

	        case Services.PARTNER_EJB_ID: {
	        	serviceClass = Services.PARTNER_EJB_CLASS;
	            break;
	        }
	        case Services.RECHARGE_FACADE_EJB_ID: {
	        	serviceClass = Services.RECHARGE_FACADE_EJB_CLASS;
		        break;
	        }
	        case Services.STATUS_ENQUIRY_EJB_ID: {
	        	serviceClass = Services.STATUS_ENQUIRY_EJB_CLASS;
	            break;
	        }
	        case Services.REVERSAL_FACADE_EJB_ID: {
	        	serviceClass = Services.REVERSAL_FACADE_EJB_CLASS;
		        break;
	        }

	        case Services.DM_TRANS_FACADE_EJB_ID: {
	        	serviceClass = Services.DM_TRANS_FACADE_EJB_CLASS;
		        break;
	        }

	        case Services.DM_ASYNC_TRANS_FACADE_EJB_ID: {
	        	serviceClass = Services.DM_ASYNC_TRANS_FACADE_EJB_CLASS;
		        break;
	        }

	        case Services.ASYNC_RECHARGE_FACADE_EJB_ID: {
		      	  System.out.println("RAM IN SWITCHCASE1111111");
			        	serviceClass = Services.ASYNC_RECHARGE_FACADE_EJB_CLASS;
			            break;
			     }
	        case Services.ASYNC_REVERSAL_FACADE_EJB_ID: {
		        	serviceClass = Services.ASYNC_REVERSAL_FACADE_EJB_CLASS;
		            break;
		     }
	      }

	      return serviceClass;
	  }


	 /**
	  * Returns the JNDI name for the required service
	  * @param service
	  * @return
	  */

	  private  static String getServiceName(int serviceID)
	  {
		  String serviceName = null;
	      switch(serviceID)
	      {

	        case Services.PARTNER_EJB_ID: {
	        	serviceName = Services.PARTNER_EJB_NAME;
	            break;
	        }
	        case Services.RECHARGE_FACADE_EJB_ID: {
	        	serviceName = Services.RECHARGE_FACADE_EJB_NAME;
		        break;
	        }
	        case Services.STATUS_ENQUIRY_EJB_ID: {
	        	serviceName = Services.STATUS_ENQUIRY_EJB_NAME;
	            break;
	        }
	        case Services.REVERSAL_FACADE_EJB_ID: {
	        	serviceName = Services.REVERSAL_FACADE_EJB_NAME;
		        break;
	        }
	        case Services.DM_TRANS_FACADE_EJB_ID: {
	        	serviceName = Services.DM_TRANS_FACADE_EJB_NAME;
		        break;
	        }
	        case Services.DM_ASYNC_TRANS_FACADE_EJB_ID: {
	        	serviceName = Services.DM_ASYNC_TRANS_FACADE_EJB_NAME;
		        break;
	        }
	        case Services.ASYNC_RECHARGE_FACADE_EJB_ID: {
				     	serviceName = Services.ASYNC_RECHARGE_FACADE_EJB_NAME;
					     break;
				  }
	        case Services.ASYNC_REVERSAL_FACADE_EJB_ID: {
			     	serviceName = Services.ASYNC_REVERSAL_FACADE_EJB_NAME;
				     break;
			  }
	      }
	      return serviceName;
	 }






	 public EJBObject getRemote(int serviceId)
	 {
		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
		                        ServerConfigConstant.CONFIG_FILE_NAME;
		logger.info("CM Server Access Config File Path: "+configfilePath);

		Properties properties    = new Properties();
		int 	  index 		 = 0;
		boolean   remoteLookUp   = true;
		String    ipAddress      = "";
		EJBObject remoteObj      = null;
		String 	  jndiHomeName   = "";

		try
		{





			properties.load(new FileInputStream(configfilePath));

			String nodes  = properties.getProperty(ServerConfigConstant.NO_OF_NODES);

			int noOfNodes = Integer.parseInt(nodes);

			logger.info("noOfNodes====>"+noOfNodes);

			while(noOfNodes-- > 0)
			{
				remoteLookUp = true;
				++index;

				try
				{
					ipAddress = properties.getProperty(ServerConfigConstant.NODE + index);

					EJBHome homeObj = getHome(serviceId,ipAddress);

					Class  homeClass  = homeObj.getClass();
					Method homeMethod = homeClass.getMethod("create",(Class []) null);

					Object obj = homeMethod.invoke(homeObj,(Object [])null);

					remoteObj = (EJBObject)obj;

					logger.info("remoteObj===1111====>"+remoteObj);
				}
				catch(Exception e)
				{
				   logger.error("Could not find Remote Object on Machine:(PROVIDER URL)"+ipAddress);
				   remoteLookUp = false;
				}

				if(remoteLookUp)
				{
					break;
				}
				else
				{
					// Check whether the object exists in cache. if exists remove it and relookup to the same node again.
					if( cache.containsKey(jndiHomeName) )
					{
						cache.remove(jndiHomeName);

						++noOfNodes; // resetting the node counter to perform the same action for the same node again
						--index; // resetting the node to perform a lookup to the same node again.
					}
				}
			}


	}
	catch(Exception e)
	{
		logger.error("Exception occured in getRemote=====>"+e.getMessage());
	}
		return remoteObj;
   }

	 public EJBObject getRemoteUsingServerList(int serviceId)
	 {

		 System.out.println("getRemoteUsingServerList:"+serviceId);

			EJBObject remoteObj      = null;
			 try{

			EJBHome homeObj = getHomeUsingServerList(serviceId);


			Class  homeClass  = homeObj.getClass();
			Method homeMethod = homeClass.getMethod("create",(Class [])null);

			Object obj = homeMethod.invoke(homeObj,(Object [])null);

			remoteObj = (EJBObject)obj;

			logger.info("remoteObj=======>"+remoteObj);


			 }catch(Exception e)
			{
				logger.error("Exception occured in getRemote=====>"+e.getMessage());
			}


			return remoteObj;


	 }


	 /**
	  *  Gets the EJBHome for the given service using the
	  *  JNDI name and the Class for the EJBHome
	  */
     public EJBHome getHomeUsingServerList( int service) throws ServiceLocatorException {

    	 System.out.println("getHomeUsingServerList:"+service);

       EJBHome home = null;

       try
       {
    	  	 Object obj  = getRemoteHomeUsingServerList(getServiceName(service),getServiceClass(service)) ;
             home = (EJBHome)obj;
       }
       catch( Exception ex )
       {
           //logger.error("Exception occured in getHome() method for Node:"+ipAddress);
           throw new ServiceLocatorException(ex.getMessage());
       }
       return home;
     }


     private String getCurServerName (){

    	 String curSerName = null;
    	 String curSerStatus = null;
    	 int tryCount=0;


     	do{

     		curSerName = (String)serverlist.get(currentServerIndex);
     		curSerStatus =(String)serverStatus.get(curSerName);


     		if (curSerStatus.equalsIgnoreCase("INACTIVE")){
     			tryCount++;
     		}

			currentServerIndex++;
 			if (currentServerIndex == serverlist.size()){
 				currentServerIndex = 0;
 			}



    		if (tryCount == serverlist.size()){

    			curSerName=null;
    			break;

     		}


     	}while (curSerStatus.equalsIgnoreCase("INACTIVE") );


    	 return curSerName;



     }


 	/**
 	 * Lookup a remote home given the JNDI name for the remote home
 	 * @param jndiHomeName
 	 * @param homeClass
 	 * @return
 	 * @throws ServiceLocatorException
 	 */
 	public EJBHome getRemoteHomeUsingServerList(String jndiHomeName, Class homeClass)throws ServiceLocatorException
 	{

 		 System.out.println("getRemoteHomeUsingServerList");
 	        EJBHome remoteHome = null;
 	        String curSerName =null;





 	        try
 	        {
 	        	curSerName = getCurServerName();
 	        	System.out.println("Current Server:"+curSerName);

        		if (curSerName == null){
        			//ERROR - could not get active server name from the server list
        			return remoteHome;
        		}

           		System.out.println("ServerCache:"+servercache);


        		beanHomeMapCache = (Map)servercache.get(curSerName);
        		System.out.println("beanHomeMapCache:"+beanHomeMapCache);

	            if (beanHomeMapCache.containsKey(jndiHomeName))
 	            {
 	                remoteHome = (EJBHome) beanHomeMapCache.get(jndiHomeName);

 	                System.out.println("Getting remoteHome from Cache:"+remoteHome);
 	            }else{

 					logger.info("looking up...."+jndiHomeName);
 					System.out.println("looking up...."+jndiHomeName);

 					System.out.println("url:"+ServerConfigConstant.NAMING_PROVIDER_URL_STARTVAL+curSerName);


 	                env.put(ServerConfigConstant.NAMING_PROVIDER_URL,ServerConfigConstant.NAMING_PROVIDER_URL_STARTVAL+curSerName);

	                initialContext = new InitialContext(env);
 	                Object objref  =  initialContext.lookup(jndiHomeName);

 	                logger.info("objref..."+objref);
 	               System.out.println("objref..."+objref);

 	                Object obj = PortableRemoteObject.narrow(objref, homeClass);

 	                logger.info("obj..."+obj);
 	               System.out.println("obj..."+obj);

 	                remoteHome = (EJBHome)obj;

 	                logger.info("remoteHome..."+remoteHome);
 	               System.out.println("remoteHome..."+remoteHome);

 	               beanHomeMapCache.put(jndiHomeName, remoteHome);

 	               logger.info("cache..size.."+beanHomeMapCache.size());
 	              System.out.println("cache..size.."+beanHomeMapCache.size());
 	               servercache.put(curSerName,beanHomeMapCache);

 	            }






 	        	/*
 	            if (cache.containsKey(jndiHomeName))
 	            {
 	                remoteHome = (EJBHome) cache.get(jndiHomeName);
 	                logger.info("Getting from cache");
 	            }
 	            else
 	            {

 					logger.info("looking up...."+jndiHomeName);

 	                env.put(ServerConfigConstant.NAMING_PROVIDER_URL,ipAddress);

 	                initialContext = new InitialContext(env);
 	                Object objref  =  initialContext.lookup(jndiHomeName);

 	                logger.info("objref..."+objref);

 	                Object obj = PortableRemoteObject.narrow(objref, homeClass);

 	                logger.info("obj..."+obj);

 	                remoteHome = (EJBHome)obj;

 	                logger.info("remoteHome..."+remoteHome);

 	                cache.put(jndiHomeName, remoteHome);

 	                logger.info("cache..size.."+cache.size());
 	            }*/
 	        }catch(javax.naming.CommunicationException comx){

 	        	System.out.println("CommunicationException:"+comx.getMessage());

 	        	serverStatus.put(curSerName,"INACTIVE");
 	        	updatePropertiesStatus(curSerName);

 	        	remoteHome = getRemoteHomeUsingServerList(jndiHomeName, homeClass);
 	        	System.out.println("remoteHome:"+remoteHome);

 	        	//Change the server status Map to INACTIVE
 	        	//Change/update the properties file to INACTIVE
 	        	//Increment the current Index
 	        	//retry the whole process.



 	        }
 	        catch (NamingException nex)
 	        {
 	        	nex.printStackTrace();
 	            logger.error("Lookup failed on machine:"+curSerName+" for Object:"+jndiHomeName);
 	        }
 	        catch (Exception ex)
 	        {
 	        	ex.printStackTrace();
 	            logger.error("Exception occured in getRemoteHome() method while trying to lookup on machine:"+curSerName+" for Object:"+jndiHomeName);
 	        }

 	        finally
 	        {
 				try
 				{
 					if(initialContext != null)
 						initialContext.close();
 				}
 				catch (NamingException nex)
 				{
 					logger.error("Exception occured while trying to close Context. Machine:"+curSerName+" for Object:"+jndiHomeName);
 				}
 			}
 	        return remoteHome;
 	    }



 	private void updatePropertiesStatus(String serverName){

		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
		ServerConfigConstant.SERVER_LIST_CONFIG_FILE_NAME;

		System.out.println("PM Server Access Config File Path: "+configfilePath);

		Properties properties = new Properties();
	    try
	    {
	    	properties.load(new FileInputStream(configfilePath));

	        if (properties != null){

	        	String Key = serverName.replace(':',',');
	        	properties.setProperty(Key, "INACTIVE");

	        	 // Write properties file.
	            properties.store(new FileOutputStream(configfilePath), null);
	        }




	    }
	    catch (Exception ex){
			logger.error("updatePropertiesStatus() - Exception occurred while updating Server Config File. "+ ex.getMessage());
			ex.printStackTrace();
	    }



 	}


}
