package models.dao;

import play.db.ebean.Model;

import java.util.List;

import static play.libs.F.*;

/**
 * モデルインターフェース
 *
 * @author 
 * @since 
 */
public interface ModelDao<T extends Model> {

    public Option<T> findById(Long id);
    public Option<T> save(T entry);
    public Option<List<T>> findWithPage(Integer pageSource);

}
