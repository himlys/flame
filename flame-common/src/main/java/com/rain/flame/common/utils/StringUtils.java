package com.rain.flame.common.utils;

public class StringUtils {
    public static String upperFirstChar(String string){
        if(string == null || "".equals(string)) return null;
        char[] chars = string.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }
    public static boolean isContains(String[] values, String value) {
        if (isNotEmpty(value) && ArrayUtils.isNotEmpty(values)) {
            for (String v : values) {
                if (value.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
