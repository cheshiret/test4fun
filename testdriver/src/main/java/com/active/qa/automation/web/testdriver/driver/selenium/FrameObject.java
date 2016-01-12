package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.ActionFailedException;
import com.active.qa.automation.web.testapi.interfaces.html.IFrame;
import org.jsoup.nodes.Element;

/**
 * Created by tchen on 1/6/2016.
 */
class FrameObject extends HtmlObject implements IFrame {

    public FrameObject(Element element, String... handler) {
        super(element,handler);
    }

    @Override
    public boolean containsText(Object value) {
        try {
            String text=RuntimeUtil.parsePropertyValue(value);

            return element.text().contains(text);
        } catch (Exception e) {
            throw new ActionFailedException(e);
        }
    }

}