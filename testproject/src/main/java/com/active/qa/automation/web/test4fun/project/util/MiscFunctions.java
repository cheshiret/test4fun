package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.exception.ErrorOnDataException;
import com.active.qa.automation.web.testapi.exception.ErrorOnPageException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.interfaces.browser.Browser;
import com.active.qa.automation.web.testapi.interfaces.html.IHtmlObject;
import com.active.qa.automation.web.testapi.interfaces.html.IHtmlTable;
import com.active.qa.automation.web.testapi.util.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by tchen on 1/18/2016.
 */
public class MiscFunctions {
  /**
   * Get text from Clipboard
   *
   * @return text in Clipboard
   */
  public static String getClipboardText() {
    java.awt.datatransfer.Clipboard clip = java.awt.Toolkit
        .getDefaultToolkit().getSystemClipboard();
    java.awt.datatransfer.Transferable t = clip.getContents(null);
    String s = null;
    try {
      s = (String) t
          .getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
    } catch (Exception e) {
    }
    return s;
  }

  /**
   * get specified length String which is made up by specified letter.
   *
   * @param letter
   * @param length
   * @return
   */
  public static String getSpacifiedLengthString(char letter, int length) {
    StringBuffer sb = new StringBuffer();
    int i = 0;
    while (i < length) {
      sb.append(letter);
      i++;
    }

    return sb.toString();
  }

  /**
   * Print all objects stored in the given HashTable
   *
   * @param ht
   */
  public static void dumpHashtable(Hashtable<String, Object> ht) {
    Enumeration<String> enumerator = ht.keys();
    while (enumerator.hasMoreElements()) {
      Object obj = enumerator.nextElement();
      if (obj != null) {
        String d = (String) obj;
        System.out.println(d);
      }
    }
  }

  /**
   * Get a String represents the content of the array of Objects with
   * delimiter as ","
   *
   * @param objects
   * @return
   */
  public static String arrayToString(Object[] objects) {
    String toString = "";

    for (int i = 0; objects != null && i < objects.length; i++) {
      toString += objects[i].toString();

      if (i != objects.length - 1) {
        toString += ",";
      }
    }

    return toString;
  }

  /**
   * Print all property value of given Object
   *
   * @param o
   */
  public static void dumpProperty(IHtmlObject o) {
    try {
      System.out.println("text=" + o.text());
    } catch (Exception ee) {
    }

    // try{
    // System.out.println("html="+o.html());
    // }catch(Exception ee) {}
    try {
      System.out.println("innerText=" + o.text());
    } catch (Exception ee) {
    }
    try {
      System.out.println("href=" + o.getAttributeValue(".href"));
    } catch (Exception ee) {
    }
    try {
      System.out.println("id=" + o.id());
    } catch (Exception ee) {
    }
    try {
      System.out.println("name=" + o.name());
    } catch (Exception ee) {
    }
    // try{
    // System.out.println("title="+o.title());
    // }catch(Exception ee) {}
    // try{
    // System.out.println("className="+o.className());
    // }catch(Exception ee) {}
    try {
      System.out.println("type=" + o.type());
    } catch (Exception ee) {
    }
    // try{
    // System.out.println("value="+o.value());
    // }catch(Exception ee) {}
  }

  public static void dumpProperty(IHtmlObject[] elements) {
    int size = 0;

    try {
      size = elements.length;
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }

    for (int i = 0; i < size; i++) {
      System.out.println("Element#" + i);

      dumpProperty(elements[i]);
    }
  }

  public static Property[] expendPropertyArray(String propertyKey,
                                               Object value, int index, Property[] properties) {
    Property[] p = new Property[properties.length + 1];
    boolean duplicated = false;
    for (int i = 0; i < index; i++) {
      String name = properties[i].getPropertyName();
      if (name.equalsIgnoreCase(propertyKey)) {
        duplicated = true;
        break;
      }
      p[i] = properties[i];
    }

    if (duplicated) {
      return properties;
    } else {
      p[index] = new Property(propertyKey, value);

      for (int i = index; i < properties.length; i++) {
        p[i + 1] = properties[i];
      }

      return p;
    }
  }

