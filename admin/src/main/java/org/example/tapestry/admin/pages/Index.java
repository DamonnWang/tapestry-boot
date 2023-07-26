package org.example.tapestry.admin.pages;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.example.inter.service.UserService;
import org.example.tapestry.admin.components.BaseComponent;
import org.example.tapestry.admin.services.TapestryService;

@Import(stylesheet = {"context:test-styles.css"})
public class Index extends BaseComponent {

    @Inject
    private TapestryService tapestryService;
    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;
    @Inject
    private UserService userService;

    @Property
    private int count;

    public int getView() {
        System.out.println(userService);
        System.out.println(tapestryService);
        System.out.println(tapestryService.getUser());
        return count;
    }

    @OnEvent("addEvent")
    public void onAddEvent(int count) {
        this.count += count;

        ajaxResponseRenderer.addRender("indexZone",
                ((Zone) getComponentResources().getEmbeddedComponent("indexZone")).getBody());
    }
}
