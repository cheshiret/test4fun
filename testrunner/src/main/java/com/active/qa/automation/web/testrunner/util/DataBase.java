package com.active.qa.automation.web.testrunner.util;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model the database
 * Created by tchen on 1/18/2016.
 */
public class DataBase {
    public static Logger logger=Logger.getLogger(DataBase.class);

    public String dbUser;
    public String dbPassword;
    public String dbURL;
    public String dbDriver;
    protected Connection dbConnection;

    /**
     * The constructor could be made private
     * to prevent others from instantiating this class.
     * But this would also make it impossible to
     * create instances of Singleton subclasses.
     */
    protected DataBase(String user,String password, String driver,String url)  {
        dbUser =user;
        dbPassword =password;
        dbDriver =driver;
        dbURL =url;
        dbConnection = null;
    }

    /**
     * @return an instance of this class.
     */
    public static DataBase getInstance() throws RuntimeException {
//   	   String user=TestProperty.get("db.qa.user");
//   	   String pw=CryptoUtil.decrypt(TestProperty.get("db.qa.pwd"));
//   	   String driver=TestProperty.get("db.qa.driver");
//   	   String url=TestProperty.get("db.qa.url");
        String user=TestProperty.get("db.qa.user");
        System.out.println(TestProperty.get("db.qa.user"));
        String pw=TestProperty.get("db.qa.password");
        String driver=TestProperty.get("db.qa.driver");
        String url=TestProperty.get("db.qa.url");

        return new DataBase(user,pw,driver,url);
    }

    /**
     * @return a new instance of this class.
     */
    public static DataBase getInstance(String user,String password, String driver,String url) {
        return new DataBase(user,password,driver,url);
    }

    public void connect() {
        // Establish database connection if not already connected

        if (dbConnection == null) {

            if (dbURL != null) {
                try {
                    Class.forName(this.dbDriver);
                    logger.debug("Connect to schema "+dbUser+" with URL="+dbURL);
                    dbConnection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
                }catch (ClassNotFoundException e) {
                    logger.error("Driver not found: "+dbDriver);
                    throw new RuntimeException(e);
                }catch (SQLException sqle) {
                    logger.error("Failed to connect DB.");
                    throw new RuntimeException(sqle);
                }
            } else {
                throw new RuntimeException("dbURL parameter is missing.");
            }
        }
        if (dbConnection==null)
            throw new RuntimeException("Failed to connect to Database.");
    }

    public void disconnect() {
        // Close current database connection
        if (dbURL != null && dbConnection != null) {
            try {
                logger.debug("Disconnect DB.");
                dbConnection.close();
                dbConnection = null;
            }
            catch (SQLException sqle) {
                logger.error("SQL Exception - " + sqle.getMessage());
                throw new RuntimeException(sqle);
            }
        }

    }

    public void reconnect() {
        this.disconnect();
        this.connect();
    }

    public int[] executeBatch(String[] updates) {
        int[] toReturn=null;
        try {
            if(dbConnection==null) {
                connect();
            }

            boolean supportBatch=dbConnection.getMetaData().supportsBatchUpdates();
            Statement stmt = dbConnection.createStatement();
            if(!supportBatch) {
                logger.warn("Batch updateing is not supported, execute each individual update in a loop instead.");
                toReturn=new int[updates.length];
                for(int i=0;i<toReturn.length;i++) {
                    try {
                        toReturn[i]= stmt.executeUpdate(updates[i]);
                    } catch (SQLException sqle) {
                        logger.error("Failed to run query " + updates[i]);
                        toReturn[i]=0;
                    }
                }
            } else {

                for(String update: updates) {
                    stmt.addBatch(update);
                }

                toReturn= stmt.executeBatch();
            }

            stmt.close();
        } catch (SQLException sqle) {
            logger.error("Failed to run batch updates.");
            throw new RuntimeException(sqle);
        }

        return toReturn;
    }

    public ResultSet executeQuery(String query) {
        connect();
        ResultSet rs=null;
        try {
            Statement stmt = dbConnection.createStatement();
            rs= stmt.executeQuery(query);
        } catch (SQLException sqle) {
            logger.error("Failed to run query "+query);
            throw new RuntimeException(sqle);
        } finally{
            disconnect();
        }

        return rs;
    }

    /**
     * This method is used to execute given sql statement
     * @param query
     * @return affected line num
     */
    public int executeUpdate(String query) {
        int toReturn = -1;
        // return null if not connected to database
        this.connect();

        try {
            Statement stmt = dbConnection.createStatement();
            toReturn = stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException sqle) {
            logger.error("Failed to run query '" + query+ "' due to "+sqle.getMessage());
//			throw new RuntimeException("SQL Exception - " + sqle.getMessage());


        } finally {
            logger.debug("Total Number of " + toReturn + " records were updated in DB");
            this.disconnect();
        }
        return toReturn;
    }

    /**
     * Return all columns' values for the given query
     * @param query - the query string
     * @param colNames - array of column names
     * @return - ArrayList of all columns values, each element in ArrayList is a String[] holding the values for corresponding colNames
     */
    public List<String[]> executeQuery(String query, String[] colNames) {
        // return null if not connected to database
        this.connect();

        ArrayList<String[]> output = new ArrayList<String[]>();
        String[] rowData=null;
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                rowData=new String[colNames.length];
                for(int i=0;i<colNames.length;i++) {
                    rowData[i]=rs.getString(colNames[i]);
                }
                output.add(rowData);
            }
            rs.close();
            stmt.close();
        } catch (SQLException sqle) {
            logger.error("Failed to run query " + query);
            throw new RuntimeException("SQL Exception - "+ sqle.getMessage());
        } finally {
            this.disconnect();
        }
        return output;
    }

    /**
     * return all the column value for colName from query result
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
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                output.add(rs.getString(colName));
            }

            rs.close();
            stmt.close();
        } catch (SQLException sqle) {
            logger.error("Failed to run query " + query);
            throw new RuntimeException("SQL Exception - "+ sqle.getMessage());
        } finally {
            this.disconnect();
        }
        return output;
    }
}
