package com.active.qa.automation.web.testapi.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

/**
 * Wrap the convenient functions for manipulating numbers
 * Created by tchen on 1/11/2016.
 */
public class NumberUtil {
    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isGreaterThanZero(Object obj) {
        if(obj instanceof Integer) {
            return ((Integer)obj) > 0;
        } else if(obj instanceof Double) {
            return ((Double)obj) > 0;
        }

        return false;
    }

    public static boolean isEqualToZero(Object obj){
        if(obj instanceof Integer) {
            return ((Integer)obj) == 0;
        } else if(obj instanceof Double) {
            return ((Double)obj) == 0;
        }

        return false;
    }


    public static double getFractionalPart(Object from) {
        String fromStr = (from instanceof Double) ? String.valueOf(from) : (String)from;
        String temp[] = fromStr.split("\\.");
        String toStr = "0." + temp[1];

        return Double.parseDouble(toStr);
    }

    public static boolean isInteger(Object from) {
        String str = "";
        if(from instanceof String) {
            str = (String)from;
        } else {
            str = String.valueOf(from);
        }
        return str.matches("[\\d]+");
    }

    /**
     *
     * @param d1
     * @param d2
     * @param scale
     * @return
     */
    public static double subtractDouble(double d1, double d2, int scale) {
        BigDecimal decimal1 = new BigDecimal(d1);
        BigDecimal decimal2 = new BigDecimal(d2);
        BigDecimal result = decimal1.subtract(decimal2).setScale(scale, BigDecimal.ROUND_HALF_UP);
//		String str = result.toString();
//		int index = str.indexOf('.');
//		if(str.substring(index).matches(".0+")){
//			str = str.replaceAll("\\.0+", ".00");
//		}
        return result.doubleValue();
    }

    /**
     *
     * @param s1
     * @param s2
     * @param scale
     * @return
     */
    public static String subtractDouble(String s1, String s2, int scale) {
        BigDecimal decimal1 = new BigDecimal(s1);
        BigDecimal decimal2 = new BigDecimal(s2);
        BigDecimal result = decimal1.subtract(decimal2).setScale(scale, BigDecimal.ROUND_HALF_UP);

        return result.toString();
    }

    public static String convertFormat(String from, int scale) {
        return convertFormat(from, scale, scale);
    }

    public static String convertFormat(String from, int scale, int scaleForTrailingZero) {
        return convertFormat(from, scale, scaleForTrailingZero, true);
    }

    /**
     * This method was used to convert String number to BigDecimal according to scale,
     * If ignore trailing zero for String number, use scaleto to convert the String number
     * for example: 28.0000(scaleForTrailingZero:1)-->28.0; 28.0000(scaleForTrailingZero:2)-->28.00
     * @param from -- String number
     * @param scale -- scale for Number String
     * @param scaleForTrailingZero -- scale for trailing zero
     * @param ignoreTrailingZero -- ignore trailing zero or not
     * @return
     */
    public static String convertFormat(String from, int scale, int scaleForTrailingZero, boolean ignoreTrailingZero) {
        BigDecimal decimal = new BigDecimal(from).setScale(scale, BigDecimal.ROUND_HALF_UP);
        if(ignoreTrailingZero) {
            if(decimal.compareTo(new BigDecimal(decimal.intValue())) == 0) {
                decimal = new BigDecimal(decimal.toString()).setScale(scaleForTrailingZero);//3.0000---->3.00
            } else {
                decimal = decimal.stripTrailingZeros();
            }
        }

        return decimal.toString();
    }

    public static boolean exactEquals(String s1, String s2) {
        BigDecimal decimal1 = new BigDecimal(s1);
        BigDecimal decimal2 = new BigDecimal(s2);

        return decimal1.equals(decimal2);
    }

    public static boolean exactEquals(double d1, double d2) {
        BigDecimal decimal1 = new BigDecimal(String.valueOf(d1));
        BigDecimal decimal2 = new BigDecimal(String.valueOf(d2));

        return decimal1.equals(decimal2);
    }

    public static boolean valueEquals(double d1, double d2) {
        BigDecimal decimal1 = new BigDecimal(String.valueOf(d1));
        BigDecimal decimal2 = new BigDecimal(String.valueOf(d2));

        return decimal1.compareTo(decimal2) == 0 ? true : false;
    }

    public static String getUniqueSSN() {
        String str="9999"+new DecimalFormat("00000").format(Math.random()*10000);

        return str.substring(str.length()-9);
    }

    /**
     * generate a random integer value which is less than or equals with [max] and greater than or equals with [min]
     * @param min
     * @param max
     * @return
     */
    public static int getRandomInt(int min, int max) {
        Random r = new Random();
        int toReturn = r.nextInt(max);
        if(toReturn < min) {
            toReturn = getRandomInt(min, max);
        }

        return toReturn;
    }

    public static double getRandomDouble(int min, int max) {
        return getRandomDouble(min, max, 2);
    }

    public static double getRandomDouble(int min, int max, int scale) {
        Random r = new Random();
        int intValue = getRandomInt(min, max - 1);
        double doubleValue = r.nextDouble();
        BigDecimal bd = new BigDecimal(doubleValue).setScale(scale, BigDecimal.ROUND_HALF_UP);
        double toReturn = intValue + bd.doubleValue();
        if(toReturn < min) {
            toReturn = getRandomDouble(min, max);
        }

        return toReturn;
    }

    public static double getRandomDouble(double min, double max) {
        return getRandomDouble((int)min, (int)max);
    }

    public static boolean verifyDecimalListSortingByAsc (List<BigDecimal> valueList) {
        boolean sorted = true;
        for(int i=0;i<valueList.size()-1;i++) {
            BigDecimal value1 = valueList.get(i);
            BigDecimal value2 = valueList.get(i+1);
            int compared = value1.compareTo(value2);
            if(compared<=0)
                sorted &= true;
            else
                sorted &= false;
        }

        return sorted;
    }

    public static boolean isEven(int num) {
        if((num & 1) ==0) {
            return true;
        } else {
            return false;
        }
    }
}

