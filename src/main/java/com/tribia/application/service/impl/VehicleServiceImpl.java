package com.tribia.application.service.impl;

import com.tribia.application.client.SVVApiClient;
import com.tribia.application.dto.svv.response.SVVApiResponse;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.repository.VehicleRepository;
import com.tribia.application.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final SVVApiClient svvApiClient;


    public List<Vehicle> findByLicensePlates(List<String> licensePlates) {
        return vehicleRepository.findByLicensePlateIn(licensePlates);
    }

    public Vehicle createVehicle(String licensePlate) {
        SVVApiResponse vehicleData = svvApiClient.getVehicleData(licensePlate);
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setInspectionDeadline(LocalDate
                .parse(vehicleData
                        .getKjoretoydataListe()
                        .getFirst()
                        .getPeriodiskKjoretoyKontroll()
                        .getKontrollfrist()));
        vehicle.setSubscribed(true);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> saveAll(List<Vehicle> vehicles) {
        return vehicleRepository.saveAll(vehicles);
    }
}
