package date_helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/*
 * A class that returns current Date
 * and current time(hour:min:sec)
 */

public class dateHelper {
	
	public static Date getDate() {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		return c.getTime();
	}
	
	public static String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(c.getTime());
				
	}
}
