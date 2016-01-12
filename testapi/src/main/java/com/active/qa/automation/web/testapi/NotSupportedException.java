package com.active.qa.automation.web.testapi;

/**
 * Thrown when a function/feature is not supported for certain tool/product.
 * Created by tchen on 1/11/2016.
 */
public class NotSupportedException extends AutoRuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a NotImplementedException with no detail message.
     */
    public NotSupportedException() {
        super();
    }

    /**
     * Constructs a NotImplementedException with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public NotSupportedException(String message) {
        super(message);
    }

    /**
     * Constructs a NotImplementedException with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public NotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a NotImplementedException with the specified cause.
     *
     * @param cause
     *            the cause
     */
    public NotSupportedException(Throwable cause) {
        super(cause);
    }
}

