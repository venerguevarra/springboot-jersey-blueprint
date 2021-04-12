package com.doesitwork.springboot.domain.enums;

public enum EntityName {

    USER("User");

    private String name;

    EntityName(String name) {
        this.name = name;
    }

    public String value() {
        return this.name;
    }
}
