package com.bcgi.paymgr.posserver.common.util;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.bcgi.paymgr.posserver.common.constant.ServerConfigConstant;
import com.bcgi.paymgr.posserver.domestic.columbia.constant.LRCMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.common.util.Utility;
import com.bcgi.paymgr.posserver.domestic.ecuador.constant.DMMessageTypeIDConstant;
import com.bcgi.paymgr.posserver.domestic.panama.constant.LRPMessageTypeIDConstant;



public class DataUtil {
	static Logger logger	= Logger.getLogger(DataUtil.class);

private static Hashtable  ht = new Hashtable();
private static String reversalConnectionIds=null;

static 
{
	try
	{
		reversalConnectionIds= PropertiesReader.getValue("AUTO_REVERSAL_SUPPORTED_BILLING_SYSTEM_CONNECTION_IDS");
	}catch(Exception _ex){
		_ex.printStackTrace();
		logger.error("Exception occured while getting the key AUTO_REVERSAL_SUPPORTED_BILLING_SYSTEM_CONNECTION_IDS");
	}
}



public static boolean isAutoReversalSupported(String connectionId)
{
	boolean isAutoReversalSupported=false;
	try
	{
		isAutoReversalSupported=connectionId!=null?reversalConnectionIds.contains(connectionId):false;
	}catch(Exception _ex){
		_ex.printStackTrace();
		logger.error("Exception occured in processing isAutoReversalSupported(connectionId)");
	}finally
	{
		logger.info("isAutoReversalSupported::"+isAutoReversalSupported+"::connectionId::"+connectionId);
	}
	return isAutoReversalSupported;
}

public static String getAmountWithoutPrefixZeroes(String amount){
	int iAmount = Integer.parseInt(amount);
	return String.valueOf(iAmount);
}

public static double getDoubleValue(String amount)
{
	double dAmount = 0.0;
	try
	{
		dAmount = Double.parseDouble(amount);
	}
	catch(NumberFormatException e)
	{

	}

	return dAmount;

}

public static String getValidPaymentAmount(String paymentAmount)
{
	try
	{
		if(paymentAmount!=null && paymentAmount.trim().length()>0)
		{
			/*paymentAmount = String.valueOf(Long.parseLong(paymentAmount));
			String tempDenom=String.valueOf(paymentAmount).substring((paymentAmount.length() - 2));
			String tempDenom1=String.valueOf(Long.parseLong(paymentAmount)).substring(0,(paymentAmount.length() - 2));
			paymentAmount = tempDenom1 + "." + tempDenom;*/

			/** Above code is commeted and below validation is added to send the amount between 0.01 to 0.09 also. Above one
				is don't consider the 0.01 to 0.09 it sends only the 0.0 in this case. By Srinivas P on 12th July 2007.
			*/
			 String tempdenom1str = paymentAmount.substring(0,(paymentAmount.length() - 2));
             String denom1str = String.valueOf(Long.parseLong(tempdenom1str));
             String denom2str = paymentAmount.substring(paymentAmount.length() - 2);
             paymentAmount = denom1str + "." + denom2str;



			return paymentAmount;
		}
	}
	catch(Exception ex)
	{
		System.out.println("Exception in getValidPaymentAmount " + ex);
	}

	return "";
}


	public static String getDecryptedPin(String encryptedPinNo)
	{
		String decryptedString = null;

		DESedeKeySpec      keyspec              =  null;
		SecretKeyFactory   keyfactory           =  null;
		SecretKey          key                  =  null;

		BASE64Decoder decoder = new BASE64Decoder();

		byte     keyBytes[]      = null;

		try
		{
			//java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());

			//System.out.println("key=="+DataUtil.getHashValue("KEY"));

			keyBytes = DataUtil.hexToByte(DataUtil.getHashValue("KEY"));
			keyspec = new DESedeKeySpec(keyBytes);
			keyfactory = SecretKeyFactory.getInstance("DESede");
			key = keyfactory.generateSecret(keyspec);

			byte[] myIV = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
			IvParameterSpec ivspec = new IvParameterSpec(myIV);

			Cipher decryptCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			decryptCipher.init(Cipher.DECRYPT_MODE, key, ivspec);

			byte[] decryptPinBytes = decryptCipher.doFinal(decoder.decodeBuffer(encryptedPinNo));

			decryptedString= new String(decryptPinBytes);
		}
		catch(Exception e)
		{
			System.out.println("Exception in getDecryptedPin(String pin) ==>"+e);
		}
		return decryptedString;
	}

	public static String getHashValue(String key)
	{
		if(ht == null || ht.size() ==0)
			readPinConfigFile();

		return (String)ht.get(key);
	}

