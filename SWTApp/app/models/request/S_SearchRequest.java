package models.request;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidatorContext;

/**
 * 検索リクエストのフォームモデル
 *
 * @author 
 * @since 
 */
public class S_SearchRequest extends BaseRequest{
    public String station_name;
    public String keyword;
    public String lat;
    public String lon;
    public String area;
    public String prefecture;
    public String line_id;
}
