package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.browser.IBrowser;
import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.interfaces.html.IText;
import com.active.qa.automation.web.testapi.util.DateFunctions;
import com.active.qa.automation.web.testapi.util.KeyInput;
import com.active.qa.automation.web.testapi.util.Property;

import java.util.List;

/**
 * This is the class that all web page classes need to extend
 * Created by tchen on 1/11/2016.
 */
public abstract class HtmlMainPage extends HtmlPage {
    protected IBrowser browser;

    public HtmlMainPage() {
        browser = Browser.getInstance();
    }

    public String retrieveMatchedOption(String propertyKey,Object value,String regx){
        List<String> options = browser.getDropdownElements(propertyKey,value);
        String matchedOption = "";
        for(String item:options){
            if(item.matches(regx)){
                matchedOption = item;
                break;
            }
        }
        return matchedOption;
    }

    /**
     * unify to set date for date component and get confirm message - 'Invalid date. The date format should be: YYYYMMDD'
     * @param dateFieldObject
     * @param date
     * @return
     */
    public String setDateAndGetMessage(IText dateFieldObject, String date) {
        dateFieldObject.setText(date, IText.Event.LOSEFOCUS);

        ConfirmDialogPage confirmPage = ConfirmDialogPage.getInstance();
        confirmPage.setDismissible(false);
        confirmPage.setBeforePageLoading(false);
        Object pg = browser.waitExists(confirmPage, this);//wait ConfirmDialogPage and the page which dateFieldObject belong to
        String confirmMsg = "";
        if(pg == confirmPage) {
            confirmMsg = confirmPage.text();
            confirmPage.dismiss();
        }
        return confirmMsg;
    }

    public String setDateAndGetMessage(Property[] pros, String date){
        IHtmlObject[] objs = browser.getTextField(pros);
        IText obj = (IText)objs[0];
        String msg = this.setDateAndGetMessage(obj, date);
        Browser.unregister(objs);
        return msg;
    }

    /**
     * if invalid date is change into an valid date or null string, Return true; or return false
     * @param invaildDates:   an array of invalid dates
     * @param index
     * @param propertys
     */
    protected boolean isInvaildDateParsedProperlyByDateComponent(String[] invalidDates,int index,Property... propertys){
        for(int i = 0; i < invalidDates.length; i++) {
            browser.setTextField(propertys, invalidDates[i], true, index);
            //this is used to remove foc	us
//			browser.clickGuiObject(".class", "Html.DIV", ".className", "ui-dialog-content ui-widget-content");
            moveFocusOutOfDateComponent();
            String valForChangableDate=browser.getTextFieldValue(propertys, index);
            if(!DateFunctions.isValidDate(valForChangableDate)&&valForChangableDate.trim().length()>0){
                logger.error("Failed to parse Invaild date: "+invalidDates[i]);
                return false;
            }
        }
        return true;
    }

    public void moveFocusOutOfDateComponent() {
        browser.inputKey(KeyInput.get(KeyInput.TAB));
    }

    public IHtmlObject[] getTableObject(String key, Object value) {
        return browser.getTableTestObject(key, value);
    }

    public IHtmlObject[] getTableObjectById(Object value) {
        return getTableObject(".id", value);
    }
}
