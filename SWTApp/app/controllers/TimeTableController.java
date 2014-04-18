package controllers;

import static play.data.Form.form;

import java.util.Calendar;
import java.util.List;

import play.*;
import play.data.Form;
import play.libs.F.Option;
import play.mvc.*;
import utils.CalendarUtil;
import views.html.*;
import models.entity.*;
import models.request.S_SearchRequest;
import models.request.TimeTableRequest;
import models.response.R_Station;
import models.response.TimeTableResponse;
import models.service.TimeTableService;
public class TimeTableController extends BaseController {

    public static Result start() {
	
    	TimeTableService service = new TimeTableService();
    	service.setTimeTable();
        return ok(s_search.render("Your new application is ready."));
    	
    }
    
    // 時刻表情報取得
    public static Result stationTimeTable(){
    	Form<TimeTableRequest> form = form(TimeTableRequest.class).bindFromRequest();
    	TimeTableRequest data = form.get();
    	
    	// 表示する曜日を設定する
    	Integer kind = CalendarUtil.getDayOfWeekKind();
    	
    	// バリデーションなどの処理は後で
    	
    	TimeTableService service = new TimeTableService();
    	Option<TimeTableResponse> result =service.getStationTimeTable(
    			Long.parseLong(data.station_id),
    			kind,
    			data.line_name,
    			data.direction);
    	if(result.isDefined()){
    		return ok(s_timetable.render("時刻表.", result.get()));
    	}else{
    		return ok(s_search.render("Your new application is ready."));
    	}
    	
    }

}
