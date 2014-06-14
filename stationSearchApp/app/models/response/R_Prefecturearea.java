package models.response;

import java.util.List;

/**
 * 検索結果表示用　都道府県地域
 *
 * @author 
 * @since 
 */
public class R_Prefecturearea {
    public Long id;
	public String area_name;
	public List<R_Prefecture> prefectures;
}