  /**
   * Dumps each row + col of an HTML Table to console. Useful for determining
   * the content and layout of tables.
   *
   * @param table the table object to process.
   */
  public static void dumpTable(IHtmlTable table) {
    // Print out total rows & columns.
    System.out.println("Total Rows: " + table.rowCount());
    System.out.println("Total Cols: " + table.columnCount());

    // Print out cell values.
    for (int row = 0; row < table.rowCount(); row++) {

      for (int col = 0; col < table.columnCount(); col++) {

        System.out.println("Value at cell (" + row + "," + col
            + ") is: '" + table.getCellValue(row, col) + "'");
      }
    }
  }

  public static boolean matchOrEqual(Object value, String text) {
    if (value instanceof RegularExpression) {
      return ((RegularExpression) value).match(text);
    } else {
      return ((String) value).equalsIgnoreCase(text);
    }
  }


  public static boolean isRAEnv() {
    String url = Browser.getInstance().url();
    String env = TestProperty.getProperty("target_env");
    return url.startsWith(TestProperty.getProperty(env + ".web.ra.url")) || url.startsWith(TestProperty.getProperty(env + ".web.ra.security.url"));//Sara[08/26/2013], the previous for middle sign in, the later for beginning sign in
  }

  public static boolean isRECEnv() {
    String url = Browser.getInstance().url();
    String env = TestProperty.getProperty("target_env");
    return url.startsWith(TestProperty.getProperty(env + ".web.recgov.url")) || url.startsWith(TestProperty.getProperty(env + ".web.recgov.security.url"));//Sara[08/29/2013], the previous for middle sign in, the later for beginning sign in
  }

  public static boolean isPLEnv(String plwURL) {
    String url = Browser.getInstance().url();
    return url.startsWith(plwURL);
  }

  public static boolean isRAUnifiedSearchOpen() {
    String env = TestProperty.getProperty("target_env");
    return Boolean.valueOf((TestProperty.getProperty(env + ".web.ra.unifiedsearch")));
  }

  public static boolean isPLWUnifiedSearchOpen() {
    String env = TestProperty.getProperty("target_env");
    return Boolean.valueOf((TestProperty.getProperty(env + ".web.plw.unifiedsearch")));
  }

  public static boolean isPLWNYUnifiedSearchOpen() {
    String env = TestProperty.getProperty("target_env");
    return Boolean.valueOf((TestProperty.getProperty(env + ".web.plw.unifiedsearch")));
  }

  public static boolean isRECUnifiedSearchOpen() {
    return Boolean.valueOf((TestProperty.getProperty("recgov.unified.search")));
  }

  public static boolean positionMark_2() {
    return true;
  }

  public static boolean positionMark_3() {
    return true;
  }

  public static boolean positionMark_4() {
    return true;
  }

  public static boolean positionMark_5() {
    return true;
  }

  public static boolean isUATEnv() {
    String env = TestProperty.getProperty("target_env");

    return env.equals("qa5");
  }

  public static boolean blockByDefect() {
    return true;
  }

  public static boolean isNoCancel(String contract) {
    String contracts = TestProperty.getProperty(TestProperty
        .getProperty("target_env") + ".web.nocancel.contracts");
    contracts = "/" + contracts + "/";
    return contracts.contains("/" + contract.toUpperCase() + "/");
  }

  public static boolean isSiteTransfer(String contract) {
    String contracts = TestProperty.getProperty(TestProperty
        .getProperty("target_env") + ".web.issitetransfer.contracts");
    contracts = "/" + contracts + "/";
    return contracts.contains("/" + contract.toUpperCase() + "/");
  }

  public static boolean isRecSiteTransfer(String contract) {
    String contracts = TestProperty.getProperty(TestProperty
        .getProperty("target_env") + ".rec.issitetransfer.contracts");
    contracts = "/" + contracts + "/";
    return contracts.contains("/" + contract.toUpperCase() + "/");
  }

  public static boolean isNoChange(String contract) {
    String contracts = TestProperty.getProperty(TestProperty
        .getProperty("target_env") + ".web.nochange.contracts");
    contracts = "/" + contracts + "/";
    return contracts.contains("/" + contract.toUpperCase() + "/");
  }



