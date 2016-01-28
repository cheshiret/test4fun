package com.active.qa.automation.web.test4fun.util.project;

import com.active.qa.automation.web.testapi.interfaces.browser.BrowserPlugin;

/**
 * This utility class is used to retrieve and maintain AWO product related information
 * @author tchen
 * @since 1/28/2016.
 */
public class BrowserAddon extends BrowserPlugin {

  public static void init() {
    if(_instance==null) {
      _instance=new BrowserAddon();
    }
  }

  protected BrowserAddon(){};

  @Override
  public void recordPageTiming(String pageName, int time) {
    DataBaseFunctions.recordPageTime(pageName, TestDriverUtil.getTool(),time);

  }

}
