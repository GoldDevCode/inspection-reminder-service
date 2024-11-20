package com.tribia.application.exception;

public class AlreadyUnsubscribedException extends RuntimeException {
    public AlreadyUnsubscribedException(String message) {
        super(message);
    }
}
