package com.active.qa.automation.web.test4fun.util.project;

import com.active.qa.automation.web.test4fun.TestConstants;
import com.active.qa.automation.web.testapi.TestApiConstants;
import com.active.qa.automation.web.testapi.exception.ErrorOnDataException;
import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.util.*;

import java.util.List;
import java.util.TimeZone;

/**
 * Created by tchen on 1/18/2016.
 */
public class DataBaseFunctions {
  private static AutomationLogger logger = AutomationLogger.getInstance();
  private static DatabaseInst db = (DatabaseInst) DatabaseInst.getInstance();

  /**
   * get location Short name by location name from DB
   */
  public static String getLocationShortName(String name, String schema) {
    String sql = "SELECT SHORT_NAME FROM D_LOC WHERE NAME LIKE '" + name
        + "'";
    db.resetSchema(schema);
    return db.executeQuery(sql, "SHORT_NAME", 0);
  }

  public static String getSiteLoopName(String schema, String parkId,
                                       String siteID) {
    String sql = "SELECT NAME FROM D_LOC WHERE ID =(SELECT LOC_ID FROM P_PRD  WHERE PRD_ID = '"
        + siteID + "' AND PARK_ID ='" + parkId + "') AND level_num =50";
    db.resetSchema(schema);
    try {
      return db.executeQuery(sql, "NAME", 0);
    } catch (Exception e) {
      return "";
    }
  }

