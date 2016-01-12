package com.active.qa.automation.web.testapi.interfaces.html;

import com.active.qa.automation.web.testapi.interfaces.IGuiObject;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IHtmlObject extends IGuiObject {
    public String getAttributeValue(String name);

    public String tag();

    public String text();

    public String id();

    public String name();

    public String type();

    public String title();

    public String style(String name);

    public String className();

    public IHtmlObject[] getChildren();

    public IHtmlObject getParent();

    public void onMouseDown();

    public void onMouseOver();

    public void onKeyUp();


}

