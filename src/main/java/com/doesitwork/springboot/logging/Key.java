package com.doesitwork.springboot.logging;

public enum Key {
    ID("id");

    private final String key;

    Key(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
