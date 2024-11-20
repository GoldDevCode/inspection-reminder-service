package com.tribia.application.mapper;

import com.tribia.application.dto.UserDto;
import com.tribia.application.dto.VehicleDto;
import com.tribia.application.entity.User;
import com.tribia.application.entity.Vehicle;

import java.util.stream.Collectors;

public class MappingHelper {

    public static UserDto mapToUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .vehicles(user.getVehicles().stream()
                        .map(MappingHelper::mapToVehicleDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private static VehicleDto mapToVehicleDto(Vehicle vehicle) {
        return VehicleDto.builder()
                .id(vehicle.getId())
                .inspectionDeadline(vehicle.getInspectionDeadline())
                .subscribed(vehicle.isSubscribed())
                .licensePlate(vehicle.getLicensePlate())
                .build();
    }
}
