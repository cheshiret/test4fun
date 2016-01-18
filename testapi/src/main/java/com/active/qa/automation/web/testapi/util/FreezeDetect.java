package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.interfaces.browser.Browser;

/**
 * This class is used to detect if any process is frozen/no-responding.
 * It will keep a record of noticed changes from target object. If there is not change notice within the timeout period, a freeze is detected.
 * If a freeze is detected, it will kill the browser to release the resource
 * Created by tchen on 1/11/2016.
 */
public class FreezeDetect extends Thread {
    private int previous,current;
    private boolean done,killed;
    private int count;
    private static AutomationLogger logger=AutomationLogger.getInstance();
    private boolean debug;
    private int timeout;
    private String mark;
    private static final int TIMEOUT=45;

    public FreezeDetect() {
        previous=0;
        current=0;
        count=0;
        done=false;
        killed=false;
        debug=Boolean.valueOf(TestProperty.getProperty("debug")).booleanValue();
//  	  	debug=false;
        mark="";
        timeout=TIMEOUT;
    }

    public void run() {
        while (!done && count<timeout) {

            try {
                sleep(1000);
                if(!debug) {
                    //turn off freeze detector in DEBUG mode
                    if(previous==current) {
                        count++;
                    } else {
                        count=0;
                        previous=current;
                    }
                }
            } catch (InterruptedException e) {
            }
        }

        if(!done && !debug) {
            logger.warn("The script froze for "+count+" seconds"+(mark.length()>0?" at "+mark:"")+". Kill browsers.");
            Browser.getInstance().catchScreenShot(TestProperty.getProperty("snapshot"));
            TestProperty.putProperty("snapshot","");
            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("taskkill /F /IM iexplore.exe /T").waitFor();
                rt.exec("taskkill /F /IM dw20.exe /T").waitFor();
                killed=true;
            }catch (Exception e) {
                logger.error("Failed to kill browser due to "+e.getMessage());
            }
        }
    }

    public void notice(String mark) {
        current++;
        this.mark=mark;
        this.count=0;
        this.timeout=TIMEOUT;
    }

    public void notice(String mark,int timeout) {
        current++;
        this.mark=mark;
        this.timeout=timeout;
        this.count=0;
    }

    public void exit() {
        done=true;
    }

    public boolean killed() {
        return killed;
    }
}

