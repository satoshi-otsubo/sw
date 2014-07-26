package controllers;

import static play.data.Form.form;
import common.exception.AppException;
import common.exception.AppErrorConstants.AppError;
import play.data.Form;
import play.libs.F.Option;
import play.mvc.*;
import utils.calendar.CalendarUtil;
import views.html.*;
import models.request.TimeTableRequest;
import models.response.TimeTableResponse;
import models.service.TimeTableService;
public class TimeTableController extends BaseController {

/*
    public static Result start() {
	
    	TimeTableService service = new TimeTableService();
    	service.setTimeTable();
    	
    	// 初期ページへ
    	return ok(index.render("index."));
    	
    }
*/
	
    // 時刻表情報取得
    public static Result stationTimeTable(){
    	final Integer RESULT_VIEW_COUNT = 0;
    	final Integer RESULT_VIEW_NO_COUNT = 1;
    	Integer resultView = RESULT_VIEW_COUNT;
    	
    	Form<TimeTableRequest> form = form(TimeTableRequest.class).bindFromRequest();
    	TimeTableRequest data = form.get();
    	
    	// バリデーションなどの処理は後で
    	
    	TimeTableService service = new TimeTableService();
    	Option<TimeTableResponse> result = null;
    	
    	try{
	    	// 表示する曜日を設定する
	    	Integer kind = 1;
    		if(data.kind != null && data.kind.length() > 0){
    			kind = Integer.parseInt(data.kind);
    		}else{
    			kind = CalendarUtil.getDayOfWeekKind();
    		}
    		
    		// カウントページの表示を設定
    		if(data.result_view != null && data.result_view.length() > 0){
    			resultView = RESULT_VIEW_NO_COUNT;
    		}

        	result =service.getStationTimeTable(
        			Long.parseLong(data.station_id),
        			kind,
        			data.line_name,
        			data.direction);
        	
    	}catch(AppException e){
    		return appError(e);
    	}catch(Exception e){
    		return appError(new AppException(AppError.UNKNOWN_ERROR, e, ""));
    	}

    	if(result.isDefined()){
    		//Logger.info(routes.TimeTableController.stationTimeTable().url());
    		if(resultView == RESULT_VIEW_NO_COUNT){
    			return ok(s_timetable_no_count.render("時刻表.", result.get()));
    		}else{
    			return ok(s_timetable.render("時刻表.", result.get()));
    		}
    		
    	}else{
    		return fail(routes.S_SearchController.search(), "error", "時刻表が取得できませんでした。");
    	}
    	
    }

}
