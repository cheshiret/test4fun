package com.active.qa.automation.web.testrunner.util;

import com.active.qa.automation.web.testrunner.TestConstants;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tchen on 1/18/2016.
 */
public class Util {
    /**
     * Query the test case status for the given environment in database
     * @param env - test environment qa1/qa2
     * @param caseName
     * @return - the current status
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static Logger logger = Logger.
            getLogger(Util.class);
    private static final String CASHED_BUILD = getProjectPath() + File.separator + "awo_build.properties";

    public static int checkTestStatus(String env, String caseName) throws SQLException, ClassNotFoundException {
        String query="select " + env + "_status from test_cases where casename = '" + caseName + "'";
        int status;
        DataBase db=DataBase.getInstance();
        List<String> resultList = db.executeQuery(query, env+"_status");
        status = Integer.parseInt(resultList.get(0));
        return status;
    }



    /**
     * Update the test case result in database with the given result
     * @param env
     * @param runningId
     * @param caseName
     * @param result
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static boolean updateResult(String env,String runningId,String caseName,int result) throws SQLException, ClassNotFoundException {
        String query = "update test_cases set " + env + "_result=" + result + " where " + env + "_running_id='" + runningId + "' and casename='" + caseName + "'";
        DataBase db=DataBase.getInstance();
        int count=db.executeUpdate(query);
        if(count<1) {
            logger.warn("Failed to execute query: " + query);
        }
        return count > 0;
    }

    /**
     *
     * Update the test case status in database with the given status
     * @param env
     * @param runningId
     * @param caseName
     * @param status
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static boolean updateStatus(String env,String runningId,String caseName,int status) throws SQLException, ClassNotFoundException {
        String query="update test_cases set "+env+"_status="+status+" where "+env+"_running_id='"+runningId+"' and casename='"+caseName+"'";
        DataBase db=DataBase.getInstance();
        int count=db.executeUpdate(query);
        if(count<1) {
            logger.warn("Failed to execute query: "+query);
        }

        return count>0;

    }

    public static void updateStatus(String env,String runningId,List<String> ids,int status){
        DataBase db=DataBase.getInstance();

        if(ids.size()<Integer.parseInt(TestProperty.get("size_limitation"))){
            String idStr = Util.listToString(ids, false, ",");
            String query="update test_cases set "+env+"_running_id="+runningId+","+env+"_status="+status+" where id in ("+idStr+")";
            db.executeUpdate(query);
        }else{
            String[] idStrs=Util.listToStrings(ids, false, ",",Integer.parseInt(TestProperty.get("size_limitation")));
            for(String idStr:idStrs) {
                String query="update test_cases set "+env+"_running_id="+runningId+","+env+"_status="+status+" where id in ("+idStr+")";
                db.executeUpdate(query);
            }
        }
    }

    public static int updateOrmsclientSyncStatus(int status, int concurrent, String... env) {
        DataBase db=DataBase.getInstance();
        if(env.length<1) {
            env=TestProperty.get("test.env").split(",");
        }
        String set="";

        for(String e:env) {
            set+="val_"+e+"='"+status+"',";
        }

        set+="val=seq('ormsclient')";

        String query="update qa_automation set "+set+" where var='ormsclient_sync'"+(concurrent>=0? " and val='"+concurrent+"'":"");
        return db.executeUpdate(query);
    }

    public static int[] getOrmsclientSyncStatus(String env) {
        DataBase db=DataBase.getInstance();

        int[] status=new int[2];
        String query="select val_"+env+", val from qa_automation where var='ormsclient_sync'";
        List<String[]> result=db.executeQuery(query, new String[]{"val_"+env,"val"});
        String[] statusStr=result.get(0);
        status[0]=Integer.parseInt(statusStr[0]);
        status[1]=Integer.parseInt(statusStr[1]);

        return status;
    }

    /**
     * Get all external jar files for the given script path
     * @param libpath
     * @return - array of jar file names
     */
    public static String[] getExternalJars(String libpath){
        File lib=new File(libpath);

        if (!lib.exists() || !lib.isDirectory()){
            throw new RuntimeException(libpath + " directory doesn't exist.");
        }

        return lib.list(new JarFilter());
    }

