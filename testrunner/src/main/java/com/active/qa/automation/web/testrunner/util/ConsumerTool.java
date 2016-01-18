package com.active.qa.automation.web.testrunner.util;

import com.sun.messaging.Queue;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * A simple tool for consuming messages
 * Created by tchen on 1/18/2016.
 */
public class ConsumerTool implements ExceptionListener {
    public static Logger logger=Logger.getLogger(ConsumerTool.class);
    private boolean isConnected;
    private Session session;
    private Destination destination;
    private String subject;			// = "TOOL.DEFAULT";
    private String selector;
    private boolean topic;
    private boolean transacted;
    private boolean durable;
    private int ackMode;
    private String consumerName;
    private Connection connection;
    private MessageConsumer consumer;
    private MessageProducer replyProducer;

    public static ConsumerTool getInstance() throws JMSException {
        return new ConsumerTool();
    }

    private ConsumerTool() throws JMSException {
        this.connection=null;//ConnectionTool.getConnection();
        this.session=null;
        this.consumer=null;
        this.isConnected=false;
        this.selector=null;

        //following variables are set with default values
        this.transacted=false;
        this.durable=true;
        this.ackMode = Session.AUTO_ACKNOWLEDGE;
        this.topic=false;
//    	String timeStamp=(new SimpleDateFormat("yyyyMMddhhmmss")).format(Calendar.getInstance().getTime()).toString();

        try{
            this.consumerName=InetAddress.getLocalHost().getHostName();
        }catch(Throwable e) {
            this.consumerName="Unknow host";
        }

        //following variables must be set upon connection
        this.subject=null;
    }

    public void connect(String subject) throws JMSException {
        this.subject=subject;
        connect();
    }

    public void connect(String subject,String selector) throws JMSException {
        this.subject=subject;
        this.selector=selector;
        connect();
    }

    public void connect(String subject, boolean isTopic) throws JMSException {
        connect(subject,isTopic,this.selector);
    }

    public void connect(String subject, boolean isTopic, String selector) throws JMSException {
        this.subject=subject;
        this.topic=isTopic;
        this.selector=selector;
        connect();
    }

    public void connect() throws JMSException{
        if(isConnected)
            return;
        logger.debug("Creating a new session...");
        if(this.subject==null || subject.length()<1) {
            throw new JMSException("Variable subject is null or empty.");
        }

        connection=ConnectionTool.getConnection();
        session = connection.createSession(transacted, ackMode);
        logger.debug("Created a session.");
        if (topic) {
            destination = session.createTopic(subject);
            logger.debug("Create a topic destination: "+subject);
        } else {
            destination = session.createQueue(subject);
            logger.debug("Create a queue destination: "+subject);
        }

        if (durable && topic) {
            consumer = session.createDurableSubscriber((Topic)destination, consumerName,this.selector,true);
            logger.debug("Create a durable topic subscriber: "+consumerName);
        } else {
            consumer = session.createConsumer(destination,this.selector);
            logger.debug("Create "+(topic?"an undurable topic subscriber: ":"queue consumer: ")+consumerName);
        }

//        connection.start();
        logger.debug("Session created!");
        isConnected=true;
    }

    public void disconnect() throws JMSException{
        if(isConnected) {
            consumer.close(); //will close all sessions, message consumers associated with this connection
            session.close();
            this.subject=null;
            logger.debug("Fully Disconnected.");
            isConnected=false;
        } else {
            logger.debug("Not connected.");
        }

    }

    public Message consumeMessage(String subject) throws JMSException {
        return consumeMessage(subject,-1);
    }

    public Message consumeMessage(String subject,long timeout) throws JMSException {
        return consumeMessage(subject, timeout, false,true);
    }

    public Message consumeMessage(String subject,boolean isTopic) throws JMSException {
        return consumeMessage(subject, -1, isTopic,false);
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
    public Message consumeMessage(String subject,long timeout,boolean isTopic, boolean durable) throws JMSException {
        //We will keep trying to consume a message until we get one
        Message message;
        if(isConnected && (this.subject.equals(subject) || this.topic!=isTopic || this.durable!=durable)) {
            disconnect();
        }
        this.subject=subject;
        this.topic=isTopic;
        this.durable=durable;
        connect();
        logger.debug("Retrieving a message...");
        if(timeout<0) {
            //This call blocks indefinitely until a message is produced or until this message consumer is closed.
            message=consumer.receive();
        }else {
            message=consumer.receive(timeout);
        }
        if(message!=null) {
//        	logger.info("Recieved message: "+ message.getJMSMessageID());
            logger.debug("Message Content: "+ message.toString());

            if (message.getJMSReplyTo() != null) {
                logger.info("Reply to "+message.getJMSReplyTo());
                replyProducer = session.createProducer(null);
                replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                replyProducer.send(message.getJMSReplyTo(), session.createTextMessage("Reply: " + message.getJMSMessageID()));
                logger.info("Reply done.");
            }

            if (transacted) {
                session.commit();
            } else if (ackMode == Session.CLIENT_ACKNOWLEDGE) {
                message.acknowledge();
            }
        }
        disconnect();
        return message;
    }

    public List<Message> browserMessages(String subject, String selector) throws JMSException {
        Destination queue = session.createQueue(subject);
        QueueBrowser browser=session.createBrowser((Queue)queue, selector);

        Enumeration<?> msgs=browser.getEnumeration();
        Message aMsg=null;
        List<Message> msgList=new ArrayList<Message>();
        while(msgs.hasMoreElements()) {
            aMsg=(Message)msgs.nextElement();
            msgList.add(aMsg);
        }

        return msgList;
    }

    public void setAckMode(String ackMode) {
        if ("CLIENT_ACKNOWLEDGE".equals(ackMode)) {
            this.ackMode = Session.CLIENT_ACKNOWLEDGE;
        }
        if ("AUTO_ACKNOWLEDGE".equals(ackMode)) {
            this.ackMode = Session.AUTO_ACKNOWLEDGE;
        }
        if ("DUPS_OK_ACKNOWLEDGE".equals(ackMode)) {
            this.ackMode = Session.DUPS_OK_ACKNOWLEDGE;
        }
        if ("SESSION_TRANSACTED".equals(ackMode)) {
            this.ackMode = Session.SESSION_TRANSACTED;
        }
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public void setSelector(String selector_code){
        this.selector = selector_code;
    }

    public void onException(JMSException e) {
        //If a JMS provider detects a serious problem with a connection it will inform the connection's ExceptionListener if one has been registered.
        //It does this by calling the listener's onException() method passing it a JMSException describing the problem.
        logger.error(e.getMessage(),e);

        //TODO: Try to re-connect??
    }

    public void consumeAllMessage(String subject,long timeout) throws JMSException {
        boolean stop=false;
        while(!stop) {
            Message msg=consumeMessage(subject,timeout);
            stop=msg==null;
        }
    }
}


