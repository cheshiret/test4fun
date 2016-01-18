package com.active.qa.automation.web.testapi.pages;

import com.active.qa.automation.web.testapi.interfaces.dialog.IDialog;
import com.active.qa.automation.web.testapi.util.Timer;


/**
 * Created by tchen on 1/11/2016.
 */
public abstract class DialogPage extends Page {
  protected IDialog dialog;
  protected boolean dismissable;
  protected boolean beforePageLoading;

  protected DialogPage() {
    dialog = null;
    dismissable = true;
    beforePageLoading = true;
  }

  public void resetDefault() {
    dismissable = true;
    beforePageLoading = true;
  }

  public void quit() {
    dialog.quit();
  }

  public String text() {
    return dialog.text();
  }

  public String title() {
    return dialog.title();
  }

  public abstract void dismiss();

  public void dismiss(int timeout) {
    Timer timer = new Timer();
    while (timer.diff() < timeout) {
      if (this.exists()) {
        dismiss();
      }
    }
  }

  public boolean dismissible() {
    return this.dismissable;
  }

  public void setDismissible(boolean option) {
    dismissable = option;
  }

  public void setBeforePageLoading(boolean option) {
    beforePageLoading = option;
  }

  public boolean isBeforePageLoading() {
    return beforePageLoading;
  }
}

