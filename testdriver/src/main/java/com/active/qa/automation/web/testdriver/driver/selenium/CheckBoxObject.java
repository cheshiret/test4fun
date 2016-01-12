package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.interfaces.html.ICheckBox;
import org.jsoup.nodes.Element;

/**
 * Created by tchen on 1/6/2016.
 */
class CheckBoxObject extends HtmlObject implements ICheckBox {

    public CheckBoxObject(Element element, String... handler) {
        super(element,handler);
    }

    @Override
    public void deselect() {
        if(getWebElement().isSelected()){
            click();
        }
    }

    @Override
    public boolean isSelected() {
        return getWebElement().isSelected();
    }

    @Override
    public void select() {
        if(!getWebElement().isSelected()){
            click();
        }
    }

}

