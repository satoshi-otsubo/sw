/**
 * 
 */
// ajaxを実行する
function exeAjax(url, reqData, HttpRType){
	var retData;
	$.ajax({
        type: HttpRType,
        url: url,
        dataType: "json",
        data: reqData,
        async: false,
        success: function(resData, textStatus, jqXHR) {
        	// エラー処理 TODO
        	retData = resData;
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
        	if(XMLHttpRequest.status == 0){
        	}else{
        	}
        }
    });
	return retData;
}