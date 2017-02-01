package com.andevice.library.exception;

/**
 * Created by arun.singh on 1/21/2017.
 */
/**
 * It's a custom exception specially designed for some critical events.
 * This is used when there are invalid data given by user like filling a registratino form or login credential.
 */

public class InvalidRequestParametersException extends Exception {

    private String message;
    public InvalidRequestParametersException()
    {
        super();
    }
    public InvalidRequestParametersException(String message)
    {
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
