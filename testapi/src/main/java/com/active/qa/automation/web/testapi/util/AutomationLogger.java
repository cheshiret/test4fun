package com.active.qa.automation.web.testapi.util;

import java.io.File;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.active.qa.automation.web.testapi.exception.NotInitializedException;

/**
 * This class wraps Logger to maintain a general access point to logger
 * Created by tchen on 1/11/2016.
 */
public class AutomationLogger {
    private Logger logger;

    private static AutomationLogger _instance = null;

    /**
     * the loggerName
     */
    private String loggerName;

    //log file name with relative path to the logRootFolder
    private String logFileName;

    /**Root log folder will always be created in this logParrentFolder*/
    private String logParentFolder;

    /**
     * logRoolFolder is where all log paths start from
     */
    private String logRootFolder;

    private Properties properties = new Properties();

    /**
     * The logger name root in log4j.properties, corresponding to "log4j.logger.Functest"
     * when resetting loggerName, the LoggerNameRool "Functest." will always be prefix
     */
    private static final String loggerNameRoot = "Migrest";

    /**
     * the is the folder where the log file is stored
     */
    private String logFolder;

    /**
     * Single pattern to get the Instance of AutomationLogger. However, this getInstance method will not initialize the instance.
     * The AutomationLogger instance should be initialized via init() method in the bootstrap.
     *
     * @return instance of AutomationLogger
     */
    public static AutomationLogger getInstance() {
        if (null == _instance) {
            throw new NotInitializedException("The AutomationLogger is not initialized yet. Please initialize it by calling init() method.");

        }
        return _instance;
    }

    /**
     * get the current logger instance and
     *
     * @param name
     * @return
     */
    public static AutomationLogger getInstance(Object name) {
        getInstance().setLogger(name);
        return _instance;
    }

    /**
     * Initialize with the project full path and log4j.properties full path. The AutomationLogger instance will be initialized as:
     * logParentFolder=project parent folder calculated based on project path, this value cannot be reset later.
     * logRootFolder=logParentFolder/functest_log, the default logRootFolder name "functest_log" can be reset later by calling resetLogRootFolder().
     * logFileName=functest.log, logFileName can contain the relative path to the logRootFolder. This value can be reset later by calling resetLogfileName().
     * A log file path is constructed by the above 3 parts: logParentFolder/logRootFolder/logFileName
     * @param projectPath - the project full path
     * @param log4jProp - the log4j.properties full path
     */
    public static void init(String projectPath, String log4jProp) {
        if(_instance==null) {
            //set the logParentFolder as the parent folder of project path
            String path = projectPath;

            if (path.endsWith("\\")) {
                path = path.substring(0, path.length() - 1);
            }

            int index = path.lastIndexOf("\\");
            path = path.substring(0, index);

            String logParentFoler=path;

            _instance=new AutomationLogger(logParentFoler,null,null,null,log4jProp);

        }


    }

    /**
     * Construct a Logger instance
     *
     * @param logParentFolder - this is the parent folder of root log folder. This is mandatory and cannot be null or empty
     * @param logRootFolder - the log folder to keep all log files. If not provided, use default "functest_logs";
     * @param loggerName - the logger name to be displayed in log output. If not provided, loggerNameRoot will be used
     * @param logFileName - the log file name. If not provided, use default "functest.log"
     * @param log4jProp - the log4j.properties full path. If not provided, the default log configuration will be used.
     */
    private AutomationLogger(String logParentFolder, String logRootFolder, String loggerName,String logFileName, String log4jProp) {
        this.logParentFolder=logParentFolder;
        //this.logRootFolder=isEmpty(logRootFolder)?"functest_logs":logRootFolder;
        //this.logFileName=isEmpty(logFileName)?"functest":logFileName;
        this.logRootFolder=isEmpty(logRootFolder)?"Migrtest_logs":logRootFolder;
        this.logFileName=isEmpty(logFileName)?"Migrtest":logFileName;

        setLogger(loggerName);
        properties = new Properties();
        if(!isEmpty(log4jProp)) {
            TestProperty.loadProperty(log4jProp, properties);
        }
        resetLogFile();
    }

    private boolean isEmpty(String value) {
        return value==null || value.length()<1;
    }

    public String getFullLogFileName() {
        String fullLogFileName = logParentFolder+File.separator+logRootFolder+File.separator+logFileName+".log";

        if(!fullLogFileName.endsWith(".log")) {
            fullLogFileName +=".log";
        }

        return fullLogFileName;
    }

