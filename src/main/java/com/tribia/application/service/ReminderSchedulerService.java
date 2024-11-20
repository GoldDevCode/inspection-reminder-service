package com.tribia.application.service;

import com.tribia.application.entity.Vehicle;
import com.tribia.application.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.lang.Long.parseLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderSchedulerService {

    private final VehicleRepository vehicleRepository;
    private final NotificationService notificationService;
    Logger logger = LoggerFactory.getLogger(ReminderSchedulerService.class);
    // create a field for number of days to send reminder before inspection deadline
    @Value("${send.reminder.days.before.inspection.deadline}")
    private String daysBeforeInspectionDeadline;

    @Scheduled(cron = "${send.reminder.cron.expression}")
    public void sendDailyReminders() {

        logger.info("Starting to send daily reminders");

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(parseLong(daysBeforeInspectionDeadline));

        List<Vehicle> vehiclesNeedingInspection = vehicleRepository
                .findBySubscribedTrueAndInspectionDeadlineBetweenAndNotificationSentFalse(today, thirtyDaysFromNow);

        for (Vehicle vehicle : vehiclesNeedingInspection) {
            notificationService.sendNotification(vehicle, today);
            vehicle.setNotificationSent(true);
            vehicleRepository.save(vehicle);
        }

        logger.info("Finished sending daily reminders");
    }
}
