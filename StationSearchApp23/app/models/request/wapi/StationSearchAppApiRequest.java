package models.request.wapi;

/**
 * 時刻表リクエストのフォームモデル
 *
 * @author 
 * @since 
 */
public class StationSearchAppApiRequest {
    public String station_id;
    public String kind;
    public String line_name;
    public String direction;
    public String s_day;
    // カウントページ表示区分
    public String result_view;
}
