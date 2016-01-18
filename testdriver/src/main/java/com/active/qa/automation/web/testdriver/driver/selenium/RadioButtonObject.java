package com.active.qa.automation.web.testdriver.driver.selenium;

import com.active.qa.automation.web.testapi.interfaces.html.IRadioButton;
import org.jsoup.nodes.Element;

/**
 * Created by tchen on 1/6/2016.
 */
class RadioButtonObject extends HtmlObject implements IRadioButton {

  public RadioButtonObject(Element element, String... handler) {
    super(element, handler);
  }

  @Override
  public void deselect() {
    getWebElement().clear();
  }

  @Override
  public boolean isSelected() {
    return getWebElement().isSelected();
  }

  @Override
  public void select() {
    if (!isSelected()) {
      getWebElement().click();
    }
  }
}
