package com.tribia.application.validatortests;

import com.tribia.application.entity.User;
import com.tribia.application.entity.Vehicle;
import com.tribia.application.exception.VehicleAlreadySubscribedException;
import com.tribia.application.validator.SubscriptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionValidatorTest {

    private SubscriptionValidator subscriptionValidator;
    private User user;
    private Vehicle vehicle1;
    private Vehicle vehicle2;

    @BeforeEach
    void setUp() {
        subscriptionValidator = new SubscriptionValidator();

        user = new User();
        user.setEmail("test@example.com");
        vehicle1 = new Vehicle();
        vehicle1.setLicensePlate("EV12345");
        vehicle1.setSubscribed(true);
        vehicle1.setUser(user);

        vehicle2 = new Vehicle();
        vehicle2.setLicensePlate("EV67890");
        vehicle2.setSubscribed(false);
        vehicle2.setUser(user);

        user.setVehicles(Set.of(vehicle1, vehicle2));
    }

    @Test
    void filterNewLicensePlates_shouldReturnNewPlates() {
        List<String> requestedPlates = Arrays.asList("EV12345", "EV11111");
        List<String> newPlates = subscriptionValidator.filterNewLicensePlates(user, requestedPlates);

        assertEquals(Collections.singletonList("EV11111"), newPlates);
    }

    @Test
    void filterLicensePlatesToUnsubscribe_shouldReturnSubscribedPlates() {
        List<String> requestedPlates = Arrays.asList("EV12345", "EV67890");
        List<String> platesToUnsubscribe = subscriptionValidator.filterLicensePlatesToUnsubscribe(user, requestedPlates);

        assertEquals(Collections.singletonList("EV12345"), platesToUnsubscribe);
    }


    @Test
    void validateVehicleSubscription_shouldThrowExceptionWhenVehicleSubscribedByAnotherUser() {
        User anotherUser = new User();
        anotherUser.setEmail("test1@example.com");
        vehicle1.setUser(anotherUser);
        anotherUser.setVehicles(Set.of(vehicle1));

        assertThrows(VehicleAlreadySubscribedException.class, () -> {
            subscriptionValidator.validateVehicleSubscription(vehicle1, user);
        });
    }

    @Test
    void validateVehicleSubscription_shouldNotThrowExceptionWhenVehicleSubscribedBySameUser() {
        assertDoesNotThrow(() -> {
            subscriptionValidator.validateVehicleSubscription(vehicle1, user);
        });
    }

    @Test
    void validateVehicleSubscription_shouldNotThrowExceptionWhenVehicleNotSubscribed() {
        vehicle1.setUser(null);

        assertDoesNotThrow(() -> {
            subscriptionValidator.validateVehicleSubscription(vehicle1, user);
        });
    }
}
