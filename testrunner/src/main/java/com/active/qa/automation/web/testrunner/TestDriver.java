package com.active.qa.automation.web.testrunner;

import com.active.qa.automation.web.testrunner.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

//import util.Driverlauncher;

/**
 * Created by tchen on 1/18/2016.
 */
public class TestDriver implements TestConstants, Executable {
  private static Logger logger = Logger.getLogger(TestDriver.class);
  private String env = "";
  private String testSet = "";
  private boolean failedOnly = false;
  private boolean individual = false;
  private static int priority = UNKNOWN_PRIORITY;
  private static int tool = 0; // 0-na, 1-rft, 2-selenium
  private static int caseTotalBranch = 0; //default
  private static final int ERROR = -1;
  private static final int TESTCASE = 0;
  private static final int QUERY = 1;
  private static final int STOP = 2;
  private static final int SUSPEND = 3;
  private static final int RESET = 4;
  private static final int PROPERTY = 5;
  private static final int RUNNER_INFO = 6;
  private static final int RUNNER_CMD = 7;
  private static final int BUILD = 8;
  private static final int RESULT = 9;
  private static final int CLEAN = 10;
  private static final int QUIT = 11;
  private static final int SETUPSCRIPTS = 12;
  private static final int PRDSANITY = 13;
  private boolean newSetup = false;//this flag was used to indicate a new setup scripts or not
  private int setValue = -1;
  private int testSuiteValue = 0; //test suite is all both sanity and regression by default

  //added by pzhu, AUTO-1494
  private boolean productionRestart = false;//this flag used to indicate test for production restart.
  private int dataFetchRandomNum = 1;
  //

  private UserTestSuites user_TestSuites;
  private boolean userDefinedSuite = false;
  //Get Case Name as List<String>
  private List<String> caseNameList;
  //Split Case Name into String[] according to limitsize=1000
//	private String[] caseNameListStrs;
  private String[] caseIDListStrs;

  public void execute(String[] args) {
    TestProperty.load();
    TestProperty.resetLogfile("TestDriver");

    user_TestSuites = UserTestSuites.getInstance();

    if (Boolean.valueOf(TestProperty.get("debug", "false")) == true) {
      if (logger.getLevel() != Level.DEBUG) {
        logger.setLevel(Level.DEBUG);
      }
    }

    try {
      int code = parserCMDLine(args);

      switch (code) {
        case TESTCASE:
          submitTestCases();
          break;
        case SETUPSCRIPTS:
          submitSetupScripts();
          break;
        case PRDSANITY:
          submitPrdSanityTestCases();
          break;
        case BUILD:
          build();
          break;
        case QUERY:
          queryStatus("query");
          break;
        case RUNNER_INFO:
          queryStatus("runner");
          break;
        case RUNNER_CMD:
          controlTestRunners(TestProperty.get("runner.cmd"), TestProperty.get("runner.node"));
          break;
        case STOP:
          resetTestStatus(EXECUTION_SUBMITTED, EXECUTION_STOP);
          resetTestStatus(EXECUTION_HIBERNATED, EXECUTION_STOP);
          Util.setEFTRunning(STATUS_FALSE);
//				requestStop();
          break;
        case SUSPEND:
          resetTestStatus(EXECUTION_SUBMITTED, EXECUTION_SUSPEND);
          resetTestStatus(EXECUTION_HIBERNATED, EXECUTION_SUSPEND);
          Util.setEFTRunning(STATUS_FALSE);
//				requestSuspend();
          break;
        case PROPERTY:
          reloadProperties();
          break;
        case RESULT:
          sendResult();
          break;
        case RESET:
          resetTestStatus();
          break;
        case CLEAN:
          cleanTestSuites();
          Util.setEFTRunning(STATUS_FALSE);
          break;
      }
      if (code < 0)
        System.exit(1);
      else
        System.exit(0);

    } catch (JMSException e) {
      System.out.println("Failed to connect to message Queue due to " + e.getMessage());
    } catch (Throwable e) {
      System.out.println(e.toString());
      e.printStackTrace();
    }
  }

  /**
   * Submit test cases in the given test set for the given test environment
   *
   * @param env        - test environment qa1/qa2
   * @param testSet    - test set name
   * @param failedOnly - only cases failed in the last execution
   * @throws Exception
   */
  private void submitTestCases() throws Exception {
    ConnectionTool.connect();
    String output = " testset \"" + testSet + "\" for " + env + " environment.";
    if (individual) {
      output = "Starting " + TestProperty.get("cases") + " in" + output;
    } else if (failedOnly) {
      output = "Re-running failed cases in" + output;
    } else {
      output = "Starting all test cases in" + output;
    }
    System.out.println(output);
    System.out.println("Test results will be sent to email: " + TestProperty.get("mail.to"));

    ProducerTool producer = ProducerTool.getInstance();
    ProducerTool.logger = logger;

    List<String> cases = getTestCases();
    if (cases == null)
      System.exit(0);

    int multiCases = Integer.valueOf(cases.remove(cases.size() - 1).toString());
    String runningId = cases.remove(cases.size() - 1).toString();
    String multiList = cases.remove(cases.size() - 1).toString();
    String caseList = cases.remove(cases.size() - 1).toString();
    int size = cases.size();
    String monitorQueue = TestProperty.get("mq.monitor.queue");
    String emailto = TestProperty.get("mail.to");

    //inform monitor
    producer.connect(monitorQueue);
    int repeat = Integer.parseInt(TestProperty.get("repeat", "0")); //re-run all failed test cases automatically for the given number of times

    String[] msgProperty1 = {"type=head", "env=" + env, "testSet=" + testSet, "runningId=" + runningId, "total=" + size, "multiTotal=" + multiCases,
        "email=" + emailto, "repeat=" + repeat, "runners=" + TestProperty.get("runners")};
    Message msg = producer.createTextMessage(caseList + ":" + multiList, msgProperty1);
    msg.setBooleanProperty("failedOnly", failedOnly);
    Util.createTestExecutionRecord(testSet, env, runningId, emailto, size, repeat, failedOnly);
    producer.produceMessage(msg);
    producer.disconnect();

    //submit test case messages
    String testQueue = TestProperty.get("mq.test.queue");
    String rftQueue = TestProperty.get("mq.rft.queue");
    String smQueue = TestProperty.get("mq.sm.queue");
    String eftQueue = TestProperty.get("mq.eft.queue");
    String reportQueue = TestProperty.get("mq.report.queue");

    Long exetime = Calendar.getInstance().getTimeInMillis();
    for (int i = 0; i < size; i++) {
      int toolcode = NONE;
      int appcode = WEB;
      String caseID = null;

      String runners = TestProperty.get("runners");
      String aCase = (String) cases.get(i);

//			if(functest4 ) {
//				if(aCase.startsWith("testCases."))
//					aCase="com.activenetwork.qa.awo.testcases"+aCase.substring(9);
//				else
//					aCase="com.activenetwork.qa.awo."+aCase;
//			}

      toolcode = Integer.parseInt(Util.getAttribute(aCase, "tool"));
      caseID = Util.getAttribute(aCase, "testcaseId");
      int initial = 0;
      int branchTotal = Integer.parseInt(Util.getAttribute(aCase, "branchTotal"));
      appcode = Util.getAppCodeByCaseName(aCase, branchTotal);

      switch (appcode) {
        case REPORT:
          producer.connect(reportQueue);
          break;
        case EFT:
          producer.connect(eftQueue);
          break;
        case SM:
          producer.connect(smQueue);
          break;
        default:
          if (toolcode == RFT)
            producer.connect(rftQueue);
          else
            producer.connect(testQueue);
      }

      String[] msgProperty2 = {"env=" + env, "monitor=" + TestProperty.get("mq.monitor.queue"), "runningId=" + runningId, "caseID=" + caseID,
          "debug=" + TestProperty.get("debug", "false"), "draft=" + TestProperty.get("draft", "false"),
          "toolcode=" + toolcode, "runners=" + runners};
      Message msg2 = producer.createTextMessage(aCase, msgProperty2);
      msg2.setLongProperty("exetime", exetime);
      msg2.setIntProperty("branchTotal", branchTotal);
      msg2.setIntProperty("branch", initial); //the 1st time submit, branch value was 0
//    		msg2.setBooleanProperty("legacy", legacy); //flag for legacy functest3 framework
      producer.produceMessage(msg2, priority);
      producer.disconnect();
      Util.createTestExecutionDetailRecord(caseID, runningId, String.valueOf(toolcode));
    }

    logger.info("Total " + multiCases + " test cases with multiple branches.");
    logger.info("Total " + size + " cases.");
  }

//	public void createTestExecutionDetailRecord(String caseID,String runningId) {
//		DataBase db=DataBase.getInstance();
//		String[] ids=caseID.split(",");
//		String[] querys=new String[ids.length];
//		for(int i=0;i<ids.length;i++) {
//			querys[i]="insert into TEST_EXECUTION_DETAILS (case_id,execution_id,status,result) values ("+ids[i]+",'"+runningId+"',"+TestConstants.TESTCASE_PENDING+","+TestConstants.RESULT_NA+")";
//		}
//		int result=db.executeUpdate(querys);
//		if(result<1) {
//			logger.warn("Failed to record "+caseID+" in TEST_EXECUTION_DETAILS table!");
//		}
//	}

