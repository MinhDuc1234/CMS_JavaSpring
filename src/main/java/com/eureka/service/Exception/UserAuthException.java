package com.eureka.service.Exception;

public class UserAuthException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserAuthException(String message) {
        super(message);
    }
}