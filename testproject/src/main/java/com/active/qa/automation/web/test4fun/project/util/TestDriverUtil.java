package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.exception.UserStoppedScriptException;
import com.active.qa.automation.web.testapi.interfaces.testcase.Executable;
import com.active.qa.automation.web.testapi.util.*;
import com.active.qa.automation.web.testapi.verification.CheckPoints;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author : tchen
 * @since : 1/18/2016.
 */
public class TestDriverUtil {
  static final String PARAM_SEPARATOR = ":";
  static final String VALUE_SEPARATOR = "=";
  private static String testSuite = "", passedCases = "", failedCases = "", exception = "", notExecuted = "";
  private static int totalCases = 0, passedNum = 0, failedNum = 0, totalMins = 0;
  private static String runningId = null;
  private static int tool = TestConstants.SELENIUM; //default tool
  private static String driverui = TestConstants.LAUNCH_BATCH_TEST_WITHOUT_UI;

  public static int callScript(String scriptFullName, String args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
    Class.forName("com.active.qa.automation.web.testdriver.driver.selenium.SeleniumBrowser").getDeclaredMethod("init", (Class<?>[]) null).invoke(null, (Object[]) null);
    Class<?> c = Class.forName(scriptFullName);
    Object[] arg = StringUtil.isEmpty(args) ? new Object[0] : new Object[]{args};
    return ((Executable) c.newInstance()).testMain(arg);
  }

  public static int getTool() {
    return tool;
  }

  public static int callScript(String scriptFullName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
    return callScript(scriptFullName, "");
  }

  public static String[] transferArgs(Object[] args) {
    String[] newArgs = new String[args.length];
    for (int i = 0; i < args.length; i++) {
      newArgs[i] = args[i].toString();
    }

    return newArgs;
  }

  public static Object[] transferArgs(String[] args) {
    Object[] newArgs = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      newArgs[i] = args[i];
    }