  private void usage(String msg) {
    String usage = "\nUsage: runtest [testSuite env] [-options]\n\n" +
        "\tthe testsuite is like \"sanity.orms\" or \"regression.basic.web\" or \"regression.basic.orms.call\",\"qasetup\",\"support.admin\" etc.\n" +
        "\tand also could be \"temp\" or \"dailyrun\" or pre-defined test suite by command \"--testsuites\"\n" +
        "\tthe env can be \"qa1\", \"qa2\", \"qa3\", \"qa4\" or \"qa5\"\n\n" +
        "where options include:\n\n" +
        "\t-c <casenames>\trun only specified test cases, case name should seperated by delimiter \",\"\n" +
        "\t-p <value>\tspecify the priority for the current test set low<0-9>high. The default priority is 6 for Sanity, 3 for regression.\n" +
        "\t-f\trun only failed cases in the last execution\n" +
        "\t-e <email>\tspecify the email address the result to be sent to, if the email domain is @activenetwork.com, you can just specify the account name\n" +
        "\t-t\t tool code: 0-none,1-rft,2-selenium\n" +
        "\t-x\t specifiy test runner to run test suite\n" +
        "\t-d\t for \"dailyrun\" with this option, it specify a specific date or date range seperated by delimiter.\n" +
        "\t-d\t for \"temp\" with this option, it specify cases list file(.txt) path. You should fist upload your file to raon-toolsvm. The default path is /home/deploy/TestDriver/temp/.\n" +
        "\t-l\t for \"temp\" with this option, it sspecify cases list, and cases should be seperated by delimiter \",\"\n" +
        "\t--stop\tstop the current test suite, with this option, the other options (-f and -e <email>) will be ignored if provided\n" +
        "\t--query\tquery the status of all test suite which are ongoing. In this case, testSuite and env are not necessary.\n" +
        "\t--build [cmd [project]]\tdeploy a new automation scripts' build. cmd options: update+clean+compile; project options: rft or selenium\n" +
        "\t--force\tforce the given test suite to be submitted\n" +
        "\t--property\task test runners or monitor to reload the test.properties file\n" +
        "\t--runner [cmd [runner list]]\tquery or control test runners: stop/restart/reboot/suspend/resume\n" +
        "\t--result\tsend a test result summary for the given test suite and environmen\n" +
        "\t--clean <runningIDs>\tremove the dead test suites with the given runningID, IDs should be seperated by delimiter \",\"\n" +
        "\t-set <value> \tsubmit all setup scripts in set#<value>[0-9] for all setup data. this is mandatory option for qasetup suite\n" +
        "\t-s\t test suite code: 0-all,1-sanity,2-regression\n" +
        "\t--new , all data setup result in env will be reset to 0.\n" +
        "\nUsage for --testsuites: ./runtest --testsuites <suite name> [new|add|remove] [-l|-d] [caselist|filepath]\n" +
        "\t--testsuites\t\t\t\t List all customized test suites\n" +
        "\t--testsuites <suite name>\t\t List test cases under specific test suite.\n" +
        "\t--testsuites <suite name> new [-l|-d] [caselist|filepath]\t Create a new test suite with -l or -d\n" +
        "\t--testsuites <suite name> add [-l|-d] [caselist|filepath]\t Add test cases to existed test suite with -l or -d\n" +
        "\t--testsuites <suite name> remove[-l|-d] [caselist|filepath]\t Delete test cases from existed test suite with -l or -d\n" +
        "\t--testsuites <suite name> remove\t Delete specific test suite\n\n" +
        "\t-l <caselist>\t With -l, specify cases list, and cases should be seperated by delimiter \",\"\n" +
        "\t-d <filepath>\t With -d, specify cases list file(.txt) path. You should fist upload your file to raon-toolsvm. The default path is /home/deploy/TestDriver/temp/.\n" +
        "\t-pr\trun production sanity for production restart\n";  //added by pzhu: AUTO-1494


    if (msg != null && msg.length() > 0) {
      System.out.println(msg);
    }
    System.out.println(usage);
  }

