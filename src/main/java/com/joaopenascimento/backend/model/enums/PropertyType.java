package com.joaopenascimento.backend.model.enums;

import lombok.Getter;

@Getter
public enum PropertyType {
    CASA("casa"),
    TERRENO("terreno"),
    APARTAMENTO("apartamento");

    private String type;

    PropertyType(String type) {
        this.type = type;
    }
}
