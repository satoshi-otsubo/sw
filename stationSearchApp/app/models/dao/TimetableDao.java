package models.dao;

import models.entity.Timetable;
import play.db.ebean.Model;
import play.libs.F.Option;
import utils.ModelUtil;
import utils.OptionUtil;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

import static play.libs.F.*;

/**
 * Checkモデルのサービスクラス
 *
 * @author 
 * @since 
 */
public class TimetableDao implements ModelDao<Timetable> {

    public static TimetableDao use() {
        return new TimetableDao();
    }

    /**
     * IDで検索
     * @param id
     * @return
     */
    @Override
    public Option<Timetable> findById(Long id) {
        Option<Long> idOps = OptionUtil.apply(id);
        if(idOps.isDefined()) {
            Model.Finder<Long, Timetable> find = ModelUtil.getFinder(Timetable.class);
            return OptionUtil.apply(
            		find.where().eq("id", id).findUnique());
        }
        return new None<Timetable>();
    }
    
    public Option<List<Timetable>> findAll(){
    	Model.Finder<Long, Timetable> find = ModelUtil.getFinder(Timetable.class);
    	return OptionUtil.apply(find.all());
    }
    		
    /**
     * 保存
     * @param entry
     * @return
     */
    @Override
    public Option<Timetable> save(Timetable entry) {
        Option<Timetable> entryOps = OptionUtil.apply(entry);
        if(entryOps.isDefined()) {
            entry.save();
            if(OptionUtil.apply(entry.id).isDefined()) {
                return OptionUtil.apply(entry);
            }
        }
        return new None<Timetable>();
    }
    
    /**
     * 駅IDで取得
     * @param stationId
     * @return　Option<List<Timetable>>
     */
    public Option<List<Timetable>> findByStationId(Long stationId){
    	Model.Finder<Long, Timetable> find = ModelUtil.getFinder(Timetable.class);
    	return OptionUtil.apply(find.where().eq("station_id", stationId).findList());
    }
    
    /**
     * 削除
     * @param Timetable
     * @return　なし
     */
    public void delete(Timetable timeTable){
    	timeTable.delete();
    }

    // 駅idから路線と方面を取得する。複数存在する情報は一つにまとめる
	public Option<List<Timetable>> findStationLineDirections2(List<Long> stationIds) {
		String sql = "select distinct line_name, direction from timetable";

		sql += " where station_id in (";
		int iCount = 0;
		for(Long id: stationIds){
			if(iCount > 0){
				sql += ",";
			}
			sql += id;
			iCount ++;
		}
		sql += ")";

		List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
		List<Timetable> retTimeTables = new ArrayList<Timetable>();
		for(SqlRow row: sqlRows){
			Timetable tt = new Timetable();
			tt.line_name = row.getString("line_name");
			tt.direction = row.getString("direction");
			retTimeTables.add(tt);
		}
		return OptionUtil.apply(retTimeTables);
	}
	
	public Option<List<Timetable>> findStationLineDirections(List<Long> stationIds) {
		Model.Finder<Long, Timetable> find = ModelUtil.getFinder(Timetable.class);
		// 件数を減らすため、平日データを取得
        return OptionUtil.apply(find.where().eq("kind", 1).in("station_id", stationIds).findList());
	}

	// 駅時刻表を取得
	public Option<List<Timetable>> findStationTimeTables(Long station_id, Integer kind, String line_name, String direction){
		Model.Finder<Long, Timetable> find = ModelUtil.getFinder(Timetable.class);
		return OptionUtil.apply(
				find.where().
				eq("station_id", station_id).
				eq("kind", kind).
				eq("line_name", line_name).
				eq("direction", direction).
				orderBy("hour, minute").
				findList());
	}
	
    // 駅idから駅idと路線名取得する。複数存在する情報は一つにまとめる
	public Option<List<Timetable>> findStationLineNameList(List<Long> stationIds) {
		//String sql = "select distinct station_id, line_name from timetable";
		
		String sql = "select station_id, line_name from timetable";
		sql += " where station_id in (";
		int iCount = 0;
		for(Long id: stationIds){
			if(iCount > 0){
				sql += ",";
			}
			sql += id;
			iCount ++;
		}
		sql += ")";
		sql += " and kind = 1";
		List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).setMaxRows(10000000).findList();
		
		
		/*
		Model.Finder<Long, Timetable> find = ModelUtil.getFinder(Timetable.class);
		List<Timetable> sqlRows = find.where().in("station_id", stationIds).eq("kind", 1).findList();
		*/
		List<Timetable> retTimeTables = new ArrayList<Timetable>();
//		Map<String, String> hashStationLine = new HashMap<String, String>();
		for(SqlRow row: sqlRows){
		//for(Timetable row: sqlRows){
			// 駅IDと路線名が同じものは結果に加えない
			//String putKey = String.valueOf(row.getLong("station_id")) + "_" + row.getString("line_name");
/*
			String putKey = String.valueOf(row.station_id) + "_" + row.line_name;
			if(!hashStationLine.containsKey(putKey)){
				System.out.println(putKey + ": を追加します。");
				hashStationLine.put(putKey, putKey);
				Timetable tt = new Timetable();
				//tt.station_id = row.getLong("station_id");
				//tt.line_name = row.getString("line_name");
				tt.station_id = row.station_id;
				tt.line_name = row.line_name;
				retTimeTables.add(tt);
			}
*/
			Timetable tt = new Timetable();
			tt.station_id = row.getLong("station_id");
			tt.line_name = row.getString("line_name");
			retTimeTables.add(tt);
		}
		return OptionUtil.apply(retTimeTables);
	}	
	
	// 駅の方面を取得する
	public Option<List<Timetable>> findStationLineDirections(Long station_id, Integer kind, String line_name){
		String sql = "select distinct direction from timetable";

		sql += " where station_id = " + station_id;
		sql += " and kind = " + kind;
		sql += " and line_name = '" + line_name + "'";
		
		List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
		List<Timetable> retTimeTables = new ArrayList<Timetable>();
		for(SqlRow row: sqlRows){
			Timetable tt = new Timetable();
			tt.direction = row.getString("direction");
			retTimeTables.add(tt);
		}
		return OptionUtil.apply(retTimeTables);
	}
	
	
    /**
    *
    * 路線名から駅IDを取得する
    * @param 
    * @return
    */
	public Option<List<Timetable>> findStationsByLineName(Integer kind, String line_name) {
		String sql = "select distinct station_id from timetable";
		sql += " where kind = " + kind;
		sql += " and line_name = '" + line_name + "'";
		
		List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
		List<Timetable> retTimeTables = new ArrayList<Timetable>();
		for(SqlRow row: sqlRows){
			Timetable tt = new Timetable();
			tt.station_id = row.getLong("station_id");
			retTimeTables.add(tt);
		}
		return OptionUtil.apply(retTimeTables);
	}
	
	
	@Override
	public Option<List<Timetable>> findWithPage(Integer pageSource) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
