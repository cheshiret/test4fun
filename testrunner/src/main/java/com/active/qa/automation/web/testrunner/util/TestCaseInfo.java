package com.active.qa.automation.web.testrunner.util;

import com.active.qa.automation.web.testrunner.TestConstants;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by tchen on 1/18/2016.
 */
public class TestCaseInfo {
  private int caseStatus = 0;
  private String caseName = "";
  private String caseID = "";
  private String executionHostName = "";
  private String executionHostIp = "";
  private long startTime = 0;
  private int testResult = TestConstants.RESULT_NA;
  private int tool = 0;
  private int executionTime = 0;
  private int branchTotoal = 0;
  private int branch = 0;
  private String caseOwner = "";
  //below parameters were used for support script
  private int totalRecords = 0;
  private String recordTable = "";
  private String records = "";

  public Set<String> inprogress = new HashSet<String>(); //records are in progress
  public Set<String> finishedList = new HashSet<String>(); //records finished
  public Set<String> unknownList = new HashSet<String>(); //records unknown

  public void setCaseName(String caseName) {
    this.caseName = caseName;
  }

  public String getCaseID() {
    return this.caseID;
  }

  public void setCaseID(String caseID) {
    this.caseID = caseID;
  }

  public String getCaseName() {
    return this.caseName;
  }

  public void setStatus(int status) {
    caseStatus = status;
  }

  public int getStatus() {
    return caseStatus;
  }

  public void setExecutionHostName(String hostName) {
    this.executionHostName = hostName;
  }

  public String getExecutionHostName() {
    return this.executionHostName;
  }

  public void setExecutionHostIP(String ip) {
    this.executionHostIp = ip;
  }

  public String getExecutionHostIP() {
    return this.executionHostIp;
  }

  public int getTool() {
    return tool;
  }

  public void setStartTime() {
    if (startTime == 0) {
      startTime = Calendar.getInstance().getTimeInMillis();
    }
  }

//	public long getStartTime() {
//		return startTime;
//	}

  public void setTestResult(int result) {
    //for initial status, use the result directly;
    //for test case which partially not run, use 'NOT_RUN' as result;
    if (testResult == TestConstants.RESULT_NA || result == TestConstants.RESULT_NOT_RUN)
      this.testResult = result;
    else if (result == TestConstants.RESULT_PASSED && this.testResult == TestConstants.RESULT_PASSED)
      this.testResult = result;
    else
      this.testResult = TestConstants.RESULT_FAILED;
  }

  public int getTestResult() {
    return this.testResult;
  }

  public void setExecutionTime(int time) {
    if (executionTime == 0) {
      executionTime = time;
    }
  }

//	public int getExecutionTime() {
//		return this.executionTime;
//	}

  public int getTimeRemaining() {
    if (startTime == 0 || executionTime == 0) {
      return -1;
    } else {
      long now = Calendar.getInstance().getTimeInMillis();
      int diff = Math.round((now - startTime) / 1000);
      int remain = executionTime - diff;
      if (remain < 0) {
        remain = 0;
      }
      return remain;
    }
  }

  public void setTool(int tool) {
    this.tool = tool;
  }

  public void setBranchTotal(int branchTotal) {
    this.branchTotoal = branchTotal;
  }

  public int getBranchTotal() {
    return branchTotoal;
  }

  public void setBranch(int branch) {
    this.branch = branch;
  }

  public int getBranch() {
    return branch;
  }

  public void setCaseOwner(String caseOwner) {
    this.caseOwner = caseOwner;
  }

  public String getCaseOwner() {
    return this.caseOwner;
  }

  public void setRecords(String records) {
    this.records = records;
  }

  public String getRecords() {
    return this.records;
  }

  public void setRecordTable(String table) {
    this.recordTable = table;
  }

  public String getRecordTable() {
    return this.recordTable;
  }

  public void setTotalRecords(int totalRecords) {
    this.totalRecords = totalRecords;
  }

  public int getTotalRecords() {
    return this.totalRecords;
  }

  public void addIDsIntoList(Set<String> list, String ids) {
    String[] idArray = ids.split(",");
    for (int i = 0; i < idArray.length; i++)
      list.add(idArray[i]);
  }

  public void removeIDsFromList(Set<String> list, String ids) {
    String[] idArray = ids.split(",");
    for (int i = 0; i < idArray.length; i++)
      list.remove(idArray[i]);
  }

  public void removeIDFromList(Set<String> list, String id) {
    list.remove(id);
  }

  public int getRemainingNumber() {
    return unknownList.size();
  }

  public int getInprogressNumber() {
    return inprogress.size();
  }

  public boolean isDone() {
    return getRemainingNumber() == 0 && getInprogressNumber() == 0;
  }
}

