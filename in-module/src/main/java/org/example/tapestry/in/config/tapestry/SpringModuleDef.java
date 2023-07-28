package org.example.tapestry.in.config.tapestry;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.internal.AbstractContributionDef;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ModuleBuilderSource;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.def.ContributionDef;
import org.apache.tapestry5.ioc.def.DecoratorDef;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.def.ServiceDef;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.plastic.PlasticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 一个包装器，它将Spring ApplicationContext转换为一组服务定义，与Tapestry 5 IoC兼容，用于上下文中定义的bean以及上下文本身
 * SpringModuleDef 是自定义的一个类，实现了 ModuleDef 接口，用于将 Spring 的 ApplicationContext 转换为一组 Tapestry 兼容的服务定义。
 * 它在构造函数中通过遍历 Spring 的 Bean 定义，将其中的 Bean 信息转换为 Tapestry 的服务定义，并存储在 services 集合中
 */
public class SpringModuleDef implements ModuleDef {
    private static final Logger log = LoggerFactory.getLogger(SpringModuleDef.class);

    private final Map<String, ServiceDef> services = CollectionFactory.newMap();
    private final ApplicationContext applicationContext;
    private final Set<ContributionDef> contributionDefs = CollectionFactory.newSet();

    public SpringModuleDef(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        addServiceDefsForSpringBeans(applicationContext);
    }

    private void addServiceDefsForSpringBeans(ApplicationContext applicationContext) {
        for (final String beanName : applicationContext.getBeanDefinitionNames()) {
            String trueName = beanName.startsWith("&") ? beanName.substring(1) : beanName;
            if (beanName.equals("referenceAnnotationBeanPostProcessor")) {
                log.debug("ignore Bean: referenceAnnotationBeanPostProcessor");
                // Dubbo整合异常, Dubbo会移除此bean, 导致启动异常, 故此忽略该bean, 不加入services中
                continue;
            }
            services.put(trueName, new SpringBeanServiceDef(trueName, applicationContext));
        }
    }


    @Override
    public Class<?> getBuilderClass() {
        return null;
    }

    /**
     * Returns a contribution, "SpringBean", to the MasterObjectProvider service. It is ordered
     * after the built-in
     * contributions.
     */
    @Override
    public Set<ContributionDef> getContributionDefs() {
        // log.debug("getContributionDefs");
        if (contributionDefs.isEmpty()) {
            ContributionDef def = createContributionToMasterObjectProvider();
            contributionDefs.addAll(CollectionFactory.newSet(def));
        }
        return contributionDefs;
    }

    private ContributionDef createContributionToMasterObjectProvider() {
        log.info("createContributionToMasterObjectProvider");
        return new AbstractContributionDef() {
            @Override
            public String getServiceId() {
                return "MasterObjectProvider";
            }

            @Override
            @SuppressWarnings("unchecked")
            public void contribute(ModuleBuilderSource moduleSource, ServiceResources resources, OrderedConfiguration configuration) {
                final OperationTracker tracker = resources.getTracker();

                final ObjectProvider springBeanProvider = new ObjectProvider() {
                    @Override
                    public <T> T provide(Class<T> objectType, AnnotationProvider annotationProvider, ObjectLocator locator) {
                        // 先byType
                        Map<String, T> beanMap = applicationContext.getBeansOfType(objectType);
                        switch (beanMap.size()) {
                            case 0:
                                return null;
                            case 1:
                                Object bean = beanMap.values().iterator().next();
                                return objectType.cast(bean);
                            default:
                                // 一个Type多个实现, 此时再byName找出具体的bean
                                Service service = annotationProvider.getAnnotation(Service.class);
                                if (service != null) {
                                    String value = service.value();
                                    if (value != null && value.length() > 0) {
                                        Object namedBean = applicationContext.getBean(value);
                                        return objectType.cast(namedBean);
                                    }
                                }

                                String message = String.format(
                                        "Spring context contains %d beans assignable to type %s: %s.",
                                        beanMap.size(), PlasticUtils.toTypeName(objectType), InternalUtils.joinSorted(beanMap.keySet()));
                                throw new IllegalArgumentException(message);
                        }
                    }
                };

                final ObjectProvider springBeanProviderInvoker = new ObjectProvider() {
                    @Override
                    public <T> T provide(final Class<T> objectType, final AnnotationProvider annotationProvider, final ObjectLocator locator) {
                        return tracker.invoke(
                                "Resolving dependency by searching Spring ApplicationContext",
                                () -> springBeanProvider.provide(objectType, annotationProvider, locator));
                    }
                };

                configuration.add("SpringBean", springBeanProviderInvoker, "after:AnnotationBasedContributions", "after:ServiceOverride");
            }
        };
    }

    @Override
    public Set<DecoratorDef> getDecoratorDefs() {
        return Collections.emptySet();
    }

    @Override
    public String getLoggerName() {
        return SpringModuleDef.class.getName();
    }

    @Override
    public ServiceDef getServiceDef(String serviceId) {
        return services.get(serviceId);
    }

    @Override
    public Set<String> getServiceIds() {
        return services.keySet();
    }
}

