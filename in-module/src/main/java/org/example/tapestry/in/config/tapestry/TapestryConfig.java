package org.example.tapestry.in.config.tapestry;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author Damon
 * @date 2023/7/28
 **/
@Configuration
public class TapestryConfig {

    @Bean
    public FilterRegistrationBean<TapestrySpringFilter> tapestrySpringFilter(ApplicationContext applicationContext) {
        /*
         * FilterRegistrationBean是Spring框架提供的一个用于注册过滤器的类。它可以在Spring应用程序中注册Filter，并提供一些方便的方法来配置过滤器的属性和顺序等。
         * FilterRegistrationBean的作用包括：
         * 注册Filter：通过FilterRegistrationBean，可以将自定义的Filter注册到Spring应用程序中。只需要实例化FilterRegistrationBean对象，并将过滤器实例设置为其中的属性，然后通过Spring的BeanFactory将该对象注册到应用程序上下文中即可。
         * 配置Filter属性：FilterRegistrationBean提供了一些方法，可以用于设置过滤器的属性，例如设置过滤器的名称、URL模式、初始化参数等。这些属性可以在过滤器初始化时使用。
         * 配置Filter顺序：在应用程序中，可能会有多个Filter需要按照一定的顺序执行。通过FilterRegistrationBean，可以设置过滤器的顺序，以确保它们按照正确的顺序执行。
         * 禁用Filter：如果需要禁用某个过滤器，可以使用FilterRegistrationBean提供的方法将其禁用。
         * <p>
         * 总之，FilterRegistrationBean提供了一种方便的方式来注册和配置过滤器，使得开发者可以更加灵活地管理Spring应用程序中的过滤器。
         */
        FilterRegistrationBean<TapestrySpringFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TapestrySpringFilter((AnnotationConfigServletWebServerApplicationContext) applicationContext));
        registrationBean.setName("app");
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registrationBean;
    }
}
