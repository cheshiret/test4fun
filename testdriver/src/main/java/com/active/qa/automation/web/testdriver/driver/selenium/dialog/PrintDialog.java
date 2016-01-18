package com.active.qa.automation.web.testdriver.driver.selenium.dialog;

import com.active.qa.automation.web.testapi.interfaces.dialog.IPrintDialog;

/**
 * Created by tchen on 1/6/2016.
 */
public class PrintDialog extends Dialog implements IPrintDialog {

  public PrintDialog(String attributes) {
    super(attributes);
  }

  public PrintDialog() {
    this("[TITLE:Print; CLASS:#32770]");
  }

  @Override
  public void clickCancel() {
    this.clickButton("Cancel");
  }

  @Override
  public void clickPrint() {
    this.clickButton("&Print");
  }
}
