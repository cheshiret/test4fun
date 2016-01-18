package com.active.qa.automation.web.testapi.util;


import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.exception.NotInitializedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Wrap the general Database access functions
 * Created by tchen on 1/11/2016.
 */

public class Database {
  protected static AutomationLogger logger = AutomationLogger.getInstance();

  protected String dbUser; // the main login user to connect to database

  protected String dbPassword;  // the password for dbUser

  protected String schema; // the schema to execute the query against

  protected String dbURL;

  protected String dbDriver;

  protected Connection dbConnection;

  protected static Database _instance = null;

  public static Database getInstance() {
    if (null == _instance) {
      throw new NotInitializedException("Database is not initialized yet. please call getInstance() with initial arguments, or call init() method.");
    }

    return _instance;
  }

  public static Database getInstance(String user, String password, String schema, String dbUrl, String dbDriver) {
    init(user, password, schema, dbUrl, dbDriver);

    return _instance;

  }

  protected Database() {

  }

  protected Database(String user, String password, String schema, String dbUrl, String dbDriver) {
    this.dbDriver = dbDriver;
    this.dbUser = user;
    this.dbPassword = password;
    this.schema = schema;
    this.dbURL = dbUrl;
  }

  public static void init(String user, String password, String schema, String dbUrl, String dbDriver) {
    if (null == _instance) {
      _instance = new Database(user, password, schema, dbUrl, dbDriver);
    } else {
      _instance.resetDB(schema, user, password, dbDriver, dbUrl);
    }
  }

  /**
   * Use given parameters reset DB
   *
   * @param schema
   * @param pw
   * @param driver
   * @param url
   */
  public void resetDB(String schema, String user, String pw, String driver, String url) {
    if (dbConnection != null) {
      disconnect();
    }
    this.schema = schema;
    this.dbUser = user;
    this.dbPassword = pw;
    this.dbDriver = driver;
    this.dbURL = url;
  }

  /**
   * This method is used to create DB connection
   *
   * @throws
   */
  public void connect() {
    // Establish database connection if not already connected
    if (dbConnection == null) {
      if (dbURL != null) {
        try {
          Class.forName(this.dbDriver);
          dbConnection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (ClassNotFoundException e) {
          logger.error("Driver not found: " + dbDriver);
          throw new ItemNotFoundException(
              "Failed to load oracle Driver! " + e.getMessage());
        } catch (SQLException sqle) {
          logger.error("Failed to connect DB.");
          throw new ItemNotFoundException("SQL Exception - "
              + sqle.getMessage());
        }
      } else {
        throw new ItemNotFoundException("dbURL parameter is missing.");
      }
    }
    if (dbConnection == null) {
      throw new ItemNotFoundException("Failed to connect to Database.");
    }
  }

  /**
   * This method is used to close DB connection
   */
  public void disconnect() {
    // Close current database connection
    if (dbURL != null && dbConnection != null) {
      try {
        //				logger.debug("Disconnect DB.");
        dbConnection.close();
        dbConnection = null;
      } catch (SQLException sqle) {
        logger.error("SQL Exception - " + sqle.getMessage());
        throw new ItemNotFoundException("Failed to disconnect DB.");
      }
    }
  }

  /**
   * This method is used reconnect with DB
   */
  public void reconnect() {
    this.disconnect();
    this.connect();
  }

  public Connection getConnection() {
    connect();
    return dbConnection;
  }
}

