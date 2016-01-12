package com.active.qa.automation.web.testapi;

/**
 * This is the RuntimeException specific for Automation
 * Created by tchen on 1/11/2016.
 */
public class AutoRuntimeException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a AutoRuntimeException with no detail message.
     */
    public AutoRuntimeException() {
        super();
    }

    /**
     * Constructs a AutoRuntimeException with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public AutoRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a AutoRuntimeException with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public AutoRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a AutoRuntimeException with the specified cause.
     *
     * @param cause
     *            the cause
     */
    public AutoRuntimeException(Throwable cause) {
        super(cause);
    }
}

