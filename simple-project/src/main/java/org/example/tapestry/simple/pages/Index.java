package org.example.tapestry.simple.pages;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.example.tapestry.simple.components.BaseComponent;
import org.example.tapestry.simple.services.TapestryService;

@Import(stylesheet = {"context:test-styles.css"})
public class Index extends BaseComponent {

    @Inject
    private TapestryService tapestryService;
    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    public String getView() {
        System.out.println(tapestryService.getClass());
        return tapestryService.getType();
    }

    @OnEvent("addEvent")
    public void onAddEvent(int count) {
        ajaxResponseRenderer.addRender("indexZone",
                ((Zone) getComponentResources().getEmbeddedComponent("indexZone")).getBody());
    }
}
