package models.response;

import java.util.List;

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
