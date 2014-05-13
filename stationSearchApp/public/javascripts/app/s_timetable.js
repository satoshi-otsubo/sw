	function dispCountTime(){
		var now  = new Date();
		var yy = now.getFullYear();
		var mm = now.getMonth() + 1;
		var dd = now.getDate();
		var hour = now.getHours(); // 時
		var min  = now.getMinutes(); // 分
		var sec  = now.getSeconds(); // 秒
		
		if(getConvTimeNum(hour + ":" + min) < getConvTimeNum($("#time0").val())){
			if($("#fTrnKbn").val() == 1){
				hour = hour + 24;
			}
		}
		
		// 次の時間を取得（現在表示されてい時刻）
		var nextArrayStr = $("#dispTime").text().split(":");
		var nextTime = new Date(yy, mm, dd, nextArrayStr[0], nextArrayStr[1], 0);
		
		// 現在時刻を取得
		var nowTime = new Date(yy, mm, dd, hour, min, sec);

		// 時間の差を求める
		var outputTime = nextTime.getTime() - nowTime.getTime();
		var countHour = parseInt(outputTime / (60*60*1000));
		var countMin = parseInt((outputTime % (60*60*1000)) / (60*1000));
		var countSec = parseInt((outputTime % (60*1000)) / 1000);
		
		// 終電時刻を過ぎた場合、ページを再読み込み
		if($("#selectCount").val() == $("#timeSize").val() - 1){
			if(countSec == 0 && countMin == 0 && countHour == 0){
				location.href = location.href;

			}
		}else{
			
			// 0時の場合の処理
			if(hour == 0 && min == 0 && sec == 0){
				location.href = location.href;
			}
			
			if(countSec == 0 && countMin == 0 && countHour == 0){
				$("#countTime").html("00<span class='font-disp-time-count-2'>秒</span>");
				setNextTime(getNowTimeNum());
				setTimeout("dispCountTime()",1000);
				return;
			}
		}
		

		
		// 次の時間までのカウントを表示する
		var outputTimeStr = "";
		var pnStr = "";
		//時間
		if(countHour <= 0){
			if(countHour < 0){
				outputTimeStr += convTimeNumKeta(Math.abs(countHour)) + "<span class='font-disp-time-count-2'>時間</span>";
				pnStr = "<span class='font-disp-time-count-2'>前</span>";
			}
		}else{
			outputTimeStr = convTimeNumKeta(countHour) + "<span class='font-disp-time-count-2'>時間</span>";
			pnStr = "<span class='font-disp-time-count-2'>後</span>";
		}
		//分
		if(countMin <= 0){
			if(countMin < 0){
				outputTimeStr += convTimeNumKeta(Math.abs(countMin)) + "<span class='font-disp-time-count-2'>分</span>";
				pnStr = "<span class='font-disp-time-count-2'>前</span>";
			}
		}else{
			outputTimeStr += convTimeNumKeta(countMin) + "<span class='font-disp-time-count-2'>分</span>";
			pnStr = "<span class='font-disp-time-count-2'>後</span>";
		}
		//秒
		if(countSec < 0){
			if(countSec < 0){
				outputTimeStr += convTimeNumKeta(Math.abs(countSec)) + "<span class='font-disp-time-count-2'>秒";
				pnStr = "<span class='font-disp-time-count-2'>前</span>";
			}
		}else{
			outputTimeStr += convTimeNumKeta(countSec) + "<span class='font-disp-time-count-2'>秒";
			pnStr = "<span class='font-disp-time-count-2'>後</span>";
		}
		outputTimeStr += pnStr;

		$("#countTime").html(outputTimeStr);
		setTimeout("dispCountTime()",1000);
	}

	// 現在時刻の数字を取得
	function getNowTimeNum(){
		var now  = new Date();
		var hour = now.getHours(); // 時
		var min  = now.getMinutes(); // 分
		var sec  = now.getSeconds(); // 秒
		var retNowTimeNum = 0;

		
		retNowTimeNum = String(hour) + String(min);
		if(min < 10){
			retNowTimeNum = String(hour) + "0" + String(min);
		}
		
		
		// 時間の再設定 日付が変わっても時刻表に２４時以降のデータがある場合２４をたす
		if(parseInt(retNowTimeNum) < getConvTimeNum($("#time0").val())){
			if($("#fTrnKbn").val() == 1){
				hour = hour + 24;
				retNowTimeNum = String(hour) + String(min);
				if(min < 10){
					retNowTimeNum = String(hour) + "0" + String(min);
				}
			}
		}
	
		return retNowTimeNum;
	}
	
	// 現在時刻の次の時間を取得する
	function setNextTime(nowTime){
		var retNextTime = null;
		var selectCount = 0;
		var timeSize = $("#timeSize").val();
		for(i = 0; i < timeSize; i++){
			var convTime = getConvTimeNum($("#time" + i).val());
			if(parseInt(nowTime) < parseInt(convTime)){
				retNextTime = $("#time" + i).val();
				selectCount = i;
				break;
			}
		}
		if(retNextTime == null){
			// 最終時刻をセットする
			retNextTime = $("#time" + (parseInt(timeSize)-1) ).val();
		}
		
		// 始発と終電のラベルを設定する
		var seLabel = "";
		if(selectCount == 0){
			seLabel = '始発';
			$("#dispSELabel").removeClass("label-danger");
			$("#dispSELabel").addClass("label-primary");
		}else if(selectCount == parseInt(timeSize)-1){
			seLabel = '終電';
			$("#dispSELabel").removeClass("label-primary");
			$("#dispSELabel").addClass("label-danger");
		}else{
			$("#dispSELabel").removeClass("label-primary");
			$("#dispSELabel").removeClass("label-danger");
		}
		$("#dispSELabel").html(seLabel);
		
		// 画面の時間選択カウントに取得した時間の番号をセット
		$("#selectCount").val(selectCount);
		
		$("#dispTime").html(retNextTime);
		
		$("#dispSta").html($("#sta" + selectCount).val());
		$("#dispTrn").html($("#trn" + selectCount).val());
		$("#dispMark").html($("#mark" + selectCount).val());
		
		convTrnKind();
		//alert(retNextTime);
	}
	
	// 時刻表の時間を数字に変換する
	function getConvTimeNum(time){
		var retTimeNum = 0;
		var splitTime = time.split(":");
		var sh = "";
		var st = "";
		sh = splitTime[0];
		if(splitTime[1].length == 1){
			st = "0" + splitTime[1];
		}else{
			st = splitTime[1];
		}
		retTimeNum = sh + st;
		return parseInt(retTimeNum);
	}
	
	function setDispTime(select, nextFlg){
		var maxTime = parseInt($("#timeSize").val());
		if(select < 0){
			return;
		}else if(select >= maxTime){
			return;
		}
		
		if(nextFlg == 0){
			$(".dispTimeCounterAnimate")
			.animate({marginLeft: '100px',opacity: 0}, "fast")
			.animate({marginLeft: '0px',opacity: 0}, "fast")
			.animate({marginLeft: '0px',opacity: 100}, "fast");
		}else{
			$(".dispTimeCounterAnimate")
			.animate({marginLeft: '-100px',opacity: 0}, "fast")
			.animate({marginLeft: '0px',opacity: 0}, "fast")
			.animate({marginLeft: '0px',opacity: 100}, "fast");
		}
		
		// 始発と終電のラベルを設定する
		var seLabel = "";
		if(select == 0){
			seLabel = '始発';
			$("#dispSELabel").removeClass("label-danger");
			$("#dispSELabel").addClass("label-primary");
		}else if(select == parseInt(maxTime)-1){
			seLabel = '終電';
			$("#dispSELabel").removeClass("label-primary");
			$("#dispSELabel").addClass("label-danger");
		}else{
			$("#dispSELabel").removeClass("label-primary");
			$("#dispSELabel").removeClass("label-danger");
		}
		$("#dispSELabel").html(seLabel);

		
		$("#dispTime").html($("#time" + select).val());
		
		$("#dispSta").html($("#sta" + select).val());
		$("#dispTrn").html($("#trn" + select).val());
		$("#dispMark").html($("#mark" + select).val());
		
		convTrnKind();
		
		$("#selectCount").val(select);
	}
	
	// 曜日変換
	function convDayKind(){
		var kind = $("#deyKind").text();
		if(kind == "1"){
			$("#deyKind").html("平日");
		}else if(kind == "2"){
			$("#deyKind").html("土曜");
			$("#deyKind").removeClass("label-default");
			$("#deyKind").addClass("label-success");
		}else if(kind == "4"){
			$("#deyKind").html("日曜・祝日");
			$("#deyKind").removeClass("label-default");
			$("#deyKind").addClass("label-danger");
		}else{
			$("#deyKind").html("謎の曜日");
		}
	}
	// 電車種類変換（色）
	function convTrnKind(){
		var kind = $("#dispTrn").text();
		$("#dispTrn").removeClass("label-default");
		$("#dispTrn").removeClass("label-primary");
		$("#dispTrn").removeClass("label-success");
		$("#dispTrn").removeClass("label-info");
		$("#dispTrn").removeClass("label-warning");
		$("#dispTrn").removeClass("label-danger");
		if(kind == "普通" || kind == "各駅停車"){
			//デフォルト
			$("#dispTrn").addClass("label-default");
		}else if(kind == "快速"){
			$("#dispTrn").addClass("label-primary");
		}else if(kind == "寝台特急"){
			$("#dispTrn").addClass("label-warning");
		}else if(kind == "特急"){
			$("#dispTrn").addClass("label-danger");
		}else if(kind == "急行"){
			$("#dispTrn").addClass("label-warning");
		}else if(kind == "ＳＬ"){
			$("#dispTrn").addClass("label-default");
		}else if(kind == "区間快速"){
			$("#dispTrn").addClass("label-primary");
		}else if(kind == "通勤快速"){
			$("#dispTrn").addClass("label-danger");
		}else if(kind == "通勤急行"){
			$("#dispTrn").addClass("label-danger");
		}else if(kind == "準急"){
			$("#dispTrn").addClass("label-success");
		}else if(kind == "快速急行"){
			$("#dispTrn").addClass("label-info");
		}else if(kind == "区間準急"){
			$("#dispTrn").addClass("label-info");
		}else if(kind == "区間急行"){
			$("#dispTrn").addClass("label-warning");
		}else if(kind == "ライナー"){
			$("#dispTrn").addClass("label-danger");
		}else if(kind == "通勤急行"){
			$("#dispTrn").addClass("label-warning");
		}else{
			$("#dispTrn").addClass("label-default");
		}
	}
	
	
	function setTimeTableOfDay(){
		// 始発より前出会った場合
		if(isBeforeFirstTrain()){
			// 一日前の時刻表を取得する
			var previousDayTimetableData = getPreviousDayTimetable();
			
			//終電前であるか判定
			if(isBeforeLastTrain(previousDayTimetableData)){
				// 終電と始発の間の時刻であることを表すフラグ
				$("#fTrnKbn").val(1);
				// 終電前であった場合、１日前の時刻表をセットする
				setDispTimeTable(previousDayTimetableData);
			}
		}
	}
	
	// 現在時刻が始発時刻より前かの判定
	function isBeforeFirstTrain(){
		var now  = new Date();
		var hour = now.getHours(); // 時
		var min  = now.getMinutes(); // 分
		
		//今日の始発時間を取得
		var firstTrainTime = getConvTimeNum($("#time0").val());
		
		// 現在時刻の比較用データを取得
		var nowTimeNum = getConvTimeNum(hour + ":" + min);
		
		// 現在時刻と始発時刻を比較し、始発時間のほうが大きい場合trueを返す
		if(nowTimeNum < firstTrainTime){
			return true;
		}else{
			return false;
		}
	}
	
	// 終電前かの判定を行う
	function isBeforeLastTrain(previousDayTimetableData){
		// 現在の時刻を取得
		var now  = new Date();
		var hour = now.getHours(); // 時
		var min  = now.getMinutes(); // 分
		var sec  = now.getSeconds(); // 秒
		var nowTimeNum = 0;

		hour = hour + 24;
		nowTimeNum = String(hour) + String(min);
		if(min < 10){
			nowTimeNum = String(hour) + "0" + String(min);
		}
		
		var getName = previousDayTimetableData.totalTimes - 1;
		var lastTimeInfo = previousDayTimetableData.timeInfos[getName];
		var lastTimeNum = getConvTimeNum(lastTimeInfo.hour + ":" + lastTimeInfo.minute);
		
		if(parseInt(nowTimeNum) < parseInt(lastTimeNum)){
			return true;
		}else{
			return false;
		}		
	}
