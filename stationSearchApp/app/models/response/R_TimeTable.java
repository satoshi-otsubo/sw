package models.response;

import java.util.List;

public class R_TimeTable {
	public String mark;
	public String trn;
	public String sta;
	public Integer hour;
	public Integer minute;
	
    public String dispTrainKind;
    public String dispDestination;
    public String dispMark;
	
	public Integer timeIndex;
	public List<R_TimeTable> detailTimeTables;
}
