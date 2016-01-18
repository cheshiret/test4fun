package com.active.qa.automation.web.testrunner;

import com.active.qa.automation.web.testrunner.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by tchen on 1/18/2016.
 */
public class TestAgent implements Executable,TestConstants {
    protected static Logger logger = Logger.getLogger(TestAgent.class);
    private String topic;
    private ConsumerTool consumer;
    private ProducerTool producer;
    private TestRunner runner;
    private String hostName;
    private String hostIP;
    private boolean restart=false;


    public TestAgent() throws JMSException {
        TestProperty.load();
        topic=TestProperty.get("mq.runner.topic");
        ConnectionTool.setClientID(Util.getHostName());

        hostName= Util.getHostName().toLowerCase();
        hostIP=Util.getHostIP();

        String daemonjar=TestProperty.get("testagent.daemon.jar","na");
        if(!daemonjar.equalsIgnoreCase("na") && new File(daemonjar).exists()) {
            String port=TestProperty.get("testagent.daemon.port","");
            String cmd="java -jar "+daemonjar+" "+port;
            Executor daemon = new Executor(cmd,false);
            daemon.start();
        }
    }

    public void execute(String[] args) {
        boolean connect_error=false;
        int errorCount=0;
        runner=new TestRunner();
        try {
            try {
                ConnectionTool.connect();
            } catch (InvalidClientIDException e) {
                restart=false;
                throw e;
            } catch (JMSException e) {
                connect_error=true;
                runner.connectErrorOccured();
            }

            consumer = ConsumerTool.getInstance();
            producer = ProducerTool.getInstance();
            ConsumerTool.logger=logger;
            ProducerTool.logger=logger;

            runner.start();

            boolean isTopic=true;

            while(runner.isAlive() && runner.isRunning()) {
                Message msg=null;

                try {
                    if(connect_error) {
                        ConnectionTool.connect();
                        connect_error=false;
                        runner.errorCleared();
                        logger.info("Connected!");
                    }

                    msg= consumer.consumeMessage(topic,isTopic);
                    errorCount=0;
                } catch (InvalidClientIDException e) {
                    throw e;
                } catch(JMSException e) {
                    if(!connect_error) {
                        connect_error=true;
                        logger.error("Connection failed due to "+e.getMessage());
                        runner.connectErrorOccured();
                        logger.info("Will retry connection in every "+Util.getRetrySleep()+" seconds");
                    }

                    Util.sleep(Util.getRetrySleep()*1000);
                    ConnectionTool.close();
                    errorCount++;
                    if(errorCount%30 ==0 ) {
                        String hostName=Util.getHostName();
                        String subject="Warning: Test runner "+hostName+" has connection error!";
                        String text="Test runner "+hostName+" has failed to connect to message queue for "+Util.getRetrySleep()*errorCount+" seconds.\n\n";
                        ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
                        e.printStackTrace(new PrintStream(byteOut));
                        text +=byteOut.toString();
                        Email.sendErrorMessageToMaster(subject, text);
                    }
                    continue;
                }

                if(msg!=null && msg instanceof TextMessage) {
                    boolean toProcess=false;
                    String node=msg.getStringProperty("node");
                    if(node==null) {
                        continue;
                    }

                    if(node.equalsIgnoreCase("all")){
                        toProcess=true;
                    } else {
                        List<String> nodeList=Util.getListFromArray(node.split(","));
                        if( nodeList.contains(hostName) || nodeList.contains(hostIP)) {
                            toProcess=true;
                        }
                    }

                    if(toProcess) {
                        String type=msg.getStringProperty("type");

                        if(type==null) {
                            continue;
                        }

                        if(type.equalsIgnoreCase("query")) {
                            processQuery((TextMessage)msg);
                        } else if(type.equalsIgnoreCase("stop")) {
                            logger.info("Script runner is stopping now ......");
                            runner.stopRunner();
                        } else if(type.equalsIgnoreCase("reboot")) {
                            logger.info("Script runner is stopping now before reboot ......");
                            runner.stopRunner();

                            boolean stopped=Util.waitToFinish(runner, runner.getTimeRemaining());

                            if(!stopped) {
                                logger.warn("Script runner is not stopped, force to reboot...");
                            }

                            Util.rebootHost();
                        } else if(type.equalsIgnoreCase("restart")) {
                            logger.info("Script runner is restarting now ......");
                            runner.stopRunner();

                            boolean stopped=Util.waitToFinish(runner, runner.getTimeRemaining());

                            if(!stopped) {
                                logger.warn("Script runner is not stopped, kill it.");
                            }

                            restart=true;


                        } else if(type.equalsIgnoreCase("reload")) {
                            logger.info("Reloading properties ......");
                            TestProperty.reLoad();
                            ConnectionTool.close();
                            boolean debug=Boolean.valueOf(TestProperty.get("runner.debug"));
                            if(debug) {
                                logger.setLevel(Level.DEBUG);
                                TestRunner.logger.setLevel(Level.DEBUG);
                            } else {
                                logger.setLevel(Level.INFO);
                                TestRunner.logger.setLevel(Level.INFO);
                            }
                        } else if(type.equalsIgnoreCase("suspend")) {
                            logger.info("Suspend runner ......");
                            runner.suspendRunner();
                        } else if(type.equalsIgnoreCase("resume")) {
                            logger.info("Resume runner ......");
                            runner.resumeRunner();
                        } else if(type.toLowerCase().startsWith("cmd.")) {
                            logger.info("Execute cmd '"+type+"' ......");
                            String cmd=TestProperty.get(type, "");
                            if(cmd.length()>0) {
                                Executor exe=new Executor(cmd,false);
                                exe.start();
                            }
                        }
                    }
                }
            }

            logger.info("TestAgent is stopped");

        } catch (InvalidClientIDException e) {
            runner.stopRunner();
            restart=false;
        } catch(Throwable e) {
            System.out.println(e);
            restart=true;

        } finally {
            ConnectionTool.close();
            if(runner.isCrashed()) {
                restart=true;
            }

            if(restart) {
                Runtime rt = Runtime.getRuntime();
                try {
                    Properties vp=new Properties();
                    String baseDir=TestProperty.get("testdriver.win.base");

                    vp.load(new FileInputStream(new File(baseDir+"/version.properties")));
                    String version=vp.getProperty("major.number")+"."+vp.getProperty("build.number")+"."+vp.getProperty("svn.revision");
                    rt.exec("cmd /k start java -jar "+baseDir+"/testdriver_"+version+".jar com.activenetwork.qa.testdriver.date.TestAgent");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Email.sendErrorMessageToMaster("Failed to restart "+hostName, e1.getMessage());
                }
            }

            System.exit(0);

        }
    }

