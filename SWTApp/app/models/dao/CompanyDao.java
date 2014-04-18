package models.dao;

import models.entity.Company;
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
public class CompanyDao implements ModelDao<Company> {

    public static CompanyDao use() {
        return new CompanyDao();
    }


    /**
     * IDで検索
     * @param id
     * @return
     */
    @Override
    public Option<Company> findById(Long id) {
        Option<Long> idOps = OptionUtil.apply(id);
        if(idOps.isDefined()) {
            Model.Finder<Long, Company> find = ModelUtil.getFinder(Company.class);
            return OptionUtil.apply(
            		find.where().eq("id", id).findUnique());
        }
        return new None<Company>();
    }
    
    public Option<List<Company>> findAll(){
    	Model.Finder<Long, Company> find = ModelUtil.getFinder(Company.class);
    	return OptionUtil.apply(find.all());
    }
    		
    /**
     * 保存
     * @param entry
     * @return
     */
    @Override
    public Option<Company> save(Company entry) {
        Option<Company> entryOps = OptionUtil.apply(entry);
        if(entryOps.isDefined()) {
            entry.save();
            if(OptionUtil.apply(entry.id).isDefined()) {
                return OptionUtil.apply(entry);
            }
        }
        return new None<Company>();
    }


	@Override
	public Option<List<Company>> findWithPage(Integer pageSource) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
