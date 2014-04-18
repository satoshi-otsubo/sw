package models.dao;

import models.entity.Station;
import play.db.ebean.Model;
import play.libs.F;
import utils.ModelUtil;
import utils.OptionUtil;
import utils.PageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.libs.F.*;

/**
 * Checkモデルのサービスクラス
 *
 * @author 
 * @since 
 */
public class StationDao implements ModelDao<Station> {

    public static StationDao use() {
        return new StationDao();
    }
    
    // 路線から駅リストを取得する
    public Option<List<Station>> findByLine(Long line_id){
    	Model.Finder<Long, Station> find = ModelUtil.getFinder(Station.class);
        return OptionUtil.apply(
        		find.fetch("line").where().eq("line_id", line_id).orderBy("e_sort").findList());
    }
    
    // 駅名から駅リストを取得する
    public Option<List<Station>> findByName(String station_name){
    	String repStationName = station_name;
    	repStationName = repStationName.replaceAll(" ", ",");
    	repStationName = repStationName.replaceAll("　", ",");
    	String[] whereVals = repStationName.split(",");
    	
    	String whereStr = "";
    	for(String v: whereVals){
    		if(whereStr.length() == 0){
    			whereStr += "station_name like'%" + v + "%' ";
    		}else{
    			whereStr += " or station_name like'%" + v + "%'";
    		}
    	}
    	
    	Model.Finder<Long, Station> find = ModelUtil.getFinder(Station.class);
        return OptionUtil.apply(
        		//find.fetch("line").where().like("station_name", "%" + station_name + "%").findList()
        		find.fetch("line").where().raw(whereStr).findList()
        		);
    }
    
    // 現在地から指定された範囲内の駅を取得する
    public Option<List<Station>> findByGeoCode(
    		Double latitudeFrom, Double longitudeFrom, Double latitudeTo, Double longitudeTo) {

        //Integer page                   = PageUtil.rightPage(pageSource);
        Model.Finder<Long, Station> find = ModelUtil.getFinder(Station.class);
        return OptionUtil.apply(
        		find.fetch("line").where().
        		ge("lat", latitudeTo).le("lat", latitudeFrom).
        		ge("lon", longitudeFrom).le("lon", longitudeTo).
        		findList());
    }
    
    // 駅名、緯度、経度が等しい駅は１つにまとめる
    public Option<List<Station>> distinctByNameLatLon(List<Station> stations){
    	Map<String, Station> stationsMap = new HashMap<String, Station>();
    	List<Station> retlist = new ArrayList<Station>();
    	for(Station station: stations){
    		String id = station.station_name + "_" + String.valueOf(station.lat) + "_" + String.valueOf(station.lon);
    		if(!stationsMap.containsKey(id)){
    			stationsMap.put(id, station);
				stationsMap.get(id).stationIds = new ArrayList<Long>();
				stationsMap.get(id).stationIds.add(station.id);
    		}else{
    			if(stationsMap.get(id).stationIds == null){
    				stationsMap.get(id).stationIds = new ArrayList<Long>();
    				stationsMap.get(id).stationIds.add(station.id);
    			}else{
    				stationsMap.get(id).stationIds.add(station.id);
    			}
    		}
    	}
    	for(Station station: stationsMap.values()){
    		retlist.add(station);
    	}
    	return OptionUtil.apply(retlist);
    }

    /**
     * IDで検索
     * @param id
     * @return
     */
    @Override
    public Option<Station> findById(Long id) {
        Option<Long> idOps = OptionUtil.apply(id);
        if(idOps.isDefined()) {
            Model.Finder<Long, Station> find = ModelUtil.getFinder(Station.class);
            //return OptionUtil.apply(find.byId(id));
            return OptionUtil.apply(
            		find.fetch("line").fetch("line.company").fetch("prefecture").where().eq("id", id).findUnique());
        }
        return new None<Station>();
    }
    
    /**
     * 県で検索
     * @param id
     * @return
     */
    public Option<List<Station>> findByPref(Long id) {
        Option<Long> idOps = OptionUtil.apply(id);
        if(idOps.isDefined()) {
            Model.Finder<Long, Station> find = ModelUtil.getFinder(Station.class);
            return OptionUtil.apply(
            		find.fetch("line").fetch("line.company").fetch("prefecture").where().eq("prefecture_id", id).findList());
        }
        return new None<List<Station>>();
    }
    
    public Option<List<Station>> findAll(){
    	Model.Finder<Long, Station> find = ModelUtil.getFinder(Station.class);
    	return OptionUtil.apply(find.all());
    }
    		
    /**
     * 保存
     * @param entry
     * @return
     */
    @Override
    public Option<Station> save(Station entry) {
        Option<Station> entryOps = OptionUtil.apply(entry);
        if(entryOps.isDefined()) {
            entry.save();
            if(OptionUtil.apply(entry.id).isDefined()) {
                return OptionUtil.apply(entry);
            }
        }
        return new None<Station>();
    }

    /**
     * ページ番号で取得
     * @param pageSource
     * @return
     */
    @Override
    public Option<List<Station>> findWithPage(Integer pageSource) {
        Integer page                   = PageUtil.rightPage(pageSource);
        Model.Finder<Long, Station> find = ModelUtil.getFinder(Station.class);
        return OptionUtil.apply(
                find.order()
                        .asc("createDate")
                        .findPagingList(10)
                        .getPage(page)
                        .getList());
    }

    /**
     * 最大ページ数を取得
     * @return
     */
    public Option<Integer> getMaxPage() {
        Model.Finder<Long, Station> find = ModelUtil.getFinder(Station.class);
        return OptionUtil.apply(
                find.order()
                        .asc("createDate")
                        .findPagingList(10)
                        .getTotalPageCount());
    }

}
