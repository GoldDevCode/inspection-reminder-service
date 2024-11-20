package com.tribia.application.exception;

public class VehicleNotAlreadySubscribedException extends RuntimeException {
    public VehicleNotAlreadySubscribedException(String message) {
        super(message);
    }
}
