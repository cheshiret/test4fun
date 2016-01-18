package com.active.qa.automation.web.testapi.exception;

/**
 * Thrown when it needs to fail the test case due to some errors which are not thrown as exceptions
 * Created by tchen on 1/11/2016.
 */
@SuppressWarnings("serial")
public class TestCaseFailedException extends AutoRuntimeException {
  /**
   *
   */
  public TestCaseFailedException() {
    super();
  }

  /**
   * @param message
   */
  public TestCaseFailedException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public TestCaseFailedException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public TestCaseFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}

