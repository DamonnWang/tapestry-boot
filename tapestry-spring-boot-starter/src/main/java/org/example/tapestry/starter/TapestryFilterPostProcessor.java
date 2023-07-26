package org.example.tapestry.starter;

import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.ioc.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author Damon
 * @date 2023/6/26
 * 如果创建的bean是tapestryFilter, 则宣布启动tapestry
 **/
class TapestryFilterPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(TapestryFilterPostProcessor.class);


    private final TapestryAppInitializer appInitializer;
    private final Registry registry;

    public TapestryFilterPostProcessor(TapestryAppInitializer appInitializer, Registry registry) {
        log.debug("construct TapestryFilterPostProcessor, appInitializer: {}, registry: {}", appInitializer, registry);
        this.appInitializer = appInitializer;
        this.registry = registry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 在TapestryFilter初始化之前
        if (bean.getClass() == TapestryFilter.class) {
            log.debug("postProcessBefore TapestryFilter Initialization");
            //这个方法是 Tapestry 框架中的一个核心方法，它用于执行注册表（Registry）的启动过程。
            // 在一个应用程序中，通常有许多服务和组件需要在启动时进行初始化，例如数据库连接、依赖注入容器等。
            // 注册表是一个集中管理这些服务和组件的地方。
            // performRegistryStartup() 方法会触发注册表中所有服务和组件的初始化过程，确保它们在应用程序启动时正确地配置和准备好
            registry.performRegistryStartup();
            // 这个方法执行的操作是清理或终止一些与线程相关的资源或任务。线程是程序执行的最小单位，它们在程序运行过程中可能会创建、启动和终止。
            // 在某些情况下，线程执行完任务后可能会留下一些垃圾数据或资源未及时释放，这可能导致内存泄漏或其他问题。
            // cleanupThread() 方法的目的就是确保在适当的时机进行资源释放和线程清理，以优化应用程序的性能和稳定性。
            registry.cleanupThread();
            // 这个方法的作用是通知应用程序初始化已经完成，可能会触发一些相关的动作或回调函数。
            // 在大型应用程序中，初始化过程可能涉及多个子系统的配置和加载，例如数据库连接、缓存设置、安全策略等。
            // 当这些初始化任务完成后，通常希望通知其他组件或模块，以便它们可以开始执行其它任务或显示用户界面。
            // announceStartup() 方法可能会向监听器、事件总线或其它关注应用程序启动完成事件的部分发送通知。
            appInitializer.announceStartup();
            log.debug("postProcessBefore TapestryFilter initialization complete");
        }
        return bean;
    }

}
