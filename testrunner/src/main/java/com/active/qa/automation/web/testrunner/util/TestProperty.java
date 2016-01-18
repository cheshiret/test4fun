package com.active.qa.automation.web.testrunner.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by tchen on 1/18/2016.
 */
public class TestProperty {
    private static Logger logger=Logger.getLogger(TestProperty.class);
    private static Properties properties = new Properties();
    private static boolean loaded=false;
    public static String DEFAULT_USER="guest";
    public static String DEFAULT_PASSWORD="guest";
    private static final String PROPERTY="test.properties";

    public static Properties load(String path) {
        Properties p=new Properties();
        File f=new File(path);
        if(!f.exists()) {
            logger.warn("File "+path+" doesn't exist.");
        } else {
            try {
                FileInputStream in = new FileInputStream(f);
                p.load(in);
                in.close();
            } catch (IOException e) {
                logger.warn("Cannot load properties file: "+ path, e);
            }
        }

        return p;
    }

    public static void save(Properties p, String file) {
        File f=new File(file);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                logger.warn("Failed to create new file: "+file,e);
            }
        }

        try {
            p.store(new FileOutputStream(f), "");
        } catch (IOException e) {
            logger.warn("Failed to store properties to file: "+file, e);
        }
    }

    /**
     * Load the properties file
     * @param name - the properties file name
     */
    public static void load() {
        if(loaded) {
            return;
        }

        String path=Util.getProjectPath();
//		String[] tokens=Util.matches(path, "/\\w+\\.jar!");
//
//		if(tokens.length>0) {
//			int j=path.indexOf(tokens[0]);
//			path=path.substring(path.indexOf("/"),j);
//		} else {
//			path=path.replaceAll("util/TestProperty.class", "");
//		}
        TestProperty.put("project.path", path);

        path=path+"/properties/"+PROPERTY;
        try {
            FileInputStream in = new FileInputStream(path);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load properties file: "+ path, e);
        }

        String hostname="";
        try{
            hostname=InetAddress.getLocalHost().getHostName();
        }catch(UnknownHostException e) {
            hostname="Unknown host";
        }

        String hostip="";
        try{
            hostip=InetAddress.getLocalHost().getHostAddress();
        }catch(UnknownHostException e) {
            hostip="0.0.0.0";
        }
        properties.setProperty("host.name", hostname);
        properties.setProperty("host.ip", hostip);

        PropertyConfigurator.configure(properties);

        String mqtype=properties.getProperty("mq.type","openmq");
        properties.setProperty("mq.url", properties.getProperty(mqtype+".url"));

        //set proxy server
        String proxyHost=properties.getProperty("proxy.host");
        if(proxyHost !=null && proxyHost.length()>0) {
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", properties.getProperty("proxy.port",""));
            System.setProperty("http.proxyUser", properties.getProperty("proxy.user",""));
            System.setProperty("http.proxyPassword", properties.getProperty("proxy.pw",""));
        }


        loaded=true;
    }

    public static void reLoad() {
        Properties temp = new Properties();

        String path=get("project.path")+"/properties/"+PROPERTY;
        try {
            FileInputStream in = new FileInputStream(path);
            temp.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load properties file: "+ path, e);
        }

        Enumeration<Object> keys=temp.keys();
        while(keys.hasMoreElements()) {
            String key=(String) keys.nextElement();
            String newValue=temp.getProperty(key);
            String oldValue=properties.getProperty(key);
            if(oldValue==null || !newValue.equals(oldValue)) {
                logger.debug("Update key '"+key+"' from "+oldValue+" to "+newValue);
                properties.put(key, newValue);
            }
        }

        String mqtype=properties.getProperty("mq.type","openmq");
        properties.setProperty("mq.url", properties.getProperty(mqtype+".url"));
    }

    /**
     * Reset the log file name with the given name
     * @param file - the new log file name
     */
    public static void resetLogfile(String file) {
        put("log4j.appender.logfile.File",file+".log");
        PropertyConfigurator.configure(properties);
    }

    /**
     * Get the property with the given key
     * @param key
     * @return
     */
    public static String get(String key) {
        if(!loaded) {
            return null;
        }
        return properties.getProperty(key);
    }

    /**
     * Get the property with the given key, if the property key doesn't exist, return the defaultValue
     * @param key
     * @param defaultValue
     * @return
     */
    public static String get(String key, String defaultValue) {
        if(!loaded) {
            return null;
        }
        return properties.getProperty(key,defaultValue);
    }

    /**
     * Put the given key with given value into properties
     * @param key
     * @param value
     */
    public static void put(String key,String value) {
        properties.setProperty(key, value);
    }
}
