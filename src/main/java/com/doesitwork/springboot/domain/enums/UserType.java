package com.doesitwork.springboot.domain.enums;

import java.util.Arrays;
import java.util.Optional;

public enum UserType {

    USER("USER");

    private String name;

    UserType(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }

    public static Optional<UserType> enumValue(String value) {
        return Arrays.stream(values()).filter(bl -> bl.value().equalsIgnoreCase(value)).findFirst();
    }
}
