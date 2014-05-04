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
public class R_Station {
	public String station_name;
	public Double distance;
	public Double lon;
	public Double lat;
	public Integer sort;
    
    public List<R_LineDirection> lineDirections;
}
