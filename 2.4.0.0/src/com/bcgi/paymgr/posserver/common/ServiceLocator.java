package com.bcgi.paymgr.posserver.common;

import javax.naming.*;
import javax.rmi.*;

import java.io.FileInputStream;
import java.util.*;

import javax.ejb.*;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;

import java.lang.reflect.Method;

public class ServiceLocator {

	private InitialContext initialContext = null;
	private static Map cache = null;
	private static ServiceLocator servlocatorInstance = null;
	static org.apache.log4j.Logger logger	= org.apache.log4j.Logger.getLogger(ServiceLocator.class);
	private static Properties env = null;


    private ServiceLocator() throws ServiceLocatorException
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
	              if(cache == null)
	              {
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

    private Properties readPMServerAccessConfigFile() throws Exception
	 {

		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +
		                        ServerConfigConstant.PMSERVER_ACCESS_CONFIG_FILE_NAME;
		logger.info("PM Server Access Config File Path: "+configfilePath);

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
    public synchronized static ServiceLocator getInstance() throws ServiceLocatorException {
    	try
    	{
		    if (servlocatorInstance == null) {
		    	servlocatorInstance = new ServiceLocator();
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
			jndiHomeName = getServiceName(serviceId);

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
					Method homeMethod = homeClass.getMethod("create",(Class [])null);

					Object obj = homeMethod.invoke(homeObj, (Object [])null);

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

}
