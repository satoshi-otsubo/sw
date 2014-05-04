package models.dao;

import models.entity.Line;
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
public class LineDao implements ModelDao<Line> {

    public static LineDao use() {
        return new LineDao();
    }


    /**
     * IDで検索
     * @param id
     * @return
     */
    @Override
    public Option<Line> findById(Long id) {
        Option<Long> idOps = OptionUtil.apply(id);
        if(idOps.isDefined()) {
            Model.Finder<Long, Line> find = ModelUtil.getFinder(Line.class);
            return OptionUtil.apply(
            		find.fetch("company").where().eq("id", id).findUnique());
        }
        return new None<Line>();
    }
    
    public Option<List<Line>> findAll(){
    	Model.Finder<Long, Line> find = ModelUtil.getFinder(Line.class);
    	return OptionUtil.apply(find.all());
    }
    		
    /**
     * 保存
     * @param entry
     * @return
     */
    @Override
    public Option<Line> save(Line entry) {
        Option<Line> entryOps = OptionUtil.apply(entry);
        if(entryOps.isDefined()) {
            entry.save();
            if(OptionUtil.apply(entry.id).isDefined()) {
                return OptionUtil.apply(entry);
            }
        }
        return new None<Line>();
    }


	@Override
	public Option<List<Line>> findWithPage(Integer pageSource) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
