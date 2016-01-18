package com.active.qa.automation.web.test4fun.util.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.active.qa.automation.web.testapi.exception.ActionFailedException;
import com.active.qa.automation.web.testapi.util.CryptoUtil;
import com.active.qa.automation.web.testapi.util.RegularExpression;
import com.active.qa.automation.web.testapi.util.TestProperty;
import org.apache.log4j.Logger;

/**
 * Review the class to see if it is duplicated, should be removed or not
 * Created by tchen on 1/11/2016.
 */
public class TD_TestProperty  extends TestProperty {
    private static Logger logger=Logger.getLogger(TD_TestProperty.class);
    private static Properties properties = new Properties();


    /**
     * Maintain a list of loaded properties file names
     */
    private static List<String> loaded = new ArrayList<String>();

    /**
     * Loads the properties file
     * @param file - the properties file with full path
     */

    public static void load(String file) {
        if(loaded.contains(file)) {
            logger.warn("Properties file '"+file+"' has already been loaded.");
        } else {
            loaded.add( file);
            loadProperty(file, properties);
        }
    }



    /**
     * load properties file into prop
     * @param file
     * @param prop
     * @throws IOException
     */
    public static void loadProperty(String contract, String file, Properties prop)  {
        InputStream in=null;
        try {
            in = new FileInputStream(new File(file));
            prop.load(in);
            in.close();
        } catch (Exception e) {
            throw new ActionFailedException(e);
        } finally {
            try{if(in!=null)in.close();} catch(Exception e){}
        }

    }

    public static void putProperty(String propertyKey, String value) {
        properties.put(propertyKey, value);
    }

    public static void savePropertyToFile(String contract, Properties p, String file) {
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
     * Searches for the property with the specified key.
     *
     * @param key
     *            the property key
     * @return the value associated with the specified key or null if the key is not found.
     * @see java.util.Properties.getProperty(java.lang.String)
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        RegularExpression pwPattern =new RegularExpression(".+(pw|pwd|password)$",false);

        if (value == null) {
            value = "";
        } else if(pwPattern.match(key)) {
            value= CryptoUtil.decrypt(value);
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
        return properties.getProperty(key, defaultValue);
    }

    public static boolean getBooleanProperty(String key, boolean b) {
        return Boolean.valueOf(properties.getProperty(key, Boolean.toString(b)));
    }

    /**
     * Verify if the property value is the same as the expected value with cases insensitive
     * @param key
     * @param expectedValue
     * @return
     */
    public static boolean verifyProperty(String key, String expectedValue) {

        String value=properties.getProperty(key, "");

        return value.equalsIgnoreCase(expectedValue);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value= properties.getProperty(key, defaultValue+"");

        return Integer.parseInt(value);
    }

    public static void savePropertyToFile(Properties p,	String file, String key, String value_) throws IOException, FileNotFoundException {
        File f=new File(file);
        if(!f.exists()) {
            f.createNewFile();
        }

        InputStream in = new FileInputStream(f);
        p.load(in);
        in.close();

        OutputStream out = new FileOutputStream(f);
        p.setProperty(key, value_);
        p.store(new FileOutputStream(f), "");
        out.close();

    }
}

//	  public static String getPropertyValue( String namespace, String name ) {
//	    Property property = findProperty( namespace, name );
//	    return property != null ? property.getValue() : null;
//	  }
//
//	  public static String getContractPropertyValue( String name ) {
//		    return getPropertyValue( CONTRACT_NAMESPACE, name );
//		  }
//
//	  public static String setPropertyValue( String namespace, String name, String value ) {
//		    String oldValue;
//		    Property property = findProperty( namespace, name );
//		    if (property == null) {
//		      property = new Property();
//		      property.setNamespace( namespace );
//		      property.setName( name );
//		      oldValue = null;
//		    }
//		    else {
//		      oldValue = property.getValue();
//		    }
//
//		    property.setValue( value );
//
//		    EjbUtil.dao().store( property );
//
//		    return oldValue;
//		  }
//
//	  public static String setContractPropertyValue( String name, String value ) {
//		    return setPropertyValue( CONTRACT_NAMESPACE, name, value );
//		  }
//
//		  public static boolean deleteProperty( String namespace, String name ) {
//		    Property property = findProperty( namespace, name );
//		    if (property == null) {
//		      return false;
//		    }
//
//		    EjbUtil.dao().delete( property );
//		    return true;
//		  }
//
//		  public static boolean deleteContractProperty( String name ) {
//		    return deleteProperty( CONTRACT_NAMESPACE, name );
//		  }
//
//		  /**
//		   * Synchronize the list of properties for the namespace to what is persisted.
//		   * Including adding, updating and removing persisted properties.
//		   *
//		   * @param namespace
//		   * @param properties
//		   */
//		  public static void synchronizeNamespace( String namespace, Property... properties ) {
//		    List<Property> persisted = getProperties( namespace );
//
//		    for( Property property : properties ) {
//		      if ( persisted.contains( property ) )
//		        persisted.remove( property );
//		      setPropertyValue( property.getNamespace(), property.getName(), property.getValue() );
//		    }
//
//		    for ( Property property : persisted )
//		      deleteProperty( property.getNamespace(), property.getName() );
//
//		  }
//
//		  public static List<Property> getProperties( String namespace ) {
//		    return findProperties( namespace, null );
//		  }
//
//		  // Private methods
//
//		  private static Property findProperty( String namespace, String name ) {
//		    return ColUtil.getFirst( findProperties( namespace, name ) );
//		  }
//
//		  private static List<Property> findProperties( String namespace, String name ) {
//		    QueryExpression query = new QueryExpression( Property.class );
//		    query.equal( Property.FIELD.namespace, namespace );
//		    if ( name != null )
//		      query.equal( Property.FIELD.name, name );
//		    return ConfigurableUtil.search( query );
//		  }

