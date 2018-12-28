package com.rain.flame.config.spring.convert.converter;

import com.rain.flame.common.utils.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ObjectUtils;

import java.util.Map;

public class StringArrayToMapConverter implements Converter<String[], Map<String, String>> {

    @Override
    public Map<String, String> convert(String[] source) {
        return ObjectUtils.isEmpty(source) ? null : CollectionUtils.toStringMap(source);
    }

}