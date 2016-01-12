package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.browser.ISimpleBrowser;
import com.active.qa.automation.web.testapi.interfaces.page.IPopupPage;

/**
 * Created by tchen on 1/11/2016.
 */
public abstract class HtmlPopupPage extends HtmlPage implements IPopupPage {

    protected ISimpleBrowser popup;
    protected String attributeName;
    protected Object value;
    protected boolean beforePageLoading;

    protected HtmlPopupPage(String attributeName,Object value) {
        timeout= Browser.LONG_SLEEP;
        popup=null;
        this.attributeName=attributeName;
        this.value=value;
        beforePageLoading=true;

    }

    @Override
    public boolean exists() {
        popup=Browser.getInstance().getHTMLDialog(attributeName, value);
        return popup!=null;
    }

    public void close() {
        popup.close();
    }

    public void setBeforePageLoading(boolean option) {
        beforePageLoading=option;
    }

    public boolean isBeforePageLoading() {
        return beforePageLoading;
    }

}
