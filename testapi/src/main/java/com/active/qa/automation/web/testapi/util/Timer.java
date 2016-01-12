package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.ItemNotFoundException;
import com.active.qa.automation.web.testapi.interfaces.Ready;

import java.util.Calendar;

/**
 * keep track of the time different
 * Created by tchen on 1/11/2016.
 */
public class Timer {
    private long start;

    public Timer() {
        start=Calendar.getInstance().getTimeInMillis();
    }

    public int diff() {
        return DateFunctions.getTimeDiff(start);
    }

    public long diffLong() {
        long end = Calendar.getInstance().getTimeInMillis();
        return end-start;

    }

    public static void sleep(long millsec) {
        try {
            if(millsec>0)
                Thread.sleep(millsec);
        } catch (Exception e) {}
    }

    public static void waitUntilReady(long timeout,Ready ready) {
        boolean isReady=false;
        Timer timer=new Timer();

        while(!isReady && timer.diffLong()<timeout) {
            sleep(1000);
            isReady=ready.isReady();
        }

        long time=timer.diffLong();

        if(!isReady) {
            throw new ItemNotFoundException("Not ready in "+time+" millsecs");
        }
    }

    public void reset() {
        start=Calendar.getInstance().getTimeInMillis();
    }

    /**
     * extra delay to wait for actions like click/setText to take effect based on benchmark
     */
    public static void actionSync() {
        sleep(SysInfo.benchmark()-TestProperty.getIntProperty("action.delay.sync",400));
    }
}

