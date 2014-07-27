// 検索種類のバー、検索方法をクリックした時クッキーを削除して遷移する処理

function conditionSearchClick(url){
	deleteCookieById(CONDITION_SEARCH_CONDITIONS);
	window.location = url;
}
function geocodeSearchClick(url){
	deleteCookieById(GEOCODE_SEARCH_CONDITIONS);
	window.location = url;
}

//アラート表示
function displayAlert(data, id, msg){
	if(data.empty == true){
		$("#" + id).html(makeAlertMessage(msg));
	}else{
		$("#" + id).empty();
	}
}

function ajaxLoading(){
	$("#loading").bind("ajaxSend", function(){
		$("#overlay").css('height', $(document).height());
		$("#overlay").css("opacity", "0.3").show();
		$(this).show();
		$("#loading").css('top', $(window).height() / 2);
		$("#loading").css('left', $(window).width() / 2);
		$("#loading").activity();
	}).bind("ajaxComplete", function(){
		$("#overlay").fadeOut(100);
		$("#loading").fadeOut(100);
		$("#loading").css("display", "none");
	});
}

/**
 * エラー表示用のHTMLを返す
 * @param msg
 * @return {String}
 */
function makeAlertMessage(msg){
    var alertMessage = '<div class="alert alert-error">'
        + '<button type="button" class="close" data-dismiss="alert">×</button>'
        + '<strong></strong> ' + msg
        + '</div>';

    return alertMessage;
}

// アドレスバーを隠す処理
function initAdBar(){
	hideAdBar();
	$(window).bind("orientationchange",function(){
		if(Math.abs(window.orientation) === 90){
			hideAdBar();
		}else{
			hideAdBar();
		}
	});
}

function hideAdBar(){
	setTimeout("scrollTo(0,1)", 100);
}
