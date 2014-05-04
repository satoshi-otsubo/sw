package controllers.api;

import static play.data.Form.form;

import java.util.ArrayList;

import common.api.ApiStatusConstants;
import play.Logger;
import play.data.Form;
import play.libs.F.Option;
import play.mvc.*;
import utils.calendar.CalendarUtil;
import models.request.TimeTableRequest;
import models.response.R_TimeTable;
import models.response.TimeTableResponse;
import models.response.api.ApiTimeInfo;
import models.response.api.TimeTableApiResponse;
import models.service.TimeTableService;
import net.arnx.jsonic.JSON;

public class ApiTimeTableController extends Controller{


    
    // 時刻表情報取得
    public static Result stationTimeTable(){
    	Form<TimeTableRequest> form = form(TimeTableRequest.class).bindFromRequest();
    	TimeTableRequest data = form.get();
    	
    	// バリデーションなどの処理は後で
    	
    	TimeTableService service = new TimeTableService();
    	Option<TimeTableResponse> oTimeTableResponse = null;
    	TimeTableApiResponse result = null;
    	String retJSON = null;
    	try{
	    	// 表示する曜日1日前を設定する
	    	//Integer kind = CalendarUtil.getDayOfWeekKind();
    		Integer kind = CalendarUtil.getDayOfWeekKind(Integer.parseInt(data.s_day));
    		
    		oTimeTableResponse = service.getStationTimeTable(
        			Long.parseLong(data.station_id),
        			kind,
        			data.line_name,
        			data.direction);
        	
    		// API返却用にちょっと修正します
    		if(oTimeTableResponse.isDefined()){
    			TimeTableResponse sRes = oTimeTableResponse.get();
    			result = new TimeTableApiResponse();
    			result.timeInfos = new ArrayList<ApiTimeInfo>();
    			
    			result.code = ApiStatusConstants.OK.getCode();
    			result.status = ApiStatusConstants.OK.getMessage();
    			result.message = "";
    			result.station_id = sRes.station_id;
    			result.station_name = sRes.station_name;
    			result.kind = sRes.kind;
    			result.line_name = sRes.line_name;
    			result.direction = sRes.direction;
    			result.totalTimes = sRes.totalTimes;
    			result.noticeTrainKinds = sRes.noticeTrainKinds;
    			result.noticeDestinations = sRes.noticeDestinations;
    			result.noticeMarks = sRes.noticeMarks;
    			for(R_TimeTable rtt: sRes.timeTables){
    				for(R_TimeTable dRtt: rtt.detailTimeTables){
    	   				ApiTimeInfo apiTimeInfo = new ApiTimeInfo();
        				apiTimeInfo.mark = dRtt.mark;
        				apiTimeInfo.trn = dRtt.trn;
        				apiTimeInfo.sta = dRtt.sta;
        				apiTimeInfo.hour = dRtt.hour;
        				apiTimeInfo.minute = dRtt.minute;
        				if(dRtt.dispTrainKind != null && dRtt.dispTrainKind.length() > 0){
        					apiTimeInfo.dispTrainKind = dRtt.dispTrainKind;
        				}else{
        					apiTimeInfo.dispTrainKind = "";
        				}
        				if(dRtt.dispDestination != null && dRtt.dispDestination.length() > 0){
        					apiTimeInfo.dispDestination = dRtt.dispDestination;
        				}else{
        					apiTimeInfo.dispDestination = "";
        				}
        				if(dRtt.dispMark != null && dRtt.dispMark.length() > 0){
        					apiTimeInfo.dispMark = dRtt.dispMark;
        				}else{
        					apiTimeInfo.dispMark = "";
        				}
        				apiTimeInfo.timeIndex = dRtt.timeIndex;
        				result.timeInfos.add(apiTimeInfo);
    				}
 
    			}
    		}
    		retJSON = JSON.encode(result, true);
    		//Logger.info(retJSON);
    	}catch(Exception e){
    		//controllers.api.routes.ApiTimeTableController.stationTimeTable()
    	}
    	
    	if(retJSON != null){
    		return ok(retJSON);
    	}
    	
    	return null;
    }

}
