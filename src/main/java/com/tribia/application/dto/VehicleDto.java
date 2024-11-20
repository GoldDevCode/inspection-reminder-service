package com.tribia.application.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class VehicleDto {
    private UUID id;
    private String licensePlate;
    private boolean subscribed;
    private LocalDate inspectionDeadline;
}
