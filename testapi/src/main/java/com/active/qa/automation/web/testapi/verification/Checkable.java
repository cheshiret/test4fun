package com.active.qa.automation.web.testapi.verification;

import com.active.qa.automation.web.testapi.datacollection.Data;

/**
 * Created by tchen on 1/11/2016.
 */
public interface Checkable {
  public void check(Data<?>... data);

  public String getName();

}

