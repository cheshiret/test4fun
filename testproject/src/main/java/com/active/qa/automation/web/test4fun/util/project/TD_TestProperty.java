package com.active.qa.automation.web.test4fun.util.project;

import com.active.qa.automation.web.test4fun.datacollection.project.Customer;
import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.util.CryptoUtil;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import com.active.qa.automation.web.testapi.util.TestProperty;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Review the class to see if it is duplicated, should be removed or not
 * Created by tchen on 1/11/2016.
 */
public class TD_TestProperty extends TestProperty{

  private static Logger logger=Logger.getLogger(TestProperty.class);
  private static Properties testdata = new Properties();

  /**
   * Maintain a list of loaded properties file names
   */
  private static List<String> loaded = new ArrayList<String>();

  public static Customer loadCustomer = new Customer();

//	public static Address loadAddress = new Address();

//	public static NoteAndAlertInfo loadNoteAndAlert = new NoteAndAlertInfo();

  /**
   * Loads the properties file
   * @param file - the properties file with full path
   */

  public static void load(String file) {
    if(loaded.contains(file)) {
      logger.warn("Test Data file '"+file+"' has already been loaded.");
    } else {
      loaded.add( file);
      loadTestData(file, testdata);
    }
  }

  /**
   * load properties file into prop
   * @param file
   * @param prop
   * @throws IOException
   */
  public static void loadTestData(String file, Properties tdata)  {
    InputStream in=null;
    try {
      in = new FileInputStream(new File(file));
      tdata.load(in);
      in.close();
    } catch (Exception e) {
      throw new ActionFailedException(e);
    } finally {
      try{if(in!=null)in.close();} catch(Exception e){}
    }

  }

  public static void putTestData(String propertyKey, String value) {
    testdata.put(propertyKey, value);
  }

  public static boolean isDebug() {
    return getProperty("debug", "false").equalsIgnoreCase("true");
  }

  /**
   * Searches for the property with the specified key.
   *
   * @param key
   *            the property key
   * @return the value associated with the specified key or null if the key is not found.
   * @see java.util.Properties.getProperty(java.lang.String)
   */
  public static String getTestData(String key) {
    String value = testdata.getProperty(key);
    RegularExpression pwPattern =new RegularExpression(".+(pw|pwd|password)$",false);

    if (value == null) {
      value = "";
    } else if(pwPattern.match(key)) {
      value=CryptoUtil.decrypt(value);
    }

    return value;
  }

  /**
   * Searches for the property with the specified key.
   *
   * @param key
   *            the property key
   * @param defaultValue
   * @return the value associated with the specified key or defaultValue if the key is not found.
   * @see java.util.Properties.getProperty(java.lang.String, java.lang.String)
   */
  public static String getProperty(String key, String defaultValue) {
    return testdata.getProperty(key, defaultValue);
  }

  public static boolean getBooleanProperty(String key, boolean b) {
    return Boolean.valueOf(testdata.getProperty(key, Boolean.toString(b)));
  }

  /**
   * Verify if the property value is the same as the expected value with cases insensitive
   * @param key
   * @param expectedValue
   * @return
   */
  public static boolean verifyProperty(String key, String expectedValue) {

    String value=testdata.getProperty(key, "");

    return value.equalsIgnoreCase(expectedValue);
  }

  public static int getIntProperty(String key, int defaultValue) {
    String value= testdata.getProperty(key, defaultValue+"");

    return Integer.parseInt(value);
  }

  public static void savePropertyToFile(Properties p,	String file) {
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

  public static Customer loadCustomer(String key){
    Customer cust = new Customer();
    cust.fName = testdata.getProperty(key+".fName","");
    cust.lName = testdata.getProperty(key+".lName","");
    cust.hPhone = testdata.getProperty(key+".hPhone","");
    return cust;
  }







  public static void unloadTestData() {
    testdata.clear();
  }


}
