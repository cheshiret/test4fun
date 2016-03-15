package com.active.qa.automation.web.test4fun.project.scripts.support.dbsetup;

import com.active.qa.automation.web.test4fun.project.util.DatabaseInst;
import com.active.qa.automation.web.test4fun.project.util.Util;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.FileUtil;
import com.active.qa.automation.web.testapi.util.StringUtil;

import java.io.File;
import java.util.List;

/**
 * @author : tchen
 * @since : 3/15/2016.
 */
public class InsertTestCaseNameIntoDB {
  public static final int INACTIVE = 0;
  public static final int ACTIVE = 1;
  public static final int DRAFT = 2;

  public static final int SELENIUM_TOOL = 1;

  private static String prefix = "java/com/activenetwork/qa/awo/";
  DatabaseInst db = null;

  public InsertTestCaseNameIntoDB() {
    AutomationLogger.init(Util.PROJECT_PATH, Util.LOG4J_PROPERTY);
    Util.initTestProperty();
    db = DatabaseInst.getInstance();
  }

  public static void main(String[] args) {

    InsertTestCaseNameIntoDB insertTestCaseNameIntoDB = new InsertTestCaseNameIntoDB();

    //path can be either a folder or a file
    //a folder
    //String path= "testcases/regression/advanced/orms/inventory/closure/";
    //String path= "testcases.regression.advanced.orms.inventory.closure";//Note: should use path = path.replaceAll("\\.", "/");

    //Single file
    //String path= "testcases/regression/advanced/orms/inventory/closure/SearchClosure.class";

    String packagePath = prefix.replaceAll("/", "\\.") + "testcases.sanity.orms.migrauto";   //Note:This is package path, please do not add ".java" even it is a single case

    String path = packagePath.replaceAll("\\.", "/");

    File file = new File(path);
    if (!file.isDirectory()) {
      path = path + ".java";
    }

    System.out.println(path);
    String caseOwner = "tchen"; //Input your name, and will be write into DB.
    String sprint = "26";//input sprint number,only number is fine, such as 39
    int multi = 0;
    System.out.println("Insert Case for sprint#" + sprint + ",Author is '" + caseOwner + "'");
    insertTestCaseNameIntoDB.insertTestCasesIntoDB(path, ACTIVE, ACTIVE, ACTIVE, ACTIVE, ACTIVE, ACTIVE, SELENIUM_TOOL, caseOwner, multi, sprint);

    //Disable for uploading migration test cases.
//		MiscFunctions.createAutoSpiraTeamMapping(AwoUtil.getProjectPath()+"\\"+path);

//		MiscFunctions.linkSetUpDataWithCases(packagePath);
  }

  public static void updateTestCaseHelper(String criteriaClause, String setClause) {
    DatabaseInst db = (DatabaseInst) DatabaseInst.getInstance();
    db.connect();

    String query = "update test_cases set " + setClause + " where " + criteriaClause;
    db.executeUpdate(query);
  }

  public static void deactivateTestCases(String[] testcases, String env) {
    DatabaseInst db = (DatabaseInst) DatabaseInst.getInstance();
    db.connect();
    String query = "update test_cases set qa1_active=0, qa2_active=1,qa3_active=0,qa4_active=1 where casename in (" + StringUtil.arrayToString(testcases, true) + ")";
    db.executeUpdate(query);
    db.disconnect();
  }

  /**
   * Create testcase name criteria from String array of case names
   *
   * @param caseNames
   * @return
   */
  public static String createCaseNameCriteria(String[] caseNames) {
    String names = "";
    if (caseNames != null && caseNames.length > 0) {
      StringBuffer namesBuf = new StringBuffer();
      if (caseNames.length == 1) {
        namesBuf.append("casename='");
        namesBuf.append(caseNames[0]);
        namesBuf.append("'");
      } else {
        for (int i = 0; i < caseNames.length; i++) {
          if (i != 0) {
            namesBuf.append(",");
          } else {
            namesBuf.append("casename in (");
          }
          namesBuf.append("'");
          namesBuf.append(caseNames[i]);
          namesBuf.append("'");
          if (i == caseNames.length - 1) {
            namesBuf.append(")");
          }
        }
      }
      names = namesBuf.toString();
    }

    return names;
  }

  /**
   * create test case name criteria from test case names within the given file
   *
   * @param fileNmae
   * @return
   */
  public static String createCaseNameCriteria(String fileNmae) {
    return createCaseNameCriteria(FileUtil.readLines(fileNmae));
  }

