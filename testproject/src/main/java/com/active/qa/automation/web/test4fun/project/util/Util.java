package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.FileUtil;
import com.active.qa.automation.web.testapi.util.TestProperty;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This utility class is used to retrieve and maintain AWO product related information
 *
 * @author tchen
 * @since 1/18/2016.
 */
public class Util {

  /**
   * This is the AWO project path
   */
  public static final String PROJECT_PATH = getProjectPath();

  /**
   * Properties file information for AWO project
   */
  public static final String PROPERTY_FOLDER = "properties";
  public static final String MA_PROPERTY_FOLDER = "ma_properties";
  public static final String RESOURCES_FOLDER = PROJECT_PATH + File.separator + "resources";
  public static final String PROPERTY_PATH = PROJECT_PATH + File.separator + PROPERTY_FOLDER;
  //public static final String MA_PROPERTY_PATH = PROPERTY_PATH +File.separator+ MA_PROPERTY_FOLDER;
  public static final String TEST_PROPERTY = PROPERTY_PATH + File.separator + "test.properties";
  public static final String TEST_DATA = PROPERTY_PATH + File.separator + "testdata.properties";
  public static final String LIVE_PROPERTY = PROPERTY_PATH + File.separator + "live.properties";
  public static final String UAT_TEST_DATA = PROPERTY_PATH + File.separator + "UATdata.properties";
  public static final String LOG4J_PROPERTY = PROPERTY_PATH + File.separator + "log4j.properties";
  public static final String DATAPOOL_ROOT = RESOURCES_FOLDER + File.separator + "datapool";
  public static final String PNG_ROOT = RESOURCES_FOLDER + File.separator + "png";
  public static final String CSV_ROOT = RESOURCES_FOLDER + File.separator + "csv";
  public static final String DOC_ROOT = RESOURCES_FOLDER + File.separator + "doc";
  public static final String JPG_ROOT = RESOURCES_FOLDER + File.separator + "jpg";
  public static final String PDF_ROOT = RESOURCES_FOLDER + File.separator + "pdf";
  public static final String GIF_ROOT = RESOURCES_FOLDER + File.separator + "gif";
  public static final String TXT_ROOT = RESOURCES_FOLDER + File.separator + "txt";
  public static final String JPEG_ROOT = RESOURCES_FOLDER + File.separator + "jpeg";
  public static final String CSV_DATA = CSV_ROOT + File.separator + "testdata";
  public static final String CSV_OUTPUT = CSV_ROOT + File.separator + "testresult";

  private static String ormsURL = null;
  private static String awoLogPath = null;

  public static void initAwo() {
    AutomationLogger.init(PROJECT_PATH, LOG4J_PROPERTY);
    initTestDataLoader();
    initTestProperty();
    BrowserAddon.init();
  }


  public static void initTestProperty() {
    TestProperty.load(Util.TEST_PROPERTY);
    if (TestProperty.getProperty("target_env").equalsIgnoreCase("live")) {
      //load the production sanity test information
      loadLiveInformation();
    }

    TestProperty.putProperty("property.folder", PROPERTY_PATH);

  }

  public static void initTestDataLoader() {
    TD_TestProperty.load(Util.TEST_DATA);
    if (TD_TestProperty.getProperty("target_env").equalsIgnoreCase("uat")) {
      //load the production sanity test information
      loadUATTestData();
    }

    TD_TestProperty.putProperty("ma_property.folder", PROPERTY_PATH);

  }

  /**
   * construct a default datapool path for the given script based on DATAOLL_ROOL folder.
   * for example if the script is com.activenetwork.qa.awo.testcases.sanity.SampleCase, the datapool path will be:
   * <DATAPOOL_ROOT>/testcases/sanity/SampleCase.datapool
   *
   * @param script
   * @return
   */
  public static String generateDatapoolPath(Class<?> script) {
    String datapoolPath = script.getName().substring(25).replaceAll("\\.", "/");
    return DATAPOOL_ROOT + File.separator + datapoolPath;
  }

  /**
   * construct a default datapool path for the given script with the given file name based on DATAOLL_ROOL folder.
   * for example if the script is com.activenetwork.qa.awo.testcases.sanity.SampleCase, and the file name is "TestDP", the datapool path will be:
   * <DATAPOOL_ROOT>/testcases/sanity/TestDP.datapool
   *
   * @param script
   * @param datapoolFileName
   * @return
   */
  public static String generateDatapoolPath(Class<?> script, String datapoolFileName) {
    String scriptFullName = script.getName();
    int index = scriptFullName.lastIndexOf(".");
    String datapoolPath = scriptFullName.substring(25, index).replaceAll("\\.", "/");
    return DATAPOOL_ROOT + File.separator + datapoolPath + File.separator + datapoolFileName;
  }