  /**
   * Compare String list
   *
   * @param dscr
   * @param expectList
   * @param actualList
   */
  public static void compareStringList(String dscr, List<String> expectList, List<String> actualList) {
    AutomationLogger logger = AutomationLogger.getInstance();
    boolean result = compareListString(dscr, expectList, actualList);

    if (!result) {
      throw new ErrorOnPageException("Not all the check points are passed. Please check details info from previous logs.");
    } else {
      logger.info("All the check points are passed.");
    }
  }

  public static boolean compareListString(String dscr, List<String> expectList, List<String> actualList) {
    boolean result = true;

    result = result && compareResult("The size of " + dscr, expectList.size(), actualList.size());

    for (int i = 0; i < expectList.size(); i++) {
      result = result && compareResult("The value of " + dscr, expectList.get(i), actualList.get(i));
    }

    return result;
  }

  public static boolean compareArrayString(String dscr, String[] expect, String[] actual) {
    boolean result = true;

    result = result && compareResult("The length of " + dscr, expect.length, actual.length);

    if (result) {
      for (int i = 0; i < expect.length; i++) {
        result = result && compareResult("The value of " + dscr, expect[i], actual[i]);
      }
    }
    return result;
  }

  public static boolean compareList(String dscr, List<List<String>> expectList, List<List<String>> actualList) {
    boolean result = true;

    result = result && compareResult("The size of " + dscr, expectList.size(), actualList.size());

    for (int i = 0; i < expectList.size(); i++) {
      for (int j = 0; j < expectList.get(i).size(); j++) {
        result = result && compareResult("The value of " + dscr, expectList.get(i).get(j), actualList.get(i).get(j));
      }
    }

    return result;
  }

  /**
   * This method is used for comparing actual and expected values
   *
   * @param dscr
   * @param expectMsg
   * @param actualMsg
   */
  public static boolean compareResult(String dscr, Object expectMsg,
                                      Object actualMsg) {
    AutomationLogger logger = AutomationLogger.getInstance();

    if (expectMsg == null && actualMsg == null) {
      return true;
    }

    boolean result = true;
    if (expectMsg instanceof Boolean && actualMsg instanceof Boolean) {
      if (expectMsg != actualMsg) {
        result = false;
      }
    } else if (expectMsg instanceof Double && actualMsg instanceof Double) {
      if (Math.abs((Double) expectMsg - (Double) actualMsg) > 0.0001) {
        result = false;
      }
    } else if (expectMsg instanceof Integer && actualMsg instanceof Integer) {
      if (((Integer) expectMsg).intValue() != ((Integer) actualMsg).intValue()) {
        result = false;
      }
    } else if (expectMsg instanceof BigDecimal && actualMsg instanceof BigDecimal) {
      if (((BigDecimal) expectMsg).compareTo((BigDecimal) actualMsg) != 0) {//-1:less than, 0:equal,1:bigger than
        result = false;
      }
    } else if (DateFunctions.isValidDate((String) expectMsg) && DateFunctions.isValidDate((String) actualMsg)) {
      String expectedDate = DateFunctions.formatDate((String) expectMsg, "M/d/yyyy");
      String actualDate = DateFunctions.formatDate((String) actualMsg, "M/d/yyyy");
      if (DateFunctions.compareDates(expectedDate, actualDate) != 0) {
        result = false;
      }
    } else if (expectMsg instanceof String && actualMsg instanceof String) {
      if (!expectMsg.equals(actualMsg)) {
        result = false;
      }
    } else {
      throw new ErrorOnPageException("Unkown Object - expect:" + expectMsg.getClass().getName() + ", or actual:" + actualMsg.getClass().getName());
    }

    if (!result) {
      Object obj = dscr + " is wrong. Expected value is: " + expectMsg
          + ", but actual value is: " + actualMsg;
      logger.error(obj);
    } else {
      logger.info(dscr + " is correct as: " + expectMsg);
    }

    return result;
  }

  /**
   * Check if oriStr contain subStr
   *
   * @param msg
   * @param oriStr
   * @param subStr
   * @return
   * @author Lesley Wang
   * Apr 16, 2013
   */
  public static boolean containString(String msg, String oriStr, String subStr) {
    AutomationLogger logger = AutomationLogger.getInstance();
    if (oriStr.contains(subStr)) {
      logger.info(msg + " is correct! Original String is: " + oriStr + "; Sub String is: " + subStr);
      return true;
    } else {
      logger.error(msg + " is wrong! Original String is: " + oriStr + "; Sub String is: " + subStr);
      return false;
    }
  }

