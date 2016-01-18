package com.active.qa.automation.web.testapi;

import com.active.qa.automation.web.testapi.util.TestProperty;

/**
 * @author : tchen
 * @since  : 1/11/2016.
 */
public interface TestApiConstants {

  //Timing related constants
  public static final int VERY_LONG_SLEEP = Integer
            .parseInt(TestProperty.getProperty("page.loading.wait.extralong"));
  public static final int LONG_SLEEP = Integer
            .parseInt(TestProperty.getProperty("page.loading.wait.long"));
  public static final int SLEEP = Integer
            .parseInt(TestProperty.getProperty("page.loading.wait"));
  public static final int SHORT_SLEEP = Integer
            .parseInt(TestProperty.getProperty("page.loading.wait.short"));
  public static final int VERY_SHORT_SLEEP = Integer
            .parseInt(TestProperty.getProperty("page.loading.wait.extrashort"));
  public static final int PAGELOADING_SYNC_TIME = Integer
            .parseInt(TestProperty.getProperty("page.loading.sync"));
  public static final int FILE_DIALOG_LONG_SLEEP = Integer
            .parseInt(TestProperty.getProperty("dialog.loading.wait.extralong"));
  /**
   * The amount of time (in seconds) to wait between attempts
   * to find the object.
   */
  public static final int FIND_OBJECT_WAIT_BETWEEN_RETRY = 1;

  //Test result status
  public static final int RESULT_FAILED = 1;
  public static final int RESULT_PASSED = 2;
  public static final int RESULT_NOT_RUN = 3;
  public static final int RESULT_NA = 4;
  public static final int RESULT_BLOCKED = 5;
  public static final int RESULT_CAUTION = 6;
  public static final int RESULT_MEMERROR = 7;
  public static final int RESULT_HIBERNATED = 8;

  //Execution status code
  public static final int EXECUTION_FINISH = 0;
  public static final int EXECUTION_SUBMITTED = 1;
  public static final int EXECUTION_RUNNING = 2;
  public static final int EXECUTION_STOP = 3;
  public static final int EXECUTION_NA = 4;

  //Test case active
  public static final int INACTIVE = 0;
  public static final int ACTIVE = 1;
  public static final int DRAFT = 2;

  //Test case status
  public static final int TESTCASE_PENDING = 0;
  public static final int TESTCASE_RUNNING = 1;
  public static final int TESTCASE_FINISHED = 2;
  public static final int TESTCASE_HIBERNATED = 3;
  public static final String KEYS_CLEAR_FIELD = "^{ExtEnd}^+{ExtHome}{ExtDelete}";

  public static final String CELL_DELIMITER = "##%%##";

  //The key used for encrypt all the password.
  public static final String KEY = "CA67C6C94D75A9CF08CC7E7697E32298";

}

