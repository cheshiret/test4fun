package com.active.qa.automation.web.testapi;

/**
 * Thrown when some data are invalid in format, range, or type
 * Created by tchen on 1/11/2016.
 */
@SuppressWarnings("serial")
public class InvalidDataException extends AutoRuntimeException {

    /**
     *
     */
    public InvalidDataException() {
        super();
    }

    /**
     * @param message
     */
    public InvalidDataException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidDataException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

}