  /**
   * Parse command line arguments
   *
   * @param args
   * @throws Exception
   */
  private int parserCMDLine(String[] args) throws Exception {
    int code = -1;
    int size = args.length;

    List<String> userTestSuites = user_TestSuites.getTestSuiteName();

    if (args.length < 1) {
      usage("Error: missing command line arguments.");
      code = ERROR;
    } else if (args[0].equalsIgnoreCase("--query")) {
      code = QUERY;
    } else if (args[0].equalsIgnoreCase("--property")) {
      code = PROPERTY;
    } else if (args[0].equalsIgnoreCase("--clean")) {
      code = CLEAN;
      if (size == 2) {
        TestProperty.put("testsuite.ids", args[1]);
      } else {
        if (size < 2) {
          usage("Error: Missing execution IDs.");
        } else {
          usage("Error: too many arguments.");
        }
        code = ERROR;
      }

    } else if (args[0].equalsIgnoreCase("--runner")) {
      code = RUNNER_INFO;
      if (size > 1) {
        if (!args[1].matches("(stop|reboot|restart|start|suspend|resume|cmd\\..+)") || size > 3) {
          usage("Error: wrong arguments to operate test runners");
          code = ERROR;
        } else if (args[1].matches("cmd\\..+") && TestProperty.get(args[1]) == null) {
          usage("Error: command '" + args[1] + "' is not valid");
          code = ERROR;
        } else {
          String runners = "all";
          if (size == 3 && args[2].length() > 0) {
            runners = args[2];
          }
          //controlTestRunners(args[1], runners);
          TestProperty.put("runner.cmd", args[1]);
          TestProperty.put("runner.node", runners);
          code = RUNNER_CMD;
        }
      }
//		} else if(args[0].equalsIgnoreCase("--build") || args[0].equalsIgnoreCase("--build3")) {
    } else if (args[0].equalsIgnoreCase("--build")) {
      code = BUILD;

      String buildProjectPattern;
//			if(args[0].equalsIgnoreCase("--build")) {
      TestProperty.put("build.type", "update+deploy");
      TestProperty.put("build.project", "awo"); //default
      buildProjectPattern = "(rft|selenium|awo(_qa\\d)?|core|testdriver|ormsclient(_qa\\d)?)(\\+(rft|selenium|core|awo(_qa\\d)?|testdriver|ormsclient(_qa\\d)?))*";
//			} else {
//				legacy=true;
//				TestProperty.put("build.type", "update+compile");
//				TestProperty.put("build.project", "rft+selenium+ormsclient"); //default
//				buildProjectPattern="(rft|selenium|testdriver|ormsclient(_qa\\d)?)(\\+(rft|selenium|testdriver|ormsclient(_qa\\d)?))*";
//			}

      String buildTypePattern = "(update|clean|compile)?(\\+(clean|compile)){0,2}";
      if (size > 1) {
        if (!(args[1].matches(buildTypePattern) || args[1].matches(buildProjectPattern)) || size > 3) {
          usage("Error: wrong arguments to operate test runners");
          code = ERROR;
        } else {
          if (args[1].matches(buildTypePattern)) {
            TestProperty.put("build.type", args[1]);
            if (size > 2) {
              if (args[2].matches(buildProjectPattern)) {
                TestProperty.put("build.project", args[2]);
              } else {
                usage("Error: wrong arguments to operate test runners");
                code = ERROR;
              }
            }

          } else if (size > 2) {
            usage("Error: wrong arguments to operate test runners");
            code = ERROR;
          } else {
            TestProperty.put("build.project", args[1]);
          }
        }
      }
    } else if (args[0].equalsIgnoreCase("--testsuites")) {
      if (size == 1) {
        System.out.println("All User Defined Test Suites are listed as below:\n");
        printList(userTestSuites);
        System.out.println("\n\tTotal " + userTestSuites.size() + " test suites.\n");
        System.out.println("Get test cases list for each test suite by command: --testsuites <test suite name> \n");
      } else if (size == 2) {
        List<String> ucases = user_TestSuites.getTestCasesbyTestSuite(args[1]);
        if (ucases != null) {
          System.out.println("Test Cases for Test Suite: " + args[1] + "\n");
          printList(ucases);
          System.out.println("\n\tTotal " + ucases.size() + " cases.\n");
        }
      } else if (size == 3 && args[2].matches("remove")) {
        System.out.println("Warning: You are going to remove Test Suite: " + args[1]);
        if (!userConfirm()) {
          System.out.println("\nQuit!");
          code = QUIT;
        } else {
          user_TestSuites.deleteTestSuite(args[1]);
        }
      } else if (size == 5 && args[2].matches("(new|add|remove)") && args[3].matches("(-l|-d)")) {
        String testSuite = args[1];
        String ops = args[2];
        String mode = args[3];
        String path = args[4];
        user_TestSuites.parseTestSuitesCMD(testSuite, ops, mode, path);
      } else {
        usage("Error: wrong arguments for --testsuites");
        code = ERROR;
      }

    } else if (size > 1 && args[1].matches("^(qa[1-5]$)||live") &&
        (args[0].matches("^(sanity|regression|production)\\.(\\w+\\.)?(orms|web|eft|activenet)(\\.\\w+)*") ||
            userTestSuites.contains(args[0]) ||
            (size > 2 && args[0].matches("^(temp|dailyrun)$")) ||
            //qasetup command could be:
            //	qasetup qa1 -set 8
            //	qasetup qa1 -c supportscripts.qasetup.license.AddPrivilegeLicenseYears
            (size > 3 && args[0].matches("^qasetup(\\.\\w+)*")))
        ) {

      env = args[1];
      TestProperty.put("runners", "ALL");

      int index = 999;
      if (args[0].matches("^production(\\.\\w+)*")) {
        code = PRDSANITY;
        testSet = "testcases." + args[0];
        index = 2;
        priority = TestConstants.PRODUCTION_PRIORITY;
      } else if (args[0].matches("^qasetup(\\.\\w+)*")) {
        code = SETUPSCRIPTS;
        testSet = "supportscripts." + args[0];
        index = 2;//handled with -set option below
        priority = TestConstants.NORMAL_PRIORITY;
        failedOnly = true;
      } else {
        code = TESTCASE;

        if (userTestSuites.contains(args[0]) || args[0].equalsIgnoreCase("temp") || args[0].equalsIgnoreCase("dailyrun")) {
          testSet = args[0];
          caseNameList = user_TestSuites.getTestCasesbyTestSuite(args[0]);
          userDefinedSuite = true;
        } else if (args[0].matches("^(sanity|regression)\\.(\\w+\\.)?(orms|web|eft|activenet)(\\.\\w+)*")) {
          testSet = "testcases." + args[0];
          caseNameList = getCaseNames();
          userDefinedSuite = false;
        }

        if (size > 2) {
          if (args[0].equalsIgnoreCase("temp")) {
            testSet = "TempSuite";
            code = TESTCASE;
            index = 2;
          } else if (args[0].equalsIgnoreCase("dailyrun")) {
            testSet = "DailyRun";
            code = TESTCASE;
            index = 2;
          } else if (args[2].equalsIgnoreCase("--result")) {
            code = RESULT;
            index = 3;
          } else if (args[2].equalsIgnoreCase("--reset")) {
            code = RESET;
            System.out.println("Reset test status for test set \"" + testSet + "\" to NA for " + env);
            if (!userConfirm()) {
              System.out.println("\nQuit!");
              code = QUIT;
            }
          } else if (args[2].equalsIgnoreCase("--stop")) {
            String msg = "\nWarning: it is going to stop all test cases under this test suite";

            if (userConfirm("Do you want to keep the current result?")) {
              code = SUSPEND;
              msg += " and keep the current result!";
            } else {
              code = STOP;
              msg += " and persist the previous result!";
            }
            System.out.println(msg);
            if (!userConfirm()) {
              System.out.println("\nQuit stop command!");
              code = QUIT;
            } else {
              System.out.println("\nTest execution for test suite \"" + testSet + "\" for " + env + " will be stopped.");
              System.out.println("If some test cases are in middle of execution, they will continue and finish.");
              if (code == SUSPEND) {
                System.out.println("The remaining test cases will be set the result as NOT_RUN.\n");
              }
              System.out.println("It may take several minutes before it completely stops.\n");
            }
          } else if (Util.isBuildInProgress()) {
            syncBuildingProcess();
            System.out.println("\nBuilding process is in progress now! Please try again later.\n");
            code = QUIT;
          } else {
            index = 2;
          }// end if(args[0].equalsIgnoreCase("temp"))
        }// end if(size > 2)
      } //end if(args[0].matches("^qasetup(\\.\\w+)*"))

      if (code == TESTCASE) {
        if (args[0].indexOf("regression") >= 0) {
          priority = NORMAL_PRIORITY;
        }

        TestProperty.put("failedOnly", "false");
        TestProperty.put("force", "false");
        TestProperty.put("draft", "false");
        TestProperty.put("draft.only", "true");
        index = 2;

      }// end if(code==TESTCASE)

      //parse rest of optional arguments start from index
      //index=999 means all rest arguments will be ignored
      for (int i = index; i < size; i++) {
        if (args[i].equals("-set") && args[i + 1].matches("^[0-9]$") && code == SETUPSCRIPTS) {
          setValue = Integer.parseInt(args[i + 1]);
          i++;
        } else if (args[i].equals("-s") && args[i + 1].matches("^[0-9]$") && code == SETUPSCRIPTS) {//-s stands for test suite, 1-sanity,2-regression
          testSuiteValue = Integer.parseInt(args[i + 1]);
          i++;
        } else if (testSet.equals("TempSuite") && args[i].matches("-l|-d") && i + 1 < size && args[i + 1].length() > 0) {
          //for temp suite, it should have -l|-d parameter
          String[] temp = args[i + 1].trim().split(",");
          List<String> templist = new ArrayList<String>();
          if (args[i].matches("-l")) {
            templist = user_TestSuites.removeDuplicatedItem(Arrays.asList(temp));
          } else if (args[i].matches("-d")) {
            //handle exception
            templist = user_TestSuites.getTestCaseFromFile(args[i + 1]);
          }
          caseNameList.addAll(templist);
          i++;
        } else if (testSet.equals("DailyRun") && args[i].matches("-d") && i + 1 < size && args[i + 1].length() > 0) {
          //for daily run suite, it should have -d parameter
          String[] temp = args[i + 1].trim().split(",");
          if (temp.length < 1 || temp.length > 2) {
            System.out.println("There should be either one or two date parameters.");
            code = ERROR;
            break;
          }

          for (int k = 0; k < temp.length; k++) {
            if (Util.getDateStringPattern(temp[k]) == null) {
              System.out.println("Could not parse date " + temp[k]);
              code = ERROR;
              break;
            }
          }
          if (code != ERROR)
            caseNameList = Util.getCaseNamesByDate(temp);
          i++;
        } else if (args[i].equalsIgnoreCase("-f") && (code == TESTCASE || code == PRDSANITY)) {//for setup script, default as failedOnly
          failedOnly = true;
        } else if (args[i].equalsIgnoreCase("-pr") && (code == PRDSANITY)) {//For sanity of production restart. added by pzhu: AUTO-1494
          productionRestart = true;
        } else if (args[i].equalsIgnoreCase("-e") && i + 1 < size && args[i + 1].length() > 0 && !args[i + 1].startsWith("-")) {
          i++;
          int j = args[i].indexOf("@");
          if (j > 0)
            TestProperty.put("mail.to", args[i]);
          else
            TestProperty.put("mail.to", args[i] + "@" + TestProperty.get("mail.domain"));
        } else if (args[i].equalsIgnoreCase("-x")) {
          if (i + 1 < size && !args[i + 1].startsWith("-")) {
            String[] runners = args[i + 1].trim().split(",");
            for (int k = 0; k < runners.length; k++) {
              if ((TestProperty.get("test.runner").indexOf(runners[k] + ",")) == -1) {
                System.out.println("Error: " + runners[k] + " is an unknown runner");
                code = ERROR;
                break;
              }
            }
            TestProperty.put("runners", args[i + 1]);
            i++;
          } else {
            System.out.println("Error: missing argument after \"-x\" option");
            code = ERROR;
            break;
          }
        } else if (args[i].equalsIgnoreCase("-p") && i + 1 < size && args[i + 1].length() > 0) {
          i++;
          if (args[i].matches("^[0-9]$"))
            priority = Integer.parseInt(args[i]);
          else {
            System.out.println("Error: " + args[i] + " is not a valid priority low(0-9)high.");
            code = ERROR;
            break;
          }
        } else if (args[i].equalsIgnoreCase("-t") && i + 1 < size && args[i + 1].length() > 0) {
          i++;
          if (args[i].matches("^[0-2]$"))
            tool = Integer.parseInt(args[i]);
          else {
            System.out.println("Error: " + args[i] + " is not a valid tool code: 0-na, 1-rft, 2-selenium.");
            code = ERROR;
            break;
          }
        } else if (args[i].equalsIgnoreCase("-c")) {
          String cases = "";
          if (i + 1 < size && !args[i + 1].startsWith("-")) {
            cases = args[i + 1];
            i++;
          } else {
            usage("Error: missing argument after \"-c\" option");
            code = ERROR;
            break;
          }

          if (cases.length() > 0) {
            TestProperty.put("cases", cases);
            individual = true;
          }
          caseNameList = Util.getTestCasesList(cases.split(","), testSet);
        } else if (args[i].equalsIgnoreCase("--debug") && (code == TESTCASE || code == SETUPSCRIPTS || code == PRDSANITY)) {
          TestProperty.put("debug", "true");
        } else if (args[i].equalsIgnoreCase("-r") && (code == TESTCASE || code == SETUPSCRIPTS)) { //only for test case for far
          TestProperty.put("repeat", "1");
          if (i + 1 < size && !args[i + 1].startsWith("-")) {
            i++;
            String repeatNum = args[i];
            if (repeatNum.matches("\\d")) { //repeat number can only be 0-9
              TestProperty.put("repeat", repeatNum);
            } else {
              System.out.println("Error: " + args[i] + " is not valid repeat number 0-9");
              code = ERROR;
              break;
            }
          }
        } else if (args[i].equalsIgnoreCase("--draft") && code == TESTCASE) {
          TestProperty.put("debug", "true");
          TestProperty.put("draft", "true");
          TestProperty.put("draft.only", "false");
        } else if (args[i].equalsIgnoreCase("--draftonly") && code == TESTCASE) {
          TestProperty.put("debug", "true");
          TestProperty.put("draft", "true");
          TestProperty.put("draft.only", "true");
        } else if (args[i].equalsIgnoreCase("--force") && code == TESTCASE) {
          TestProperty.put("force", "true");
          System.out.println("\nWarning:\n\tYou are forcing to submit a test suite.");
          System.out.println("\tThe given test suite may be a subset of a test suite which has been submitted and is in progress right now.");
          System.out.println("\tIf this is the case, you may see test cases failed due to conflicts.");
          if (!userConfirm()) {
            System.out.println("\nQuit!");
            code = QUIT;
            break;
          }
        } else if (args[i].equalsIgnoreCase("--new") && code == SETUPSCRIPTS) {
          newSetup = true;
          failedOnly = false;
//				} else if(args[i].equalsIgnoreCase("--legacy") ){
//					legacy=true;
        } else {
          usage("Error: Unknown option#" + i + ": \"" + args[i] + "\"");
          code = ERROR;
          break;
        }
      }

      if (failedOnly && individual) {
        failedOnly = false;
        System.out.println("Warning: \"-f\" will be ignored since \"-c\" is provided.");
      }

      if (userDefinedSuite) {
        caseIDListStrs = Util.getCaseIDStringArrayByCaseName(caseNameList);
      }

    } else {
      if (!args[0].equalsIgnoreCase("help")) {
        usage("Error: wrong arguments.");
      } else {
        usage("");
      }
      code = ERROR;
    }
    return code;
  }

