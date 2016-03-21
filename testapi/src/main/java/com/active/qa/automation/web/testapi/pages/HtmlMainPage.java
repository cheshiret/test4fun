package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.browser.IBrowser;
import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.interfaces.html.IText;
import com.active.qa.automation.web.testapi.util.DateFunctions;
import com.active.qa.automation.web.testapi.util.KeyInput;
import com.active.qa.automation.web.testapi.util.Property;
import com.active.qa.automation.web.testapi.util.TestProperty;

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

  public String retrieveMatchedOption(String propertyKey, Object value, String regx) {
    List<String> options = browser.getDropdownElements(propertyKey, value);
    String matchedOption = "";
    for (String item : options) {
      if (item.matches(regx)) {
        matchedOption = item;
        break;
      }
    }
    return matchedOption;
  }

  /**
   * unify to set date for date component and get confirm message - 'Invalid date. The date format should be: YYYYMMDD'
   *
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
    if (pg == confirmPage) {
      confirmMsg = confirmPage.text();
      confirmPage.dismiss();
    }
    return confirmMsg;
  }

  public String setDateAndGetMessage(Property[] pros, String date) {
    IHtmlObject[] objs = browser.getTextField(pros);
    IText obj = (IText) objs[0];
    String msg = this.setDateAndGetMessage(obj, date);
    Browser.unregister(objs);
    return msg;
  }

  /**
   * if invalid date is change into an valid date or null string, Return true; or return false
   *
   * @param invalidDates: an array of invalid dates
   * @param index
   * @param propertys
   */
  protected boolean isInvaildDateParsedProperlyByDateComponent(String[] invalidDates, int index, Property... propertys) {
    for (int i = 0; i < invalidDates.length; i++) {
      browser.setTextField(propertys, invalidDates[i], true, index);
      //this is used to remove foc	us
//			browser.clickGuiObject(".class", "Html.DIV", ".className", "ui-dialog-content ui-widget-content");
      moveFocusOutOfDateComponent();
      String valForChangableDate = browser.getTextFieldValue(propertys, index);
      if (!DateFunctions.isValidDate(valForChangableDate) && valForChangableDate.trim().length() > 0) {
        logger.error("Failed to parse Invaild date: " + invalidDates[i]);
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

  public boolean elementsExist(Property[] property) {
    return browser.checkHtmlObjectExists(property);
  }

  public boolean elementsExist(List<Property[]> property) {
    return browser.checkHtmlObjectExists(property);
  }

  public String propertyString(Property[] property) {
    String s = null;
    for (int i = 0; i < property.length; i++) {
      s += property[i].getPropertyName() + "=" + property[i].getPropertyValue() + ",";
    }
    return s;
  }

  //public function to manipulate browser element
  public void setElements(Property[] property, String value) {
    if (elementsExist(property)) {
      browser.setTextField(property, value);
    } else {
      logger.warn("There is no such element for " + propertyString(property) + " or the element has been updated");
    }
  }

  public void setOnChangeElements(Property[] property, String value) {
    if (elementsExist(property)) {
      IHtmlObject[] objs = browser.getTextField(property);
      if (objs.length > 0) {
        ((IText) objs[0])
            .setText(value, IText.Event.LOSEFOCUS);
      }
    } else {
      logger.warn("There is no such element for " + propertyString(property) + " or the element has been updated");
    }
  }


  public void selectElements(Property[] property, String value) {
    if (elementsExist(property)) {
      browser.selectDropdownList(property, value);
    } else {
      logger.warn("There is no such element for " + propertyString(property) + " or the element has been updated");
    }
  }


  public void selectCheckBox(Property[] property) {
    if (elementsExist(property)) {
      browser.selectCheckBox(property);
    } else {
      logger.warn("There is no such element for " + propertyString(property) + " or the element has been updated");
    }
  }


  public void click(Property[] property) {
    if (elementsExist(property)) {
      browser.clickGuiObject(property, true);
    } else {
      logger.warn("There is no such element for " + propertyString(property) + " or the element has been updated");
    }
  }

  public void click(List<Property[]> property) {
    if (elementsExist(property)) {
      browser.clickGuiObject(property);
    } else {
      logger.warn("There is no such element for " + property + " or the element has been updated");
    }
  }

  public void jsclick(Property[] property) {
    if (elementsExist(property)) {
      TestProperty.putProperty("object.needJavascriptClick", "true");
      browser.clickGuiObject(property, true);
      TestProperty.putProperty("object.needJavascriptClick", "false");
    } else {
      logger.warn("There is no such element for " + propertyString(property) + " or the element has been updated");
    }
  }

  public void jsclick(List<Property[]> property) {
    if (elementsExist(property)) {
      TestProperty.putProperty("object.needJavascriptClick", "true");
      browser.clickGuiObject(property);
      TestProperty.putProperty("object.needJavascriptClick", "false");
    } else {
      logger.warn("There is no such element for " + property + " or the element has been updated");
    }
  }

}