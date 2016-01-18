package com.active.qa.automation.web.testrunner;

import com.active.qa.automation.web.testrunner.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by tchen on 1/18/2016.
 */
public class TestMonitor implements TestConstants, Executable {
  public static Logger logger = Logger.getLogger(TestMonitor.class);
  private static final int TEST_CASE_TIMEOUT = 5;
  private Hashtable<String, TestSuite> testSets = new Hashtable<String, TestSuite>();
  private int timeout = -1;
  private boolean buildInProgress = false;
  private ProducerTool producer;
  private ConsumerTool consumer;
  private int errorCount = 0;
  private boolean connect_error = false;

  /**
   * buildMap is used to record the last build number for each QA environment.
   * This record was maintained in case it is failed to retrieve the latest build number somehow, the last build number will be used.
   */
  private Properties buildMap;
  private static final String CASHED_BUILD = Util.getProjectPath() + File.separator + "awo_build.properties";

  public TestMonitor() {
    TestProperty.load();
    buildMap = TestProperty.load(CASHED_BUILD);
    refreshBuildMap();

  }

  private void refreshBuildMap() {
    Properties newBuildMap = Util.getAWOBuildMap();

    Set<Object> keys = newBuildMap.keySet();

    for (Object k : keys) {
      String value = (String) newBuildMap.get(k);
      if (value != null && value.length() > 0) {
        buildMap.put((String) k, value);
      }
    }

    TestProperty.save(buildMap, CASHED_BUILD);
  }

  private List<String> getUniqeBuilds() {
    refreshBuildMap();
    List<String> builds = new ArrayList<String>();

    Set<Object> envs = buildMap.keySet();
    for (Object env : envs) {
      String build = (String) buildMap.get(env);
      if (!builds.contains(build)) {
        builds.add(build);
      }
    }

    return builds;
  }

  private List<String> getUniqeMajorBuilds(String[] envs) {
    refreshBuildMap();
    List<String> builds = new ArrayList<String>();

    for (Object env : envs) {
      if (env.equals("")) {
        String defaultBuild = TestProperty.get("functest4.awo.default");
        logger.info("Using default build major#" + defaultBuild);
        if (!builds.contains(defaultBuild)) {
          builds.add(defaultBuild);
        }
      } else {
        String build = (String) buildMap.get(env);
        if (build != null && build.length() > 7) {
          String majorBuild = build.substring(0, 7).replaceAll("\\.", "");//30500
          if (!builds.contains(majorBuild)) {
            builds.add(majorBuild);
          }
        }
      }
    }

    return builds;
  }

  private String getBuild(String env) {
    refreshBuildMap();

    return buildMap.getProperty(env);
  }

