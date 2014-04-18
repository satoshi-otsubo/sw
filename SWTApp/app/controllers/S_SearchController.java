package controllers;

import java.util.ArrayList;
import java.util.List;

import common.exception.AppException;

import play.data.Form;
import play.libs.F.Option;
import play.mvc.*;
import views.html.*;
import models.request.S_SearchRequest;
import models.response.R_Line;
import models.response.R_Prefecture;
import models.response.R_Station;
import models.response.S_SearchConditionsResponse;
import models.response.S_SearchResponse;
import models.service.S_SearchService;
import static play.data.Form.form;

public class S_SearchController extends BaseController {
	
	public static Result search() {
		return ok(s_search.render("search_index 条件指定画面."));
	}
    
    /**
    *
    * @param 
    * @return
    */
    public static Result searchStationsByLine(){
    	Form<S_SearchRequest> form = form(S_SearchRequest.class).bindFromRequest();
    	S_SearchRequest data = form.get();
    	S_SearchService service = new S_SearchService();
    	try{
	    	Option<List<R_Station>> stations = service.getStationsByLine(Long.parseLong(data.line_id));
	    	if(stations.isDefined()){
	    		S_SearchResponse respose = new S_SearchResponse();
	    		respose.stations = stations.get();
	    		respose.line_id = data.line_id;
	    		respose.action = 1;
	    		
	    		// ページ用設定
	    		respose.page = data.page;
	    		service.setResponsePageData(respose);
	    		
	    		return ok(s_search_result.render("searchStationsByLine", respose));
	    	}
	    }catch(AppException e){
	    	appError(e);
    	}catch(Exception e){
    		UnknownError( e);
    	}
    	return fail(routes.S_SearchController.searchConditions(), "error", "路線検索でエラーが発生しました。");
    }
    /**
    *
    * @param 
    * @return
    */
    public static Result searchStationsByGeoCode(){
    	Form<S_SearchRequest> form = form(S_SearchRequest.class).bindFromRequest();
    	S_SearchRequest data = form.get();
    	S_SearchResponse respose = new S_SearchResponse();
    	
    	if(data.area == null){
    		data.area = "5000";
    	}
    	
    	if(data.area == null || data.lat == null || data.lon == null){
    		return ok(s_search.render("search_index 条件指定画面 geoSearchから."));
    	}
    	S_SearchService service = new S_SearchService();
    	
    	try{
	    	Option<List<R_Station>> result = 
	    			service.getStationsByGeoCode(
	    					Double.parseDouble(data.lat),
	    					Double.parseDouble(data.lon),
	    					Integer.parseInt(data.area));
 
	    	if(result.isDefined()){
	    		
	    		respose.stations = result.get();
	    		respose.action = 2;
	    		respose.lat = data.lat;
	    		respose.lon = data.lon;
	    		respose.area = data.area;
	    		
	    		// ページ用設定
	    		if(data.page == null){
	    			data.page = 1;
	    		}
	    		respose.page = data.page;
	    		service.setResponsePageData(respose);
	    		
	    		return ok(s_search_result.render("geo Search.", respose));
	    	}
	    }catch(AppException e){
	    	appError(e);
    	}catch(Exception e){
    		UnknownError( e);
    	}
    	
    	return fail(routes.S_SearchController.searchConditions(), "error", "位置情報検索でエラーが発生しました。");
    }
    
    /**
    * 駅名（部分一致）から駅リストを取得する
    * @param 
    * @return
    */
    public static Result searchStationsByName(){
    	Form<S_SearchRequest> form = form(S_SearchRequest.class).bindFromRequest();
    	S_SearchRequest data = form.get();
    	if(data.station_name == null){
    		return ok(s_search.render("search_index 条件指定画面 searchStationsByNameから."));
    	}
    	S_SearchService service = new S_SearchService();
    	try{
	    	Option<List<R_Station>> result = service.getStationsByName(data.station_name);
	    	if(result.isDefined()){
	    		S_SearchResponse respose = new S_SearchResponse();
	    		respose.stations = result.get();
	    		respose.station_name = data.station_name;
	    		respose.action = 3;
	    		
	    		// ページ用設定
	    		if(data.page == null){
	    			data.page = 1;
	    		}
	    		respose.page = data.page;
	    		service.setResponsePageData(respose);
	    		return ok(s_search_result.render("searchStationsByName.", respose));
	    	}
	    }catch(AppException e){
	    	appError(e);
    	}catch(Exception e){
    		UnknownError( e);
    	}
    	return fail(routes.S_SearchController.searchConditions(), "error", "駅名検索でエラーが発生しました。");
    }
    
    /**
    *
    * 検索ページ（開始画面）
    * @param 
    * @return
    */
    public static Result searchConditions(){
    	Option<List<R_Prefecture>> result = new S_SearchService().getPrefectureList();
    	if(result.isDefined()){
    		S_SearchConditionsResponse respose = new S_SearchConditionsResponse();
    		respose.prefectures = result.get();
    		
    		Form<S_SearchRequest> request = new Form<S_SearchRequest>(S_SearchRequest.class);
    		return ok(s_search_conditions.render("s_search_conditions.", respose, request));
    	}
    	// 初期ページへ
    	return ok(index.render("index."));
    }
    
    /**
    *
    * 都道府県から路線リストを取得する
    * @param 
    * @return
    */
    public static Result lineListSearchByPrefecture(Long prefectureId) {
    	Option<R_Prefecture> prefecture = new S_SearchService().getPrefecture(prefectureId);
    	if(prefecture.isDefined()){
        	// 都道府県idから都道府県に属する路線リストを取得
        	Option<List<R_Line>> lines = new S_SearchService().getLineListByPrefecture(prefecture.get().id);
        	if(lines.isDefined()){
        		S_SearchConditionsResponse respose = new S_SearchConditionsResponse();
        		List<R_Prefecture> prefectures = new ArrayList<R_Prefecture>();
        		
        		// 都道府県が持っている路線リストをセット
        		prefecture.get().lines = lines.get();
        		prefectures.add(prefecture.get());
        		respose.prefectures = prefectures;
        		
        		return ok(s_search_lineListByPrefecture.render("s_search_lineListByPrefecture.", respose));
        	}
    	}
    	return fail(routes.S_SearchController.searchConditions(), "error", "都道府県検索でエラーが発生しました。");
    }
    
    public static Result index() {
    	return ok(index.render("index."));
    }
    
}