  public static String getSchemaName(String contractBrief) {
    String sql = "select login from d_env where name=UPPER(contractBrief)";
    String env = TestProperty.getProperty("target_env");
    db.resetSchema(TestProperty.getProperty(env + ".global.schema"));
    try {
      return db.executeQuery(sql, "login", 0);
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Get specific park time zone
   *
   * @param schema
   * @param parkName
   * @return
   */
  public static TimeZone getParkTimeZone(String schema, String parkName) {
    String timeZoneCode = getTimeZoneString(schema, parkName);
    TimeZone timeZone = TimeZone.getTimeZone(timeZoneCode);
    return timeZone;
  }

  public static TimeZone getParkTimeZoneByParkID(String schema, String parkID) {
    String timeZoneCode = getTimeZoneStringByParkID(schema, parkID);
    TimeZone timeZone = TimeZone.getTimeZone(timeZoneCode);
    return timeZone;
  }

  /**
   * get timezone for specified contract by given schema
   *
   * @param schema DB schema
   * @return String
   */
  public static TimeZone getContractTimeZone(String schema) {
    TimeZone timezone = null;
    String timezoneCode = getTimeZoneString(schema);
    timezone = TimeZone.getTimeZone(timezoneCode);
    return timezone;
  }

  public static TimeZone getContractTimeZone(String schema, String id) {
    String timezoneCode = "US/Eastern";
    String sql = "Select * from D_LOC where id=" + id;
    if (id == null || id.length() < 1) {
      throw new ErrorOnDataException("");
    }
    // if schema are not specified,
    // will choose the US/Eastern time
    if (schema != null && schema.trim().length() > 0) {
      // query time zone from DB
      db.resetSchema(schema);
      timezoneCode = db.executeQuery(sql, "TIME_ZONE", 0);
    }

    return TimeZone.getTimeZone(timezoneCode);
  }

  /**
   * Get a specific park time zone
   *
   * @param schema   - if null, return US/Eastern by default
   * @param parkName - if null, return contract level time zone
   * @return
   */
  public static String getTimeZoneString(String schema, String parkName) {
    String timezoneCode = "US/Eastern";
    String sql = "";
    if (parkName == null || parkName.length() == 0) {
      sql = "select * from D_LOC where ID = 1";
    } else {
      sql = "select * from D_LOC where UPPER(NAME) = UPPER('" + parkName
          + "') and LEVEL_NUM=40";// 40----Facility
    }
    // if schema are not specified,
    // will choose the US/Eastern time
    if (schema != null && schema.trim().length() > 0) {
      // query time zone from DB
      db.resetSchema(schema);
      timezoneCode = db.executeQuery(sql, "TIME_ZONE", 0);
    }
    return timezoneCode;
  }

  public static String getTimeZoneStringByParkID(String schema, String parkID) {
    String timezoneCode = "US/Eastern";
    String sql = "";
    if (parkID == null || parkID.length() == 0) {
      sql = "select * from D_LOC where ID = 1";
    } else {
      sql = "select * from D_LOC where ID = " + parkID;
    }
    // if schema are not specified,
    // will choose the US/Eastern time
    if (schema != null && schema.trim().length() > 0) {
      // query time zone from DB
      db.resetSchema(schema);
      timezoneCode = db.executeQuery(sql, "TIME_ZONE", 0);
    }
    return timezoneCode;
  }

  /**
   * get time zone String for specified contract by given schema
   *
   * @param schema
   * @return
   */
  public static String getTimeZoneString(String schema) {
    return getTimeZoneString(schema, null);
  }

  /**
   * Query information from qa_automation table with given parameter
   *
   * @param var
   * @return
   */
  public static String getValueFromQAAutomationTable(String var) {
    db.resetDefaultDB();
    String query = "select val from qa_automation where var=\'" + var
        + "\'";
    String value = db.executeQuery(query, "val", 0);
    return value;
  }

  public static void setValueFromQAAutomationTable(String var, String val) {
    setValueFromQAAutomationTable(var, val, null);
  }

  public static void setValueFromQAAutomationTable(String var, String val,
                                                   String env) {
    db.resetSchema("qa_user");
    String setClause = (StringUtil.isEmpty(env) ? "val" : "val_" + env)
        + "='" + val + "'";
    String query = "update qa_automation set " + setClause + "where var=\'"
        + var + "\'";
    db.executeUpdate(query);
  }

  /**
   * Get the sequence Num of specific var value in QA_AUTOMATION TABLE
   *
   * @param var
   * @return
   */
  public static String getSeqNumber(String var) {
    String seq = getValueFromQAAutomationTable(var);
    int newSeq = Integer.parseInt(seq) + 1;
    if (newSeq > 10000) {
      String queryUpdate = "update qa_automation set val=\'" + newSeq
          + "\' where var=\'" + var + "\'";
      db.executeUpdate(queryUpdate);
    }

    return seq;
  }

  /**
   * Record page time and insert it into DB
   *
   * @param pageName
   * @param time
   */
  public static void recordPageTime(String pageName, int toolID, int time) {
    if (!TestProperty.getProperty("pageTiming").equalsIgnoreCase("true")) {
      // page timing is off
      // logger.debug("Page timing: page time flag is off.");
      return;
    }

    if (!pageName.matches("\\w+((P|p)age|(B|b)ar|(P|p)anel)$")) {
      // not a html web page
      // logger.debug("Page timing: OFF due to not a web page.");
      return;
    }

    String project = TestProperty.getProperty("product");
    String build = TestProperty.getProperty(project + ".build");

    if (StringUtil.isEmpty(build) || build == "null") {
      // no build number,no page timing
      // logger.debug("Page timing: OFF due to Build number is unknown.");
      return;
    }
    try {
      String runningId = TestProperty.getProperty("running.id");
      String env = TestProperty.getProperty("target_env");
      String fullCaseName = TestProperty.getProperty("fullCaseName");
      db.resetDefaultDB();
      db.connect();

      String queryInsert = "insert into page_timing (pagename,loadingtime,buildversion,testcasename,env,project,recorddate,execution_id,tool) values "
          + "('"
          + pageName
          + "',"
          + time
          + ",'"
          + build
          + "','"
          + fullCaseName
          + "','"
          + env
          + "','"
          + project
          // update sysdate to sysdate() if db vendor is mysql
          + "',"
          + "sysdate(),'"
          + runningId
          + "'"
          + ","
          + toolID
          + ")";

      db.executeUpdate(queryInsert);
      db.disconnect();
    } catch (Exception e) {
      logger.warn("Failed to record page time due to " + e.getMessage());
    }
  }

  /**
   * Retrieve the testcase id from TEST_CASES table in QA_USER schema
   *
   * @param caseName - the test case full name. For example:
   *                 testCases.sanity.orms.CM_AddCustomer
   * @return - the test case id, if the test case name is not in the table,
   * return 0
   */
  public static int getTestCaseID(String caseName) {
    if (caseName.matches("^testCases\\.production\\..+")) {
      return 0;
    }

    db.resetDefaultDB();
    db.connect();

    // String query =
    // "select id from test_cases where UPPER(casename)=UPPER('"
    // + caseName + "')";

    String query = "select id from test_cases where casename='" + caseName
        + "'";
    List<String> result = db.executeQuery(query, "id");

    if (result.size() < 1) {
      return 0;
    } else {
      return Integer.parseInt(result.get(0).toString());
    }
  }

  /**
   * Retrieve the last email account sequence number.
   *
   * @return - the sequence number
   */
  public static int getEmailSequence() {
    db.resetDefaultDB();
    db.connect();
    // String query = "select email_seq.nextval as seq from dual"; //oracle
    String query = "select nextval('email_seq') as seq";// mysql
    String seq = db.executeQuery(query, "seq", 0);
    db.disconnect();

    return Integer.parseInt(seq);
  }

  /**
   * Record the build number for the given running id in DB.
   *
   * @param runningid
   *            - the test suite submitting id
   * @param buildNumber
   *            - the orms or web build number
   */
  // public static void recordBuildNumber(String runningid, String
  // buildNumber) {
  // db.resetDefaultDB();
  //
  // String query = "insert into testrunning_build values ('" + runningid
  // + "','" + buildNumber + "')";
  // db.executeUpdate(query);
  // }

  /**
   * Get the current build number for the given running id
   *
   * @param runningid
   *            - this is the test suite submitting id
   * @return the build number, or null if not found
   */
  // public static String getBuildNumber(String runningid) {
  // db.resetDefaultDB();
  //
  // String query = "select build from testrunning_build where runningid='"
  // + runningid + "'";
  // List<String> result = db.executeQuery(query, "build");
  //
  // if (result.size() < 1) {
  // return null;
  // } else {
  // return result.get(0).toString();
  // }
  // }

  /**
   * Get the current schema name from contract abbreviation and envrionment
   *
   * @param contract - contract abbreviation
   * @param env      - envrionment qa1 or qa2
   * @return - the schema name
   */
  public static String getSchemaName(String contract, String env) {
    String schema = "";
    schema = TestProperty.getProperty(env + ".db.schema.prefix") + contract;

    return schema;
  }

  public static String getRIDBSchemaName(String env) {
    String schema = "";
    schema = TestProperty.getProperty(env + ".ridb.schema");

    return schema;
  }

  public static String getPin(String login) {
    String env = TestProperty.getProperty("target_env");
    String schema = TestProperty.getProperty(env + ".global.schema");
    db.resetSchema(schema);
    db.connect();

    String query = "select pin from d_user_auth where login='" + login
        + "'";
    String pin = db.executeQuery(query, "pin", 0);

    return pin;
  }

  public static String getContractFromSchemaName(String schema) {
    int index = schema.lastIndexOf("_");
    return schema.substring(index + 1);
  }

  public static void updateTestCaseTiming(String caseFullName, int time) {
    if (testcaseRegistered(caseFullName)) {
      db.resetSchema("auto");
      String query = "select timing from test_cases where casename=\'"
          + caseFullName + "\'";
      String value = db.executeQuery(query, "TIMING", 0);
      int previousTime = Integer.parseInt(value);

      if (value == null || previousTime < time
          || previousTime - time > 300) {
        query = "update test_cases set timing=" + time
            + ", update_date=sysdate()" + " where casename=\'"
            + caseFullName + "\'";
        int count = db.executeUpdate(query);
        if (count <= 0) {
          AutomationLogger.getInstance().warn(
              "Failed to update the execution time for "
                  + caseFullName);
        }
      }
    }
  }

  public static boolean testcaseRegistered(String caseFullName) {
    db.resetSchema("auto");
    String query = "select count(*) as count from test_cases where casename=\'"
        + caseFullName + "\'";
    String value = db.executeQuery(query, "COUNT", 0);
    if (value == null || Integer.parseInt(value) <= 0) {
      return false;
    } else {
      return true;
    }
  }

  public static String getKOASiteID(String park_id, String code) {
    String env = TestProperty.getProperty("target_env");
    String schema = TestProperty.getProperty(env + ".db.schema.prefix")
        + "KOA";
    db.resetSchema(schema);

    // String
    // query="select p.prd_id as id from p_prd p, p_prd_attr pa where p.prd_id=pa.prd_id and pa.attr_id=109 and p.park_id=196854 and p.prd_cd='"
    // + code + "' and pa.attr_value='55'"; //after changed schema,
    // currently there are no site match give attr_value and park_Id is
    // always changed
    String query = "select p.prd_id as id from p_prd p, p_prd_attr pa where p.prd_id=pa.prd_id and pa.attr_id=109 and p.park_id="
        + park_id + " and p.prd_cd='" + code + "'";

    String value = db.executeQuery(query, "id", 0);

    return value;
  }

  public static String getLastKOAStartDate() {
    return getValueFromQAAutomationTable("last_koa_date");
  }

  public static String getFacilityName(String facilityID, String schema) {
    db.resetSchema(schema);
    String query = "select name from d_loc where id=" + facilityID;
    return db.executeQuery(query, "name", 0).trim();
  }

  public static String getFacilityNameFromRidb(String facilityID,
                                               String schema) {
    db.resetSchema(schema);
    String query = "select facilityname from facility where facilityid="
        + facilityID;
    return db.executeQuery(query, "facilityname", 0).trim();
  }

  public static String getRecreationAreaName(String recreationAreaID,
                                             String ridbSchema) {
    db.resetSchema(ridbSchema);
    String query = "select recareaname from recarea where recareaid="
        + recreationAreaID;
    return db.executeQuery(query, "recareaname", 0).trim();
  }

  public static String getRecreationAreaID(String recreationAreaName,
                                           String ridbSchema) {
    db.resetSchema(ridbSchema);
    String query = "select recareaid from recarea where recareaname='"
        + recreationAreaName + "'";
    return db.executeQuery(query, "recareaid", 0).trim();
  }

  public static String getStateByParkId(String parkId, String schema) {
    db.resetSchema(schema);
    String query = "SELECT STATE FROM D_LOC WHERE ID=" + parkId;
    return db.executeQuery(query, "STATE", 0);
  }

  public static String getAgencyName(String agencyCode, String schema) {
    db.resetSchema(schema);
    String query = "select * from d_loc where id = " + agencyCode;
    return db.executeQuery(query, "DSCR", 0);
  }

  public static String getFacilityID(String facilityName, String schema) {
    db.resetSchema(schema);
    String query = "select ID from d_loc where UPPER(name)='"
        + facilityName.toUpperCase() + "'";
    return db.executeQuery(query, "ID", 0);
  }

  public static String getSiteID(String siteCode, String schema) {
    db.resetSchema(schema);
    String query = "select PRD_ID from P_PRD where ACTIVE_IND=1 and PRD_CD = '"
        + siteCode + "'";
    return db.executeQuery(query, "PRD_ID", 0);
  }

  public static String getSiteNum(String siteId, String schema) {
    db.resetSchema(schema);
    String query = "select prd_cd from p_prd where prd_id=" + siteId;
    return db.executeQuery(query, "prd_cd", 0);
  }

  public static String getSiteName(String siteId, String schema) {
    db.resetSchema(schema);
    String query = "select prd_name from p_prd where prd_id=" + siteId;
    return db.executeQuery(query, "prd_name", 0);
  }

  public static String getSiteId(String siteName, String schema) {
    db.resetSchema(schema);
    String query = "select prd_id from p_prd where prd_name='" + siteName
        + "'";
    String siteId = db.executeQuery(query, "prd_id", 0);

    return siteId;
  }

  public static String getSlipID(String slipCode, String schema) {
    db.resetSchema(schema);
    String query = "select PRD_ID from P_PRD where ACTIVE_IND=1 and PRD_CD = '"
        + slipCode + "'";
    return db.executeQuery(query, "PRD_ID", 0);
  }

  public static String getSiteType(String siteId, String schema) {
    db.resetSchema(schema);
    String query = "select ppg.prd_grp_name from p_prd pp, p_prd_grp ppg where pp.prd_id = "
        + siteId + " and ppg.prd_grp_id = pp.prd_grp_id";
    return db.executeQuery(query, "prd_grp_name", 0);
  }

  public static int getSiteTypeID(String siteType, String schema) {
    db.resetSchema(schema);

    String query = "select prd_grp_id as id from p_prd_grp where UPPER(prd_grp_name) = '"
        + siteType.toUpperCase() + "'";
    String id = db.executeQuery(query, "id", 0);

    return Integer.parseInt(id);
  }

  public static String constructSchemaName(String contract) {
    String env = TestProperty.getProperty("target_env");
    String prefix = TestProperty.getProperty(env + ".db.schema.prefix");

    return prefix + contract;
  }

  public static String getTypeOfUse(String siteId, String schema) {
    db.resetSchema(schema);
    String query = "select UNIT_OF_STAY_TYPE_ID from p_prd where prd_id="
        + siteId;
    String siteTypeOfUse = db
        .executeQuery(query, "UNIT_OF_STAY_TYPE_ID", 0);
    if (siteTypeOfUse.equals("1")) {
      siteTypeOfUse = "OverNight";
    } else if (siteTypeOfUse.equals("2")) {
      siteTypeOfUse = "Day";
    } else if (siteTypeOfUse.equals("3")) {
      siteTypeOfUse = "Hour";
    } else if (siteTypeOfUse.equals("4")) {
      siteTypeOfUse = "None";
    } else {
      throw new ErrorOnDataException(
          "Can't find 'type of use' information.");
    }

    return siteTypeOfUse;
  }

  public static boolean educationVerifiable(String schema, String eduName) {
    db.resetSchema(schema);
    String query = "select verifiable_ind from d_education_type where name='"
        + eduName + "'";
    int verifiable_ind = Integer.parseInt(db.executeQuery(query,
        "verifiable_ind", 0));
    if (verifiable_ind == 0) { // the system will not verify, so it can be
      // manually verified
      return true;
    } else if (verifiable_ind == 1) { // the system will automatically
      // verify, so it can not be manually
      // verified. If the system verify
      // failed, it has status "Active"
      return false;
    } else {
      throw new ItemNotFoundException("Unknown verifiable_ind code "
          + verifiable_ind);
    }

  }

  /**
   * Retrieve login user's full name
   *
   * @param login
   * @return - in format lastname,firstname
   */
  public static String getLoginUserName(String login) {
    String env = TestProperty.getProperty("target_env");
    String schema = TestProperty.getProperty(env + ".global.schema");
    String query = "select first_name,last_name from d_user_auth where login='"
        + login + "'";
    db.resetSchema(schema);
    String[] name = db.executeQuery(query, new String[]{"first_name",
        "last_name"}, 0);
    String currentUser = name[1] + "," + name[0]; // lastname,firstname
    return currentUser;
  }

  public static String getLoginUserID(String schema, String loginUserName) {
    String query = "select id from d_user_auth where login = '"
        + loginUserName + "'";
    db.resetSchema(schema);

    return db.executeQuery(query, "id", 0);
  }

  public static int getAdmissionTypeID(String schema, String type) {
    String query = "select id from p_admission_type where name='" + type
        + "'";
    db.resetSchema(schema);

    String id = db.executeQuery(query, "id", 0);

    return Integer.parseInt(id);
  }

  /**
   * Record test case finish information to the test case execution record in
   * TEST_EXECUTION_DETAILS table
   *
   * @param caseID
   * @param runningID
   * @param result
   * @param errorMsg
   * @param duration
   */
  public static void recordTestcaseEnd(int caseID, String runningID,
                                       int result, String errorMsg, int duration, String session_id) {
    int status = (result == TestApiConstants.RESULT_HIBERNATED ? TestApiConstants.RESULT_NA
        : TestConstants.TESTCASE_FINISHED);
    duration += getTestCaseDuration(caseID, runningID); // for multiple
    // parts test cases

    recordTestExecutionDetails(caseID, runningID, result, errorMsg,
        duration, null, DateFunctions.getLongTimeStamp(), status, -1,
        null, "sysdate()", session_id);
  }

  /**
   * Record the EWeb session IDs to AWO_SESSIONS and TEST_EXECUTION_DETAILS
   * tables
   *
   * @param caseID
   * @param executionID
   * @param session_ids
   * @param new_session
   */
  public static String recordSessionID(int caseID, String executionID,
                                       String session_ids, String new_session, String awo_version, String contract,
                                       String bs_version, String test_result, String test_ind) {
    db.resetDefaultDB();

    // update sysdate to sysdate() if db vendor is mysql
    // String
    // query="insert into awo_sessions (case_id,case_name,machine,session_id,create_date ) values ("+caseID+
    // ",'"+TestProperty.getProperty("fullCaseName")+"','"+SysInfo.getHostName()+"("+SysInfo.getHostIP()+")','"+new_session+"',sysdate())";
    // Change SysInfo.getIEVersion() if the browser is not IE

    String value = null;
    String query = "insert into awo_sessions (case_id,case_name,machine,"
        + "session_id,awo_version, contract, bs_version, test_result, create_date, test_ind ) values "
        + "("
        + caseID
        + ",'"
        + TestProperty.getProperty("fullCaseName")
        + "','"
        + SysInfo.getHostName()
        + "("
        + SysInfo.getHostIP()
        + ")','"
        + new_session
        + "','"
        + awo_version
        + "','"
        + contract
        + "','"
        + "IE"
        + SysInfo.getIEVersion()
        + "','"
        + test_result
        + "',sysdate(),"
        + test_ind + ")";
    try {
      int code = db.executeUpdate(query);
      value = getTestSessionId();
//			logger.info("" + value);
      if (code < 1) {
        logger.warn("Inserted nothing: " + query);
      }
    } catch (Exception e) {
      logger.warn(e);
    }

    return value;

  }

  public static int getTestCaseDuration(int caseID, String runningID) {
    if (caseID > 0 && !StringUtil.isEmpty(runningID)
        && runningID.matches("\\d+")) {
      db.resetDefaultDB();
      String query = "select duration from test_execution_details where case_id="
          + caseID + " and execution_id='" + runningID + "'";

      String duration = db.executeQuery(query, "duration", 0);
      if (duration == null || duration.equalsIgnoreCase("null")) {
        return 0;
      } else {
        return Integer.parseInt(duration);
      }
    } else {
      return 0;
    }

  }

  /**
   * Record test case starting information to the test case execution record
   * in TEST_EXECUTION_DETAILS table
   *
   * @param caseID
   * @param runningID
   * @param tool
   * @param runner
   */
  public static void recordTestcaseStart(int caseID, String runningID,
                                         int tool, String runner, String sessionsid) {
    recordTestExecutionDetails(caseID, runningID, -1, null, -1,
        DateFunctions.getLongTimeStamp(), null,
        TestConstants.TESTCASE_RUNNING, tool, runner, null, sessionsid);
  }

  /**
   * Upage a test case execution record in TEST_EXECUTION_DETAILS table
   *
   * @param caseID
   * @param runningID
   * @param result
   * @param errorMsg
   * @param duration
   * @param start_time
   * @param end_time
   * @param status
   * @param tool
   * @param runner
   */
  public static void recordTestExecutionDetails(int caseID, String runningID,
                                                int result, String errorMsg, int duration, String start_time,
                                                String end_time, int status, int tool, String runner,
                                                String update_date, String session_id) {
    if (caseID > 0 && !StringUtil.isEmpty(runningID)
        && runningID.matches("\\d+")) {
      db.resetDefaultDB();
      // if(StringUtil.isEmpty(session_id)){
      // if (!StringUtil.isEmpty(errorMsg)) {
      // errorMsg = errorMsg.length() > 1024 ? errorMsg.substring(0,
      // 1024) : errorMsg;
      // errorMsg = errorMsg.replaceAll("'", "''");
      // }
      // String build = TestProperty.getProperty(
      // TestProperty.getProperty("product") + ".build", "");
      // String query = "update test_execution_details set "
      // + "status="
      // + status
      // + (result > 0 ? ", result=" + result : "")
      // + (StringUtil.isEmpty(errorMsg) ? "" : ", exception='"
      // + errorMsg + "'")
      // + (StringUtil.isEmpty(start_time) ? "" : ", start_time='"
      // + start_time + "'")
      // + (StringUtil.isEmpty(end_time) ? "" : ", end_time='"
      // + end_time + "'")
      // + (duration > 0 ? ", duration=" + duration : "")
      // + (StringUtil.isEmpty(runner) ? "" : ", test_runner='"
      // + runner + "'")
      // + (tool > 0 ? ", tool=" + tool : "")
      // + (StringUtil.isEmpty(update_date) ? "" : ", update_date="
      // + update_date )
      // + (StringUtil.isEmpty(session_id) ? "" : ", session_id='"
      // + session_id + "'")
      // + (StringUtil.isEmpty(build) ? "" : ", build='" + build
      // + "'") + " where case_id=" + caseID
      // + " and execution_id='" + runningID + "' and status!="
      // + TestConstants.TESTCASE_FINISHED;
      // try {
      // int code = db.executeUpdate(query);
      //
      // if (code < 1) {
      // logger.warn("Updated nothing: " + query);
      // }
      // } catch (Exception e) {
      // logger.warn(e);
      // }
      // }
      //
      // else{
      if (!StringUtil.isEmpty(errorMsg)) {
        errorMsg = errorMsg.length() > 1024 ? errorMsg.substring(0,
            1024) : errorMsg;
        errorMsg = errorMsg.replaceAll("'", "''");
      }
      String build = TestProperty.getProperty(
          TestProperty.getProperty("product") + ".build", "");
      String query = "update test_execution_details set "
          + "status="
          + status
          + (result > 0 ? ", result=" + result : "")
          + (StringUtil.isEmpty(errorMsg) ? "" : ", exception='"
          + errorMsg + "'")
          + (StringUtil.isEmpty(start_time) ? "" : ", start_time='"
          + start_time + "'")
          + (StringUtil.isEmpty(end_time) ? "" : ", end_time='"
          + end_time + "'")
          + (duration > 0 ? ", duration=" + duration : "")
          + (StringUtil.isEmpty(runner) ? "" : ", test_runner='"
          + runner + "'")
          + (tool > 0 ? ", tool=" + tool : "")
          + (StringUtil.isEmpty(update_date) ? "" : ", update_date="
          + update_date)
          + (StringUtil.isEmpty(build) ? "" : ", build='" + build
          + "'") + " where case_id=" + caseID
          + " and execution_id='" + runningID + "' and status!="
          + TestConstants.TESTCASE_FINISHED + " and session_id='"
          + session_id + "'";
      try {
        int code = db.executeUpdate(query);

        if (code < 1) {
          logger.warn("Updated nothing: " + query);
        }
      } catch (Exception e) {
        logger.warn(e);
      }
    }
    // }
  }

  public static int getTotalPermitInventory(String park, String entrance,
                                            String date, String schema) {
    db.resetSchema(schema);
    date = DateFunctions.formatDate(date, "yyyy-MM-dd");
    String[] tokens = entrance.split("-");
    String prd_cd = tokens[0].trim();
    String prd_name = tokens[1].trim();
    String query = "select ipi.available as available from i_permit_inv ipi, p_prd pp, d_loc dl where pp.prd_cd='"
        + prd_cd
        + "' and pp.prd_name='"
        + prd_name
        + "' and pp.prd_id=ipi.entrance_id and start_date=to_date('"
        + date
        + "','yyyy-mm-dd') and upper(dl.name)='"
        + park.toUpperCase() + "' and dl.id=ipi.loc_id";
    String num = db.executeQuery(query, "available", 0);

    return Integer.parseInt(num);
  }

  public static int getTotalTourInventory(String park, String tour,
                                          String date, String schema) {
    db.resetSchema(schema);
    date = DateFunctions.formatDate(date, "yyyy-MM-dd");
    String from = date + " 00:00:00";
    String to = date + " 23:59:59";
    String query = "select sum(tot_avail) as tot_avail from p_prd pp, i_tour_inv iti, d_loc dl where iti.loc_id=dl.id and upper(dl.name)=upper('"
        + park
        + "') and iti.prd_id=pp.prd_id and upper(prd_name)=upper('"
        + tour
        + "') and date_time >=to_date('"
        + from
        + "','yyyy-mm-dd hh24:mi:ss') and date_time <=to_date('"
        + to
        + "','yyyy-mm-dd hh24:mi:ss')";
    String value = db.executeQuery(query, "tot_avail", 0);
    int total = (value == null ? -1 : Integer.parseInt(value));

    return total;

  }

  public static List<String> getCooperatorLogins(String schema, int facilityID) {
    db.resetSchema(schema);
    String query = "select cc.login_name as login from c_cust cc, c_cust_loc ccl, d_loc dl where ccl.cust_id=cc.cust_id and dl.id=ccl.loc_id and dl.cd like '%|"
        + facilityID + "|%' and cc.active_ind=1 order by cc.cust_id";

    List<String> logins = db.executeQuery(query, "login");
    return logins;
  }

  public static String getNextCooperatorLogin(String schema, int facilityID,
                                              boolean forLottery) {
    List<String> logins = getCooperatorLogins(schema, facilityID);
    int seq = getEmailSequence();

    int index = seq % (logins.size() / 2);
    if (forLottery) { // odd index for lottery
      index = index * 2 + 1;
    } else { // even index for permit
      index = index * 2;
    }
    return logins.get(index);
  }

  public static String getNextCooperatorLogin(String schema,
                                              String facilityName, boolean forLottery) {
    String id = DataBaseFunctions.getFacilityID(facilityName, schema);

    return getNextCooperatorLogin(schema, Integer.parseInt(id), forLottery);
  }

  public static String getCooperatorLogin(String schema, String cust_id) {
    db.resetSchema(schema);
    String query = "select login_name as login from c_cust where cust_id="
        + cust_id;

    String login = db.executeQuery(query, "login", 0);
    return login;
  }

  public static int countCustomerPhoneNumber(String schema, String phone) {
    db.resetSchema(schema);
    // query whether the telephone number have existed
    String sql = "select count(*) as valCount from C_CUST_PHONE where val='"
        + phone + "'";
    String countStr = db.executeQuery(sql, "valCount", 0);

    if (StringUtil.isEmpty(countStr)) {
      return 0;
    } else {
      return Integer.parseInt(countStr);
    }
  }

  public static String getAdminContractRoleLoc(String username, String schema) {
    db.resetSchema(schema);
    db.connect();
    String query = "select dl.name as locname, xr.name as rolename from d_user_auth dua, d_loc dl, x_role xr, x_user_role_loc xurl where "
        + "dua.login='"
        + username
        + "' "
        + "and dua.id=xurl.user_id "
        + "and xr.name='Administrator' "
        + "and xr.id=xurl.role_id "
        + "and dl.id=xurl.loc_id " + "and dl.id=1";

    String[] colNames = {"locname", "rolename"};
    String[] rolloc = db.executeQuery(query, colNames, 0);
    if (rolloc.length > 1)
      return rolloc[1] + "/" + rolloc[0];
    else
      return null;
  }

  public static int getLoadDispatch() {
    db.resetDefaultDB();
    db.connect();
    // String query =
    // "select load_dispatch.nextval as value from dual";//oracle
    String query = "select seq('load_dispatch') as value";

    String value = db.executeQuery(query, "value", 0);
    return Integer.parseInt(value);
  }

  public static String[] getPrivilegeNumber(String orderNum, String schema) {
    db.resetSchema(schema);
    String query = "select opi.priv_number from o_order oo, o_ord_item ooi, o_priv_inst opi where oo.ord_num='"
        + orderNum
        + "' and oo.id=ooi.ord_id and ooi.id=opi.orig_ord_item_id";
    List<String> priv_nums = db.executeQuery(query, "priv_number");

    return priv_nums.toArray(new String[0]);
  }

  /**
   * get translatable label value identifier by label key
   *
   * @param key
   * @return
   */
  public static String getTranslatableLabelValue(String key) {
    return getTranslatableLabelValue(key, "CNTR");
  }

  public static String getTranslatableLabelValue(String key, String contract) {
    // db.resetSchema(getSchemaName("common",
    // TestProperty.getProperty("target_env")));
    // String sql =
    // "select LABEL_VALUE from All_X_TRANSLATION where LABEL_KEY = '"
    // + key + "' and contract='"+contract+"'";
    db.resetSchema(getSchemaName(contract,
        TestProperty.getProperty("target_env"))); // Lesley[20140114]:
    // update due to no
    // such table in
    // Live_Common
    String sql = "select LABEL_VALUE from X_TRANSLATION where LABEL_KEY = '"
        + key + "'";

    logger.info("Execute query: " + sql);
    List<String> result = db.executeQuery(sql, "LABEL_VALUE");

    if (result.size() < 1) {
      if (!contract.equals("CNTR")) {
        return getTranslatableLabelValue(key, "CNTR");
      } else {
        throw new ErrorOnDataException(
            "Can't found any translation record by label key - "
                + key);
      }
    }

    return result.get(0);
  }

  public static String getTranslatableLabelValue(String schema, String key,
                                                 String locCode) {
    db.resetSchema(schema);
    String sql = "select LABEL_VALUE from X_TRANSLATION where LABEL_KEY = '"
        + key
        + "' and LOC_CODE = (select cd from d_loc where id="
        + locCode + ")";

    logger.info("Execute query: " + sql);
    List<String> result = db.executeQuery(sql, "LABEL_VALUE");

    if (result.size() < 1)
      throw new ErrorOnDataException(
          "Can't found any translation record by label key - " + key
              + " and loc code - " + locCode);

    return result.get(0);
  }

  public static String getLicenseFiscalYearTranslatedLabel(String contract) {
    return getTranslatableLabelValue("translatable.licensefiscalyear",
        contract);
  }

  public static String getStateFeeTranslatedLabel(String contract) {
    return getTranslatableLabelValue("translatable.statefee", contract);
  }

  public static String getVendorFeeTranslatedLabel(String contract) {
    return getTranslatableLabelValue("translatable.vendor", contract);
  }

  public static String getStateTransFeeTranslatedLabel(String contract) {
    return getTranslatableLabelValue("translatable.statetransfee", contract);
  }

  public static String getStateVendorFeeTranslatedLabel(String contract) {
    return getTranslatableLabelValue("translatable.statevendorfee",
        contract);
  }

  public static String getTransFeeTranslatedLabel(String contract) {
    return getTranslatableLabelValue("translatable.transactionfee",
        contract);
  }

  public static String getHoldingFeeTranslatedLabel(String contract) {
    return getTranslatableLabelValue("translatable.holdingfee", contract);
  }

  public static String getLatestAvailableTourDate(String schema,
                                                  String parkName, String tourName) {
    String today = DateFunctions.getToday("yyyy-MM-dd");
    return getLatestAvailableTourDate(schema, parkName, tourName, today, 1);
  }

  public static String getLatestAvailableTourDate(String schema,
                                                  String parkName, String tourName, String startDate, int num) {
    db.resetSchema(schema);
    startDate = DateFunctions.formatDate(startDate, "yyyy-MM-dd");
    String query = "select to_char( min(iti.date_time), 'YYYY-MM-DD') as adate from i_tour_inv iti, d_loc dl, p_prd pp where UPPER(dl.name)='"
        + parkName.toUpperCase()
        + "' and dl.id=iti.loc_id and iti.prd_id=pp.prd_id and UPPER(pp.prd_name)='"
        + tourName.toUpperCase()
        + "' and status=1 and date_time>=to_date('"
        + startDate
        + "','YYYY-MM-DD') and iti.tot_avail>=" + num;
    String aDate = db.executeQuery(query, "adate", 0);

    if (StringUtil.isEmpty(aDate)) {
      throw new ItemNotFoundException(
          "No tour inventory found for tour '" + tourName
              + "' after " + startDate);
    } else {
      logger.info("Tour '" + tourName + "' has at least " + num
          + " available inventory on " + aDate);
    }

    return aDate;

  }

  // public static Properties getTranslations(String schema, String
  // contract_abbr) {
  // db.resetSchema(schema);
  // String
  // query="select label_key, label_value from all_x_translation where contract=UPPER('"+contract_abbr+"') or contract='CNTR'";
  // List<String[]> list=db.executeQuery(query, new
  // String[]{"label_key","label_value"});
  //
  // Properties trans=new Properties();
  // for(String[] ss:list) {
  // trans.setProperty(ss[0], ss[1]);
  // }
  //
  // return trans;
  //
  // }

  // public static Hashtable<String,Properties> getTranslations(String schema)
  // {
  // db.resetSchema(schema);
  // String
  // query="select contract,label_key, label_value from all_x_translation";
  // List<String[]> list=db.executeQuery(query, new
  // String[]{"contract","label_key","label_value"});
  //
  // Properties trans=new Properties();
  // for(String[] ss:list) {
  // trans.setProperty(ss[0], ss[1]);
  // }
  //
  // return trans;
  //
  // }

  public static String getTranslations(String schema, String label_key) {
    db.resetSchema(schema);
    String query = "select label_value from x_translation where label_key='"
        + label_key + "'";
    String value = db.executeQuery(query, "label_value", 0);

    return value;

  }

  /**
   * This method was used to update result for record with id in data table
   *
   * @param tableName
   * @param id
   * @param env
   * @param result
   */
  public static void updateResultForDataTable(String tableName, String id,
                                              String env, int result) {
    logger.info("Update table " + tableName + " for id " + id + " " + env
        + "_result to " + result);

    db.resetDefaultDB();
    String query = "update " + tableName + " set " + env + "_result="
        + result + " where id=" + id;
    db.executeUpdate(query);
  }

  public static void updateValueForDataTable(String tableName, String id,
                                             String env, String value) {
    logger.info("Update table " + tableName + " for id " + id + " " + env
        + "_value to " + value);

    db.resetDefaultDB();
    String query = "update " + tableName + " set " + env + "_value='"
        + value + "' where id=" + id;
    db.executeUpdate(query);
  }

  public static String getTCsIDinSpiraTeam(int script_id) {
    db.resetDefaultDB();
    String query = "select tc_number from spira_auto where script_id="
        + script_id;
    try {
      return db.executeQuery(query, "tc_number", 0);
    } catch (Exception e) {
      AutomationLogger.getInstance().warn(
          "Failed to get spira tc number due to " + e);
      return null;
    }
  }

  public static void insertDataIntoDB(String tableName, String tableColNames,
                                      List<String[]> values) {
    db.resetDefaultDB();
    for (int i = 0; i < values.size(); i++) {
      String[] value = values.get(i);
      String colValues = StringUtil.EMPTY;
      for (int j = 0; j < value.length; j++) {
        colValues += "'" + value[j] + "'"
            + (j == value.length - 1 ? "" : ",");
      }
      String sql = "insert into " + tableName + " (" + tableColNames
          + ") values (" + colValues + ")";
      db.executeUpdate(sql);
    }
  }

  public static void updateRecordSession(String session_id, int test_result) {
    db.resetDefaultDB();
    String query = "update awo_sessions set test_result =" + test_result
        + "," + " update_date=sysdate()" + " where session_id='"
        + session_id + "'";
    db.executeUpdate(query);
  }

  // public static void createTestExecutionDetailRecord(List<String>
  // caseIDs,String runningId) {
  // createTestExecutionDetailRecords(caseIDs.toArray(new
  // String[0]),runningId);
  // }
  //
  // public static void createTestExecutionDetailRecord(String caseIDs,String
  // runningId) {
  // String[] ids=caseIDs.split(",");
  // createTestExecutionDetailRecords(ids,runningId);
  // }
  //
  public static void createTestExecutionDetailRecord(int caseID,
                                                     String runningId, String tool, String test_ind, String session_id) {
    db.resetDefaultDB();
    String querys = "insert into test_execution_details (case_id,execution_id,tool,status,result, test_ind, session_id) values "
        + "("
        + caseID
        + ",'"
        + runningId
        + "',"
        + tool
        + ","
        + TestApiConstants.TESTCASE_PENDING
        + ","
        + TestApiConstants.RESULT_NA
        + ","
        + test_ind
        + ",'"
        + session_id + "')";
    int result = db.executeUpdate(querys);
    if (result < 1) {
      logger.warn("Failed to record cases in TEST_EXECUTION_DETAILS table!");
    }
  }

  public static void recordTestRefData(String test_session_id,
                                       String operation, String object, String value, String comment) {
    db.resetDefaultDB();
    String query = "insert into test_ref_data(test_session_id, operation, object,value,comment,create_date) values "
        + "("
        + test_session_id
        + ",'"
        + operation
        + "','"
        + object
        + "','" + value + "',"
        // TO DO branch if comment is null
        + "'" + comment + "'" + "," + "sysdate()" + ")";
    db.executeUpdate(query);
  }

  public static void recordTestRefData(String test_session_id,
                                       String operation, String object, String value) {
    recordTestRefData(test_session_id, operation, object, value, null);
  }

  public static String getTestSessionId() {
    db.resetDefaultDB();
    String query = "select max(id) as id from awo_sessions";
    String value = db.executeQuery(query, "id", 0);
    return value;
  }

  // //need fix
  // public static void createTestExecutionDetailRecords(String[] ids,String
  // runningId) {
  // db.resetDefaultDB();
  // String[] querys=new String[ids.length];
  // for(int i=0;i<ids.length;i++) {
  // querys[i]="insert into test_execution_details (case_id,execution_id,status,result) values ("+ids[i]+",'"+runningId+"',"+TestConstants.TESTCASE_PENDING+","+TestConstants.RESULT_NA+")";
  // }
  // db.connect();
  // db.executeBatch(querys);
  // db.disconnect();
  //
  // }

  /**
   * Update the test case result in database with the given result
   *
   * @param env
   * @param runningId
   * @param caseId
   * @param result
   * @return
   */
  public static boolean updateCaseResult(String env, String runningId,
                                         int caseId, int result) {
    db.resetDefaultDB();
    String query = "update test_cases set " + env + "_result=" + result
        + " where " + env + "_running_id='" + runningId
        + "' and id='" + caseId + "'";
    int count = db.executeUpdate(query);
    if (count < 1) {
      logger.warn("Failed to execute query: " + query);
    }
    return count > 0;
  }

//	/**
//	 * 
//	 * Update the test case status in database with the given status
//	 * 
//	 * @param env
//	 * @param runningId
//	 * @param caseName
//	 * @param status
//	 * @return
//	 * @throws SQLException
//	 * @throws ClassNotFoundException
//	 */
//	public static boolean updateStatus(String env, String runningId,
//			String caseName, int status) throws SQLException,
//			ClassNotFoundException {
//		db.resetDefaultDB();
//		String query = "update test_cases set " + env + "_status=" + status
//				+ " where " + env + "_running_id='" + runningId
//				+ "' and casename='" + caseName + "'";
//		int count = db.executeUpdate(query);
//		if (count < 1) {
//			logger.warn("Failed to execute query: " + query);
//		}
//
//		return count > 0;
//
//	}

  public static void updateCaseRunningId(String env, String runningId,
                                         int caseId) {
    db.resetDefaultDB();
    String query = "update test_cases set " + env + "_running_id="
        + runningId + "," + env + "_status=1" + " where id ="
        + caseId + " and " + env + "_active " + "=1";
    db.executeUpdate(query);
  }

  // Copy from class Util of testdriver, no sure if it's suitable for now
  // public static void updateStatus(String env,String runningId,List<String>
  // ids,int status){
  // db.resetDefaultDB();
  // if(ids.size()<Integer.parseInt(TestProperty.getProperty("size_limitation"))){
  // String idStr = Util.listToString(ids, false, ",");
  // String
  // query="update test_cases set "+env+"_running_id="+runningId+","+env+"_status="+status+" where id in ("+idStr+")";
  // db.executeUpdate(query);
  // }else{
  // String[] idStrs=Util.listToStrings(ids, false,
  // ",",Integer.parseInt(TestProperty.get("size_limitation")));
  // for(String idStr:idStrs) {
  // String
  // query="update test_cases set "+env+"_running_id="+runningId+","+env+"_status="+status+" where id in ("+idStr+")";
  // db.executeUpdate(query);
  // }
  // }
  // }

}
