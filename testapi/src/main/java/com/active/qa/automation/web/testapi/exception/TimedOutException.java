package com.active.qa.automation.web.testapi.exception;

/**
 * Thrown when a synchronization process is timed out
 * Created by tchen on 1/11/2016.
 */
public class TimedOutException extends AutoRuntimeException {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a TimedOutException with no detail message.
   */
  public TimedOutException() {
    super();
  }

  /**
   * Constructs a TimedOutException with the specified detail message.
   *
   * @param message the detail message
   */
  public TimedOutException(String message) {
    super(message);
  }

  /**
   * Constructs a TimedOutException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause
   */
  public TimedOutException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a TimedOutException with the specified cause.
   *
   * @param cause the cause
   */
  public TimedOutException(Throwable cause) {
    super(cause);
  }
}

