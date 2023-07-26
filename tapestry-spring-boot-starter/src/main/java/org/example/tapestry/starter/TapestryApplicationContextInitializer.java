package org.example.tapestry.starter;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * Tapestry自动装配的入口, 在整个入口初始化整个tapestry框架
 * 在此入口中, 向applicationContext中注册了 {@link TapestryBeanFactoryPostProcessor} 的Bean工厂处理器 {@link BeanFactoryPostProcessor}
 *
 */
public class TapestryApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    private static final Logger log = LoggerFactory.getLogger(TapestryApplicationContextInitializer.class);

    private final AtomicBoolean inited = new AtomicBoolean(false);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.debug("initialize Tapestry ApplicationContextInitializer");
        if (inited.get()) {
            return;
        }
        TapestryBeanFactoryPostProcessor tapestryBeanFactoryPostProcessor = new TapestryBeanFactoryPostProcessor((AnnotationConfigServletWebServerApplicationContext) applicationContext);
        applicationContext.addBeanFactoryPostProcessor(tapestryBeanFactoryPostProcessor);
        log.debug("registered Tapestry BeanFactoryPostProcessor {} to applicationContext {}", tapestryBeanFactoryPostProcessor, applicationContext);
        inited.set(true);

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
