package models.request;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidatorContext;

/**
 * 要求基礎クラス
 *
 * @author 
 * @since 
 */
public class BaseRequest {
    /**
     * ページ
     */
    public Integer page;
    
    public Integer action;
}
