package com.spark.chiefwallet.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/18
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class test {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        strings.add("4123431234");
        strings.add("5");
        for (String string : strings) {
            if (string.equals(4)) {
                string.substring(0,4);
            }
        }
        System.out.println(strings.get(3));
    }

    /**
     * 精确除法
     *
     * @param scale 精度
     */
    public static double divUp(double value1, double value2, int scale) throws IllegalAccessException {
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        // return b1.divide(b2, scale).doubleValue();
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     *
     * @param scale 小数点后保留几位
     */
    public static double roundUp(double v, int scale) throws IllegalAccessException {
        return divUp(v, 1, scale);
    }
}
