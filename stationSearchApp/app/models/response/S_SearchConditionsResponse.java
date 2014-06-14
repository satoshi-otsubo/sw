package models.response;

import java.util.List;

/**
 * 検索条件表示用
 *
 * @author 
 * @since 
 */
public class S_SearchConditionsResponse extends BaseResponse{
	public List<R_Prefecturearea> prefectureareas;
    public List<R_Prefecture> prefectures;
}
