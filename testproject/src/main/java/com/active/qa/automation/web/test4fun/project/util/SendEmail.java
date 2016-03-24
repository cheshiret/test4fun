package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.Email;
import com.active.qa.automation.web.testapi.util.SysInfo;
import com.active.qa.automation.web.testapi.util.TestProperty;

/**
 * @author : tchen
 * @since : 3/22/2016.
 */
public class SendEmail {

  public static void sendEmail(int totalCases, int failedNum, int passedNum, int totalMins,
                               String exception, String passedCases, String failedCases, String notExecuted, String testSuite, String emailAddress) {
    //Email email = new Email();
    AutomationLogger logger = AutomationLogger.getInstance("TestDriver");
    StringBuffer text = new StringBuffer();

    text.append("The test suite is executed on " + SysInfo.getHostName() + "(" + SysInfo.getHostIP() + ")\n\n");
    if (totalCases > 0) {
      text.append("Total " + totalCases + " test case(s).\n");
    } else {
      text.append("There were no test cases found.\n");
    }

    if (failedNum < 1 && exception.length() < 1 && passedCases.length() > 0 && totalCases > 0 && passedNum == totalCases) {
      text.append("All test cases PASSED!\n");
    } else {
      if (exception.length() > 0) {
        if (exception.indexOf("Testmethod running was stopped by user") >= 0) {
          text.append("Testmethod suite were stopped by user.\n");
          exception = "";
        } else {
          text.append("Testmethod suite execution meets exceptions/errors.\n");
        }
      }
      if (failedNum > 0) {
        text.append("-- " + failedNum + " test case(s) FAILED.\n");
      }
      if (passedNum > 0) {
        text.append("-- " + passedNum + " test case(s) PASSED.\n");
      }
      int notRun = totalCases - failedNum - passedNum;
      if (notRun > 0) {
        text.append("-- " + notRun + " test case(s) were not executed.\n");
      }
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
      to += ";" + TestProperty.getProperty("notification.debug.to", "tony.chen@project.com");
    }

    String[] attachments = new String[1];
    attachments[0] = logger.getFullLogFileName();

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
}
