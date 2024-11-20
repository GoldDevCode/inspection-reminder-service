package com.tribia.application.repositorytests;


import com.tribia.application.TestcontainersConfiguration;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;


    @Test
    void testFindByLicensePlate() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC123");
        vehicleRepository.save(vehicle);

        Optional<Vehicle> foundVehicle = vehicleRepository.findByLicensePlate("ABC123");
        assertTrue(foundVehicle.isPresent());
        assertEquals("ABC123", foundVehicle.get().getLicensePlate());
    }

    @Test
    void testFindByLicensePlateIn() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setLicensePlate("ABC123");
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicensePlate("XYZ789");
        vehicleRepository.save(vehicle1);
        vehicleRepository.save(vehicle2);

        List<Vehicle> foundVehicles = vehicleRepository
                .findByLicensePlateIn(List.of("ABC123", "XYZ789"));
        assertEquals(2, foundVehicles.size());
    }

    @Test
    void testFindBySubscribedTrueAndInspectionDeadlineBetweenAndNotificationSentFalse() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC123");
        vehicle.setSubscribed(true);
        vehicle.setInspectionDeadline(LocalDate.now()
                .plusMonths(1));
        vehicleRepository.save(vehicle);

        List<Vehicle> foundVehicles = vehicleRepository
                .findBySubscribedTrueAndInspectionDeadlineBetweenAndNotificationSentFalse(
                        LocalDate.now(),
                        LocalDate.now()
                                .plusMonths(2));

        assertEquals(1, foundVehicles.size());
    }

    @Test
    void testFindByInspectionDeadlineBefore() {

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC123");
        vehicle.setInspectionDeadline(LocalDate.now()
                .minusDays(1));
        vehicleRepository.save(vehicle);

        List<Vehicle> foundVehicles = vehicleRepository
                .findByInspectionDeadlineBefore(LocalDate.now());

        assertEquals(1, foundVehicles.size());
    }

    @Test
    void testFindByInspectionDeadlineAfterNotFound() {

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC123");
        vehicle.setInspectionDeadline(LocalDate.now()
                .plusDays(10));
        vehicleRepository.save(vehicle);

        List<Vehicle> foundVehicles = vehicleRepository
                .findByInspectionDeadlineBefore(LocalDate.now());

        assertEquals(0, foundVehicles.size());
    }
}
