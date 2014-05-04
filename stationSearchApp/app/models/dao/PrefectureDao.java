package models.dao;

import play.db.ebean.Model;
import play.libs.F.Option;
import utils.ModelUtil;
import utils.OptionUtil;

import java.util.List;

import models.entity.Prefecture;
import static play.libs.F.*;

/**
 * Checkモデルのサービスクラス
 *
 * @author 
 * @since 
 */
public class PrefectureDao implements ModelDao<Prefecture> {

    public static PrefectureDao use() {
        return new PrefectureDao();
    }


    /**
     * IDで検索
     * @param id
     * @return
     */
    @Override
    public Option<Prefecture> findById(Long id) {
        Option<Long> idOps = OptionUtil.apply(id);
        if(idOps.isDefined()) {
            Model.Finder<Long, Prefecture> find = ModelUtil.getFinder(Prefecture.class);
            return OptionUtil.apply(
            		find.where().eq("id", id).findUnique());
        }
        return new None<Prefecture>();
    }
    
    public Option<List<Prefecture>> findAll(){
    	Model.Finder<Long, Prefecture> find = ModelUtil.getFinder(Prefecture.class);
    	return OptionUtil.apply(find.all());
    }
    		
    /**
     * 保存
     * @param entry
     * @return
     */
    @Override
    public Option<Prefecture> save(Prefecture entry) {
        Option<Prefecture> entryOps = OptionUtil.apply(entry);
        if(entryOps.isDefined()) {
            entry.save();
            if(OptionUtil.apply(entry.id).isDefined()) {
                return OptionUtil.apply(entry);
            }
        }
        return new None<Prefecture>();
    }


	@Override
	public Option<List<Prefecture>> findWithPage(Integer pageSource) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
