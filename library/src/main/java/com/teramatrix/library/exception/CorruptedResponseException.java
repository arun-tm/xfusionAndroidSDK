package com.teramatrix.library.exception;

/**
 * It's a custom exception specially designed for some critical events.
 * This is related to the server's response. Sometimes program is unable to evaluate server's response.
 * In that condition this exception will be thrown with specific messages to describe problem.
 * Those messages are listed below.
 */

public class CorruptedResponseException extends Exception {
    private String message = null;

    public static final String NULL_RESPONSE = "Null response from the server";
    public static final String STATUS_NOT_OK = "Response status is not OK";
    public static final String EMPTY_ARRAY = "Results array is empty";

    public CorruptedResponseException() {
        super();
    }

    public CorruptedResponseException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
