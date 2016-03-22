package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.interfaces.testcase.Executable;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.FileUtil;
import com.active.qa.automation.web.testapi.util.StringUtil;
import com.active.qa.automation.web.testapi.util.TestProperty;
import org.testng.TestNG;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author : tchen
 * @since : 3/22/2016.
 */
public class TestNGDriverUtil {

  static final String PARAM_SEPARATOR = ":";
  static final String VALUE_SEPARATOR = "=";
  private static String testSuite = "", passedCases = "", failedCases = "", exception = "", notExecuted = "";
  private static int totalCases = 0, passedNum = 0, failedNum = 0, totalMins = 0;
  private static String runningId = null;
  private static int tool = TestConstants.SELENIUM; //default tool
  private static String driverui = TestConstants.LAUNCH_BATCH_TEST_WITHOUT_UI;


  public static void testNGRunner(String location) {
    TestNG tng = new TestNG();
    List<String> suites = Lists.newArrayList();
    suites.add(location);
    tng.setTestSuites(suites);
    tng.run();
  }


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

  @BeforeTest
  public static void initTest() {

  }

  /**
   * Load the test script
   *
   * @param args       - command line arguments
   * @param scriptName - the default script name to be used if it is not provided from args
   */
  @Test
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
        String env = TestNGDriverUtil.getParameter(args, "env");

        tool = Integer.parseInt(TestNGDriverUtil.getParameter(args, "tool", tool + ""));

        if (env != null && env.length() > 0) {
          TestProperty.putProperty("target_env", env);
        }

        TestNGDriverUtil.getParameter(args, "debug", TestProperty.getProperty("debug"));
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

  @AfterTest
  public static void testTearUp() {

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


  private static List<String> filterTestCases(List<String> v, String list) {
    List<String> toReturn = new ArrayList<String>();
    for (int i = 0; i < v.size(); i++) {
      String fullName = v.get(i).toString();
      //String[] s = fullName.split("\\.");
      //String caseName = s[s.length - 1];
      //if (list.indexOf(caseName + "|") >= 0)
      if (list.contains(fullName)) {
        toReturn.add(fullName);
      }
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
    return TestNGDriverUtil.getParameter(param, key, "");
  }

  public static String getParameter(Object[] param, String key, String defaultValue) throws ItemNotFoundException {
    if (param == null || param.length < 1) {
      return defaultValue;
    }

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
      if (defaultValue == null) {
        throw new ItemNotFoundException("Parameter is empty.");
      } else {
        return defaultValue;
      }
    }

    int i = paramString.indexOf(key);
    if (i == -1) {
      return defaultValue;
    }
    int j = paramString.indexOf(TestNGDriverUtil.VALUE_SEPARATOR, i);
    if (j == -1) {
      throw new ItemNotFoundException("Parameter format is wrong. \""
                                      + TestNGDriverUtil.VALUE_SEPARATOR + "\" is missing for "
                                      + paramString.substring(j));
    }
    int k = paramString.indexOf(TestNGDriverUtil.PARAM_SEPARATOR, j);
    if (k == -1) {
      k = paramString.length();
    }

    String paraValue = paramString.substring(j + 1, k);
    return paraValue.replaceAll(replacer, ":");
  }

  public static void checkParameter(String key, String value)
      throws ItemNotFoundException {
    if (value.length() < 1) {
      throw new ItemNotFoundException("Mandatory parameter \"" + key
                                      + "\" is missing.");
    }
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

