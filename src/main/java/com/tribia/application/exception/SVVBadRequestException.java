package com.tribia.application.exception;

public class SVVBadRequestException extends RuntimeException {
    public SVVBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}