  public static void loadLiveInformation() {
    TestProperty.load(Util.LIVE_PROPERTY);
  }

  public static void loadUATTestData() {
    TestProperty.load(Util.UAT_TEST_DATA);
  }

  public static String getOrmsURL(String env) {
    return getOrmsURL(env, null);
  }

  public static String getOrmsURL(String env, String param) {
    if (ormsURL == null) {
      boolean loadbalance = Boolean.valueOf(TestProperty.getProperty(env + ".orms.loadbalance", "true"));
      int dispatchid = Integer.parseInt(TestProperty.getProperty(env + ".orms.url.default", "1"));
      if (loadbalance) {
        dispatchid = DataBaseFunctions.getLoadDispatch();
      }

      String url = TestProperty.getProperty(env + ".orms.url" + dispatchid);

      ormsURL = url;
    }

    if (param != null) {
      String url = ormsURL + "/" + TestProperty.getProperty("orms." + param);
      return url;
    } else {
      return ormsURL;
    }
  }

  /**
   * The project path was calculated based on assumption that the root project folder will always has a properties folder
   * and only root project folder can have a properties folder
   *
   * @return
   */
  public static String getProjectPath() {
    String path = null;
    URL classUrl = Util.class.getResource("Util.class");
    if (classUrl != null) {
      path = classUrl.getFile();
      if (path.startsWith("file:")) {
        path = path.substring(5).trim();
      }

      if (path.startsWith("/")) {
        path = path.substring(1).trim();
      }

      String className = Util.class.getName() + ".class";
      path = path.replaceAll(className, "");
    }

    path = FileUtil.searchFile(new File(path), "properties", true);

    return path;
  }


  /**
   * Load test cases from certain file
   *
   * @param caseslist -  a file with cases list as test suite
   * @return FileUtil which contain all these file
   */
  public static String loadListedCases(String caseslist) {
    String file = Util.PROJECT_PATH + File.separator + "testsuite" + File.separator + caseslist;

    String text = FileUtil.read(file);
    //text=text+",";
    //text=text.replaceAll("[^a-zA-Z_0-9]", ",").replaceAll("failed", ",").replaceAll(",+", "|");

    return text;
  }

  public static String getCurrentEnv() {
    return TestProperty.getProperty("target_env");
  }

  /**
   * Builds a java classpath for the current project.
   *
   * @param path -  a test case file or directory containing jar files.
   * @return a list jars
   */
  public static List<String> getClassPath(String path) {
    File file = new File(getProjectPath(), path);

    if (!file.exists()) {
      throw new ItemNotFoundException(file.getAbsolutePath()
                                      + " (Cannot find test case)");
    }

    List<String> jars = new ArrayList<String>();

    if (file.isFile() && file.getName().endsWith(".jar")) {
      String jarname = path;
      jars.add(getProjectPath() + jarname);
      //			logger.debug("  Found test case: " + testCase.getAbsolutePath());
    } else if (file.isDirectory() && !file.getName().equals(".svn")) {
      // skip '.svn' directories
      String[] dirContents = file.list();

      // recurse through files and sub-directories
      for (int i = 0; i < dirContents.length; i++) {
        jars.addAll(getClassPath(path + "/"
                                 + dirContents[i]));
      }
    }
    return jars;
  }

  /**
   * Bulds a list of test case files starting with the specified file/directory.  If the specified
   * test case is a file then list will contain only the specified file otherwise the list will
   * contain all files in the specified directory and all sub-directories.
   *
   * @param testCasePath -  a test case file or directory containing test case files.
   * @return a list of all File objects for each file found
   */
  public static List<String> getTestCaseFiles(String testCasePath) {
    File testCase = new File(getProjectPath(), testCasePath);

    if (!testCase.exists()) {
      throw new ItemNotFoundException(testCase.getAbsolutePath()
                                      + " (Cannot find test case)");
    }

    List<String> testCases = new ArrayList<String>();

    if (testCase.isFile() && (testCase.getName().endsWith(".java") || testCase.getName().endsWith(".class")) && (!testCase.getName().contains("$"))) {
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


}

