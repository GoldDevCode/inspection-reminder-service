package com.tribia.application.schedulertests;

import com.tribia.application.TestcontainersConfiguration;
import com.tribia.application.client.SVVApiClient;
import com.tribia.application.dto.svv.response.KjoretoyData;
import com.tribia.application.dto.svv.response.PeriodiskKjoretoyKontroll;
import com.tribia.application.dto.svv.response.SVVApiResponse;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.repository.VehicleRepository;
import com.tribia.application.service.InspectionDateUpdaterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class InspectionDateUpdaterServiceIntegrationTest {

    @Autowired
    private InspectionDateUpdaterService inspectionDateUpdaterService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @MockBean
    private SVVApiClient svvApiClient;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        vehicleRepository.deleteAll();

        // Set up mock response for SVVApiClient
        SVVApiResponse mockResponse = new SVVApiResponse();
        KjoretoyData kjoretoyData = new KjoretoyData();
        PeriodiskKjoretoyKontroll periodiskKjoretoyKontroll = new PeriodiskKjoretoyKontroll();
        periodiskKjoretoyKontroll.setKontrollfrist(LocalDate.now().plusYears(1).toString());
        kjoretoyData.setPeriodiskKjoretoyKontroll(periodiskKjoretoyKontroll);
        mockResponse.setKjoretoydataListe(List.of(kjoretoyData));

        when(svvApiClient.getVehicleData(anyString())).thenReturn(mockResponse);
    }

    @Test
    void updateExpiredInspectionDates_shouldUpdateDates() {
        // Arrange
        Vehicle expiredVehicle = new Vehicle();
        expiredVehicle.setLicensePlate("ABC123");
        expiredVehicle.setInspectionDeadline(LocalDate.now().minusDays(1));
        expiredVehicle.setNotificationSent(true);
        vehicleRepository.save(expiredVehicle);

        // Act
        inspectionDateUpdaterService.updateExpiredInspectionDates();

        // Assert
        Vehicle updatedVehicle = vehicleRepository.findByLicensePlate("ABC123").orElseThrow();
        assertNotNull(updatedVehicle);
        assertTrue(updatedVehicle.getInspectionDeadline().isAfter(LocalDate.now()));
        assertFalse(updatedVehicle.isNotificationSent());
    }
}

