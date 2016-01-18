package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.interfaces.html.ILink;
import org.jsoup.nodes.Element;

/**
 * Created by tchen on 1/6/2016.
 */
class LinkObject extends HtmlObject implements ILink {

    public LinkObject(Element element, String... handler) {
        super(element,handler);
    }

    @Override
    public String href() {
        try {
            return element.attr("href");
        } catch (Exception e) {
            throw new ActionFailedException(e);
        }
    }

}