    /**
     * Set Log File Name with the relative path to the logRootFolder by the given parameter
     *
     * @param logFileName - log file name plus the relative path
     */
    public void resetLogfileName(String logFileName) {
        this.logFileName=logFileName;
        resetLogFile();
    }

    /**
     * reset the root log folder relative to the logParentFolder
     * @param folderName - the root log name with relative paths to the logParentFolder
     */
    public void resetLogRootFolder(String folderName) {
        this.logRootFolder=folderName;
        resetLogFile();
    }

    public void resetLogFile() {
        String logFile = getFullLogFileName();
        logFolder=logFile.substring(0,logFile.lastIndexOf(File.separator));

        File file=new File(logFile);
        if(!file.getParentFile().exists()) {
            boolean success = file.getParentFile().mkdirs();
            if (!success) {
                throw new RuntimeException("Failed to create directory " + file.getParent());
            }
        }
        properties.put("log4j.appender.logfile.File", logFile);

        PropertyConfigurator.configure(properties);
    }

    /**
     * Set Logger with given parameter
     *
     * @param name
     */
    public void setLogger(Object name) {
        if(name==null || name.equals(loggerNameRoot) || name.equals("")) {
            loggerName=loggerNameRoot;
        } else {
            loggerName = loggerNameRoot + ".";
            if (name instanceof String)
                loggerName += (String) name;
            else if (name instanceof Class<?>)
                loggerName += ((Class<?>) name).getName();
            else
                loggerName += name.getClass().getName();
        }

        logger = Logger.getLogger(loggerName);
    }

    /**
     * Wrapped method to Record common Information
     *
     * @param info
     */
    public void info(Object info) {
        logger.info(info);
    }

    /**
     * Wrapped method to Record Waring Information
     *
     * @param warn
     */
    public void warn(Object warn) {
        logger.warn(warn);
    }

    /**
     * Wrapped method to Record Error information
     *
     * @param error
     */
    public void error(Object error) {
        logger.error(error);
    }

    /**
     * Wrapped method to Record fatal Information
     *
     * @param fatal
     */
    public void fatal(Object fatal) {
        logger.fatal(fatal);
    }

    /**
     * Wrapped method to Record debug Information
     *
     * @param debug
     */
    public void debug(Object debug) {
        logger.debug(debug);
    }

    public void debug(Object info, Throwable e) {
        logger.debug(info, e);
    }

    /**
     * Record common Information
     *
     * @param info
     * @param e
     */
    public void info(Object info, Throwable e) {
        logger.info(info, e);
    }

    /**
     * Record the Warning information
     *
     * @param warn
     * @param e
     */
    public void warn(Object warn, Throwable e) {
        logger.warn(warn, e);
    }

    /**
     * Record the error information
     *
     * @param error
     * @param e
     */
    public void error(Object error, Throwable e) {
        logger.error(error, e);
    }

    /**
     * Record fatal information
     *
     * @param fatal
     * @param e
     */
    public void fatal(Object fatal, Throwable e) {
        logger.fatal(fatal, e);
    }

    /**
     * Record the trace information
     *
     * @param trace
     */
    public void trace(Object trace) {
        logger.trace(trace);
    }

    public void trace(Object trace, Throwable e) {
        logger.trace(trace, e);
    }

    /**
     * Get Log Level
     *
     * @return
     */
    public Level getLevel() {
        return logger.getLevel();
    }

    /**
     * Set Log Level
     *
     * @param level
     */
    public void setLevel(Level level) {
        logger.setLevel(level);
    }

    public String getLoggerName() {
        return this.loggerName;
    }

    /**
     * Get Log File Name which excludes paths
     *
     * @return
     */
    public String getLogFileName() {
        return this.logFileName;
    }

    /**
     * get the current full path of the log folder where log files reside in
     * @return
     */
    public String getLogPath() {
        return logFolder;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * retrieve the current log root folder name with relative paths to log parent folder
     * @return
     */
    public String getLogRootFolder() {
        return logRootFolder;
    }

    /**
     * retrieve the current absolute log root path
     * @return
     */
    public String getFullLogRootPath() {
        return logParentFolder+File.separator+logRootFolder;
    }

    /**
     * retrieve the current log parent folder name with absolute path in the system
     * @return
     */
    public String getLogParentFolder() {
        return logParentFolder;
    }

}

