package org.example.tapestry.in.config.tapestry;

import java.util.Collections;
import java.util.Set;

import org.apache.tapestry5.ioc.ObjectCreator;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBuilderResources;
import org.apache.tapestry5.ioc.def.ServiceDef2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Wrapper class to register Spring services in Tapestry.
 */
@SuppressWarnings("rawtypes")
public class SpringBeanServiceDef implements ServiceDef2 {
    private static final Logger log = LoggerFactory.getLogger(SpringBeanServiceDef.class);

    private final String beanName;

    private final ApplicationContext applicationContext;

    public SpringBeanServiceDef(String beanName, ApplicationContext applicationContext) {
        this.beanName = beanName;
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean isPreventDecoration() {
        return true;
    }

    @Override
    public ObjectCreator createServiceCreator(ServiceBuilderResources resources) {
        return new ObjectCreator() {
            @Override
            public Object createObject() {
                return applicationContext.getBean(beanName);
            }

            @Override
            public String toString() {
                return String.format("ObjectCreator<Spring Bean '%s'>", beanName);
            }
        };
    }

    @Override
    public String getServiceId() {
        // log.debug("getServiceId, return beanName: {}", beanName);
        return beanName;
    }

    @Override
    public Set<Class> getMarkers() {
        return Collections.emptySet();
    }

    @Override
    public Class getServiceInterface() {
        // log.debug("getServiceInterface");
        return applicationContext.getType(beanName);
    }

    @Override
    public String getServiceScope() {
        return ScopeConstants.DEFAULT;
    }

    @Override
    public boolean isEagerLoad() {
        return false;
    }

}

