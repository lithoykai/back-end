package com.joaopenascimento.backend.model.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("admin"),
    CORRETOR("corretor"),
    CLIENTE("cliente");

    private String role;

    UserRole(String role) {
        this.role = role;
    }
}
