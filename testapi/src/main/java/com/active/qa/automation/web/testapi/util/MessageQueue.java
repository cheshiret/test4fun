package com.active.qa.automation.web.testapi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.Topic;

/**
 * MessageQueue is a utility class for accessing Message Queue in this framework.
 *
 * In a JVM, we maintain only one connection to MessageQueue server. The message queue server can be either OpemMQ or ActiveMQ
 * An MessageQueue object will maintain one session. If you need a different session, a separate MessageQueue object should be created.
 * An MessageQueue object will also maintain one producer and one consumer.
 *
 * Created by tchen on 1/11/2016.
 */
public class MessageQueue {
    public static AutomationLogger logger=AutomationLogger.getInstance();
    private static Connection connection=null; //only one connection for the life time
    private boolean started=false;
    private String clientID=null;

    private Destination destination;
    private MessageProducer producer;
    private MessageConsumer consumer;

    //session related
    private Session session;
    private String subject;
    private boolean isTopic;
    private boolean transacted;
    //    private boolean persistent;
    private boolean linked;
    private int ackMode;

    /**
     * Connect to the default OpenMQ server set in test.properties file. We will keep only one connection.
     * @throws JMSException
     */
    public void connect() throws JMSException {
        if(connection==null) {
            logger.info("Connecting...");
            com.sun.messaging.ConnectionFactory connectionFactory=new com.sun.messaging.ConnectionFactory();
            connectionFactory.setProperty(ConnectionConfiguration.imqAddressList, TestProperty.getProperty("mq.url"));
            connectionFactory.setProperty(ConnectionConfiguration.imqReconnectEnabled, "true");

            connection = connectionFactory.createConnection();
            if(clientID!=null && clientID.length()>0) {
                connection.setClientID(clientID);
            }
            connection.start();
            started=true;
            logger.info("Connected!");

        }
    }

    /**
     * Connect to the default OpenMQ server set in test.properties file with the given client ID
     * @param id
     * @throws JMSException
     */
    public void connect(String id) throws JMSException {
        if(clientID==null || !clientID.equalsIgnoreCase(id)) {
            close();
            clientID=id;
        }

        connect();
    }

    /**
     * Check if the connection started or not
     * @return
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Start the existing connection is it is not started. The message will begin arriving at the connection's consumers
     * @throws JMSException
     */
    public void start() throws JMSException {
        if(!started) {
            connection.start();
            started=true;
        }
    }

    /**
     * Close the existing connection. The connection resources will be released
     * Closing a connection causes all temporary destinations to be deleted.
     */
    public void close() {
        if(connection!=null) {
            try {
                connection.close();
            } catch(Exception e){
                logger.warn(e);
            }
        }
        started=false;
        connection=null;
    }

    /**
     * Stop the existing connection. The messages will stop arriving at the connecton's consumers
     * @throws JMSException
     */
    public void stop() throws JMSException {
        if(started) {
            connection.stop();
            started=false;
        }
    }

    /**
     * set the client ID if it is not set. The client ID will not take effect unless you disconnect and connect again
     * @param id
     */
    public void setClientID(String id) {
        if(clientID==null || clientID.length()<1) {
            clientID=id;
        } else {
            throw new IllegalStateException("CientID is already set to '"+clientID+"'!");
        }
    }

