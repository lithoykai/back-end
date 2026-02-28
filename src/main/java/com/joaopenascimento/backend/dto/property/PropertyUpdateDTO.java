package com.joaopenascimento.backend.dto.property;

import com.joaopenascimento.backend.model.enums.PropertyType;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PropertyUpdateDTO(
     @Size(min = 10, max = 100) String name,
     String description,
     PropertyType type,
     @Positive Double value,
     @Positive Integer area,
     @Positive Integer bedrooms,
     String address,
     String city,
     String state,
     Long brokerId
) {}
