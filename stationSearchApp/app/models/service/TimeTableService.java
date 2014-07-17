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
        // 1:北海道、2:青森、3:岩手、4:宮城、5:秋田、6:山形、7:福島、8:茨城、9栃木、10:群馬、
        // 11:埼玉、12:千葉、13:東京、14:神奈川、15:新潟県、16:富山、17:石川県、18:福井県、19:山梨、
        // 20:長野、21:岐阜、22:静岡、23:愛知、24:三重、25:滋賀、26:京都、27:大阪、28:兵庫県、29:奈良県
        // 30:和歌山、31:鳥取県、32:島根県、33:岡山県、34:広島県、35:山口県、36:徳島県、37:香川県、38:愛媛県、39:高知県
        // 40:福岡県、41:佐賀県、42:長崎県、43:熊本県、44:大分県、45:宮崎県、46:鹿児島県
        
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
        company_conv_map.put("京王電鉄", "");  // 東京・神奈川
        company_conv_map.put("京急電鉄", "");  // 東京・神奈川
        company_conv_map.put("京成電鉄", "");  // 東京・神奈川
        company_conv_map.put("西武鉄道", "");  // 東京・埼玉
        company_conv_map.put("多摩都市モノレール", "");  // 東京
        company_conv_map.put("ゆりかもめ", "");        // 東京
        company_conv_map.put("相模鉄道", ""); // 神奈川
        company_conv_map.put("湘南モノレール", ""); // 神奈川
        company_conv_map.put("横浜市交通局", "横浜市営"); // 神奈川
        company_conv_map.put("銚子電鉄", ""); // 千葉
        company_conv_map.put("上信電鉄", ""); // 群馬
        company_conv_map.put("鹿島臨海鉄道", ""); // 茨城
        company_conv_map.put("アルピコ交通", "松本電鉄"); // 長野
        company_conv_map.put("岳南鉄道", ""); // 静岡
        company_conv_map.put("静岡鉄道", "静岡鉄道"); // 静岡
        company_conv_map.put("札幌市交通局", "札幌市営"); //　北海道
        company_conv_map.put("十和田観光電鉄", ""); //　青森　鉄道ではない
        company_conv_map.put("青い森鉄道", ""); //　青森
        company_conv_map.put("仙台市交通局", ""); //　宮城
        company_conv_map.put("仙台空港鉄道", ""); //　宮城
        company_conv_map.put("富山地鉄", ""); //　富山
        company_conv_map.put("近江鉄道", ""); //　滋賀
        company_conv_map.put("南海電鉄", ""); //　大阪
        company_conv_map.put("大阪市交通局", "大阪市営"); //　大阪
        company_conv_map.put("大阪高速鉄道", ""); //　大阪
        company_conv_map.put("神戸市交通局", "神戸市営"); //　兵庫
        company_conv_map.put("神戸高速鉄道", ""); //　兵庫
        company_conv_map.put("福岡市交通局", "福岡市営"); //　福岡
        company_conv_map.put("北九州モノレール", ""); //　福岡
        company_conv_map.put("北九州高速鉄道", ""); //　福岡
        company_conv_map.put("甘木鉄道", ""); //　佐賀
        company_conv_map.put("島原鉄道", ""); //　長崎
        company_conv_map.put("肥薩おれんじ鉄道", ""); //　熊本
        
        // 路線を変換するマップをセット
        line_conv_map.put("東武伊勢崎線", "東武伊勢崎線(東武スカイツリーライン)");
        line_conv_map.put("東武野田線", "東武野田線(東武アーバンパークライン)");
        line_conv_map.put("埼玉高速鉄道線", "埼玉高速鉄道");
        line_conv_map.put("日暮里・舎人ライナー", "舎人ライナー");
        line_conv_map.put("JR総武本線", "JR総武線快速");
        line_conv_map.put("小田急線", "小田急小田原線");
        line_conv_map.put("東京モノレール", "東京モノレール羽田空港線");  // 東京
        line_conv_map.put("金沢シーサイドライン", "シーサイドライン");  // 神奈川
        line_conv_map.put("JR京浜東北線", "JR京浜東北線(ＪＲ根岸線)");  // 神奈川
        line_conv_map.put("いすみ線", "いすみ線(いすみ鉄道)");  // 千葉
        line_conv_map.put("東葉高速線", "東葉高速線(東葉高速鉄道)");  // 千葉
        line_conv_map.put("京成本線", "京成本線(京成東成田線)");  // 千葉
        line_conv_map.put("銚子電鉄線", "銚子電鉄aaaaa線(銚子電鉄)");  // 千葉
        line_conv_map.put("ほっとスパ・ライン", "ほっとスパ・ライン(野岩鉄道会津鬼怒川線)");  // 栃木・福島
        line_conv_map.put("関東鉄道竜ヶ崎線", "関東鉄道竜ケ崎線");  // 茨城
        //line_conv_map.put("長野電鉄屋代線", "長野電鉄屋代線");  // 長野 廃線
        line_conv_map.put("天竜浜名湖線", "天竜浜名湖線(天竜浜名湖鉄道)");  // 静岡
        //line_conv_map.put("南アルプスあぷとライン", "南アルプスあぷとライン(大井川鐵道井川線)");  // 静岡 愛称
        line_conv_map.put("JR札沼線", "JR札沼線(ＪＲ学園都市線)");  // 北海道
        //line_conv_map.put("はまなすベイライン大湊線", "はまなすベイライン大湊線(ＪＲ大湊線) ");  // 青森 愛称
        //line_conv_map.put("ドラゴンレール大船渡線", "ドラゴンレール大船渡線(ＪＲ大船渡線)");  // 岩手 愛称
        //line_conv_map.put("銀河ドリームライン釜石線", "銀河ドリームライン釜石線(ＪＲ釜石線)");  // 岩手 愛称
        //line_conv_map.put("十和田八幡平四季彩ライン", "十和田八幡平四季彩ライン(ＪＲ花輪線)");  // 岩手 愛称
        //line_conv_map.put("JR岩泉線", "JR岩泉線");  // 岩手 廃線
        //line_conv_map.put("くりはら田園鉄道線", "くりはら田園鉄道線");  // 宮城 廃線
        line_conv_map.put("仙台市営地下鉄南北線", "仙台市営地下鉄南北線(仙台市地下鉄南北線)");  // 宮城
        line_conv_map.put("仙台空港線", "仙台空港線(仙台空港アクセス線)");  // 宮城
        //line_conv_map.put("十和田八幡平四季彩ライン", "十和田八幡平四季彩ライン(ＪＲ花輪線)");  // 秋田　愛称
	    //line_conv_map.put("フルーツライン左沢線", "フルーツライン左沢線(ＪＲ左沢線)");  // 山形　愛称
	    //line_conv_map.put("奥の細道湯けむりライン", "奥の細道湯けむりライン(ＪＲ陸羽東線));  // 山形　愛称
	    //line_conv_map.put("奥の細道最上川ライン", "奥の細道最上川ライン(ＪＲ陸羽西)");  // 山形　愛称
        //line_conv_map.put("森と水とロマンの鉄道", "森と水とロマンの鉄道(ＪＲ磐越西線)");  // 新潟　愛称
        //line_conv_map.put("北アルプス線", "北アルプス線(ＪＲ大糸線)");  // 新潟　愛称
        //line_conv_map.put("神岡鉄道神岡線", "神岡鉄道神岡線");  // 富山 廃線
        line_conv_map.put("富山地鉄本線", "富山地鉄本aaa線(富山地方鉄道本線)");  // 富山　
        line_conv_map.put("富山地鉄立山線", "富山地鉄立山線(富山地方鉄道立山線)");  // 富山
        line_conv_map.put("黒部峡谷鉄道本線", "黒部峡谷鉄道本線(黒部峡谷鉄道)");  // 富山
        line_conv_map.put("富山地鉄市内線", "富山地方鉄道２系統(富山地方鉄道１系統・２系統)");  // 富山
        line_conv_map.put("富山地鉄富山都心線", "富山地方鉄道３系統環状線(富山地方鉄道１系統・２系統)");  // 富山
        line_conv_map.put("富山地鉄不二越・上滝線", "富山地方鉄道不二越線(富山地方鉄道上滝線)");  // 富山
        //line_conv_map.put("敦賀港線", "消えたよ");  // 福井　廃線
        //line_conv_map.put("ゆとりーとライン", "ゆとりーとライン");  // 愛知　yahooにはデータなし
        line_conv_map.put("豊橋鉄道運動公園前線", "豊橋鉄道東田本線");  // 愛知　yahooがおかしいからしょうがない
        //line_conv_map.put("神岡鉄道神岡線", "神岡鉄道神岡線");  // 岐阜　廃線
        //line_conv_map.put("近鉄養老線", "運営移管で近鉄ではなくなった");  // 岐阜　運営移管で養老鉄道に　取らないのが正解
        line_conv_map.put("嵯峨野観光線", "嵯峨野観光線(嵯峨野観光鉄道)");  // 京都
        line_conv_map.put("京福電鉄嵐山本線", "京福電鉄嵐山本線(嵐電嵐山本線)");  // 京都
        line_conv_map.put("京福電鉄北野線", "京福電鉄北野線(嵐電北野線)");  // 京都
        line_conv_map.put("北大阪急行電鉄", "北大阪急行電鉄(北大阪急行線)");  // 大阪
        line_conv_map.put("阪堺電軌阪堺線", "阪堺電軌阪堺線(阪堺電気軌道阪堺線)");  // 大阪
        line_conv_map.put("阪堺電軌上町線", "阪堺電軌上町線(阪堺電気軌道上町線)");  // 大阪
        line_conv_map.put("神戸高速東西線", "神戸高速線");  // 兵庫
        line_conv_map.put("神戸高速南北線", "神戸高速線");  // 兵庫
        line_conv_map.put("公園都市線", "神鉄三田線");  // 兵庫
        line_conv_map.put("北神急行北神線", "北神急行電鉄");  // 兵庫
        //line_conv_map.put("三木鉄道三木線", "三木鉄道三木線");  // 兵庫 廃線
        line_conv_map.put("神戸市営地下鉄西神線", "神戸市営地下鉄西神・山手線");  // 兵庫
        line_conv_map.put("神戸市営地下鉄山手線", "神戸市営地下鉄西神・山手線");  // 兵庫
        line_conv_map.put("夢かもめ", "夢かもめ(神戸市営地下鉄海岸線)");  // 兵庫 愛称
        line_conv_map.put("ポートライナー", "ポートライナー(神戸新交通ポートアイランド線)");  // 兵庫 愛称
        line_conv_map.put("六甲ライナー", "六甲ライナー(神戸新交通六甲アイランド線)");  // 兵庫 愛称
        line_conv_map.put("きのくに線", "きのくに線");  // 和歌山
        line_conv_map.put("若桜線", "若桜鉄道");  // 鳥取
        line_conv_map.put("水島本線", "水島臨海鉄道");  // 岡山
        line_conv_map.put("スカイレールみどり坂線", "スカイレールサービス");  // 広島
        line_conv_map.put("広電２号線(宮島線)", "広電２号線(広島電鉄宮島線)");  // 広島
        line_conv_map.put("よしの川ブルーライン", "徳島線");  // 広島
        line_conv_map.put("阿波室戸シーサイドライン", "阿佐海岸鉄道阿佐東線");  // 徳島
        line_conv_map.put("しまんとグリーンライン", "予土線");  // 愛媛
        line_conv_map.put("JR予讃・内子線", "内子線(ＪＲ予讃線)");  // 愛媛
        line_conv_map.put("ごめん線", "土佐電気鉄道後免線");  // 高知
        line_conv_map.put("ゆふ高原線", "ゆふ高原線(ＪＲ久大本線)");  // 福岡
        line_conv_map.put("海の中道線", "海の中道線(ＪＲ香椎線)");  // 福岡
        line_conv_map.put("福岡市営地下鉄空港線", "福岡市営地下鉄空港線(福岡市地下鉄空港線)");  // 福岡
        line_conv_map.put("福岡市営地下鉄箱崎線", "福岡市営地下鉄箱崎線(福岡市地下鉄箱崎線)");  // 福岡
        line_conv_map.put("福岡市営地下鉄七隈線", "福岡市営地下鉄七隈線(福岡市地下鉄七隈線)");  // 福岡
        line_conv_map.put("北九州モノレール", "北九州モノレール(北九州モノレール小倉線)");  // 福岡
        line_conv_map.put("甘木鉄道", "甘木線");  // 佐賀
        line_conv_map.put("長崎電軌１系統", "長崎電気軌道１系統");  // 長崎
        line_conv_map.put("長崎電軌３系統", "長崎電気軌道３系統");  // 長崎
        line_conv_map.put("長崎電軌４系統", "長崎電気軌道４系統");  // 長崎
        line_conv_map.put("長崎電軌５系統", "長崎電気軌道５系統");  // 長崎
        line_conv_map.put("阿蘇高原線", "阿蘇高原線(ＪＲ豊肥本線)");  // 熊本
        line_conv_map.put("えびの高原線(八代～吉松)", "ＪＲ肥薩線");  // 熊本
        line_conv_map.put("熊本電鉄本線", "熊本電気鉄道菊池線(熊本電気鉄道藤崎線)");  // 熊本
        line_conv_map.put("熊本電鉄上熊本線", "熊本電鉄上熊本線(熊本電気鉄道菊池線)");  // 熊本
        line_conv_map.put("肥薩おれんじ鉄道線", "肥薩おれんじ鉄道線(肥薩おれんじ鉄道)");  // 熊本
        line_conv_map.put("えびの高原線", "えびの高原線(ＪＲ吉都線)");  // 宮崎
        
        // 駅を変換するマップをセット
        station_conv_map.put("鳩ヶ谷", "鳩ケ谷");             // 東京
        station_conv_map.put("霞ヶ関", "霞ケ関");             // 東京
        station_conv_map.put("押上〈スカイツリー前〉", "押上");  // 東京
        station_conv_map.put("押上（スカイツリー前）", "押上");  // 東京
        station_conv_map.put("幡ヶ谷", "幡ケ谷");  // 東京
        station_conv_map.put("明治神宮前〈原宿〉", "明治神宮前");  // 東京
        station_conv_map.put("百合ヶ丘", "百合ケ丘");  // 神奈川
        station_conv_map.put("桜ヶ丘", "桜ケ丘");  // 神奈川
        station_conv_map.put("鎌ヶ谷", "鎌ケ谷");  // 千葉
        station_conv_map.put("空港第２ビル（第２旅客ターミナル）", "空港第２ビル");  // 千葉
        station_conv_map.put("成田空港（第１旅客ターミナル）", "成田空港");  // 千葉
        station_conv_map.put("鹿島サッカースタジアム（臨）", "鹿島サッカースタジアム");  // 茨城
        station_conv_map.put("竜ヶ崎", "竜ケ崎");  // 茨城
        station_conv_map.put("ジヤトコ前（ジヤトコ１地区前）", "ジヤトコ前");  // 静岡
        station_conv_map.put("古館", "古館");  // 岩手
        station_conv_map.put("旭ヶ丘", "旭ケ丘");  // 宮城、富山、宮崎
        station_conv_map.put("仙台空港", "仙台空港(鉄道)");  // 宮城
        station_conv_map.put("粟島（大阪屋ショップ前）", "粟島");  // 富山
        station_conv_map.put("中町（西町北）", "中町");  // 富山
        station_conv_map.put("向ヶ丘", "向ケ丘");  // 愛知
        station_conv_map.put("星ヶ丘", "星ケ丘");  // 愛知,大阪
        station_conv_map.put("市役所", "市役所");  // 愛知
        station_conv_map.put("三野瀬", "三野瀬");  // 三重
        station_conv_map.put("松ヶ崎", "松ケ崎");  // 京都、三重
        station_conv_map.put("霞ヶ丘", "霞ケ丘");  // 兵庫
        station_conv_map.put("江井ヶ島", "江井ケ島");  // 兵庫
        station_conv_map.put("米子空港", "米子空港(鉄道)");  // 鳥取
        station_conv_map.put("広島港（宇品）", "広島港・宇品");  // 広島
        station_conv_map.put("綾川（イオンモール綾川）", "綾川");  // 香川

        
        // 特定の駅の場合に路線を変換する
        //station_line_conv_map.put("初台", "京王新線");
        //station_line_conv_map.put("幡ケ谷", "京王新線");
        //station_line_conv_map.put("笹塚", "京王");
        
        //Option<List<Station>> opts = StationDao.use().findAll();
        Option<List<Station>> opts = StationDao.use().findByPref(new Long(46));
        
        int iCount = 0;
        for(Station s: opts.get()){
        	
        	
        	if(s.prefecture_id == 4){
        		// 宮城特殊ロジック
        		if(s.line.line_name.equals("仙台市営地下鉄南北線")){
        			station_conv_map.put("仙台", "仙台(地下鉄)");  // 宮城
        		}
        	}else if(s.prefecture_id == 17){
        		// 石川特殊ロジック
        		if(s.line.line_name.equals("北陸鉄道石川線")){
        			station_conv_map.put("野々市", "野々市(北陸鉄道線)");  // 石川
        		}
        	}else if(s.prefecture_id == 18){
        		// 福井特殊ロジック
        		if(s.line.line_name.equals("敦賀港線")){
                	// 時刻表、注意を削除
                	// Timetableテーブルを駅IDで取得、削除
                	System.out.println("時刻表/注意書を削除　駅ID：" + s.id);        			
        			continue;
        		}	
        	}else if(s.prefecture_id == 21){
        		// 岐阜県特殊ロジック
        		if(s.line.line_name.equals("近鉄養老線")){
                	// 時刻表、注意を削除
                	// Timetableテーブルを駅IDで取得、削除
                	System.out.println("時刻表/注意書を削除　駅ID：" + s.id);

        			continue;
        		}
        	}else if(s.prefecture_id == 23){
        		// 愛知特殊ロジック
        		if(s.line.line_name.equals("豊橋鉄道渥美線")){
        			station_conv_map.put("植田", "植田(豊橋鉄道線)");  // 愛知
        		}else if(s.line.line_name.equals("名古屋市営地下鉄鶴舞線")){
        			station_conv_map.put("植田", "植田(名古屋市営)");  // 愛知
        		}else if(s.line.line_name.equals("ピーチライナー")){
        			continue;
        		}else if(s.line.line_name.equals("名鉄空港線")){
        			station_conv_map.put("中部国際空港", "中部国際空港(鉄道)");  // 愛知
        		}else if(s.line.line_name.equals("東海交通事業城北線")){
        			station_conv_map.put("味美", "味美(東海交通線)");  // 愛知
        		}
        	}else if(s.prefecture_id == 26){
        		// 京都特殊ロジック
        		if(s.line.line_name.equals("京阪本線")){
                	// 時刻表、注意を削除
                	// Timetableテーブルを駅IDで取得、削除
                	System.out.println("時刻表/注意書を削除　駅ID：" + s.id);
                	Option<List<Timetable>> delTimetables = TimetableDao.use().findByStationId(s.id);
                	if(delTimetables.isDefined()){
        	        	for(Timetable dtt: delTimetables.get()){
        	        		TimetableDao.use().delete(dtt);
        	        	}
                	}
                	Option<List<Timenotice>> delTimenotices = TimenoticeDao.use().findByStationId(s.id);
                	if(delTimenotices.isDefined()){
        	        	for(Timenotice dtn: delTimenotices.get()){
        	        		TimenoticeDao.use().delete(dtn);
        	        	}	
                	}        			
        			if(s.station_name.equals("出町柳")){
        				//出町柳は京阪本線に存在しない
        				continue;
        			}else if(s.station_name.equals("神宮丸太町")){
        				//神宮丸太町は京阪本線に存在しない
        				continue;	
        			}
        		}else if(s.line.line_name.equals("京都市営地下鉄烏丸線")){
        			if(s.station_name.equals("十条")){
        				station_conv_map.put("十条", "十条(京都市営)");  // 京都
        			}
        		}else if(s.line.line_name.equals("京都市営地下鉄東西線")){
        			if(s.station_name.equals("六地蔵")){
        				station_conv_map.put("六地蔵", "六地蔵(京都市営)");  // 京都
        			}
        		}else if(s.line.line_name.equals("京福電鉄嵐山本線")){
        			if(s.station_name.equals("西院")){
        				station_conv_map.put("西院", "西院(京福線)");  // 京都
        			}else if(s.station_name.equals("嵐山")){
        				station_conv_map.put("嵐山", "嵐山(京福線)");  // 京都
        			}
        		}
        	}else if(s.prefecture_id == 27){
        		// 大阪特殊ロジック
        		if(s.line.line_name.equals("大和路線")){
        			station_conv_map.put("高井田", "高井田(関西本線)");  // 大阪
        			station_conv_map.put("平野", "平野(関西本線)");  // 大阪
        		}else if(s.line.line_name.equals("北大阪急行電鉄")){
        			station_conv_map.put("千里中央", "千里中央(北大阪急行)");  // 大阪
        		}else if(s.line.line_name.equals("大阪モノレール線")){
        			station_conv_map.put("千里中央", "千里中央(大阪モノレール)");  // 大阪
        			station_conv_map.put("山田", "山田(大阪モノレール)");  // 大阪
        			station_conv_map.put("南茨木", "南茨木(大阪モノレール)");  // 大阪
        		}else if(s.line.line_name.equals("大阪市営地下鉄中央線")){
        			station_conv_map.put("高井田", "高井田(大阪市営)");  // 大阪
        		}else if(s.line.line_name.equals("阪急京都本線")){
        			if(s.station_name.equals("南茨木")){
        				station_conv_map.put("南茨木", "南茨木(阪急線)");  // 大阪
        			}
        		}else if(s.line.line_name.equals("大阪市営地下鉄谷町線")){
        			station_conv_map.put("平野", "平野(大阪市営)");  // 大阪
        		}
        	}else if(s.prefecture_id == 28){
        		// 兵庫特殊ロジック
        		if(s.line.line_name.equals("JR神戸線(大阪～神戸)")){
        			if(s.station_name.equals("西宮")){
               			station_conv_map.put("西宮", "西宮(ＪＲ線)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("JR山陽本線(兵庫～和田岬)")){
        			if(s.station_name.equals("兵庫")){
        				line_conv_map.put("JR山陽本線(兵庫～和田岬)", "ＪＲ和田岬線");  // 兵庫
        			}else if(s.station_name.equals("和田岬")){
        				line_conv_map.put("JR山陽本線(兵庫～和田岬)", "ＪＲ和田岬線");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("JR宝塚線")){
        			if(s.station_name.equals("塚口")){
        				station_conv_map.put("塚口", "塚口(福知山線)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("JR加古川線")){
        			if(s.station_name.equals("市場")){
        				station_conv_map.put("市場", "市場(兵庫県・加古川)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("阪神本線")){
        			if(s.station_name.equals("尼崎")){
        				station_conv_map.put("尼崎", "尼崎(阪神線)");  // 兵庫
        			}else if(s.station_name.equals("芦屋")){
        				station_conv_map.put("芦屋", "芦屋(阪神線)");  // 兵庫
        			}else if(s.station_name.equals("三宮")){
        				station_conv_map.put("三宮", "神戸三宮(阪神)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("神戸高速東西線")){
        			if(s.station_name.equals("三宮")){
        				station_conv_map.put("三宮", "神戸三宮(阪急・神戸高速)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("有馬線")){
        			if(s.station_name.equals("長田")){
        				station_conv_map.put("長田", "長田(神戸電鉄線)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("ポートライナー")){
        			if(s.station_name.equals("三宮")){
        				station_conv_map.put("三宮", "三宮(神戸新交通)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("六甲ライナー")){
        			if(s.station_name.equals("住吉")){
        				station_conv_map.put("住吉", "住吉(兵庫県・東海道)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("阪急神戸本線")){
        			if(s.station_name.equals("塚口")){
        				station_conv_map.put("塚口", "塚口(阪急線)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("粟生線")){
        			if(s.station_name.equals("市場")){
        				station_conv_map.put("市場", "市場(兵庫県・神鉄線)");  // 兵庫
        			}
        		}else if(s.line.line_name.equals("神戸市営地下鉄山手線")){
        			if(s.station_name.equals("三宮")){
        				station_conv_map.put("三宮", "三宮(神戸市営)");  // 兵庫
        			}else if(s.station_name.equals("長田")){
        				station_conv_map.put("長田", "長田(神戸市営)");  // 兵庫
        			}
        		}
        	}else if(s.prefecture_id == 39){
        		// 高知特殊ロジック
        		if(s.line.line_name.equals("ごめん線")){
        			if(s.station_name.equals("後免町")){
        				station_conv_map.put("後免町", "後免町(軌道線)");  // 高知
        			}
        		}else if(s.line.line_name.equals("ごめん・なはり線")){
        			if(s.station_name.equals("後免町")){
        				station_conv_map.put("後免町", "後免町(鉄道線)");  // 高知
        			}
        		}else if(s.line.line_name.equals("伊野線")){
        			if(s.station_name.equals("朝倉")){
        				station_conv_map.put("朝倉", "朝倉(高知県・土電線)");  // 高知
        			}
        		}
        	}else if(s.prefecture_id == 40){
        		// 福岡特殊ロジック
        		if(s.line.line_name.equals("北九州モノレール")){
        			if(s.station_name.equals("城野")){
        				station_conv_map.put("城野", "城野(北九州高速鉄道)");  // 福岡
        			}else if(s.station_name.equals("志井")){
        				station_conv_map.put("志井", "志井(北九州高速鉄道)");  // 福岡
        			}
        		}
        	}else if(s.prefecture_id == 42){
        		// 長崎特殊ロジック
        		if(s.line.line_name.equals("島原鉄道線")){
        			if(s.station_name.equals("深江")){
                    	// 時刻表、注意を削除

                    	// Timetableテーブルを駅IDで取得、削除
                    	System.out.println("時刻表/注意書を削除　駅ID：" + s.id);
            			continue;		
        			}else if(s.station_name.equals("有家")){
                    	// 時刻表、注意を削除
	
                    	// Timetableテーブルを駅IDで取得、削除
                    	System.out.println("時刻表/注意書を削除　駅ID：" + s.id);
            			continue;	
        			}

        		}
        	}else if(s.prefecture_id == 43){
        		// 熊本特殊ロジック
        		if(s.line.line_name.equals("JR三角線")){
        			if(s.station_name.equals("住吉")){
        				station_conv_map.put("住吉", "住吉(熊本県)");  // 熊本
        			}
        		}
        	}else if(s.prefecture_id == 46){
        		// 鹿児島特殊ロジック
        		if(s.line.line_name.equals("JR鹿児島本線(川内～鹿児島)")){
        			if(s.station_name.equals("鹿児島")){
        				continue;
        			}
        		}else if(s.line.line_name.equals("鹿児島市電１系統") || s.line.line_name.equals("鹿児島市電２系統")){
        			if(s.station_name.equals("郡元")){
        				station_conv_map.put("郡元", "郡元(鹿児島市電)");  // 鹿児島
        			}else if(s.station_name.equals("谷山")){
        				station_conv_map.put("谷山", "谷山(鹿児島市電)");  // 鹿児島
        			}
        		}
        	}

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
        		
        		if(s.prefecture_id == 30){
	        		// 和歌山特殊ロジック
	        		if(s.line.line_name.equals("きのくに線")){
	        			s.line.line_name_h = line_conv_map.get(s.line.line_name);
	        		}
        		}else if(s.prefecture_id == 34){
	        		// 広島特殊ロジック
	        		if(s.line.line_name.equals("広電２号線(宮島線)")){
	        			s.line.line_name_h = "";
	        		}
        		}else if(s.prefecture_id == 38){
	        		// 愛媛特殊ロジック
	        		if(s.line.line_name.equals("JR予讃・内子線")){
	        			s.line.line_name_h = "";
	        		}
        		}
        	}
        	
        	//if(s.id == 1191001){
        		
        	// 時刻表、注意を削除
        	// Timetableテーブルを駅IDで取得、削除
        	System.out.println("時刻表/注意書を削除　駅ID：" + s.id);
        	Option<List<Timetable>> delTimetables = TimetableDao.use().findByStationId(s.id);
        	if(delTimetables.isDefined()){
	        	for(Timetable dtt: delTimetables.get()){
	        		TimetableDao.use().delete(dtt);
	        	}
        	}
        	Option<List<Timenotice>> delTimenotices = TimenoticeDao.use().findByStationId(s.id);
        	if(delTimenotices.isDefined()){
	        	for(Timenotice dtn: delTimenotices.get()){
	        		TimenoticeDao.use().delete(dtn);
	        	}	
        	}

            System.out.println("No." + iCount + " この駅を取得：" + s.station_name + " 会社：" + s.line.company.company_name_r + " 元路線：" + s.line.line_name);
    		List<Timetable> timeTableList = new ArrayList<Timetable>();
    		List<Timetable> timeTableLastSetList = new ArrayList<Timetable>();
            // 駅番号取得
            String stationNo = stationNoSearch(s);
            System.out.println("駅番号：" + stationNo + " 駅ID：" + s.id);
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
	    				// 愛知件得書ロジック
	    				if(info_name.equals("愛知県")){
	    					if(s.line.line_name.equals("名古屋市営地下鉄名城線")){
		    					if(!info_name.equals("市役所")){
		    						continue;
		    					}
	    					}
	    				}
	    				
	    				// 福岡特殊ロジック　若松の場合、若松港がとれてしまう為
	    				if(pref_name.equals("福岡県")){
	    					if(s.line.line_name.equals("若松線")){
		    					if(!info_name.equals("若松")){
		    						continue;
		    					}
	    					}
	    				}
	    				
	    				// 熊本特殊ロジック　三角の場合、三角港がとれてしまう為
	    				if(pref_name.equals("熊本県")){
	    					if(s.line.line_name.equals("JR三角線")){
		    					if(!info_name.equals("三角")){
		    						continue;
		    					}
	    					}
	    				}
	    				
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
		}else{
			return true;
		}
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