  private void printList(List<String> list) {
    if (list == null) {
      return;
    }
    for (int i = 0; i < list.size(); i++) {
      System.out.println("\t" + list.get(i).toString());
    }
  }

  private List<String> getCaseNames() throws SQLException {
    DataBase db = DataBase.getInstance();
    String query = "select * from test_cases where casename like '" + testSet + "%'";
    List<String> names = db.executeQuery(query, "casename");
    return names;
  }

  private boolean userConfirm() {
    return userConfirm("Continue?");
  }

  private boolean userConfirm(String question) {
    String[] options = {"yes", "no"};
    int code = userConfirm(question, options);

    return code == 0;
  }

  private int userConfirm(String question, String... options) {
    Scanner scanner = new Scanner(System.in);
    String selection = options[0];
    for (int i = 1; i < options.length; i++) {
      selection += "/" + options[i];
    }
    List<String> optionList = Arrays.asList(options);
    String msg = "\n" + question + " (" + selection + "): ";
    System.out.print(msg);
    String response = scanner.nextLine();

    while (!optionList.contains(response)) {
      System.out.print("\nWrong response \"" + response + "\". " + msg);
      response = scanner.nextLine();
    }

    return optionList.indexOf(response);
  }

//	private void requestStop() throws JMSException {
//		ConnectionTool.connect();
//		ProducerTool producer =ProducerTool.getInstance();
//		ProducerTool.logger=logger;
//		producer.connect(TestProperty.get("mq.monitor.queue"));
//		String[]msgProperty={"type=stop","runningID="+msgID};
//	}

  private void build() throws JMSException, IOException {
    ConnectionTool.connect();

    if (TestProperty.get("build.project").matches("rft\\+selenium\\+ormsclient")) {
      if (Util.isBuildInProgress()) {
        syncBuildingProcess();
        System.out.println("\nAnother building process is in progress now. Quit.\n");
        return;
      } else {
        Util.setBuilding(true);
      }
    }

    ProducerTool producer = ProducerTool.getInstance();
    ConsumerTool consumer = ConsumerTool.getInstance();
    ProducerTool.logger = logger;
    ConsumerTool.logger = logger;
    String msgID = (new SimpleDateFormat("yyyyMMddhhmmss")).format(Calendar.getInstance().getTime()).toString();


    producer.connect(TestProperty.get("mq.monitor.queue"));
    String[] msgProperty = {"type=build", "buildtype=" + TestProperty.get("build.type"), "project=" + TestProperty.get("build.project"), "replyto=" + TestProperty.get("mq.driver.topic"), "replytype=topic", "msgID=" + msgID};
    Message msg = producer.createTextMessage("Update and build", msgProperty);
//		msg.setBooleanProperty("legacy", legacy);
    producer.produceMessage(msg);
    producer.disconnect();

    System.out.println("\nWaiting for building process to finish......\n");

    boolean got = false;
    boolean isTopic = true;
    while (!got) {
      Message reply = consumer.consumeMessage(TestProperty.get("mq.driver.topic"), isTopic);
      String aMsgID = reply.getStringProperty("msgID");
      if (reply != null && aMsgID != null && aMsgID.equalsIgnoreCase(msgID)) {
        got = true;
        System.out.println("\nBuild result:\n");
        System.out.println(((TextMessage) reply).getText());
      }
    }
  }

  private void cleanTestSuites() throws JMSException {
    String runningIDs = TestProperty.get("testsuite.ids");
    System.out.println("Warning: it is going to remove all submitted test suites with id=" + runningIDs);
    if (!userConfirm()) {
      System.out.println("\nQuit CLEAN command!");
    } else {
      ConnectionTool.connect();
      ProducerTool producer = ProducerTool.getInstance();
      producer.connect(TestProperty.get("mq.monitor.queue"));

      String[] msgProperty = {"type=clean"};

      Message msg = producer.createTextMessage(runningIDs, msgProperty);
      producer.produceMessage(msg);
      producer.disconnect();
    }
  }

  private void syncBuildingProcess() throws JMSException, IOException {
    ProducerTool producer = ProducerTool.getInstance();
    producer.connect(TestProperty.get("mq.monitor.queue"));

    String[] msgProperty = {"type=buildsync"};
    Message msg = producer.createTextMessage("Sync building status", msgProperty);
    producer.produceMessage(msg);
    producer.disconnect();
  }

  /**
   * Query the status of all test sets submitted and ongoing
   *
   * @throws JMSException
   * @throws IOException
   */
  private void queryStatus(String type) throws JMSException, IOException {
    ConnectionTool.connect();
//		if(Util.isBuildInProgress()) {
//			syncBuildingProcess();
//			System.out.println("\nA building process is in progress right now.\n");
//			return;
//		}

    ProducerTool producer = ProducerTool.getInstance();
    ConsumerTool consumer = ConsumerTool.getInstance();
    ProducerTool.logger = logger;
    ConsumerTool.logger = logger;
    String queryID = Util.getQueryID();
    int timeout = Integer.parseInt(TestProperty.get("testdriver.query.timeout"));

    CheckMessage checkMessage = new CheckMessage(consumer, timeout * 1000, queryID);
    checkMessage.start();

    producer.connect(TestProperty.get("mq.monitor.queue"));
    String[] msgProperty = {"type=" + type, "replyto=" + TestProperty.get("mq.driver.topic"), "replytype=topic", "queryID=" + queryID};
    Message msg = producer.createTextMessage("Query status", msgProperty);
    producer.produceMessage(msg);
    producer.disconnect();
    int count = 0;
    while (!checkMessage.got && count < timeout) {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }

      count++;
    }

