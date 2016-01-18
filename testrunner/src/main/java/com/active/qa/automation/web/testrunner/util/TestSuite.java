package com.active.qa.automation.web.testrunner.util;

import com.active.qa.automation.web.testrunner.ItemNotFoundException;
import com.active.qa.automation.web.testrunner.TestConstants;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by tchen on 1/18/2016.
 */
public class TestSuite implements TestConstants {
    protected static Logger logger = Logger.getLogger(TestSuite.class);
    private boolean failedOnly;
    private int status=TESTSUITE_PENDING;
    private int repeat=0;
    private String runningId="";
    private String testSet="";
    private String env="";
    //	private int tool=0;// 0-na, 1-rft, 2-selenium
    private Set<String> inprogress=new HashSet<String>(); //cases are in progress
    private Set<String> passedList=new HashSet<String>(); //cases passed
    private Set<String> failedList=new HashSet<String>(); //cases failed
    private Set<String> notrunList=new HashSet<String>(); //cases not blocked
    private Set<String> unknownList=new HashSet<String>(); //cases unknown

    private long startTime=0;
    private long endTime=0;

    private String mailto="";
    private String runners="";

    private boolean allTestcasesGetConsumed=false;
    private long time; //expected finish time

    public long getExpectedFinishTime() {
        if(allTestcasesGetConsumed)
            return time;
        else
            return -1;
    }

    public Hashtable<String,TestCaseInfo> caseTable=new Hashtable<String,TestCaseInfo>();

    public void setTestCaseResult(String id, int result, String ids) {
        TestCaseInfo tc=caseTable.get(id);
        if(tc==null) {
            logger.warn("Null: failed to get test case info for caseID="+id);
            return;
        }

        tc.setTestResult(result);

        if(Util.notEmpty(ids)) {
            tc.removeIDsFromList(tc.inprogress, ids);
            tc.addIDsIntoList(tc.finishedList, ids);
            if(!tc.isDone())
                return;
        }

        switch(result) {
            case RESULT_HIBERNATED:
                //the test case will still be inprogress
                break;
            case RESULT_PASSED:
                passedList.add(id);
                inprogress.remove(id);
                break;
            case RESULT_NOT_RUN:
            case RESULT_MEMERROR:
            case RESULT_JVMCRASH:
                notrunList.add(id);
                inprogress.remove(id);
                break;
            case RESULT_FAILED:
            default:
                failedList.add(id);
                inprogress.remove(id);
        }

    }

    public void setTestCaseBranch(String id, int branch) {
        TestCaseInfo tc=caseTable.get(id);
        if(tc==null) {
            logger.warn("Null: failed to get test case info for caseID="+id);
            return;
        }
        tc.setBranch(branch);
    }

    public void start(String id) {
        unknownList.remove(id);
        inprogress.add(id);
    }

    public boolean isFailedOnly() {
        return this.failedOnly;
    }

    public void setRunningID(String id) {
        if(runningId.length()<1) {
            this.runningId=id;
        }
    }

    public void setMailto(String mailto) {
        this.mailto=mailto;
    }

    public String getMailto() {
        return mailto;
    }

    public String getRunningID() {
        return this.runningId;
    }

    public void setTestSuite(String testsuite) {
        if(testSet.length()<1) {
            this.testSet=testsuite;
        }
    }

    public String getTestSuite() {
        return testSet;
    }

    public void setEnv(String env) {
        if(this.env.length()<1) {
            this.env=env;
        }
    }

    public String getEnv() {
        return env;
    }

    public void setFailedOnly(boolean failedonly) {
        this.failedOnly=failedonly;
    }

    public boolean failedOnly() {
        return this.failedOnly;
    }

    private void start() {
        long now=Calendar.getInstance().getTimeInMillis();
        start(now);
    }

    private void start(long time) {
        if(status<TESTSUITE_INPROGRESS) {
            status=TESTSUITE_INPROGRESS;
            startTime=time;

            Util.updateTestExecutionStatus(this.runningId, TESTSUITE_INPROGRESS, startTime, -1);
        }
    }

