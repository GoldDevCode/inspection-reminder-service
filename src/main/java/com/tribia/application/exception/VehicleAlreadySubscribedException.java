package com.tribia.application.exception;

public class VehicleAlreadySubscribedException extends RuntimeException {
    public VehicleAlreadySubscribedException(String message) {
        super(message);
    }
}
