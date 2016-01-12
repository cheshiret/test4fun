package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.ErrorOnDataException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tchen on 1/11/2016.
 */
public class LatitudeLongitude {
    private static final double EARTH_RADIUS = 3963.0;
    private static final double DEGREES_TO_RADIANS = Math.PI/180.0;
    private static final double RADIANS_TO_DEGREES = 180.0/Math.PI;

    /**
     *
     * Calculate the distance between two longitude and latitude info. longitude and latitude were given in degrees.
     * @return
     */
    public static double getLatitudeLongitudeDistance(double lat1, double lon1, double lat2, double lon2) {
        double rlon1   = Math.toRadians(lon1);
        double rlat1   = Math.toRadians(lat1);
        double rlon2   = Math.toRadians(lon2);
        double rlat2   = Math.toRadians(lat2);
        double distance = EARTH_RADIUS*(Math.acos(Math.cos(rlat2)*Math.cos(rlat1)*Math.cos(rlon2-rlon1)+Math.sin(rlat2)*Math.sin(rlat1)));
        return distance;
    }

    /**
     * Given the degree changes north/south by miles, return the change in latitude.
     *  For example:
     *  50 miles you get degrees = 0.7228839201751491
     *  200 miles you get 锟絛egrees = 2.891535680700597
     * 	1000 miles you get degrees = 14.457678403502983
     * @return
     */
    public static double getDegreeChangesByMile(double miles){
        return (miles/EARTH_RADIUS)*RADIANS_TO_DEGREES;
    }

    /**
     * Give a latitude and a distance west/east, return the change in longitude.
     * @param latitude
     * @param miles
     * @return
     */
    public static double getLongitudeChangesByLatitudeAndMile(double latitude,double miles){
        double r = EARTH_RADIUS*Math.cos(latitude*DEGREES_TO_RADIANS);
        return (miles/r)*RADIANS_TO_DEGREES;
    }

    /**
     * Parse String to degree, minutes and seconds.
     * for example, when you enter "14.457678", the return List will contain "14" degree, "27" minutes, and "27.642" seconds.
     * @param degs
     * @return
     */
    public static List<Double> parseDegree(double degs){
        List<Double> degreeMinuesSeconds = new ArrayList<Double>();
        double deg = degs;
        double degree = Math.floor(deg);
        double minutes = Math.floor((deg - degree) * 60);
        double seconds = ((deg - degree) * 60 - minutes)*60;
        degreeMinuesSeconds.add(degree);
        degreeMinuesSeconds.add(minutes);
        degreeMinuesSeconds.add(seconds);
        return degreeMinuesSeconds;
    }

    /**
     * Parse degree, minutes and seconds to degree.
     * for example: when you enter  14 degree, 27 minutes, and 27.642 seconds, the return result will be "14.457678"
     * @param degs
     * @param minutes
     * @param seconds
     * @return
     */
    public static double parseToDegree(double degs, double minutes, double seconds){
        double degree = 0;
        degree = degs + minutes/60 + seconds/3600;
        return degree;
    }

    /**
     * Parse degree, minutes and seconds to degree.
     * for example: when you enter  "0383302N", the return value will be "38.5505556"; when you enter "1074112W", the return value will be "-107.6866667";
     * @param degs
     * @return
     */
    public static String parseToDegree(String degs){
        //make up the longitude and latitude length to 8 chars.
        int prefixAmount = 8 - degs.length();
        String prefix = "";
        for(int j =0 ; j < prefixAmount; j ++){
            prefix = prefix + "0";
        }
        degs = prefix + degs;

        //parse latitude/longitude info.
        if (degs.endsWith("N") || degs.endsWith("S") || degs.endsWith("W") || degs.endsWith("E")){
            double tempLatLongDegree =0;
            double tempLatLongMinute = 0;
            double tempLatLongSecond = 0;

            if(degs.contains("*")){
                String temp[] = degs.split("\\*");
                tempLatLongDegree = Double.parseDouble(temp[0]);
                tempLatLongMinute = Double.parseDouble(temp[1])/60;
            } else {
                tempLatLongDegree =  Double.parseDouble(degs.substring(0, 3));
                tempLatLongMinute =  Double.parseDouble(degs.substring(3, 5))/60;
                tempLatLongSecond =  Double.parseDouble(degs.substring(5, 7))/60/60;
            }

            if(degs.endsWith("N") || degs.endsWith("E")){
                degs = Double.toString(tempLatLongDegree + tempLatLongMinute + tempLatLongSecond);
            }else{
                degs = "-" + Double.toString(tempLatLongDegree + tempLatLongMinute + tempLatLongSecond);
            }
        }else{
            throw new ErrorOnDataException("The passing longitude or latitude format should end with N, S, W or E...; The current value is:" + degs + "!");
        }
        return degs;
    }

}

