package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class will load and maintain all properties needed for automated scripts execution.
 * This class has to be initialized as first thing in the bootstrap, as the framework functions heavily rely on test properties
 * Created by tchen on 1/11/2016.
 */
public class TestProperty {
  private static Logger logger = Logger.getLogger(TestProperty.class);
  private static Properties properties = new Properties();

  /**
   * Maintain a list of loaded properties file names
   */
  private static List<String> loaded = new ArrayList<String>();

  /**
   * Loads the properties file
   *
   * @param file - the properties file with full path
   */

  public static void load(String file) {
    if (loaded.contains(file)) {
      logger.warn("Properties file '" + file + "' has already been loaded.");
    } else {
      loaded.add(file);
      loadProperty(file, properties);
    }
  }

  /**
   * load properties file into prop
   *
   * @param file
   * @param prop
   * @throws IOException
   */
  public static void loadProperty(String file, Properties prop) {
    InputStream in = null;
    try {
      in = new FileInputStream(new File(file));
      prop.load(in);
      in.close();
    } catch (Exception e) {
      throw new ActionFailedException(e);
    } finally {
      try {
        if (in != null) in.close();
      } catch (Exception e) {
      }
    }

  }

  public static void putProperty(String propertyKey, String value) {
    properties.put(propertyKey, value);
  }

  public static boolean isDebug() {
    return getProperty("debug", "false").equalsIgnoreCase("true");
  }

  /**
   * Searches for the property with the specified key.
   *
   * @param key the property key
   * @return the value associated with the specified key or null if the key is not found.
   * @see java.util.Properties.getProperty(java.lang.String)
   */
  public static String getProperty(String key) {
    String value = properties.getProperty(key);
    RegularExpression pwPattern = new RegularExpression(".+(pw|pwd|password)$", false);

    if (value == null) {
      value = "";
    } else if (pwPattern.match(key)) {
      value = CryptoUtil.decrypt(value);
    }

    return value;
  }

  /**
   * Searches for the property with the specified key.
   *
   * @param key          the property key
   * @param defaultValue
   * @return the value associated with the specified key or defaultValue if the key is not found.
   * @see java.util.Properties.getProperty(java.lang.String, java.lang.String)
   */
  public static String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  public static boolean getBooleanProperty(String key, boolean b) {
    return Boolean.valueOf(properties.getProperty(key, Boolean.toString(b)));
  }

  /**
   * Verify if the property value is the same as the expected value with cases insensitive
   *
   * @param key
   * @param expectedValue
   * @return
   */
  public static boolean verifyProperty(String key, String expectedValue) {

    String value = properties.getProperty(key, "");

    return value.equalsIgnoreCase(expectedValue);
  }

  public static int getIntProperty(String key, int defaultValue) {
    String value = properties.getProperty(key, defaultValue + "");

    return Integer.parseInt(value);
  }

  public static void savePropertyToFile(Properties p, String file) {
    File f = new File(file);
    if (!f.exists()) {
      try {
        f.createNewFile();
      } catch (IOException e) {
        logger.warn("Failed to create new file: " + file, e);
      }
    }

    try {
      p.store(new FileOutputStream(f), "");
    } catch (IOException e) {
      logger.warn("Failed to store properties to file: " + file, e);
    }
  }

}