    private void processQuery(TextMessage msg) throws JMSException {
        String msgID=msg.getStringProperty("msgID");
        String replyto=msg.getStringProperty("replyto");
        String replytype=msg.getStringProperty("replytype");
        boolean replayToTopic=replytype!=null && replytype.equalsIgnoreCase("topic");

        if(replyto==null || replyto.length()<1) {
            logger.debug("ReplyTo message queue is unknown. Skipped query!");
            return;
        }

        String info=getRunnerStatusInfo();
        producer.connect(replyto, replayToTopic);
        Message toreply=producer.createTextMessage(info);
        toreply.setStringProperty("type","runnerInfo");
        toreply.setStringProperty("msgID",msgID);
        toreply.setStringProperty("node", hostName);
        toreply.setStringProperty("ip", hostIP);
        toreply.setStringProperty("selector", runner.getSelector());
        producer.produceMessage(toreply);
        producer.disconnect();

    }

    private String getRunnerStatusInfo() {
        int status=runner.getStatus();
        String info=hostName+"("+hostIP+")";
        if(status==BUSY){
            info +=" - running "+runner.getCaseName()+ " time remaining="+runner.getTimeRemaining();
        } else if (status==IDLE) {
            info +=" - idle";
        } else if (status==SUSPEND) {
            info +=" - suspended";
        } else if (status==OCAM_RESTART) {
            info +=" - suspended for OCAM restart";
        } else {
            info +=" - (unknown status code '"+status+"')";
        }
        return info;
    }
}