    System.out.println();
    if (checkMessage.got) {
      System.out.println(checkMessage.result);
    } else if (count >= timeout) {
      System.out.println("Timed out. Failed to get result");
    } else {
      System.out.println("Failed to get result due to:" + checkMessage.result);
    }
    System.out.println();
  }

  /**
   * Reset the test case status in database
   *
   * @param fromStatus - old status
   * @param toStatus   - new status
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  private void resetTestStatus(int fromStatus, int toStatus) throws SQLException, ClassNotFoundException {
    DataBase db = DataBase.getInstance();
    String query = "";
    if (userDefinedSuite) {
      for (int i = 0; i < caseIDListStrs.length; i++) {
        query = "update test_cases set " + env + "_status=" + toStatus + " where id in (" + caseIDListStrs[i] + ") and " + env + "_status=" + fromStatus;
        db.executeUpdate(query);
      }
    } else {
      query = "update test_cases set " + env + "_status=" + toStatus + " where casename like '" + testSet + "%' and " + env + "_status=" + fromStatus;
      db.executeUpdate(query);
    }
  }

  /**
   * Reset the test cases status which are not FINISH to NA in database
   *
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  private void resetTestStatus() throws SQLException, ClassNotFoundException {
    DataBase db = DataBase.getInstance();
    String query = "";
    if (userDefinedSuite) {
      for (int i = 0; i < caseIDListStrs.length; i++) {
        query = "update test_cases set " + env + "_status=" + EXECUTION_NA + " where casename in (" + caseIDListStrs[i] + ") and " + env + "_status !=" + EXECUTION_FINISH;
        db.executeUpdate(query);
      }
    } else {
      query = "update test_cases set " + env + "_status=" + EXECUTION_NA + " where casename like '" + testSet + "%' and " + env + "_status !=" + EXECUTION_FINISH;
      db.executeUpdate(query);
    }
  }

  private void controlTestRunners(String cmd, String runners) throws JMSException {
    ConnectionTool.connect();
    System.out.println("Warning: it is going to execute [" + cmd + "] to test runner(s): " + runners);
    if (!userConfirm()) {
      System.out.println("\nQuit executing [" + cmd + "] in test runner(s)!");
    } else if (cmd.equalsIgnoreCase("start")) {
      if (runners.equalsIgnoreCase("all")) ;
      runners = TestProperty.get("test.runner");

      String[] rs = runners.split(",");
      List<String> ips = new ArrayList<String>();

      for (String runner : rs) {
        String ip = TestProperty.get("test." + runner.toLowerCase() + ".ip");
        if (ip != null && ip.length() > 0) {
          ips.add(ip);
        } else {
          logger.info("Testrunner '" + runner + "' is not registered. Skipped.");
        }
      }

      startTestRunnerViaDaemon(ips);
    } else {
      ProducerTool producer = ProducerTool.getInstance();

      producer.connect(TestProperty.get("mq.runner.topic"), true);

      String[] msgProperty1 = {"type=" + cmd, "node=" + runners};
      Message msg = producer.createTextMessage("Query status", msgProperty1);
      producer.produceMessage(msg);
      producer.disconnect();
      System.out.println("\nCommand " + cmd.toUpperCase() + " test runner(s) " + runners + " is submitted.");
      if (!cmd.equalsIgnoreCase("resume")) {
        System.out.println("If a script runner is in middle of execution, it will continue and finish before " + cmd.toUpperCase() + " is executed.");
      }
    }
  }

  private void startTestRunnerViaDaemon(List<String> ips) {
    int port = Integer.parseInt(TestProperty.get("testagent.daemon.port", "4445"));
    RPCClient.rpc(port, "startrunner", ips);
  }

  private void reloadProperties() throws JMSException {
    ConnectionTool.connect();
    ProducerTool producer = ProducerTool.getInstance();

    producer.connect(TestProperty.get("mq.runner.topic"), true);
    String[] msgProperty1 = {"type=reload", "node=all"};
    Message msg = producer.createTextMessage("Reload properties", msgProperty1);
    producer.produceMessage(msg);
    producer.disconnect();

    producer.connect(TestProperty.get("mq.monitor.queue"));
    msg = producer.createTextMessage("Reload properties", msgProperty1);
    producer.produceMessage(msg);
    producer.disconnect();

  }

  private void sendResult() throws SQLException {
    StringBuffer failed = new StringBuffer();
    StringBuffer passed = new StringBuffer();
    int failedCount = 0;
    int passedCount = 0;
    String query = "";

    if (individual) {
      String[] tokens = TestProperty.get("cases").split(",");

      query = "select casename, " + env + "_result, " + env + "_status, " + env + "_running_id from test_cases where casename in (" + Util.arrayToString(tokens, true, ",") + ") and " + env + "_active=" + ACTIVE;
    } else {
      query = "select casename, " + env + "_result, " + env + "_status, " + env + "_running_id from test_cases where casename like ('" + testSet + "%') and " + env + "_active=" + ACTIVE;
    }

    DataBase db = DataBase.getInstance();

    if (failedOnly) {
      query += " and " + env + "_result !=" + RESULT_PASSED;
    }

    String[] colNames = new String[]{"casename", env + "_result", env + "_running_id"};
    List<String[]> resultList = db.executeQuery(query, colNames);

    for (int i = 0; i < resultList.size(); i++) {
      String caseName = resultList.get(i)[0];
      int runResult = Integer.parseInt(resultList.get(i)[1]);
      String time = "";
      if (runResult < 3) {
        String t = resultList.get(i)[2];
        if (t == null || t.length() < 1) {
          time = "unknown";
        } else {
          time = Util.parseTime(t);
        }
      }
      StringBuffer buf;
      if (runResult == RESULT_PASSED) {
        buf = passed;
        passedCount++;
      } else {
        buf = failed;
        failedCount++;
      }
      buf.append(caseName);
      buf.append("   ---   ");
      String textResult;
      switch (runResult) {
        case RESULT_FAILED:
          textResult = "FAILED";
          break;
        case RESULT_PASSED:
          textResult = "PASSED";
          break;
        default:
          textResult = "NOT EXECUTED";
      }

      buf.append(textResult);
      if (time.length() > 0) {
        buf.append(" (submitted at ");
        buf.append(time);
        buf.append(")");
      }

      buf.append("\n");

    }

    int total = failedCount + passedCount;

    if (total < 1) {
      System.out.println("\nThere were no test cases found.\n\n");
    } else {
      Email mail = new Email();
      mail.from = TestProperty.get("mail.from", "noreply@reserveamerica.com");
      mail.to = TestProperty.get("mail.to", "AOQAAutomation@activenetwork.com");

      if (mail.to.matches(".+@null")) {
        mail.to = "AOQAAutomation@activenetwork.com";
      }

      mail.subject = testSet;

      if (individual) {
        mail.subject += " test result for some test cases for " + env;
      } else if (failedOnly) {
        mail.subject += " failed test cases summary for " + env;
      } else {
        mail.subject += " test result summary for " + env;
      }

      if (individual) {
        mail.text += "Summary of test result in " + env + "for test cases specified\r\n";
      } else if (failedOnly) {
        mail.text += "Summary of all failed test cases for " + testSet + " in " + env + "\r\n";
      } else {
        mail.text += "Summary of test result for " + testSet + " in " + env + "\r\n";
      }

      if (failedOnly) {
        mail.text += "\nTotal " + total + " failed test case(s):\r\n";
      } else {
        mail.text += "\nTotal " + total + " test case(s):\r\n";

        if (failedCount < 1 && passedCount > 0)
          mail.text += "All test cases in the test suite PASSED!\r\n";
        else {
          if (failedCount > 0)
            mail.text += "-- " + failedCount + " test case(s) FAILED or NOT EXECUTED.\r\n";
          if (passedCount > 0)
            mail.text += "-- " + passedCount + " test case(s) PASSED.\r\n";
        }
      }

      mail.text += "---------------------------------\r\n";

      if (failedCount > 0) {
        if (!failedOnly) {
          mail.text += "Failed cases:\r\n\r\n";
        }
        mail.text += failed.toString();
        mail.text += "---------------------------------\r\n";
      }
      if (passedCount > 0 && !failedOnly) {
        mail.text += "Passed cases:\r\n\r\n";
        mail.text += passed.toString();
        mail.text += "---------------------------------\r\n";
      }

      //send email
      try {
        mail.bodyFormat = "text";
        mail.send();
      } catch (Throwable e) {
        TestMonitor.logger.error("Failed to send email due to Excepton/error: " + e.getMessage(), e);
        TestMonitor.logger.info("Test result: \n" + mail.text);
      }
      logger.info("Email is sent to " + mail.to);
    }
  }


  private void submitSetupScripts() throws Exception {
    List<String> scriptlist = new ArrayList<String>();
    StringBuffer scriptinfo = new StringBuffer();

    DataBase db = DataBase.getInstance();
    String query = "select * from test_setupscripts where active = 1";
    if (individual)
      query += " and script_name in(" + Util.listToString(caseNameList, true, ",") + ")";
    else
      query += " and set_id=" + setValue + " and script_name like '" + testSet + "%'";

    String[] cols = new String[]{"id", "script_name", "table_name", "notes", "tool", "threshold"};
    List<String[]> results = db.executeQuery(query, cols);

    if (results.size() < 1) {
      if (individual)
        System.out.println("There is no setup scripts defined for " + Util.listToString(caseNameList, true, ","));
      else
        System.out.println("There is no setup scripts defined for Set#" + setValue);
      System.exit(0);
    }

    for (int i = 0; i < results.size(); i++) {
      String scriptid = results.get(i)[0];
      String scriptname = results.get(i)[1];
      String tablename = results.get(i)[2];
      String notes = results.get(i)[3];
      int dbtool = Integer.parseInt(results.get(i)[4]);
      int defaultTool = Integer.parseInt(TestProperty.get("default.toolcode"));
      int toolcode = (tool == 0) ? (dbtool == 0 ? defaultTool : dbtool) : tool;
      int threshold = Integer.parseInt(results.get(i)[5]);

      if (Util.isEmpty(tablename)) {
        String cmdStr = scriptname + " env=" + env + ":cmdLine=true:tool=" + toolcode + ":branch=0:branchTotal=0"
            + ":scriptid=" + scriptid + ":tablename=" + tablename + ":notes=\"" + notes + "\":size=0"
            + ":ids=" + ":failedonly=" + failedOnly;
        scriptlist.add(cmdStr);
        scriptinfo.append(scriptid + ";;");
        scriptinfo.append("|");
        continue;
      }

      if (newSetup)
        Util.resetResultForDataTable(tablename, env, TestConstants.RESULT_NA);

      List<String> idlist = new ArrayList<String>();

      query = "select id from " + tablename + " where ";
      if (testSuiteValue == 1) {//submit all records required by sanity test
        query += "testsuite like '%anity%' and ";
      } else if (testSuiteValue == 2) {//submit all records required by regression test
        query += "(testsuite is null or testsuite <> 'sanity') and ";
      }
      query += "(" + env + "_result is null or " + env + "_result != " + TestConstants.RESULT_PASSED + ")";
//			query="select id from "+tablename+" where ("+env+"_result is null or "+env+"_result != "+TestConstants.RESULT_PASSED+")";
      logger.debug("Query from " + tablename + ":" + query);

      idlist = db.executeQuery(query, "id");
      if (idlist == null || idlist.size() < 1) {
        if (newSetup)
          System.out.println("There is no setup data available for Set#" + setValue + " in Table " + tablename);
        else
          System.out.println("There is no failed setup data available for Set#" + setValue + " in Table " + tablename);

        continue;
      }

      List<String[]> subArray = Util.subArray(idlist, threshold);
      for (int j = 0; j < subArray.size(); j++) {
        String[] idArray = subArray.get(j);
        String ids = Util.arrayToString(idArray, false, ",");
        String cmdStr = scriptname + " env=" + env + ":cmdLine=true:tool=" + toolcode + ":branch=0:branchTotal=0"
            + ":scriptid=" + scriptid + ":tablename=" + tablename + ":notes=\"" + notes + "\":size=" + idArray.length
            + ":ids=" + ids + ":failedonly=" + failedOnly;
        scriptlist.add(cmdStr);
        scriptinfo.append(scriptid + ";" + tablename + ";" + ids);
        scriptinfo.append("|");
      }
    }

    if (scriptlist == null || scriptlist.size() < 1) {
      System.out.println("There is no record need to setup.");
      System.exit(0);
    }

    String output = " supportscript \"" + testSet + "\" for " + env + " environment.";

    if (individual) {
      output = " " + Util.listToString(caseNameList, true, ",") + " in" + output;
    }

    if (newSetup) {
      output = "Starting all records in" + output;
    } else {
      output = "Re-running failed records in" + output;
    }

    System.out.println(output);
    System.out.println("Running results will be sent to email: " + TestProperty.get("mail.to"));

    ConnectionTool.connect();
    ProducerTool producer = ProducerTool.getInstance();
    ProducerTool.logger = logger;

    String runningId = Util.createRunningID();
    String monitorQueue = TestProperty.get("mq.monitor.queue");
    String testQueue = TestProperty.get("mq.test.queue");
    String emailto = TestProperty.get("mail.to");

    int total = scriptlist.size();

    //inform monitor
    producer.connect(monitorQueue);
    int repeat = Integer.parseInt(TestProperty.get("repeat", "0"));

    String[] msgProperty1 = {"type=head", "env=" + env, "testSet=" + testSet, "runningId=" + runningId, "total=" + total,
        "email=" + emailto, "repeat=" + repeat, "runners=" + TestProperty.get("runners")};
    //message content:
    //scriptinfo('scriptid+";"+tablename+";"+totalrecords','scriptid+";"+tablename+";"+totalrecords',...):recordslist('id1;id2;...','id1;id2;...')
    Message msg = producer.createTextMessage(scriptinfo.toString() + ":", msgProperty1);
    msg.setBooleanProperty("failedOnly", failedOnly);
    producer.produceMessage(msg);
    producer.disconnect();

    //submit test case messages
    producer.connect(testQueue);

    Long exetime = Calendar.getInstance().getTimeInMillis();
    for (int i = 0; i < scriptlist.size(); i++) {
      String cmdStr = scriptlist.get(i);
//			System.out.println("cmdStr:"+cmdStr);
      //scriptname+" env="+env+":cmdLine=true:tool="+toolcode+":branch=0:branchTotal=0:scriptid="+scriptid+":tablename="+tablename+":rulename="+rulename+":size="+idArray.size()+":ids="+ids+":failedonly="+failedOnlye;
      String[] tokens = cmdStr.split(" ");
      String scriptname = tokens[0];
      String args = cmdStr.replaceFirst(scriptname, "");
      String scriptid = Util.getAttribute(args, "scriptid");
      int size = Integer.parseInt(Util.getAttribute(args, "size"));
      int toolcode = Integer.parseInt(Util.getAttribute(args, "tool"));
      String tablename = Util.getAttribute(args, "tablename");
      String ids = Util.getAttribute(args, "ids");

      String[] msgProperty2 = {"env=" + env, "monitor=" + TestProperty.get("mq.monitor.queue"), "runningId=" + runningId,
          "debug=" + TestProperty.get("debug", "false"), "draft=" + TestProperty.get("draft", "false"),
          "appcode=" + WEB, "toolcode=" + toolcode, "runners=" + TestProperty.get("runners"),
          "scriptid=" + scriptid, "tablename=" + tablename, "ids=" + ids
      };
      Message msg2 = producer.createTextMessage(cmdStr, msgProperty2);
      msg2.setIntProperty("branchTotal", 0);
      msg2.setIntProperty("branch", 0);
      msg2.setLongProperty("exetime", exetime);
//    		msg2.setBooleanProperty("legacy", legacy); //flag for legacy functest3 framework
      producer.produceMessage(msg2);

      logger.info("Submit setup script:" + scriptname + ". Total " + size + " records.");
    }

    producer.disconnect();
    logger.info("Total " + total + " setup scripts.");
  }

  /*
     * Parse test case info and construct command
     * @return - a list of test cases to be run
     */
  public List<String> getTestCases() throws SQLException, ClassNotFoundException {
    List<String> cases = new ArrayList<String>();
    String caseName = "", testcaseId = "", individualnames = "";
    StringBuffer caseList = new StringBuffer();
    StringBuffer multiList = new StringBuffer();
    String runningId = Util.createRunningID();
    int multiCasesSize = 0;
    boolean forced = Boolean.parseBoolean(TestProperty.get("force")); //this flag will force the test suite to be submitted
    boolean draft = Boolean.parseBoolean(TestProperty.get("draft"));
    boolean draftonly = Boolean.parseBoolean(TestProperty.get("draft.only"));

    DataBase db = DataBase.getInstance();

    try {
      String query;

      if (!forced) { //verify if the given test suite is a subset of a test suite which has been submitted and is in progress right now
        int total = this.getTestCaseInProgress();
        if (total > 0) {
          logger.info("There are " + total + " test cases in test suite \"" + testSet + "\" are in progress right now, please submit after all finished.");
          return null;
        }
      }

      //retrieve all test cases for the given test suite
      List<String[]> resultList = this.getTestCasesInfo();

      for (int i = 0; i < resultList.size(); i++) {
        caseTotalBranch = 0;//default value
        testcaseId = resultList.get(i)[0];
        caseName = resultList.get(i)[1];
        int dbTool = Integer.parseInt(resultList.get(i)[2]);
        int defaultTool = Integer.parseInt(TestProperty.get("default.toolcode"));
        //if tool==0, no cmd specific tool, use dbTool
        //if dbTool==0, use default tool
        int toolCode = (tool == 0) ? (dbTool == 0 ? defaultTool : dbTool) : tool;

        //for store manager and flex test cases, should be run under EFT
        if (caseName.matches(TestProperty.get("StoreManagerCases_Format")))
          toolCode = RFT;

        if (caseName.matches(TestProperty.get("FlexCases_Format")))
          toolCode = RFT;

        String multiValue = resultList.get(i)[3];
        if (multiValue != null && multiValue.length() > 0)
          caseTotalBranch = Integer.parseInt(multiValue);

        int appcode = Util.getAppCodeByCaseName(caseName, caseTotalBranch);

        if (caseTotalBranch > 1)
          multiCasesSize++;//count test cases which have branch

        caseList.append(testcaseId);
        caseList.append(",");
        multiList.append(caseTotalBranch);
        multiList.append(",");

        if (priority == UNKNOWN_PRIORITY) {
          priority = caseName.indexOf(".regression.") >= 0 ? NORMAL_PRIORITY : SANITY_PRIORITY;
        }

        String cmdStr = caseName + " env=" + env + ":cmdLine=true:runningId=" + runningId + ":testcaseId=" + testcaseId + ":tool=" + toolCode + ":appcode=" + appcode
            + ":branch=0:branchTotal=" + caseTotalBranch + ":priority=" + priority;

        if (TestProperty.get("debug").equalsIgnoreCase("True")) {
          cmdStr += ":debug=true";
        }
        cases.add(cmdStr);
      }

      if (cases.size() < 1) {
        logger.info("There is no test case available matching the given test suite: \"" + testSet + "\" with the given options.");
        return null;
      }

      //update test cases' status to SUBMITTED
      if (individual) {
        query = "update test_cases set " + env + "_running_id=" + runningId + "," + env + "_status=" + EXECUTION_SUBMITTED + " where casename in (" + Util.listToString(caseNameList, true, ",") + ")" + " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
        db.executeUpdate(query);
      } else if (userDefinedSuite) {
        for (String caseIDListStr : caseIDListStrs) {
          query = "update test_cases set " + env + "_running_id=" + runningId + "," + env + "_status=" + EXECUTION_SUBMITTED + " where id in (" + caseIDListStr + ") " +
              " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
          if (failedOnly) {
            query = "update test_cases set " + env + "_running_id=" + runningId + "," + env + "_status=" + EXECUTION_SUBMITTED + " where id in (" + caseIDListStr + ")" +
                " and " + env + "_result!='" + RESULT_PASSED + "' and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
          }
          db.executeUpdate(query);
        }
      } else {
        query = "update test_cases set " + env + "_running_id=" + runningId + "," + env + "_status=" + EXECUTION_SUBMITTED + " where casename like '" + testSet + "%'" +
            " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
        if (failedOnly) {
          query = "update test_cases set " + env + "_running_id=" + runningId + "," + env + "_status=" + EXECUTION_SUBMITTED + " where casename like '" + testSet + "%'" +
              " and " + env + "_result!='" + RESULT_PASSED + "' and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
        } else if (individual) {
          individualnames = "(" + individualnames.trim().replaceAll(" ", ",") + ")";
          query = "update test_cases set " + env + "_running_id=" + runningId + "," + env + "_status=" + EXECUTION_SUBMITTED + " where casename in " + individualnames +
              " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
        }
        db.executeUpdate(query);
      }

      cases.add(caseList.toString());
      cases.add(multiList.toString());
      cases.add(runningId);
      cases.add(String.valueOf(multiCasesSize));
    } finally {
      db.disconnect();
    }
    return cases;
  }

  /**
   * get total number of test cases which is in progress
   *
   * @return
   */
  private int getTestCaseInProgress() {
    int total = 0;

    boolean draft = Boolean.parseBoolean(TestProperty.get("draft"));
    boolean draftonly = Boolean.parseBoolean(TestProperty.get("draft.only"));

    DataBase db = DataBase.getInstance();
    String query = "";

    if (individual) {
      query = "select count(*) as total from test_cases where casename in (" + Util.listToString(caseNameList, true, ",") + ")" +
          " and (" + env + "_status='" + EXECUTION_RUNNING + "' or " + env + "_status='" + EXECUTION_SUBMITTED + "' or " + env + "_status='" + EXECUTION_STOP + "' or " + env + "_status='" + EXECUTION_HIBERNATED + "') " +
          " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
      List<String> totalList = db.executeQuery(query, "total");
      total = Integer.parseInt(totalList.get(0));
    } else if (userDefinedSuite) {
      for (String caseIDListStr : caseIDListStrs) {
        query = "select count(*) as total from test_cases where casename in (" + caseIDListStr + ") " +
            " and (" + env + "_status='" + EXECUTION_RUNNING + "' or " + env + "_status='" + EXECUTION_SUBMITTED + "' or " + env + "_status='" + EXECUTION_STOP + "' or " + env + "_status='" + EXECUTION_HIBERNATED + "') " +
            " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
        List<String> totalList = db.executeQuery(query, "total");
        total = total + Integer.parseInt(totalList.get(0));
      }
    } else {
      query = "select count(*) as total from test_cases where casename like '" + testSet + "%'" +
          " and (" + env + "_status='" + EXECUTION_RUNNING + "' or " + env + "_status='" + EXECUTION_SUBMITTED + "' or " + env + "_status='" + EXECUTION_STOP + "' or " + env + "_status='" + EXECUTION_HIBERNATED + "') " +
          " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
      List<String> totalList = db.executeQuery(query, "total");
      total = Integer.parseInt(totalList.get(0));
    }

    return total;
  }

  /**
   * Look up Test Set in database and fetch the list of associated cases info
   *
   * @return
   */
  private List<String[]> getTestCasesInfo() {

    boolean draft = Boolean.parseBoolean(TestProperty.get("draft"));
    boolean draftonly = Boolean.parseBoolean(TestProperty.get("draft.only"));

    DataBase db = DataBase.getInstance();
    String query = "";
    List<String[]> resultList = new ArrayList<String[]>();
    String[] colName = new String[]{"id", "casename", env + "_tool", "multi"};

    if (individual) {
      query = "select * from test_cases where casename in (" + Util.listToString(caseNameList, true, ",") + ")" + " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);
      resultList.addAll(db.executeQuery(query, colName));
    } else if (userDefinedSuite) {
      for (String caseIDListStr : caseIDListStrs) {
        query = "select * from test_cases where id in (" + caseIDListStr + ") and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);

        if (failedOnly)
          query = "select * from test_cases where id in (" + caseIDListStr + ") and " + env + "_result!='" + RESULT_PASSED + "'" +
              " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);

        resultList.addAll(db.executeQuery(query, colName));
      }
    } else {
      query = "select * from test_cases where casename like '" + testSet + "%'" + " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);

      if (failedOnly)
        query = "select * from test_cases where casename like '" + testSet + "%'" + " and " + env + "_result!='" + RESULT_PASSED + "'" +
            " and " + env + "_active " + (draft ? (draftonly ? "=" + DRAFT : "!=" + INACTIVE) : "=" + ACTIVE);

      resultList.addAll(db.executeQuery(query, colName));
    }

    return resultList;
  }

  private List<String> getPrdSanityTestCases() {
    List<String> scriptlist = new ArrayList<String>();
    StringBuffer scriptinfo = new StringBuffer();

    DataBase db = DataBase.getInstance();
    String query = "select * from test_production where script_name like '" + testSet + "%'";
    String update = "update test_production set " + env + "_result=" + RESULT_NA + " where script_name like '" + testSet + "%'";

    //begin: added by pzhu for sanity only when production restart: AUTO-1494
    if (productionRestart) {
      query = "select * from test_production where script_name like '" + testSet + "%' and sanity_type <> " + PRODUCTION_NONE;
      update = "update test_production set " + env + "_result=" + RESULT_NA + " where script_name like '" + testSet + "%' and sanity_type <>" + PRODUCTION_NONE;
    }
    //end

    if (failedOnly) {
      query += " and " + env + "_result!=" + RESULT_PASSED;
      update += " and " + env + "_result!=" + RESULT_PASSED;
    }

    String[] cols = new String[]{"id", "script_name", "table_name", "tool", "threshold", "sanity_type"};//updated by pzhu: AUTO-1494
    List<String[]> results = db.executeQuery(query, cols);

    if (results.size() < 1) {
      System.out.println("There is no test case available matching the given test suite: \"" + testSet + "\" with the given options.");
      System.exit(0);
    }

    //reset result for production sanity test cases
    db.executeUpdate(update);

    for (int i = 0; i < results.size(); i++) {
      String scriptid = results.get(i)[0];
      String scriptname = results.get(i)[1];
      String tablename = results.get(i)[2];
      int dbtool = Integer.parseInt(results.get(i)[3]);
      int defaultTool = Integer.parseInt(TestProperty.get("default.toolcode"));
      int toolcode = (tool == 0) ? (dbtool == 0 ? defaultTool : dbtool) : tool;
      int threshold = Integer.parseInt(results.get(i)[4]);
      int sanityType = Integer.parseInt(results.get(i)[5]);//added by pzhu: AUTO-1494

      if (Util.isEmpty(tablename)) {
        String cmdStr = scriptname + " env=" + env + ":cmdLine=true:tool=" + toolcode + ":branch=0:branchTotal=0"
            + ":scriptid=" + scriptid + ":tablename=" + tablename + ":size=0"
            + ":ids=" + ":failedonly=" + failedOnly;
        scriptlist.add(cmdStr);
        scriptinfo.append(scriptid + ";;");
        scriptinfo.append("|");
        continue;
      }

      List<String> idlist = new ArrayList<String>();

      //begin: added by pzhu: AUTO-1494
      //If 'sanityType' of table is 'PRODUCTION_PARTIAL', we fetch all records 'fetch_type' of which are 'DATA_FETCH_MANDATORY',
      //and random records 'fetch_type' of which are 'DATA_FETCH_RANDOM'.
      //we can use parameter 'dataFetchRadomNum' to limit random size.
      if ((productionRestart) && (sanityType == PRODUCTION_PARTIAL)) {
        String cond = "";
        if (failedOnly) {
          cond = " and (" + env + "_result is null or " + env + "_result != " + TestConstants.RESULT_PASSED + ")";
        }
        String unionTable = " ((select * from " +
            tablename +
            " where fetch_type=" +
            DATA_FETCH_MANDATORY +
            cond +
            ") union all (select * FROM " +
            tablename +
            " where fetch_type=" +
            DATA_FETCH_RANDOM +
            cond +
            " ORDER BY rand() LIMIT " +
            dataFetchRandomNum +
            "))as o ";
        query = "select id from " + unionTable;

      } else {      //end
        query = "select id from " + tablename;

        if (failedOnly)
          query += " where (" + env + "_result is null or " + env + "_result != " + TestConstants.RESULT_PASSED + ")";
      }

      logger.debug("Query from " + tablename + ":" + query);

      idlist = db.executeQuery(query, "id");
      if (idlist == null || idlist.size() < 1) {
        logger.debug("No failed records for script \"" + scriptname + "\" in table " + tablename);
        continue;
      }

      String idStr = Util.listToString(idlist, false, ",");
      //reset result for data table
      Util.resetResultForDataTable(tablename, idStr, env, RESULT_NA);

      List<String[]> subArray = Util.subArray(idlist, threshold);
      for (int j = 0; j < subArray.size(); j++) {
        String[] idArray = subArray.get(j);
        String ids = Util.arrayToString(idArray, false, ",");
        String cmdStr = scriptname + " env=" + env + ":cmdLine=true:tool=" + toolcode + ":branch=0:branchTotal=0"
            + ":scriptid=" + scriptid + ":tablename=" + tablename + ":size=" + idArray.length
            + ":ids=" + ids + ":failedonly=" + failedOnly;
        scriptlist.add(cmdStr);
        scriptinfo.append(scriptid + ";" + tablename + ";" + ids);
        scriptinfo.append("|");
      }
    }

    scriptlist.add(scriptinfo.toString());

    return scriptlist;
  }

  private void submitPrdSanityTestCases() throws JMSException {
    String output = " testSet \"" + testSet + "\" for " + env + " environment.";

    if (failedOnly) {
      output = "Re-running failed test cases in" + output;
    } else {
      output = "Starting all test cases in" + output;
    }

    System.out.println(output);
    System.out.println("Running results will be sent to email: " + TestProperty.get("mail.to"));


    List<String> scriptlist = this.getPrdSanityTestCases();
    if (scriptlist == null || scriptlist.size() < 1) {
      System.out.println("There is no test case available matching the given test suite: \"" + testSet + "\" with the given options.");
      System.exit(0);
    }
    String scriptinfo = scriptlist.remove(scriptlist.size() - 1).toString();

    ConnectionTool.connect();
    ProducerTool producer = ProducerTool.getInstance();
    ProducerTool.logger = logger;

    String runningId = Util.createRunningID();
    String monitorQueue = TestProperty.get("mq.monitor.queue");
    String emailto = TestProperty.get("mail.to");

    int total = scriptlist.size();

    //inform monitor
    producer.connect(monitorQueue);
    int repeat = Integer.parseInt(TestProperty.get("repeat", "0"));

    String[] msgProperty1 = {"type=head", "env=" + env, "testSet=" + testSet, "runningId=" + runningId, "total=" + total,
        "email=" + emailto, "repeat=" + repeat, "runners=" + TestProperty.get("runners")};
    //message content:
    //scriptinfo(scriptid;tablename;ids|scriptid;tablename;ids)
    Message msg = producer.createTextMessage(scriptinfo.toString() + ":", msgProperty1);
    msg.setBooleanProperty("failedOnly", failedOnly);
    producer.produceMessage(msg);
    producer.disconnect();

    String testQueue = TestProperty.get("mq.test.queue");
    String rftQueue = TestProperty.get("mq.rft.queue");
    //submit test case messages

    Long exetime = Calendar.getInstance().getTimeInMillis();
    for (int i = 0; i < scriptlist.size(); i++) {
      String cmdStr = scriptlist.get(i);
      //scriptname+" env="+env+":cmdLine=true:tool="+toolcode+":branch=0:branchTotal=0:scriptid="+scriptid+":tablename="+tablename+":size="+idArray.size()+":ids="+ids+":failedonly="+failedOnlye;
      String[] tokens = cmdStr.split(" ");
      String scriptname = tokens[0];
      String args = cmdStr.replaceFirst(scriptname, "");
      String scriptid = Util.getAttribute(args, "scriptid");
      int size = Integer.parseInt(Util.getAttribute(args, "size"));
      int toolcode = Integer.parseInt(Util.getAttribute(args, "tool"));
      String tablename = Util.getAttribute(args, "tablename");
      String ids = Util.getAttribute(args, "ids");

      String[] msgProperty2 = {"env=" + env, "monitor=" + TestProperty.get("mq.monitor.queue"), "runningId=" + runningId,
          "debug=" + TestProperty.get("debug", "false"), "draft=" + TestProperty.get("draft", "false"),
          "appcode=" + WEB, "toolcode=" + toolcode, "runners=" + TestProperty.get("runners"),
          "scriptid=" + scriptid, "tablename=" + tablename, "ids=" + ids
      };

      if (toolcode == RFT)
        producer.connect(rftQueue);
      else
        producer.connect(testQueue);

      Message msg2 = producer.createTextMessage(cmdStr, msgProperty2);
      msg2.setIntProperty("branchTotal", 0);
      msg2.setIntProperty("branch", 0);
      msg2.setLongProperty("exetime", exetime);
//    		msg2.setBooleanProperty("legacy", legacy); //flag for legacy functest3 framework
      producer.produceMessage(msg2);
      producer.disconnect();
      logger.debug("Submit test case:" + scriptname + ". Total " + size + " records.");
    }

    logger.info("Total " + total + " cases.");
  }

  static class CheckMessage extends Thread {
    ConsumerTool consumer;
    long timeout;
    String id;
    public String result;
    public boolean got;
    private String topic;

    public CheckMessage(ConsumerTool consumer, long timeout, String id) {
      this.consumer = consumer;
      this.timeout = timeout;
      this.id = id;
      this.got = false;
      this.topic = TestProperty.get("mq.driver.topic");
    }

    public void run() {
      boolean timedout = false;
      boolean haserror = false;
      while (!got && !timedout && !haserror) {
        try {
          Message reply = consumer.consumeMessage(topic, timeout, true, false);
          if (reply == null) {
            System.out.println("\nTimed out. No responds from TestMonitor.");
            timedout = true;
            result = "Timed out";
          } else {
            String reply_queryID = reply.getStringProperty("queryID");
            if (reply_queryID != null && reply_queryID.equalsIgnoreCase(id)) {
              got = true;
//							System.out.println("\nQuery got following result:");
//							System.out.println(((TextMessage)reply).getText());
              result = ((TextMessage) reply).getText();

            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          haserror = true;
          result = e.getMessage();
        }
      }
    }
  }
}
