package com.tribia.application.servicetests;

import com.tribia.application.client.SVVApiClient;
import com.tribia.application.dto.svv.response.KjoretoyData;
import com.tribia.application.dto.svv.response.PeriodiskKjoretoyKontroll;
import com.tribia.application.dto.svv.response.SVVApiResponse;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.repository.VehicleRepository;
import com.tribia.application.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private SVVApiClient svvApiClient;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private List<String> licensePlates;
    private List<Vehicle> vehicles;
    private SVVApiResponse svvApiResponse;

    @BeforeEach
    void setUp() {
        licensePlates = Arrays.asList("ABC123", "XYZ789");
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setLicensePlate("ABC123");
        vehicle1.setInspectionDeadline(LocalDate.now().plusYears(1));
        vehicle1.setSubscribed(true);
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicensePlate("XYZ789");
        vehicle2.setInspectionDeadline(LocalDate.now().plusYears(2));
        vehicle2.setSubscribed(true);

        vehicles = Arrays.asList(
                vehicle1,
                vehicle2
        );

        svvApiResponse = new SVVApiResponse();
        KjoretoyData kjoretoyData = new KjoretoyData();
        PeriodiskKjoretoyKontroll periodiskKjoretoyKontroll = new PeriodiskKjoretoyKontroll();
        periodiskKjoretoyKontroll.setKontrollfrist(LocalDate.now().plusYears(1).toString());
        kjoretoyData.setPeriodiskKjoretoyKontroll(periodiskKjoretoyKontroll);
        svvApiResponse.setKjoretoydataListe(List.of(kjoretoyData));
    }

    @Test
    void findByLicensePlates_shouldReturnVehicles() {
        when(vehicleRepository.findByLicensePlateIn(licensePlates)).thenReturn(vehicles);

        List<Vehicle> result = vehicleService.findByLicensePlates(licensePlates);

        assertEquals(vehicles, result);
        verify(vehicleRepository).findByLicensePlateIn(licensePlates);
    }

    @Test
    void createVehicle_shouldCreateAndSaveVehicle() {
        String licensePlate = "ABC123";
        when(svvApiClient.getVehicleData(licensePlate)).thenReturn(svvApiResponse);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = vehicleService.createVehicle(licensePlate);

        assertNotNull(result);
        assertEquals(licensePlate, result.getLicensePlate());
        assertEquals(LocalDate.now().plusYears(1), result.getInspectionDeadline());
        assertTrue(result.isSubscribed());
        verify(svvApiClient).getVehicleData(licensePlate);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void saveAll_shouldSaveAllVehicles() {
        when(vehicleRepository.saveAll(vehicles)).thenReturn(vehicles);

        List<Vehicle> result = vehicleService.saveAll(vehicles);

        assertEquals(vehicles, result);
        verify(vehicleRepository).saveAll(vehicles);
    }

    @Test
    void findByLicensePlates_withEmptyList_shouldReturnEmptyList() {
        List<String> emptyList = List.of();
        when(vehicleRepository.findByLicensePlateIn(emptyList)).thenReturn(List.of());

        List<Vehicle> result = vehicleService.findByLicensePlates(emptyList);

        assertTrue(result.isEmpty());
        verify(vehicleRepository).findByLicensePlateIn(emptyList);
    }

    @Test
    void createVehicle_withNullResponse_shouldThrowException() {
        String licensePlate = "ABC123";
        when(svvApiClient.getVehicleData(licensePlate)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> vehicleService.createVehicle(licensePlate));
        verify(svvApiClient).getVehicleData(licensePlate);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void saveAll_withEmptyList_shouldReturnEmptyList() {
        List<Vehicle> emptyList = List.of();
        when(vehicleRepository.saveAll(emptyList)).thenReturn(emptyList);

        List<Vehicle> result = vehicleService.saveAll(emptyList);

        assertTrue(result.isEmpty());
        verify(vehicleRepository).saveAll(emptyList);
    }
}

