package models.response;

import java.util.List;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidatorContext;


/**
 * 検索結果表示用　路線
 *
 * @author 
 * @since 
 */
public class R_Line {
	public Long line_id;
    public String line_name;
}
