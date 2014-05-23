// 地図を動かせないようにする
function nonDraggable(){
    var opts = { draggable: false };
    var map = new google.maps.Map(document.getElementById("map"), opts)
}

// 現在地を取得する
function setNowLocate(callbackfuction, errorCallback){
	//console.log("plus-geomap.jsを実行します。");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(callbackfuction, errorCallback, {timeout:30000});
    } else {
        alert("お使いのブラウザは、現在位置情報の取得機能をご利用頂けません。");
    }
}

var addMarker = function(map, latitude, longitude, mapCount){
	if(mapCount == 0){
	    //現在位置をマップに表示
	    var latlng = new google.maps.LatLng(latitude, longitude);
	    var myOptions = {
	    		zoom: 15,
	    		center: latlng,
	    		mapTypeId: google.maps.MapTypeId.ROADMAP
	    };
	    map.setOptions(myOptions);	
	}
	
	var marker = new google.maps.Marker({
		position: new google.maps.LatLng(latitude, longitude),
		map: map
	});	
}

// 位置情報検索時使用
var addMarkerGeocodeSearch = function(map, latitude, longitude, mapCount, markerTitle){
	var iconUrl = "";
	var iZoom = 0;
	
	iZoom = 12;
	
	if(markerTitle == 1){
	    //現在位置をマップに表示
	    var latlng = new google.maps.LatLng(latitude, longitude);
	    var myOptions = {
	    		zoom: iZoom,
	    		center: latlng,
	    		mapTypeId: google.maps.MapTypeId.ROADMAP
	    };
	    map.setOptions(myOptions);	
	    
	    // 現在値は表示しない
	    //iconUrl = "http://waox.main.jp/png/source-bluedot.png";
	    iconUrl = "http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + markerTitle + "|3366FF|ffffff";    
	}else{
		iconUrl = "http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + markerTitle + "|3366FF|ffffff";
	}

	var marker = new google.maps.Marker({
		position: new google.maps.LatLng(latitude, longitude),
		icon: iconUrl,
		map: map
	});	
	
	if(mapCount > 0){
		// マーカーのクリック時のイベント
		google.maps.event.addListener(marker, 'click', function(){
			markerClickFunction(markerTitle, mapCount);
		});
	}
}

function markerClickFunction(markerTitle, mapCount) {
	var offset = 0;
	var target = $("#" + markerTitle).offset().top - offset;
	
	for(i = 1; i <= mapCount ; i++){
		$("#" + i).removeClass("stationPanel-heading-active");
		$("#" + i).addClass("stationPanel-heading");
	}
	
	$("#" + markerTitle).removeClass("stationPanel-heading");
	$("#" + markerTitle).addClass("stationPanel-heading-active");
	
	$('html, body').animate({scrollTop:target}, 100);
  }

//ブラウザが位置情報取得に対応しているか
function geolocationExist(){
	if (navigator.geolocation) {
		return true;
	}else{
		return false;
	}
}