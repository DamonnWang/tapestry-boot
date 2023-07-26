package org.example.tapestry.simple.services;

import org.apache.tapestry5.ioc.ServiceBinder;

import org.example.tapestry.simple.services.impl.TapestryServiceImpl;

/**
 *
 */
public class AppModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(TapestryService.class, TapestryServiceImpl.class);
    }

    /*@Contribute(ComponentClassResolver.class)
    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
        configuration.add(new LibraryMapping("myapp", "org.example.org.example.tapestry.admin.components"));
    }*/
}
