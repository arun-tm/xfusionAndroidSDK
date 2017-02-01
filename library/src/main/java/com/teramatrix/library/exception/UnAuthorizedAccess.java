package com.teramatrix.library.exception;

/**
 * Created by arun.singh on 1/21/2017.
 */
/**
 * /**
 * It's a custom exception specially designed for some critical events.
 * This is used when user is accessigng an API without having a valid token and access key
 */

public class UnAuthorizedAccess extends Exception {

    private String message;
    public UnAuthorizedAccess()
    {
        super();
    }
    public UnAuthorizedAccess(String message)
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
