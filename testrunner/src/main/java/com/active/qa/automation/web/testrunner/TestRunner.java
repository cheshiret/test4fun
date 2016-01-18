package com.active.qa.automation.web.testrunner;

import com.active.qa.automation.web.testrunner.util.*;
import com.compdev.jautoit.autoitx.AutoitXFactory;
import com.compdev.jautoit.autoitx.Autoitx;
import com.sun.jna.WString;
import org.apache.log4j.Logger;

import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by tchen on 1/18/2016.
 */
public class TestRunner extends Thread implements TestConstants{
    static Logger logger = Logger.getLogger(TestRunner.class);
    private boolean stop=false;
    private boolean crashed=false;
    private int status=IDLE;
    private String caseName="";
    private int executionTime=0; //in seconds
    private int recordExecutionTime=0; //in seconds
    private int automationTool=SELENIUM;//defaut tool
    private long startTime=0; //test case start time in millseconds
    private ConsumerTool consumer;
    private ProducerTool producer;
    private boolean suspend=false;
    private boolean connect_error=false;
    //	private boolean checkOCAMRestart=true;
    private boolean isRFTRunner=false;
    private boolean setupscripts=false;
    private boolean prdsanitycases=false;
    IEDriverCrashCleaner crashCleaner=new IEDriverCrashCleaner();
    private String selector_code="";

