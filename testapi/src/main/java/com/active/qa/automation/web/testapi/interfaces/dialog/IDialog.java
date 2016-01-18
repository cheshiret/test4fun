package com.active.qa.automation.web.testapi.interfaces.dialog;

/**
 * Created by tchen on 1/11/2016.
 */
public interface IDialog {
  public String text();

  public String title();

  public void quit();

  public boolean exists();
}
