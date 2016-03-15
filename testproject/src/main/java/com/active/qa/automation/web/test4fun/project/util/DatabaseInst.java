package com.active.qa.automation.web.test4fun.project.util;

import com.active.qa.automation.web.testapi.exception.ItemNotFoundException;
import com.active.qa.automation.web.testapi.util.Database;
import com.active.qa.automation.web.testapi.util.FileUtil;
import com.active.qa.automation.web.testapi.util.TestProperty;

import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tchen on 1/18/2016.
 */
public class DatabaseInst extends Database {
  protected String dbVendor;

  protected DatabaseInst() {
    resetDefaultDB();
  }

  /**
   * @return The unique instance of this class.
   */
  static public DatabaseInst getInstance() {
    if (null == _instance) {
      _instance = new DatabaseInst();
    }

    return (DatabaseInst) _instance;
  }

  public String getDbDriver() {
    return dbDriver;
  }

  public void setDbDriver(String dbDriver) {
    this.dbDriver = dbDriver;
  }

  /**
   * Reset to default qa_user schema settings
   */
  public void resetDefaultDB() {
    if (dbConnection != null) {
      disconnect();
    }
    dbUser = TestProperty.getProperty("auto.db.user");
    dbPassword = TestProperty.getProperty("auto.db.psw");
    dbVendor = TestProperty.getProperty("auto.db.vendor");
    dbDriver = TestProperty.getProperty("db." + dbVendor + ".driver");
    dbURL = TestProperty.getProperty("auto.db.connectURL");
    schema = dbUser;
  }

  /**
   * Replace the dbUser with the given schema
   *
   * @param schema
   * @throws SQLException
   */
  public void resetSchema(String schema) {
    if (dbConnection != null) {
      disconnect();
    }
    if (schema.equalsIgnoreCase("auto")) {
      resetDefaultDB();
    } else {
      this.schema = TestProperty.getProperty(schema.toLowerCase(), schema);
      String env = TestProperty.getProperty("target_env");
      dbUser = TestProperty.getProperty(env + ".db.user");
      dbPassword = TestProperty.getProperty(env + ".db.pw");
      dbURL = TestProperty.getProperty(env + ".db.connectURL");
      dbVendor = TestProperty.getProperty(env + ".db.vendor");
      dbDriver = TestProperty.getProperty("db." + dbVendor + ".driver");
    }

    logger.debug("Reset schema=" + schema);
  }

  private void alterSchema(Statement stmt) throws SQLException {
    if (!schema.equalsIgnoreCase(dbUser)) {
      try {
        stmt.executeUpdate("alter session set current_schema=" + schema);
      } catch (SQLException sqle) {
        logger.error("Failed to alter schema");
        throw sqle;
      }
    }
  }

  /**
   * This method is used to execute given sql statement
   *
   * @param query
   * @return affected line num
   */
  public int executeUpdate(String query) {
    int toReturn = -1;
    // return null if not connected to database
    this.connect();

    try {
      Statement stmt = dbConnection.createStatement();
      alterSchema(stmt);
      toReturn = stmt.executeUpdate(query);
      stmt.close();
    } catch (SQLException sqle) {
      logger.error("Failed to run query " + query);
      throw new ItemNotFoundException("SQL Exception - " + sqle.getMessage());
    } finally {
      logger.debug("Total Number of " + toReturn + " records were updated in DB");
      this.disconnect();
    }
    return toReturn;
  }

  /**
   * This method use prepare statement to update CLOB type data
   *
   * @param tableName
   * @param whereCondition
   * @param clobColumnName
   * @param clobValue
   * @return
   */
  public int executeUpdate(String tableName, String whereCondition, String clobColumnName, String clobValue, String colName, String colValue) {
    int toReturn = -1;
    // return null if not connected to database
    this.connect();
    String sql = "";

    try {
      sql = "update " + tableName + " set " + clobColumnName + " =?, " + colName + "=? " + whereCondition;
      PreparedStatement pstmt = dbConnection.prepareStatement(sql);
      StringReader reader = new StringReader(clobValue);
      pstmt.setCharacterStream(1, reader, clobValue.length());
      pstmt.setString(2, colValue);
      toReturn = pstmt.executeUpdate();
      dbConnection.commit();
      pstmt.close();

    } catch (SQLException sqle) {
      logger.error("Failed to execute query " + sql);
      throw new ItemNotFoundException("SQL Exception - " + sqle.getMessage());
    } finally {
      logger.debug("Total Number of " + toReturn + " records were updated in DB");
      this.disconnect();
    }
    return toReturn;
  }

