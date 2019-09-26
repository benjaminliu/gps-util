package com.ben.gps.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

/**
 * — <br>
 *
 * @author: 刘恒 <br>
 * @date: 2019/9/26 <br>
 */

public class GpsUtilTest {

    @Test
    public void test1() {

        int distance = GpsUtil.calcDistanceMeter(23.412125815515367, 113.30386230145066, 26.612019446391354, 113.56976319554525);
        System.out.println(distance);

        double azimuth = GpsUtil.calcAzimuth(23.412125815515367, 113.30386230145066, 26.612019446391354, 113.56976319554525);
        System.out.println(azimuth);
        Pair<Double, Double> pair = GpsUtil.calcGpsPoint(23.412125815515367, 113.30386230145066, distance, azimuth);

        System.out.println(pair.getLeft());
        System.out.println(pair.getRight());

        int distance1 = GpsUtil.calcDistanceMeter(26.612019446391354, 113.56976319554525, pair.getLeft(), pair.getRight());
        System.out.println(distance1);

    }

}