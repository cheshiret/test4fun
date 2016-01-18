package com.active.qa.automation.web.testrunner.util;

import com.sun.messaging.ConnectionConfiguration;
import org.apache.log4j.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * Created by tchen on 1/18/2016.
 */
public class ConnectionTool {
  public static Logger logger = Logger.getLogger(ConnectionTool.class);
  private static Connection connection = null;
  private static boolean started = false;
  private static boolean connected = false;
  private static String clientID = null;

  private static String mqtype = TestProperty.get("mq.type", "openmq");

  public static void connect() throws JMSException {
    if (!connected) {
      logger.info("Connecting...");
      connection = null;
      started = false;
      if (mqtype.equalsIgnoreCase("openmq")) {
        com.sun.messaging.ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
        connectionFactory.setProperty(ConnectionConfiguration.imqAddressList, TestProperty.get("mq.url"));
        connectionFactory.setProperty(ConnectionConfiguration.imqReconnectEnabled, "true");

        connection = connectionFactory.createConnection();
      } else {
        String user = org.apache.activemq.ActiveMQConnection.DEFAULT_USER;
        String password = org.apache.activemq.ActiveMQConnection.DEFAULT_PASSWORD;
        org.apache.activemq.ActiveMQConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(user, password, TestProperty.get("mq.url"));
        connection = connectionFactory.createConnection();
      }
      if (clientID != null && clientID.length() > 0) {
        connection.setClientID(clientID);
      }
      connected = true;
      logger.info("Connected!");

    }

    start();
  }

  public static void connect(String id) throws JMSException {
    if (clientID == null || !clientID.equalsIgnoreCase(id)) {
      close();
      clientID = id;
    }

    connect();
  }

  public static Connection getConnection() throws JMSException {
    connect();
    return connection;
  }

  public static boolean isStarted() {
    return started;
  }

  public static boolean isConnected() {
    return connected;
  }

  public static void start() throws JMSException {
    if (!started) {
      connection.start();
      started = true;
    }
  }

  public static void close() {
    if (connection != null) {
      try {
        connection.stop();
        connection.close();
      } catch (Throwable e) {
      }
    }
    started = false;
    connection = null;
    connected = false;
  }

  public static void stop() throws JMSException {
    if (started) {
      connection.stop();
      started = false;
    }
  }

  public static void setClientID(String id) {
    if (clientID == null || clientID.length() < 1) {
      clientID = id;
    } else {
      throw new IllegalStateException("CientID is already set to '" + clientID + "'!");
    }
  }


}