    return newArgs;
  }

  /**
   * Load the test script
   *
   * @param args       - command line arguments
   * @param scriptName - the default script name to be used if it is not provided from args
   */
  public static void load(String[] args, String scriptName) {
    //initialize AWO test properties
    Util.initAwo();
    int result = TestApiConstants.RESULT_NA;
    tool = TestProperty.getIntProperty("tool", tool);
    runningId = (new SimpleDateFormat("yyyyMMddhhmmss")).format(Calendar.getInstance().getTime()).toString();
    TestProperty.putProperty("running.id", runningId);
    try {
      String arg = "";
      if (args.length > 0) {
        scriptName = args[0];
        if (args.length > 1) {
          arg = args[1];
        }

        //AutomationLogger needs the env value to set the log path
        String env = TestDriverUtil.getParameter(args, "env");

        tool = Integer.parseInt(TestDriverUtil.getParameter(args, "tool", tool + ""));

        if (env != null && env.length() > 0) {
          TestProperty.putProperty("target_env", env);
        }


        TestDriverUtil.getParameter(args, "debug", TestProperty.getProperty("debug"));
      } else {
        AutomationLogger.getInstance("TestDriver").info("ScriptName is not provided from command argumment, use preset scriptName: \"" + scriptName + "\"");
      }
      AutomationLogger.getInstance().resetLogRootFolder("Migrtest_" + TestProperty.getProperty("target_env") + "_logs");
      result = callScript(scriptName, arg);


    } catch (Exception e) {
      e.printStackTrace();
      if (e.getMessage().matches("InvalidSpyMemReference|OutOfMemoryError|OutOfSpyMemException|ForegroundLockTimeout|locked workstation")) {
        result = TestApiConstants.RESULT_MEMERROR;
      } else {
        result = TestApiConstants.RESULT_FAILED;
      }
    } finally {
      try {
        File ieDriver = new File(System.getProperty("webdriver.ie.driver"));
        File chromeDriver = new File(System.getProperty("webdriver.chrome.driver"));
        Runtime.getRuntime().exec("taskkill /F /IM " + ieDriver.getName()).waitFor();
        Runtime.getRuntime().exec("taskkill /F /IM " + chromeDriver.getName()).waitFor();
        cleanupTempFolder();
      } catch (Exception e) {
      }

      AutomationLogger.getInstance().info("result_code=" + result);
      System.gc();
      System.exit(result);
    }

  }

  public static void batchload() {
    //define local variables
    Timer timer = new Timer();
    runningId = (new SimpleDateFormat("yyyyMMddhhmmss")).format(Calendar.getInstance().getTime()).toString();

    //Initialize TestProperty to load all properties
    Util.initAwo(); //updated by pzhu
    TestProperty.putProperty("running.id", runningId);
    TestProperty.putProperty("isBatch", "true");

    int cursor = 0;
    List<String> cases = null;
    AutomationLogger logger = AutomationLogger.getInstance("TestDriver");
    String emailto = null;
    boolean loopmode = false;

    if (driverui.equalsIgnoreCase(TestConstants.LAUNCH_BATCH_TEST_WITH_UI)) {
      ScriptsLauncher sl = new ScriptsLauncher();
      while (!sl.exit) {
        Timer.sleep(1);
      }
      testSuite = sl.getTestCasesPath();
      emailto = sl.getEmailAddress();
      if (sl.onlyListed()) {
        cases = Arrays.asList(Util.loadListedCases().split("\\s+"));//filterTestCases(cases, TestProperty.loadListedCases());
      }

    } else {
      cases = Arrays.asList(Util.loadListedCases().split("\\s+"));
      emailto = TestProperty.getProperty("notification.to");
    }

    try {
//			if (sl.onlyListed()) {
//				cases = Arrays.asList(AwoUtil.loadListedCases().split("\\s+"));//filterTestCases(cases, TestProperty.loadListedCases());
//			}


      if (TD_TestProperty.getTestData("loop.mode").equalsIgnoreCase("0")) {
        totalCases = cases.size();
        logger.info("Found total " + totalCases + " cases:");

      }


      // Testmethod if loop one test cases for several times //has bug with no log saved in the folder
      // Existing bug,only the latest log would be saved in the log, no append
      else if (TD_TestProperty.getTestData("loop.mode").equalsIgnoreCase("1")) {
        totalCases = TD_TestProperty.getIntProperty("loop.size", 1);
        logger.info("Will execute " + (String) cases.get(0) + " for " + totalCases + " times.");
        loopmode = true;

      }

      // not sure what is it?
//				for (int i = 0; i < totalCases; i++) {
//					logger.debug(cases.get(i).toString());
//					}

      for (int i = 0; i < totalCases; i++) {
        cursor = i;
        String scriptName = loopmode ? (String) cases.get(0) : (String) cases.get(i);
        String[] s = scriptName.split("\\.");
        String caseName = s[s.length - 1];

        String tmp = logger.getLogRootFolder();
        String caseFolder = logger.getLogRootFolder() + File.separator + caseName;
        logger.resetLogRootFolder(caseFolder);
        logger.info(loopmode ? "Start to execute case " + scriptName + " for " + (i + 1) + " time." : "Start to execute case " + (i + 1) + ": " + scriptName);
        int result = ((Integer) callScript(scriptName)).intValue();
        CheckPoints.getInstance().reset();//added by pzhu
        logger = AutomationLogger.getInstance("TestDriver");

        logger.resetLogRootFolder(tmp);//add by pzhu


        if (result == TestApiConstants.RESULT_PASSED) {
          passedCases += caseName + " --- passed." + System.lineSeparator();
          passedNum++;
        } else {
          failedCases += caseName + " --- failed." + System.lineSeparator();
          failedNum++;
        }
      }

    } catch (UserStoppedScriptException e) {
      logger.info("TestDriver stopped by user. ");
      exception = "Testmethod running was stopped by user.\n\n";
    } catch (Throwable e) {
      logger.error(e.toString(), e);
      exception = "Functional tester meets an exception/error -- " + e.toString() + "\n\n";
    } finally {
      int seconds = timer.diff();
      totalMins = Math.round(seconds / (float) (1000 * 60));

      logger.info("Total execution time: " + totalMins + " minutes");
      logger.info(passedNum + " cases were passed");
      logger.info(failedNum + " cases were failed.");
      logger.info((totalCases - passedNum - failedNum)
          + " cases were not executed.");

      if (cursor + 1 != totalCases) {
        for (int i = cursor; i < totalCases; i++) {
          String scriptName = (String) cases.get(i);

          String[] s = scriptName.split("\\.");
          String caseName = s[s.length - 1];
          notExecuted += caseName + "\r";
        }
      }
      if (TD_TestProperty.getIntProperty("ma.test.ind", 0) == 1) {
        sendEmail(emailto);
      }
      System.exit(0);
    }
  }

  /**
   * Builds a list of test case files starting with the specified file/directory.  If the specified
   * test case is a file then list will contain only the specified file otherwise the list will
   * contain all files in the specified directory and all sub-directories.
   *
   * @param testCasePath -  a test case file or directory containing test case files.
   * @return a list of all File objects for each file found
   */
  public static List<String> getTestCaseFiles(String testCasePath) {
    String projectPath = Util.PROJECT_PATH;
    File testCase = new File(projectPath, testCasePath);

    if (!testCase.exists()) {
      testCase = new File(projectPath, testCasePath + ".class");
      if (!testCase.exists()) {
        throw new ItemNotFoundException(testCase.getAbsolutePath() + " (Cannot find test case)");
      }
    }

    List<String> testCases = new ArrayList<String>();

    if (testCase.isFile() && testCase.getName().endsWith(".class")
        && !testCase.getName().equalsIgnoreCase("TestDriver.class")) {
      String caseName = testCasePath.split("\\.")[0];
      caseName = caseName.replaceAll("/|\\\\", "\\.").replaceAll(
          "\\.\\.", "\\.");
      testCases.add(caseName);
      //			logger.debug("  Found test case: " + testCase.getAbsolutePath());
    } else if (testCase.isDirectory() && !testCase.getName().equals(".svn")) {
      // skip '.svn' directories
      String[] dirContents = testCase.list();

      // recurse through files and sub-directories
      for (int i = 0; i < dirContents.length; i++) {
        testCases.addAll(getTestCaseFiles(testCasePath + "/"
            + dirContents[i]));
      }
    }
    return testCases;
  }

  private static void sendEmail(String emailAddress) {
    //Email email = new Email();
    AutomationLogger logger = AutomationLogger.getInstance("TestDriver");
    StringBuffer text = new StringBuffer();

    text.append("The test suite is executed on " + SysInfo.getHostName() + "(" + SysInfo.getHostIP() + ")\n\n");
    if (totalCases > 0)
      text.append("Total " + totalCases + " test case(s).\n");
    else
      text.append("There were no test cases found.\n");

    if (failedNum < 1 && exception.length() < 1 && passedCases.length() > 0 && totalCases > 0 && passedNum == totalCases)
      text.append("All test cases PASSED!\n");
    else {
      if (exception.length() > 0) {
        if (exception.indexOf("Testmethod running was stopped by user") >= 0) {
          text.append("Testmethod suite were stopped by user.\n");
          exception = "";
        } else
          text.append("Testmethod suite execution meets exceptions/errors.\n");
      }
      if (failedNum > 0)
        text.append("-- " + failedNum + " test case(s) FAILED.\n");
      if (passedNum > 0)
        text.append("-- " + passedNum + " test case(s) PASSED.\n");
      int notRun = totalCases - failedNum - passedNum;
      if (notRun > 0)
        text.append("-- " + notRun + " test case(s) were not executed.\n");
    }
    text.append("---------------------------------\n");

    if (exception.length() > 0) {
      text.append("Exceptions:\n\n");
      text.append(exception);
      text.append("---------------------------------\n");
    }
    if (failedNum > 0) {
      text.append("Failed cases:\n\n");
      text.append(failedCases);
      text.append("---------------------------------\n");
    }
    if (passedCases.length() > 0) {
      text.append("Passed cases:\n\n");
      text.append(passedCases);
      text.append("---------------------------------\n");
    }
    if (notExecuted.length() > 0) {
      text.append("Cases not executed:\n\n");
      text.append(notExecuted);
      text.append("---------------------------------\n");
    }

    text.append("\r\n The scripts have run for total " + totalMins + " minutes\r\n");
    String subject = testSuite + " result for " + TestProperty.getProperty("target_env").toUpperCase();
    String from = TestProperty.getProperty("notification.from");
    String to = emailAddress;
    if (TestProperty.getProperty("debug", "false").equalsIgnoreCase("true")) {
      to += ";" + TestProperty.getProperty("notification.debug.to", "jdu@reserveamerica.com");
    }

    String[] attachments = new String[1];
    attachments[0] = logger.getFullLogFileName();

    if (attachments[0].indexOf("orms_") >= 0)
      subject = "Orms " + subject;
    else if (attachments[0].indexOf("web_") >= 0)
      subject = "Web " + subject;

    try {
      Email.send(from, to, subject, text.toString(), attachments);
    } catch (Exception e) {
      logger.error("Failed to send email due to Excepton: " + e.getMessage());
      e.printStackTrace();
      logger.info("Testmethod result: \n" + text.toString());
    } catch (Error e) {
      logger.error("Failed to send email due to Error: " + e.getMessage());
      e.printStackTrace();
      logger.info("Testmethod result: \n" + text.toString());
    }
  }

  private static List<String> filterTestCases(List<String> v, String list) {
    List<String> toReturn = new ArrayList<String>();
    for (int i = 0; i < v.size(); i++) {
      String fullName = v.get(i).toString();
      //String[] s = fullName.split("\\.");
      //String caseName = s[s.length - 1];
      //if (list.indexOf(caseName + "|") >= 0)
      if (list.contains(fullName))
        toReturn.add(fullName);
    }
    return toReturn;
  }

  public static void cleanupTempFolder() {
    String temp = System.getProperty("java.io.tmpdir");
    AutomationLogger.getInstance().info("Cleaning up temp files at " + temp + "......");

    int deleted = FileUtil.cleanupFolder(temp);
    AutomationLogger.getInstance().info("Deleted total " + deleted);
  }

  public static String getParameter(Object[] param, String key) {
    return TestDriverUtil.getParameter(param, key, "");
  }

  public static String getParameter(Object[] param, String key, String defaultValue) throws ItemNotFoundException {
    if (param == null || param.length < 1)
      return defaultValue;

    String replacer = "#@#@#@#";
    String paramString = null;
    for (Object p : param) {
      String ap = ((String) p).replaceAll(":{2}", replacer);
      if (ap.contains(key + "=")) {
        paramString = ap;
        break;
      }
    }

    if (StringUtil.isEmpty(paramString)) {
      if (defaultValue == null)
        throw new ItemNotFoundException("Parameter is empty.");
      else
        return defaultValue;
    }


    int i = paramString.indexOf(key);
    if (i == -1)
      return defaultValue;
    int j = paramString.indexOf(TestDriverUtil.VALUE_SEPARATOR, i);
    if (j == -1)
      throw new ItemNotFoundException("Parameter format is wrong. \""
          + TestDriverUtil.VALUE_SEPARATOR + "\" is missing for "
          + paramString.substring(j));
    int k = paramString.indexOf(TestDriverUtil.PARAM_SEPARATOR, j);
    if (k == -1)
      k = paramString.length();

    String paraValue = paramString.substring(j + 1, k);
    return paraValue.replaceAll(replacer, ":");
  }

  public static void checkParameter(String key, String value)
      throws ItemNotFoundException {
    if (value.length() < 1)
      throw new ItemNotFoundException("Mandatory parameter \"" + key
          + "\" is missing.");
  }

  /**
   * Parses a parameter string as passed in via command-line
   *
   * @param paramString the ':'-separated list of parameters
   * @return an array of Strings containing the stored parameters
   */
  public static String[] parseTestDirectorParamaters(String paramString) {

    return paramString.split(PARAM_SEPARATOR);
  }
}

