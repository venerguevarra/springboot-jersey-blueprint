package com.doesitwork.springboot.bean;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(includeFieldNames = true)
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdResultBean {
    private String id;

    private IdResultBean(String id) {
        this.id = id;
    }

    public static IdResultBean build(UUID id) {
        return new IdResultBean(id.toString());
    }
}
