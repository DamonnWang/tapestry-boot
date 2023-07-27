package org.example.tapestry.starter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.apache.tapestry5.services.ServletApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * FilterRegistrationBean是Spring框架提供的一个用于注册过滤器的类。它可以在Spring应用程序中注册Filter，并提供一些方便的方法来配置过滤器的属性和顺序等。
 * FilterRegistrationBean的作用包括：
 * 注册Filter：通过FilterRegistrationBean，可以将自定义的Filter注册到Spring应用程序中。只需要实例化FilterRegistrationBean对象，并将过滤器实例设置为其中的属性，然后通过Spring的BeanFactory将该对象注册到应用程序上下文中即可。
 * 配置Filter属性：FilterRegistrationBean提供了一些方法，可以用于设置过滤器的属性，例如设置过滤器的名称、URL模式、初始化参数等。这些属性可以在过滤器初始化时使用。
 * 配置Filter顺序：在应用程序中，可能会有多个Filter需要按照一定的顺序执行。通过FilterRegistrationBean，可以设置过滤器的顺序，以确保它们按照正确的顺序执行。
 * 禁用Filter：如果需要禁用某个过滤器，可以使用FilterRegistrationBean提供的方法将其禁用。
 * <p>
 * 总之，FilterRegistrationBean提供了一种方便的方式来注册和配置过滤器，使得开发者可以更加灵活地管理Spring应用程序中的过滤器。
 */
// @Component
public class TapestryFilter extends FilterRegistrationBean<TapestryFilter> implements Filter {

    private final Logger log = LoggerFactory.getLogger(TapestryFilter.class);

    private final Registry registry;

    private HttpServletRequestHandler handler;

    // @Autowired
    public TapestryFilter(Registry registry) {
        this.registry = registry;
        log.debug("register TapestryFilter");
        // setOrder(Ordered.LOWEST_PRECEDENCE);
        // addUrlPatterns("/*");
    }

    @Override
    public final void init(FilterConfig filterConfig) throws ServletException {

        ServletContext servletContext = filterConfig.getServletContext();
        handler = registry.getService(HttpServletRequestHandler.class);
        ServletApplicationInitializer ai = registry.getService(ServletApplicationInitializer.class);
        ai.initializeApplication(servletContext);
        log.debug("TapestryFilter init completed");
    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            boolean handled = false;
            if (needHandle(httpServletRequest)) {
                handled = handler.service(httpServletRequest, httpServletResponse);
            }
            if (!handled) {
                chain.doFilter(request, response);
            }
        } finally {
            registry.cleanupThread();
        }
    }

    @Override
    public void destroy() {
        registry.shutdown();
    }


    @Override
    public TapestryFilter getFilter() {
        return this;
    }

    /**
     * 是否需要Tapestry处理
     */
    private boolean needHandle(HttpServletRequest httpServletRequest) {
        String uri = httpServletRequest.getRequestURI();
        // /rest/ 接口下的内容无需处理
        return !uri.startsWith("/rest/");
    }

}
