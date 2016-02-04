package com.active.qa.automation.web.test4fun.keywords.project;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.verification.CheckIdentifier;


/**
 * @author : tchen
 * @since : 2/3/2016.
 */
public interface Keyword extends TestApiConstants {

  /**
   * The method execute the process that open the url
   * @param newBrowser if it's a new browser
   * @param url the url string
   */
  public void invokeURL(String url, boolean newBrowser);

  public void check(CheckIdentifier checkid);

}

