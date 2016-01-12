package com.active.qa.automation.web.testapi.keywords;

import com.active.qa.automation.web.testapi.TestApiConstants;

/**
 * Created by tchen on 1/11/2016.
 */
public class Keyword implements TestApiConstants{
    protected static AutomationLogger logger = AutomationLogger.getInstance();
    protected IBrowser browser;
    protected CheckPoints checkPoints;

    protected Keyword() {
        browser=Browser.getInstance();
        checkPoints=CheckPoints.getInstance();
    }

    /**
     * The method execute the process that open the url
     *
     * @param url
     */
    public void invokeURL(String url, boolean newBrowser) {
        logger.info("Invoke " + url);

        if (newBrowser || !browser.exists()) {
            browser.closeAllBrowsers();
            browser.open(url);
        } else {
            browser.load(url);
        }
    }

    protected void check(CheckIdentifier checkid) {
        if(checkPoints.isEmpty())
            return;

        if(checkPoints.hasKey(checkid) ) {
            CheckPoint cp=checkPoints.getCheckPoint(checkid);
            cp.check();
        }
    }
}

