package com.joaopenascimento.backend.dto.user;

import com.joaopenascimento.backend.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateDTO(
        @NotBlank @Size(min = 3, max = 100) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password,
        @NotNull UserRole role
) {}