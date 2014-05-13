package models.response;

import java.util.List;

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
    public String line_id;
    public String direction;
    public List<String> directionList;
    public Integer totalTimes;
    public List<R_TimeNotice> noticeTrainKinds;
    public List<R_TimeNotice> noticeDestinations;
    public List<R_TimeNotice> noticeMarks;
    public List<R_TimeTable> timeTables;
}
