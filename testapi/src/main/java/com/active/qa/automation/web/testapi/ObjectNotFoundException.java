package com.active.qa.automation.web.testapi;

/**
 * Thrown when methods are called on an object that cannot be found on the current page.
 * Created by tchen on 1/11/2016.
 */
@SuppressWarnings("serial")
public class ObjectNotFoundException extends AutoRuntimeException {

    /**
     * Constructs a ObjectNotFoundException with no detail message.
     */
    public ObjectNotFoundException() {
        super();
    }

    /**
     * Constructs a ObjectNotFoundException with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public ObjectNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a ObjectNotFoundException with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a ObjectNotFoundException with the specified cause.
     *
     * @param cause
     *            the cause
     */
    public ObjectNotFoundException(Throwable cause) {
        super(cause);
    }
}

