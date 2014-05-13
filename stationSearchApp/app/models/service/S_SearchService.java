package models.service;

import common.Constants;

import utils.GeoCodeRange;
import utils.GeoCodeUtil;
import utils.OptionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.libs.F.None;
import play.libs.F.Option;
import models.dao.PrefectureDao;
import models.dao.StationDao;
import models.dao.TimetableDao;
import models.entity.Prefecture;
import models.entity.Station;
import models.entity.Timetable;
import models.response.R_Line;
import models.response.R_LineDirection;
import models.response.R_Prefecture;
import models.response.R_Station;
import models.response.S_SearchResponse;
import models.setting.S_SearchSetting;

/**
 * サービスクラス
 *
 * @author 
 * @since 
 */
public class S_SearchService {
	
    /**
    *
    * @param 
    * @return
    */
	public Option<List<R_Station>> getStationsByGeoCode(Double lat, Double lon, int area) throws Exception{

		// 現在地から指定エリアの範囲を緯度・経度に変換
		GeoCodeRange gcr = GeoCodeUtil.getGeoCodeRange(area, lat, lon);
		
		// 範囲内にある駅を取得する
		Option<List<Station>> optStations = 
				StationDao.use().findByGeoCode(
						gcr.getRangeFrom().getLatitude(),
						gcr.getRangeFrom().getLongitude(),
						gcr.getRangeTo().getLatitude(),
						gcr.getRangeTo().getLongitude());
		
		// 時刻表から画面に表示する内容を取得する
		if(optStations.isDefined()){
			Option<List<R_Station>> rStations = getStationsConvResponse(optStations.get(), Constants.STATION_SEARCH_GEOCODE, lat, lon);
			if(rStations.isDefined()){
				return rStations;
			}
		}

		return new None<List<R_Station>>();
	}
	
    /**
    *
    * @param 
    * @return
    */
	// 路線から駅リストを取得する
	public Option<List<R_Station>> getStationsByLine(Long line_id)  throws Exception {
		// 路線から駅リストを取得する
		Option<List<Station>> stations = StationDao.use().findByLine(line_id);
		// 時刻表から画面に表示する内容を取得する
		if(stations.isDefined()){
			Option<List<R_Station>> rStations = getStationsConvResponse(stations.get(), Constants.STATION_SEARCH_LINE, new Double(0), new Double(0));
			if(rStations.isDefined()){
				return rStations;
			}
		}
		return new None<List<R_Station>>();
	}
	
    /**
    *
    * @param 
    * @return
    */
	// 駅名から駅リストを取得する
	public Option<List<R_Station>> getStationsByName(String station_name)  throws Exception {
		// 路線から駅リストを取得する
		Option<List<Station>> stations = StationDao.use().findByName(station_name);
		// 時刻表から画面に表示する内容を取得する
		if(stations.isDefined()){
			Option<List<R_Station>> rStations = getStationsConvResponse(stations.get(), Constants.STATION_SEARCH_NAME, new Double(0), new Double(0));
			if(rStations.isDefined()){
				return rStations;
			}
		}
		return new None<List<R_Station>>();
	}
	
    /**
    * 
    * 駅リストから表示用リストをセットする
    * @param 
    * @return
    */
	private Option<List<R_Station>> getStationsConvResponse(
			List<Station> stations, Integer searchCode, Double lat, Double lon) throws Exception{
		// 返却用インスタンス
		List<R_Station> rStations = new ArrayList<R_Station>();
		
		// 駅名・緯度・軽度が等しい物は１つにまとめる
		Option<List<Station>> distinctStations = StationDao.use().distinctByNameLatLon(stations);

		for(Station station: distinctStations.get()){
			// 画面表示用Beanに情報を格納する
			R_Station rStation = new R_Station();
			rStation.station_name = station.station_name;
			rStation.lat = station.lat;
			rStation.lon = station.lon;
			rStation.sort = station.e_sort;
			
			// 現在地からの検索の場合
			if(searchCode == Constants.STATION_SEARCH_GEOCODE){
				// 現在地と駅の緯度・経度から距離を求める
				rStation.distance = GeoCodeUtil.getGeoCodeDistance(lat, lon, station.lat, station.lon);
			}

			// 路線名・方面を表示用データに格納する
			rStation.lineDirections = new ArrayList<R_LineDirection>();
			// List<Timetable> timeTables = TimetableDao.use().findStationLineDirections(station.stationIds).get();
			Option<List<Timetable>> timeTables = TimetableDao.use().findStationLineDirections(station.stationIds);
			//timeTables.get();
			if(timeTables.isDefined()){
				Map<String, Timetable> stationLineDirectionMap = new HashMap<String, Timetable>();
				for(Timetable timeTable: timeTables.get()){
					// キーになる文字列を作成
					String lineDirectionKey = timeTable.line_name + timeTable.direction;
					if(!stationLineDirectionMap.containsKey(lineDirectionKey)){
						// TODO　駅IDから駅情報を取得し、路線名が一致しないものは格納しないようにする
						
						
						
						stationLineDirectionMap.put(lineDirectionKey, timeTable);
						
						R_LineDirection lineDirection = new R_LineDirection();
						lineDirection.station_id = timeTable.station_id;
						lineDirection.line_name = timeTable.line_name;
						lineDirection.direction_name = timeTable.direction;
						rStation.lineDirections.add(lineDirection);
					}
				}
			}
			rStations.add(rStation);
		}
		
		// 現在地からの検索の場合
		if(searchCode == Constants.STATION_SEARCH_GEOCODE){
			Collections.sort(rStations, new Comparator<R_Station>(){
				public int compare(R_Station t1, R_Station t2) {
					return (int) (t1.distance - t2.distance);  // 昇順
				}
			});
		}else if(searchCode == Constants.STATION_SEARCH_LINE){
			// 路線検索の場合、DBのソート順で並び替えを行う
			Collections.sort(rStations, new Comparator<R_Station>(){
				public int compare(R_Station t1, R_Station t2) {
					return (int) (t1.sort - t2.sort);  // 昇順
				}
			});
		}else{
			// 駅名検索の場合、DBの名前順で並び替えを行う（日本語なんで思ったように並ばないけど、とりあえず同じ名前の駅をまとめる）
			Collections.sort(rStations, new Comparator<R_Station>(){
				public int compare(R_Station t1, R_Station t2) {
					//return (int) (t1.station_name.length() - t2.station_name.length());  // 昇順
					return t1.station_name.compareTo(t2.station_name);
				}
			});
		}
		return OptionUtil.apply(rStations);
	}
	
