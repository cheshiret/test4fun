package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.dialog.IPrintDialog;

/**
 * Created by tchen on 1/11/2016.
 */
public class PrintDialogPage extends DialogPage implements IPrintDialog {
  private static PrintDialogPage _instance = null;
  private boolean dismissable;

  public static PrintDialogPage getInstance() {
    if (null == _instance) {
      _instance = new PrintDialogPage();
    }

    return _instance;
  }

  protected PrintDialogPage() {
    super();
    dismissable = true;
  }

  @Override
  public void clickCancel() {
    ((IPrintDialog) dialog).clickCancel();
  }

  @Override
  public void clickPrint() {
    ((IPrintDialog) dialog).clickPrint();
  }

  @Override
  public boolean exists() {
    dialog = Browser.getInstance().getPrintDialog();
    return dialog != null;
  }

  @Override
  public void dismiss() {
    clickCancel();

  }

  @Override
  public boolean dismissible() {
    return dismissable;
  }

  public void setDismissible(boolean choice) {
    this.dismissable = choice;
  }

}
