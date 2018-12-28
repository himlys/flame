package com.rain.flame.config.spring.convert.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class StringArrayToStringConverter implements Converter<String[], String> {
    @Override
    public String convert(String[] source) {
        return ObjectUtils.isEmpty(source) ? null : StringUtils.arrayToCommaDelimitedString(source);
    }

}
