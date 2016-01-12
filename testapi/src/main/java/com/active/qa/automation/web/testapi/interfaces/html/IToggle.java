package com.active.qa.automation.web.testapi.interfaces.html;

/**
 * Support objects with binary state characteristics such as check boxes, radio buttons, and toggle buttons.
 * Created by tchen on 1/11/2016.
 */
public interface IToggle extends IHtmlObject {
    public void deselect();

    public void select();

    public boolean isSelected();
}

