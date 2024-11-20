package com.tribia.application.service.impl;

import com.tribia.application.dto.UserDto;
import com.tribia.application.dto.UserSubscriptionRequest;
import com.tribia.application.entity.User;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.exception.AlreadySubscribedException;
import com.tribia.application.exception.AlreadyUnsubscribedException;
import com.tribia.application.exception.ResourceNotFoundException;
import com.tribia.application.service.SubscriptionService;
import com.tribia.application.service.UserService;
import com.tribia.application.service.VehicleService;
import com.tribia.application.validator.SubscriptionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tribia.application.mapper.MappingHelper.mapToUserDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final SubscriptionValidator subscriptionValidator;
    Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    public UserDto subscribeUser(UserSubscriptionRequest request) {
        logger.info("Subscribing user to vehicles: {}", request.getLicensePlates());
        User user = userService.findOrCreateUser(request.getEmail());
        List<String> newLicensePlates = subscriptionValidator
                .filterNewLicensePlates(user, request.getLicensePlates());

        if (newLicensePlates.isEmpty()) {
            logger.info("User is already subscribed to all provided license plates.");
            throw new AlreadySubscribedException("User is already subscribed to all provided license plates.");
        }

        List<Vehicle> vehicles = fetchAndUpdateVehicles(newLicensePlates, user);
        associateVehiclesWithUser(user, vehicles);
        return mapToUserDto(userService.saveUser(user));
    }

    public UserDto unsubscribeVehicles(String email, List<String> licensePlates) {
        logger.info("Unsubscribing user from vehicles: {}", licensePlates);

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        subscriptionValidator.validateVehicleUnsubscription(user, licensePlates);

        List<String> licensePlatesToUnsubscribe = subscriptionValidator
                .filterLicensePlatesToUnsubscribe(user, licensePlates);

        if (licensePlatesToUnsubscribe.isEmpty()) {
            logger.info("All provided license plates are already unsubscribed.");
            throw new AlreadyUnsubscribedException("All provided license plates are already unsubscribed.");
        }

        user.getVehicles().stream()
                .filter(vehicle
                        -> licensePlatesToUnsubscribe.contains(vehicle.getLicensePlate()))
                .forEach(vehicle
                        -> vehicle.setSubscribed(false));

        return mapToUserDto(userService.saveUser(user));
    }

    private List<Vehicle> fetchAndUpdateVehicles(List<String> licensePlates, User user) {
        List<Vehicle> existingVehicles = vehicleService.findByLicensePlates(licensePlates);
        Map<String, Vehicle> existingVehicleMap = existingVehicles.stream()
                .collect(Collectors.toMap(Vehicle::getLicensePlate, v -> v));

        List<Vehicle> updatedVehicles = new ArrayList<>();

        for (String licensePlate : licensePlates) {
            Vehicle vehicle = existingVehicleMap
                    .getOrDefault(licensePlate, vehicleService.createVehicle(licensePlate));
            subscriptionValidator
                    .validateVehicleSubscription(vehicle, user);
            vehicle.setUser(user);
            vehicle.setSubscribed(true);
            updatedVehicles.add(vehicle);
        }
        return vehicleService.saveAll(updatedVehicles);
    }

    private void associateVehiclesWithUser(User user, List<Vehicle> vehicles) {
        vehicles.forEach(user::addVehicle);
    }
}
