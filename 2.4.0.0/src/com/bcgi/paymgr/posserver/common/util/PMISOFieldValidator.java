package com.bcgi.paymgr.posserver.common.util;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


public class PMISOFieldValidator
{
	static Logger logger = Logger.getLogger(PMISOFieldValidator.class);
public static boolean isValueExists(String value){
	if (value == null) {
		 return false;
	}
	else{

		if (value.trim().length()== 0) {
			return false;
		}
	}
	return true;
}



public static boolean isNumericValue(String value){
	if (value == null) {
		 return false;
	}
	else{

		try
		{
			Long.parseLong(value);
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;


	}

}

public static boolean isNumericDoubleValue(String value){
	if (value == null) {
		 return false;
	}
	else{

		try
		{
			Double.parseDouble(value);
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;


	}

}

public static boolean isPositiveValue(String value)
{
	if (value == null)
	{
		 return false;
	}
	else
	{
		try
		{
			long lvalue = Long.parseLong(value);

			logger.info("lvalue====>"+lvalue);

			if(lvalue < 0)
				return false;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;
	}

}


//yymmdd
/*
public static boolean isValidDate(String value){
	if (value == null) {
		 return false;
	}
	else{

	Date utilDate = null;
	SimpleDateFormat formatter = new SimpleDateFormat ("yyMMdd");

	try {
		//formatter.setLenient( false ) ;
 		utilDate = formatter.parse(value);

	} catch (Exception e) {

		return false;
	}
		return true;
	}

}
*/

public static boolean isValidDate(String transDate) {

    boolean result = false;

    try {

          String format ="yyMMdd"; 
    	  logger.info("transDate:"+transDate);
    	  
          SimpleDateFormat formatter = new SimpleDateFormat(format);

          Date dt = formatter.parse(transDate);



          Calendar cal1 = Calendar.getInstance();
          cal1.add(Calendar.DATE, -1);
          cal1.set(Calendar.HOUR_OF_DAY,0);
          cal1.set(Calendar.MINUTE,0);
          cal1.set(Calendar.SECOND,0);
          Date previousDay = cal1.getTime();
          
          Calendar cal2 = Calendar.getInstance();
          cal2.add(Calendar.DATE, 1);
          cal2.set(Calendar.HOUR_OF_DAY,23);
          cal2.set(Calendar.MINUTE,59);
          cal2.set(Calendar.SECOND,59);
          Date nextDay = cal2.getTime();
          
          logger.info("previousDay: "+previousDay.toString());
          logger.info("nextDay: "+nextDay.toString());
          logger.info("(previousDay.compareTo(dt) < 0)"+(previousDay.compareTo(dt) < 0));
          logger.info("nextDay.compareTo(dt) > 0): "+(nextDay.compareTo(dt) > 0));
          logger.info("Result: "+ (previousDay.compareTo(dt) < 0 && nextDay.compareTo(dt) > 0));

          if (previousDay.compareTo(dt) < 0 && nextDay.compareTo(dt) > 0) {
            result = true;
          }

    } catch (Exception e) {

          e.printStackTrace();

    }



    return result;

}






//hhmmss
public static boolean isValidTime(String value){
	if (value == null) {
		 return false;
	}
	else{
	//Date utilDate = null;
	SimpleDateFormat formatter = new SimpleDateFormat ("HHmmss");

	try {
		//formatter.setLenient( false ) ;
		formatter.parse(value);
 		 //utilDate = formatter.parse(value);

	} catch (Exception e) {

		return false;
	}
		return true;
	}
}

public static boolean isValidMMDDDate(String value){
	if (value == null) {
		 return false;
	}
	else{
	//Date utilDate = null;
	SimpleDateFormat formatter = new SimpleDateFormat ("MMdd");

	try {
		//formatter.setLenient( false ) ;
		formatter.parse(value);
 		 //utilDate = formatter.parse(value);

	} catch (Exception e) {

		return false;
	}
		return true;
	}
}

public static boolean isValidDateTime(String value){
	if (value == null) {
		 return false;
	}
	else{
	//Date utilDate = null;
	SimpleDateFormat formatter = new SimpleDateFormat ("MMddHHmmss");

	try {
		//formatter.setLenient( false ) ;
 		 //utilDate = formatter.parse(value);
		formatter.parse(value);

	} catch (Exception e) {

		return false;
	}
		return true;
	}
}

public static boolean isSubscriberNumValidLength(String value, int len){
	if (value == null) {
		 return false;
	}
	else{
		if (value.trim().length() != len) {
			return false;
		}
	}
	return true;
}

public static boolean isSubscriberNumMinimumValidLength(String value, int len){
	if (value == null) {
		 return false;
	}
	else{
		if (value.trim().length() < len) {
			return false;
		}
	}
	return true;
}





public static boolean isAllZeroes(String value){
	if (value == null) {
		 return false;
	}
	else{

		try
		{
			
			//long lvalue = Long.parseLong(value);
			Long.parseLong(value);
			
			int intValue = Integer.parseInt(value);
			if (intValue != 0){

				return false;
			}
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;


	}
}

public static boolean is1_12AllZeroes(String value){
	if (value == null) {
		 return false;
	}
	else{
		if (value.trim().length()>=12) {
			String value1_12 = value.substring(0,11);
			return isAllZeroes(value1_12);
		}
		else
		{
			return false;
		}
	}

}



public static boolean isTransAmountWholeNumber(String value){

	if (value == null) {
		 return false;
	}
	else
	{

		if (value.trim().length()>=2)
		{
			int len = value.trim().length();
			String last2digitvalue = value.substring(len-2,len);
			return isAllZeroes(last2digitvalue);
		}
		else
		{
			return false;
		}
	}

}
/*
public static boolean isValidJNPAddress(String jnpURL){


		String ipaddress = "";
		String token = "";
		String jnpPort = "";


		StringTokenizer strTok = null;


		try{
			if (jnpURL == null  || jnpURL.length()==0) {
				 return false;
			}
			else
			{
				ipaddress = jnpURL.substring(0,jnpURL.lastIndexOf(":"));
				jnpPort   = jnpURL.substring(jnpURL.lastIndexOf(":")+1);
				logger.info("==ipaddress======="+ipaddress);
				logger.info("==jnpPort======="+jnpPort);

				strTok = new StringTokenizer(ipaddress,".");
				if(strTok.countTokens() == 4)
				{
					while(strTok.hasMoreTokens())
					{
						token = strTok.nextToken();
						logger.info("===token======"+token);
						if(!isValidRange(token,0,255) ||!isNumericValue(token))
						{
						 logger.info("=====INVALID====");
						 return false;
						}
						else
						{
							if(!isNumericValue(jnpPort))
							return false;
						}
					}

				}
				else
				{
					return false;
				}



			}
		}catch(Exception e)
		{
			logger.info("===Exception while getting the ipaddress validations======");
			return false;
		}
		return true;
    }
*/
	public static boolean isValidRange(String value,int min,int max){
		if (value == null) {
			 return false;
		}
		else{

			try
			{
				long longValue = Long.parseLong(value);
				logger.info("========longValue="+longValue);

				if(!(longValue >= 0 && min <= max))
				 return false;

			}
			catch(NumberFormatException e)
			{
				return false;
			}
			return true;


		}

  }

public static boolean isBelowSysDate(String posDate,String format1)
{
	 /* try{

		SimpleDateFormat formatter = new SimpleDateFormat(format);

	    Date d1 = formatter.parse(posDate);
	    Date d2 = Calendar.getInstance().getTime();

     	if(d1.compareTo(d2) > 0)
     	{
     	   return false;
	 	}


	}catch(Exception e){

		e.printStackTrace();

	}
  		return true;*/
	

	    boolean result = false;
	    //String transDate = posDate.substring(0,6);
	    String transDate = posDate;
	    try {

	          String format ="yyMMddhhmmss"; 
	    	  logger.info("transDate:"+transDate);
	    	  
	          SimpleDateFormat formatter = new SimpleDateFormat(format);

	          Date dt = formatter.parse(transDate);



	          Calendar cal1 = Calendar.getInstance();
	          cal1.add(Calendar.DATE, -1);
	          cal1.set(Calendar.HOUR_OF_DAY,0);
	          cal1.set(Calendar.MINUTE,0);
	          cal1.set(Calendar.SECOND,0);
	          Date previousDay = cal1.getTime();
	          
	          Calendar cal2 = Calendar.getInstance();
	          cal2.add(Calendar.DATE, 1);
	          cal2.set(Calendar.HOUR_OF_DAY,23);
	          cal2.set(Calendar.MINUTE,59);
	          cal2.set(Calendar.SECOND,59);
	          Date nextDay = cal2.getTime();
	          
	         
	          logger.info("Result: "+ (previousDay.compareTo(dt) < 0 && nextDay.compareTo(dt) > 0));

	          if (previousDay.compareTo(dt) < 0 && nextDay.compareTo(dt) > 0) {
	            result = true;
	          }

	    } catch (Exception e) {

	          e.printStackTrace();

	    }



	    return result;

	

}

//
/**
 * This method is used to check alphanumeric validation for any field. return true or false.
 * @param String s
 * @return
 */
public static boolean isAlphaNumeric ( String s ) 
{
	int i = 0, len = s.length();
	while ( i < len && (( Character.isLetterOrDigit( s.charAt( i ) ) ||
			s.charAt( i ) == ' ' || s.charAt( i ) == '.' ||
			s.charAt( i ) == '-' || s.charAt( i ) == '_' )
			|| s.charAt( i ) == '?' )){
		i++;
	}
	return ( i >= len );
}

public static boolean isAlphaNumeric_GTQ ( String s ) 
{
	String exp = "^[a-zA-Z0-9_]*$";
	Pattern p = Pattern.compile(exp);
	Matcher m = p.matcher(s);

	return m.matches();
}
/**
 * @param paymentAmount
 * @return boolean
 * @author srinivasp
 * This method is used to check the amount field conatains fraction part or not.
 */
public static boolean isValidPaymentAmount(String paymentAmount)
{
	try
	{
		logger.debug("Payment Amount before validation:::::"+paymentAmount);
		if(paymentAmount!=null && paymentAmount.trim().length()>0)
		{
			paymentAmount = String.valueOf(Long.parseLong(paymentAmount));
			String tempDenom1 = String.valueOf(Long.parseLong(paymentAmount)).substring((paymentAmount.length() - 2));
			String tempDenom = String.valueOf(Long.parseLong(paymentAmount)).substring(0,(paymentAmount.length() - 2));

			logger.debug("tempDenom===>" + tempDenom);
			logger.debug("tempDenom1(after point)===>" + tempDenom1);

			if(tempDenom1!=null && Integer.parseInt(tempDenom1) > 0)
				return false;
			else
				return true;
		}
	}
	catch(Exception ex)
	{
		logger.error("Exception in checking if the Payment Amount is Valid in the method isValidPaymentAmount ==> " + ex);
		return false;
	}

	return false;
}
}