    /**
     * Create a session to the message queue with the given subject with auto acknowledge enabled
     * @param subject
     * @throws JMSException
     */
    public void link(String subject) throws JMSException {
        link(subject,false,Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * Create a session to a message queue or topic with the given subject with auto acknowledge enabled
     * @param subject
     * @param isTopic
     * @throws JMSException
     */
    public void link(String subject, boolean isTopic) throws JMSException {
        link(subject,isTopic,Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * Create a session to a message queue or topic with the given subject with the given acknowledge mode
     * @param subject
     * @param isTopic
     * @param ackMode
     * @throws JMSException
     */
    public void link(String subject, boolean isTopic, int ackMode) throws JMSException {
        logger.debug("Linking to "+(isTopic?"Topic: ":"Queue: ")+subject);
        if(subject==null || subject.length()<1)
            throw new JMSException("Variable subject is null or empty.");

        this.subject=subject;
        this.isTopic=isTopic;
        this.ackMode=ackMode;
        session = connection.createSession(transacted, ackMode);
        logger.debug("Created a session.");
        if (isTopic) {
            destination = session.createTopic(subject);
            logger.debug("Create a topic destination: "+subject);
        } else {
            destination = session.createQueue(subject);
            logger.debug("Create a queue destination: "+subject);
        }

        logger.debug("Linked to "+(isTopic?"Topic: ":"Queue: ")+subject);
        linked=true;
    }

    /**
     * disconnect the current session and also reset producer and consumer
     * @throws JMSException
     */
    public void unlink() throws JMSException {
        if(linked) {
            this.resetProducer();
            this.resetConsumer();

            session.close();
            subject=null;
            isTopic=false;
            ackMode=-1;
            linked=false;
            logger.debug("Link to "+(isTopic?"Topic: ":"Queue: ")+subject+" is disconnected");
        }
    }

    /**
     * Create a default persistent producer with unlimited life time
     * @throws JMSException
     */
    public void createProducer() throws JMSException {
        createProducer(0);//unlimited life time
    }

    /**
     * Create a default persistent producer
     * @param timeToLive
     * @throws JMSException
     */
    public void createProducer(int timeToLive) throws JMSException {
        createProducer(true, timeToLive);
    }

    /**
     * create a producer from the current live session
     * @param persistent
     * @param timeToLive
     * @throws JMSException
     */
    public void createProducer(boolean persistent, int timeToLive) throws JMSException {
        resetProducer();

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
            logger.debug("Set producer time to live="+timeToLive+"ms");
        } else
            logger.debug("Set producer time to live=unlimited");

        logger.debug("Producer is created");
    }

    /**
     * Clear the existing producer
     * @throws JMSException
     */
    public void resetProducer() throws JMSException {
        if(producer!=null) {
            producer.close();
            producer=null;
        }
    }

    /**
     * Create a default durable consumer with consumer name as the Host name
     * @throws JMSException
     */
    public void createConsumer() throws JMSException {
        createConsumer(true,null,SysInfo.getHostName());
    }

    /**
     * Create a default durable consumer with the given selector
     * @param selector
     * @throws JMSException
     */
    public void creatConsumer(String selector) throws JMSException {
        createConsumer(true,selector,SysInfo.getHostName());
    }

    /**
     * Create a durable consumer with the given selector and the given name
     * @param selector
     * @param name
     * @throws JMSException
     */
    public void createConsumer(String selector, String name) throws JMSException {
        createConsumer(true,selector,name);
    }

    /**
     * Create a consumer from the existing session
     * @param durable
     * @param selector - only messages with properties matching the message selector expression are delivered. A value of null or an empty string indicates that there is no message selector for the message consumer.
     * @param consumerName
     * @throws JMSException
     */
    public void createConsumer(boolean durable, String selector, String consumerName) throws JMSException {
        resetConsumer();

        if (durable && isTopic) {
            consumer = session.createDurableSubscriber((Topic)destination, consumerName,selector,true);
            logger.debug("Create a durable topic subscriber: "+consumerName);
        } else {
            consumer = session.createConsumer(destination,selector);
            logger.debug("Create "+(isTopic?"an undurable topic subscriber: ":"queue consumer: ")+consumerName);
        }

        logger.debug("Consumer is created!");
    }

    /**
     * Clear the existing consumer
     * @throws JMSException
     */
    public void resetConsumer() throws JMSException {
        if(consumer!=null) {
            consumer.close();
            consumer=null;
        }
    }

    /**
     * Create a text message with the given text body
     * @param body
     * @return
     * @throws JMSException
     */
    public TextMessage createTextMessage(String body) throws JMSException{
        return createTextMessage(body,null);
    }

    /**
     * Create a message
     * @return
     * @throws JMSException
     */
    public Message createMessage() throws JMSException {
        Message msg=session.createMessage();
        return msg;
    }

    /**
     * Create a text message with the given text body and properites
     * @param body
     * @param property
     * @return
     * @throws JMSException
     */
    public TextMessage createTextMessage(String body,String[] property) throws JMSException{
        TextMessage msg=session.createTextMessage(body);
        int index=property==null?-1:property.length-1;

        while (index>=0) {
            String[] s=property[index].split("=",2);
            msg.setStringProperty(s[0], s[1]);
            index--;
        }

        msg.setIntProperty("index", 0);

        return msg;
    }

    /**
     * Create a list text messages corresponding to a list of text bodies
     * @param bodies
     * @return
     * @throws JMSException
     */
    public List<Message> createTextMessages(List<String> bodies) throws JMSException {
        return createTextMessages(bodies,null);
    }

    /**
     * Create a list text messages corresponding to a list of text bodies
     * @param bodies
     * @param property
     * @return
     * @throws JMSException
     */
    public List<Message> createTextMessages(List<String> bodies, String[] property) throws JMSException {
        int size=bodies.size();
        List<Message> msgs=new ArrayList<Message>();
        for(int i=0;i<size;i++) {
            Message msg=createTextMessage((String)bodies.get(i), property);
            msg.setIntProperty("index", i);
            msgs.add(msg);
        }

        return msgs;
    }

    /**
     * Produce a text message with default priority 4
     * @param msg
     * @throws JMSException
     */
    public void produceMessage(String msg) throws JMSException {
        produceMessage(session.createTextMessage(msg),4);
    }

    /**
     * Produce a text message with the given priority
     * @param msg
     * @param priority
     * @throws JMSException
     */
    public void produceMessage(String msg, int priority) throws JMSException {
        produceMessage(session.createTextMessage(msg),priority);
    }

    /**
     * Produce an array of text messages
     * @param msgs
     * @throws JMSException
     */
    public void produceMessages(String[] msgs) throws JMSException {
        int messageCount=msgs.length;

        for (int i = 0; i < messageCount; i++) {
            produceMessage(msgs[i]);
        }
    }

    /**
     * Produce a message with default priority 6
     * @param msg
     * @throws JMSException
     */
    public void produceMessage(Message msg) throws JMSException {
        produceMessage(msg,6);
    }

    /**
     * Produce a message with the given priority
     * @param msg
     * @param priority
     * @throws JMSException
     */
    public void produceMessage(Message msg, int priority) throws JMSException {
        logger.info("Sending message: " + (msg instanceof TextMessage?((TextMessage)msg).getText():msg.toString()));
        logger.debug("Message Content: "+ msg.toString());
//    	System.out.println("env="+msg.getStringProperty("env")+" for "+((TextMessage)msg).getText());
        producer.setPriority(priority);
        producer.send(msg);

        if(transacted) {
            session.commit();
        }
        logger.info("Done.");
    }

    /**
     * Produce an array of messages
     * @param msgs
     * @throws Exception
     */
    public void produceMessages(Message[] msgs) throws Exception {
        produceMessages(msgs,6);
    }

    /**
     * Produce an array of messages with the given priority
     * @param msgs
     * @param priority
     * @throws Exception
     */
    public void produceMessages(Message[] msgs, int priority) throws Exception {
        int messageCount=msgs.length;

        for (int i = 0; i < messageCount; i++) {
            produceMessage(msgs[i],priority);
        }
    }

    /**
     * Produce a list of messages
     * @param msgs
     * @throws JMSException
     */
    public void produceMessages(List<?> msgs) throws JMSException {
        produceMessages(msgs,6);
    }

    /**
     * Produce a list of messages with the given priority
     * @param msgs
     * @param priority
     * @throws JMSException
     */
    public void produceMessages(List<?> msgs, int priority) throws JMSException {
        int messageCount=msgs.size();

        boolean isString=msgs.get(0) instanceof String;

        for (int i = 0; i < messageCount; i++) {
            if(isString)
                produceMessage((String) msgs.get(i),priority);
            else
                produceMessage((Message) msgs.get(i),priority);
        }
    }

    /**
     * Consume a message. The consumer will be blocked at queue indefinitely until a message is produced or until the consumer is closed
     * @return
     * @throws JMSException
     */
    public Message consumeMessage() throws JMSException {
        return consumeMessage(-1);
    }

    /**
     * Consume a message
     * @param subject
     * @param timeout - (in milliseconds)
     * @param isTopic
     * @param durable
     * @return
     * @throws JMSException
     * @throws IOException
     */
    public Message consumeMessage(long timeout) throws JMSException {
        //We will keep trying to consume a message until we get one
        Message message;

//      connect();
//    	link(TestProperty.getProperty("mq.test.queue"));
//    	createConsumer();

        logger.debug("Retrieving a message...");
        if(timeout<0) {
            //This call blocks indefinitely until a message is produced or until this message consumer is closed.
            message=consumer.receive();
        }else {
            message=consumer.receive(timeout);
        }
        if(message!=null) {
            logger.info("Recieved message: "+ message.getJMSMessageID());
            logger.debug("Message Content: "+ message.toString());
            MessageProducer replyProducer;
            if (message.getJMSReplyTo() != null) {
                logger.info("Reply to "+message.getJMSReplyTo());
                replyProducer = session.createProducer(null);
                replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                replyProducer.send(message.getJMSReplyTo(), session.createTextMessage("Reply: " + message.getJMSMessageID()));
                replyProducer.close();
                replyProducer=null;
                logger.info("Reply done.");
            }

            if (transacted) {
                session.commit();
            } else if (ackMode == Session.CLIENT_ACKNOWLEDGE) {
                message.acknowledge();
            }
        }

        unlink();//disconnect
//        close();

        return message;
    }

    /**
     * List messages in a queue with the given subject and filtered by the selector
     * @param subject
     * @param selector
     * @return
     * @throws JMSException
     */
    public List<Message> browserMessages(String subject, String selector) throws JMSException {
        session = connection.createSession(transacted, ackMode);
        logger.debug("Created a session.");
        Destination queue = session.createQueue(subject);
        QueueBrowser browser;
        if(StringUtil.isEmpty(selector)) {
            browser=session.createBrowser((Queue)queue);
        } else {
            browser=session.createBrowser((Queue)queue, selector);
        }

        Enumeration<?> msgs=browser.getEnumeration();
        Message aMsg=null;
        List<Message> msgList=new ArrayList<Message>();
        while(msgs.hasMoreElements()) {
            aMsg=(Message)msgs.nextElement();
            msgList.add(aMsg);
        }

        return msgList;
    }
}

