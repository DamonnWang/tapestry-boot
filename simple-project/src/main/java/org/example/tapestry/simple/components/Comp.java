package org.example.tapestry.simple.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class Comp extends BaseComponent {


    @Parameter(required = true)
    @Property
    private int count;

    public void onAdd(int count) {
        boolean addEvent = getComponentResources().triggerEvent("addEvent", new Object[]{count}, null);
        System.out.println(addEvent);
    }


}