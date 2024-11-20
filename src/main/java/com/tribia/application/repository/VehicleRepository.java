package com.tribia.application.repository;

import com.tribia.application.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByLicensePlateIn(List<String> licensePlates);

    List<Vehicle> findBySubscribedTrueAndInspectionDeadlineBetweenAndNotificationSentFalse(LocalDate start, LocalDate end);

    List<Vehicle> findByInspectionDeadlineBefore(LocalDate date);
}
