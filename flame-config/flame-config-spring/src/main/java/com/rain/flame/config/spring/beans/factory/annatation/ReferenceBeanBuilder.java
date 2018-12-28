package com.rain.flame.config.spring.beans.factory.annatation;

import com.rain.flame.config.annatation.Reference;
import com.rain.flame.config.spring.ReferenceBean;
import com.rain.flame.config.spring.convert.converter.StringArrayToMapConverter;
import com.rain.flame.config.spring.convert.converter.StringArrayToStringConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

import static com.rain.flame.common.utils.ObjectUtils.of;

class ReferenceBeanBuilder extends AbstractAnnotationConfigBeanBuilder<Reference, ReferenceBean> {

    private ReferenceBeanBuilder(Reference annotation, ClassLoader classLoader, ApplicationContext applicationContext) {
        super(annotation, applicationContext, classLoader);
    }

    @Override
    protected ReferenceBean doBuild() {
        return new ReferenceBean<Object>();
    }

    @Override
    protected String[] resolveRegistryConfigBeanNames(Reference annotation) {
        return annotation.registry();
    }

    @Override
    protected void preConfigureBean(Reference annotation, ReferenceBean bean) {
        DataBinder dataBinder = new DataBinder(bean);
        dataBinder.setConversionService(getConversionService());
        String[] ignoreAttributeNames = of("application", "module", "consumer", "monitor", "registry");
        dataBinder.bind(new AnnotationPropertyValuesAdapter(annotation, applicationContext.getEnvironment(), ignoreAttributeNames));
    }

    private ConversionService getConversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new StringArrayToStringConverter());
        conversionService.addConverter(new StringArrayToMapConverter());
        return conversionService;
    }

    @Override
    protected void postConfigureBean(Reference annotation, ReferenceBean bean) {
        bean.setApplicationContext(applicationContext);
        configureInterface(annotation, bean);
        bean.afterPropertiesSet();
    }

    private void configureInterface(Reference reference, ReferenceBean referenceBean) {
        Class<?> interfaceClass = reference.interfaceClass();
        if (void.class.equals(interfaceClass)) {
            interfaceClass = null;
            String interfaceClassName = reference.interfaceName();
            if (StringUtils.hasText(interfaceClassName)) {
                if (ClassUtils.isPresent(interfaceClassName, classLoader)) {
                    interfaceClass = ClassUtils.resolveClassName(interfaceClassName, classLoader);
                }
            }
        }
        if (interfaceClass == null) {
            interfaceClass = this.interfaceClass;
        }
        referenceBean.setInterface(interfaceClass);

    }

    public static ReferenceBeanBuilder create(Reference annotation, ClassLoader classLoader,
                                              ApplicationContext applicationContext) {
        return new ReferenceBeanBuilder(annotation, classLoader, applicationContext);
    }
}