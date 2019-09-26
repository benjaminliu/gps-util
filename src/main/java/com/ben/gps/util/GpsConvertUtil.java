package com.ben.gps.util;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 从世界84坐标系，转成火星坐标系,转成百度坐标系 <br>
 *
 * @author: 刘恒 <br>
 * @date: 2019/7/26 <br>
 */
public class GpsConvertUtil {

    //
    // Krasovsky 1940
    //
    // a = 6378245.0, 1/f = 298.3
    // b = a * (1 - f)
    // ee = (a^2 - b^2) / a^2;

    /**
     * 地球半径
     **/
    static final double a = 6378245.0;
    static final double ee = 0.00669342162296594323;

    static final double pi = 3.14159265358979324;

    /**
     * 转百度用
     **/
    static final double x_pi = pi * 3000.0 / 180.0;

    /**
     * 世界（84）转百度
     **/
    public static Pair<Double, Double> world2Baidu(double lat, double lon) {
        //中国以外不用转
        if (outsideChina(lat, lon)) {
            return Pair.of(lat, lon);
        }

        Pair<Double, Double> pair = world2Mars(lat, lon);

        return mars2Baidu(pair.getLeft(), pair.getRight());
    }

    /**
     * 世界（84）转火星
     **/
    public static Pair<Double, Double> world2Mars(double lat, double lon) {
        double mgLat;
        double mgLon;

        if (outsideChina(lat, lon)) {
            mgLat = lat;
            mgLon = lon;
        } else {
            double dLat = convertLat(lon - 105.0, lat - 35.0);
            double dLon = convertLon(lon - 105.0, lat - 35.0);

            double radLat = lat / 180.0 * pi;
            double magic = Math.sin(radLat);
            magic = 1 - ee * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
            dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
            mgLat = lat + dLat;
            mgLon = lon + dLon;
        }

        return Pair.of(mgLat, mgLon);
    }

    /**
     * 火星转百度
     **/
    public static Pair<Double, Double> mars2Baidu(double lat, double lon) {
        double z = Math.sqrt(lon * lon + lat * lat) + 0.00002 * Math.sin(lat * x_pi);
        double theta = Math.atan2(lat, lon) + 0.000003 * Math.cos(lon * x_pi);
        double resLon = z * Math.cos(theta) + 0.0065;
        double resLat = z * Math.sin(theta) + 0.006;
        return Pair.of(resLat, resLon);
    }

    /**
     * 是否是中国之外
     **/
    public static boolean outsideChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    static double convertLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    static double convertLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }
}
