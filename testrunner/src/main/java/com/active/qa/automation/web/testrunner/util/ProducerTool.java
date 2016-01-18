package com.active.qa.automation.web.testrunner.util;

import com.active.qa.automation.web.testrunner.TestConstants;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple tool for publishing messages
 * Created by tchen on 1/18/2016.
 */
public class ProducerTool {
  public static Logger logger = Logger.getLogger(ProducerTool.class);
  private boolean isConnected;
  private Destination destination;
  private long timeToLive;

  @SuppressWarnings("unused")
  private String subject;    // = "TOOL.DEFAULT";
  private boolean topic;
  private boolean transacted;
  private boolean persistent;

  private Session session;
  private MessageProducer producer;
  private Connection connection;


  public static ProducerTool getInstance() throws JMSException {
    return new ProducerTool();
  }

  public static ProducerTool getInstance(String user, String password, String url) throws JMSException {
    return new ProducerTool(user, password, url);
  }

  public static void main(String[] args) throws JMSException {

  }


  private ProducerTool() throws JMSException {
    this(TestProperty.DEFAULT_USER, TestProperty.DEFAULT_PASSWORD, TestProperty.get("mq.url"));
  }

  private ProducerTool(String user, String password, String url) throws JMSException {
//    	this.user=user;
//    	this.password=password;
//    	this.url=url;
//    	this.connectionFactory = new com.sun.messaging.ConnectionFactory();
//    	this.connectionFactory.setProperty(ConnectionConfiguration.imqAddressList, url);
//    	this.connectionFactory.setProperty(ConnectionConfiguration.imqReconnectEnabled, "true");
    this.connection = null;//ConnectionTool.getConnection();
    this.session = null;
    this.destination = null;

    //variables below are set with default values
    this.topic = false;
    this.transacted = true;
    this.persistent = true;
    this.isConnected = false;
    this.timeToLive = 0; //zero is unlimited

    //variables below need to be set upon connection
    this.subject = null;
  }

  public void connect(String subject) throws JMSException {
    connect(subject, false);
  }

  public void connect(String subject, boolean isTopic) throws JMSException {
    if (isConnected) {
      if (this.topic == isTopic)
        return;
      else {
        disconnect();
        this.topic = isTopic;
      }
    }
    topic = isTopic;

    logger.debug("Connecting...");
    if (subject == null || subject.length() < 1)
      throw new JMSException("Variable subject is null or empty.");

    //create connection
//    	connection = connectionFactory.createConnection();
//        connection.start();
//        logger.debug("Connection started.");

    connection = ConnectionTool.getConnection();

    //create the session
    //A JMS Session is a single threaded context for producing and consuming messages
    //Each transaction groups a set of message sends and a set of message receives into an atomic unit of work
    //When a transaction commits, its atomic unit of input is acknowledged and its associated atomic unit of output is sent.
    //If a transaction rollback is done, its sent messages are destroyed and the session's input is automatically recovered
    session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
    logger.debug("Created a session.");
    if (topic) {
      destination = session.createTopic(subject);
      logger.debug("Create a topic destination: " + subject);
    } else {
      destination = session.createQueue(subject);
      logger.debug("Create a queue destination: " + subject);
    }

    //create the producer with a destination. If creating a message producer without supplying a Destination,
    //a Destination must be input on every send operation.
    producer = session.createProducer(destination);

    //setting delivery mode
    //A client can specify a default delivery mode, priority and time-to-live for messages sent by a message producer.
    //It can also specify delivery mode, priority and time-to-live per message.
    if (persistent) {
      //This mode instructs the JMS provider to log the message to stable storage as part of the client's send operation.
      //Only a hard media failure should cause a PERSISTENT message to be lost.
      //A message is guaranteed to be delivered once-and-only-once by a JMS Provider if the delivery mode of the message is persistent and if the destination has a sufficient message retention policy.
      producer.setDeliveryMode(DeliveryMode.PERSISTENT);
      logger.debug("Create a persistent producer.");
    } else {
      //This is the lowest overhead delivery mode because it does not require that the message be logged to stable storage.
      //The level of JMS provider failure that causes a NON_PERSISTENT message to be lost is not defined.
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      logger.debug("Create a non_persistent producer.");
    }

    //Set the default length of time in milliseconds from its dispatch time that a produced message should be retained by the message system.
    //zero is unlimited
    if (timeToLive != 0) {
      producer.setTimeToLive(timeToLive);
      logger.debug("Set producer time to live=" + timeToLive + "ms");
    } else
      logger.debug("Set producer time to live=unlimited");

    logger.debug("Fully connected");
    isConnected = true;
  }

