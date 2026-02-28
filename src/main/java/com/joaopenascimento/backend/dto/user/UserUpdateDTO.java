package com.joaopenascimento.backend.dto.user;

import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(min = 3, max = 100) String name,
        @Size(min = 6) String password
        ) {}
