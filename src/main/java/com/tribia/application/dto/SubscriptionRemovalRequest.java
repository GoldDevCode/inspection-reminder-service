package com.tribia.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SubscriptionRemovalRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "At least one license plate number is required")
    private List<@Pattern(regexp = "^[A-Z]{2}[0-9]{5}$", message = "Invalid license plate format") String> licensePlates;

}
