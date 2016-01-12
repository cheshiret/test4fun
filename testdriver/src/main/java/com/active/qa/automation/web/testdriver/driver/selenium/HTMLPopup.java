package com.active.qa.automation.web.testdriver.driver.selenium;


import com.active.qa.automation.web.testapi.ItemNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * Created by tchen on 1/6/2016.
 */
class HTMLPopup extends SimpleBrowser {

    public HTMLPopup(WebDriver browser) {
        this.browser=browser;
        this.handler=browser.getWindowHandle();
    }

    @Override
    public void close() {
//		((JavascriptExecutor) browser).executeScript("window.close()");
        executeJavascript("window.close()", null);
    }

    @Override
    public boolean exists() {
        try {
            return browser!=null && browser.getPageSource().length()>0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String text() {
        try {
            return browser.findElement(By.xpath("/HTML/BODY")).getText();
        }catch(Exception e) {
            throw new ItemNotFoundException(e.getMessage());
        }
    }

    @Override
    public String title() {
        try {
            return browser.getTitle();
        }catch(Exception e) {
            throw new ItemNotFoundException(e.getMessage());
        }
    }

    @Override
    public String url() {
        try {
            return browser.getCurrentUrl();
        }catch(Exception e) {
            throw new ItemNotFoundException(e.getMessage());
        }
    }

    @Override
    public boolean sync() {
        return sync(LONG_SLEEP);
    }

    @Override
    public boolean sync(int timeout) {
        try {
            waitExists(timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}