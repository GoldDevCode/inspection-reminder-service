package com.tribia.application.service.impl;

import com.tribia.application.entity.Vehicle;
import com.tribia.application.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class ConsoleNotificationImpl implements NotificationService {

    Logger logger = LoggerFactory.getLogger(ConsoleNotificationImpl.class);

    public void sendNotification(Vehicle vehicle, LocalDate today) {
        long daysUntilInspection = ChronoUnit.DAYS.between(today, vehicle.getInspectionDeadline());
        logger.info("Sending notification to user: " +
                vehicle.getUser().getEmail() +
                " | Vehicle: " +
                vehicle.getLicensePlate() +
                " | Inspection Deadline: " +
                vehicle.getInspectionDeadline() +
                " | Days Until Inspection: " +
                daysUntilInspection);
    }
}
