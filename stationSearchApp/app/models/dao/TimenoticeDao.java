package models.dao;

import models.entity.Timenotice;
import play.db.ebean.Model;
import play.libs.F.Option;
import utils.ModelUtil;
import utils.OptionUtil;

import java.util.List;

import static play.libs.F.*;

/**
 * Checkモデルのサービスクラス
 *
 * @author 
 * @since 
 */
public class TimenoticeDao implements ModelDao<Timenotice> {

    public static TimenoticeDao use() {
        return new TimenoticeDao();
    }

    /**
     * IDで検索
     * @param id
     * @return
     */
    @Override
    public Option<Timenotice> findById(Long id) {
        Option<Long> idOps = OptionUtil.apply(id);
        if(idOps.isDefined()) {
            Model.Finder<Long, Timenotice> find = ModelUtil.getFinder(Timenotice.class);
            return OptionUtil.apply(
            		find.where().eq("id", id).findUnique());
        }
        return new None<Timenotice>();
    }
    
    /**
     * 駅IDと方面で検索
     * @param station_id,direction
     * @return
     */
    public Option<List<Timenotice>> findByStationIdDirection(Long station_id, String direction) {
    	Model.Finder<Long, Timenotice> find = ModelUtil.getFinder(Timenotice.class);
        return OptionUtil.apply(
        		find.where().eq("station_id", station_id).eq("direction", direction).findList());

    }
    
    
    
    public Option<List<Timenotice>> findAll(){
    	Model.Finder<Long, Timenotice> find = ModelUtil.getFinder(Timenotice.class);
    	return OptionUtil.apply(find.all());
    }
    		
    /**
     * 保存
     * @param entry
     * @return
     */
    @Override
    public Option<Timenotice> save(Timenotice entry) {
        Option<Timenotice> entryOps = OptionUtil.apply(entry);
        if(entryOps.isDefined()) {
            entry.save();
            if(OptionUtil.apply(entry.id).isDefined()) {
                return OptionUtil.apply(entry);
            }
        }
        return new None<Timenotice>();
    }


	@Override
	public Option<List<Timenotice>> findWithPage(Integer pageSource) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
