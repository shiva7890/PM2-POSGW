package com.bcgi.paymgr.posserver.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.Format;
import org.apache.log4j.Logger;

public class DateUtil {
	private static Logger logger = Logger.getLogger(DateUtil.class);
	
	public static final String getSystemMonthDate()
	{
		Date date = new Date();
		Format formatter = new SimpleDateFormat("MMdd");    
		String monthday = formatter.format(date);
		return monthday;

	}
	public static final int getSystemYear()
	{
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yy");    
		String year = formatter.format(date);
		
		return Integer.parseInt(year);

	}
	
	public static final String getYear(String monthday){
		
		int iYear = 0;
		String year = "";
		
		String sysMonthDay = getSystemMonthDate();
		
		if ((monthday.equals("1231")) && (sysMonthDay.equals("0101"))){
			iYear = getSystemYear() - 1;
		}
		else if ((monthday.equals("0101"))&& (sysMonthDay.equals("1231"))) {
			iYear = getSystemYear() +1;
		}
		else
		{
			iYear = getSystemYear();
		}
		
		if (iYear < 10){
			year = "0"+	iYear;
		}
		else{
			year = String.valueOf(iYear);
		}
		
		return year;
		 
	}
	
    /**
     * This method returns the current date with the specified format
     * @param	TimeZone        timeZone
     * @param   String			format
     * @return  String          current date with the specified format
     */
	public static String getTodaysDate(TimeZone timeZone,String format)
	{
		String today = "";
		try
		{
			Date date = getCalendar().getTime();
			SimpleDateFormat dateFormatter = new SimpleDateFormat (format);

			if(timeZone!=null)
				dateFormatter.setTimeZone(timeZone);

			today = dateFormatter.format(date);
		}
        catch(Exception e)
        {
        	logger.error("Exception in DateUtil:getTodaysDate(TimeZone,String): " + e.toString());
        }
        return today;
	} // End of String getTodaysDate(TimeZone timeZone,String format)

    // This method is to get the instance of java.util.Calendar
    private static Calendar getCalendar()
    {
         return Calendar.getInstance();
    }
 public static String getTodaysDateAndTime()
	    {
	           Calendar calendar = Calendar.getInstance();

	           String  day = "";
	           String  hour="";
	           String  mints = "";
	           String  month="";
	           String  secs="";
	           String millisecs="";

	           String  todaysDate="";

	           if(calendar.get(Calendar.MONTH)+1 < 10)
	                   month = "0"+ (calendar.get(Calendar.MONTH)+1);
	           else
	               month = Integer.toString(calendar.get(Calendar.MONTH)+1);

	           if(calendar.get(Calendar.DATE) < 10 )
	               day = "0"+calendar.get(Calendar.DATE);
	           else
	               day = Integer.toString(calendar.get(Calendar.DATE));

	           // HOUR_OF_DAY is used to get 24 hr clock
	           if(calendar.get(Calendar.HOUR_OF_DAY) < 10 )
	               hour = "0"+calendar.get(Calendar.HOUR_OF_DAY);
	           else
	               hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));

	           if(calendar.get(Calendar.MINUTE) < 10 )
	               mints = "0"+calendar.get(Calendar.MINUTE);
	           else
	               mints = Integer.toString(calendar.get(Calendar.MINUTE));

	           if(calendar.get(Calendar.SECOND) < 10 )
	               secs = "0"+calendar.get(Calendar.SECOND);
	           else
	               secs = Integer.toString(calendar.get(Calendar.SECOND));
	           
	           logger.info("=getTodaysDateAndTime=Calendar.MILLISECOND===="+Calendar.MILLISECOND);
	           if(calendar.get(Calendar.MILLISECOND) < 1000 )
	               millisecs = "0"+calendar.get(Calendar.MILLISECOND);
	           else
	               millisecs = Integer.toString(calendar.get(Calendar.MILLISECOND));
	        //   logger.info("=getTodaysDateAndTime=millisecs===="+millisecs);
	          
	           return  month+"/"+
               day+"/"+
               (calendar.get(Calendar.YEAR))+" "+
               hour+":"+
               mints+":"+
               secs+":"+millisecs;

	         

	    }  //End of getTodaysDateWithTime() Method

}
