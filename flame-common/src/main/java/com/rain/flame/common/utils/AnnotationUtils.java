package com.rain.flame.common.utils;

import org.springframework.core.env.PropertyResolver;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.String.valueOf;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes;
import static org.springframework.core.annotation.AnnotationUtils.getDefaultValue;
import static org.springframework.util.CollectionUtils.arrayToList;
import static org.springframework.util.ObjectUtils.nullSafeEquals;
import static org.springframework.util.StringUtils.trimAllWhitespace;
public class AnnotationUtils {
    public static Map<String, Object> getAttributes(Annotation annotation, PropertyResolver propertyResolver,
                                                    boolean ignoreDefaultValue, String... ignoreAttributeNames) {
        Set<String> ignoreAttributeNamesSet = new HashSet<String>(arrayToList(ignoreAttributeNames));
        Map<String, Object> attributes = getAnnotationAttributes(annotation);
        Map<String, Object> actualAttributes = new LinkedHashMap<String, Object>();
        boolean requiredResolve = propertyResolver != null;
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            Object attributeValue = entry.getValue();
            if (ignoreDefaultValue && nullSafeEquals(attributeValue, getDefaultValue(annotation, attributeName))) {
                continue;
            }
            if (ignoreAttributeNamesSet.contains(attributeName)) {
                continue;
            }
            if (requiredResolve && attributeValue instanceof String) { // Resolve Placeholder
                String resolvedValue = propertyResolver.resolvePlaceholders(valueOf(attributeValue));
                attributeValue = trimAllWhitespace(resolvedValue);
            }
            actualAttributes.put(attributeName, attributeValue);
        }
        return actualAttributes;
    }

}
