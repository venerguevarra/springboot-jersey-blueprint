package com.doesitwork.springboot.bean;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.doesitwork.springboot.validation.ValidationKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class PageableBean {
    @QueryParam("size")
    @NotNull(message = ValidationKey.REQUIRED)
    @DefaultValue("10")
    private Integer size;

    @QueryParam("page")
    @NotNull(message = ValidationKey.REQUIRED)
    @DefaultValue("0")
    private Integer page;
}
