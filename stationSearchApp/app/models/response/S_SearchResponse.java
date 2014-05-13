package models.response;

import java.util.List;

/**
 * 検索結果表示用
 *
 * @author 
 * @since 
 */
public class S_SearchResponse extends BaseResponse{
    public String station_name;
    public String keyword;
    public String lat;
    public String lon;
    public String area;
    public String prefecture;
    public String line;
    public String line_id;
    public List<R_Station> stations;
}