    private static void readPinConfigFile()
	 {
    	ArrayList serlist = new ArrayList();

		System.out.println("configfilePath....");

		String configfilePath = ServerConfigConstant.CONFIG_FILE_FOLDER_PATH +ServerConfigConstant.PIN_CONFIG_FILE;

		System.out.println("Pin Config File Path: "+configfilePath);

		Properties properties = new Properties();
	    try
	    {
	    	properties.load(new FileInputStream(configfilePath));
	        if (properties != null){

				Set servernameSet = properties.keySet();

				Iterator serNameSetIter = servernameSet.iterator();

				while (serNameSetIter.hasNext()) {

						String key = (String) (serNameSetIter.next());
						String value = (String)(properties.get(key));

						System.out.println("Key :" + key);
						System.out.println("Value :" + value);

						ht.put(key,value);

				}
		    }
	    }
	    catch (Exception ex){
			System.out.println("readPinConfigFile() - Exception occurred while reading Pin Config File. "+ ex.getMessage());
	    }
	 }

	public static byte[] hexToByte (String hexString)
	{
		int len = hexString.length()/2;
		byte [] keyBytes = new byte[len];

		for (int i=0,j=0; j<len; i+=2,j++)
		{
			int tmpByte = 0;
			int hVal = Byte.parseByte(hexString.substring(i,i+1), 16) ;
			int lVal = Byte.parseByte(hexString.substring(i+1,i+2), 16) ;
			tmpByte =  hVal & 0xFF;
			tmpByte = tmpByte<<4;
			tmpByte = (tmpByte | (lVal & 0xFF));

			keyBytes[j] = (byte)tmpByte;
		}

		return keyBytes;
	}//end of hexToByte()

   public static String getSubscriberNumber(String fullSubscriberId) {
		String customerId = "";
		try{
			
			String subStartPos  = PropertiesReader.getValue(ServerConfigConstant.SUBSCRIBER_START_POSITION);
			String subEndPos  = PropertiesReader.getValue(ServerConfigConstant.SUBSCRIBER_END_POSITION);
			subStartPos = (subStartPos==null || subStartPos.length()==0)?"31":subStartPos.trim();
			subEndPos = (subEndPos==null || subEndPos.length()==0)?"":subEndPos.trim();
			
			if(subEndPos!=null && subEndPos.length()>0)
				customerId = fullSubscriberId.substring(Integer.parseInt(subStartPos),Integer.parseInt(subEndPos));
			else
				customerId = fullSubscriberId.substring(Integer.parseInt(subStartPos),fullSubscriberId.length());
		}
		catch(Exception exception)
		{
			logger.error("getSubscriberNumber() - Exception occurred while reading the Subscriber Start Position and End Position from posservercfg.properties file "+ exception.getMessage());
		}
		return customerId;
   }
   /*
    * Following method is going to be used to get the Subscriber Number --- 30-Jun-2011 
    * @inputparam  	fullSubscriberId
    * @inputparam  	p_StartPosition
    * @inputparam  	p_EndPosition
    * @outputparam  String
    * */
   public static String getRequiredSubscriberNumber(String fullSubscriberId,String p_StartPosition,String p_EndPosition) {
		String customerId = "";
		Properties properties    = new Properties();
		try{
			String subStartPos  = p_StartPosition;
			String subEndPos  = p_EndPosition;
			subStartPos = (subStartPos==null || subStartPos.length()==0)?"31":subStartPos.trim();
			subEndPos = (subEndPos==null || subEndPos.length()==0)?"":subEndPos.trim();
			
			if(subEndPos!=null && subEndPos.length()>0)
				customerId = fullSubscriberId.substring(Integer.parseInt(subStartPos),Integer.parseInt(subEndPos));
			else
				customerId = fullSubscriberId.substring(Integer.parseInt(subStartPos),fullSubscriberId.length());
		}
		catch(Exception exception)
		{
			logger.error("getSubscriberNumber() - Exception occurred while reading the Subscriber Start Position and End Position from posservercfg.properties file "+ exception.getMessage());
		}
		return customerId;
  }
   
   public static String getAutoReversalFlag() {
		String flagValue = "";
		try{
			flagValue = PropertiesReader.getValue(ServerConfigConstant.DOMESTIC_AUTO_REVERSAL_FLAG);
			logger.info("flagValue ====> "+flagValue);
		}
		catch(Exception exception)
		{
			logger.error("getAutoReversalFlag() - Exception occurred while reading the AutoReversalFlag from posservercfg.properties file "+ exception.getMessage());
		}
		return flagValue;
  }
   
