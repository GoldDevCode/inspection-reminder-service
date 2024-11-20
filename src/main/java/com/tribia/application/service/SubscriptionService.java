package com.tribia.application.service;

import com.tribia.application.dto.UserDto;
import com.tribia.application.dto.UserSubscriptionRequest;

import java.util.List;

public interface SubscriptionService {
    UserDto subscribeUser(UserSubscriptionRequest request);

    UserDto unsubscribeVehicles(String email, List<String> licensePlates);
}
