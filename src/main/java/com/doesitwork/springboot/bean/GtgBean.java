package com.doesitwork.springboot.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(includeFieldNames = true)
@Setter
@Getter
public class GtgBean {
    public static final String OK = "ok";
    public static final String NOT_OK = "not_ok";

    private final String environment;
    private final String status;
    private final String version;
    private final String component;

    public GtgBean(String environment, String status, String version, String component) {
        this.environment = environment;
        this.status = status;
        this.version = version;
        this.component = component;
    }
}