  /**
   * Check if oriStr matched the patter reg
   *
   * @param msg
   * @param oriStr
   * @param reg
   * @return
   * @author Lesley Wang
   * Apr 16, 2013
   */
  public static boolean matchString(String msg, String oriStr, String reg) {
    AutomationLogger logger = AutomationLogger.getInstance();
    if (oriStr.matches(reg)) {
      logger.info(msg + " is correct! Original String is: " + oriStr);
      return true;
    } else {
      logger.error(msg + " is wrong! Original String is: " + oriStr + "; but Regular Expression is: " + reg);
      return false;
    }
  }

  /**
   * Check if oriStr starts with subStr
   *
   * @param msg
   * @param oriStr
   * @param subStr
   * @return
   * @author Sara Wang
   * July 07, 2013
   */
  public static boolean startWithString(String msg, String oriStr, String subStr) {
    AutomationLogger logger = AutomationLogger.getInstance();
    if (oriStr.startsWith(subStr)) {
      logger.info(msg + " is correct! Original String is: " + oriStr + "; Sub String is: " + subStr);
      return true;
    } else {
      logger.error(msg + " is wrong! Original String is: " + oriStr + "; Sub String is: " + subStr);
      return false;
    }
  }

  public static boolean endWithString(String msg, String oriStr, String subStr) {
    AutomationLogger logger = AutomationLogger.getInstance();
    if (oriStr.endsWith(subStr)) {
      logger.info(msg + " is correct! Original String is: " + oriStr + "; Sub String is: " + subStr);
      return true;
    } else {
      logger.error(msg + " is wrong! Original String is: " + oriStr + "; Sub String is: " + subStr);
      return false;
    }
  }

  /**
   * This method is used for comparing actual and expected values
   *
   * @param dscr
   * @param expectMsg
   * @param actualMsg
   */
  public static void validateResult(String dscr, Object expectMsg, Object actualMsg) {
    boolean result = MiscFunctions.compareResult(dscr, expectMsg, actualMsg);
    if (!result) {
      throw new ErrorOnPageException();
    }
  }



  public static void compareResult(double expectResult, double actualResult,
                                   String msg) {
    if (Math.abs(expectResult - actualResult) > 0.0001) {
      throw new ErrorOnPageException(msg + ",Expect Result is:'"
          + expectResult + "',Actual Result is:'" + actualResult
          + "'.");
    }
  }

  public static Process startApp(String appPath) {
    Runtime rt = Runtime.getRuntime();

    try {
      Process p = rt.exec(appPath);
      return p;
    } catch (IOException e) {
      throw new ItemNotFoundException(e);
    }
  }

  private static IHtmlObject[] getTableObject() {
    IHtmlObject objs[] = Browser.getInstance().getTableTestObject(".id", "SearchVehicleRegistrationUIGrid_LIST");
    return objs;
  }

  /**
   * verify the whole column are the same value after searching by a common criteria, like 'Active'
   *
   * @param tableId
   * @param colName
   * @param expectedValue
   * @return
   */
  public static boolean verifySearchResultMatchCriteria(String tableId, String colName, String expectedValue) {
    IHtmlObject objs[] = getTableObject();
    if (objs.length < 1) {
      throw new ItemNotFoundException("Can't find Table object identified by id - " + tableId);
    }
    IHtmlTable table = (IHtmlTable) objs[0];

    int colIndex = table.findColumn(0, colName);
    List<String> columnValues = table.getColumnValues(colIndex);
    boolean exists = true;
    for (int i = 0; i < columnValues.size(); i++) {
      if (!columnValues.get(i).equals(expectedValue)) {
        exists = false;
        break;
      }
    }

    Browser.unregister(objs);
    return exists;
  }

  /**
   * verify specific record existing in the searching result after searching by a specific criteria, like id
   *
   * @param tableId
   * @param colName
   * @param expectedValue
   * @return
   */
  public static boolean verifyRecordExistInSearchResult(String tableId, String colName, String expectedValue) {
    IHtmlObject objs[] = getTableObject();
    if (objs.length < 1) {
      throw new ItemNotFoundException("Can't find Table object identified by id - " + tableId);
    }
    IHtmlTable table = (IHtmlTable) objs[0];

    int colIndex = table.findColumn(0, colName);
    List<String> columnValues = table.getColumnValues(colIndex);
    boolean exists = false;
    for (int i = 0; i < columnValues.size(); i++) {
      if (columnValues.get(i).equals(expectedValue)) {
        exists = true;
        break;
      }
    }

    Browser.unregister(objs);
    return exists;
  }

