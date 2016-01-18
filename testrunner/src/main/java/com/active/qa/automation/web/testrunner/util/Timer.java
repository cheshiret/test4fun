package com.active.qa.automation.web.testrunner.util;

import java.util.Calendar;

/**
 * Created by tchen on 1/18/2016.
 */
public class Timer {
  private long start;
  private long timeout;

  public Timer() {

    this(60 * 1000);
  }

  public Timer(long timeout) {
    start = Calendar.getInstance().getTimeInMillis();
    this.timeout = timeout;
  }

  public long diff() {
    long now = Calendar.getInstance().getTimeInMillis();
    return now - start;
  }

  public void reset() {
    start = Calendar.getInstance().getTimeInMillis();
  }

  public boolean isTimedout() {
    return diff() > timeout;
  }
}
