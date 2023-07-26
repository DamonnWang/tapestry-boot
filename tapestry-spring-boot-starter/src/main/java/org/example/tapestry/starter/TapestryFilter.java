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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class TapestryFilter extends FilterRegistrationBean<TapestryFilter> implements Filter {

    private final Logger log = LoggerFactory.getLogger(TapestryFilter.class);

    private final Registry registry;

    private HttpServletRequestHandler handler;

    @Autowired
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
