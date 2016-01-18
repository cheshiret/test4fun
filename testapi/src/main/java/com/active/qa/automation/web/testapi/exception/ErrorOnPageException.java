package com.active.qa.automation.web.testapi.exception;

/**
 * Thrown when when UI Page has errors on it
 * Created by tchen on 1/11/2016.
 */
@SuppressWarnings("serial")
public class ErrorOnPageException extends AutoRuntimeException {

    /**
     *
     */
    public ErrorOnPageException() {
        super();
    }

    /**
     * When Doing verification, we expect use this constructor to generate message
     * with expected result and actual result;
     * @param message
     * @param expectedResult
     * @param actualResult
     */
    public ErrorOnPageException(String message,Object expectedResult,Object actualResult) {
        super(message+"\n Expected Result:"+expectedResult+"\n Actual   Result:"+actualResult);
    }

    /**
     * @param message
     */
    public ErrorOnPageException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ErrorOnPageException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ErrorOnPageException(String message, Throwable cause) {
        super(message, cause);
    }

}

