package org.example.tapestry.simple.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * @author Damon
 * @date 2023/6/25
 **/
public class BaseComponent {

    @Inject
    private ComponentResources componentResources;

    public ComponentResources getComponentResources() {
        return componentResources;
    }
}
