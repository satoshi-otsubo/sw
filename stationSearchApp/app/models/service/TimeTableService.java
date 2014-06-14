package models.service;


import utils.OptionUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import common.api.ApiStatusConstants;

import play.libs.F.Option;
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
	
	private Map<String, String> company_conv_map = new HashMap<String, String>();
	private Map<String, String> line_conv_map = new HashMap<String, String>();
	private Map<String, String> station_conv_map = new HashMap<String, String>();
	private Map<String, String> station_line_conv_map = new HashMap<String, String>();
	
	public void setTimeTable() {		
        System.out.println("===== service Start =====");
        
        // 会社名を変換するマップをセット
        company_conv_map.put("JR北海道", "JR");
        company_conv_map.put("JR東日本", "JR");
        company_conv_map.put("JR東海", "JR");
        company_conv_map.put("JR西日本", "JR");
        company_conv_map.put("JR四国", "JR");
        company_conv_map.put("JR九州", "JR");
        company_conv_map.put("東京都交通局", "都営");
        company_conv_map.put("名古屋市交通局", "名古屋市営");
        company_conv_map.put("埼玉高速鉄道", "");
        company_conv_map.put("京王電鉄", "");
        company_conv_map.put("京急電鉄", "");
        company_conv_map.put("京成電鉄", "");
        company_conv_map.put("西武鉄道", "");
        company_conv_map.put("多摩都市モノレール", "");
        company_conv_map.put("ゆりかもめ", "");
        
        // 路線を変換するマップをセット
        line_conv_map.put("東武伊勢崎線", "東武伊勢崎線(東武スカイツリーライン)");
        line_conv_map.put("東武野田線", "東武野田線(東武アーバンパークライン)");
        line_conv_map.put("埼玉高速鉄道線", "埼玉高速鉄道");
        line_conv_map.put("日暮里・舎人ライナー", "舎人ライナー");
        line_conv_map.put("JR総武本線", "JR総武線快速");
        line_conv_map.put("小田急線", "小田急小田原線");
        line_conv_map.put("東京モノレール", "東京モノレール羽田空港線");
        
        // 駅を変換するマップをセット
        station_conv_map.put("鳩ヶ谷", "鳩ケ谷");
        station_conv_map.put("霞ヶ関", "霞ケ関");
        station_conv_map.put("押上〈スカイツリー前〉", "押上");
        station_conv_map.put("押上（スカイツリー前）", "押上");
        station_conv_map.put("幡ヶ谷", "幡ケ谷");
        station_conv_map.put("明治神宮前〈原宿〉", "明治神宮前");
        
        // 特定の駅の場合に路線を変換する
        //station_line_conv_map.put("初台", "京王新線");
        //station_line_conv_map.put("幡ケ谷", "京王新線");
        //station_line_conv_map.put("笹塚", "京王");
        
        //Option<List<Station>> opts = StationDao.use().findAll();
        Option<List<Station>> opts = StationDao.use().findByPref(new Long(13));
        
        int iCount = 0;
        for(Station s: opts.get()){

        	// 特定駅名から路線名変換
        	if(station_line_conv_map.containsKey(s.station_name)){
            	// 駅名変換
            	if(station_conv_map.containsKey(s.station_name)){
            		s.station_name = station_conv_map.get(s.station_name);
            	}
        		s.line.line_name = station_line_conv_map.get(s.station_name);
        	}else{
        		s = StationDao.use().findById(s.id).get();
            	// 駅名変換
            	if(station_conv_map.containsKey(s.station_name)){
            		s.station_name = station_conv_map.get(s.station_name);
            	}
        	}
        	
        	// 会社名変換
        	if(company_conv_map.containsKey(s.line.company.company_name)){
        		s.line.company.company_name_r = company_conv_map.get(s.line.company.company_name);
        	}
        	
        	// 路線名変換
        	if(line_conv_map.containsKey(s.line.line_name)){
        		s.line.line_name = line_conv_map.get(s.line.line_name);
        	}
        	
        	//if(s.id == 9930136){
        		
            System.out.println("No." + iCount + " この駅を取得：" + s.station_name + " 会社：" + s.line.company.company_name_r + " 元路線：" + s.line.line_name);
    		List<Timetable> timeTableList = new ArrayList<Timetable>();
    		List<Timetable> timeTableLastSetList = new ArrayList<Timetable>();
            // 駅番号取得
            String stationNo = stationNoSearch(s);
            System.out.println("駅番号：" + stationNo);
            if(stationNo != null){
            	timeTableList = lineDirectionSearch(stationNo, s);
            	System.out.println("路線・方面件数：" + timeTableList.size());
            	for(Timetable t: timeTableList){
            		System.out.println("路線URL：" + t.line_name + " 方面名：" + t.direction);
            	}
            	
        		List<Timetable> tt1 = null;
        		List<Timetable> tt2 = null;
        		List<Timetable> tt4 = null;

            	for(Timetable t: timeTableList){
            		// 注意書きの取得追加
            		Map<Integer, Timenotice> timeNoticeMap = new HashMap<Integer, Timenotice>();
            		t.station_id = s.id;
            		tt1 = getTimeTableList(t, "1", timeNoticeMap);
            		setTimeTableList(timeTableLastSetList, tt1);
            		saveTimenotice(timeNoticeMap);
            		
            		tt2 = getTimeTableList(t, "2", timeNoticeMap);
            		setTimeTableList(timeTableLastSetList, tt2);
            		saveTimenotice(timeNoticeMap);
            
            		tt4 = getTimeTableList(t, "4", timeNoticeMap);
            		setTimeTableList(timeTableLastSetList, tt4);
            		saveTimenotice(timeNoticeMap);
            		
            	}       	
            }
            Timetable logTimetable = null;
    		for(Timetable t: timeTableLastSetList){
    			//System.out.println("stationId:" + t.station_id + " 曜日種類:" + t.kind +" 路線：" + t.line_name + " 方面:" + t.direction + " 時：" + t.hour + " 分：" + t.minute);
    			TimetableDao.use().save(t);
    			logTimetable = t;
    		}
    		
    		if(logTimetable == null){
    			logTimetable = new Timetable();
    		}
    		System.out.println("登録路線：" + logTimetable.line_name + " 登録件数：" + timeTableLastSetList.size());
    		System.out.println(s.station_name + " の取得終了");
    		
        	//}
    		iCount ++;
        	
        }
        
        System.out.println("===== service End =====");

	}
	
	private String stationNoSearch(Station s){
    	String startUrl = "http://transit.loco.yahoo.co.jp/station/time/search?q=";
    	String station_name = s.station_name;
    	List<String[]> stationList = new ArrayList<String[]>();
    	String retStationNo = null;
    	try {
    		URL url = new URL(startUrl + station_name);
    		HttpURLConnection connection = null;
    		try {
	            connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("GET");
	            // 駅名一致フラグ
	            //boolean esFlg = false; 
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                //String stationNo = null;
	            	try (InputStreamReader isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
	                BufferedReader reader = new BufferedReader(isr)) {
	                	String line;
	                	boolean onlyStation = false;
	                    while ((line = reader.readLine()) != null) {
	                    	// 時刻表が存在しない場合、終了
	                    	if(line.matches(".*" + "該当する時刻表はありません。" + ".*")){
	                    		retStationNo = null;
	                    		break;
		                    	//return null;
	                    	}
	                    	// 駅が一回で確定出来た場合は以下の処理を行わず駅コードを返す
	                    	if(line.matches(".*" + "<title>" + ".*")){
		                    	if(line.matches(".*" + "路線一覧" + ".*")){
		                    		onlyStation = true;
		                    	}
	                    	}
	                    	if(line.matches(".*" + "/station/top/" + ".*")){
	                    		int s1 = line.indexOf("top/");
	                    		int s2 = line.indexOf("/?");
	                    		retStationNo = line.substring(s1+ 4, s2);
	                    		break;
	                    		//return line.substring(s1+ 4, s2);
	                    	}
	                    	if(!onlyStation){
	                    		// 駅のリストを取得
		                    	String[] stationInfo = getStationInfo(line, station_name);
		                    	if(stationInfo != null){
		                    		stationList.add(stationInfo);
		                    	}
	                    	}
	                    }
	            	}
	            }
    		}finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
    	
    	if(stationList.size() > 0){
    		String stationNo = getStationNo(stationList, station_name, s);
    		if(stationNo != null){
    			return stationNo;
    		}
    	}
    	
        //return null;
    	return retStationNo;
    }
	
	private String[] getStationInfo(String lineStr, String station_name){
		String[] stationInfo = null;
    	if(lineStr.matches(".*" + "<li><a href=\"/station/rail/" + ".*")){
    		//System.out.println(lineStr);
    		int s1 = lineStr.indexOf("\">");
    		int s2 = lineStr.indexOf("</a>");
    		String sn = lineStr.substring(s1+ 2, s2);
    		//System.out.println(sn);

    		int n1 = lineStr.indexOf("rail/");
    		int n2 = lineStr.indexOf("/?");
    		String nn = lineStr.substring(n1+ 5, n2);
    		//System.out.println(nn);
    		stationInfo = new String[2];
    		stationInfo[0] = sn;
    		stationInfo[1] = nn;
    		
    	} 
    	return stationInfo;
	}
	
	private String getStationNo(List<String[]> stationList, String station_name, Station s){
		boolean matchFlg = false;
		for(String[] stationInfo: stationList){
			String info_name = stationInfo[0];
			// (が存在するか
			if(info_name.matches(".*" + "\\(" + ".*")){
	    		int s1 = info_name.indexOf("(");
	    		int s2 = info_name.indexOf(")");
	    		// 括弧の中身
	    		String info_name_option = info_name.substring(s1+ 1, s2);
	    		// かっこの前の駅名が一致するか
	    		//前方一致で検索に変更
	    		//if(info_name.matches(".*" + station_name + ".*")){
	    		if(info_name.startsWith(station_name)){
	    			// 駅の都道府県
	    			String pref_name = s.prefecture.pref_name;
	    			// 括弧の中身が都道府県名と一致するか(完全一致)
	    			//if(info_name_option.matches(".*" + pref_name + ".*")){
	    			if(info_name_option.equals(pref_name)){
	    			matchFlg = true;
	    				return stationInfo[1];
	    			}
	    			// 括弧の中身が鉄道会社名と一致するか(変換前の情報と一致するか)
	    			if(!matchFlg){
	    		        Option<Company> opt = CompanyDao.use().findById(s.line.company_id);
	    		        Company c = opt.get();
	    				if(info_name_option.matches(".*" + c.company_name_r + ".*")){
	    					matchFlg = true;
	    					return stationInfo[1];
	    				}
	    			}
	    			// 括弧の中身が鉄道会社名と一致するか(変換後の情報と一致するか)
	    			if(!matchFlg){
	    				if(!s.line.company.company_name_r.equals("")){
		    				if(info_name_option.matches(".*" + s.line.company.company_name_r + ".*")){
		    					matchFlg = true;
		    					return stationInfo[1];
		    				}
	    				}
	    			}	    			
	    			// 括弧の中身が路線名と一致するか
	    			if(!matchFlg){
	    		        String ln = s.line.line_name;
	    		        if(!ln.equals("")){
		    				if(info_name_option.matches(".*" + ln + ".*")){
		    					matchFlg = true;
		    					return stationInfo[1];
		    				}
	    		        }

	    				// 会社略名を削除して一致するか
	    				String cnr = s.line.company.company_name_r;
	    				ln = ln.replaceAll(cnr, "");
	    				if(!ln.equals("")){
		    				if(info_name_option.matches(".*" + ln + ".*")){
		    					matchFlg = true;
		    					return stationInfo[1];
		    				}
	    				}
	    			}
	    		}
			}
		}
		
		for(String[] stationInfo: stationList){
			// 駅名が一致するか
			//前方一致で検索に変更
			//if(stationInfo[0].matches(".*" + station_name + ".*")){
			if(stationInfo[0].startsWith(station_name)){
				matchFlg = true;
				return stationInfo[1];
			}
		}
		return null;
	}
    
	private List<Timetable> lineDirectionSearch(String stationNo, Station s){
		List<Timetable> retDirectionInfo = new ArrayList<Timetable>();
		try {
        	String startUrl = "http://transit.loco.yahoo.co.jp/station/rail/";
        	URL url = new URL(startUrl + stationNo + "/?done=time");
        	
        	HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStreamReader isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
                        BufferedReader reader = new BufferedReader(isr)) {
                        String line;
                        String bLineName = null;
                        while ((line = reader.readLine()) != null) {
                        	String lineName = null;
                        	
                           	if(bLineName != null){
                        		// 方面情報を取得
                        		List<Timetable> directionInfo = setDirectionInfo(line, bLineName);
                        		if(directionInfo != null && directionInfo.size() > 0){
                        			for(Timetable t: directionInfo){
                        				retDirectionInfo.add(t);
                        			}
                        		}
                        		bLineName = null;
                        	}
                        	
                           	lineName = getLineName(line, s);

                        	if(lineName != null){
                        		bLineName = lineName;
                        	}

                        	if(line.matches(".*" + "</div><!--/#station-line-select end-->" + ".*")){
                        		break;
                        	}
                        }
                    }
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        	
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return retDirectionInfo;
	}
	
	private String getLineName(String lineStr, Station station){
		boolean eLineFlg = false;
		String lineName = null;
    	if(lineStr.matches(".*" + "<dt>" + ".*")){
    		int r1 = lineStr.indexOf("<dt>");
    		int r2 = lineStr.indexOf("</dt>");
    		lineName = lineStr.substring(r1+ 4, r2);
    		//System.out.println(lineName);
    	}
    	
    	if(lineName != null){
    		String aLineName = station.line.line_name;
    		String aLineName_h = station.line.line_name_h;
    		Timetable timeTable = null;
    		
    		// 路線名が一致するものを探す
    		if(lineName.matches(".*" + aLineName + ".*")){
    			eLineFlg = true;
    			timeTable = new Timetable();
    			timeTable.line_name = lineName;
    			//timeTableList.add(timeTable);
    			return lineName;
    		}
    		
    		// 路線名(完全名)が一致するものを探す
    		if(!eLineFlg){
    			if(lineName.matches(".*" + aLineName_h + ".*")){
        			eLineFlg = true;
        			timeTable = new Timetable();
        			timeTable.line_name = lineName;
        			//timeTableList.add(timeTable);
        			return lineName;
    			}
    		}
    		//会社名（略名）を取得
    		String campanyName = station.line.company.company_name_r;
    		// 路線名から会社名略称を消した名前が一致するか
    		if(!eLineFlg){
    			String tLineName = aLineName.replaceAll(campanyName, "");
    			String t2LineName = aLineName_h.replaceAll(campanyName, "");
        		if(lineName.matches(".*" + tLineName + ".*")){
        			
        			// 関係ない会社の路線でないかを判断
        			if(reigaiLineName(lineName, campanyName)){
            			eLineFlg = true;
            			timeTable = new Timetable();
            			timeTable.line_name = lineName;
            			return lineName;
        			}

        		}
    			if(lineName.matches(".*" + t2LineName + ".*")){
        			// 関係ない会社の路線でないかを判断
    				if(reigaiLineName(lineName, campanyName)){	 
	        			eLineFlg = true;
	        			timeTable = new Timetable();
	        			timeTable.line_name = lineName;
	        			return lineName;
        			}
    			}
    		}	
    		// 路線名から括弧の内容を消した名前と一致するか
    		if(!eLineFlg){
    			String tLineName = null;
    			String t2LineName = null;
    			if(aLineName.matches(".*" + "\\(" + ".*")){
    	    		int s1 = aLineName.indexOf("(");
    	    		tLineName = aLineName.substring(0, s1);
    			}
    			
    			if(aLineName_h.matches(".*" + "\\(" + ".*")){
    	    		int s1 = aLineName_h.indexOf("(");
    	    		t2LineName = aLineName_h.substring(0, s1);
    			}
    			
    			if(tLineName != null){
	        		if(lineName.matches(".*" + tLineName + ".*")){
	        			eLineFlg = true;
	        			timeTable = new Timetable();
	        			timeTable.line_name = lineName;
	        			return lineName;
	        		}
    			}
    			if(t2LineName != null){
	    			if(lineName.matches(".*" + t2LineName + ".*")){
	        			eLineFlg = true;
	        			timeTable = new Timetable();
	        			timeTable.line_name = lineName;
	        			return lineName;
	    			}
    			}
    		}
    		
    		// 路線名から括弧の内容・会社名を消した名前と一致するか、
    		if(!eLineFlg){
    			String tLineName = aLineName.replaceAll(campanyName, "");
    			String t2LineName = aLineName_h.replaceAll(campanyName, "");
    			if(tLineName.matches(".*" + "\\(" + ".*")){
    	    		int s1 = tLineName.indexOf("(");
    	    		tLineName = tLineName.substring(0, s1);
    			}
    			
    			if(t2LineName.matches(".*" + "\\(" + ".*")){
    	    		int s1 = t2LineName.indexOf("(");
    	    		t2LineName = t2LineName.substring(0, s1);
    			}
    			
    			if(tLineName != null){
	        		if(lineName.matches(".*" + tLineName + ".*")){
	        			// 関係ない会社の路線でないかを判断
	        			if(reigaiLineName(lineName, campanyName)){	 
		        			eLineFlg = true;
		        			timeTable = new Timetable();
		        			timeTable.line_name = lineName;
		        			return lineName;
	        			}

	        		}
    			}
    			if(t2LineName != null){
	    			if(lineName.matches(".*" + t2LineName + ".*")){
	        			// 関係ない会社の路線でないかを判断
	        			if(reigaiLineName(lineName, campanyName)){	    				
		        			eLineFlg = true;
		        			timeTable = new Timetable();
		        			timeTable.line_name = lineName;
		        			return lineName;
	        			}
	    			}
    			}
    		}
    		// こっちのDBの路線名と検索路線名が一致する部分があるか、
    		if(!eLineFlg){
    			if(station.line.line_name.matches(".*" + lineName + ".*")){
        			eLineFlg = true;
        			timeTable = new Timetable();
        			timeTable.line_name = lineName;
        			return lineName;
    			}
    		}
    	}
    	if(eLineFlg){
    		return lineName;
    	}else{
    		return null;
    	}
	}
	
	// 関係ない会社の路線でないかを判断
	private boolean reigaiLineName(String lineName, String campanyName){
		if(lineName.equals("西武有楽町線")){
			if(lineName.matches(".*" + campanyName + ".*")){
				return true;
			}else{
				return false;
			}
		}
		return true;
	}
	
	private List<Timetable> setDirectionInfo(String lineStr, String lineName){
		List<Timetable> tList = new ArrayList<Timetable>();
    	if(lineStr.matches(".*" + "<dd>" + ".*")){
    		int s1 = lineStr.indexOf("=\"");
    		int s2 = lineStr.indexOf("\">");
    		int s3 = lineStr.indexOf("</a>");
    		int l1 = lineStr.lastIndexOf("=\"");
    		int l2 = lineStr.lastIndexOf("\">");
    		int l3 = lineStr.lastIndexOf("</a>");
    		
    		Timetable t1 = new Timetable();
    		String url1 = lineStr.substring(s1+ 2, s2);
    		String directionName1 = lineStr.substring(s2+ 2, s3);
    		t1.line_name = url1;
    		t1.direction = directionName1;
    		//t1.line_name = lineName;
    		tList.add(t1);
    		if(s1 != l1){
    			Timetable t2 = new Timetable();
    			String url2 = lineStr.substring(l1 + 2, l2);
        		String directionName2 = lineStr.substring(l2+ 2, l3);
        		t2.line_name = url2;
    			t2.direction = directionName2;
    			tList.add(t2);
    		}
    	}
    	return tList;
	}
	
	private List<Timetable> getTimeTableList(Timetable tt, String kindNo, Map<Integer, Timenotice> timeNoticeMap){
		List<Timetable> retTt = new ArrayList<Timetable>();
		
		// url
		String startUrl = tt.line_name;
		String tKind = "&kind=" + kindNo;
		startUrl = startUrl + tKind; 

		// 路線名
		String lineName = null;
		// 方面名
		String directionName = tt.direction;
		
		// 注意書きNo
		Integer noticeNo = 0;
		
		try {
        	URL url = new URL(startUrl);
        	HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStreamReader isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
                        BufferedReader reader = new BufferedReader(isr)) {
                        String line;
                        String hourBk = null;
                        boolean readFlg = false;
                        String ln = null;
                        while ((line = reader.readLine()) != null) {
                        	// 読み込みフラグ
                        	if(line.matches(".*" + "<tbody>" + ".*")){
                        		readFlg = true;
                        	}
                        	
                        	// 路線名を設定
                        	if(ln == null){
                        		ln = getTimeTableLineName(line);
                        	}else{
                        	//if(ln != null){
                        		lineName = ln;
                        	}
                        	
                        	if(readFlg){
                        		if(hourBk != null){
                        			Timetable getTt = getTimeTable(line, tt.station_id, lineName, directionName, kindNo, hourBk);
                        			if(getTt != null){
                        				retTt.add(getTt);
                        			}
                        		}
                        		String hour = getTimeTableHour(line);
                        		if(hour != null){
                        			hourBk = hour;
                        		}
                        		// 注意書きをセット
                        		noticeNo = setTimeNoticeList(line, tt.station_id, lineName, directionName, kindNo, noticeNo, timeNoticeMap);
                        	}
                        	
                        	if(line.matches(".*" + "</tfoot>" + ".*")){
                        		break;
                        	}
                        }
                    }
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
		} catch (IOException e) {
	        e.printStackTrace();
	    }
		return retTt;
	}
	
	private String getTimeTableLineName(String lineStr){
		if(lineStr.matches(".*" + "&nbsp;" + ".*")){
			int eNo = lineStr.indexOf("&nbsp;");
			return lineStr.substring(0, eNo);
		}
		return null;
	}
	
	private String getTimeTableHour(String lineStr){
		if(lineStr.matches(".*" + "col1" + ".*")){
			int sNo = lineStr.indexOf(">");
			int eNo = lineStr.indexOf("</");
			return lineStr.substring(sNo + 1, eNo);
		}
		return null;
	}
	
	private Timetable getTimeTable(String lineStr, Long station_id, String lineName, String directionName, String kindNo, String hour){
		Timetable retTimetable = null;
		String minute = null;
		String mark = null;
		String trn = null;
		String sta = null;
/*		
		if(lineStr.matches(".*" + "timeNumb" + ".*")){
			String sStr = "lh=" + hour + "&lm=";
			int sNo = lineStr.indexOf(sStr);
			int eNo = lineStr.indexOf("&tab=time");
			minute = lineStr.substring(sNo + sStr.length(), eNo);
		}
*/
		// 分を取得
		if(lineStr.matches(".*" + "dl" + ".*")){
			int sNo = lineStr.indexOf("<dt>");
			int eNo = 0;
			int sMark = 0;
			int eMark = 0;
			if(lineStr.matches(".*" + "mark" + ".*")){
				eNo = lineStr.indexOf("<span");
				sMark = lineStr.indexOf("class=\"mark\">");
				eMark = lineStr.indexOf("</span>");
			}else{
				eNo = lineStr.indexOf("</dt>");
			}
			if(sNo > 0 && eNo > 0){
				try{
					minute = lineStr.substring(sNo + 4, eNo);
					Integer.parseInt(minute);
					// マークを取得
					if(sMark > 0 && eMark > 0){
						mark = lineStr.substring(sMark + 13, eMark);
					}
				}catch(Exception e){
					return null;
				}
			}
			
			// 電車を取得
			if(lineStr.matches(".*" + "trn-cls" + ".*")){
				sNo = lineStr.indexOf("trn-cls");
				eNo = lineStr.indexOf("</dd>");
				trn = lineStr.substring(sNo + 9, eNo);
			}
			// 行き先を取得
			if(lineStr.matches(".*" + "sta-for" + ".*")){
				sNo = lineStr.indexOf("sta-for");
				sta = lineStr.substring(sNo + 9, sNo + 10);
			}
		}

		if(minute != null){
			retTimetable = new Timetable();
			retTimetable.station_id = station_id;
			retTimetable.line_name = lineName;
			retTimetable.direction = directionName;
			retTimetable.kind = Integer.parseInt(kindNo);
			retTimetable.mark = mark;
			retTimetable.trn = trn;
			retTimetable.sta = sta;
			retTimetable.hour = Integer.parseInt(hour);
			retTimetable.minute = Integer.parseInt(minute);
		}
		return retTimetable;
	}
		
	private void setTimeTableList(List<Timetable> timeTableList, List<Timetable> timeTableList2){
		if(timeTableList2 != null){
			for(Timetable tt: timeTableList2){
				timeTableList.add(tt);
			}
		}
	}
	
	private Integer setTimeNoticeList(String lineStr, Long station_id, String lineName, String direction, String kindNo, Integer noticeNo, Map<Integer, Timenotice> timeNoticeMap){
		//System.out.println("noticeNo: " + noticeNo);
		if(lineStr.matches(".*" + "timeNotice1" + ".*")){
			noticeNo = 1;
			//System.out.println("noticeNoをセット: " + noticeNo);
		}
		if(lineStr.matches(".*" + "timeNotice2" + ".*")){
			noticeNo = 2;
			//System.out.println("noticeNoをセット: " + noticeNo);
		}
		if(lineStr.matches(".*" + "timeNotice3" + ".*")){
			noticeNo = 3;
			//System.out.println("noticeNoをセット: " + noticeNo);
		}
		
		if(noticeNo > 0){
			if(!timeNoticeMap.containsKey(noticeNo)){
				Timenotice timeNotice = new Timenotice();
				timeNotice.station_id = station_id;
				timeNotice.line_name = lineName;
				timeNotice.direction = direction;
				timeNotice.kind = Integer.parseInt(kindNo);
				timeNotice.notice = noticeNo;
				timeNoticeMap.put(noticeNo, timeNotice);
				//System.out.println("注意書きをセット " + "lineName:" + lineName + "kindNo:" + kindNo + "noticeNo:" + noticeNo);
			}
			Timenotice timeNotice = timeNoticeMap.get(noticeNo);
			if(timeNotice.contents == null || timeNotice.contents.length() <= 0){
				timeNotice.contents = lineStr;
			}else{
				timeNotice.contents += "," + lineStr;
			}
			//System.out.println("timeNotice.contents " + timeNotice.contents);
		}
		return noticeNo;

	}
	
	private void saveTimenotice(Map<Integer, Timenotice> timeNoticeMap){
		for(Timenotice timeNotice: timeNoticeMap.values()){
			TimenoticeDao.use().save(timeNotice);
		}
		timeNoticeMap = new HashMap<Integer, Timenotice>();
	}

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