  public static String getPLNameFromURL(String url) {
//		RegularExpression pattern=new RegularExpression("qa\\d-\\w+\\.qa",false);
//		String[] tokens=pattern.getMatches(url);
//		String text=tokens[0].substring(4,tokens[0].length()-3).replaceAll("\\d+", "");
    String urlPrefix = "https://";
    String text = url.split("-")[0].replace(urlPrefix, StringUtil.EMPTY);
    return text;
  }

  /**
   * Get PL Name from current URL
   */
  public static String getPLNameFromURL() {
    String url = Browser.getInstance().url();
    return getPLNameFromURL(url);
  }


  public static boolean compareString(String dscr, String expectStr, String actualStr) {
    AutomationLogger logger = AutomationLogger.getInstance();
    boolean pass = true;
    if (StringUtil.isEmpty(expectStr) && StringUtil.isEmpty(actualStr))
      return true;
    //handle money data compare
    if (expectStr.matches("^\\d+\\.\\d{2}$")) {
      BigDecimal decimal1 = new BigDecimal(String.valueOf(expectStr.replace(".", "")));
      BigDecimal decimal2 = new BigDecimal(String.valueOf(actualStr.replace(".", "")));
      pass = decimal1.compareTo(decimal2) == 0;
    } else if (!expectStr.equalsIgnoreCase(actualStr))
      pass = false;

    if (!pass) {
      logger.error(dscr + " is wrong. Expected value is: " + expectStr
          + ", but actual value is: " + actualStr);
    } else {
      logger.info(dscr + " is correct as: " + expectStr);
    }
    return pass;
  }

  public static void checkAllTestCaseInDB(String path) {
    DatabaseInst db = (DatabaseInst) DatabaseInst.getInstance();
    AutomationLogger logger = AutomationLogger.getInstance();
    db.connect();
    String query = "";
    List<String> caseNames = FileUtil.listFiles(path, ".java");
    logger.info("Please see not insert into DB case under folder '" + path + "' as below:");
    for (String casename : caseNames) {
      String fileName = casename;
//			casename = casename.replaceFirst(AwoUtil.PROJECT_PATH.replaceAll("/", "."), "");
      casename = casename.substring(casename.indexOf("testcases")).replaceAll("/", ".").replaceAll(".java", "");
      casename = casename.replaceAll("\\\\", ".");
      query = "select count(*) as count from test_cases where casename='" + casename + "'";
      String result = db.executeQuery(query, "count", 0);
      if (Integer.parseInt(result) < 1) {
        List<String> authors = FileUtil.scanFile(fileName, " \\* @author.*", "\\* @Date.*");
        String author = "";
        if (authors != null && authors.size() > 0) {
          author = authors.get(0).replaceAll("\\*", "");
        }
        logger.error(author + ": " + casename);
      }
    }
    logger.info("Finish Check,please see detail from log file.");
  }

