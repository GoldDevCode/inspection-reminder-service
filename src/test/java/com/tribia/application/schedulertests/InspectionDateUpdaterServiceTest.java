package com.tribia.application.schedulertests;

import com.tribia.application.TestcontainersConfiguration;
import com.tribia.application.client.SVVApiClient;
import com.tribia.application.dto.svv.response.KjoretoyData;
import com.tribia.application.dto.svv.response.PeriodiskKjoretoyKontroll;
import com.tribia.application.dto.svv.response.SVVApiResponse;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.repository.VehicleRepository;
import com.tribia.application.service.InspectionDateUpdaterService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        "update.inspection.dates.cron.expression=0/1 * * * * ?"
})
public class InspectionDateUpdaterServiceTest {

    @Autowired
    private InspectionDateUpdaterService inspectionDateUpdaterService;

    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private SVVApiClient svvApiClient;

    @BeforeEach
    void setUp() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("EV22347");
        vehicle.setInspectionDeadline(LocalDate.now().minusDays(1));
        vehicle.setNotificationSent(false);

        when(vehicleRepository.findByInspectionDeadlineBefore(any(LocalDate.class)))
                .thenReturn(Collections.singletonList(vehicle));

        SVVApiResponse response = new SVVApiResponse();
        // Set up the response object with necessary data
        KjoretoyData kjoretoyData = new KjoretoyData();

        PeriodiskKjoretoyKontroll periodiskKjoretoyKontroll = new PeriodiskKjoretoyKontroll();
        periodiskKjoretoyKontroll.setKontrollfrist(LocalDate.now().plusYears(1).toString());
        kjoretoyData.setPeriodiskKjoretoyKontroll(periodiskKjoretoyKontroll);
        response.setKjoretoydataListe(List.of(kjoretoyData));
        when(svvApiClient.getVehicleData(anyString())).thenReturn(response);
    }

    @Test
    void schedulerShouldUpdateInspectionDates() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(vehicleRepository, atLeast(1))
                            .save(any(Vehicle.class));
                });
    }

    // Test to verify that the service does not update when there are no expired vehicles
    @Test
    void schedulerShouldNotUpdateWhenNoExpiredVehicles() {
        when(vehicleRepository.findByInspectionDeadlineBefore(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(vehicleRepository, never())
                            .save(any(Vehicle.class));
                });
    }

    // Test to verify that the service updates each expired vehicle
    @Test
    void schedulerShouldUpdateEachExpiredVehicle() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setLicensePlate("EV22347");
        vehicle1.setInspectionDeadline(LocalDate.now().minusDays(1));
        vehicle1.setNotificationSent(false);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicensePlate("EV22348");
        vehicle2.setInspectionDeadline(LocalDate.now().minusDays(1));
        vehicle2.setNotificationSent(false);

        when(vehicleRepository.findByInspectionDeadlineBefore(any(LocalDate.class)))
                .thenReturn(List.of(vehicle1, vehicle2));

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(vehicleRepository, times(2))
                            .save(any(Vehicle.class));
                });
    }
}