package com.tribia.application.schedulertests;

import com.tribia.application.TestcontainersConfiguration;
import com.tribia.application.entity.User;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.repository.VehicleRepository;
import com.tribia.application.service.ReminderSchedulerService;
import com.tribia.application.service.impl.ConsoleNotificationImpl;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "send.reminder.cron.expression=0/1 * * * * ?"
})
public class ReminderSchedulerServiceTest {

    @Autowired
    private ReminderSchedulerService reminderSchedulerService;

    @MockBean
    private VehicleRepository vehicleRepository;

    @SpyBean
    private ConsoleNotificationImpl notificationService;

    @BeforeEach
    void setUp() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("EV22347");
        vehicle.setSubscribed(true);
        vehicle.setInspectionDeadline(LocalDate.now().plusDays(30));
        vehicle.setNotificationSent(false);

        User user = new User();
        user.setEmail("test@example.com");
        vehicle.setUser(user);

        when(vehicleRepository.findBySubscribedTrueAndInspectionDeadlineBetweenAndNotificationSentFalse(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(vehicle));
    }

    @Test
    void schedulerShouldTriggerNotificationService() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(notificationService, atLeast(1))
                            .sendNotification(any(Vehicle.class), any(LocalDate.class));
                });
    }

    //Test to verify that notification service is not called when there are no vehicles needing inspection
    @Test
    void schedulerShouldNotTriggerNotificationService() {
        when(vehicleRepository.findBySubscribedTrueAndInspectionDeadlineBetweenAndNotificationSentFalse(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(notificationService, never())
                            .sendNotification(any(Vehicle.class), any(LocalDate.class));
                });
    }

    //Test to verify that notification service is called for each vehicle needing inspection
    @Test
    void schedulerShouldTriggerNotificationServiceForEachVehicle() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setLicensePlate("EV22347");
        vehicle1.setSubscribed(true);
        vehicle1.setInspectionDeadline(LocalDate.now().plusDays(30));
        vehicle1.setNotificationSent(false);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicensePlate("EV22348");
        vehicle2.setSubscribed(true);
        vehicle2.setInspectionDeadline(LocalDate.now().plusDays(30));
        vehicle2.setNotificationSent(false);

        User user = new User();
        user.setEmail("test@example.com");
        vehicle1.setUser(user);
        vehicle2.setUser(user);

        when(vehicleRepository.findBySubscribedTrueAndInspectionDeadlineBetweenAndNotificationSentFalse(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(vehicle1, vehicle2));

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(notificationService, times(2))
                            .sendNotification(any(Vehicle.class), any(LocalDate.class));
                });
    }

}
