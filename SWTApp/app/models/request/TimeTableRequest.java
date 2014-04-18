package models.request;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidatorContext;

/**
 * 時刻表リクエストのフォームモデル
 *
 * @author 
 * @since 
 */
public class TimeTableRequest {
    public String station_id;
    public String kind;
    public String line_name;
    public String direction;
}