  private static String and(String... criteria) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < criteria.length; i++) {
      if (i != 0) {
        buf.append(" and ");
      }
      buf.append(criteria[i]);
    }
    return buf.toString();
  }

  /**
   * Reset the test result status to the given resultStatus for the test cases names provided in given file
   * Any set test result status to otherStatus for all other test cases for the given testset, product and env
   * This method is frequently used to reset test cases status which is cleaned by some command by mistake
   *
   * @param caseNameFile
   * @param testset
   * @param product
   * @param env
   * @param resultStatus
   * @param otherStatus
   */
  public static void resetTestcaseStatus(String caseNameFile, String testset, String product, String env, int resultStatus, int otherStatus) {
    String caseNameCriteria = createCaseNameCriteria(FileUtil.readLines(caseNameFile));
    String productCriteria = "product='" + product + "'";
    String testsetCriteria = "test_set='" + testset + "'";
    String setResultStatus = env + "_result=" + resultStatus;
    String setOtherResultStatus = env + "_result=" + otherStatus;

    updateTestCaseHelper(and(productCriteria, testsetCriteria), setOtherResultStatus);
    updateTestCaseHelper(and(productCriteria, testsetCriteria, caseNameCriteria), setResultStatus);
  }

  /**
   * This method will insert all test cases in the give path to the TEST_CASES table.
   * It will skip any test cases which have already been inserted.
   *
   * @param path
   * @param qa1_active
   * @param qa2_active
   * @param qa3_active
   * @param qa4_active
   */
  public void insertTestCasesIntoDB(String path, int qa1_active, int qa2_active, int qa3_active, int qa4_active, int qa5_active, int lvmig_active, int tool_code, String caseOwner, int multi, String sprint) {
    String caseName = "", product = "", testSuite = "";
    db.connect();
    String query = "select ifnull(Max(id),0) as id from test_cases";
    //String query = "select Max(id) as id from test_cases";
    int idSeq = Integer.parseInt(db.executeQuery(query, "id", 0));

    List<String> testCases = (List<String>) Util.getTestCaseFiles(path);//("testCases/regression/basic/web/");
    prefix = prefix.replaceAll("/", "\\.");
    for (int i = 0; i < testCases.size(); i++) {

      caseName = testCases.get(i).toString().replace(prefix, "");
      query = "select count(*) as count from test_cases where UPPER(caseName)= UPPER('" + caseName + "')";
      String result = db.executeQuery(query, "count", 0);


      if (Integer.parseInt(result) < 1) {
        if (caseName.indexOf(".web.") > 0) {
          product = "web";
        } else if (caseName.indexOf(".orms.") > 0) {
          product = "orms";
        } else if (caseName.indexOf(".activenet.") > 0) {
          product = "ane";
        } else {
          throw new ItemNotFoundException("Failed to retrieve product name from case name: " + caseName);
        }

        if (caseName.indexOf(".basic.") > 0)
          testSuite = "basic";
        else if (caseName.indexOf(".advanced.") > 0)
          testSuite = "advanced";
        else if (caseName.indexOf(".sanity.") > 0)
          testSuite = "sanity";
        else
          throw new ItemNotFoundException("Failed to retrieve test suite name from case name: " + caseName);

        idSeq += 10;
        //query = "insert into test_cases values (" + idSeq + ",\'"+ caseName + "\','"+product+"','"+testSuite+"',null,sysdate,"+multi+",120,'"+caseOwner+"',null,null, null, null, null, "+qa1_active+","+qa2_active+","+qa3_active+","+qa4_active+","+qa5_active+",0,"+tool_code+","+tool_code+","+tool_code+","+tool_code+",null,0,0,0,0,0,2,2,2,2,2,null, null, null,"+sprint+")";
        query = "insert into test_cases values (" + idSeq + ",\'" + caseName + "\','" + product + "','" + testSuite + "',null,"
            + "sysdate(),null," + multi + ",120,'" + caseOwner + "',null,null, null,null,null, null, "
            + qa1_active + "," + qa2_active + "," + qa3_active + "," + qa4_active + "," + qa5_active + "," + lvmig_active
            + ",0," + tool_code + "," + tool_code + "," + tool_code + "," + tool_code + "," + tool_code + ",null,0,0,0,0,0,0,2,2,2,2,2,2,null, null, null," + sprint + ")";

        System.out.println(idSeq + " " + caseName);
        db.executeUpdate(query);
      }
    }
  }
}


