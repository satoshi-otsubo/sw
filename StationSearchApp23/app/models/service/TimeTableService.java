package models.service;


import utils.OptionUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import common.api.ApiStatusConstants;
import play.libs.F.Option;
import play.libs.Time;
import models.dao.CompanyDao;
import models.dao.StationDao;
import models.dao.TimenoticeDao;
import models.dao.TimetableDao;
import models.entity.Company;
import models.entity.Line;
import models.entity.Station;
import models.entity.Timenotice;
import models.entity.Timetable;
import models.response.R_TimeNotice;
import models.response.R_TimeTable;
import models.response.TimeTableResponse;
import models.response.api.ApiLineInfo;
import models.response.api.LinesApiResponse;

/**
 * サービスクラス
 *
 * @author 
 * @since 
 */
public class TimeTableService {


	// 駅検索結果画面より呼ばれる。時刻表情報を返す。
	public Option<TimeTableResponse> getStationTimeTable(
			Long station_id, Integer kind, String line_name, String direction) throws Exception {
		
		TimeTableResponse result  = new TimeTableResponse();
		result.station_id = station_id.toString();
		result.station_name = StationDao.use().findById(station_id).get().station_name;
		result.kind = kind.toString();
		result.line_name = line_name;
		result.direction = direction; 
		
		//　方面一覧を取得する
		Option<List<Timetable>> optDirectionList = TimetableDao.use().findStationLineDirections(station_id, kind, line_name);
		if(optDirectionList.isDefined()){
			result.directionList = new ArrayList<String>();
			for(Timetable directon: optDirectionList.get()){
				result.directionList.add(directon.direction);
			}
		}

		//Map<String, String> goMap = new HashMap<String, String>();
		// 駅の注意書きを取得
		Option<List<Timenotice>> timenotices = TimenoticeDao.use().findByStationIdDirection(station_id, direction);
		if(timenotices.isDefined()){
			// 列車種別
			result.noticeTrainKinds = getConvTimeNotice(1, kind, timenotices.get());
			// 行き先
			result.noticeDestinations = getConvTimeNotice(2, kind, timenotices.get());
			// マーク
			result.noticeMarks = getConvTimeNotice(3, kind, timenotices.get());
		}
		
		// 駅に時刻リストを取得
		List<R_TimeTable> rTimeTables = new ArrayList<R_TimeTable>();
		Option<List<Timetable>> timeTables = TimetableDao.use().findStationTimeTables(station_id, kind, line_name, direction);
		if(timeTables.isDefined()){
			Integer timeIndex = 0;
			Integer compHour = 0;
			R_TimeTable perTimeTable = null;
			
			for(Timetable t: timeTables.get()){
				R_TimeTable timeTable = new R_TimeTable();
				timeTable.mark = t.mark;
				timeTable.trn = t.trn;
				timeTable.sta = t.sta;
				timeTable.hour = t.hour;
				timeTable.minute = t.minute;
				
				if(t.mark != null && !t.mark.equals("")){
					String mark = t.mark;
					for(R_TimeNotice nMark: result.noticeMarks){
						if(mark.equals(nMark.abbreviation)){
							timeTable.dispMark = nMark.detail;
							if(mark.equals("●")){
								timeTable.dispMark = "[始]";
							}else if(mark.equals("◆")){
								timeTable.dispMark = "[特定]";
							}else{
								timeTable.dispMark = "";
							}
						}
					}
				}
				
				if(t.trn != null && !t.trn.equals("")){
					String trn = t.trn;
					trn = trn.replaceAll(Pattern.quote("["), "");
					trn = trn.replaceAll(Pattern.quote("]"), "");
					for(R_TimeNotice trnKind: result.noticeTrainKinds){
						if(trn.equals(trnKind.abbreviation)){
							timeTable.dispTrainKind = trnKind.detail;
						}
					}
				}else{
					String trn = "無印";
					for(R_TimeNotice trnKind: result.noticeTrainKinds){
						if(trn.equals(trnKind.abbreviation)){
							timeTable.dispTrainKind = trnKind.detail;
						}
					}
				}
				
				if(t.sta != null && !t.sta.equals("")){
					String sta = t.sta;
					for(R_TimeNotice destination: result.noticeDestinations){
						if(sta.equals(destination.abbreviation)){
							timeTable.dispDestination = destination.detail;
						}
					}
				}else{
					String sta = "無印";
					for(R_TimeNotice destination: result.noticeDestinations){
						if(sta.equals(destination.abbreviation)){
							timeTable.dispDestination = destination.detail;
						}
					}
				}
				
				timeTable.timeIndex = timeIndex;
				// 比較用時間
				if(compHour == t.hour){
					if(perTimeTable.detailTimeTables == null){
						perTimeTable.detailTimeTables = new ArrayList<R_TimeTable>();
					}
					perTimeTable.detailTimeTables.add(timeTable);
				}else{
					rTimeTables.add(timeTable);
					perTimeTable = timeTable;
					
					if(perTimeTable.detailTimeTables == null){
						perTimeTable.detailTimeTables = new ArrayList<R_TimeTable>();
					}
					perTimeTable.detailTimeTables.add(timeTable);
				}
				compHour = t.hour;
				timeIndex ++;
			}
			result.timeTables = rTimeTables;
			result.totalTimes = timeTables.get().size();
		}
		return OptionUtil.apply(result); 
	}
	
