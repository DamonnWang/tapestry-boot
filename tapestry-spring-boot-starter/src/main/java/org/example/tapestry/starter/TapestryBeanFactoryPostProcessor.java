package org.example.tapestry.starter;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.internal.util.DelegatingSymbolProvider;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.services.ServiceActivityScoreboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * 协调整个tapestry集成的BeanFactoryPostProcessor
 */
public class TapestryBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    public static final String SPRING_CONTEXT_PATH = "server.servlet.context-path";
    public static final String PROPERTY_APPMODULE = "tapestry.integration.appmodule";
    private static final Logger log = LoggerFactory.getLogger(TapestryBeanFactoryPostProcessor.class);
    protected final AnnotationConfigServletWebServerApplicationContext applicationContext;

    public TapestryBeanFactoryPostProcessor(AnnotationConfigServletWebServerApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.debug("postProcessBeanFactory, beanFactory: {}", beanFactory);
        // ... .services.AppModule.class, 获取 Tapestry AppModule 类的全限定名
        String appModuleClass = applicationContext.getEnvironment().getProperty(PROPERTY_APPMODULE, "");
        if (!StringUtils.hasText(appModuleClass)) {
            throw new IllegalStateException("Tapestry AppModule not found");
        }
        log.debug("Found Tapestry AppModule class: {}", appModuleClass);

        // 设置 Tapestry 框架所需的配置信息
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        Map<String, Object> tapestryContext = new HashMap<>();

        // 获取 Tapestry AppModule 类的简短名"APP"作为过滤器名
        String filterName = appModuleClass.substring(appModuleClass.lastIndexOf('.') + 1).replace("Module", "");
        tapestryContext.put("tapestry.filter-name", filterName);

        //从两个可能的配置途径获取contextPath上下文路径和执行模式，并设置到 Tapestry 的配置中
        String servletContextPath = environment.getProperty(SymbolConstants.CONTEXT_PATH, environment.getProperty(SPRING_CONTEXT_PATH, ""));
        tapestryContext.put(SymbolConstants.CONTEXT_PATH, servletContextPath);

        // 执行模式 (是否生产模式)
        String executionMode = environment.getProperty(SymbolConstants.EXECUTION_MODE, "production");
        tapestryContext.put(SymbolConstants.EXECUTION_MODE, executionMode);

        // 获取 Tapestry 应用程序的根包名，并设置到 Tapestry 的配置中
        String rootPackageName = appModuleClass.substring(0, appModuleClass.lastIndexOf('.')).replace(".services", "");
        tapestryContext.put(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, rootPackageName);

        // 将 Tapestry 的配置信息加入到 Spring 的环境变量中
        /*
        当我们创建 TapestryAppInitializer 对象时，会设置一些 Tapestry 框架所需的配置信息，例如上下文路径、执行模式、Tapestry 应用程序包名等。
        这些配置信息存储在一个名为 tapestryContext 的 Map 中，其中包含了键值对形式的配置项。
        但是，Tapestry 框架并不直接使用 Spring 的环境变量来获取这些配置信息。

        在这段代码中，我们通过 MapPropertySource 将 tapestryContext 中的配置信息添加到 Spring 的环境变量中，以便 Tapestry 和 Spring 可以共享这些配置项。

        MapPropertySource 是 Spring 提供的一个实现，它可以将 Map 中的键值对作为属性添加到 Spring 的环境变量中，这样就可以通过 Spring 的 Environment 对象获取这些配置信息。
        通过调用 environment.getPropertySources().addFirst(new MapPropertySource("tapestry-context", tapestryContext));
        将 tapestryContext 中的配置信息添加到 Spring 的环境变量的第一个位置，这样这些配置项的优先级就会高于其他来源的配置信息。
        这意味着 Tapestry 框架优先使用 tapestryContext 中的配置，如果找不到对应的配置项，才会去其他地方查找。
        这样，Tapestry 和 Spring 就可以共享相同的配置信息，而且 Tapestry 会优先使用添加的这些配置项。
        这种方式方便了在 Tapestry 和 Spring 之间共享配置，保证了两者的配置信息一致性，让整合过程更加灵活和高效。
         */
        environment.getPropertySources().addFirst(new MapPropertySource("tapestry-context", tapestryContext));

        // 创建一个符号提供者，用于提供 Tapestry 配置中的符号值
        DelegatingSymbolProvider combinedProvider = new DelegatingSymbolProvider(
                symbolName -> applicationContext.getEnvironment().getProperty(symbolName),
                new SingleKeySymbolProvider(SymbolConstants.CONTEXT_PATH, servletContextPath),
                new SingleKeySymbolProvider(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, rootPackageName),
                new SingleKeySymbolProvider(SymbolConstants.EXECUTION_MODE, executionMode)
        );
        log.debug("to start Tapestry app module: {}, filterName: {}, executionMode: {}", appModuleClass, filterName, executionMode);

        // 创建 TapestryAppInitializer 对象，用于启动 Tapestry 框架
        TapestryAppInitializer tapestryAppInitializer = new TapestryAppInitializer(
                LoggerFactory.getLogger("TapestryAppInitializer"), combinedProvider, filterName, executionMode);

        // 将 SpringModuleDef 类添加到 TapestryAppInitializer 对象的模块列表中。
        // 这样，TapestryAppInitializer 在初始化时会识别并加载 SpringModuleDef 中定义的服务，从而使得 Tapestry 和 Spring 可以共享并使用同一组服务
        log.debug("add Spring Beans to Tapestry");
        tapestryAppInitializer.addModules(new SpringModuleDef(applicationContext));

        tapestryAppInitializer.addModules(AssetSourceModule.class);

        // 创建 Tapestry 的 Registry 对象，用于管理应用程序中的服务和组件
        /*
            在 Tapestry 框架中，Registry 扮演着一个重要的角色，它的作用是充当一个集中管理和存储应用程序组件的中心容器。具体来说，Registry 的作用包括以下几个方面：
            1. 组件注册与解析： Registry 负责注册和解析应用程序中的各种服务和组件。当你编写一个自定义的服务、组件或页面时，你可以通过 Registry 将它注册到框架中，从而使其他部分能够方便地访问和使用它。其他组件可以通过 Registry 中的接口来获取所需的服务实例，而无需直接关注服务的具体实现。
            2. 依赖注入： Tapestry 是一个依赖注入（Dependency Injection）框架，Registry 在其中扮演着重要的角色。通过 Registry，Tapestry 能够管理组件之间的依赖关系。当一个组件需要依赖其他组件时，Tapestry 将会在 Registry 中查找并注入相应的依赖，使组件之间能够协同工作。
            3. 组件的生命周期管理： Registry 负责管理组件的生命周期。它会在合适的时机创建和初始化组件，并在应用程序关闭时进行销毁和清理操作。这确保了组件的正确初始化和释放，避免了资源泄漏和不必要的内存占用。
            4. 应用程序配置管理： Registry 还可以用于存储和管理应用程序的配置信息。你可以将一些全局配置信息注册到 Registry 中，使其在整个应用程序中都可以被访问和使用。这样，你可以更加灵活地管理应用程序的配置，甚至在运行时动态修改配置。
            总的来说，Registry 在 Tapestry 框架中扮演了一个重要的角色，它是一个中心容器，负责组件的注册、解析、依赖注入和生命周期管理，同时也提供了一种存储和管理应用程序配置的机制，使得应用程序开发更加灵活和方便。
         */
        log.debug("create tapestry registry");
        Registry registry = tapestryAppInitializer.createRegistry();

        // 创建 TapestryFilterPostProcessor 对象，并将其添加到 Spring 的 BeanPostProcessor 链中
        // 这个后处理器会在扫描到 TapestryFilter 过滤器的初始化之前，来执行 Registry 的启动过程
        TapestryFilterPostProcessor tapestryFilterPostProcessor = new TapestryFilterPostProcessor(tapestryAppInitializer, registry);
        beanFactory.addBeanPostProcessor(tapestryFilterPostProcessor);
        log.debug("beanFactory addBeanPostProcessor {}", tapestryFilterPostProcessor);

        // 将 Tapestry 的服务注册到 Spring 的 Bean 工厂中，使得 Spring 可以访问和使用这些 Tapestry 的服务
        ServiceActivityScoreboard scoreboard = registry.getService(ServiceActivityScoreboard.class);
        scoreboard.getServiceActivity().forEach(service -> {
            if (service.getServiceInterface().getPackage().getName().startsWith(combinedProvider.valueForSymbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM) + ".services")
                    || !service.getMarkers().isEmpty()
                    || service.getServiceInterface().getName().contains("tapestry5")) {

                Object proxy = registry.getService(service.getServiceId(), (Class<?>) service.getServiceInterface());
                // 将 Tapestry的服务注册到Spring中
                beanFactory.registerResolvableDependency(service.getServiceInterface(), proxy);
                // log.debug("Tapestry service {} exposed to spring", service.getServiceId());
            }
        });

        // 将 Tapestry 的 Registry 对象注册为一个可解析的依赖项，使得 Spring 可以使用它
        log.debug("register Tapestry Registry to Spring (Still pending initialization)");
        beanFactory.registerResolvableDependency(Registry.class, registry);
        // 注入TapestryFilter
        beanFactory.registerSingleton("tapestryFilter", new TapestryFilter(registry));

        // 扫描当前包以及子包，找到 TapestryFilter 并对其进行后处理，完成 Tapestry 框架的初始化
        /* 改为表述更为清晰的通过 beanFactory.registerSingleton()方式注入的方式
        String packagePath = this.getClass().getPackage().getName();
        log.debug("scan BeanDefinition in class path {}", packagePath);
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(applicationContext);
        scanner.scan(packagePath);*/
    }


    /**
     * @return 此后处理器的优先级
     */
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