  public void disconnect() throws JMSException {
    if (!isConnected)
      return;

    logger.debug("Disconnecting...");

    producer.close();
    logger.debug("Producer closed");
    session.close();
    logger.debug("Session closed");
//        connection.close();
//        logger.debug("Connection closed");

    this.subject = null;

    logger.debug("Fully Disconnected.");

    isConnected = false;
  }

  public TextMessage createTextMessage(String body) throws JMSException {
    return createTextMessage(body, null);
  }

  public Message createMessage() throws JMSException {
    Message msg = session.createMessage();
    return msg;
  }

  public TextMessage createTextMessage(String body, String[] property) throws JMSException {
    TextMessage msg = session.createTextMessage(body);
    int index = property == null ? -1 : property.length - 1;

    while (index >= 0) {
      String[] s = property[index].split("=", 2);
      msg.setStringProperty(s[0], s[1]);
      index--;
    }

    msg.setIntProperty("index", 0);

    return msg;
  }

  public List<Message> createTextMessages(List<String> v) throws JMSException {
    return createTextMessages(v, null);
  }

  public List<Message> createTextMessages(List<String> v, String[] property) throws JMSException {
    int size = v.size();
    List<Message> msgs = new ArrayList<Message>();
    for (int i = 0; i < size; i++) {
      Message msg = createTextMessage((String) v.get(i), property);
      msg.setIntProperty("index", i);
      msgs.add(msg);
    }

    return msgs;
  }

  public void produceMessage(String msg) throws JMSException {
    produceMessage(session.createTextMessage(msg), 4);
  }

  public void produceMessage(String msg, int priority) throws JMSException {
    produceMessage(session.createTextMessage(msg), priority);
  }

  public void produceMessages(String[] msgs) throws JMSException {
    int messageCount = msgs.length;

    for (int i = 0; i < messageCount; i++) {
      produceMessage(msgs[i]);
    }
  }

  public void produceMessage(Message msg) throws JMSException {
    produceMessage(msg, TestConstants.NORMAL_PRIORITY);
  }

  public void produceMessage(Message msg, int priority) throws JMSException {
    logger.debug("Sending message: " + (msg instanceof TextMessage ? ((TextMessage) msg).getText() : msg.toString()));
    logger.debug("Message Content: " + msg.toString());
    producer.setPriority(priority);
    producer.send(msg);

    if (transacted) {
      session.commit();
    }
    logger.debug("Done.");
  }

  public void produceMessages(Message[] msgs) throws Exception {
    produceMessages(msgs, 6);
  }

  public void produceMessages(Message[] msgs, int priority) throws Exception {
    int messageCount = msgs.length;

    for (int i = 0; i < messageCount; i++) {
      produceMessage(msgs[i], priority);
    }
  }

  public void produceMessages(List msgs) throws JMSException {
    produceMessages(msgs, TestConstants.NORMAL_PRIORITY);
  }

  public void produceMessages(List msgs, int priority) throws JMSException {
    int messageCount = msgs.size();

    boolean isString = msgs.get(0) instanceof String;

    for (int i = 0; i < messageCount; i++) {
      if (isString)
        produceMessage((String) msgs.get(i), priority);
      else
        produceMessage((Message) msgs.get(i), priority);
    }
  }

//    public void setConnectionFactory() throws JMSException {
//    	disconnect();
//    	connectionFactory.setProperty(ConnectionConfiguration.imqAddressList, url);
//    	logger.debug("Reset connection factory with url="+url);
//    }

  public void setPersistent(boolean durable) {
    this.persistent = durable;
  }

//    public void setPassword(String pwd) {
//        this.password = pwd;
//    }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setTimeToLive(long timeToLive) {
    this.timeToLive = timeToLive;
  }

  public void setTopic(boolean topic) {
    this.topic = topic;
  }

  public void setQueue(boolean queue) {
    this.topic = !queue;
  }

  public void setTransacted(boolean transacted) {
    this.transacted = transacted;
  }

//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public void setUser(String user) {
//        this.user = user;
//    }
}