    public void finish() throws SQLException {
        if(status!=TESTSUITE_FINISH) {
            endTime=Calendar.getInstance().getTimeInMillis();
            if(status==TESTSUITE_PENDING) {
                startTime=endTime;
            }
            status=TESTSUITE_FINISH;
            Util.updateTestExecutionStatus(this.runningId, TESTSUITE_FINISH,-1,endTime);
            Util.setEFTRunning(STATUS_FALSE);
            fillUnknownTestCaseInfo();
        }
    }

    public void finishTestSuiteWithDBTable() throws SQLException {
        if(status!=TESTSUITE_FINISH) {
            endTime=Calendar.getInstance().getTimeInMillis();
            if(status==TESTSUITE_PENDING) {
                startTime=endTime;
            }
            status=TESTSUITE_FINISH;
        }
    }

    public int getExecutionTime() {
        if(status==TESTSUITE_FINISH) {
            int diff=Math.round((endTime-startTime)/1000);

            return diff;
        } else {
            return -1;
        }
    }

    public int getStatusCode() {
        return status;
    }

    public String getStatusInfo() {
        switch(status) {
            case TESTSUITE_PENDING:
                return "PENDING";
            case TESTSUITE_INPROGRESS:
                return "IN_PROGRESS - passed="+passedList.size()+" failed="+failedList.size();
            case TESTSUITE_FINISH:
                return "FINISHED";
            default:
                return "UNKNOWN";
        }
    }

    public int getTimeDiff() {
        long now=Calendar.getInstance().getTimeInMillis();
        int diff=Math.round((now-startTime)/1000);

        return diff;
    }

    public void setRepeat(int repeat) {
        this.repeat=repeat;
    }

    public int getRepeat() {
        return repeat;
    }

    public boolean allTestcasesGotConsumed() {
        return allTestcasesGetConsumed;
    }

    private void fillUnknownTestCaseInfo() throws SQLException {
        if(this.testSet.matches("^(com.activenetwork.qa.awo.)?test(C|c)ases\\.(regression|sanity).*")) {
            if(unknownList.size()>0) {
                DataBase db=DataBase.getInstance();
                List<String> list = new ArrayList<String>();
                list.addAll(unknownList);
                String[] caseNameListStrs = Util.listToStrings(list, true, ",", Integer.parseInt(TestProperty.get("size_limitation")));
                List<String[]> resultList=new ArrayList<String[]>();
                for(int i=0;i<caseNameListStrs.length;i++) {
                    String query="select id,casename,"+env+"_tool from test_cases where id in ("+caseNameListStrs[i]+")";
                    String[] colNames = new String[]{"id","casename",env+"_tool"};
                    resultList.addAll(db.executeQuery(query, colNames));
                }

                for(int i=0;i<resultList.size();i++){
                    String id=resultList.get(i)[0];
                    String caseName=resultList.get(i)[1];
                    int tool=Integer.parseInt(resultList.get(i)[2]);

                    TestCaseInfo tc=caseTable.get(id);
                    tc.setCaseName(caseName);
                    tc.setTool(tool);
                }
            }
        }
    }

    public void createCaseTable(String caseidList) {
        String[] caseIDs=caseidList.split(",");
        createCaseTable(caseIDs);
    }

    public void createCaseTable(String[] caseIDs) {
        if(caseTable.size()<1) {
            caseTable=new Hashtable<String,TestCaseInfo>();

            for(int i=0;i<caseIDs.length;i++) {
                TestCaseInfo tc=new TestCaseInfo();
                caseTable.put(caseIDs[i], tc);
                unknownList.add(caseIDs[i]);
            }
        }
    }

    public void createCaseTable(String[] caseIDs, String[] multiValues) {
        if(caseTable.size()<1) {
            caseTable=new Hashtable<String,TestCaseInfo>();

            for(int i=0;i<caseIDs.length;i++) {
                TestCaseInfo tc=new TestCaseInfo();
                tc.setBranchTotal(Integer.parseInt(multiValues[i]));//0 as default
                caseTable.put(caseIDs[i], tc);
                unknownList.add(caseIDs[i]);
            }
        }
    }