  public void execute(String[] args) {
    String errorMsg = "";
    TestProperty.load();
    TestProperty.resetLogfile("TestMonitor");
    ConnectionTool.setClientID("TestMonitor");

    try {
      try {
        ConnectionTool.connect();
      } catch (InvalidClientIDException e) {
        throw e;
      } catch (JMSException e) {
        logger.warn(e);
        connect_error = true;
      }

      consumer = ConsumerTool.getInstance();
      producer = ProducerTool.getInstance();
      ConsumerTool.logger = logger;
      ProducerTool.logger = logger;

      //Automatically recovery test suites which are not finished yet.
      autoRecovery();

      while (true) {

        String queue = TestProperty.get("mq.monitor.queue");
        Message msg = null;
        try {
          if (connect_error) {
            ConnectionTool.connect();
            connect_error = false;
            logger.info("Connected!");
          }

          msg = consumer.consumeMessage(queue, timeout);
          errorCount = 0;
        } catch (InvalidClientIDException e) {
          throw e;
        } catch (JMSException e) {
          if (!connect_error) {
            connect_error = true;
            logger.error(e);
            logger.info("Will retry connection in every " + Util.getRetrySleep() + " seconds");

          }
          ConnectionTool.close();

          Util.sleep(Util.getRetrySleep() * 1000);
          errorCount++;
          if (errorCount % 30 == 0) {
            String subject = "Warning: Test Monitor has connection error!";
            String text = "Test Monitor has failed to connect to message queue for " + Util.getRetrySleep() * errorCount + " seconds.\n\n";
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(byteOut));
            text += byteOut.toString();
            Email.sendErrorMessageToMaster(subject, text);
          }
          continue;
        }

        if (msg != null && msg instanceof TextMessage) {
          processMessage((TextMessage) msg);

        } else if (msg != null) {
          logger.error("Not a TextMessage, ignored: " + msg.toString());
        }

        if (testSets.size() > 0)
          checkTestSetStatus();

        if (testSets.size() < 1)
          timeout = -1;
      }

    } catch (Exception e) {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      e.printStackTrace(new PrintStream(byteOut));
      errorMsg += byteOut.toString();
      logger.fatal(errorMsg);

      if (!(e instanceof InvalidClientIDException)) {
        try {
          ConnectionTool.close();

          if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            Runtime rt = Runtime.getRuntime();
            rt.exec("cmd /k start java -jar X:/TestDriver/testdriver.jar com.active.qa.testdriver.TestMonitor");
          } else {
            //ToDo: implement linux auto start
          }
        } catch (IOException e1) {
          e1.printStackTrace();
          logger.fatal("Failed to restart test monitor automatically");
        }
      }
    } finally {
      ConnectionTool.close();
      String subject = "Warning: Test Monitor at " + Util.getHostName() + "(" + Util.getHostIP() + ") is down!";
      Email.sendErrorMessageToMaster(subject, errorMsg);
      System.exit(0);
    }
  }

  private void processHeadMsg(TextMessage msg) throws JMSException {
    String mailto = msg.getStringProperty("email");
    String testSet = msg.getStringProperty("testSet");
    String env = msg.getStringProperty("env");
    String runningId = msg.getStringProperty("runningId");
    int total = Integer.parseInt(msg.getStringProperty("total"));
    //for normal suite, content should be caseList;multiList
    //for support script suite, content should be scriptinfo(scriptid;tablename;ids)
    String content = msg.getText();
    String[] listArray = content.split(":");
    String[] ids = null;
    if (listArray[0].contains("|"))
      ids = listArray[0].split("\\|");
    else
      ids = listArray[0].split(",");
    String[] multi = listArray.length > 1 ? listArray[1].split(",") : new String[0];

    boolean failedOnly = msg.getBooleanProperty("failedOnly");
    int repeat = Integer.parseInt(msg.getStringProperty("repeat"));
    String runners = msg.getStringProperty("runners");

    addNewTestSuite(env, runningId, mailto, testSet, runners, total, ids, multi, repeat, failedOnly);
  }

  private void autoRecovery() throws SQLException, JMSException {
    logger.info("Automatic recovery in progress...");
    DataBase db = DataBase.getInstance();
    String query = "select id from test_execution where status_code !=3 order by id desc";
    List<String> ids = db.executeQuery(query, "id");
    int size = ids.size();
    logger.info("Found " + size + " non-finished test suites.");
    if (size > 10) { //only auto recover the first 10 if there are too many
      size = 10;
    }

    for (int i = 0; i < size; i++) {
      recoverTestSuite(ids.get(i));
    }

  }

  private boolean recoverTestSuite(String runningId) throws SQLException, JMSException {
    DataBase db = DataBase.getInstance();
    String query = "select * from test_execution where id='" + runningId + "' and status_code !=3";
    String[] cols = new String[]{"test_suite", "qa_env", "email_to", "total", "auto_repeat", "failed_only", "status_code", "start_time"};
    List<String[]> rs = db.executeQuery(query, cols);

    if (rs == null || rs.size() < 1) {
      return false;
    } else {
      logger.info("Recoverying execution_id#" + runningId + "......");
    }
    String testsuite = rs.get(0)[0];
    if (testsuite.startsWith("supportscripts") || testsuite.startsWith("testCases.production")) {
      logger.warn("Sorry that we could not recover setup script and production sanity suite yet.");
      return false;
    }

    String env = rs.get(0)[1];
    String mailto = rs.get(0)[2];
    int total = Integer.parseInt(rs.get(0)[3]);
    int repeat = Integer.parseInt(rs.get(0)[4]);
    boolean failedOnly = Integer.parseInt(rs.get(0)[5]) == 1;
    int status = Integer.parseInt(rs.get(0)[6]);
    String runners = "ALL";//TODO check

    query = "select * from " +
        "(test_cases inner join test_execution_details " +
        "on test_cases.id=test_execution_details.case_id " +
        "and test_cases." + env + "_running_id=test_execution_details.execution_id) " +
        "where " + env + "_running_id='" + runningId + "'";

    String[] cols1 = new String[]{"id", "casename", "test_runner", "timing", "tool", "result", "multi", "status", "caseowner"};
    List<String[]> rs1 = db.executeQuery(query, cols1);
    String[] ids = new String[rs1.size()];
    String[] multi = new String[rs1.size()];
    for (int i = 0; i < rs1.size(); i++) {
      ids[i] = rs1.get(i)[0];
      String value = rs1.get(i)[6];
      if (Util.isEmpty(value))
        value = "0";
      multi[i] = value;
    }

    //for pending suite, just add new test suite;
    //for in-progress suite, add new test suite, and fill each fields with DB query result
    if (status != TESTSUITE_PENDING && status != TESTSUITE_INPROGRESS) {
      logger.warn("The test suite was not pending nor in-progress now. Discard this message.");
      return false;
    }

    addNewTestSuite(env, runningId, mailto, testsuite, runners, total, ids, multi, repeat, failedOnly);

    if (status == TESTSUITE_INPROGRESS) {
      TestSuite tr = (TestSuite) testSets.get(runningId);
      tr.fillRecoveryTestCaseInfo(rs1);

      if (tr.isDone())
        finishTestSuite(tr);
    }

    return true;
  }

  private void addNewTestSuite(String env, String runningId, String mailto, String testset, String runners, int total, String[] ids, String[] multi, int repeat, boolean failedOnly) {
    timeout = 5 * 1000 * 60;
    TestSuite tr = new TestSuite();

    tr.setMailto(mailto);
    tr.setTestSuite(testset);
    tr.setEnv(env);
    tr.setRunningID(runningId);
    if (testset.startsWith("supportscripts") || testset.startsWith("testcases.production")) { //multi is not valid for support scripts
      tr.createScriptTable(ids);
    } else {
      tr.createCaseTable(ids, multi);
    }

    tr.setFailedOnly(failedOnly);
    tr.setRepeat(repeat);
    tr.setRunners(runners);

    if (tr.getTotal() != total) {
      logger.warn("The actual test suite size " + tr.getTotal() + " is not expected " + total);
    }

    testSets.put(tr.getRunningID(), tr);
    logger.info("size=" + testSets.size());
    logger.info("Created a new test suite: mail=" + tr.getMailto() + " testsuite=" + tr.getTestSuite() + " env=" + tr.getEnv() + " runId=" + tr.getRunningID() + " total=" + tr.getTotal() + " runners=" + tr.getRunners());
  }

  private void processCleanMsg(TextMessage msg) throws JMSException, SQLException, ItemNotFoundException {
    String[] ids = msg.getText().split(",");
    for (String id : ids) {
      clean(id);
    }
    cleanupTestQueue(ids);
  }

  private void processResultMsg(TextMessage msg) throws Exception {
    String runningId = msg.getStringProperty("runningId");
    if (!testSets.containsKey(runningId)) {
      boolean recovered = recoverTestSuite(runningId);
      if (!recovered) {//dismiss
        return;
      }
    }
//		String text=msg.getText();
//		boolean setupscripts=text.startsWith("supportscripts");
    boolean setupscripts = msg.getBooleanProperty("setupscripts");
    boolean prdsanitycases = msg.getBooleanProperty("prdsanitycases");
//		int index=Integer.parseInt(msg.getStringProperty("index"));
    String caseID = msg.getStringProperty("caseID");
    String env = msg.getStringProperty("env");
    String runners = msg.getStringProperty("runners");
//		String host=msg.getStringProperty("host");
//		String ip=msg.getStringProperty("ip");
    int tool = Integer.parseInt(msg.getStringProperty("tool"));
    int result = Integer.parseInt(msg.getStringProperty("result"));
    int branchTotal = msg.getIntProperty("branchTotal");
    int branch = msg.getIntProperty("branch");
    String caseOwner = msg.getStringProperty("caseOwner");
    String ids = msg.getStringProperty("ids");//setup scripts ids
    System.out.println("production sanity test case:" + prdsanitycases);
    if (setupscripts || prdsanitycases) { // construct unique key for setup script as caseID
      String tablename = msg.getStringProperty("tablename");
      if (Util.isEmpty(tablename))
        caseID = caseID + "::";
      else
        caseID = caseID + ":" + tablename + ":" + ids;
      System.out.println("result table key:" + caseID);
    }

    TestSuite tr = (TestSuite) testSets.get(runningId);

    if (branchTotal > 1 && (result == RESULT_FAILED || branch == branchTotal - 1))//for eft test case running finished or failed
      Util.setEFTRunning(TestConstants.STATUS_FALSE);
    if (branchTotal > 1 && branch < branchTotal - 1 && result == RESULT_HIBERNATED)//for eft case ends with one branch
      submitNextBranch(runningId, caseID, env, tool, runners, branchTotal, branch);

    tr.setTestCaseResult(caseID, result, ids);
    tr.setTestCaseBranch(caseID, branch);
    tr.setTestCaseOwner(caseID, caseOwner);
    logger.info("Received a new result from " + msg.getStringProperty("host") + ". Test suite's status: passed=" + tr.getPassedNumber() + " failed=" + tr.getFailedNumber() + " notrun=" + tr.getNotRunNumber() + " text=" + ((TextMessage) msg).getText());

    if (tr.isDone()) {
      if (setupscripts || prdsanitycases)
        finishTesSuiteWithDataTable(tr);
      else
        finishTestSuite(tr);
    }
  }

  /**
   * This method only handle with eft test cases so far. For eft test cases, it should wait for 15 mins for next branch, and also run on sanity machine in turns
   *
   * @param runningId
   * @param caseId
   * @param env
   * @param tool
   * @param runners
   * @param branchTotal
   * @param branch
   * @throws Exception
   */
  private void submitNextBranch(String runningId, String caseId, String env, int tool, String runners, int branchTotal, int branch) throws Exception {
    //submit test case messages
    logger.info("Submit test case " + caseId + " with branch " + (branch + 1) + " to test runners " + runners + ", total branches:" + branchTotal);
    String eftQueue = TestProperty.get("mq.eft.queue");
    producer.connect(eftQueue);
    String caseName = Util.getCaseName(caseId);
    int toolcode = tool;//TODO how about EFT test cases which need to be run on RFT?
    int appcode = WEB;
    java.util.Calendar Cal = java.util.Calendar.getInstance();

    if (runners.equals("ALL"))
      runners = TestProperty.get("eft.runners");
    Cal.add(java.util.Calendar.MINUTE, Integer.parseInt(TestProperty.get("exetime")));

    long exetime = Cal.getTimeInMillis();
    int priority = NORMAL_PRIORITY + 1;
    String cmdStr = caseName + " env=" + env + ":cmdLine=true:runningId=" + runningId + ":testcaseId=" + caseId + ":tool=" + tool + ":branch=" + (branch + 1) + ":branchTotal=" + branchTotal;
    String[] msgProperty = {"env=" + env, "monitor=" + TestProperty.get("mq.monitor.queue"), "runningId=" + runningId, "caseID=" + caseId,
        "debug=" + TestProperty.get("debug", "false"), "draft=" + TestProperty.get("draft", "false"),
        "appcode=" + appcode, "toolcode=" + toolcode, "runners=" + runners};
    Message msg = producer.createTextMessage(cmdStr, msgProperty);
    msg.setLongProperty("exetime", exetime);
    msg.setIntProperty("branchTotal", branchTotal);
    msg.setIntProperty("branch", (branch + 1));
    producer.produceMessage(msg, priority);
    producer.disconnect();
  }

  /**
   * a new thread to finish a test suite and send email for result
   *
   * @author jdu
   */
  private class AsyncFinish extends Thread {
    private TestSuite tr;

    public AsyncFinish(TestSuite tr) {
      this.tr = tr;
    }

    @SuppressWarnings("unchecked")
    public void run() {

      try {
        tr.finish();
        logger.info("Sending mail for test suite " + tr.getTestSuite());
        sendEmail(tr);
        //process repeat
        if (tr.getRepeat() > 0 && tr.getNotRunNumber() < 1 && !tr.allPassed()) {
          //don't repeat if user stopped test suite
          Object[] data = tr.generateFailedTestcaseCMD();

          Properties prop = (Properties) data[0];
          List<String> ids = (List<String>) data[1];
          List<String> cmds = (List<String>) data[2];
          List<String> caseNames = (List<String>) data[3];
          List<String> multis = (List<String>) data[4];

          String env = prop.getProperty("env");
          String runningId = prop.getProperty("runningId");
          String testset = prop.getProperty("testset");
          String mailto = prop.getProperty("mailto");
          String runners = tr.getRunners();

          int total = ids.size();
          logger.info("TestSet " + testset + " repeat for " + tr.getRepeat() + " times.");
          logger.info("Total " + total + " test cases.");
          Util.updateStatus(env, runningId, ids, TestConstants.EXECUTION_SUBMITTED);

          addNewTestSuite(env, runningId, mailto, testset, runners, total, ids.toArray(new String[ids.size()]), multis.toArray(new String[multis.size()]), tr.getRepeat() - 1, true);

          Util.createTestExecutionRecord(testset, env, runningId, mailto, total, tr.getRepeat() - 1, true);

          //submit test case messages
          ProducerTool prd = ProducerTool.getInstance();
          String testQueue = TestProperty.get("mq.test.queue");
          String rftQueue = TestProperty.get("mq.rft.queue");
          String smQueue = TestProperty.get("mq.sm.queue");
          String eftQueue = TestProperty.get("mq.eft.queue");
          String reportQueue = TestProperty.get("mq.report.queue");

          Long exetime = Calendar.getInstance().getTimeInMillis();
          for (int i = 0; i < ids.size(); i++) {
            String aCase = caseNames.get(i);
            int toolcode = Util.getToolCode(cmds.get(i));
            int appcode = WEB;
            String runner = runners;
            int branchTotal = 0;
            if (multis.get(i) != null)
              branchTotal = Integer.parseInt(multis.get(i));
            appcode = Util.getAppCodeByCaseName(aCase, branchTotal);

            switch (appcode) {
              case REPORT:
                prd.connect(reportQueue);
                break;
              case EFT:
                prd.connect(eftQueue);
                break;
              case SM:
                prd.connect(smQueue);
                break;
              default:
                if (toolcode == RFT)
                  prd.connect(rftQueue);
                else
                  prd.connect(testQueue);
            }
            int priority = caseNames.get(i).indexOf(".regression.") >= 0 ? NORMAL_PRIORITY : SANITY_PRIORITY;

            String[] msgProperty2 = {"env=" + env, "monitor=" + TestProperty.get("mq.monitor.queue"), "runningId=" + runningId, "total=" + total,
                "debug=" + TestProperty.get("debug", "false"), "draft=" + TestProperty.get("draft", "false"),
                "appcode=" + appcode, "toolcode=" + toolcode, "runners=" + runner, "caseID=" + ids.get(i)};

            Message msg = prd.createTextMessage(cmds.get(i), msgProperty2);
            msg.setLongProperty("exetime", exetime);
            msg.setIntProperty("branchTotal", branchTotal);
            msg.setIntProperty("branch", 0); //the 1st time submit, branch value was 0
            prd.produceMessage(msg, priority);
            prd.disconnect();
            Util.createTestExecutionDetailRecord(ids.get(i), runningId, String.valueOf(toolcode));
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void finishTestSuite(TestSuite tr) throws JMSException, ItemNotFoundException, SQLException {
    testSets.remove(tr.getRunningID());
    logger.debug("Current submitted test suites size=" + testSets.size());
    if (testSets.size() < 1) {
      timeout = -1;
    }

    tr.finish();

    new AsyncFinish(tr).start();
  }

  private void finishTesSuiteWithDataTable(TestSuite tr) throws SQLException, ItemNotFoundException {
    tr.finishTestSuiteWithDBTable();
    if (tr.getTestSuite().contains("testcases.production"))
      tr.setResultForPrdSanityCases();
    testSets.remove(tr.getRunningID());

    logger.info("Sending mail for scripts " + tr.getTestSuite());
    Email mail = new Email();
    mail.from = TestProperty.get("mail.from", "noreply@reserveamerica.com");
    mail.to = tr.getMailto();
    String debugto = TestProperty.get("mail.regression.debug.to");

    if (mail.to.matches(".+@null") || mail.to.length() < 1) {
      mail.to = TestProperty.get("mail.to", "AO.QAOrmstest@activenetwork.com");
    }

    if (debugto != null && debugto.length() > 0 && !mail.to.toLowerCase().contains(debugto.toLowerCase()) && mail.to.toLowerCase().contains("ao.qaormstest@activenetwork.com")) {
      mail.cc = debugto;
    }

    String err = "";
    int total = tr.getTotal();
    int passed = tr.getPassedNumber();
    int failed = tr.getFailedNumber();
    int notrun = tr.getNotRunNumber();
    int unknown = total - passed - failed - notrun;
    if (unknown != tr.getUnknownNumber()) {
      err = "Calculated unknown number " + unknown + " is not expected " + tr.getUnknownNumber();
      logger.warn(err);
    }

    mail.subject = tr.getTestSuite() + " result for " + tr.getEnv();

    mail.text = "<body>\n" + "<p>\n";
    if (total > 0)
      mail.text += "Total " + total + " script(s).<br />";
    else
      mail.text += "There were no script(s) found.<br />";

    if (failed < 1 && notrun < 1 && unknown < 1 && passed == total) {
      mail.text += "All submitted script(s) PASSED!<br />";
    } else {
      if (failed > 0)
        mail.text += "-- " + failed + " script(s) FAILED.<br />";
      if (passed > 0)
        mail.text += "-- " + passed + " script(s) PASSED.<br />";
      if (notrun > 0)
        mail.text += "-- " + notrun + " script(s) were not executed.<br />";
      if (unknown > 0)
        mail.text += "-- " + unknown + " script(s) didn't get results.<br />";
    }
    mail.text += "</p>\n";

    if (err.length() > 0) {
      mail.text += "Warn: " + err + "<br />";
    }

    String[] info = tr.createResultInfoWithDataTable();
    if (failed > 0) {
      mail.text += "<div>\n";
      mail.text += "<h2>Failed scripts:</h2>\n";
      mail.text += "<p>";
      mail.text += info[0];
      mail.text += "</p>\n" +
          "</div>\n";
    }
    if (passed > 0) {
      mail.text += "<div>\n";
      mail.text += "<h2>Passed scripts:</h2>\n";
      mail.text += "<p>";
      mail.text += info[1];
      mail.text += "</p>\n" +
          "</div>\n";
    }
    if (notrun > 0) {
      mail.text += "<div>\n";
      mail.text += "<h2>Scripts not executed:</h2>\n";
      mail.text += "<p>";
      mail.text += info[2];
      mail.text += "</p>\n" +
          "</div>\n";
    }

    if (unknown > 0) {
      mail.text += "<div>\n";
      mail.text += "<h2>Scripts with unknown status:</h2>\n";
      mail.text += "<p>";
      mail.text += info[3];
      mail.text += "</p>\n" +
          "</div>\n";
    }

    int seconds = tr.getExecutionTime();

    if (seconds > 0) {
      mail.text += "<p>The scripts have run for total ";
      int totalMins = Math.round(seconds / 60);
      if (totalMins <= 0) {
        int totalSecs = seconds % 60;
        mail.text += totalSecs + " seconds</p>\n";
      } else {
        mail.text += totalMins + " minutes</p>\n";
      }
    }
    mail.text += "</body>\n";

    //send email
    try {
      TestMonitor.logger.info("Mail text:\n" + mail.text);
      mail.send();
    } catch (Throwable e) {
      TestMonitor.logger.error("Failed to send email due to Excepton/error: " + e.getMessage(), e);
      TestMonitor.logger.info("Test result: \n" + mail.text);
    }
    logger.info("Sending email");
    testSets.remove(tr.getRunningID());
  }

//	private void recoverAll() {
//		for(int i=1;i<5;i++) {
//			String env="qa"+i;
//			String runningidCol=env+"_running_id";
//			String statusCol=env+"_status";
//			String query="select qa"+i+"";
//		}
//	}
//
//	private void recoverTestSets(String runningID, String env) {
//		String[] env={"qa","","",""};
//
//		String query="select casename, "+env+"_result, "+env+"_status, "+env+"_running_id from test_cases where casename like '"+testSet+"%' and "+env+"_active="+ACTIVE;
//		if(individual) {
//			String caseList=TestProperty.get("cases");
//			String[] tokens=caseList.split(",");
//			StringBuffer buf=new StringBuffer();
//			for(int i=0;i<tokens.length;i++) {
//				buf.append("'");
//				buf.append(tokens[i]);
//				buf.append("'");
//				if(i<tokens.length-1) {
//					buf.append(",");
//				}
//
//			}
//			query="select casename, "+env+"_result, "+env+"_running_id from test_cases where casename in ("+buf.toString()+") and "+env+"_active="+ACTIVE;
//
//		} else if(failedOnly) {
//			query +=" and "+env+"_result !="+RESULT_PASSED;
//		}
//		DataBase db=DataBase.getInstance();
//		db.connect();
//		ResultSet rs=db.executeQuery(query);
//		while(rs.next()) {
//			String caseName=rs.getString("casename");
//			int runResult=rs.getInt(env+"_result");
//			String time="";
//			if(runResult<3) {
//				String t=rs.getString(env+"_running_id");
//				if(t==null || t.length()<1) {
//					time="unknown";
//				} else {
//					time=Util.parseTime(t);
//				}
//			}
//			StringBuffer buf;
//			if(runResult==RESULT_PASSED) {
//				buf=passed;
//				passedCount++;
//			} else {
//				buf=failed;
//				failedCount++;
//			}
//			buf.append(caseName);
//			buf.append("   ---   ");
//			String textResult;
//			switch (runResult) {
//			case RESULT_FAILED:
//				textResult="FAILED";
//				break;
//			case RESULT_PASSED:
//				textResult="PASSED";
//				break;
//			default:
//				textResult="NOT EXECUTED";
//			}
//
//			buf.append(textResult);
//			if(time.length()>0) {
//				buf.append(" (submitted at ");
//				buf.append(time);
//				buf.append(")");
//			}
//
//			buf.append("\n");
//		}
//
//		db.disconnect();
//	}

  private void processBeginMsg(TextMessage msg) throws JMSException, SQLException {

    String runningId = msg.getStringProperty("runningId");
    if (!testSets.containsKey(runningId)) {//dismiss
      recoverTestSuite(runningId);
    }
//		int index=Integer.parseInt(msg.getStringProperty("index"));
    String caseName = msg.getText();
    String caseID = msg.getStringProperty("caseID");
    String host = msg.getStringProperty("host");
    String ip = msg.getStringProperty("ip");
    int tool = Integer.parseInt(msg.getStringProperty("tool"));
    int executeTime = Integer.parseInt(msg.getStringProperty("executionTime"));
    int branchTotal = msg.getIntProperty("branchTotal");
    int branch = msg.getIntProperty("branch");
    String ids = msg.getStringProperty("ids");
    //construct unique key as caseID for setup script and production sanity test cases
    if (caseName.contains("supportscripts.qasetup.") || caseName.contains("testcases.production.")) {
      String tablename = msg.getStringProperty("tablename");
      if (Util.isEmpty(tablename))
        caseID = caseID + "::";
      else
        caseID = caseID + ":" + tablename + ":" + ids;
    }

    if (!testSets.containsKey(runningId)) {//dismiss
      logger.debug("Missing head message for test set id=" + runningId);
      return;
    }
    TestSuite tr = (TestSuite) testSets.get(runningId);
    tr.startTestcase(caseID, caseName, host, ip, executeTime, tool, branchTotal, branch, ids);
  }

  private void processQueryMsg(TextMessage msg) throws JMSException, SQLException {
    logger.debug("Query all test set status.");
    String replyto = msg.getStringProperty("replyto");
    String replytype = msg.getStringProperty("replytype");
    boolean isTopic = replytype != null && replytype.equalsIgnoreCase("topic");

    if (replyto == null || replyto.length() < 1) {
      logger.debug("ReplyTo message queue is unknown. Skipped query!");
      return;
    }

    String text = "\nQuery got following result:\n";
    if (testSets.size() < 1)
      text += "\tThere are no test suites ongoing right now.";
    else
      text += queryTestSetStatus();

    String runnerInfo = "Script runners' status:\n";
    runnerInfo += runnerInfoToString(queryRunnerInfo("all"));
    text += "\n\n" + runnerInfo;

    producer.connect(replyto, isTopic);
    Message toreply = producer.createTextMessage(text);
    toreply.setStringProperty("queryID", msg.getStringProperty("queryID"));
    producer.produceMessage(toreply);
    producer.disconnect();
  }

//	private void processStartMsg(TextMessage msg) throws JMSException, IOException, InterruptedException, SQLException {
//		String node=msg.getStringProperty("node");
//		List<String> info=queryRunnerInfo(node);
//		List<String> nodes=new ArrayList<String>();
//
//		for(String text:info) {
//			if(text.contains("no response")) {
//				String nodeName=text.split(" - ")[0].trim();
//				nodes.add(nodeName);
//			}
//		}
//		List<String> errorMsg=new ArrayList<String>();
//		if(nodes.size()>0) {
//			StringBuffer ips=new StringBuffer();
//			for(String text: nodes) {
//				String ip=TestProperty.get("test."+text.toLowerCase()+".ip");
//				if(ip!=null && ip.length()>0) {
//					ips.append(ip);
//					ips.append(" ");
//				} else {
//					errorMsg.add(text+"'s ip is not in properties file");
//				}
//			}
//			String ipString=ips.toString().trim();
//			String cmd="java -jar X:/TestDriver/testdriver.jar com.active.qa.testdriver.TestAgent";
//			if(ipString.length()>0) {
//				logger.info("Starting test runners: "+ipString);
//				Executor startRunner=new Executor("x:\\TestDriver\\tools\\psexec.exe \\"+ipString+" -i 0 -d "+cmd);
//				startRunner.start();
//				startRunner.join();
//			}
//		}
//
//	}

  private void processRunnerMsg(TextMessage msg) throws JMSException, SQLException {
    logger.debug("Query all script runners' status.");
    String replyto = msg.getStringProperty("replyto");
    String replytype = msg.getStringProperty("replytype");
    boolean isTopic = replytype != null && replytype.equalsIgnoreCase("topic");

    if (replyto == null || replyto.length() < 1) {
      logger.debug("ReplyTo message queue is unknown. Skipped query!");
      return;
    }

    String runnerInfo = "Script runners' status:\n";
    runnerInfo += runnerInfoToString(queryRunnerInfo("all", 1)); //1- only display selector info

    producer.connect(replyto, isTopic);
    Message toreply = producer.createTextMessage(runnerInfo);
    toreply.setStringProperty("queryID", msg.getStringProperty("queryID"));
    producer.produceMessage(toreply);
    producer.disconnect();
  }

  private String runnerInfoToString(List<String> info) {
    StringBuffer infos = new StringBuffer();
    int count = 1;
    for (String text : info) {
      infos.append("\t#" + count + ". ");
      infos.append(text);
      infos.append("\n");
      count++;
    }

    return infos.toString();
  }

  private List<String> queryRunnerInfo(String node) throws JMSException, SQLException {
    return queryRunnerInfo(node, 0);
  }

  /**
   * @param node
   * @param type - 0-test case execution status; 1-selector info
   * @return
   * @throws JMSException
   * @throws SQLException
   */
  private List<String> queryRunnerInfo(String node, int type) throws JMSException, SQLException {
    String topic = TestProperty.get("mq.runner.topic");
    String replyto = TestProperty.get("mq.monitor.queue2");
    String msgID = Util.getQueryID();

    producer.connect(topic, true);
    String[] msgProperty = {"type=query", "replyto=" + replyto, "replytype=queue", "node=" + node, "msgID=" + msgID};
    Message msg = producer.createTextMessage("Query runners' info", msgProperty);

    producer.produceMessage(msg);
    producer.disconnect();

    String[] runners;
    if (node.equalsIgnoreCase("all")) {
      runners = Util.getRegisteredRunners();
    } else {
      runners = node.split(",");
    }

    List<String> registeredNodes = new ArrayList<String>();
    for (String runner : runners) {
      registeredNodes.add(runner);
    }

    List<String> info = new ArrayList<String>();

    boolean done = false;
    int timeout = Integer.parseInt(TestProperty.get("testmonitor.query.timeout")) * 1000;

    while (!done) {
      String aID = "";
      Message aMsg = null;
      aMsg = consumer.consumeMessage(replyto, timeout);
      if (aMsg != null) {
        aID = aMsg.getStringProperty("msgID");
        if (aID != null && aID.equalsIgnoreCase(msgID)) {
          String text = ((TextMessage) aMsg).getText();

          String host = aMsg.getStringProperty("node");
          String ip = aMsg.getStringProperty("ip");
          String selector = aMsg.getStringProperty("selector");
          switch (type) {
            case 1:
              info.add(host + "(" + ip + "):" + selector);
              if (registeredNodes.contains(host)) {
                registeredNodes.remove(host);
              }
              break;
            case 0:
            default:
              if (!registeredNodes.contains(host)) {
                text += " (Unregistered)";
              } else {
                registeredNodes.remove(host);
              }
              if (registeredNodes.size() < 1) {
                timeout = 2000;
              }
              info.add(text);

          }
        }
      } else {
        done = true;
      }
    }

    for (String text : registeredNodes) {
      info.add(text + " - no response");
    }

    return info;

  }

  private void clean(String id) throws JMSException, SQLException, ItemNotFoundException {
    TestSuite ts = testSets.get(id);
    if (ts != null) {
      String suitename = ts.getTestSuite();
      if (suitename.matches("^testcases\\.(sanity|regression)\\..+")) {
        updateStatusWithExecutionStop(ts.getEnv(), id);
      }
      finishTestSuite(ts);
    }
  }

  private void processBuildMsg(TextMessage msg) throws JMSException, SQLException {
    buildInProgress = true;
    String replyto = msg.getStringProperty("replyto");
    String replytype = msg.getStringProperty("replytype");
    String buildtype = msg.getStringProperty("buildtype");
    String project = msg.getStringProperty("project");

    String text = build(buildtype, project);

    buildInProgress = false;
    Util.setBuilding(false);

    if (project.contains("ormsclient")) {
      String[] envs = project.contains("ormsclient_qa") ? Util.matches(project, "(?<=ormsclient_)qa\\d") : TestProperty.get("test.env").split(",");
      Util.updateOrmsclientSyncStatus(0, -1, envs);
    }

    if (replyto != null && replyto.length() > 0) {
      boolean isTopic = replytype != null && replytype.equalsIgnoreCase("topic");
      producer.connect(replyto, isTopic);
      Message toreply = producer.createTextMessage(text);
      toreply.setStringProperty("msgID", msg.getStringProperty("msgID"));
      producer.produceMessage(toreply);
      producer.disconnect();
    }

  }

  /**
   * Process TextMessage
   *
   * @param msg
   * @throws Exception
   * @throws IOException
   * @throws InterruptedException
   */
  private void processMessage(TextMessage msg) throws Exception {
    String type = msg.getStringProperty("type");
    System.out.println("Monitor receive " + type + " message....");
    if (type.equalsIgnoreCase("head")) {
      // the text message is a test set head
      //Test set head is telling the test monitor all the information about the test set.
      //Test monitor will start to monitor test set status according to this information
      processHeadMsg(msg);
    } else if (type.equalsIgnoreCase("result")) {
      // the text message is a test result
      processResultMsg(msg);
    } else if (type.equalsIgnoreCase("clean")) {
      // the text message is informing to clean up a test suite
      processCleanMsg(msg);
    } else if (type.equalsIgnoreCase("begin")) {
      // the text message is informing a test case is consumed and will start
      processBeginMsg(msg);
    } else if (type.equalsIgnoreCase("query")) {
      // the text message is querying all test sets' status
      processQueryMsg(msg);
    } else if (type.equalsIgnoreCase("runner")) {
      // the text message is querying all test runners' status
      processRunnerMsg(msg);
//		} else if(type.equalsIgnoreCase("start")) {
      // the text message is asking to start test runners which are down
      //remote start is not implemented yet
      //processStartMsg(msg);
    } else if (type.equalsIgnoreCase("build")) {
      processBuildMsg(msg);
    } else if (type.equalsIgnoreCase("buildsync")) {
      if (!buildInProgress) {
        Util.setBuilding(false);
        logger.info("Set building in progress=false");
        buildInProgress = false;
      }
    } else if (type.equalsIgnoreCase("reload")) {
      TestProperty.reLoad();
      ConnectionTool.close();
      boolean debug = Boolean.valueOf(TestProperty.get("monitor.debug"));
      if (debug) {
        logger.setLevel(Level.DEBUG);
      } else {
        logger.setLevel(Level.INFO);
      }
    }

  }

  /**
   * Query all test sets' status
   *
   * @return - query result in a String
   */
  private String queryTestSetStatus() {
    Enumeration<TestSuite> items = testSets.elements();
    int count = 1;
    String text = "\tThere are total " + testSets.size() + " test suites ongoing:\n";

    //query test set's status
    while (items.hasMoreElements()) {
      TestSuite tr = (TestSuite) items.nextElement();
      text += "\t#" + count + ". test_suite=" + tr.getTestSuite() + "-" + tr.getEnv() + (tr.isFailedOnly() ? " (rerun failed cases)" : "") + ", submit_time=" + Util.parseTime(tr.getRunningID()) + ", id=" + tr.getRunningID() + ", size=" + tr.getTotal() + ", status=" + tr.getStatusInfo() + "\n";
      count++;
    }

    return text;
  }

  /**
   * Check test set to see if any test set timed out. If timed out, close the test set and send out an email
   *
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws JMSException
   * @throws ItemNotFoundException
   */
  private void checkTestSetStatus() throws SQLException, JMSException, ItemNotFoundException {
    logger.debug("Check all test set status.");
    Enumeration<TestSuite> items = testSets.elements();

    //verify each test set's status
    while (items.hasMoreElements()) {
      TestSuite tr = (TestSuite) items.nextElement();

      logger.debug("suite id:" + tr.getRunningID() + (tr.allTestcasesGotConsumed() ? " all cases get consumed: time_remain=" + tr.getTimeRemaining() + " inprogress=" + tr.getInprogressNumber() : " some cases are still in the queue"));
      if (tr.getTestSuite().startsWith("supportscripts") || tr.getTestSuite().startsWith("testcases.production")) {
        logger.debug("No implement for check support script time out yet.");
        continue;
      }
      if (tr.allTestcasesGotConsumed() && tr.getTimeRemaining() <= 0 && tr.getInprogressNumber() > 0) {
        long now = Calendar.getInstance().getTimeInMillis();
        long diff = Math.round((now - tr.getExpectedFinishTime()) / (1000 * 60));
        logger.debug("testsetID#" + tr.getRunningID() + " timed out (expected remaining time =" + tr.getTimeRemaining() + ") ");
        if (diff > TEST_CASE_TIMEOUT) { //time out, force to send an email and end the test suite
          logger.debug("Time out after " + diff + " minutes beyond expected finish time, force sending an email.");
          finishTestSuite(tr);
          //update test result and status to NA for those test cases timed out
          updateStatusWithExecutionStop(tr.getEnv(), tr.getRunningID());
        }
      }
    }
  }

  /**
   * Update test case status with EXECUTION_STOP
   *
   * @param env
   * @param runningId
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  private void updateStatusWithExecutionStop(String env, String runningId) throws SQLException {
    String query = "update test_cases set " + env + "_status=" + EXECUTION_STOP + "," + env + "_result=" + RESULT_NA + " where " + env + "_running_id='" + runningId + "' and (" + env + "_status=" + EXECUTION_RUNNING + " or " + env + "_status=" + EXECUTION_SUBMITTED + ")";
    DataBase db = DataBase.getInstance();
//		db.connect();
    int count = db.executeUpdate(query);
//		db.disconnect();
    if (count < 0)
      logger.error("Failed to run query: " + query);
  }

  private void cleanupTestQueue(String... runningIds) {
    String query = "update test_execution set status_code=" + EXECUTION_STOP + " where id in (" + Util.arrayToString(runningIds, true, ",") + ")";
    DataBase.getInstance().executeUpdate(query);

    StringBuffer selector = new StringBuffer();
    for (int i = 0; i < runningIds.length; i++) {
      if (i == 0) {
        selector.append("runningId = '" + runningIds[i] + "'");
      } else {
        selector.append(" OR runningId = '" + runningIds[i] + "'");
      }
    }

    TestConsumer consumer = new TestConsumer(selector.toString());
    consumer.start();
  }

  /**
   * Send an email about the test result
   *
   * @param tr - test result information
   * @throws ItemNotFoundException
   * @throws SQLException
   */
  public static void sendEmail(TestSuite tr) throws ItemNotFoundException, SQLException {
    Email mail = new Email();
    mail.from = TestProperty.get("mail.from", "noreply@reserveamerica.com");
    mail.to = tr.getMailto();
    String debugto = tr.getTestSuite().startsWith("testcases.sanity") ? TestProperty.get("mail.sanity.debug.to") : TestProperty.get("mail.regression.debug.to");

    if (mail.to.matches(".+@null") || mail.to.length() < 1) {
      mail.to = TestProperty.get("mail.to", "AO.QAOrmstest@activenetwork.com");
    }

    if (debugto != null && debugto.length() > 0 && !mail.to.toLowerCase().contains(debugto.toLowerCase()) && mail.to.toLowerCase().contains("ao.qaormstest@activenetwork.com")) {
      mail.cc = debugto;
    }

    String err = "";
    int total = tr.getTotal();
    int passed = tr.getPassedNumber();
    int failed = tr.getFailedNumber();
    int notrun = tr.getNotRunNumber();
    int unknown = total - passed - failed - notrun;
    if (unknown != tr.getUnknownNumber()) {
      err = "Calculated unknown number " + unknown + " is not expected " + tr.getUnknownNumber();
      logger.warn(err);
    }

    mail.subject = tr.getTestSuite() + " test result for " + tr.getEnv();

    mail.text = "<body>\n" + "<p>\n";
    if (total > 0)
      mail.text += "Total " + total + " test case(s).<br />";
    else
      mail.text += "There were no test cases found.<br />";

    if (failed < 1 && notrun < 1 && unknown < 1 && passed == total) {
      mail.text += "All submitted test cases PASSED!<br />";
    } else {
      if (failed > 0)
        mail.text += "-- " + failed + " test case(s) FAILED.<br />";
      if (passed > 0)
        mail.text += "-- " + passed + " test case(s) PASSED.<br />";
      if (notrun > 0)
        mail.text += "-- " + notrun + " test case(s) were not executed.<br />";
      if (unknown > 0)
        mail.text += "-- " + unknown + " test case(s) didn't get results.<br />";
    }
    mail.text += "</p>\n";

    if (err.length() > 0) {
      mail.text += "Warn: " + err + "<br />";
    }

    String[] info = tr.createResultInfo();
    if (failed > 0) {
      mail.text += "<div>\n";
      mail.text += "<h2>Failed cases:</h2>\n";
      mail.text += "<p>";
      mail.text += info[0];
//			mail.text +="---------------------------------<br />";
      mail.text += "</p>\n" +
          "</div>\n";
    }
    if (passed > 0) {
      mail.text += "<div>\n";
      mail.text += "<h2>Passed cases:</h2>\n";
      mail.text += "<p>";
      mail.text += info[1];
//			mail.text +="\n---------------------------------<br />";
      mail.text += "</p>\n" +
          "</div>\n";
    }
    if (notrun > 0) {
      mail.text += "<div>\n";
      mail.text += "<h2>Cases not executed:</h2>\n";
      mail.text += "<p>";
      mail.text += info[2];
//			mail.text +="\n---------------------------------<br />";
      mail.text += "</p>\n" +
          "</div>\n";
    }

//		if (info[3].length()>0){
    if (unknown > 0) {
      mail.text += "<div>\n";
      mail.text += "<h2>Cases with unknown status:</h2>\n";
      mail.text += "<p>";
      mail.text += info[3];
//			mail.text +="\n---------------------------------<br />";
      mail.text += "</p>\n" +
          "</div>\n";
    }

    int seconds = tr.getExecutionTime();

    if (seconds > 0) {
      mail.text += "<p>The scripts have run for total ";
      int totalMins = Math.round(seconds / 60);
      if (totalMins <= 0) {
        int totalSecs = seconds % 60;
        mail.text += totalSecs + " seconds</p>\n";
      } else {
        mail.text += totalMins + " minutes</p>\n";
      }
    }

    //page timing summary
    try {
      String pageTimingSummary = getPageTimingSummary(tr.getRunningID());
      if (pageTimingSummary != "")
        mail.text += "<p>\nWarning: the following pages' loading time exceeds " + TestProperty.get("page.time.treshold") + " seconds:\n\n";
      mail.text += pageTimingSummary;
      mail.text += "</p>\n";
    } catch (Throwable e) {
      TestMonitor.logger.error("Failed to get page timing information due to Excepton/error: " + e.getMessage(), e);
    }
    mail.text += "</body>\n";

    //send email
    try {
      mail.send();
    } catch (Throwable e) {
      TestMonitor.logger.error("Failed to send email due to Excepton/error: " + e.getMessage(), e);
      TestMonitor.logger.info("Test result: \n" + mail.text);
    }
    logger.info("Sending email");
  }

  public static String getPageTimingSummary(String runningId) throws SQLException, ClassNotFoundException {
    DataBase db = DataBase.getInstance();
    String time_treshold = TestProperty.get("page.time.treshold");
    String query = "select * from page_timing where loadingtime >" + time_treshold + " and execution_id='" + runningId + "' order by testcaseName";
    String summary = "";
    try {
      String[] colNames = new String[]{"pagename", "testcaseName", "loadingtime"};
      List<String[]> resultList = db.executeQuery(query, colNames);
      int count = 0;
      for (int i = 0; i < resultList.size(); i++) {
        count++;
        String pageName = resultList.get(i)[0];
        String caseName = resultList.get(i)[1];
        String timing = resultList.get(i)[2];
        summary += count + ". " + pageName + "\tloading time=" + timing + " in " + caseName + "<br />\n";
      }
    } catch (Exception e) {
      summary = "Failed to get page timing result due to " + e.getMessage();
    }

    return summary;
  }

  //comment out legacy framework build cmds
//	private static List<CMD> contructLegacyBuildCmds(String buildtype,String project) {
//		String os=System.getProperty("os.name").split("\\s")[0].toLowerCase();
//		if(!os.matches("windows|linux")) {
//			throw new ItemNotFoundException("OS '"+os+"' is not supported.");
//		}
//
//		String delimiter="/";
//		String rft_dir=TestProperty.get("rft.project.folder");
//		String selenium_dir=TestProperty.get("selenium.project.folder");
//		String ormsclient_dir=TestProperty.get("ormsclient.project.folder");
//		String testdriver_dir=TestProperty.get("testdriver.project.folder");
//
//		String basedir=TestProperty.get(os+".functest.basedir");
//		String update=TestProperty.get(os+".svn.cmd")+" update "+basedir;
//		String antcmd=TestProperty.get(os+".ant.cmd")+" -buildfile "+basedir;
//		String buildfile=TestProperty.get(os+".ant.buildfile");
//
//		String rft_update=update+delimiter+rft_dir;
//		String selenium_update=update+delimiter+selenium_dir;
//		String ormsclient_update=update+delimiter+ormsclient_dir;
//		String testdriver_update=update+delimiter+testdriver_dir;
//
//
//		String rft_build=antcmd+delimiter+rft_dir+delimiter+buildfile;
//		String selenium_build=antcmd+delimiter+selenium_dir+delimiter+buildfile;
//		String ormsclient_build=antcmd+delimiter+ormsclient_dir+delimiter+buildfile;
//		String testdriver_build=antcmd+delimiter+testdriver_dir+delimiter+buildfile;
//
//		List<CMD> cmds=new ArrayList<CMD>();
//		if(buildtype.contains("update")) {
//			if(project.contains("rft")){
//				cmds.add(new CMD("Update rft8",rft_update,null,".*Updated to revision \\d+\\."));
//			}
//
//			if(project.contains("selenium")) {
//				cmds.add(new CMD("Udpate selenium",selenium_update,null,".*Updated to revision \\d+\\."));
//			}
//
//			if(project.contains("ormsclient")) {
//				cmds.add(new CMD("Udpate ormsclient",ormsclient_update,null,".*Updated to revision \\d+\\."));
//			}
//
//			if(project.contains("testdriver")) {
//				cmds.add(new CMD("Udpate testdriver",testdriver_update,null,".*Updated to revision \\d+\\."));
//			}
//
//		}
//
//		if(buildtype.contains("clean")) {
//			if(project.contains("rft")){
//				cmds.add(new CMD("Clean rft8",rft_build+" clean",null,".*BUILD SUCCESSFUL.*"));
//			}
//			if(project.contains("selenium")) {
//				cmds.add(new CMD("Clean selenium",selenium_build+" clean",null,".*BUILD SUCCESSFUL.*"));
//			}
//			if(project.contains("ormsclient")) {
//				cmds.add(new CMD("Clean ormsclient",ormsclient_build+" clean",null,".*BUILD SUCCESSFUL.*"));
//			}
//			if(project.contains("testdriver")) {
//				cmds.add(new CMD("Clean testdriver",testdriver_build+" clean",null,".*BUILD SUCCESSFUL.*"));
//			}
//		}
//
//		if(buildtype.contains("compile")) {
//			if(project.contains("ormsclient")) {
//				String[] envs;
//
//				if(project.contains("ormsclient_")) {
//					envs=Util.matches(project, "(?<=ormsclient_)qa\\d");
//				} else {
//					envs=TestProperty.get("test.env").split(",");
//				}
//
//				List<String> builds=Util.getAWObuild(envs);
//
//				for(String build:builds) {
//					cmds.add(new CMD("Build ormsclient for "+build,ormsclient_build+" -Dormsrelease.version="+build+" deploy",".*(error|FAILED).*",".*BUILD SUCCESSFUL.*"));
//				}
//			}
//
//			if(project.contains("rft")){
//				cmds.add(new CMD("Build rft8",rft_build,".*(error|FAILED).*",".*BUILD SUCCESSFUL.*"));
//			}
//
//			if(project.contains("selenium")) {
//				cmds.add(new CMD("Build selenium",selenium_build,".*(error|FAILED).*",".*BUILD SUCCESSFUL.*"));
//			}
//
//			if(project.contains("testdriver")) {
//				cmds.add(new CMD("Build testdriver",testdriver_build+" deploy",".*(error|FAILED).*",".*BUILD SUCCESSFUL.*"));
//			}
//
//
//		}
//
//		return cmds;
//	}

  private List<String> getAwoMajorBuild(String project) {
    if (project.contains("awo")) {
      String[] envs = Util.matches(project, "(?<=awo_)qa\\d");
      return getUniqeMajorBuilds(envs);
    } else {
      return new ArrayList<String>();
    }
  }

  private List<CMD> contructBuildCmds(String buildtype, String project) {
    String os = System.getProperty("os.name").split("\\s")[0].toLowerCase();
    if (!os.matches("windows|linux")) {
      throw new ItemNotFoundException("OS '" + os + "' is not supported.");
    }

    List<String> awoMajor = getAwoMajorBuild(project);

    String delimiter = "/";
    String rft_dir = TestProperty.get("functest4.rft.driver.folder");
    String selenium_dir = TestProperty.get("functest4.selenium.driver.folder");

    String core_dir = TestProperty.get("functest4.core.folder");
    String ormsclient_dir = TestProperty.get("ormsclient.project.folder");
    String testdriver_dir = TestProperty.get("testdriver.project.folder");

    String basedir = TestProperty.get(os + ".functest.basedir");
    String update = TestProperty.get(os + ".svn.cmd") + " update " + basedir;
    String antcmd = TestProperty.get(os + ".ant.cmd") + " -buildfile " + basedir;
    String buildfile = TestProperty.get(os + ".ant.buildfile");

    String rft_update = update + delimiter + rft_dir;
    String selenium_update = update + delimiter + selenium_dir;

    String core_update = update + delimiter + core_dir;
    String ormsclient_update = update + delimiter + ormsclient_dir;
    String testdriver_update = update + delimiter + testdriver_dir;


    String rft_build = antcmd + delimiter + rft_dir + delimiter + buildfile;
    String selenium_build = antcmd + delimiter + selenium_dir + delimiter + buildfile;

    String core_build = antcmd + delimiter + core_dir + delimiter + buildfile;
    String ormsclient_build = antcmd + delimiter + ormsclient_dir + delimiter + buildfile;
    String testdriver_build = antcmd + delimiter + testdriver_dir + delimiter + buildfile;

    //awo commands:
    int awo_size = awoMajor.size();
    String[] awo_dir = new String[awo_size];
    String[] awo_update = new String[awo_size];//update+delimiter+awo_dir;
    String[] awo_build = new String[awo_size];//antcmd+delimiter+awo_dir+delimiter+buildfile;
    for (int i = 0; i < awo_dir.length; i++) {
      String am = awoMajor.get(i);

      awo_dir[i] = TestProperty.get("functest4.awo." + am + ".folder", "functest4_awo_" + am);
      awo_update[i] = update + delimiter + awo_dir[i];
      awo_build[i] = antcmd + delimiter + awo_dir[i] + delimiter + buildfile;
    }

    List<CMD> cmds = new ArrayList<CMD>();
    if (buildtype.contains("update")) {
      if (project.contains("rft")) {
        cmds.add(new CMD("Update rft8", rft_update, null, ".*Updated to revision \\d+\\."));
      }

      if (project.contains("selenium")) {
        cmds.add(new CMD("Udpate selenium", selenium_update, null, ".*Updated to revision \\d+\\."));
      }

      for (int i = 0; i < awo_size; i++) {
        cmds.add(new CMD("Update Awo_" + awoMajor.get(i), awo_update[i], null, ".*Updated to revision \\d+\\."));
      }

      if (project.contains("core")) {
        cmds.add(new CMD("Udpate Core", core_update, null, ".*Updated to revision \\d+\\."));
      }

      if (project.contains("ormsclient")) {
        cmds.add(new CMD("Udpate ormsclient", ormsclient_update, null, ".*Updated to revision \\d+\\."));
      }

      if (project.contains("testdriver")) {
        cmds.add(new CMD("Udpate testdriver", testdriver_update, null, ".*Updated to revision \\d+\\."));
      }

    }

    if (buildtype.contains("clean")) {
      if (project.contains("rft")) {
        cmds.add(new CMD("Clean rft8", rft_build + " clean", null, ".*BUILD SUCCESSFUL.*"));
      }
      if (project.contains("selenium")) {
        cmds.add(new CMD("Clean selenium", selenium_build + " clean", null, ".*BUILD SUCCESSFUL.*"));
      }

      for (int i = 0; i < awo_size; i++) {
        cmds.add(new CMD("Clean awo_" + awoMajor.get(i), awo_build[i] + " clean", null, ".*BUILD SUCCESSFUL.*"));
      }

      if (project.contains("core")) {
        cmds.add(new CMD("Clean core", core_build + " clean", null, ".*BUILD SUCCESSFUL.*"));
      }

      if (project.contains("ormsclient")) {
        cmds.add(new CMD("Clean ormsclient", ormsclient_build + " clean", null, ".*BUILD SUCCESSFUL.*"));
      }
      if (project.contains("testdriver")) {
        cmds.add(new CMD("Clean testdriver", testdriver_build + " clean", null, ".*BUILD SUCCESSFUL.*"));
      }
    }

    if (buildtype.contains("deploy")) {
      if (project.contains("ormsclient")) {
        String[] envs;

        if (project.contains("ormsclient_")) {
          envs = Util.matches(project, "(?<=ormsclient_)qa\\d");
        } else {
          envs = TestProperty.get("test.env").split(",");
        }

        List<String> builds = Util.getAWObuild(envs);

        for (String build : builds) {
          cmds.add(new CMD("Build ormsclient for " + build, ormsclient_build + " -Dormsrelease.version=" + build + " deploy", ".*(error|FAILED).*", ".*BUILD SUCCESSFUL.*"));
        }
      }

      if (project.contains("core")) {
        cmds.add(new CMD("Build core", core_build + " deploy", ".*(error|FAILED).*", ".*BUILD SUCCESSFUL.*"));
      }

      if (project.contains("rft")) {
        cmds.add(new CMD("Build rft8", rft_build + " deploy", ".*(error|FAILED).*", ".*BUILD SUCCESSFUL.*"));
      }

      if (project.contains("selenium")) {
        cmds.add(new CMD("Build selenium", selenium_build + " deploy", ".*(error|FAILED).*", ".*BUILD SUCCESSFUL.*"));
      }

      for (int i = 0; i < awo_size; i++) {
        cmds.add(new CMD("Build awo_" + awoMajor.get(i), awo_build[i] + " deploy", ".*(error|FAILED).*", ".*BUILD SUCCESSFUL.*"));
      }

      if (project.contains("testdriver")) {
        cmds.add(new CMD("Build testdriver", testdriver_build + " deploy", ".*(error|FAILED).*", ".*BUILD SUCCESSFUL.*"));
      }

    }

    return cmds;
  }

  private static class CMD {

    @SuppressWarnings("unused")
    String name, cmd, errorPattern, passPattern;

    CMD(String name, String cmd, String errorPattern, String passPattern) {
      this.name = name;
      this.cmd = cmd;
      this.errorPattern = errorPattern;
      this.passPattern = passPattern;

    }
  }

  public String build(String buildtype, String project) {
    List<CMD> cmds = contructBuildCmds(buildtype, project);
    if (cmds == null) {
      return null;
    }
    String text = "";

    for (int i = 0; i < cmds.size(); i++) {
      CMD cmd = cmds.get(i);
      CMDResult result = executeCMD(cmd);
      if (result.exitCode != 0 || !result.success) {
        text += result.output + "\n";
        text += cmd.name + " was failed.\n";
        break;
      } else if (result.errorCount > 0) {
        text += result.output + "\n";
        text += cmd.name + " get errors \n";
      } else {
        text += cmd.name + " successful: " + result.output + "\n";
      }
    }

    return text;

  }

  private static CMDResult executeCMD(CMD cmd) {
    Runtime rt = Runtime.getRuntime();
    CMDResult result = new CMDResult();
    try {
      logger.info("Executing command: " + cmd.cmd);
      Process pr = rt.exec(cmd.cmd);
      BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

      String inputline = null;
      String last = "";
      result.success = true;

      while ((inputline = input.readLine()) != null) {
        if (cmd.errorPattern != null && cmd.errorPattern.length() > 0 && inputline != null) {
          result.output += inputline + "\n";
          if (inputline.matches(cmd.errorPattern)) {
            result.errorCount++;
            result.success = false;
          }
        }
        logger.info(inputline);
        last = inputline;
      }
      result.exitCode = pr.waitFor();
      if (result.success) {
        result.output = last;
      }

    } catch (IOException e) {
      result.exitCode = 1;
      result.output += "FATAL: " + e.getMessage();
      e.printStackTrace();
      result.success = false;
    } catch (InterruptedException e) {
      result.exitCode = 1;
      result.output += "FATAL: " + e.getMessage();
      e.printStackTrace();
      result.success = false;
    }

    logger.info(result.toString());
    return result;
  }
}

class CMDResult {
  public int exitCode = 0;
  public String output = "";
  public int errorCount = 0;
  public boolean success = true;

  @Override
  public String toString() {
    return "output=" + output + "; errorCount=" + errorCount + "; success=" + success + "; exitCode=" + exitCode;

  }
}

//class TestResult {
//	public boolean failedOnly;
//	public boolean started=false;
//	public String caseList="";
//	public int total=0;
//	public int passed=0;
//	public int failed=0;
//	public int notrun=0;
//	public String runningId="";
//	public String testSet="";
//	public String env="";
//	public String passedList="";
//	public String failedList="";
//	public String notrunList="";
//	public String unknownList="";
//
//	public long startTime=0;
//	public long endTime=0;
//
//	public String mailto="";
//
//	public boolean startTiming=false;
//	public long time;
//}


