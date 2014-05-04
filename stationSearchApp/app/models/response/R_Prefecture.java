package models.response;

import java.util.List;

import play.data.validation.Constraints;
import play.libs.F;



/**
 * 検索結果表示用　路線
 *
 * @author 
 * @since 
 */
public class R_Prefecture {
    public Long id;
	public String pref_name;
	public List<R_Line> lines;
}
