package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.util.DateFunctions;
import com.active.qa.automation.web.testapi.util.TestProperty;

/**
 * Created by tchen on 1/28/2016.
 */
public interface TestConstants extends TestApiConstants {
  public static final String TIMESTAMP = DateFunctions.getLongTimeStamp();
  public static final String DATESTAMP = DateFunctions.getLongDateStamp();

  //Timing
  public static final int NO_MORE_SLEEP = 0;
  public static final int SHORT_SLEEP_BEFORE_CHECK = 1;
  public static final int DYNAMIC_SLEEP_BEFORE_CHECK = 5; //Mainly use for UWP page loading. We can update it based on the network speed
  public static final int SLEEP_TWENTY_SECOND_BEFORE_CHECK = 20;
  public static final int SLEEP_FIFTEN_SECOND_BEFORE_CHECK = 20;
  public static final int SLEEP_ONE_MINUTE_BEFORE_CHECK = 60; //Using befoer checking timer is changed in shopping cart page

  public static final int PAGE_LOADING_TRESHOLD = Integer.parseInt(TestProperty.getProperty("page.loading.treshold"));
  public static final int CHECK_REPORT_IN_MAILBOX_THRESHOLD = Integer.parseInt(TestProperty.getProperty("check.email.report.wait"));
  public static final int CHECK_NOTIFICATION_IN_MAILBOX_THRESHOLD = Integer.parseInt(TestProperty.getProperty("check.email.notification.wait"));
  public static final int CHECK_NOTIFICATION_IN_MAILBOX_TIMEDIFF = Integer.parseInt(TestProperty.getProperty("check.email.notification.timediff"));


  // Values for <projectID> in RecordTestRun2
  public static final int PROJECT_AUTOMATION = 7;

  public static final int PROJECT_LEGACY = 4;

  public static final int PROJECT_ORMS = 5;

  public static final int PROJECT_SANDBOX = 6;

  public static final int PROJECT_WEB = 8;

  //Status id
  public final static String ACTIVE_ID = "1";

  public static final String LAUNCH_BATCH_TEST_WITH_UI = "1";
  public static final String LAUNCH_BATCH_TEST_WITHOUT_UI = "0";

  public static final int SELENIUM = 1;

}


