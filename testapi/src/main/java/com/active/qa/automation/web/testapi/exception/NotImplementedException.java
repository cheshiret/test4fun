package com.active.qa.automation.web.testapi.exception;

/**
 * Thrown when a function/feature is not implemented
 * Created by tchen on 1/11/2016.
 */
public class NotImplementedException extends AutoRuntimeException {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a NotImplementedException with no detail message.
   */
  public NotImplementedException() {
    super();
  }

  /**
   * Constructs a NotImplementedException with the specified detail message.
   *
   * @param message the detail message
   */
  public NotImplementedException(String message) {
    super(message);
  }

  /**
   * Constructs a NotImplementedException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause
   */
  public NotImplementedException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a NotImplementedException with the specified cause.
   *
   * @param cause the cause
   */
  public NotImplementedException(Throwable cause) {
    super(cause);
  }
}

