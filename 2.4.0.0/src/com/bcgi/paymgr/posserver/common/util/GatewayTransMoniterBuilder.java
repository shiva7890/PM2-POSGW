/**
 * 
 */
package com.bcgi.paymgr.posserver.common.util;

import java.util.Calendar;
import org.apache.log4j.Logger;



/**
 * @author venugopalp
 *
 */
public class GatewayTransMoniterBuilder {
	private static Logger Log = Logger.getLogger(GatewayTransMoniterBuilder.class);
	private static final String SEPERATOR = " | ";
	
	public static GatewayTransMoniterBuilder getInstance()
	{
		return new GatewayTransMoniterBuilder();
	}
	public String getDateTime(Calendar calen)
	{
		String calDate = "";
		String calTime = "";
		String month[] = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL","AUG", "SEP", "OCT", "NOV", "DEC" };

		try {
			if (calen.get(Calendar.DAY_OF_MONTH) < 10)
				calDate = "0" + calen.get(Calendar.DAY_OF_MONTH) + "-"
				+ month[calen.get(Calendar.MONTH)] + "-"
				+ calen.get(Calendar.YEAR);
			else
				calDate = calen.get(Calendar.DAY_OF_MONTH) + "-"
				+ month[calen.get(Calendar.MONTH)] + "-"
				+ calen.get(Calendar.YEAR);

			calTime = calen.get(Calendar.HOUR_OF_DAY) + ":"
			+ calen.get(Calendar.MINUTE) + ":"
			+ calen.get(Calendar.SECOND) + "."
			+ calen.get(Calendar.MILLISECOND);
		}
		catch (Exception e)
		{
			Log.error("Exception raised in the method printDateAndTime==>"+ e.getStackTrace());
		}
		return calDate +" "+calTime;
	}
	
	public String prepareMessage(String mdn,String transId,String referenceId,Calendar startDateTime,Calendar endDateTime,String comments)
	{
		StringBuffer message = new StringBuffer();
		
		message.append(mdn);
		message.append(SEPERATOR);
		message.append(transId);
		message.append(SEPERATOR);
		message.append(referenceId);
		message.append(SEPERATOR);
		message.append(getDateTime(startDateTime));
		message.append(SEPERATOR);
		message.append(getDateTime(endDateTime));
		message.append(SEPERATOR);
		message.append(comments);
		
		return message.toString();
	}

}