  public static void createAutoSpiraTeamMapping(String path) {
    DatabaseInst db = (DatabaseInst) DatabaseInst.getInstance();
    AutomationLogger logger = AutomationLogger.getInstance();
    db.connect();
    String sql = "";

    if (path.endsWith(".class")) {
      path = path.replaceAll(".class", ".java");//used to handle from insertTestCaseIntoDB script about insert single case
    }

    List<String> caseNames = FileUtil.listFiles(path, ".java");

    logger.info("Start to create Mapping between Auto Case and Spria team TC under folder '" + path + "'.");
    int mappingCount = 0;
    for (String casename : caseNames) {
      if (casename.endsWith(".java")) {
        casename = casename.replace(".java", "");
      }
      casename = casename.replaceAll("\\.", "/");
      String caseNum = "";
      List<String> list = FileUtil.scanFile(casename + ".java", "\\* @SPEC:.*", "\\ * @Task#:.*");
      if (list.size() > 0) {
        String temp = StringUtil.listToString(list, true);
        String[] nums = RegularExpression.getMatches(temp, "\\d{5,}+");
        caseNum = StringUtil.arrayToString(nums);
      }

      if (StringUtil.notEmpty(caseNum)) {
        casename = casename.substring(casename.indexOf("testcases"));
        casename = casename.replaceAll("/", ".");
        sql = "select id from test_cases where UPPER(casename)=UPPER('" + casename + "')";
        String script_id = db.executeQuery(sql, "id", 0);
        sql = "select count(*) count from spira_auto where script_id=" + script_id;
        int count = Integer.parseInt(db.executeQuery(sql, "count", 0));
        if (count < 1) {
          sql = "insert into spira_auto(tc_number,script_id) (select '" + caseNum + "',id from test_cases where casename='" + casename + "')";
          logger.info("Create mapping for case - " + casename);
          System.out.println("Mapping TC#" + caseNum + " with script -->" + script_id);
          mappingCount++;
        } else {
          sql = "update spira_auto set tc_number='" + caseNum + "' where script_id=" + script_id;
          logger.info("Mapping already exists - " + casename);
          System.out.println("Update mapping to " + script_id + "-->" + caseNum);
        }

        try {
          db.executeUpdate(sql);
        } catch (Exception e) {
          logger.error("Insert Case '" + casename + "' failed.");
          logger.error(e.getMessage());
        }

      } else {
//				throw new ItemNotFoundException(casename+" has NO required comments, at least no TC number.");
        logger.error(casename + " has NO required comments, at least no TC number.");
      }
    }
    logger.info("Create Mapping Done. Total create " + mappingCount + " of " + caseNames.size());
  }


  /**
   * Convert transmission status.
   *
   * @param statusCode
   * @return
   */
  public static String convertTransmissionStatus(String statusCode) {
    if ("1".equals(statusCode)) {
      return "Pending";
    } else if ("2".equals(statusCode)) {
      return "Held";
    } else if ("3".equals(statusCode)) {
      return "Failed";
    } else if ("4".equals(statusCode)) {
      return "Adjusted";
    } else if ("5".equals(statusCode)) {
      return "Bypassed";
    } else if ("6".equals(statusCode)) {
      return "Sent";
    } else if ("7".equals(statusCode)) {
      return "Paid";
    } else {
      return "";
    }
  }


  public static String salesChannelConvert(String sc) {
    if (sc.equalsIgnoreCase("fm"))
      return "Field Manager";
    else if (sc.equalsIgnoreCase("am"))
      return "Admin Manager";
    else if (sc.equalsIgnoreCase("cm"))
      return "Call Manager";
    else if (sc.equalsIgnoreCase("rm"))
      return "Resource Manager";
    else if (sc.equalsIgnoreCase("om"))
      return "Operations Manager";
    else if (sc.equalsIgnoreCase("im"))
      return "Inventory Manager";
    else if (sc.equalsIgnoreCase("finm"))
      return "Finance Manager";
    else if (sc.equalsIgnoreCase("vm"))
      return "Venue Manager";
    else if (sc.equalsIgnoreCase("pm"))
      return "Permit Manager";
    else if (sc.equalsIgnoreCase("lm"))
      return "License Manager";
    else if (sc.equalsIgnoreCase("mm"))
      return "Marina Manager";
    else
      throw new ItemNotFoundException(sc + " is not predefined.");
  }


  @SuppressWarnings("rawtypes")
  public static Map.Entry[] getSortedHashMapByValue(HashMap<String, String> map) {
    Set<?> set = map.entrySet();
    Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);

    Arrays.sort(entries, new Comparator<Object>() {
      public int compare(Object arg0, Object arg1) {
        BigDecimal key1 = new BigDecimal(Double.valueOf(((Map.Entry) arg0).getValue().toString()));
        BigDecimal key2 = new BigDecimal(Double.valueOf(((Map.Entry) arg1).getValue().toString()));
        return key1.compareTo(key2);
      }
    });

