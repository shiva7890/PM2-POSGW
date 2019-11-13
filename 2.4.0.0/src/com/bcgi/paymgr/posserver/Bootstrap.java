package com.bcgi.paymgr.posserver;

import java.util.logging.Level;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import java.lang.reflect.Method;
import java.io.*;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;

public final class Bootstrap {

	private static final Logger  logger = Logger.getLogger(Bootstrap.class.getName());
	private static Bootstrap daemon = null;
	private Object appDaemon = null;
	public ClassLoader appLoader = ClassLoader.getSystemClassLoader();


	public Bootstrap() {

	}

	public static void main(String args[]) {

		if (daemon == null) {
			daemon = new Bootstrap();
			try {
				daemon.init();
			} catch (Throwable t) {
				t.printStackTrace();
				return;
			}
		}

		try {
			String command = "start";
			if (args.length > 0) {
				command = args[args.length - 1];
			}

			if (command.equals("start")) {
				daemon.start();
			} else if (command.equals("stop")) {
				daemon.stop();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}


	}

	public void start()	throws Exception {
		if( appDaemon==null ) init();

		Method method = appDaemon.getClass().getMethod("start",(Class []) null);
		method.invoke(appDaemon, (Object [])null);

	}

	public void stop()	throws Exception {

		    try
		    {
		        File file = new File("appstatusfile.txt");
                file.createNewFile();

		    } catch (IOException e)
		    {
		    	logger.info("stop() open appstatusfile.txt");
		    }


		    try {
		        BufferedWriter out = new BufferedWriter(new FileWriter("appstatusfile.txt"));
		        out.write("STOP");
		        out.close();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }


	}


	public void init() throws Exception
	{

		Thread.currentThread().setContextClassLoader(appLoader);

		// Start the AppMointer
		AppMonitorWrapper.initialize();
		//End
		logger.info("Loading startup class");
		Class startupClass = 	appLoader.loadClass("com.bcgi.paymgr.posserver.POSServerMgr");
		Object startupInstance = startupClass.newInstance();

		logger.info("Setting startup class properties");
		String methodName = "setParentClassLoader";
		Class paramTypes[] = new Class[1];
		paramTypes[0] = Class.forName("java.lang.ClassLoader");
		Object paramValues[] = new Object[1];
		paramValues[0] = appLoader;

		Method method =
			startupInstance.getClass().getMethod(methodName, paramTypes);
		method.invoke(startupInstance, paramValues);

		appDaemon = startupInstance;

	}


}