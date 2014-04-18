package utils;

import java.util.Calendar;

/**
 * DESCRIPTION
 *
 * @author 
 * @since 
 */
public class CalendarUtil {

    public static Integer getDayOfWeekKind() {
    	Integer result = 1;
    	Calendar cal = Calendar.getInstance(); 
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
	        case Calendar.SUNDAY: result = 4;
	        case Calendar.MONDAY: result = 1;
	        case Calendar.TUESDAY: result = 1;
	        case Calendar.WEDNESDAY: result = 1;
	        case Calendar.THURSDAY: result = 1;
	        case Calendar.FRIDAY: result = 1;
	        case Calendar.SATURDAY: result = 2;
        }
        return result;
    }

}
