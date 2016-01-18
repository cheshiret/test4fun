package com.active.qa.automation.web.testapi.exception;

/**
 * Thrown when certain critical data were not initialized
 * Created by tchen on 1/11/2016.
 */
public class NotInitializedException extends AutoRuntimeException {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a NotInitializedException with no detail message.
   */
  public NotInitializedException() {
    super();
  }

  /**
   * Constructs a NotInitializedException with the specified detail message.
   *
   * @param message the detail message
   */
  public NotInitializedException(String message) {
    super(message);
  }

  /**
   * Constructs a ActionFailedException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause
   */
  public NotInitializedException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a ActionFailedException with the specified cause.
   *
   * @param cause the cause
   */
  public NotInitializedException(Throwable cause) {
    super(cause);
  }
}


