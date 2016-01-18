package com.active.qa.automation.web.testapi.interfaces.testcase;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.exception.TestCaseFailedException;
import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.browser.IBrowser;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.SysInfo;
import com.active.qa.automation.web.testapi.util.TestProperty;
import com.active.qa.automation.web.testapi.util.Timer;
import com.active.qa.automation.web.testapi.verification.CheckPoints;

import java.util.ArrayList;
import java.util.List;


/**
 * This is the root of all test cases. All test cases need to directly or indirectly inherit this class.
 * Created by tchen on 1/11/2016.
 */
public abstract class TestCase implements Executable, TestApiConstants {

  /**
   * the logger object will be used to write log information
   * in all Function and Keyword
   */
  protected static AutomationLogger logger = AutomationLogger.getInstance();

  /**
   * The general browser instance
   */
  protected IBrowser browser;

  /**
   * Holding the test result. Initial value is EXECUTION_STATUS_NA
   */
  protected int testResult = RESULT_NA;

  /**
   * The test environment identifier, such as qa1, qa2, qa3, qa4
   */
  protected String env;

  /**
   * The test case short name
   */
  protected String caseName;

  /**
   * The test case path
   */
  protected String casePath;

  /**
   * The log4j log file full name
   */
  protected String logFileName;

  /**
   * The full test case name
   */
  protected String fullCaseName;

  /**
   * The test case ID
   */
  protected int testCaseId;

  /**
   * The flag to tell if the test case is submitted via command line
   */
  protected boolean isCommandLine;


  /**
   * the automation tool code
   */
  protected int tool = 0;

  /**
   * total execution time
   */

  protected int totalTime = -1;

  protected boolean debug;

  /**
   * this is used to record any verification errors which will not block the test case's execution
   */
  public static List<String> verifyErrors = new ArrayList<String>();

  public CheckPoints checkpoints = CheckPoints.getInstance();

  /**
   * Construct a general TestCase instance
   */
  public TestCase() {
    browser = Browser.getInstance();

    //parse the test case name
    fullCaseName = this.getClass().getName();
    int index = fullCaseName.lastIndexOf(".");
    caseName = fullCaseName.substring(index + 1);
    casePath = fullCaseName.substring(0, index).replaceAll("\\.", "/");
    TestProperty.putProperty("fullCaseName", fullCaseName);
    TestProperty.putProperty("caseName", caseName);

    isCommandLine = false;

    debug = Boolean.parseBoolean(TestProperty.getProperty("debug"));
    verifyErrors.clear();
  }


  /**
   * This method will execute the test work flows.
   *
   * @return
   */
  public abstract void execute();

  /**
   * This method will parser and wrap parameters gotten from command line.
   *
   * @param param - the command line parameter
   */
  public abstract void wrapParameters(Object[] param);

  /**
   * Prepare data before wrapParameters
   *
   * @param args
   */
  protected abstract void prepareData(Object[] args);

  /**
   * do further data preparation for parameters which may depend on data set in wrapParameter()
   */
  protected abstract void beforeExecution();

  /**
   * analyze and process errors/exceptions
   *
   * @param e
   */
  protected abstract void processError(Throwable e);

  /**
   * the possible data process and environment cleanup after execution
   */
  protected abstract void finalize();

  /**
   * This is the test main method for all test cases
   *
   * @param args
   * @return
   */
  @Override
  public int testMain(Object[] args) {
    //initialize Logger
    logger.setLogger(caseName);
    prepareData(args);

    logger.info("Executing " + caseName + " on " + env + " in machine " + SysInfo.getHostName() + "(" + SysInfo.getHostIP() + ")");

    //start timer
    Timer timer = new Timer();

    try {
      logger.info("Parsing/Loading parameters ......");

      //wrap parameters
      wrapParameters(args);


      //set some default parameters which may depend on data set in wrapParameter() and loadIntermediateData()
      beforeExecution();
      logger.info("Executing ......");
      execute();

      if (!checkpoints.isEmpty()) {
        verifyErrors.add("Missed checkpoints: " + checkpoints.toString());
      }
      if (verifyErrors.size() > 0) {
        logger.error("There are following verification errors:");
        for (int i = 0; i < verifyErrors.size(); i++) {
          logger.error("#" + i + ": " + verifyErrors.get(i));
        }
        throw new TestCaseFailedException("Verification failed!");
      }

      testResult = RESULT_PASSED;
      logger.info("PASSED");
    } catch (Throwable e) {
      testResult = RESULT_FAILED;
      processError(e);

      logger.info("FAILED");
    } finally {

      totalTime = timer.diff(); //total execution time in second
      int mins = totalTime / 60;
      int seconds = totalTime - mins * 60;

      logger.info("Running time=" + mins + " minutes " + seconds + " seconds.");

      finalize();

      System.gc();
    }

    return testResult;
  }
}

