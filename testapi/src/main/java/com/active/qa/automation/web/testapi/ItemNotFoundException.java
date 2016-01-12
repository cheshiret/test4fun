package com.active.qa.automation.web.testapi;

/**
 * Thrown when an item cannot be found from a place
 * Created by tchen on 1/11/2016.
 */
@SuppressWarnings("serial")
public class ItemNotFoundException extends RuntimeException {

    /**
     * Constructs a ItemNotFoundException with no detail message.
     */
    public ItemNotFoundException() {
        super();
    }

    /**
     * Constructs a ItemNotFoundException with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public ItemNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a ItemNotFoundException with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a ItemNotFoundException with the specified cause.
     *
     * @param cause
     *            the cause
     */
    public ItemNotFoundException(Throwable cause) {
        super(cause);
    }
}

