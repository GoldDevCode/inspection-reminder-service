package com.tribia.application.service;

import com.tribia.application.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    List<Vehicle> findByLicensePlates(List<String> licensePlates);

    Vehicle createVehicle(String licensePlate);

    List<Vehicle> saveAll(List<Vehicle> vehicles);
}