    /**
    * 
    * 都道府県を取得する
    * @param 
    * @return
    */
	public Option<R_Prefecture> getPrefecture(Long id){
		Option<Prefecture> prefecture = PrefectureDao.use().findById(id);
		if(prefecture.isDefined()){
			R_Prefecture rPrefecture = new R_Prefecture();
			rPrefecture.id = prefecture.get().id;
			rPrefecture.pref_name = prefecture.get().pref_name;
			return OptionUtil.apply(rPrefecture);
		}
		return new None<R_Prefecture>();

	}
	
    /**
    * 
    * 都道府県一覧を取得する
    * @param 
    * @return
    */
	public Option<List<R_Prefecture>> getPrefectureList() throws Exception{
		Option<List<Prefecture>> prefecture = PrefectureDao.use().findAll();
		if(prefecture.isDefined()){
			List<R_Prefecture> rPrefectureList = new ArrayList<R_Prefecture>();
			for(Prefecture p: prefecture.get()){
				R_Prefecture r = new R_Prefecture();
				r.id = p.id;
				r.pref_name = p.pref_name;
				rPrefectureList.add(r);
			}
			return OptionUtil.apply(rPrefectureList);
		}
		return new None<List<R_Prefecture>>();

	}
	
    /**
    * 
    * 都道府県コードから路線を取得する
    * @param 
    * @return
    */
	public Option<List<R_Line>> getLineListByPrefecture(Long prefectureId) throws Exception {
		Option<List<Station>> prefStations = StationDao.use().findByPref(prefectureId);
		if(prefStations.isDefined()){
			List<Long> stationIds = new ArrayList<Long>();
			for(Station s: prefStations.get()){
				stationIds.add(s.id);
			}
			Map<Long, Timetable> lineInfoMap = new HashMap<Long, Timetable>();
			Option<List<Timetable>> LineInfos = TimetableDao.use().findStationLineNameList(stationIds);
			if(LineInfos.isDefined()){
				for(Timetable li: LineInfos.get()){
					if(!lineInfoMap.containsKey(li.station_id)){
						lineInfoMap.put(li.station_id, li);
					}
				}
			}
			// 路線名でマージするためのマップ
			Map<String, String> lineHashMap = new HashMap<String, String>();
			List<R_Line> rLineInfos = new ArrayList<R_Line>();
			for(Station s: prefStations.get()){
				if(lineInfoMap.containsKey(s.id)){
					R_Line rl = new R_Line();
					rl.line_id = s.line_id;
					//rl.line_name = lineInfoMap.get(s.id).line_name;
					rl.line_name = s.line.line_name;
					
					if(!lineHashMap.containsKey(rl.line_name)){
						rLineInfos.add(rl);
						lineHashMap.put(rl.line_name, rl.line_name);
					}
					
				}
			}
			
			return OptionUtil.apply(rLineInfos);
		}
		return new None<List<R_Line>>();
	}
	
    /**
    * 
    * ページ用情報を取得する
    * @param 
    * @return
    */
	public void setResponsePageData(S_SearchResponse respose){
		// 検索結果の件数を取得
		Integer resSize = respose.stations.size();
		Integer pageSize = resSize / S_SearchSetting.LIMIT;
		Integer maxPage = resSize % S_SearchSetting.LIMIT;
		if(pageSize > 0 && maxPage > 0){
			respose.maxPage = pageSize + 1;
		}else{
			respose.maxPage = pageSize;
		}
		
		if(pageSize == 0){
			maxPage = 0;
			respose.maxPage = pageSize;
		}
		
		List<R_Station> stations = new ArrayList<R_Station>();
		Integer getStartCount = 0;
		if(respose.page > 1){
			getStartCount = ((respose.page -1) * S_SearchSetting.LIMIT);
		}
		for(int i = 0; i < S_SearchSetting.LIMIT; i++){
			if(respose.maxPage > 0){
				if(respose.page == respose.maxPage){
					if(i < maxPage){
						stations.add(respose.stations.get(getStartCount));
					}else if(maxPage == 0){
						stations.add(respose.stations.get(getStartCount));
					}
				}else{
					stations.add(respose.stations.get(getStartCount));
				}
			}else{
				if(i < resSize){
					stations.add(respose.stations.get(getStartCount));
				}
			}
			getStartCount ++;
		}
		respose.stations = stations;
	}

}
