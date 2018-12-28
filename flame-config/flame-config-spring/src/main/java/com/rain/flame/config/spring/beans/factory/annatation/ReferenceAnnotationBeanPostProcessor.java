package com.rain.flame.config.spring.beans.factory.annatation;

import com.rain.flame.common.Constants;
import com.rain.flame.config.annatation.Reference;
import com.rain.flame.config.spring.ReferenceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

public class ReferenceAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, PriorityOrdered, ApplicationContextAware, BeanClassLoaderAware,
        DisposableBean {
    public static final String BEAN_NAME = "referenceAnnotationBeanPostProcessor";
    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    private final ConcurrentMap<String, ReferenceInjectionMetadata> injectionMetadataCache =
            new ConcurrentHashMap<String, ReferenceInjectionMetadata>(256);

    private final ConcurrentMap<String, ReferenceBean<?>> referenceBeansCache =
            new ConcurrentHashMap<String, ReferenceBean<?>>();

    private static final Map<Class<? extends Annotation>, List<Method>> annotationMethodsCache =
            new ConcurrentReferenceHashMap<Class<? extends Annotation>, List<Method>>(256);

    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = findReferenceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return pvs;
    }

    private InjectionMetadata findReferenceMetadata(String beanName, Class<?> classz, PropertyValues pvs) {
        String cacheKey = (beanName == null || "".equals(beanName) ? beanName : classz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        ReferenceInjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, classz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, classz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildReferenceMetadata(classz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect bean class [" + classz.getName() +
                                "] for reference metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    private List<ReferenceFieldElement> findFieldReferenceMetadata(final Class<?> beanClass) {
        final List<ReferenceFieldElement> elements = new LinkedList<ReferenceFieldElement>();
        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Reference reference = getAnnotation(field, Reference.class);
                if (reference != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        return;
                    }
                    elements.add(new ReferenceFieldElement(field, reference));
                }
            }
        });

        return elements;

    }

    private ReferenceInjectionMetadata buildReferenceMetadata(final Class<?> beanClass) {
        Collection<ReferenceFieldElement> fieldElements = findFieldReferenceMetadata(beanClass);
        return new ReferenceInjectionMetadata(beanClass, fieldElements);

    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private static class ReferenceInjectionMetadata extends InjectionMetadata {
        private final Collection<ReferenceFieldElement> fieldElements;

        public ReferenceInjectionMetadata(Class<?> targetClass, Collection<ReferenceFieldElement> elements) {
            super(targetClass, combine(elements));
            this.fieldElements = elements;
        }

        private static <T> Collection<T> combine(Collection<? extends T>... elements) {
            List<T> allElements = new ArrayList<T>();
            for (Collection<? extends T> e : elements) {
                allElements.addAll(e);
            }
            return allElements;
        }
    }

    private class ReferenceFieldElement extends InjectionMetadata.InjectedElement {
        private final Field field;
        private final Reference reference;
        private volatile ReferenceBean<?> referenceBean;

        protected ReferenceFieldElement(Field field, Reference reference) {
            super(field, null);
            this.field = field;
            this.reference = reference;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Class<?> referenceClass = field.getType();
            referenceBean = buildReferenceBean(reference, referenceClass);
            ReflectionUtils.makeAccessible(field);
            field.set(bean, referenceBean.getObject());

        }

        private ReferenceBean<?> buildReferenceBean(Reference reference, Class<?> referenceClass) throws Exception {
            String referenceBeanCacheKey = generateReferenceBeanCacheKey(reference, referenceClass);
            ReferenceBean<?> referenceBean = referenceBeansCache.get(referenceBeanCacheKey);
            if (referenceBean == null) {
                ReferenceBeanBuilder beanBuilder = ReferenceBeanBuilder.create(reference, classLoader, applicationContext)
                        .interfaceClass(referenceClass);
                referenceBean = beanBuilder.build();
                referenceBeansCache.putIfAbsent(referenceBeanCacheKey, referenceBean);
            }
            return referenceBean;
        }

        private String generateReferenceBeanCacheKey(Reference reference, Class<?> beanClass) {
            String key = resolveReferenceKey(annotationValues(reference));
            Environment environment = applicationContext.getEnvironment();
            key = environment.resolvePlaceholders(key);
            if (StringUtils.isEmpty(key)) {
                key = resolveInterfaceName(reference, beanClass);
            }
            return key;
        }

        private String resolveInterfaceName(Reference reference, Class<?> beanClass)
                throws IllegalStateException {
            String interfaceName;
            if (!"".equals(reference.interfaceName())) {
                interfaceName = reference.interfaceName();
            } else if (!void.class.equals(reference.interfaceClass())) {
                interfaceName = reference.interfaceClass().getName();
            } else if (beanClass.isInterface()) {
                interfaceName = beanClass.getName();
            } else {
                throw new IllegalStateException(
                        "The @Reference undefined interfaceClass or interfaceName, and the property type "
                                + beanClass.getName() + " is not a interface.");
            }
            return interfaceName;

        }

        private String resolveReferenceKey(Map<String, Object> annotations) {
            Iterator<Map.Entry<String, Object>> annotationVisitor = annotations.entrySet().iterator();
            StringBuilder builder = new StringBuilder();
            while (annotationVisitor.hasNext()) {
                Map.Entry<String, Object> attribute = annotationVisitor.next();
                String attributeValue = null;
                if (attribute.getValue() instanceof String[]) {
                    attributeValue = toPlainString((String[]) attribute.getValue());
                } else {
                    attributeValue = attribute.getValue() == null ? "" : attribute.getValue().toString();
                }

                if (attributeValue == null || "".equals(attributeValue)) {
                    if (builder.length() > 0) {
                        builder.append(Constants.URL_SPLIT);
                    }
                    builder.append(attributeValue);
                }
            }
            return builder.toString();
        }

        private String toPlainString(String[] array) {
            if (array == null || array.length == 0) {
                return "";
            }
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                if (i > 0) {
                    buffer.append(Constants.SEPARATOR);
                }
                buffer.append(array[i]);
            }
            return buffer.toString();
        }
    }

    private Map<String, Object> annotationValues(Annotation annotation) {
        Map<String, Object> annotations = new LinkedHashMap<>();

        for (Method method : getAnnotationMethods(annotation.annotationType())) {
            try {
                Object attributeValue = method.invoke(annotation);
                Object defaultValue = method.getDefaultValue();
                if (nullSafeEquals(attributeValue, defaultValue)) {
                    continue;
                }
                annotations.put(method.getName(), attributeValue);
            } catch (Throwable e) {
                throw new IllegalStateException("Failed to obtain annotation attribute value for " + method, e);
            }
        }
        return annotations;
    }

    private static List<Method> getAnnotationMethods(Class<? extends Annotation> annotationType) {
        List<Method> methods = annotationMethodsCache.get(annotationType);
        if (methods != null) {
            return methods;
        }

        methods = new ArrayList<Method>();
        for (Method method : annotationType.getDeclaredMethods()) {
            if (isAnnotationMethod(method)) {
                ReflectionUtils.makeAccessible(method);
                methods.add(method);
            }
        }

        annotationMethodsCache.put(annotationType, methods);
        return methods;
    }

    private static boolean isAnnotationMethod(Method method) {
        return (method != null
                && method.getParameterTypes().length == 0
                && method.getReturnType() != void.class);
    }

    private static boolean nullSafeEquals(Object first, Object another) {
        return ObjectUtils.nullSafeEquals(first, another);
    }
}