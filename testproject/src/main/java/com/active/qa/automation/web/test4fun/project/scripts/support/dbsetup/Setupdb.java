package com.active.qa.automation.web.test4fun.project.scripts.support.dbsetup;

import com.active.qa.automation.web.test4fun.project.util.DatabaseInst;
import com.active.qa.automation.web.test4fun.project.util.Util;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.TestProperty;

/**
 * @author : tchen
 * @since : 3/15/2016.
 */
public class Setupdb {

  DatabaseInst db = null;

  public Setupdb() {
    AutomationLogger.init(Util.PROJECT_PATH, Util.LOG4J_PROPERTY);
    Util.initTestProperty();
    db = DatabaseInst.getInstance();
  }

  public static void main(String[] args) {
    Setupdb setupdb = new Setupdb();
  }

  public static void createDb() {
    String projname = TestProperty.getProperty("project.name");
    String dbname = projname + "_qa_db";
  }

  public static void createTables() {

  }

}