    public void run() {
        TestProperty.load();
        TestProperty.resetLogfile(TestProperty.get("host.name"));

//		String queue=TestProperty.get("mq.test.queue");
        String hostName= Util.getHostName();
        String ip=Util.getHostIP();


        crashCleaner.start();
//		int count=0;
        try {
            consumer = ConsumerTool.getInstance();
            producer = ProducerTool.getInstance();
            ConsumerTool.logger=logger;
            ProducerTool.logger=logger;

            while(!stop) {
                isRFTRunner=Util.isRFTRunner(hostName);
//				selector_code=Util.getSelectorCode(hostName);
//				logger.debug("selector code: "+selector_code);
//				consumer.setSelector(selector_code);
                Message msg=null;
                try {
                    if(suspend || connect_error) {
                        setSuspend();
                        Util.sleep(1000);
                        msg=null;
                    } else {
                        setIdle();
                        msg=consumeMessage(hostName, 500);
                    }
                } catch (InvalidClientIDException e) {
                    stop=true;
                    logger.error(e);
                    e.printStackTrace();
                    msg=null;
                }catch (JMSException e) {
                    logger.error(e);
                    ConnectionTool.close();

                    Util.sleep(Util.getRetrySleep()*1000);

                    continue;
                }

                if(msg==null) {
                    continue;
                }

                String monitor=msg.getStringProperty("monitor");
                String debug=msg.getStringProperty("debug");
                String draft=msg.getStringProperty("draft");
                String mailto=msg.getStringProperty("mailto");
                int branchTotal=msg.getIntProperty("branchTotal");//get multi value from message
                int branch=msg.getIntProperty("branch");//get branch from message to construct cmd
                String runners=msg.getStringProperty("runners");//get runners to inform monitor to run branch on specific runners
                int tool=Integer.parseInt(msg.getStringProperty("toolcode"));
                if(debug==null || debug.length()<1) {
                    debug="false";
                }

                if(draft==null || draft.length()<1) {
                    draft="false";
                } else if (draft.equalsIgnoreCase("true")) {
                    debug="true";
                }

                TestProperty.put("draft",draft);
                TestProperty.put("debug", debug);

                int result;
                String testCase="";
                String caseName="";
                String caseID="";
                String env=msg.getStringProperty("env");
                String ids="";//for setup records ids
                String tablename="";//for setup script table

                if(msg instanceof TextMessage) {
                    testCase=((TextMessage) msg).getText();

                    if(!env.equals("live")) {
                        while(ocam_restart(recordExecutionTime,env)) {
                            setOCAM_restart();
                            Util.sleep(5*60*1000);
                        }
                    }

                    String[] tokens=testCase.split(" +");
                    caseName=tokens[0];
                    String args=tokens[1];
                    if(testCase.contains("testcases.production")){
                        prdsanitycases=true;
                        setupscripts=false;
                        caseID=Util.getScriptID(args);
                        ids=msg.getStringProperty("ids");
                        tablename=msg.getStringProperty("tablename");
                        recordExecutionTime=Integer.parseInt(TestProperty.get("production.sanity.cases.timing.max"))*60;//hard code for production sanity, max timing as 15 mins
                    }else if(testCase.contains("supportscripts")){
                        setupscripts=true;
                        prdsanitycases=false;
                        caseID=Util.getScriptID(args);
                        ids=msg.getStringProperty("ids");
                        tablename=msg.getStringProperty("tablename");
                        recordExecutionTime=Integer.parseInt(TestProperty.get("supportscript.timing.max"))*60;//hard code for setup script, max timing as 30 mins
                    }else{
                        setupscripts=false;
                        prdsanitycases=false;
                        caseID=Util.getCaseID(args);
                        recordExecutionTime=Util.getTestCaseTiming(caseName);
                    }

                    setBusy(caseName);

                    if(branchTotal<=1 || (branchTotal>1 && branch==0)){
                        //inform test monitor this test case started
                        producer.connect(monitor);

                        String[] msgPropery={"type=begin","index="+msg.getIntProperty("index"),"caseID="+caseID,"runningId="+msg.getStringProperty("runningId"),
                                "host="+hostName,"ip="+ip,"tool="+tool,"executionTime="+recordExecutionTime, "ids="+ids, "tablename="+tablename};
                        Message m=producer.createTextMessage(caseName,msgPropery);
                        m.setIntProperty("branchTotal", branchTotal);
                        m.setIntProperty("branch", branch);
                        producer.produceMessage(m);
                        producer.disconnect();
                    }

                    int status=EXECUTION_NA;
                    //get current status for the test case from database
                    if(!setupscripts && !prdsanitycases)
                        status=Util.checkTestStatus(msg.getStringProperty("env"),caseName);

                    if(status==EXECUTION_STOP) //check if user wants to stop the execution
                        result=RESULT_NOT_RUN;
                    else if(status == EXECUTION_SUSPEND) {
                        result=RESULT_NOT_RUN;
                        Util.updateResult(msg.getStringProperty("env"),msg.getStringProperty("runningId"),caseName,RESULT_NOT_RUN);
                    } else {
                        if(branchTotal>1){
                            Util.setEFTRunning(TestConstants.STATUS_TRUE);
                            Util.setEFTRunningCaseID(caseID);
                        }

                        //set status as running
                        if(!setupscripts && !prdsanitycases)
                            Util.updateStatus(msg.getStringProperty("env"),msg.getStringProperty("runningId"),caseName,EXECUTION_RUNNING);

                        //run test case
                        try {
                            String cmd=constructCMD(testCase, msg.getStringProperty("env"));
                            automationTool=Util.getToolCode(testCase);
                            result=runTest(cmd);
                        } catch(Exception e) {
                            logger.fatal("Got fatal error when executing "+testCase, e);
                            result=RESULT_FAILED;
                        }

                        //for those test cases which has multi branches, and current branch was not the last branch, with running result as RESULT_PASSED
                        //change the status as RESULT_HIBERNATED
                        if(branchTotal >1 && branch<branchTotal-1 && result==RESULT_PASSED)
                            result=RESULT_HIBERNATED;

                        if(!setupscripts && !prdsanitycases)
                            Util.updateResult(msg.getStringProperty("env"),msg.getStringProperty("runningId"),caseName,result);

                        setIdle();
                    }
                } else {
                    logger.error("Not a TextMessage: "+msg.toString());
                    result=RESULT_NOT_RUN;
                }

                if(testCase.length()>0) {
                    int exeStauts = EXECUTION_FINISH;
                    if(branchTotal >1 && branch<branchTotal-1 && result==RESULT_PASSED)
                        exeStauts = EXECUTION_HIBERNATED;
                    String caseOwner="QA";

                    if(!setupscripts && !prdsanitycases){
                        Util.updateStatus(msg.getStringProperty("env"),msg.getStringProperty("runningId"),caseName,exeStauts);
                        caseOwner=Util.getTestCaseOwnerByCaseID(caseID);
                    }

                    //send test result to monitor
                    if(monitor!=null && monitor.length()>0) {
                        producer.connect(monitor);
                        String[] msgPropery={"type=result","env="+env,"result="+result,"caseID="+caseID,"runningId="+msg.getStringProperty("runningId"),
                                "host="+hostName,"ip="+ip,"mailto="+mailto,"runners="+runners,"tool="+tool,"caseOwner="+caseOwner, "ids="+ids, "tablename="+tablename};
                        msg=producer.createTextMessage(caseName,msgPropery);
                        msg.setIntProperty("branchTotal", branchTotal);
                        msg.setIntProperty("branch", branch);
                        msg.setBooleanProperty("setupscripts", setupscripts);
                        msg.setBooleanProperty("prdsanitycases", prdsanitycases);
                        logger.info("--->TestRunner send result to Monitor for test case:"+caseID+" result:"+result+" branch:"+branch+" BranchTotal:"+branchTotal);
                        producer.produceMessage(msg);
                        producer.disconnect();
                    }
                }

                if(result==RESULT_MEMERROR || result==RESULT_JVMCRASH) {
                    Util.rebootHost();
                    stop=true;
                }
            }
            logger.info("TestRunner is stopped");

        } catch (InvalidClientIDException e) {
            crashed=false;
            logger.error(e);
            e.printStackTrace();
        }catch(Exception e) {
            logger.error(e);
            e.printStackTrace();
            crashed=true;
        } finally {
            stop=true;
            Util.setEFTRunning(TestConstants.STATUS_FALSE);
            try {
                producer.connect(TestProperty.get("mq.runner.topic"),true);
                Message msg=producer.createMessage();
                msg.setStringProperty("type", "dummy");
                producer.produceMessage(msg);
                producer.disconnect();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public String getSelector() {
        return selector_code;
    }

    public boolean ocam_restart(int executionTime, String env) {
        Calendar c=Calendar.getInstance();
        Calendar atExeC=Calendar.getInstance();
        atExeC.setTimeInMillis(c.getTimeInMillis()+executionTime*1000);

        String ocamTime=TestProperty.get(env+".ocam.restart.time","na");
        if(ocamTime==null || ocamTime.length()<1 || ocamTime.equalsIgnoreCase("na") || !ocamTime.matches("\\d{1,2}:\\d{2}")) {
            return false;
        }

        String[] tokens=ocamTime.split(":");
        int ocamHour=Integer.parseInt(tokens[0]);
        int ocamMinute=Integer.parseInt(tokens[1]);
        int duration=Integer.parseInt(TestProperty.get("ocam.restart.duration"));

        Calendar ocamStartC=Calendar.getInstance();
        ocamStartC.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), ocamHour, ocamMinute,0);
        Calendar ocamEndC=Calendar.getInstance();
        ocamEndC.setTimeInMillis(ocamStartC.getTimeInMillis()+duration*60*1000);

        long now=c.getTimeInMillis();
        long atExe=atExeC.getTimeInMillis();
        long ocamStart=ocamStartC.getTimeInMillis();
        long ocamEnd=ocamEndC.getTimeInMillis();

        boolean withinOcam=(now>=ocamStart && now <ocamEnd) || (atExe>=ocamStart && atExe<=ocamEnd) || (now<=ocamStart && atExe >=ocamEnd);
        if(withinOcam) {
            logger.info("Within Ocam restart period. Suspended!");
        }
        return withinOcam;

    }

    public void suspendRunner() {
        suspend=true;
    }

    public void resumeRunner() {
        suspend=false;
    }

    public void connectErrorOccured() {
        connect_error=true;
    }

    public void errorCleared() {
        connect_error=false;
    }

    /**
     * Run test cases against the target environment
     * @param testcase - the test case full name followed by command line arguments
     * @param env - the target environment qa1/qa2
     * @return - the test result
     * @throws IOException
     * @throws InterruptedException
     */
    public int runTest(String cmd) throws IOException, InterruptedException {
        logger.debug("Executing command: "+cmd);
        int executionTime=Integer.parseInt(TestProperty.get("testcase.timing.max"))*60;
        int extraWaitingTime=Integer.parseInt(TestProperty.get("testcase.timing.extra"))*60;
        if(Boolean.valueOf(TestProperty.get("testcase.timing.fixed", "true"))==false && recordExecutionTime>0) {
            executionTime =recordExecutionTime;

        }
        logger.info("Executing time: "+executionTime);
        Executor exec=new Executor(cmd);
        if(automationTool==SELENIUM)
            crashCleaner.startScan();
        exec.start();
        startTime=Calendar.getInstance().getTimeInMillis();

        //wait for the normal execution time+extra waiting time
        exec.join((executionTime+extraWaitingTime)*1000);
//		logger.warn("Test case dosn't finish in its expected execution time "+executionTime/60+" minutes, wait for extra "+extraWaitingTime+" seconds.");

//		exec.join(extraWaitingTime*1000);
        if(automationTool==SELENIUM)
            crashCleaner.stopScan();

        if(exec.isAlive()) {
            logger.warn("Test case execution didn't finish in "+executionTime/60+" minutes. Cleaning up...");
            killAllBrowsers();
            exec.killProcess();

            //wait for 2 minutes for the process to die
            exec.join(120*1000);
            if(exec.isAlive()) {
                logger.warn("Fatal error: Failed to kill process. Reboot machine...");
                stop=true;
                Util.rebootHost();
            }
        }

        int exitVal=exec.getExitVal();
        resetTiming();

        logger.info("Exited with code "+exitVal);

        return exitVal;
    }

    public boolean isRunning() {
        return !stop;
    }

    public boolean isCrashed() {
        return crashed;
    }

    private void resetTiming() {
        executionTime=0;
        recordExecutionTime=0;
        startTime=0;
    }

    protected void setSuspend() {
        status=SUSPEND;
        caseName="";
    }

    protected void setIdle() {
        status=IDLE;
        caseName="";
    }

    private void setOCAM_restart() {
        status=OCAM_RESTART;
        caseName="";
    }

    protected void setBusy(String caseName) {
        status=BUSY;
        this.caseName=caseName;
    }

    public int getStatus() {
        return status;
    }

    public String getCaseName() {
        return caseName;
    }

    protected void killAllBrowsers() {
        Executor killIE=new Executor("taskkill /F /IM iexplore.exe /T",false);
        Executor killDW20=new Executor("taskkill /F /IM dw20.exe /T",false);
        Executor killWerFault=new Executor("taskkill /F /IM werfault.exe /T",false);
        Executor killIEDrivert=new Executor("taskkill /F /IM IEDriverServer.exe /T",false);
        killIE.start();
        killDW20.start();
        killWerFault.start();
        killIEDrivert.start();
    }

    public void stopRunner() {
        stop=true;
    }

    public int getTimeRemaining() {
        int timeToUse=recordExecutionTime;
        if(timeToUse==0) {
            if(executionTime==0)
                return 0;
            else
                timeToUse=executionTime;
        }


        long now=Calendar.getInstance().getTimeInMillis();
        float diff=(now - startTime)/(float)1000;
        int spent=Math.round(diff);
        int remaining=timeToUse-spent;
        if(remaining<0) {
            return 0;
        } else {
            return remaining;
        }

    }

    protected String constructCMD(String testcase, String env) {
        String cmd=null;
        String ormsclient_path="";

        if(env.matches("qa[1-4]")) {
            String ormsclient_basedir=TestProperty.get("ormsclient.dist.basedir");
            Properties versionProp=new Properties();
            try {
                versionProp.load(new FileInputStream(new File(ormsclient_basedir+"/version.properties")));

            } catch (Exception e) {
                logger.fatal(e);
                return cmd;
            }

            String awobuild=Util.getAWObuild(env);
            String awomajor;
            String aworeleasefolder;
            if(awobuild.length()>0) {
                awomajor=awobuild.substring(0, awobuild.lastIndexOf("."));
                aworeleasefolder=ormsclient_basedir+"/lib/"+awobuild;
                File awomodulefolder=new File(aworeleasefolder);


//				int count=0;
//				while(!awomodulefolder.exists() && count<7) {
//					//awo release needs to be synchronized
//					requiredOrmsClientSync(env);
//					Util.sleep(10000);
//					count++;
//				}

                if(!awomodulefolder.exists()) {
                    logger.warn("Failed to get synchronized AWO modules. will try to use the latest modules. Sync request has been sent to TestMonitor.");
                    requiredOrmsClientSync(env);
                    awobuild=versionProp.getProperty(awomajor+".orms.version");
                    aworeleasefolder=ormsclient_basedir+"/lib/"+awobuild;
                }
            } else {
                awobuild=Util.getLastAWOBuild(env);
                aworeleasefolder=ormsclient_basedir+"/lib/"+awobuild;
                awomajor=awobuild.substring(0, awobuild.lastIndexOf("."));
            }


            String ormsclient_build=awomajor+"."+versionProp.getProperty(awomajor+".revision");
            ormsclient_path=ormsclient_basedir+"/lib/common/*;"+ormsclient_basedir+"/lib/"+awobuild+"/*;"+ormsclient_basedir+"/ormsclient_"+ormsclient_build+".jar";
        }

//		if(!legacy) {//new functest4 command
        cmd=constructCMD(testcase, ormsclient_path, env);
//		} else { //legacy framework
//			int toolCode=Util.getToolCode(testcase);
//			if (toolCode==NONE) {
//				String defaultTool=TestProperty.get("default.tool", "selenium");
//				if(defaultTool.equalsIgnoreCase("selenium")) {
//					toolCode=SELENIUM;
//				} else if (defaultTool.equalsIgnoreCase("rft")){
//					toolCode=RFT;
//				} else {
//					if(this.isRFTRunner) {
//						toolCode=RFT;
//					} else {
//						toolCode=SELENIUM;
//					}
//				}
//			}
//			switch (toolCode) {
//			case RFT:
//				cmd= constructLegacyRFTCMD(testcase, ormsclient_path,env);
//				break;
//			case SELENIUM:
//			case NONE: //use selenium by default
//				cmd= constructLegacySeleniumCMD(testcase,ormsclient_path,env);
//			}
//		}

        return cmd;
    }

    protected void requiredOrmsClientSync(String env) {
        int[] status=Util.getOrmsclientSyncStatus(env);

        if(status[0]==0) {
            int result=Util.updateOrmsclientSyncStatus(1, status[1], env);

            if(result>0) {
                try {
                    producer.connect(TestProperty.get("mq.monitor.queue"));
                    String[]msgProperty={"type=build","buildtype=update+deploy","project=ormsclient_"+env};
                    Message msg=producer.createTextMessage("Update and build", msgProperty);
                    msg.setBooleanProperty("legacy", false);
                    producer.produceMessage(msg);
                    producer.disconnect();

                } catch(Exception e) {
                    logger.warn(e);
                }
            }
        }
    }

    protected String constructLegacySeleniumCMD(String testcase, String ormsclient_path, String env) {
        String script_path=TestProperty.get(env+".selenium.functest.dir");
//		if(TestProperty.get("draft").equalsIgnoreCase("true")) {
//			script_path=TestProperty.get("qax.selenium.dir");
//		}

        //create the script classpath
        String classpath=script_path+";"+script_path+"/lib/*"+(ormsclient_path.length()>0?";"+ormsclient_path:"");

        //construct the command line for Selenium
        String cmd="java -classpath "+rebuildClassPath(classpath)+" testdriver.TestcaseLoader "+testcase;

        return cmd;
    }

    private String rebuildClassPath(String classPath) {
        String[] tokens=classPath.split(";");
        ArrayList<String> jars=new ArrayList<String>();
        ArrayList<String> paths=new ArrayList<String>();

        for(String s:tokens) {
            if(s.endsWith("*")){
                String path=s.substring(0, s.length()-2);
                String[] js=Util.getExternalJars(path);
                for(String ajar:js) {
                    String name=ajar.substring(ajar.lastIndexOf("/")+1);
                    if(!jars.contains(name)) {
                        paths.add(path+"/"+ajar);
                    }
                }
            } else {
                paths.add(s);
            }
        }

        return Util.listToString(paths, false, ";");
    }

//	public String constructLegacyRFTCMD(String testcase, String ormsclient_path, String env) {
//		String rft_path=TestProperty.get("rational_ft.install.dir");
//
//		String script_path=TestProperty.get(env+".rft.functest.dir");
//		if(TestProperty.get("draft").equalsIgnoreCase("true")) {
//			script_path=TestProperty.get("qax.functest.dir");
//		}
//
//		//construct the script classpath
//		String classpath="\""+rft_path+"\\SDP\\FunctionalTester\\bin\\rational_ft.jar\"";
//
//		classpath+=";"+script_path+"/lib/*"+(ormsclient_path.length()>0?";"+ormsclient_path:"");
//
//		//construct the command line for RFT 8.1
//		String cmd="java -classpath "+classpath+" com.rational.test.ft.rational_ft -datastore "+script_path+" -log Test_Log -playback testdriver.TestcaseLoader -args "+testcase;
//
//		return cmd;
//	}

    public String constructCMD(String testcase, String ormsclient_path,String env) {
        String basedir=TestProperty.get("functest4.base");
        int toolCode=Util.getToolCode(testcase);

        if(testcase.startsWith("testCases."))
            testcase="com.activenetwork.qa.awo.testcases"+testcase.substring(9);
        else
            testcase="com.activenetwork.qa.awo."+testcase;

        //create the script classpath
        String awoBuild=Util.getAWObuild(env);
        String awoMajor=awoBuild.substring(0,awoBuild.lastIndexOf("."));
        Properties versionProperties=new Properties();
        try {
            versionProperties.load(new FileInputStream(new File(basedir+"/version.properties")));
        } catch (FileNotFoundException e) {
            logger.fatal("version.properties file is not found under "+basedir);
        } catch (IOException e) {
            logger.fatal("Failed to read version.properties file under "+basedir);
        }
        String awoVersion=versionProperties.getProperty("awo."+awoMajor+".version");
        if(awoVersion==null || awoVersion.length()<1) {
            throw new ItemNotFoundException("Failed to get functest4_awo version for awo major build#"+awoMajor);
        }
        String awojar="functest4_awo_"+awoVersion+".jar";


        String corejar="functest4_core_"+versionProperties.getProperty("core.version")+".jar";
        String seleniumjar="functest4_selenium_driver_"+versionProperties.getProperty("selenium.version")+".jar";
        String classpath=basedir+"/lib/core/*;"+basedir+"/lib/selenium/*;"+basedir+"/lib/awo/*;"+basedir+"/"+awojar+";"+basedir+"/"+corejar+";"+basedir+"/"+seleniumjar+(ormsclient_path.length()>0?";"+ormsclient_path:"");

        //construct the command line for functest4
        String cmd="java -classpath "+classpath+" com.activenetwork.qa.awo.testcases.TestcaseLoader "+testcase;

        return cmd;
    }

    private Message consumeMessage(String hostName, int timeout) throws JMSException{
        Message msg=null;
        selector_code="(runners like '%"+hostName+"%' OR runners = 'ALL') ";
//		selector_code="";
        consumer.setSelector(selector_code);

//		List<Message> ms=consumer.browserMessages("QA_TEST", "");
//		System.out.println(ms.size());

        if (Util.isSMRunner(hostName)) {
            msg = consumer.consumeMessage(TestProperty.get("mq.sm.queue"), timeout);
            if(msg!=null)
                return msg;
        }

        if (Util.isEFTRunner(hostName)) {
            if(!Util.isEFTRunning())
                msg = consumer.consumeMessage(TestProperty.get("mq.eft.queue"), timeout);
            else {
                String caseID = Util.getEFTRunningCaseID();
                String new_selector_code = selector_code+" AND exetime < "+Calendar.getInstance().getTimeInMillis()+" AND branchTotal > 1 AND caseID = '"+caseID+"'";
                consumer.setSelector(new_selector_code);
                msg = consumer.consumeMessage(TestProperty.get("mq.eft.queue"), timeout);
                consumer.setSelector(selector_code); //roll back selector
            }
            if(msg!=null)
                return msg;
        }

        if(Util.isReportRunner(hostName)) {
            msg = consumer.consumeMessage(TestProperty.get("mq.report.queue"), timeout);
            if(msg!=null)
                return msg;
        }

        if(isRFTRunner) {
            msg = consumer.consumeMessage(TestProperty.get("mq.rft.queue"), timeout);
            if(msg!=null)
                return msg;
        }

        msg = consumer.consumeMessage(TestProperty.get("mq.test.queue"), timeout);
        logger.debug("selector code: "+selector_code);
        return msg;
    }


    /**
     * used to cleaner the java crash popup which may happen on IE Driver
     * @author jdu
     *
     */
    private static class IEDriverCrashCleaner extends Thread {
        private boolean scan=false;
        @Override
        public void run() {
            while(true) {
                if(scan) {
                    try {
                        Autoitx autoit=AutoitXFactory.getAutoitx();
                        String title="[TITLE:Command line server for the IE driver; CLASS:#32770]";
                        String closeProgramButton="[CLASS:Button; TEXT:Close the program]";
                        if(autoit.AU3_WinExists(new WString(title), new WString(""))==1) {
                            autoit.AU3_ControlFocus(new WString(title), new WString(""), new WString(closeProgramButton));
                            autoit.AU3_ControlClick(new WString(title), new WString(""), new WString(closeProgramButton), new WString(""),1, 0, 0);
                        }
                    } catch (Throwable e) {
                        logger.warn(e);
                    }
                }

                //sleep 1 min
                try {Thread.sleep(60000);} catch (InterruptedException e) {}
            }

        }

        public void startScan() {
            scan=true;
        }

        public void stopScan() {
            scan=false;
        }
    }

    public static void main(String[] args) {
        TestProperty.load();
        TestRunner tr=new TestRunner();
        System.out.println(tr.constructLegacySeleniumCMD("testcases.sanity.orms.LM_CustomerBasicSanity", "X:/ormsclient/lib/common/*;X:/ormsclient/lib/3.04.03.12/*;X:/ormsclient/ormsclient_3.04.03.36.jar", "qa1"));
    }
}

