package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Constants;
import common.exception.AppException;
import common.exception.AppErrorConstants.AppError;
import play.data.Form;
import play.libs.F.Option;
import play.mvc.*;
import views.html.*;
import models.request.S_SearchRequest;
import models.response.R_Company;
import models.response.R_Line;
import models.response.R_Prefecture;
import models.response.R_Prefecturearea;
import models.response.R_Station;
import models.response.S_SearchConditionsResponse;
import models.response.S_SearchResponse;
import models.service.S_SearchService;
import static play.data.Form.form;

public class S_SearchController extends BaseController {
	
    /**
    *
    * 検索ページ（開始画面）
    * @param 
    * @return
    */
    public static Result search(){
    	try{
    		// 都道府県地域(一緒に都道府県も)を取得
    		Option<List<R_Prefecturearea>> result = new S_SearchService().getPrefectureareaList();
    		
	    	if(result.isDefined()){
	    		S_SearchConditionsResponse respose = new S_SearchConditionsResponse();
	    		respose.prefectureareas = result.get();
	    		
	    		Form<S_SearchRequest> request = new Form<S_SearchRequest>(S_SearchRequest.class);
	    		return ok(s_search.render("s_search_conditions.", respose, request));
	    	}
    	}catch(AppException e){
	    	return appError(e);
    	}catch(Exception e){
    		return appError(new AppException(AppError.UNKNOWN_ERROR, e, ""));
    	}
    	// 初期ページへ
    	return ok(index.render("index."));
    }
    
    /**
    *
    * @param 
    * @return
    */
    public static Result searchStationsByLine(){
    	Form<S_SearchRequest> form = form(S_SearchRequest.class).bindFromRequest();
    	S_SearchRequest data = form.get();
    	// play2.3から自動でurlパラメータを自動でマッピングしてくれなくなったから、リクエストから取得
    	if(data.line_id == null){
        	data.line_id = form.data().get("line_id");
        	data.page = Integer.parseInt(form.data().get("page"));
    	}
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
	    	return appError(e);
    	}catch(Exception e){
    		return appError(new AppException(AppError.UNKNOWN_ERROR, e, ""));
    	}
    	return fail(routes.S_SearchController.search(), "error", "路線検索でエラーが発生しました。");
    }
    /**
    *　位置情報検索
    *
    * @param 
    * @return
    */
    public static Result searchStationsByGeoCode(){
    	Form<S_SearchRequest> form = form(S_SearchRequest.class).bindFromRequest();
    	S_SearchRequest data = form.get();
    	S_SearchResponse respose = new S_SearchResponse();
    	
    	// play2.3から自動でurlパラメータを自動でマッピングしてくれなくなったから、リクエストから取得
    	if(data.area == null || data.lat == null || data.lon == null){
        	data.area = form.data().get("area");
        	data.lat = form.data().get("lat");
        	data.lon = form.data().get("lon");
        	data.page = Integer.parseInt(form.data().get("page"));
    	}
    	
    	// トップページからの場合はエリアは未入力である
    	if(data.area == null){
    		data.area = "5000";
    	}

    	if(data.area == null || data.lat == null || data.lon == null){
    		return fail(routes.S_SearchController.search(), "error", "検索条件がない。");
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
	    		respose.action = Constants.STATION_SEARCH_GEOCODE;
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
	    	return appError(e);
    	}catch(Exception e){
    		return appError(new AppException(AppError.UNKNOWN_ERROR, e, ""));
    	}
    	return fail(routes.S_SearchController.search(), "error", "検索結果が存在しません。");
    }
    
    /**
    * 駅名（部分一致）から駅リストを取得する
    * @param 
    * @return
    */
    public static Result searchStationsByName(){
    	Form<S_SearchRequest> form = form(S_SearchRequest.class).bindFromRequest();
    	S_SearchRequest data = form.get();
    	// play2.3から自動でurlパラメータを自動でマッピングしてくれなくなったから、リクエストから取得
    	if(data.station_name == null){
        	data.station_name = form.data().get("station_name");
        	data.page = Integer.parseInt(form.data().get("page"));
    	}
    	if(data.station_name == null || data.station_name.isEmpty()){
    		return fail(routes.S_SearchController.search(), "error", "駅名が入力されていません。");
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
	    	return appError(e);
    	}catch(Exception e){
    		return appError(new AppException(AppError.UNKNOWN_ERROR, e, ""));
    	}
    	return fail(routes.S_SearchController.search(), "error", "検索結果が存在しません。");
    }

    /**
    *
    * 都道府県から路線リストを取得する
    * @param 
    * @return
    */
    public static Result lineListSearchByPrefecture(Long prefectureId) {
    	// 返却
    	S_SearchConditionsResponse respose = new S_SearchConditionsResponse();
    	
    	try{
        	Option<R_Prefecture> prefecture = new S_SearchService().getPrefecture(prefectureId);
        	if(prefecture.isDefined()){
            	// 都道府県idから都道府県に属する会社毎の路線リストを取得
        		Option<List<R_Company>> companyList = new S_SearchService().getLineListByPrefecture(prefecture.get().id);
        		if(companyList.isDefined()){
            		List<R_Prefecture> prefectures = new ArrayList<R_Prefecture>();
            		
            		// 都道府県が持っている会社毎の路線リストをセット
            		prefecture.get().companyList = companyList.get();
            		
            		prefectures.add(prefecture.get());
            		respose.prefectures = prefectures;
            		
            		return ok(s_search_lineListByPrefecture.render("s_search_lineListByPrefecture.", respose));
            	}
        	}
    	}catch(AppException e){
	    	return appError(e);
    	}catch(Exception e){
    		return appError(new AppException(AppError.UNKNOWN_ERROR, e, ""));
    	}

    	return fail(routes.S_SearchController.search(), "error", "都道府県検索でエラーが発生しました。");
    }
    
    public static Result index() {
    	return ok(index.render("index."));
    }
    
}