    /**
     * This method was used for create caseTable for setup script and production sanity test cases
     * @param scriptsInfo -- scriptid;tablename;ids
     * @param recordslist
     */
    public void createScriptTable(String[] scriptsInfo) {
        if(caseTable.size()<1) {
            caseTable=new Hashtable<String,TestCaseInfo>();
            for(int i=0;i<scriptsInfo.length;i++) {
                TestCaseInfo tc=new TestCaseInfo();
                String[] scriptinfo = scriptsInfo[i].split(";");
                if(scriptinfo.length>1) {
                    tc.setRecordTable(scriptinfo[1]);
                    tc.setTotalRecords(scriptinfo[2].split(",").length);
                    tc.setRecords(scriptinfo[2]);
                    tc.addIDsIntoList(tc.unknownList, scriptinfo[2]);//Add all records into unknownlist
                } else {
                    tc.setRecordTable("");
                    tc.setTotalRecords(0);
                    tc.setRecords("");
                }

                //create an unique key for this TestCaseInfo by scriptid:tableName:ids
                String key=scriptsInfo[i].replaceAll(";", ":");
                System.out.println("new case table key:"+key);
                caseTable.put(key, tc);
                unknownList.add(key);
            }
        }
    }

    public void createCaseTable(String[] caseIDs, TestCaseInfo[] tcs) {
        if(caseTable.size()<1 && caseIDs.length==tcs.length) {
            caseTable=new Hashtable<String,TestCaseInfo>();

            for(int i=0;i<caseIDs.length;i++) {
                caseTable.put(caseIDs[i], tcs[i]);
            }
        }
    }

    public void setCaseStatus(String caseId,int status) {
        TestCaseInfo tc=caseTable.get(caseId);
        tc.setStatus(status);
    }

    public int getCaseStatus(String caseId) {
        TestCaseInfo tc=caseTable.get(caseId);
        return tc.getStatus();
    }

    public String getTestSuiteStatus() {
        String info="";

        return info;
    }

    public void setExecutionHost(String caseId, String hostName,String ip) {
        TestCaseInfo tc=caseTable.get(caseId);
        tc.setExecutionHostName(hostName);
        tc.setExecutionHostIP(ip);
    }

    public void startTestcase(String caseID,String caseName,String hostName,String ip,int executionTime,int tool,int branchTotal,int branch,String ids) {
        TestCaseInfo tc=caseTable.get(caseID);
        if(tc==null) {
            return;
        }

        tc.setCaseName(caseName);
        tc.setExecutionHostName(hostName);
        tc.setExecutionHostIP(ip);
        tc.setExecutionTime(executionTime);
        tc.setTool(tool);
        tc.setBranchTotal(branchTotal);
        tc.setBranch(branch);
        tc.setStartTime();
        tc.setCaseID(caseID);

        inprogress.add(caseID);
        unknownList.remove(caseID);

        if(Util.notEmpty(ids)){
            tc.addIDsIntoList(tc.inprogress, ids);
            tc.removeIDsFromList(tc.unknownList, ids);
        }

        if(status<TESTSUITE_INPROGRESS) {
            start();
        }
        if(unknownList.size()<1) {
            int remain=getTimeRemaining();
            time=Calendar.getInstance().getTimeInMillis()+remain*1000; //expected finish time
            allTestcasesGetConsumed=true;
        }
    }

    public int getTotal() {
        return caseTable.size();
    }

    public int getPassedNumber() {
        return passedList.size();
    }

    public int getFailedNumber() {
        return failedList.size();
    }

    public int getNotRunNumber() {
        return notrunList.size();
    }

    public int getInprogressNumber() {
        return inprogress.size();
    }

    public int getUnknownNumber() {
        return unknownList.size();
    }

    public int getRemainingNumber() {
        return unknownList.size();
    }

    public boolean isDone() {
        return getRemainingNumber()==0 && getInprogressNumber()==0;
    }

