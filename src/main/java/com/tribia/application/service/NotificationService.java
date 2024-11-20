package com.tribia.application.service;

import com.tribia.application.entity.Vehicle;

import java.time.LocalDate;

public interface NotificationService {
    void sendNotification(Vehicle vehicle, LocalDate today);
}
