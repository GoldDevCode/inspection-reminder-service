package com.tribia.application.exception;

public class SVVRequestQuotaExceededException extends RuntimeException {
    public SVVRequestQuotaExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