    public int getTimeRemaining() {
        int remain=3600;
        if(allTestcasesGetConsumed) {
            remain=0;
            for(String id:inprogress) {
                TestCaseInfo tc=caseTable.get(id);
                remain=Math.max(remain, tc.getTimeRemaining());
            }
        }

        return remain;
    }

    public boolean allPassed() {
        return getPassedNumber()==getTotal();
    }

    public String[] createResultInfo() throws ItemNotFoundException {
        String[] info=new String[4];

        StringBuffer passed=new StringBuffer();
        StringBuffer failed=new StringBuffer();
        StringBuffer notrun=new StringBuffer();
        StringBuffer unknown=new StringBuffer();
        Enumeration<String> keys=caseTable.keys();
        while(keys.hasMoreElements()) {
            String key=keys.nextElement();
            TestCaseInfo tc=caseTable.get(key);

            String caseName=tc.getCaseName();
            if(caseName==null || caseName.length()<1) {
                caseName="caseid#"+key;
            }

            if(caseName.indexOf("sanity")>=0) {
                String[] tokens=caseName.split("\\.");
                caseName=tokens[tokens.length-1];
            } else if(caseName.indexOf("regression")>=0) {
                caseName=caseName.replaceAll("testCase\\.", "");
            }

            String hostInfo="";
            String hostName=tc.getExecutionHostName();
            String hostIP=tc.getExecutionHostIP();
            String caseOwner=tc.getCaseOwner();
            if(caseOwner.length()<1 && testSet.matches("testCases\\.(regression|sanity).+")) {
                caseOwner=Util.getTestCaseOwnerByCaseID(key);
            }
            if(hostName.length()>0 ){
                hostInfo=hostName;
            }

            if(hostIP.length()>0) {
                if(hostInfo.length()>0) {
                    hostInfo +="("+hostIP+")";
                }else {
                    hostInfo = hostIP;
                }
            }

            switch(tc.getTestResult()) {
                case RESULT_PASSED:
                    passed.append("<tr>\n");
                    passed.append("<td>"+caseName+"</td>\n");
                    passed.append("<td>PASSED");
                    if(hostInfo.length()>0) {
                        passed.append(" in ");
                        passed.append(hostInfo);
                    }
                    passed.append("</td>\n");
                    passed.append("<td>"+caseOwner+"</td>\n");
                    passed.append("</tr>\n");
                    break;
                case RESULT_FAILED:
                    failed.append("<tr>\n");
                    failed.append("<td>"+caseName+"</td>\n");
                    failed.append("<td>FAILED");
                    if(hostInfo.length()>0) {
                        failed.append(" in ");
                        failed.append(hostInfo);
                    }
                    failed.append("</td>\n");
                    failed.append("<td>"+caseOwner+"</td>\n");
                    String exception="unknown";
                    try {
                        exception=Util.getFailedException(this.runningId, key);
                        if(Util.isEmpty(exception) || exception=="null")
                            exception="unknown";
                        else
                            exception=exception.replaceAll("\n", " ").replaceAll("\r", " ");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    failed.append("<td>"+exception+"</td>\n");
                    failed.append("</tr>\n");
                    break;
                case RESULT_NOT_RUN:
                case RESULT_MEMERROR:
                case RESULT_JVMCRASH:
                    notrun.append("<tr>\n");
                    notrun.append("<td>"+caseName+"</td>\n");
//				caseOwner=Util.getTestCaseOwnerByCaseID(key);
                    notrun.append("<td>"+caseOwner+"</td>\n");
                    notrun.append("</tr>\n");
                    break;
                default:
                    unknown.append("<tr>\n");
                    unknown.append("<td>"+caseName+"</td>\n");
                    if(inprogress.contains(key)) {
                        unknown.append("<td>");
                        if(hostInfo.length()>0) {
                            unknown.append(" IN_PROGRESS in ");
                            unknown.append(hostInfo);
                        }
                        int seconds=tc.getTimeRemaining();
                        if(seconds>0) {
                            unknown.append(" may finish in ");
                            unknown.append(seconds);
                            unknown.append(" seconds");
                        }
                        unknown.append("</td>");
                    }

//				caseOwner=Util.getTestCaseOwnerByCaseID(key);
                    unknown.append("<td>"+caseOwner+"</td>\n");
                    unknown.append("</tr>\n");

            }

        }

        info[0]="<table border=\"1\">\n"+
                "<tr>\n"+
                "<th>CaseName</th>\n"+
                "<th>Result</th>\n"+
                "<th>CaseOwner</th>\n"+
                "<th>Exception</th>\n"+
                failed.toString()+
                "</table>\n";
        info[1]="<table border=\"1\">\n"+
                "<tr>\n"+
                "<th>CaseName</th>\n"+
                "<th>Result</th>\n"+
                "<th>CaseOwner</th>\n"+
                passed.toString()+
                "</table>\n";
        info[2]="<table border=\"1\">\n"+
                "<tr>\n"+
                "<th>CaseName</th>\n"+
                "<th>CaseOwner</th>\n"+
                notrun.toString()+
                "</table>\n";
        info[3]="<table border=\"1\">\n"+
                "<tr>\n"+
                "<th>CaseName</th>\n"+
                "<th>Result</th>\n"+
                "<th>CaseOwner</th>\n"+
                unknown.toString()+
                "</table>\n";
        return info;
    }

    public String[] createResultInfoWithDataTable() throws ItemNotFoundException {
        String[] info=new String[4];

        StringBuffer passed=new StringBuffer();
        StringBuffer failed=new StringBuffer();
        StringBuffer notrun=new StringBuffer();
        StringBuffer unknown=new StringBuffer();
//		Enumeration<String> keys=caseTable.keys();
        String[] keyArr=Util.getSortedHashtableKeys(caseTable);
//		while(keys.hasMoreElements()) {
        for(int i=0;i<keyArr.length;i++){
//			String key=keys.nextElement();
            String key=keyArr[i];
            TestCaseInfo tc=caseTable.get(key);

            String scriptName=tc.getCaseName();
            if(scriptName==null || scriptName.length()<1) {
                scriptName="caseid#"+key;
            }

            String hostInfo="";
            String hostName=tc.getExecutionHostName();
            String hostIP=tc.getExecutionHostIP();
            String records=tc.getRecords();
            String recordTable="";
            recordTable=tc.getRecordTable();
            String passlist="";
            String failedlist="";
            if(hostName.length()>0 ){
                hostInfo=hostName;
            }

            if(hostIP.length()>0) {
                if(hostInfo.length()>0) {
                    hostInfo +="("+hostIP+")";
                }else {
                    hostInfo = hostIP;
                }
            }

            switch(tc.getTestResult()) {
                case RESULT_PASSED:
                    if(!Util.isEmpty(recordTable))
                        passlist=Util.getIDsByStatusFromDataTable(recordTable, env, records, true);
                    passed.append("<tr>\n");
                    passed.append("<td>"+scriptName+"</td>\n");
                    passed.append("<td>ALL DONE");
                    if(hostInfo.length()>0) {
                        passed.append(" in ");
                        passed.append(hostInfo);
                    }
                    passed.append("</td>\n");
                    passed.append("<td>"+passlist+"</td>\n");
                    passed.append("<td>"+recordTable+"</td>\n");
                    passed.append("</tr>\n");
                    break;
                case RESULT_FAILED:
                    if(!Util.isEmpty(recordTable)) {
                        passlist=Util.getIDsByStatusFromDataTable(recordTable, env, records, true);
                        failedlist=Util.getIDsByStatusFromDataTable(recordTable, env, records, false);
                    }
                    failed.append("<tr>\n");
                    failed.append("<td>"+scriptName+"</td>\n");
                    failed.append("<td>FINISHED");
                    if(hostInfo.length()>0) {
                        failed.append(" in ");
                        failed.append(hostInfo);
                    }
                    failed.append("</td>\n");
                    failed.append("<td>"+passlist+"</td>\n");
                    failed.append("<td>"+failedlist+"</td>\n");
                    failed.append("<td>"+recordTable+"</td>\n");
                    failed.append("</tr>\n");
                    break;
                case RESULT_NOT_RUN:
                case RESULT_MEMERROR:
                case RESULT_JVMCRASH:
                    if(!Util.isEmpty(recordTable)) {
                        passlist=Util.getIDsByStatusFromDataTable(recordTable, env, records, true);
                        failedlist=Util.getIDsByStatusFromDataTable(recordTable, env, records, false);
                    }
                    notrun.append("<tr>\n");
                    notrun.append("<td>"+scriptName+"</td>\n");
                    notrun.append("<td>"+"NOT ALL FINISHED"+"</td>\n");
                    notrun.append("<td>"+passlist+"</td>\n");
                    notrun.append("<td>"+failedlist+"</td>\n");
                    notrun.append("<td>"+recordTable+"</td>\n");
                    notrun.append("</tr>\n");
                    break;
                default:
                    if(!Util.isEmpty(recordTable)) {
                        passlist=Util.getIDsByStatusFromDataTable(recordTable, env, records, true);
                        failedlist=Util.getIDsByStatusFromDataTable(recordTable, env, records, false);
                    }
                    unknown.append("<tr>\n");
                    unknown.append("<td>"+scriptName+"</td>\n");
                    if(inprogress.contains(key)) {
                        unknown.append("<td>"+"NOT ALL FINISHED"+"</td>\n");
                    }
                    unknown.append("<td>"+passlist+"</td>\n");
                    unknown.append("<td>"+failedlist+"</td>\n");
                    unknown.append("<td>"+recordTable+"</td>\n");
                    unknown.append("</tr>\n");
            }

        }

        info[0]="<table border=\"1\">\n"+
                "<tr>\n"+
                "<th>Scripts</th>\n"+
                "<th>Result</th>\n"+
                "<th>Passed_Records</th>\n"+
                "<th>Failed_Records</th>\n"+
                "<th>Data_Table</th>\n"+
                failed.toString()+
                "</table>\n";
        info[1]="<table border=\"1\">\n"+
                "<tr>\n"+
                "<th>Scripts</th>\n"+
                "<th>Result</th>\n"+
                "<th>Passed_Records</th>\n"+
                "<th>Data_Table</th>\n"+
                passed.toString()+
                "</table>\n";
        info[2]="<table border=\"1\">\n"+
                "<tr>\n"+
                "<th>Scripts</th>\n"+
                "<th>Result</th>\n"+
                "<th>Passed_Records</th>\n"+
                "<th>Failed_Records</th>\n"+
                "<th>Data_Table</th>\n"+
                notrun.toString()+
                "</table>\n";
        info[3]="<table border=\"1\">\n"+
                "<tr>\n"+
                "<th>Scripts</th>\n"+
                "<th>Result</th>\n"+
                "<th>Passed_Records</th>\n"+
                "<th>Failed_Records</th>\n"+
                "<th>Data_Table</th>\n"+
                unknown.toString()+
                "</table>\n";
        return info;
    }

    public Object[] generateFailedTestcaseCMD() {
        List<String> ids=new ArrayList<String>();
        List<String> cmds=new ArrayList<String>();
        List<String> caseNames=new ArrayList<String>();
        List<String> multis=new ArrayList<String>();

        ids.addAll(failedList);
        ids.addAll(notrunList);
        ids.addAll(unknownList);
        ids.addAll(inprogress);

        String runningId=Util.createRunningID();
        String arg=" env="+env+":cmdLine=true:runningId="+runningId;//+":testcaseId="+caseID+":tool="+tool;//+:multi=branchTotal:branch=0:priority=priority
        for(String id: ids) {
            TestCaseInfo tc=caseTable.get(id);
            StringBuffer cmd=new StringBuffer();
            cmd.append(tc.getCaseName().trim());
            cmd.append(" ");
            cmd.append(arg.trim());
            cmd.append(":testcaseId=");//testcaseId
            cmd.append(id);
            cmd.append(":tool=");
            cmd.append(tc.getTool());
            tc.setBranch(0);//set branch as initial 0
            cmd.append(":branch=0");
            cmd.append(":branchTotal=");
            cmd.append(tc.getBranchTotal());
            cmds.add(cmd.toString());
            caseNames.add(tc.getCaseName());
            multis.add(String.valueOf(tc.getBranchTotal()));
        }

        Object[] toReturn=new Object[5];
        Properties prop=new Properties();
        prop.put("runningId", runningId);
        prop.put("env", env);
        prop.put("testset", testSet);
        prop.put("mailto", mailto);
        prop.put("runners", runners);
        toReturn[0]=prop;
        toReturn[1]=ids;
        toReturn[2]=cmds;
        toReturn[3]=caseNames;
        toReturn[4]=multis;

        return toReturn;
    }

    public void setRunners(String runners) {
        this.runners = runners;
    }

    public String getRunners() {
        return runners;
    }

    public void setTestCaseOwner(String id, String owner) {
        TestCaseInfo tc=caseTable.get(id);
        if(tc==null) {
            logger.warn("Null: failed to get test case info for caseID="+id);
            return;
        }
        tc.setCaseOwner(owner);
    }

    public void fillRecoveryTestCaseInfo(List<String[]> result) {
        String caseID="";
        String caseName="";
        String runner="";
        String hostName="";
        String ip="";
        int executionTime=60;
        int tool=0;
        int cresult=TestConstants.RESULT_NA;
        int branchTotal=0;
        int branch=branchTotal;
        String caseowner="QA";

        for(int i=0;i<result.size();i++){
            int cStatus=Integer.parseInt(result.get(i)[7]);
            caseID=result.get(i)[0];
            caseName=result.get(i)[1];
            runner=result.get(i)[2];
            if(hostName.contains("(") && hostName.contains(")")){
                hostName=runner.substring(0, runner.indexOf("("));
                ip=runner.substring(runner.indexOf("(")+1, runner.indexOf(")"));
            }
            String value=result.get(i)[3];
            if(!Util.isEmpty(value))
                executionTime=Integer.parseInt(value);
            tool=Integer.parseInt(result.get(i)[4]);
            branchTotal=0;
            value=result.get(i)[6];
            if(!Util.isEmpty(value))
                branchTotal = Integer.parseInt(value);
            branch=branchTotal;
            switch(cStatus){
                case TestConstants.TESTCASE_HIBERNATED:
                case TestConstants.TESTCASE_RUNNING:
                    startTestcase(caseID, caseName, hostName, ip, executionTime, tool, branchTotal, branch, "");
                    break;
                case TestConstants.TESTCASE_FINISHED:
                    startTestcase(caseID, caseName, hostName, ip, executionTime, tool, branchTotal, branch, "");
                    cresult=Integer.parseInt(result.get(i)[5]);
                    setTestCaseResult(caseID, cresult, "");
                    setTestCaseOwner(caseID, caseowner);
                    break;
                default:
            }

        }
    }

    /**
     * Set result to test_production table for production sanity test case result
     */
    public void setResultForPrdSanityCases() {
        Enumeration<String> keys=caseTable.keys();
        while(keys.hasMoreElements()) {
            String key=keys.nextElement();
            TestCaseInfo tc=caseTable.get(key);
            String idkey=tc.getCaseID();//set caseID when receiving begin msg
            String id=idkey.split(":")[0];
            int resetResult=tc.getTestResult();
            //for production sanity test cases which has dataTable, it should summarized all records result
            if(tc.getRecordTable()!=null && tc.getRecordTable().length()>1) {
                int resultDB = Util.getPrdSanityResultById(env, id);
                int result=tc.getTestResult();
                if(resultDB==TestConstants.RESULT_NA || resultDB==TestConstants.RESULT_NOT_RUN)
                    resetResult=result;
                else if(result==TestConstants.RESULT_PASSED && resultDB==TestConstants.RESULT_PASSED)
                    resetResult=result;
                else
                    resetResult=TestConstants.RESULT_FAILED;
            }
            String update="update test_production set "+env+"_result="+resetResult+" where id="+id;
            DataBase db=DataBase.getInstance();
            db.executeUpdate(update);
        }
    }
}

