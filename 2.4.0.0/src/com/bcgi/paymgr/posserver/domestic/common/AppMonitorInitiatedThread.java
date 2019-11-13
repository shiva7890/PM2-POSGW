package com.bcgi.paymgr.posserver.domestic.common;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.logging.Logger;

import com.bcgi.paymgr.posserver.POSServerMgr;
import com.bcgi.paymgr.posserver.common.util.AppMonitorWrapper;
import com.bcgi.paymgr.posserver.common.util.GatewayTransMoniterBuilder;
import com.bcgi.paymgr.posserver.domestic.columbia.processor.LRCRechargeMINProcessor;
import com.bcgi.paymgr.posserver.domestic.common.constant.AppMoniterEventConstants;
import com.bcgi.paymgr.posserver.domestic.common.constant.ResponseCodeConstant;
import com.bcgi.paymgr.posserver.domestic.common.dto.POSGWAccountRechargeDTO;

public class AppMonitorInitiatedThread implements Runnable{
	
	static Logger logger	= Logger.getLogger(AppMonitorInitiatedThread.class);
	
	private int threadValueInSeconds = POSServerMgr.getThreadValueInSeconds();; 
	
	POSGWAccountRechargeDTO posGWAccountRechargeDTO = null;
	Calendar startCal;
	Calendar endCal;
	
	
	public AppMonitorInitiatedThread(POSGWAccountRechargeDTO pard,Calendar sc,Calendar ec)
	{
		posGWAccountRechargeDTO = pard;
		startCal = sc;
		endCal = ec;
	}
	
	
	public void run() {
		try
		{	
			//Display info about this particular thread
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<String> future = executor.submit(new Task1(posGWAccountRechargeDTO,startCal,endCal));
			try
			{
				future.get(threadValueInSeconds, TimeUnit.SECONDS);
			} catch (TimeoutException e)
			{			
			}
			executor.shutdownNow();
		}catch(Exception e){
			logger.info("AppMonitorInitiatedThread:: run() exception=>"+e);
		}
	}
}

class Task1 implements Callable<String>
{
	
	static Logger logger	= Logger.getLogger(Task1.class);
	
	POSGWAccountRechargeDTO posGWAccountRechargeDTO = null;
	Calendar startCal;
	Calendar endCal;
	
	public Task1(POSGWAccountRechargeDTO posGWAccountRechargeDTO,Calendar sc,Calendar ec)
	{
		this.posGWAccountRechargeDTO = posGWAccountRechargeDTO;
		startCal = sc;
		endCal = ec;
	}
		
	@Override
	public String call() throws Exception
	{
		//Logging the RoundTrip Time using AppMoniter
		//Calendar endCal = Calendar.getInstance();
		long roundTriptime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
		String resultMsg  = GatewayTransMoniterBuilder.getInstance().prepareMessage(posGWAccountRechargeDTO.getPinNumber(),posGWAccountRechargeDTO.getAuthorizationNumber(),posGWAccountRechargeDTO.getSystemTraceAuditNumber(), startCal, endCal,"Domestic Recharge Transaction");
		
		//logger.info("sleeping for 10 seconds......");
		//Thread.sleep(10000); // Just to demo a long running task of 10 seconds.
		//logger.info("done......");
		
		//logger.info("resultMsg=="+resultMsg);
		if(posGWAccountRechargeDTO.getResponseCode().equalsIgnoreCase(ResponseCodeConstant.SUCCESS))
			AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_SUCCESS, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
		else
			AppMonitorWrapper.logEvent(AppMoniterEventConstants.RECHARGE_GATEWAY_DOMPOS_REJECTED, resultMsg, (int)roundTriptime, AppMoniterEventConstants.CARRIER_ID);
		// End of AppMoniter Logging		
		
		return "";
	}
}
