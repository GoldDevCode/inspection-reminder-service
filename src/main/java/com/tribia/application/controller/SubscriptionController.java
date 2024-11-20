package com.tribia.application.controller;

import com.tribia.application.dto.ApiResponse;
import com.tribia.application.dto.SubscriptionRemovalRequest;
import com.tribia.application.dto.UserDto;
import com.tribia.application.dto.UserSubscriptionRequest;
import com.tribia.application.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<UserDto>> subscribeUser(@RequestBody @Validated UserSubscriptionRequest request) {
        logger.info("Subscribing user to vehicles: {}", request.getLicensePlates());
        return ResponseEntity
                .ok(ApiResponse
                        .success(subscriptionService
                                .subscribeUser(request)));
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<UserDto>> unsubscribeVehicles(@RequestBody @Validated SubscriptionRemovalRequest request) {
        logger.info("Unsubscribing user from vehicles: {}", request.getLicensePlates());
        UserDto userDto = subscriptionService.unsubscribeVehicles(request.getEmail(), request.getLicensePlates());
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }
}