    public static String getProjectPath() {
        String fullpath=null;

        String startLocation=Util.class.getResource("Util.class").getPath();

        if(startLocation.startsWith("file:")) {
            startLocation=startLocation.substring(startLocation.indexOf("/"));
        }

        File path=new File(startLocation);
        boolean found=false;

        while(!found && path!=null) {
            String[] dirs=path.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equalsIgnoreCase("properties");
                }

            });

            if(dirs !=null && dirs.length>0) {
                found=true;
            } else {
                path=path.getParentFile();
            }
        }

        if(found) {
            fullpath=path.getAbsolutePath();
        }

        return fullpath;
    }

    public static void catchScreenShot(String filename) {
        try {
            File imageFile = null;

            if (!filename.endsWith(".png"))
                imageFile = new File(filename + ".png");
            else
                imageFile = new File(filename);

            String pathString=filename.substring(0,filename.lastIndexOf("/"));

            File path=new File(pathString);
            if(!path.exists()){
                path.mkdirs();
            }

            BufferedImage img = null;
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            img = robot.createScreenCapture(captureSize);
            ImageIO.write(img, "png", imageFile);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to create screen snapshot due to "+e.getMessage());
        }
    }

    public static String[] matches(String input, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        String text = "";
        String delimit = "#&#@#";

        while (m.find())
            text += m.group() + delimit;

        String[] result = text.split(delimit);

        return result;

    }

    public static boolean isBuildInProgress(){
        boolean inProgress=false;

        String query="select val from qa_automation where var='BUILDING'";
        DataBase db=DataBase.getInstance();
        List<String> resultList = db.executeQuery(query, "val");
        if(resultList!=null){
            inProgress=Integer.parseInt(resultList.get(0))==1;
        }else{
            throw new RuntimeException("Query returned nothing.");
        }

//		try {
//			db.connect();
//			ResultSet rs=db.executeQuery(query);
//			if(rs.next()){
//				inProgress=Integer.parseInt(rs.getString("VAL"))==1;
//			} else
//				throw new SQLException("Query returned nothing.");
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		} finally {
//			db.disconnect();
//		}

        return inProgress;

    }

    public static String retrieveCaseNameFromCMD(String cmd) {
        Pattern p=Pattern.compile("testCases(\\.\\S+)+");
        Matcher m=p.matcher(cmd);
        List<String> list=new ArrayList<String>();
        while (m.find()) {
            list.add(m.group());
        }

        return list.get(0);

    }

    public static int getTestCaseTiming(String caseFullName) {
        String query="select timing from test_cases where casename='"+caseFullName+"'";
        DataBase db=DataBase.getInstance();
        List<String> resultList = db.executeQuery(query, "timing");
        if(resultList.get(0)==null) {
            return -1;
        } else {
            return Integer.parseInt(resultList.get(0));
        }
//		try {
//			db.connect();
//			ResultSet rs=db.executeQuery(query);
//			if(rs.next()) {
//				return rs.getInt("TIMING");
//			} else {
//				return -1;
//			}
//
//		} catch(Exception e) {
//			e.printStackTrace();
//			return -1;
//		} finally {
//			db.disconnect();
//		}
    }

    public static HashMap<String, Integer> getMultiValue(String[] ids){
        DataBase db=DataBase.getInstance();
        HashMap<String, Integer> result=new HashMap<String, Integer>();
        for(int i=0;i<ids.length;i++){
            String query="select multi from test_cases where id='"+ids[i]+"'";
            List<String> queryRes = db.executeQuery(query, "multi");
            String multi = queryRes.get(0);
            if(multi != null && Integer.parseInt(multi)>0){
                result.put(ids[i], Integer.parseInt(multi));
            }
        }
        return result;
    }

    public static void setBuilding(boolean inProgress) {
        int val=inProgress?1:0;
        String query="update qa_automation set val='"+val+"' where var='BUILDING'";
        DataBase db=DataBase.getInstance();
//		db.connect();
        int count=db.executeUpdate(query);
//		db.disconnect();
        if( count<=0) {
            throw new RuntimeException("Failed to set building value");
        }
    }

    public static String getQueryID() {
        return (new SimpleDateFormat("yyyyMMddhhmmss")).format(Calendar.getInstance().getTime()).toString();
    }

    /**
     * Read a text file into a String
     * @param file
     * @return
     */
    public static String read(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuffer fileData = new StringBuffer(1000);

            char[] buf = new char[1024];
            int numRead = 0;

            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }
            reader.close();

            return fileData.toString();

        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: "	+ file, e);
        }
    }

    public static String[] readLines(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> list=new ArrayList<String>();
            String aLine;
            while ((aLine = reader.readLine()) != null) {
                list.add(aLine);
            }
            reader.close();

            return list.toArray(new String[0]);

        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: "	+ file, e);
        }
    }

    /**
     * Get the Host Name
     * @return Host Name
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName().toLowerCase();
        } catch (UnknownHostException e) {
            return "Unknown host";
        }
    }

    /**
     * Get the Host IP
     * @return Host IP
     */
    public static String getHostIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "0.0.0.0";
        }
    }

    public static void rebootHost() {
        //reboot the machine in 5 seconds
        Email email=new Email();
        String hostName=TestProperty.get("host.name");
        email.subject="Host "+hostName+" will be rebooted soon.";
        email.text="Host "+TestProperty.get("host.name")+" cannot deal with certain errors and will be rebooted soon";

        String[] attachments=new String[1];
        attachments[0]=TestProperty.get("project.path")+"/"+TestProperty.get("host.name")+"_error_screen";
        Util.catchScreenShot(attachments[0]);

        email.attachments=attachments;

        email.send();
        logger.info(hostName+" is rebooting now ......");
        Executor reboot=new Executor("SHUTDOWN -r -t 05",false);
        reboot.start();
    }

    public static String[] getRegisteredRunners() {
        String runnerList=TestProperty.get("test.runner");
        String[] runners=runnerList.split(",");
        return runners;
    }

    public static List<String> getListFromArray(String[] texts) {
        List<String> list=new ArrayList<String>();
        for(String text:texts) {
            if (text!=null && text.length()>0) {
                list.add(text);
            }
        }

        return list;
    }

    public static boolean waitToFinish(Thread t, int timeout) {
        if(timeout>0) {
            sleep(timeout*1000);
        }

        int count=300;
        while(t.isAlive() && count>0) {
            //wait for another 5 minutes
            sleep(1000);
            count--;
        }

        return !t.isAlive();
    }

    public static void sleep(long millseconds) {
        try {
            Thread.sleep(millseconds);
        } catch (Exception e) {}
    }

    /**
     * Reverse the timestamp into a date string "yyyy/mm/dd hh:mm"
     * @param timestamp
     * @return
     */
    public static String parseTime(String timestamp) {
        String year=timestamp.substring(0,4);
        String month=timestamp.substring(4,6);
        String day=timestamp.substring(6,8);
        String hour=timestamp.substring(8,10);
        String minute=timestamp.substring(10,12);
        String time=year+"/"+month+"/"+day+" "+hour+":"+minute;

        return time;
    }

    public static int getRetrySleep() {
        return Integer.parseInt(TestProperty.get("jms.retry.sleep","60"));
    }

    public static int getToolCode(String cmdStr) {
        String[] tokens=matches(cmdStr, "tool=\\d");
        if(tokens.length<1) {
            return 1;
        } else {
            String toolCode=tokens[0].substring(tokens[0].length()-1);
            return Integer.parseInt(toolCode);
        }
    }

    public static int getBranch(String cmdStr) {
        String[] tokens=matches(cmdStr, "branch=\\d");
        if(tokens.length<1) {
            return 0;
        } else {
            String branch=tokens[0].substring(tokens[0].length()-1);//only one digit
            return Integer.parseInt(branch);
        }
    }

    public static String getCaseID(String cmdStr) {
        String[] tokens=matches(cmdStr,"testcaseId=\\d+");

        return tokens[0].substring(11);
    }

    public static String getScriptID(String cmdStr) {
        String[] tokens=matches(cmdStr,"scriptid=\\d+");

        return tokens[0].substring(9);
    }

    public static String getSetupScriptTable(String cmdStr) {
        String[] tokens=matches(cmdStr,":tablename=\\d+:");

        return tokens[0].replaceAll(":", "").trim().substring(10);
    }

    public static String createRunningID() {
        return (new SimpleDateFormat("yyyyMMddHHmmss")).format(Calendar.getInstance().getTime()).toString();
    }

    public static String arrayToString(String[] texts, boolean withSingleQuote, String delimit) {
        StringBuffer text=new StringBuffer();

        for(int i=0;i<texts.length;i++) {
            if(withSingleQuote) {
                text.append("'");
            }
            text.append(texts[i]);

            if(withSingleQuote) {
                text.append("'");
            }
            if(i<texts.length-1) {
                text.append(delimit);
            }
        }
        return text.toString();

    }

    public static String[] arrayToStrings(String[] texts, boolean withSingleQuote, String delimit, int sizeLimit) {
        StringBuffer text=new StringBuffer();

        int size1=texts.length;
        int size2=size1/sizeLimit+(size1%sizeLimit>0?1:0);

        String[] strArray=new String[size2];
        //count for strArray
        int count=0;
        //count for strArray item
        int j=0;
        for(int i=0;i<texts.length;i++) {
            if(withSingleQuote) {
                text.append("'");
            }
            text.append(texts[i]);
            j++;
            if(withSingleQuote) {
                text.append("'");
            }

            if((j<sizeLimit && count<size2-1)||(count==size2-1 && j<(size1%sizeLimit))){
                text.append(delimit);
            }else{
                strArray[count]=text.toString();
                text=new StringBuffer();
                count++;
                j=0;
            }

        }
        return strArray;

    }

    public static String[] listToStrings(List<String> list){
        String[] str = list.toArray(new String[list.size()]);
        return str;
    }

    public static String listToString(List<String> texts, boolean withSingleQuote, String delimit) {
        StringBuffer text=new StringBuffer();

        for(int i=0;i<texts.size();i++) {
            if(withSingleQuote) {
                text.append("'");
            }
            text.append(texts.get(i));

            if(withSingleQuote) {
                text.append("'");
            }
            if(i<texts.size()-1) {
                text.append(delimit);
            }
        }
        return text.toString();

    }

    public static String[] listToStrings(List<String> texts, boolean withSingleQuote, String delimit, int sizeLimit) {
        StringBuffer text=new StringBuffer();

        int size1=texts.size();
        int size2=size1/sizeLimit+(size1%sizeLimit>0?1:0);

        String[] strArray=new String[size2];
        //count for strArray
        int count=0;
        //count for strArray item
        int j=0;
        for(int i=0;i<texts.size();i++) {
            if(withSingleQuote) {
                text.append("'");
            }
            text.append(texts.get(i));
            j++;
            if(withSingleQuote) {
                text.append("'");
            }

            if((j<sizeLimit && count<size2-1)||(count==size2-1 && j<(size1%sizeLimit))){
                text.append(delimit);
            }else{
                strArray[count]=text.toString();
                text=new StringBuffer();
                count++;
                j=0;
            }

        }
        return strArray;

    }

    public static String getCaseName(String id) throws SQLException {
        DataBase db=DataBase.getInstance();
//		db.connect();
        String query="select casename from test_cases where id="+id;
        List<String> resultList = db.executeQuery(query, "casename");
//		ResultSet rs=db.executeQuery(query);
//		rs.next();
        String name=resultList.get(0);
//		db.disconnect();

        return name;
    }

    public static String getToolCode(String env, String id) throws SQLException {
        DataBase db=DataBase.getInstance();
//		db.connect();
        String query="select "+env+"_tool"+" from test_cases where id="+id;
//		ResultSet rs=db.executeQuery(query);
//		rs.next();
        List<String> resultList = db.executeQuery(query, env+"_tool");
        String tool=resultList.get(0);
//		db.disconnect();

        return tool;
    }

    public static String getSelectorCode(String hostname){
        String selector_code="(runners like '%"+hostname+"%' OR runners = 'ALL') ";
//		String selector_code="(runners like '%,"+hostname+",%' OR runners like '%NONE%') ";
        String sm_path = TestProperty.get("storemanager.install.dir");
        String smEnv = searchSMEnvByHostname(hostname);

        boolean isRFTRunner=isRFTRunner(hostname);
        boolean isEFTRunner=isEFTRunner(hostname);
        boolean isEFTRunning=isEFTRunning();

        if(isEFTRunner){
            selector_code = selector_code + "AND exetime < "+Calendar.getInstance().getTimeInMillis()+" ";
            if(isEFTRunning){
                String caseID = getEFTRunningCaseID();
                selector_code = selector_code + "AND ((branchTotal > 1 AND caseID = '"+caseID+"') OR branchTotal <= 1 ) ";
            }
        }

        //Search registered SM_Env by hostname
        //machines were not registered in SM_Env
        if(smEnv==null){
            if(isRFTRunner){
                //machine installed Selenium,RFT
                selector_code = selector_code+"AND (appcode = '2' OR appcode = '0')";
                return selector_code;
            }else{
                //machine installed Selenium, not installed RFT
                selector_code = selector_code+"AND appcode = '0' AND toolcode <> '1'";
                return selector_code;
            }
        }

        //machines have registered in SM_Env
        //should verify RFT/SM were installed or not
        if(!isRFTRunner){
            //machine not installed RFT, installed Selenium
            selector_code = selector_code+"AND appcode = '0' AND toolcode <> '1'";
            return selector_code;
        } else {
            if(!existFile(sm_path,"StoreManager.exe")){
                //machine installed RFT/Selenium, not installed SM
                selector_code = selector_code+"AND (appcode = '2' OR appcode = '0')";
                return selector_code;
            }else{
                //machine installed RFT/Selenium/SM
//				selector_code = selector_code;
                return selector_code;
            }
        }


    }

    public static boolean isRFTRunner(String hostname) {
        //rft runners qualification:
        //1. registered in rft.runners property
        //2. rft installed
        String rft_path = TestProperty.get("rational_ft.install.dir");
        rft_path = rft_path + "\\SDP\\FunctionalTester\\bin";
        return TestProperty.get("rft.runners").contains(hostname+",") && existFile(rft_path,"rational_ft.jar");
    }

    private static boolean existFile(String directory, String name){
        Boolean flag = false;
        File path = new File(directory);

        if(path == null || !path.exists()|| path.isFile()){
            return flag;
        }else{
            File[] files = path.listFiles();
            int list = files.length;
            for(int i=0;i<list;i++){
                if((files[i].getName()).equals(name)){
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static String searchSMEnvByHostname(String hostname){
        String env = null;
        String[] def_env = TestProperty.get("test.env").split(",");
        String[] temp = null;
        for(int i=0;i<def_env.length;i++){
            if(TestProperty.get(def_env[i] + ".storemanager.install")==null){
                continue;
            }
            temp = TestProperty.get(def_env[i] + ".storemanager.install").split(",");
            for(int j=0;j<temp.length;j++){
                if(temp[j].equalsIgnoreCase(hostname)){
                    env = def_env[i];
                    break;
                }
            }
        }
        return env;
    }

    public static void createTestExecutionDetailRecord(List<String> caseIDs,String runningId) {
        createTestExecutionDetailRecord(caseIDs.toArray(new String[0]),runningId);
    }

    public static void createTestExecutionDetailRecord(String caseIDs,String runningId) {
        String[] ids=caseIDs.split(",");
        createTestExecutionDetailRecord(ids,runningId);
    }

    public static void createTestExecutionDetailRecord(String caseID,String runningId, String tool) {
        DataBase db=DataBase.getInstance();
        String querys="insert into test_execution_details (case_id,execution_id,tool,status,result) values ("+caseID+",'"+runningId+"',"+tool+","+ TestConstants.TESTCASE_PENDING+","+TestConstants.RESULT_NA+")";
        int result=db.executeUpdate(querys);
        if(result<1) {
            logger.warn("Failed to record cases in TEST_EXECUTION_DETAILS table!");
        }
    }

    public static void createTestExecutionDetailRecord(String[] ids,String runningId) {
        DataBase db=DataBase.getInstance();
        String[] querys=new String[ids.length];
        for(int i=0;i<ids.length;i++) {
            querys[i]="insert into test_execution_details (case_id,execution_id,status,result) values ("+ids[i]+",'"+runningId+"',"+TestConstants.TESTCASE_PENDING+","+TestConstants.RESULT_NA+")";
        }
        db.connect();
        db.executeBatch(querys);
        db.disconnect();

    }

    public static void createTestExecutionRecord(String testsuite,String env, String runningId, String emailto,int total,int repeat, boolean failedOnly) {
        DataBase db=DataBase.getInstance();
        String values="'"+runningId+"','"+testsuite+"','"+env+"','"+emailto+"',"+total+","+repeat+","+(failedOnly?"1":"0")+","+TestConstants.TESTCASE_PENDING+",null,null";
        String query="insert into test_execution values ("+values+")";
        int result=db.executeUpdate(query);
        if(result<0) {
            logger.warn("Failed to record in TEST_EXECUTION table!");
        }
    }

    public static boolean isEFTRunning() {
        boolean inProgress=false;

        String query="select val from qa_automation where var='EFTRunning'";
        DataBase db=DataBase.getInstance();
        List<String> resultList = db.executeQuery(query, "VAL");
        if(resultList!=null){
            inProgress=Integer.parseInt(resultList.get(0))==1;
        }else{
            throw new RuntimeException("Query returned nothing.");
        }

        return inProgress;
    }

    public static void setEFTRunning(boolean inProgress) {
        int val=inProgress?1:0;
        String query="update qa_automation set val='"+val+"' where var='EFTRunning'";
        DataBase db=DataBase.getInstance();
        int count=db.executeUpdate(query);
        if( count<=0) {
            throw new RuntimeException("Failed to set eft running value");
        }
    }

    public static String getEFTRunningCaseID() {
        String query="select val from qa_automation where var='EFTRunningCaseID'";
        DataBase db=DataBase.getInstance();
        String caseID = "";
        List<String> resultList = db.executeQuery(query, "VAL");
        if(resultList!=null){
            caseID=resultList.get(0);
        }else{
            throw new RuntimeException("Query returned nothing.");
        }

        return caseID;
    }

    public static void setEFTRunningCaseID(String caseID) {
        String query="update qa_automation set val='"+caseID+"' where var='EFTRunningCaseID'";
        DataBase db=DataBase.getInstance();
        int count=db.executeUpdate(query);
        if( count<=0) {
            throw new RuntimeException("Failed to set eft running case id value");
        }
    }

    public static boolean isEFTRunner(String hostname) {
        return TestProperty.get("eft.runners").contains(hostname+",");
    }

    public static String constructRFTCMD(String script_path) {
        String rft_path=TestProperty.get("rational_ft.install.dir");

        //construct the script classpath
        String[] jars=Util.getExternalJars(script_path+"/lib");
        String classpath="\""+rft_path+"\\SDP\\FunctionalTester\\bin\\rational_ft.jar\"";
        for(int i=0;i<jars.length;i++) {
            classpath +=";\""+script_path+"\\lib\\"+jars[i]+"\"";
        }

        //construct the command line for RFT 8.1
        //java -classpath rational_ft.jar com.rational.test.ft.rational_ft -datastore <directory> -log <logname> -playback <script-name> [-args <values>]
        String cmd="java -classpath "+classpath+" com.rational.test.ft.rational_ft -datastore "+script_path+" -log Test_Log -playback testdriver.TestcaseLoader -args";

        return cmd;
    }

    public static String constructSeleniumCMD(String script_path) {
        //create the script classpath
        String[] jars=Util.getExternalJars(script_path+"/lib");

        String classpath=script_path;
        for(int i=0;i<jars.length;i++) {
            classpath +=";\""+script_path+"\\lib\\"+jars[i]+"\"";
        }

        //construct the command line for Selenium
        String cmd="java -classpath "+classpath+" testdriver/TestcaseLoader";

        return cmd;
    }

    public static String getDateStringPattern(String date) {
        if (date.matches("[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}"))
            return "M/d/yyyy";
        else if (date.matches("[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}"))
            return "yyyy/M/d";
        else if (date.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}"))
            return "yyyy-M-d";
        else if (date.matches("[0-9]{1,2}-[0-9]{1,2}-[0-9]{4}"))
            return "M-d-yyyy";
        else if (date.matches("[0-9]{1,2}-[a-zA-Z]{3}-[0-9]{2}"))
            return "d-MMM-yy";
        else
            return null;
    }

    /**
     * Get case name list by date
     * Date parameter could be specific date, or date range
     * @param date
     * @return
     */
    public static List<String> getCaseNamesByDate(String... date){
        String query = "";
        if(date.length == 1)
//			query = "select * from test_cases where insert_date>=str_to_date('"+date[0]+"', '%m/%d/%Y')"; //mysql format
            query = "select * from test_cases where insert_date>=to_date('"+date[0]+"', 'MM/DD/YYYY')";
        else
            //from date1 to date2
            query = "select * from test_cases where insert_date>=to_date('"+date[0]+"', 'MM/DD/YYYY') and insert_date<=to_date('"+date[1]+"', 'MM/DD/YYYY')";
        DataBase db=DataBase.getInstance();
        List<String> names=db.executeQuery(query, "casename");
        return names;
    }

    public static String getTestCaseOwnerByCaseID(String caseID) {
        String query="select caseowner from test_cases where ID="+caseID;
        DataBase db=DataBase.getInstance();
        List<String> ownerList = db.executeQuery(query, "caseowner");
        return ownerList.get(0);
    }

    public static String getFailedException(String runningID, String caseId) {
        String query="select exception from test_execution_details where case_id="+caseId+" and execution_id="+runningID;
        DataBase db=DataBase.getInstance();
        List<String> exceptionList = db.executeQuery(query, "exception");
        if(exceptionList == null || exceptionList.size()<1){
            logger.error("Could not get exception from execution details table.");
            return "";
        }
        return exceptionList.get(0);
    }

    public static String getIDsByStatusFromDataTable(String tablename, String env, String inlist, boolean status) {
        String ids="";
        System.out.println("Get "+(status==true?"Passed":"Failed")+" ids from table "+tablename+" in ("+inlist+")");
        String query="select * from "+tablename+" where "+env+"_result"+(status==true?"="+TestConstants.RESULT_PASSED:" is null or "+env+"_result!="+TestConstants.RESULT_PASSED);

        query += " and id in ("+inlist+")";
        DataBase db=DataBase.getInstance();
        List<String> resultlist=db.executeQuery(query, "id");
        String[] arr=resultlist.toArray(new String[resultlist.size()]);
        ids=arrayToString(arr, false, ",");
        return ids;
    }

    public static String getPassedRecordsFromDataTable(String tablename, String env, String inlist) {
        String result="";
        System.out.println("Get support script successful records from table "+tablename+" where id in "+inlist);
        String query = "select * from "+tablename+" where "+env+"_result="+TestConstants.RESULT_PASSED+" and id in ("+inlist+")";
        DataBase db=DataBase.getInstance();
        List<String> resultlist=db.executeQuery(query, "id");
        String[] arr=resultlist.toArray(new String[resultlist.size()]);
        result=arrayToString(arr, false, ",");
        return result;
    }


    public static String getNotPassedRecordsFromDataTable(String tablename, String env, String inlist) {
        String result="";
        System.out.println("Get support script failed records from table "+tablename+" where id in "+inlist);
        String query = "select * from "+tablename+" where ("+env+"_result is null or "+env+"_result!="+TestConstants.RESULT_PASSED+") and id in ("+inlist+")";
        DataBase db=DataBase.getInstance();
        List<String> resultlist=db.executeQuery(query, "id");
        String[] arr=resultlist.toArray(new String[resultlist.size()]);
        result=arrayToString(arr, false, ",");
        return result;
    }

    public static boolean isEmpty(String str) {
        return str ==null || str.trim().length()<1;
    }

    public static boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    public static String[] getCaseIDStringArrayByCaseName(List<String> caseNames) {
        String[] caseNameListStrs = Util.listToStrings(caseNames, true, ",", Integer.parseInt(TestProperty.get("size_limitation")));
        String[] caseIDListStrs = new String[caseNameListStrs.length];
        DataBase db=DataBase.getInstance();
        for(int i=0;i<caseNameListStrs.length;i++) {
            String query="select * from test_cases where casename in("+caseNameListStrs[i]+")";
            List<String> names=db.executeQuery(query, "id");
            caseIDListStrs[i]=Util.listToString(names, true, ",");
        }
        return caseIDListStrs;
    }

    /**
     *
     * @param list
     * @param threshold
     * @return
     */
    public static List<String[]> subArray(List<String> list, int threshold){
        List<String[]> result = new ArrayList<String[]>();
        if(list==null || list.size()<1)
            return null;

        int listLength=list.size();
        if(threshold>=listLength || threshold==0){
            result.add((String[]) list.toArray((new String[list.size()])));
            return result;
        }

        int lastListLength = 0;
        if (listLength % threshold != 0)
            lastListLength=listLength % threshold;

        int lastCount=listLength / threshold;
        int count = 0;
        int j = 0;//count for each array
        int k = 0;//index for last array
        String[] temp = null;
        String[] lastArray = null;

        for (int i = 0; i < listLength; i++) {
            if (i == 0)
                temp = new String[threshold];

            if (j == threshold && i != 0) {
                result.add(temp);
                temp = new String[threshold];
                j = 0;
            }
            if (count >= lastCount * threshold && count <= listLength) {
                if (lastArray == null)
                    lastArray = new String[lastListLength];
                lastArray[k] = list.get(i);
                k++;
            }
            temp[j] = list.get(i);
            j++;
            count++;
        }
        if (lastListLength == 0)
            result.add(temp);
        else
            result.add(lastArray);
        return result;
    }

    public static void updateTestExecutionStatus(String runningId, int toStatus,long startTime, long endTime) {
        DataBase db=DataBase.getInstance();

        String start="",end="";
        if(startTime>0) {
            start="start_time=FROM_UNIXTIME("+(startTime/1000)+")";
        }

        if(endTime>0) {
            end="end_time=FROM_UNIXTIME("+(endTime/1000)+")";
        }

        String query="update test_execution set status_code="+toStatus+(start.length()>0?","+start:"")+(end.length()>0?","+end:"")+" where id in ('"+runningId+"')";
        int result=db.executeUpdate(query);
        if(result<0) {
            logger.warn("Failed to record in TEST_EXECUTION table!");
        }
    }

    public static String getAttribute(String cmd, String attr) {
        String value=null;
        int startIndex=cmd.indexOf(attr+"=");
        int endIndex=cmd.indexOf(":", startIndex);
        if(endIndex<0){
            endIndex=cmd.length();
        }
        value=cmd.substring(startIndex+attr.length()+1, endIndex);
        return value;
    }

    public static int getAppCodeByCaseName(String aCase, int branchTotal) {
        int app=TestConstants.WEB;

        if(branchTotal>0)
            return TestConstants.EFT;

        if(aCase.matches(TestProperty.get("StoreManagerCases_Format")))
            return TestConstants.SM;

        if(aCase.matches(TestProperty.get("ReportCases_Format")))
            return TestConstants.REPORT;

        return app;
    }

    public static boolean isSMRunner(String hostname) {
        return TestProperty.get("storemanager.runner").contains(hostname+",");
    }

    public static boolean isReportRunner(String hostname) {
        return TestProperty.get("report.runners").contains(hostname+",");
    }

    public static void resetResultForDataTable(String dataTable, String idlist, String env, int result) {
        DataBase db=DataBase.getInstance();
        String update="update "+dataTable+" set "+env+"_result="+result;
        if(!isEmpty(idlist))
            update += " where id in ("+idlist+")";
        db.executeUpdate(update);
    }

    public static void resetResultForDataTable(String dataTable, String env, int result){
        resetResultForDataTable(dataTable, "", env, result);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String[] getSortedHashtableKeys(Hashtable table){
        Enumeration<String> keys=table.keys();
        List<String> list=new ArrayList<String>();
        while(keys.hasMoreElements()) {
            list.add(keys.nextElement());
        }
        String[] arr = (String[])list.toArray(new String[list.size()]);
        Arrays.sort(arr);
        return arr;
    }

//	/**
//	 * fill with test cases array with prefix which used for -c option
//	 * @param caseArr
//	 * @param prefix
//	 * @return
//	 */
//	public static List<String> fillWithTestCasePrefix(String[] caseArr, String prefix) {
//		List<String> caseList=new ArrayList<String>();
//		for(String caseName:caseArr)
//			caseList.add(prefix+"."+caseName);
//		return caseList;
//	}

    public static List<String> getTestCasesList(String[] shortCaseNames, String suite) {
        String where="";

        for(String name: shortCaseNames) {
            if(where.length()>0) {
                where+=" or ";
            }
            where+="casename like '%"+name+"'";
        }
        where ="where "+where;
        String selectfrom="";
        if(suite.startsWith("supportscripts.qasetup") || suite.equalsIgnoreCase("qasetup")) {
            where=where.replaceAll("casename", "script_name");
            selectfrom="select script_name as casename from test_setupscripts ";
        } else {
            selectfrom="select casename from test_cases ";
        }
        String query=selectfrom+where;
        DataBase db=DataBase.getInstance();

        return db.executeQuery(query, "casename");
    }

    public static String inputStreamToString(InputStream input) throws IOException {
        StringWriter  writer=new StringWriter();
        InputStreamReader reader=new InputStreamReader(input);

        char[] buffer = new char[1024*4];
        int n = 0;
        while (-1 != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);
        }
        return writer.toString();

    }

    public static String getAWObuild(String env) {
        String[] s=matches(getURLContent(TestProperty.get("test."+env.toLowerCase()+".url")), "(?<=>)\\d\\.\\d{2}\\.\\d{2}\\.\\d+(?= -)");
        String build=null;
        if(s.length>0 && s[0]!=null && s[0].length()>0)
            build= s[0];


        if(build ==null || build.length()<1){
            logger.warn("Failed to retrieve build for "+env+". try to use cashed build# at "+CASHED_BUILD);
            Properties cachedBuild=TestProperty.load(CASHED_BUILD);

            build= cachedBuild.getProperty(env);
        }
        logger.info(env+" build="+build);
        return build;
    }

    public static List<String> getAWObuild() {
        String[] envs=TestProperty.get("test.env").split(",");
        return getAWObuild(envs);
    }

    public static Properties getAWOBuildMap() {
        String[] envs=TestProperty.get("test.env").split(",");
        Properties buildMap=new Properties();

        for(String env:envs) {
            String build=getAWObuild(env);
            if(build!=null && build.length()>7) {
                buildMap.put(env, build);
            }
        }

        return buildMap;
    }

    public static List<String> getAWObuild(String[] envs) {
        List<String> builds=new ArrayList<String>();


        for(String env:envs) {
            if(!env.equals("live")) {
                String build=getAWObuild(env);
                if(!builds.contains(build) && build.length()>7) {
                    builds.add(build);
                }
            }
        }

        return builds;
    }

    public static Properties getModuleVersions(String awoBuild) {
        String url=TestProperty.get("test.ormsrelease.ivy.url");
        url=url.replaceAll("\\$<version>", awoBuild);
//		String xml=getURLContent(url);
        String[] modules=TestProperty.get("test.ormsrelease.modules").split(",");

        Properties p=new Properties();

        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(url);
            for(String m:modules) {
                @SuppressWarnings("unchecked")
                List<Node> nodes=doc.selectNodes("//dependencies/dependency[@name='"+m+"']");
                if(nodes.size()>0) {
                    String value=((Element)nodes.get(0)).attributeValue("rev");
                    p.setProperty(m, value);
                }

            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return p;
    }

    public static String getURLContent(String url) {
        if(url.matches("^https:.+")) {
            //try to handle possible untrusted certificate

            // Create a trust manager that does not validate certificate chains
            System.setProperty("jsse.enableSNIExtension", "false");
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                logger.warn(e);
            }
        }
        try{
            URL site=new URL(url);

            URLConnection con;
            String proxyHost=TestProperty.get("proxy.host","");
            if(proxyHost.length()>0) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(TestProperty.get("proxy.port","8080"))));
                con=site.openConnection(proxy);
            } else {
                con=site.openConnection();
            }


            con.setDoInput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.connect();
            return inputStreamToString(con.getInputStream());
        } catch(Exception e) {
            logger.warn("Failed to connect to "+url+" due to "+e.getMessage());
        }
        return "";
    }

    public static String getLastAWOBuild(String env) {
        DataBase db=DataBase.getInstance();
        String query="select distinct build from test_execution_details where build is not null and execution_id=(select max(ted.execution_id) from test_execution_details ted,test_execution te where ted.execution_id= te.id and te.qa_env='"+env+"' and ted.build is not null)";
        List<String> rs=db.executeQuery(query, "build");

        return rs.get(0);
    }

    public static int getPrdSanityResultById(String env, String id) {
        DataBase db=DataBase.getInstance();
        String query="select * from test_production where id="+id;
        List<String> rs=db.executeQuery(query, env+"_result");

        return Integer.parseInt(rs.get(0));
    }

}

class JarFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        return (name.endsWith(".jar"));
    }

}