	// 注意書き部分を取得する(列車種別、行き先、始発など)
	private List<R_TimeNotice> getConvTimeNotice(Integer noticeNo, Integer kind, List<Timenotice> timeNoticeList){
		List<R_TimeNotice> retList = new ArrayList<R_TimeNotice>();
		for(Timenotice timenotice: timeNoticeList){
			if(timenotice.notice.equals(noticeNo)){
				Integer kindCount = 0;
				String[] contentsArray = timenotice.contents.split(",");
				for(String content: contentsArray){
					// 曜日種類で取得する部分を変更する
					if(content.matches(".*" + "timeNotice" + ".*")){
						kindCount ++;
					}
					if(kind == 1 && kindCount == 1){
						setNotice(retList, content, noticeNo);
					}else if(kind == 2 && kindCount == 2){
						setNotice(retList, content, noticeNo);
					}else if(kind == 4 && kindCount == 3){
						setNotice(retList, content, noticeNo);
					}
				}
			}

		}
		return retList;
	}
	
	private void setNotice(List<R_TimeNotice> rTimeNotices, String content, Integer noticeNo){
		if(content.matches(".*" + "dt" + ".*")){
			if(content.matches(".*" + "dd" + ".*")){
				R_TimeNotice rTimeNotice = new R_TimeNotice();
				int sNo = content.indexOf("<dt>");
				int eNo = content.indexOf("</dt>");
				
				if(noticeNo == 1){
					if(sNo < 0){
						sNo = content.indexOf("：") - 1;
						rTimeNotice.abbreviation = content.substring(sNo, eNo - 1);
					}else{
						rTimeNotice.abbreviation = content.substring(sNo + 4, eNo - 1);
					}
				}else{
					rTimeNotice.abbreviation = content.substring(sNo + 4, eNo - 1);
				}
				sNo = content.indexOf("<dd>");
				eNo = content.indexOf("</dd>");
				rTimeNotice.detail = content.substring(sNo + 4, eNo);
				
				// 注意表示（マーク）の場合だけちょっと加工する
				if(noticeNo == 3){
					if(rTimeNotice.detail.length() > 0){
						if(rTimeNotice.detail.matches(".*" + ">" + ".*")){
							sNo = rTimeNotice.detail.indexOf(">");
							eNo = rTimeNotice.detail.indexOf("</a>");
							rTimeNotice.detail = rTimeNotice.detail.substring(sNo + 1, eNo);
						}
					}
				}
				rTimeNotices.add(rTimeNotice);
			}
		}
	}
	
    /**
    *
    * 路線名から路線情報を取得する
    * @param 
    * @return
    */
	public Option<LinesApiResponse> getLinesByLineName(String line_name) throws Exception {
		LinesApiResponse result  = new LinesApiResponse();
		//　路線名から駅一覧を取得する
		Option<List<Timetable>> optStationList = TimetableDao.use().findStationsByLineName(1, line_name);
		if(optStationList.isDefined()){
			// 駅IDリストから駅を取得する
			List<Long> stationIds = new ArrayList<Long>();
			for(Timetable tt : optStationList.get()){
				stationIds.add(tt.station_id);
			}
			Option<List<Station>> optStationInfoList = StationDao.use().findByIds(stationIds);
			if(optStationInfoList.isDefined()){
				// 駅情報の路線名をみて、返却する路線情報リストを作成する
				List<ApiLineInfo> lineInfos = new ArrayList<ApiLineInfo>();
				Map<Long, Line> lineInfoMap = new HashMap<Long, Line>();  //路線情報を一意にするための辞書
				for(Station st: optStationInfoList.get()){
					if(!lineInfoMap.containsKey(st.line_id)){
						// 返却データを作成
						ApiLineInfo lineInfo = new ApiLineInfo();
						lineInfo.line_id = st.line.id;
						lineInfo.line_name = st.line.line_name;
						lineInfos.add(lineInfo);
						result.lineInfos = lineInfos;
						
						lineInfoMap.put(st.line_id, st.line);
					}
				}
    			result.code = ApiStatusConstants.OK.getCode();
    			result.status = ApiStatusConstants.OK.getMessage();
			}
		}
		return OptionUtil.apply(result); 
	}

}