  public int[] executeBatch(List<String> sqls) {
    logger.info("Execute a Batch Sql");
    int[] rows;
    this.connect();
    try {
      Statement stmt = dbConnection.createStatement();
      for (String sql : sqls) {
        stmt.addBatch(sql);
      }
      rows = stmt.executeBatch();
      stmt.close();
    } catch (SQLException sqle) {
      throw new ItemNotFoundException("SQL Exception - " + sqle.getMessage());
    } finally {
      this.disconnect();
    }
    return rows;
  }

  public void printQueryResult(String query, String fileName)
      throws IOException {
    printQueryResult(query, fileName, true);
  }

  /**
   * Write query result into a log file
   *
   * @param query      SQL Statement
   * @param fileName   Log file Name
   * @param printTitle print column name or not
   * @throws IOException
   */
  public void printQueryResult(String query, String fileName,
                               boolean printTitle) throws IOException {
    // return null if not connected to database
    this.connect();

    try {
      Statement stmt = dbConnection.createStatement();
      alterSchema(stmt);
      ResultSet rs = stmt.executeQuery(query);

      ResultSetMetaData rsmd = rs.getMetaData();
      int numberOfColumns = rsmd.getColumnCount();

      String toLog = "";

      if (printTitle) {
        for (int i = 1; i < numberOfColumns; i++) {
          toLog += rsmd.getColumnName(i).toString();
          toLog += ",";
        }
        toLog += rsmd.getColumnName(numberOfColumns).toString();

        FileUtil.writeLog(fileName, toLog.toString());
      }

      while (rs.next()) {
        toLog = "";
        for (int i = 1; i < numberOfColumns; i++) {
          Object obj = rs.getObject(i);
          if (obj == null) {
            obj = "null";
          }

          toLog += obj.toString();
          toLog += ",";
        }

        Object obj = rs.getObject(numberOfColumns);
        if (obj == null) {
          obj = "null";
        }

        toLog += obj.toString();

        FileUtil.writeLog(fileName, toLog.toString());
      }
      rs.close();
      stmt.close();
      //MiscFunctions.writeLog(fileName,  sb.toString());
    } catch (SQLException sqle) {
      logger.error("Failed to run query " + query);
      throw new ItemNotFoundException("SQL Exception - "
          + sqle.getMessage());
    } finally {
      this.disconnect();
    }
  }

  /**
   * Method that executes query (as with above method) and returns
   * all items found - up to the limit imposed by the "maxLimit" parameter.
   *
   * @param query    the SQL query to execute
   * @param rowSize  the # of items in an output row
   * @param maxLimit the largest # of rows to return (skip = -1)
   * @return List  row items, with rows separated by "||" items
   */
  public List<String> executeQuery(String query, int rowSize, int maxLimit) {
    List<String> output = null;
    int counter = 0;

    if (rowSize > 0) {
      // return null if not connected to database
      this.connect();

      if (maxLimit <= 0) { // maxLimit = <NOT SET>

        maxLimit = Integer.MAX_VALUE;
      }

      output = new ArrayList<String>();

      try {

        Statement stmt = dbConnection.createStatement();
        alterSchema(stmt);
        ResultSet rs = stmt.executeQuery(query);

        while ((rs.next()) && (counter < maxLimit)) {

          // add all row items to output Vector
          for (int i = 1; i <= rowSize; i++) {
            output.add(rs.getString(i));

          }
          output.add("||"); // add row separator
          counter++;
        }

        int removalIndex = output.lastIndexOf("||");
        // added by CGuo, since if there is not record, it will cause indexOutofBound error
        if (removalIndex != -1) {
          output.remove(removalIndex);
        }

        rs.close();
        stmt.close();
      } catch (SQLException sqle) {
        logger.error("Failed to run query " + query);
        throw new ItemNotFoundException("SQL Exception - "
            + sqle.getMessage());
      } finally {
        this.disconnect();
      }
    }

    return output;
  }

