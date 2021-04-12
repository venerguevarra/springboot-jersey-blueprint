package com.doesitwork.springboot.bean;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;

import com.doesitwork.springboot.validation.ValidationKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class FindPageableBean {
    @QueryParam("size")
    @NotNull(message = ValidationKey.REQUIRED)
    private Integer size;

    @QueryParam("page")
    @NotNull(message = ValidationKey.REQUIRED)
    private Integer page;

    @Valid
    @NotNull(message = ValidationKey.REQUIRED)
    @JsonProperty("criteria")
    private List<SearchCriteriaBean> criteria;
}
