package com.joaopenascimento.backend.dto.user;

import com.joaopenascimento.backend.model.User;
import com.joaopenascimento.backend.model.enums.UserRole;

public record UserDTO(Long id, String name, String email, UserRole role) {
    public UserDTO(User entity) {
        this(entity.getId(), entity.getName(), entity.getEmail(), entity.getRole());
    }
}