  /**
   * return all the column value for colName from query result
   *
   * @param query
   * @param colName
   * @return result in Vector
   */
  public List<String> executeQuery(String query, String colName) {
    // return null if not connected to database
    this.connect();

    List<String> output = new ArrayList<String>();
    try {
      Statement stmt = dbConnection.createStatement();
      alterSchema(stmt);
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        output.add(rs.getString(colName));
      }

      rs.close();
      stmt.close();
    } catch (SQLException sqle) {
      logger.error("Failed to run query " + query);
      throw new ItemNotFoundException("SQL Exception - " + sqle.getMessage());
    } finally {
      this.disconnect();
    }
    return output;
  }

  /**
   * Return all columns' values for the given query
   *
   * @param query    - the query string
   * @param colNames - array of column names
   * @return - ArrayList of all columns values, each element in ArrayList is a String[] holding the values for corresponding colNames
   */
  public List<String[]> executeQuery(String query, String[] colNames) {
    // return null if not connected to database
    this.connect();

    ArrayList<String[]> output = new ArrayList<String[]>();
    String[] rowData = null;
    try {
      Statement stmt = dbConnection.createStatement();
      alterSchema(stmt);
      ResultSet rs = stmt.executeQuery(query);

      while (rs.next()) {
        rowData = new String[colNames.length];
        for (int i = 0; i < colNames.length; i++) {
          rowData[i] = rs.getString(colNames[i]);
        }
        output.add(rowData);
      }
      rs.close();
      stmt.close();
    } catch (SQLException sqle) {
      logger.error("Failed to run query " + query);
      throw new ItemNotFoundException("SQL Exception - " + sqle.getMessage());
    } finally {
      this.disconnect();
    }
    return output;
  }

  public List<HashMap<String, String>> executeQuery(String query) {
    this.connect();

    ArrayList<HashMap<String, String>> output = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> rowData = null;
    try {
      Statement stmt = dbConnection.createStatement();
      alterSchema(stmt);
      ResultSet rs = stmt.executeQuery(query);
      int columnCount = rs.getMetaData().getColumnCount();
      String[] colNames = new String[columnCount];
      for (int i = 0; i < columnCount; i++) {
        colNames[i] = rs.getMetaData().getColumnName(i + 1);
      }
      while (rs.next()) {
        rowData = new HashMap<String, String>();
        for (int i = 0; i < columnCount; i++) {
          String temp = rs.getString(i + 1);
          temp = ((temp == null) ? "" : temp);
          rowData.put(colNames[i], temp);
        }
        output.add(rowData);
      }
      rs.close();
      stmt.close();
    } catch (SQLException sqle) {
      logger.error("Failed to run query " + query);
      throw new ItemNotFoundException("SQL Exception - " + sqle.getMessage());
    } finally {
      this.disconnect();
    }
    return output;
  }

  /**
   * Return the fist row for the given query
   *
   * @param query
   * @param colNames
   * @param index
   * @return
   */
  public String[] executeQuery(String query, String[] colNames, int index) {
    List<String[]> output = executeQuery(query, colNames);

    if (output.size() < index + 1)
      throw new ItemNotFoundException("Index " + index + " out of boundary max " + output.size());

    if (output.get(index) == null) {
      return null;
    } else {
      return output.get(index);
    }
  }

  /**
   * Execute query
   *
   * @param query
   * @param colName
   * @param index   start from 0
   * @return the nth(=index starting from 0) element from query with column name(=colName)
   */
  public String executeQuery(String query, String colName, int index) {
    List<String> output = executeQuery(query, colName);

    if (output.size() < index + 1)
      throw new ItemNotFoundException("Index " + index + " out of boundary max " + output.size() + " for query: " + query);

    if (output.get(index) == null) {
      return null;
    } else {
      return output.get(index).toString();
    }
  }


}