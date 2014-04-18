package utils;

import common.exception.AppErrorConstants.AppError;
import common.exception.AppException;

/**
 * @author otsubo
 *
 */
public class GeoCodeUtil {

    private static Double SECOND_LATITUDE_FROM = 25.2450; 
    private static Double SECOND_LATITUDE_TO = 30.8184; 
    private static Double ONE_DEGREE = 0.000277778;
    
    /**
     * 
     * @param 
     * @return
     */
    public static GeoCodeRange getGeoCodeRange(int area, Double latitude, Double longitude) {
        GeoCodeRange geoCodeRange = new GeoCodeRange();
        // Range作成
        setRange(area, latitude, longitude, geoCodeRange);
        return geoCodeRange;
    }
    
    private static void setRange(int area, Double latitude, Double longitude, GeoCodeRange geoCodeRange){
        Coordinates fromCoordinates = new Coordinates();
        Coordinates toCoordinates = new Coordinates();
        
        Double fromLatitude = 0.0;
        Double fromLongitude = 0.0;
        
        Double toLatitude = 0.0;
        Double totLongitude = 0.0;
        try{
	        fromLatitude = latitude + (area / SECOND_LATITUDE_TO * ONE_DEGREE);
	        fromLongitude = longitude - (area / SECOND_LATITUDE_FROM * ONE_DEGREE);
	        fromCoordinates.setLatitude(fromLatitude);
	        fromCoordinates.setLongitude(fromLongitude);
	
	        toLatitude = latitude - (area / SECOND_LATITUDE_TO * ONE_DEGREE);
	        totLongitude = longitude + (area / SECOND_LATITUDE_FROM * ONE_DEGREE);
	        toCoordinates.setLatitude(toLatitude);
	        toCoordinates.setLongitude(totLongitude);    
	        
	        geoCodeRange.setRangeFrom(fromCoordinates);
	        geoCodeRange.setRangeTo(toCoordinates);
        }catch(Exception e){
        	String errorMessage = GeoCodeUtil.class.getName() + ":setRange";
        	throw new AppException(AppError.UTIL_GEOCODE_ERROR, e, errorMessage);
        }
    }
    
    public static Double getGeoCodeDistance(
    		Double fLatitude, Double fLongitude, Double tLatitude, Double tLongitude){
        //ラジアンに変換
    	Double a_lat = fLatitude  * Math.PI / 180;
    	Double a_lon = fLongitude * Math.PI / 180;
    	Double b_lat = tLatitude  * Math.PI / 180;
    	Double b_lon = tLongitude * Math.PI / 180;
    	// 緯度の平均、緯度間の差、経度間の差
    	Double latave = (a_lat + b_lat) / 2;
    	Double latidiff = a_lat - b_lat;
    	Double longdiff = a_lon - b_lon;
        //子午線曲率半径
        //半径を6335439m、離心率を0.006694で設定してます
    	Double meridian = 6335439 / Math.sqrt(Math.pow(1 - 0.006694 * Math.sin(latave) * Math.sin(latave), 3)); 
        //卯酉線曲率半径
        //半径を6378137m、離心率を0.006694で設定してます
    	Double primevertical = 6378137 / Math.sqrt(1 - 0.006694 * Math.sin(latave) * Math.sin(latave)); 
        //Hubenyの簡易式
    	Double x = meridian * latidiff;
    	Double y = primevertical * Math.cos(latave) * longdiff;

        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }
}
