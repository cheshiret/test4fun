package com.active.qa.automation.web.testapi;

/**
 * Throw when the execution of test case is stopped by user on purpose
 * Created by tchen on 1/11/2016.
 */
public class UserStoppedScriptException extends AutoRuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a UserStoppedScriptException with no detail message.
     */
    public UserStoppedScriptException() {
        super();
    }

    /**
     * Constructs a UserStoppedScriptException with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public UserStoppedScriptException(String message) {
        super(message);
    }

    /**
     * Constructs a UserStoppedScriptException with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public UserStoppedScriptException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a UserStoppedScriptException with the specified cause.
     *
     * @param cause
     *            the cause
     */
    public UserStoppedScriptException(Throwable cause) {
        super(cause);
    }
}

