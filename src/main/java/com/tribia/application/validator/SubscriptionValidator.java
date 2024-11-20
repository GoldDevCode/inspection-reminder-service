package com.tribia.application.validator;

import com.tribia.application.entity.User;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.exception.VehicleAlreadySubscribedException;
import com.tribia.application.exception.VehicleNotAlreadySubscribedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    public List<String> filterNewLicensePlates(User user, List<String> requestedLicensePlates) {
        Set<String> existingLicensePlates = user.getVehicles().stream()
                .filter(Vehicle::isSubscribed)
                .map(Vehicle::getLicensePlate)
                .collect(Collectors.toSet());

        return requestedLicensePlates.stream()
                .filter(plate -> !existingLicensePlates.contains(plate))
                .collect(Collectors.toList());
    }

    public List<String> filterLicensePlatesToUnsubscribe(User user, List<String> requestedLicensePlates) {
        Set<String> unSubscribedLicensePlates = user.getVehicles().stream()
                .filter(vehicle -> !vehicle.isSubscribed())
                .map(Vehicle::getLicensePlate)
                .collect(Collectors.toSet());

        return requestedLicensePlates.stream()
                .filter(plate -> !unSubscribedLicensePlates.contains(plate))
                .collect(Collectors.toList());
    }

    public void validateVehicleSubscription(Vehicle vehicle, User user) {
        if (vehicle.getUser() != null && !vehicle.getUser().equals(user)) {
            throw new VehicleAlreadySubscribedException("Vehicle with license plate " + vehicle.getLicensePlate() + " is already subscribed by another user.");
        }
    }

    public void validateVehicleUnsubscription(User user, List<String> licensePlates) {
        Set<String> existingLicensePlates = user.getVehicles().stream()
                .map(Vehicle::getLicensePlate)
                .collect(Collectors.toSet());

        //collect all license plates that are not part of the user's vehicles in a set
        Set<String> inValidLicensePlates = licensePlates.stream()
                .filter(plate -> !existingLicensePlates.contains(plate))
                .collect(Collectors.toSet());

        //if there are any invalid license plates , throw an exception
        if (!inValidLicensePlates.isEmpty()) {
            throw new VehicleNotAlreadySubscribedException("The following license plates are not part of the user's vehicle subscription : " + inValidLicensePlates);
        }

    }
}
