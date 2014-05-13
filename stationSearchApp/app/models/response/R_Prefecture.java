package models.response;

import java.util.List;

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
