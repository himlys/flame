package com.rain.flame.config.spring.beans.factory.annatation;

import com.rain.flame.common.Constants;
import com.rain.flame.config.annatation.Service;
import com.rain.flame.config.spring.ServiceBean;
import com.rain.flame.config.spring.context.annatation.FlameClassPathBeanDefinitionScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.rain.flame.config.spring.beans.factory.annatation.BeanReferenceUtils.getBeanNames;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ClassUtils.resolveClassName;

public class ServiceAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {
    private final Set<String> packagesToScan;
    private Environment environment;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    public ServiceAnnotationBeanPostProcessor(String... packagesToScan) {
        this(Arrays.asList(packagesToScan));
    }

    public ServiceAnnotationBeanPostProcessor(Collection<String> packagesToScan) {
        this(new LinkedHashSet<String>(packagesToScan));
    }

    public ServiceAnnotationBeanPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<String> resolvedPackagesToScan = resolvePackagesToScan(packagesToScan);
        if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
            registerServiceBeans(resolvedPackagesToScan, registry);
        } else {
        }
    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        FlameClassPathBeanDefinitionScanner scanner = new FlameClassPathBeanDefinitionScanner(registry, false, environment, resourceLoader);
        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);
        scanner.setBeanNameGenerator(beanNameGenerator);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        for (String packageToScan : packagesToScan) {
            scanner.scan(packageToScan);
            Set<BeanDefinitionHolder> beanDefinitionHolders =
                    findServiceBeanDefinitionHolders(scanner, packageToScan, registry, beanNameGenerator);
            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {
                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                    registerServiceBean(beanDefinitionHolder, registry, scanner);
                }
            }
        }
    }

    private void registerServiceBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry,
                                     FlameClassPathBeanDefinitionScanner scanner) {
        Class<?> beanClass = resolveClass(beanDefinitionHolder);
        Service service = findAnnotation(beanClass, Service.class);
        Class<?> interfaceClass = resolveServiceInterfaceClass(beanClass, service);
        String annotatedServiceBeanName = beanDefinitionHolder.getBeanName();
        AbstractBeanDefinition serviceBeanDefinition =
                buildServiceBeanDefinition(service, interfaceClass, annotatedServiceBeanName, registry);
        String beanName = generateServiceBeanName(service, interfaceClass);
        if (scanner.checkCandidate(beanName, serviceBeanDefinition)) {
            registry.registerBeanDefinition(beanName, serviceBeanDefinition);
        }
    }

    private String generateServiceBeanName(Service service, Class<?> interfaceClass) {
        StringBuilder beanNameBuilder = new StringBuilder(ServiceBean.class.getSimpleName());
        beanNameBuilder.append(Constants.SEPARATOR).append(interfaceClass.getSimpleName());
        String interfaceClassName = interfaceClass.getName();
        beanNameBuilder.append(Constants.SEPARATOR).append(interfaceClassName);
        String version = service.version();
        if (StringUtils.hasText(version)) {
            beanNameBuilder.append(Constants.SEPARATOR).append(version);
        }
        return beanNameBuilder.toString();
    }

    private AbstractBeanDefinition buildServiceBeanDefinition(Service service, Class<?> interfaceClass,
                                                              String annotatedServiceBeanName, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(ServiceBean.class);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        addPropertyReference(builder, "ref", annotatedServiceBeanName);
        builder.addPropertyValue("interfaceClass", interfaceClass);
        String[] protocolConfigBeanNames = service.protocol();
        String[] registryConfigBeanNames = service.registry();
        List<RuntimeBeanReference> registryRuntimeBeanReferences = toRuntimeBeanReferences(registry, "com.rain.flame.config.spring.RegistryConfig", "registry", registryConfigBeanNames);
        if (!registryRuntimeBeanReferences.isEmpty()) {
            builder.addPropertyValue("registries", registryRuntimeBeanReferences);
        }
        List<RuntimeBeanReference> protocolRuntimeBeanReferences = toRuntimeBeanReferences(registry, "com.rain.flame.config.spring.ProtocolConfig", "protocol", protocolConfigBeanNames);
        if (!protocolRuntimeBeanReferences.isEmpty()) {
            builder.addPropertyValue("protocols", protocolRuntimeBeanReferences);
        }
        return builder.getBeanDefinition();
    }

    private ManagedList<RuntimeBeanReference> toRuntimeBeanReferences(BeanDefinitionRegistry registry, String classz, String referenceType, String... beanNames) {
        ManagedList<RuntimeBeanReference> runtimeBeanReferences = new ManagedList<RuntimeBeanReference>();
        List<String> configs = getBeanNames(registry, this.getClass().getClassLoader(), classz);
        List<String> names = new ArrayList<>();
        for (String beanName : beanNames) {
            String name = getBeanName(beanName, referenceType);
            if (ifexistRegistryConfig(name, configs))
                names.add(name);
            else
                throw new RuntimeException("did not config " + referenceType + " " + beanName);
        }

        if (ObjectUtils.isEmpty(beanNames)) {
            names = configs;
        }
        for (String beanName : names) {
            if (!registry.containsBeanDefinition(beanName) && registry.getAliases(beanName).length <= 0)
                throw new RuntimeException(referenceType + " " + beanName + " did not config");
            String resolvedBeanName = environment.resolvePlaceholders(beanName);
            runtimeBeanReferences.add(new RuntimeBeanReference(resolvedBeanName));
        }
        return runtimeBeanReferences;

    }

    private boolean ifexistRegistryConfig(String beanName, List<String> registrysConfigs) {
        for (String registryConfig : registrysConfigs) {
            if (!StringUtils.isEmpty(beanName) && !StringUtils.isEmpty(registryConfig)
                    && beanName.equals(registryConfig))
                return true;
            return false;
        }
        return false;
    }

    private String getBeanName(String beanName, String referenceType) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(beanName).append(".").append(referenceType);
        return buffer.toString();
    }

    private void addPropertyReference(BeanDefinitionBuilder builder, String propertyName, String beanName) {
        String resolvedBeanName = environment.resolvePlaceholders(beanName);
        builder.addPropertyReference(propertyName, resolvedBeanName);
    }

    private Class<?> resolveServiceInterfaceClass(Class<?> annotatedServiceBeanClass, Service service) {
        Class<?> interfaceClass = service.interfaceClass();
        if (void.class.equals(interfaceClass)) {
            interfaceClass = null;
            String interfaceClassName = service.interfaceName();
            if (StringUtils.hasText(interfaceClassName)) {
                if (ClassUtils.isPresent(interfaceClassName, classLoader)) {
                    interfaceClass = resolveClassName(interfaceClassName, classLoader);
                }
            }
        }
        if (interfaceClass == null) {
            Class<?>[] allInterfaces = annotatedServiceBeanClass.getInterfaces();
            if (allInterfaces.length > 0) {
                interfaceClass = allInterfaces[0];
            }
        }
        return interfaceClass;
    }

    private Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {
        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();

        return resolveClass(beanDefinition);
    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        return resolveClassName(beanClassName, classLoader);
    }

    private Set<BeanDefinitionHolder> findServiceBeanDefinitionHolders(
            ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry registry,
            BeanNameGenerator beanNameGenerator) {
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);
        Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<BeanDefinitionHolder>(beanDefinitions.size());
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);
        }
        return beanDefinitionHolders;

    }

    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {
        BeanNameGenerator beanNameGenerator = null;
        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }
        if (beanNameGenerator == null) {
            beanNameGenerator = new AnnotationBeanNameGenerator();
        }
        return beanNameGenerator;

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
