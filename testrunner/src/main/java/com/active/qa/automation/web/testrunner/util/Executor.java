package com.active.qa.automation.web.testrunner.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by tchen on 1/18/2016.
 */
public class Executor  extends Thread {
    private static Logger logger = Logger.getLogger(Executor.class);
    private String cmd=null;
    private int exitCode=-1;
    private Process pr=null;
    private boolean waitFor;
//	FreezeDetect detector;

    public Executor(String cmd) {
        this(cmd,true);
    }

    public Executor(String cmd, boolean waitFor) {
        this.cmd=cmd;
        this.waitFor=waitFor;
//		detector=null;
    }

    public void run() {
        Runtime rt = Runtime.getRuntime();
        try {
            logger.info("Executing command: "+cmd);
            pr=rt.exec(cmd);
            if(waitFor) {
                BufferedReader error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                String line=null;
//			    detector=new FreezeDetect();
//			    detector.start();
                while((line=error.readLine()) != null) {
//			    	detector.notice();
                    System.out.println(line);
                }
//			    detector.exit();
                exitCode = pr.waitFor();
            }
        } catch (IOException e) {
            exitCode=1;
            e.printStackTrace();
        } catch (InterruptedException e) {
            exitCode=1;
            e.printStackTrace();
        }
    }

    public void killProcess() {
        if(pr!=null) {
            pr.destroy();
//			if(detector!=null) {
//				detector.exit();
//			}
        }
    }

    public int getExitVal() {
        return exitCode;
    }

//	private class FreezeDetect extends Thread {
//		private long previous,current;
//	  	private boolean done;
//	  	private int count;
//	  	private static final int TIMEOUT=300;
//
//	  	public FreezeDetect() {
//	  	  	previous=0;
//	  	  	current=0;
//	  	  	count=0;
//	  	  	done=false;
//	  	}
//
//	  	public void run() {
//	  	  	while (!done && count<TIMEOUT) {
//
//	  	  	  try {
//	  	  	    sleep(1000);
//  	  	    	if(previous==current) {
//	  	  	      	count++;
//	  	  	    } else {
//	  	  	      	previous=current;
//	  	  	    }
//	  	  	  } catch (InterruptedException e) {
//	  	  	  }
//	  	  	}
//
//	  	  	if(!done ) {
//	  	  		logger.warn("There are not output in "+TIMEOUT+" seconds. Kill the process!");
//	  	  		killProcess();
//	  	  	}
//	  	}
//
//	  	public void notice() {
//		  	current++;
//		  	count=0;
//		}
//
//	  	public void exit() {
//	  	  	done=true;
//	  	}
//	}
}
