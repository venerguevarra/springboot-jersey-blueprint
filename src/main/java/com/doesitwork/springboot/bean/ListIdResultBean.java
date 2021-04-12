package com.doesitwork.springboot.bean;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(includeFieldNames = true)
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class ListIdResultBean {
    private List<UUID> ids;
}
