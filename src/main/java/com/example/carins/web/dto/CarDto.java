package com.example.carins.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CarDto(
        Long id,

        @NotBlank(message = "VIN is required")
        @Size(min = 5, max = 50)
        String vin,

        @NotBlank
        String make,

        @NotBlank
        String model,

        @Min(1950)
        int year,

        Long ownerId,

        String ownerName,

        String ownerEmail) {}
