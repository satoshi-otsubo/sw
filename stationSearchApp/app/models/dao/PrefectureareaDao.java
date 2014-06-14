package models.dao;

import play.db.ebean.Model;
import play.libs.F.Option;
import utils.ModelUtil;
import utils.OptionUtil;

import java.util.List;

import models.entity.Prefecture;
import models.entity.Prefecturearea;
import static play.libs.F.*;

/**
 * Checkモデルのサービスクラス
 *
 * @author 
 * @since 
 */
public class PrefectureareaDao implements ModelDao<Prefecturearea> {

    public static PrefectureareaDao use() {
        return new PrefectureareaDao();
    }


    /**
     * IDで検索
     * @param id
     * @return
     */
    @Override
    public Option<Prefecturearea> findById(Long id) {
        Option<Long> idOps = OptionUtil.apply(id);
        if(idOps.isDefined()) {
            Model.Finder<Long, Prefecturearea> find = ModelUtil.getFinder(Prefecturearea.class);
            return OptionUtil.apply(
            		find.where().eq("id", id).findUnique());
        }
        return new None<Prefecturearea>();
    }
    
    public Option<List<Prefecturearea>> findAll(){
    	Model.Finder<Long, Prefecturearea> find = ModelUtil.getFinder(Prefecturearea.class);
    	return OptionUtil.apply(
    			find.fetch("prefectures").where().orderBy("prefectures.id").findList()
    			);
    	//return OptionUtil.apply(find.all());
    }
    		
    /**
     * 保存
     * @param entry
     * @return
     */
    @Override
    public Option<Prefecturearea> save(Prefecturearea entry) {
        Option<Prefecturearea> entryOps = OptionUtil.apply(entry);
        if(entryOps.isDefined()) {
            entry.save();
            if(OptionUtil.apply(entry.id).isDefined()) {
                return OptionUtil.apply(entry);
            }
        }
        return new None<Prefecturearea>();
    }


	@Override
	public Option<List<Prefecturearea>> findWithPage(Integer pageSource) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
