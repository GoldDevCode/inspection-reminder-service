package com.tribia.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private List<VehicleDto> vehicles;
}
