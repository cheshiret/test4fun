package com.active.qa.automation.web.test4fun.project.datacollection.misc;

import java.util.List;

/**
 * @author : tchen
 * @since : 3/14/2016.
 */
public class DBQuery {
  public String queryname = "";
  public String querytxt = "";
  public boolean compareresult = false;
  public List<String> queryresult = null;


  public String getqueryname() {
    return this.queryname;
  }

  public String getquerytxt() {
    return this.querytxt;
  }
}
