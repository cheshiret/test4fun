package com.active.qa.automation.web.testdriver.driver.selenium;


import com.active.qa.automation.web.testapi.interfaces.html.IOption;
import org.jsoup.nodes.Element;

/**
 * Created by tchen on 1/6/2016.
 */
class DropdownOption extends HtmlObject implements IOption {

    public DropdownOption(Element element, String handler) {
        super(element,handler);
    }

}

