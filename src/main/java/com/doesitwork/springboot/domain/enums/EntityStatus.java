package com.doesitwork.springboot.domain.enums;

import java.util.Arrays;
import java.util.Optional;

public enum EntityStatus {
    INACTIVE("INACTIVE"),
    ACTIVE("ACTIVE");

    private String name;

    EntityStatus(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public static Optional<EntityStatus> enumValue(String value) {
        return Arrays.stream(values()).filter(bl -> bl.value().equalsIgnoreCase(value)).findFirst();
    }
}
