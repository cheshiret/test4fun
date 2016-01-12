package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.ActionFailedException;
import com.active.qa.automation.web.testapi.NotSupportedException;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Provide the information of the host in which the script is executed
 * Created by tchen on 1/11/2016.
 */
public class SysInfo {
    private static int ieVersion=-1;
    private static int ffVersion=-1;
    private static String os=null;
    private static Map<String, String> winEnv=null;

    /**
     * Get the Host Name
     * @return Host Name
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
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

    /**
     * Get the OS Name
     * @return OS Name
     */
    public static String getOSName() {
        if(os==null) {
            os= System.getProperty("os.name");
        }

        return os;
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Get the OS Version
     * @return OS Version
     */
    public static String getOSVersion() {
        return System.getProperty("os.version");
    }

    public static String getOSArchtechture() {
        return System.getProperty("os.arch");
    }

    public static String getUserDir() {
        return System.getProperty("user.dir").replaceAll("\\\\", "/");
    }

    public static String readRegistry(String location, String key) {
        try {
            Process process=Runtime.getRuntime().exec("reg query \""+location+"\" /v "+key);
            InputStream is=process.getInputStream();
            StringWriter sw=new StringWriter();

            int c;
            while((c=is.read())>0) {
                sw.write(c);
            }

            return sw.toString();

        } catch(Exception e) {
            throw new ActionFailedException(e);
        }
    }

    public static int getIEVersion() {
        if(ieVersion<=0) {
            String text=readRegistry("HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Internet Explorer","Version");

            String[] tokens=RegularExpression.getMatches(text, "\\d+(\\.\\d+){2,}");
            String verStr=tokens[0];

            ieVersion=Integer.parseInt(verStr.split("\\.")[0]);
        }

        return ieVersion;
    }

    public static int getFireFoxVersion() {
        if(ffVersion<=0) {
            String text=readRegistry("HKEY_LOCAL_MACHINE\\Software\\Mozilla\\Mozilla FireFox","CurrentVersion");

            String[] tokens=RegularExpression.getMatches(text, "\\d+(\\.\\d+)+");
            String verStr=tokens[0];

            ffVersion=Integer.parseInt(verStr.split("\\.")[0]);
        }
        return ffVersion;
    }

    /**
     * calculate a benchmark value for the current host. This value can be used to dynamically adjust the some synchronization time during the script execution
     * @return a integer benchmark value
     */
    public static int benchmark() {
        long starttime = System.nanoTime();
        long sum = 0;
        for (int indx=200000; --indx >= 0; ) sum += 42;
        long endtime = System.nanoTime();
        int value= (int)(endtime - starttime)/1000;
//	    RALogger.getInstance().debug("\tbenchmark="+value);
        return value;
    }

    /**
     * This method is used to append the specified classes or resources described by the given path to the list of URLs to search for classes and resources.
     * Any path ends with a "/" is assumed to refer to a directory. Otherwise, the URL is assumed to refer to a JAR file which will b opened as needed.
     * @param path
     */
    public static void addToClassPath(String path) {
        try {
            URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            Class<?> sysclass = URLClassLoader.class;
            URL u=new File(path).toURI().toURL();

            Method method = sysclass.getDeclaredMethod("addURL",new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysloader,new Object[]{ u });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve the environment variable of Windows OS. If the the provided key is not a windows environment variable name, null will be returned.
     * @param key
     * @return
     */
    public static String getWinEnvVariable(String key) {
        if(getOSName().startsWith("Windows")) {

            if(winEnv==null) {
                winEnv=System.getenv();
            }

            return winEnv.get(key);
        } else {
            throw new NotSupportedException("WinEnvVariable is only for Windows OS. Current OS is "+getOSName());
        }
    }


}

