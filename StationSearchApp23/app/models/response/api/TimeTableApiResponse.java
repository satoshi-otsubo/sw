package models.response.api;

import java.util.List;

import models.response.R_TimeNotice;

/**
 * 
 *
 * @author 
 * @since 
 */
public class TimeTableApiResponse extends BaseApiResponse{
	public String station_id;
	public String station_name;
    public String kind;
    public String line_name;
    public String direction;
    public List<String> directionList;
    public Integer totalTimes;
    public List<R_TimeNotice> noticeTrainKinds;
    public List<R_TimeNotice> noticeDestinations;
    public List<R_TimeNotice> noticeMarks;
    public List<ApiTimeInfo> timeInfos;
}
