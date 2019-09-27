package com.ben.gps.util;

import org.apache.commons.lang3.tuple.Pair;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

/**
 * GPS的一些工具类,精度在1米 <br>
 *
 * @author: 刘恒 <br>
 * @date: 2019/9/26 <br>
 */
public class GpsUtil {

    private static Ellipsoid defaultEllipsoid = Ellipsoid.WGS84;
    private static GeodeticCalculator geodeticCalculator = new GeodeticCalculator();

    public static Ellipsoid getDefaultEllipsoid() {
        return defaultEllipsoid;
    }

    public static void setDefaultEllipsoid(Ellipsoid defaultEllipsoid) {
        GpsUtil.defaultEllipsoid = defaultEllipsoid;
    }

    /**
     * 计算2个点之间的距离(单位米)和方位角
     **/
    public static Pair<Integer, Double> calcDistanceMeterAndAzimuth(double lat1, double lng1, double lat2, double lng2) {
        if (lat1 == lat2 && lng1 == lng2)
            return Pair.of(0, Double.NaN);

        GlobalCoordinates point1 = new GlobalCoordinates(lat1, lng1);
        GlobalCoordinates point2 = new GlobalCoordinates(lat2, lng2);

        GeodeticCurve geodeticCurve = geodeticCalculator.calculateGeodeticCurve(defaultEllipsoid, point1, point2);

        return Pair.of((int) geodeticCurve.getEllipsoidalDistance(), geodeticCurve.getAzimuth());
    }

    /**
     * 计算2个定位点之间的距离，精确到米, 如果2个定位点重叠，返回0
     **/
    public static int calcDistanceMeter(double lat1, double lng1, double lat2, double lng2) {

        if (lat1 == lat2 && lng1 == lng2)
            return 0;

        GlobalCoordinates point1 = new GlobalCoordinates(lat1, lng1);
        GlobalCoordinates point2 = new GlobalCoordinates(lat2, lng2);

        GeodeticCurve geodeticCurve = geodeticCalculator.calculateGeodeticCurve(defaultEllipsoid, point1, point2);

        return (int) geodeticCurve.getEllipsoidalDistance();
    }

    /**
     * 计算2个定位点之间的方位角，如果2个定位点重叠，返回NaN
     **/
    public static double calcAzimuth(double lat1, double lng1, double lat2, double lng2) {
        if (lat1 == lat2 && lng1 == lng2)
            return Double.NaN;

        GlobalCoordinates point1 = new GlobalCoordinates(lat1, lng1);
        GlobalCoordinates point2 = new GlobalCoordinates(lat2, lng2);

        GeodeticCurve geodeticCurve = geodeticCalculator.calculateGeodeticCurve(defaultEllipsoid, point1, point2);

        return geodeticCurve.getAzimuth();
    }

    /**
     * 已知一个点，根据距离和方位角，算出另一个点
     **/
    public static Pair<Double, Double> calcGpsPoint(double lat, double lng, int distanceMeter, double azimuth) {
        if (distanceMeter <= 0)
            return Pair.of(lat, lng);

        GlobalCoordinates point1 = new GlobalCoordinates(lat, lng);
        GlobalCoordinates point2 = geodeticCalculator.calculateEndingGlobalCoordinates(defaultEllipsoid, point1, azimuth, distanceMeter);

        return Pair.of(point2.getLatitude(), point2.getLongitude());
    }

    /**
     * 计算三角形顶点对应底边的垂直距离，Vertex是顶点，另外2个点连起来就是底边
     **/
    public static double calcVerticalDistanceMeter(double latVertex, double lngVertex, double lat1, double lng1, double lat2, double lng2) {
        int side1 = calcDistanceMeter(latVertex, lngVertex, lat1, lng1);
        int side2 = calcDistanceMeter(latVertex, lngVertex, lat2, lng2);
        int baseSide = calcDistanceMeter(lat1, lng1, lat2, lng2);

        return calcVerticalDistance(side1, side2, baseSide);
    }

    /**
     * 计算三角形顶点到对应的边的垂直距离，side1,side2,baseSide 是3条边，  计算baseSide边的高（就是baseSide边对应的顶点到baseSide的距离）
     * 用海伦公式算
     **/
    public static int calcVerticalDistance(int side1, int side2, int baseSide) {
        int p = (side1 + side2 + baseSide) / 2;
        if (p == 0)
            return 0;

        double area = Math.sqrt(p * (p - side1) * (p - side2) * (p - baseSide));
        if (area == 0)
            return 0;

        return (int) (2 * area / baseSide);
    }

    /**
     * 三个点连成2条线，求这2条线之间的夹角(小于等于180度)， Connected是2条线相交的点
     **/
    public static double calcAngleBetween2Lines(double lat1, double lng1, double latConnected, double lngConnected, double lat2, double lng2) {

        double azimuth1 = calcAzimuth(latConnected, lngConnected, lat1, lng1);
        double azimuth2 = calcAzimuth(latConnected, lngConnected, lat2, lng2);
        double diff = azimuth1 - azimuth2;

        if (diff < 0)
            diff *= -1;

        //2条线平行（或重合）
        if (diff == 180)
            return 0;

        //相交的2条线之间有2个夹角，一个大于180，一个小于180，取小的那个
        //10度， 和330度 相差 320度， 但是其实他们直接的角度应该是40度（360-320）
        if (diff > 180)
            diff = 360 - diff;

        return diff;
    }
}