/*	
	// 現在日の前日の時刻表を取得する
	function getPreviousDayTimetable(){
		var url = "@controllers.api.routes.ApiTimeTableController.stationTimeTable().absoluteURL()";
		var vals = {};
		vals['station_id'] = "@respose.station_id";
		vals['line_name'] = "@respose.line_name";
		vals['direction'] = "@respose.direction";
		vals['s_day'] = "-1";
		var retData = exeAjax(url, vals, "GET");
		return retData;
	}
*/	
	// 時刻表JSONデータから画面表示時刻表テーブルを作成する
	function setDispTimeTable(data){
		$("#dispTimeTable").empty();
		var dispHtml = "";
		
		var dispHour = 999;
		
		// 曜日表示セット
		$("#deyKind").html(data.kind);
		$("#timeSize").val(data.totalTimes);

		// 分を時間の中にまとめるための情報を作成
		var timeArray = {};
		for(var i = 0; i < data.totalTimes; i++){
			var timeInfo = data.timeInfos[i];
			if(dispHour != timeInfo.hour){
				dispHour = timeInfo.hour;
				timeArray[dispHour] = 0;
			}else{
				timeArray[dispHour] = timeArray[dispHour] + 1;
			}
		}
		
		// テーブル作成
		dispHour = 999;
		var minuteCount = 0;
		dispHtml += '<table class="table table-bordered table-striped">';
		dispHtml += '<tr><th class="table-column-first">時</th>';
		dispHtml += '<th class="table-column-second">' + getLinkDayTimetable(data.kind) + '</th></tr>';
		for(var i = 0; i < data.totalTimes; i++){
			var timeInfo = data.timeInfos[i];
			if(dispHour != timeInfo.hour){
				dispHtml += '<td><br>' + timeInfo.hour + '</td>';
				dispHtml += '<td>';
				dispHtml += '<div class="row">';
				dispHour = timeInfo.hour;
				minuteCount = 0;
			}
			dispHtml += '<div class="col-xs-3 col-sm-2 col-md-1">';
			dispHtml += '<div class="disp-table-minute">';
			dispHtml += '<span class="disp-notice">';
			if(timeInfo.trn != null){
				dispHtml += timeInfo.trn;
			}
			if(timeInfo.sta != null){
				dispHtml += timeInfo.sta;
			}	
			dispHtml += '&nbsp;</span><br>';
			
			dispHtml += '<span id="minute' + timeInfo.timeIndex + '" data-toggle="popover" title="" data-trigger="click" data-placement="bottom"  data-container="body" data-html="true" data-content="">';
			dispHtml += timeInfo.minute;
			dispHtml += '</span>';
				
			if(timeInfo.mark != null){
				dispHtml += timeInfo.mark;
			}	
			
			dispHtml += '<input type="hidden" id="time' + timeInfo.timeIndex + '" value="' + timeInfo.hour + ':' + timeInfo.minute + '">';
			dispHtml += '<input type="hidden" id="mark' + timeInfo.timeIndex + '" value="' + timeInfo.dispMark + '">';
			dispHtml += '<input type="hidden" id="trn' + timeInfo.timeIndex + '" value="' + timeInfo.dispTrainKind + '">';
			dispHtml += '<input type="hidden" id="sta' + timeInfo.timeIndex + '" value="' + timeInfo.dispDestination + '">';
			dispHtml += '</div></div>';	
			
			if(timeArray[dispHour] <= minuteCount){
				dispHtml += '</div></td></tr>';
			}
			minuteCount ++;
		}
		dispHtml += '</table>';

		$("#dispTimeTable").html(dispHtml);
		
		// 時刻詳細ポップアップの設定
		setMinutePopover();
	}
	
	// 時間を２桁にする
	function convTimeNumKeta(time){
		if(time < 10){
			return "0" + time;
		}else{
			return time;
		}
	}
	
	// ポップアップの設定
	function setMinutePopover(){
		var timeSize = $("#timeSize").val();
		for(i = 0; i < timeSize; i++){
			var popInTitle = $("#time" + i).val() + "発&nbsp;詳細";
			var popInContent = "<div>列車種別&nbsp:&nbsp;" + $("#trn" + i).val() + "</div>";
			popInContent += "<div>行き先&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;" + $("#sta" + i).val() + "</div>";
			var sMark = $("#mark" + i).val();
			if(sMark != null && sMark != ""){
				popInContent += "<div>注意&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;" + $("#mark" + i).val() + "</div>";
			}
			
			$("#minute" + i).popover(
				{trigger: 'click', 
				 animation: 'false', 
				 title: popInTitle,
				 content: popInContent	
			}).
			on("mouseenter", function () {
				for(j = 0; j < timeSize; j++){
					$("#minute" + j).popover("hide");
				}
			}).on("mouseleave", function () {
				for(j = 0; j < timeSize; j++){
					$("#minute" + j).popover("hide");
				}
			});
			
		}
	}	
	
	