    return entries;
  }

  @SuppressWarnings("rawtypes")
  public static Map.Entry[] getSortedHashMapByKey(HashMap<String, String> map) {
    Object[] key_arr = map.keySet().toArray();
    Arrays.sort(key_arr);
    LinkedHashMap<Object, String> sortedMap = new LinkedHashMap<Object, String>();
    for (Object key : key_arr) {
      sortedMap.put(key, map.get(key));
    }
    Set<?> set = sortedMap.entrySet();
    Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
    return entries;
  }

  public static void linkSetUpDataWithCases(String casePack) {
    DatabaseInst db = (DatabaseInst) DatabaseInst.getInstance();
    Util.initTestProperty();
    AutomationLogger logger = AutomationLogger.getInstance();
    db.connect();
    String projectPath = Util.getProjectPath();
    String casePath = projectPath.replaceAll("\\\\", "/") + "/" + casePack.replaceAll("\\.", "/");
    List<String> caseNames = new ArrayList<String>();
    if (!FileUtil.isDirectory(casePath)) {
      casePath += ".java";
    }
    caseNames = FileUtil.listFiles(casePath, ".java");
    logger.info("Start to create Mapping between Auto Case and set up data table under folder '" + casePack + "'.");
    for (String casename : caseNames) {
      int mappingCount = 0;
      List<String[]> tableIds = getTableAndIdsForDataLinkWithCase(casename, "(:id)?=");
      String casenameInDB = casename.substring(casename.indexOf("testcases"), casename.lastIndexOf("."));
      casenameInDB = casenameInDB.replaceAll("\\/", ".");
      System.out.println(casenameInDB);
      if (tableIds.size() < 1) {
        System.out.println("No Linked Setup data!!!");
        logger.error(
            "Did not found any support data for case:" + casenameInDB + ", please check if it has any!!!");
      }
      String sql = "select id from test_cases where UPPER(casename)=UPPER('" + casenameInDB + "')";
      String caseId = db.executeQuery(sql, "id", 0);
      if (StringUtil.isEmpty(caseId)) {
        throw new ErrorOnDataException(
            "Can't find id for case:" + casenameInDB + ", please check if it has been insert into db!!!");
      }
      for (String[] tableId : tableIds) {
        String dataTable = tableId[0];
        String dataId = tableId[1];
        sql = "select count(*) count from testcase_setupdata where UPPER(table_name)=UPPER('" + dataTable + "') and setupdata_id="
            + dataId + " and case_id='" + caseId + "'";
//				logger.info("Check if added by running sql:" + sql);
        int count = Integer.parseInt(db.executeQuery(sql, "count", 0));
        if (count < 1) {
          sql = " INSERT INTO testcase_setupdata(case_id, table_name, setupdata_id) values (" +
              caseId + ",'" + dataTable + "', " + dataId + ")";
//					logger.info("Set up link by running sql:" + sql);
          try {
            db.executeUpdate(sql);
            System.out.println(caseId + "	" + dataTable + "	" + dataId);
          } catch (Exception e) {
            logger.error("Linked set up data failed '" + casenameInDB + "' failed.");
            logger.error(e.getMessage());
          }
        }
        mappingCount++;
      }
      logger.info("Create total " + mappingCount + " mapping records for case---" + casenameInDB);
    }
  }

  private static List<String[]> getTableAndIdsForDataLinkWithCase(String casepath, String middleMark) {
    List<String[]> tableIds = new ArrayList<String[]>();
    List<String> list = FileUtil.scanFile(casepath, ".*@(LinkSetUp|Preconditions)\\:.*", ".*@(?!(LinkSetUp|Preconditions)).*");
    String commentContent = StringUtil.listToString(list, true).replace("* @LinkSetUp:", StringUtil.EMPTY);
    RegularExpression pattern = new RegularExpression("(d_\\w*)" + middleMark + "(\\d+(,\\d+)*)", false);
    String[] tokens = pattern.getMatches(commentContent);
    for (int i = 0; i < tokens.length; i++) {
      String[] tableAndIds = tokens[i].split(middleMark);
      String[] ids = tableAndIds[1].split(",");
      for (String id : ids) {
        String[] tableId = new String[2];
        tableId[0] = tableAndIds[0].trim();
        tableId[1] = id.trim();
        tableIds.add(tableId);
      }
    }
    return tableIds;
  }

  /**
   * Add by Sara
   *
   * @param value
   * @return
   */
  public static String regxBracket(String value) {
    return value.replace("(", "\\(").replace(")", "\\)");
  }

  public static String removeBracket(String value) {
    return value.replace("[", "").replace("]", "");
  }
}

