package utils.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import play.Logger;
import net.arnx.jsonic.JSON;

/**
 * DESCRIPTION
 *
 * @author 
 * @since 
 */
public class CalendarUtil {

	// 1日前の種別を取得する
	public static Integer getDayOfWeekKind(Integer s_day) throws Exception{
		Calendar cal = Calendar.getInstance(); 
		//cal.add(Calendar.DATE, -1);
		cal.add(Calendar.DATE, s_day);
		return getCommonDayOfWeekKind(cal);
	}
	
	// 現在の種別を取得する
	public static Integer getDayOfWeekKind() throws Exception{
		Calendar cal = Calendar.getInstance();
		return getCommonDayOfWeekKind(cal);
	}
	
    public static Integer getCommonDayOfWeekKind(Calendar cal) throws Exception{
    	Integer result = 1;
    	//Calendar cal = Calendar.getInstance(); 
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
	        case Calendar.SUNDAY: 
	        	result = 4;
	        	break;
	        case Calendar.MONDAY: 
	        	result = 1;
	        	break;
	        case Calendar.TUESDAY: 
	        	result = 1;
	        	break;
	        case Calendar.WEDNESDAY: 
	        	result = 1;
	        	break;
	        case Calendar.THURSDAY: 
	        	result = 1;
	        	break;
	        case Calendar.FRIDAY: 
	        	result = 1;
	        	break;
	        case Calendar.SATURDAY: 
	        	result = 2;
	        	break;
	        default:
	        	result = 1;
        }
        
        if(result == 1){
        	if(isHday(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE))){
        		result = 4;
        	}
        }
        
        return result;
    }
    
    
    // 祝日であるかの判定を行う
    private static boolean isHday(Integer y, Integer m, Integer d) throws Exception{
    	boolean retBoolean = false;
    	String jsonStr = "";

    	String hdayUrl = "http://www.finds.jp/ws/calendar.php?json&y=" + y + "&m=" + m + "&t=h";
    	try {
    		URL url = new URL(hdayUrl);
    		HttpURLConnection connection = null;
    		try {
    			
	            connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("GET");
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            	try (InputStreamReader isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
	            		BufferedReader reader = new BufferedReader(isr)) {
	                	String line;
	                    while ((line = reader.readLine()) != null) {
	                    	jsonStr += line;
	                    }
	            	}
	            }
    		}finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
    		
    		if(jsonStr.length() <= 0){
    			retBoolean =  false;
    		}else{
    			CalendarJSON decodeJSON = JSON.decode(jsonStr, CalendarJSON.class);
    			if(Integer.parseInt(decodeJSON.result.hdays) > 0){
        			for(CalendarDayJSON hDay: decodeJSON.result.day){
        				if(d == Integer.parseInt(hDay.mday)){
        					retBoolean = true;
        				}
        			}
    			}
    			//Logger.info(jsonStr);
    		}
    		
    	} catch (IOException e) {
        	// TODO　JSON変換関連エラー
        }
    	
    	
    	return retBoolean;
    }

}
