package com.active.qa.automation.web.testrunner;

import com.active.qa.automation.web.testrunner.util.ConsumerTool;
import com.active.qa.automation.web.testrunner.util.TestProperty;
import org.apache.log4j.Logger;

import javax.jms.JMSException;

/**
 * Created by tchen on 1/18/2016.
 */
public class TestConsumer extends Thread implements TestConstants{
    static Logger logger = Logger.getLogger(TestConsumer.class);
    private ConsumerTool consumer;
    //	private ProducerTool producer;
    private String selector;
//	private boolean stop=false;

    public TestConsumer(String selector) {
        this.selector=selector;
    }

    @Override
    public void run() {
        TestProperty.load();
        TestProperty.resetLogfile(TestProperty.get("host.name"));
        String testQueue=TestProperty.get("mq.test.queue");
        String rftQueue=TestProperty.get("mq.rft.queue");
        String smQueue=TestProperty.get("mq.sm.queue");
        String eftQueue=TestProperty.get("mq.eft.queue");
        String reportQueue=TestProperty.get("mq.report.queue");

        try {
            consumer = ConsumerTool.getInstance();
            consumer.setSelector(selector);
            ConsumerTool.logger=logger;
            consumer.consumeAllMessage(testQueue, 500);
            consumer.consumeAllMessage(smQueue, 500);
            consumer.consumeAllMessage(eftQueue, 500);
            consumer.consumeAllMessage(reportQueue, 500);
            consumer.consumeAllMessage(rftQueue, 500);
        } catch (Exception e) {

        } finally {
            try {
                consumer.disconnect();
            } catch (JMSException e) {
            }
        }
    }
}
