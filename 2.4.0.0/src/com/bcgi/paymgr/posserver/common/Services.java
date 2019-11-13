package com.bcgi.paymgr.posserver.common;


import com.airejb.partner.PartnerHome;
import com.airejb.rechargefacade.RechargeFacadeHome;
import com.airejb.reversalfacade.ReversalFacadeHome;
import com.airejb.statusinquiry.StatusInquiryHome;
import com.bcgi.pmcore.ejb.domestictransaction.DomesticPOSTransactionFacadeHome;
import com.bcgi.pmcore.ejb.domestictransaction.asynchronous.AsynchronousDomesticPOSFacadeHome;
import com.bcgi.pmcore.ejb.domestictransaction.columbia.DomesticColumbiaPOSTransFacadeHome;
import com.bcgi.async.ejb.AsyncRechargeFacadeHome;
import com.bcgi.async.ejb.AsyncReversalFacadeHome;

public final class Services {

	public  final static int  PARTNER_EJB_ID 		 = 0;
    public  final static int  RECHARGE_FACADE_EJB_ID = 1;
    public  final static int  STATUS_ENQUIRY_EJB_ID  = 2;
    public  final static int  REVERSAL_FACADE_EJB_ID = 3;
    public  final static int  DM_TRANS_FACADE_EJB_ID = 4;
    public  final static int  DM_ASYNC_TRANS_FACADE_EJB_ID = 5;
    public  final static int  ASYNC_RECHARGE_FACADE_EJB_ID = 6;
    public  final static int  ASYNC_REVERSAL_FACADE_EJB_ID = 7;

    public  final static Class  PARTNER_EJB_CLASS 		 	= PartnerHome.class;
	public  final static String PARTNER_EJB_NAME  		 	= "com.airejb.partner";

	public  final static Class  RECHARGE_FACADE_EJB_CLASS 	= RechargeFacadeHome.class;
	public  final static String RECHARGE_FACADE_EJB_NAME  	= "com.airejb.rechargefacade";

	public  final static Class  STATUS_ENQUIRY_EJB_CLASS 	= StatusInquiryHome.class;
	public  final static String STATUS_ENQUIRY_EJB_NAME  	= "com.airejb.statusinquiry";

	public  final static Class  REVERSAL_FACADE_EJB_CLASS 	= ReversalFacadeHome.class;
	public  final static String REVERSAL_FACADE_EJB_NAME  	= "com.airejb.reversalfacade";

	//TODO - Add Domestic POS Transaction Bean information
	public  final static Class  DM_TRANS_FACADE_EJB_CLASS 	= DomesticPOSTransactionFacadeHome.class;
	public  final static String DM_TRANS_FACADE_EJB_NAME  	= "com.bcgi.pmcore.ejb.domestictransaction";
	public  final static String  DM_TRANS_FACADE_EJB_HOME 	= "DomesticPOSTransactionFacadeHome";

   public  final static Class  DM_ASYNC_TRANS_FACADE_EJB_CLASS = AsynchronousDomesticPOSFacadeHome.class;
   public  final static String DM_ASYNC_TRANS_FACADE_EJB_NAME  	= "com.bcgi.pmcore.ejb.domestictransaction.asynchronous";
   public  final static String  DM_ASYNC_TRANS_FACADE_EJB_HOME = "AsynchronousDomesticPOSFacadeHome";		
   
	public  final static Class  ASYNC_RECHARGE_FACADE_EJB_CLASS 	= AsyncRechargeFacadeHome.class;
	public  final static String ASYNC_RECHARGE_FACADE_EJB_NAME  	= "com.bcgi.async.ejb.AsyncRechargeFacade";   
   
	public  final static Class  ASYNC_REVERSAL_FACADE_EJB_CLASS 	= AsyncReversalFacadeHome.class;
	public  final static String ASYNC_REVERSAL_FACADE_EJB_NAME  	= "com.bcgi.async.ejb.AsyncReversalFacade";   	
   
	
	public  final static Class   LRC_SYNC_FACADE_EJB_CLASS 	    = DomesticColumbiaPOSTransFacadeHome.class;
	public  final static String  LRC_SYNC_FACADE_EJB_NAME  	= "com.bcgi.pmcore.ejb.domestictransaction.columbia";
	public  final static String  LRC_SYNC_FACADE_EJB_HOME 	    = "DomesticColumbiaPOSTransFacadeHome";
}
