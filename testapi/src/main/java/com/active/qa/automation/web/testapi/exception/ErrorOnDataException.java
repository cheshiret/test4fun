package com.active.qa.automation.web.testapi.exception;

/**
 * Thrown when the actual data were not the same as the expected
 * Created by tchen on 1/11/2016.
 */
public class ErrorOnDataException extends AutoRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public ErrorOnDataException() {
        super();
    }

    /**
     * When Doing verification, we expect use this constructor to generate message
     * with expected result and actual result;
     * @param message
     * @param expectedResult
     * @param actualResult
     */
    public ErrorOnDataException(String message,Object expectedResult,Object actualResult) {
        super(message+"\n Expected Result:"+expectedResult+"\n Actual   Result:"+actualResult);
    }

    /**
     * @param message
     */
    public ErrorOnDataException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ErrorOnDataException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ErrorOnDataException(String message, Throwable cause) {
        super(message, cause);
    }

}

