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
	
	iZoom = 14;
	
	if(mapCount == 0){
	    //現在位置をマップに表示
	    var latlng = new google.maps.LatLng(latitude, longitude);
	    var myOptions = {
	    		zoom: iZoom,
	    		center: latlng,
	    		mapTypeId: google.maps.MapTypeId.ROADMAP
	    };
	    map.setOptions(myOptions);	
	    
	    iconUrl = "http://waox.main.jp/png/source-bluedot.png";
	}else{
		iconUrl = "http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + markerTitle + "|FA8072|000000";
	}

	var marker = new google.maps.Marker({
		position: new google.maps.LatLng(latitude, longitude),
		//icon: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=' + markerTitle + '|FA8072|000000',
		icon: iconUrl,
		map: map
	});	
	
	
}

function markerClickFunction(markerTitle) {
    //alert(markerTitle);
	var offset = 50;
	var target = $("#" + markerTitle).offset().top - offset;
	$('html, body').animate({scrollTop:target}, 500);
  }

//ブラウザが位置情報取得に対応しているか
function geolocationExist(){
	if (navigator.geolocation) {
		return true;
	}else{
		return false;
	}
}