   public static String supressZeros(String p_Data,String p_Char_to_Suprs){
		while(p_Data.startsWith(p_Char_to_Suprs)){
			p_Data = p_Data.substring(1, p_Data.length());
		}
		logger.info("Data of after Supress"+p_Data);
		return(p_Data);
   }
   public static String convertToValidDateTime(String value){
	   try {
		   SimpleDateFormat inputFormatter = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss");
		   SimpleDateFormat requiredFormatter = new SimpleDateFormat ("MMddHHmmss");
		   if (value == null || (value!=null && value.trim().length()==0)) {
			   value =requiredFormatter.format(new Date());
		   }else if((value!=null && value.trim().length()>0)){
			   //inputFormatter.setLenient( false ) ;
			   value= requiredFormatter.format(inputFormatter.parse(value));
		   }
		}catch (Exception exception) {
			System.out.println("Exception Occured while parsing the Date in the Required Format :"+ exception.getMessage());
		}
		return value;
	} 
   
   public static String getMappedOperatorId(String processingCode)
   {
	   String mappedOperatorId="";
		
	   try
	   {
		   mappedOperatorId=PropertiesReader.getValue(processingCode.substring(processingCode.length()-2));
	   }catch(Exception e)
	   {
		   e.printStackTrace();
		   System.out.println("Exception Occured in getMappedOperatorId()"+e.getMessage());

	   }
	   finally
	   {
		   System.out.println("Mapped OpeartoId for processingCode "+processingCode+"::is::"+mappedOperatorId);

	   }
	 return mappedOperatorId;
   }
   
   public static String[] getTransTypeCategory(String...requestData)
   {
	   System.out.println("request.getProcessingCode "+requestData);
	   try{
		   for(int i=0;i<requestData.length;i++){
			   System.out.println("-->"+requestData[i]);
		   }
	   }catch(Exception ee){}
	   
	   
	   String processingCode =requestData!=null &&  requestData.length>0?requestData[0]:null;
	   System.out.println("request.processingCode "+processingCode);
	  // String customerID     =requestData!=null &&  requestData.length>1?requestData[1]:null;
	   String transInfo[]=new String[2];
	  
	       if( DMMessageTypeIDConstant.SYNC_PKT_REVERSAL_PROCESSING_CODE_ID.equals(processingCode)||
			   DMMessageTypeIDConstant.SYNC_PKT_REVERSAL_PROCESSING_CODE_ID_TUENTI.equals(processingCode)||
			   DMMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID.equals(processingCode)||
			   DMMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)||
			   LRPMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID.equals(processingCode)||
			   LRPMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)||
			   LRCMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID.equals(processingCode) ||
			   LRCMessageTypeIDConstant.SYNC_MIN_PKT_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)
	       ){
		       transInfo[0]="PKT";
	   }else if(DMMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID.equals(processingCode)||
			    DMMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)||
			    DMMessageTypeIDConstant.SYNC_REVERSAL_PROCESSING_CODE_ID.equals(processingCode)||
			    DMMessageTypeIDConstant.SYNC_REVERSAL_PROCESSING_CODE_ID_TUENTI.equals(processingCode)||
			    LRPMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID.equals(processingCode)||
			    LRPMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)||
			    LRCMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID.equals(processingCode)||
			    LRCMessageTypeIDConstant.SYNC_MIN_RECHARGE_PROCESSING_CODE_ID_TUENTI.equals(processingCode)){
		       transInfo[0]="REC";
	   }
	  return transInfo;
   }
   
   
   
   public static String[] getTranscode(String...requestData)
   {
	   System.out.println("request.getProcessingCode "+requestData);
	   try{
		   for(int i=0;i<requestData.length;i++){
			   System.out.println("-->"+requestData[i]);
		   }
	   }catch(Exception ee){}
	   
	   
	   String processingCode =requestData!=null &&  requestData.length>0?requestData[0]:null;
	   System.out.println("request.processingCode "+processingCode);
	  // String customerID =requestData!=null &&  requestData.length>1?requestData[1]:null;
	   String transInfo[]=new String[2];
	  
	       if(PropertiesReader.getValue("PacketProcessingCodes").contains(processingCode)){
		       transInfo[0]="PKT";
	   }else if(PropertiesReader.getValue("RechargeProcessingCodes").contains(processingCode)){
		        transInfo[0]="REC";
	   }
	  return transInfo;
   }
   
   public static Object getRequiredObject(String processor)
	{
		try
		{
			if(processor!=null)
			{
				Class processorClass=Class.forName(processor);

				return processorClass.newInstance();
			}
		}catch(Exception e)
		{
			logger.info("Error Occureed while creating Processor for:: "+processor);
		}

		return null;
	}
   
   
}
