package com.active.qa.automation.web.testapi.interfaces.browser;

/**
 * Created by tchen on 1/11/2016.
 */
public abstract class BrowserPlugin {
  protected static BrowserPlugin _instance = null;

  protected BrowserPlugin() {
  }

  ;

  public static BrowserPlugin getInstance() {
    return _instance;
  }

  /**
   * record the page loading time
   *
   * @param pageName
   * @param toolCode
   * @param time
   */
  public abstract void recordPageTiming(String pageName, int time);
}
