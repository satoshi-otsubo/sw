package models.response;

import java.util.List;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidatorContext;


/**
 * 検索結果表示用
 *
 * @author 
 * @since 
 */
public class TimeTableResponse extends BaseResponse{
	public String station_id;
	public String station_name;
    public String kind;
    public String line_name;
    public String direction;
    public List<R_TimeTable> timeTables;
}
