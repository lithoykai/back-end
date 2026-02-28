package com.joaopenascimento.backend.dto.property;

import com.joaopenascimento.backend.model.enums.PropertyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PropertyCreateDTO(
        @NotBlank @Size(min = 10, max = 100) String name,
        @NotBlank String description,
        @NotNull PropertyType type,
        @NotNull @Positive Double value,
        @NotNull @Positive Integer area,
        @NotNull @Positive Integer bedrooms,
        @NotBlank String address,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String imageUrls
) {}