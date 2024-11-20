package com.tribia.application.service;

import com.tribia.application.client.SVVApiClient;
import com.tribia.application.dto.svv.response.SVVApiResponse;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InspectionDateUpdaterService {

    private final VehicleRepository vehicleRepository;
    private final SVVApiClient svvApiClient;
    Logger logger = LoggerFactory.getLogger(InspectionDateUpdaterService.class);

    @Scheduled(cron = "${update.inspection.dates.cron.expression}")
    @Transactional
    public void updateExpiredInspectionDates() {
        LocalDate today = LocalDate.now();
        List<Vehicle> expiredVehicles = vehicleRepository
                .findByInspectionDeadlineBefore(today);

        logger.info("Updating Expired Inspection Dates Today ({})", today);

        for (Vehicle vehicle : expiredVehicles) {
            try {
                var expiredDate = vehicle.getInspectionDeadline();
                SVVApiResponse vehicleData = svvApiClient
                        .getVehicleData(vehicle.getLicensePlate());

                LocalDate newInspectionDeadline = LocalDate
                        .parse(vehicleData
                                .getKjoretoydataListe()
                                .getFirst()
                                .getPeriodiskKjoretoyKontroll()
                                .getKontrollfrist());

                vehicle.setInspectionDeadline(newInspectionDeadline);
                vehicle.setNotificationSent(false);
                vehicleRepository.save(vehicle);

                logger.info("Updated: {} | Old Inspection Date: {} | New Inspection Date: {}",
                        vehicle.getLicensePlate(),
                        expiredDate,
                        vehicle.getInspectionDeadline()
                );

            } catch (Exception e) {
                logger.error("Failed to update {} : {}", vehicle.getLicensePlate(), e.getMessage());
            }
        }
        logger.info("--- Update Complete ---");
    }
}
