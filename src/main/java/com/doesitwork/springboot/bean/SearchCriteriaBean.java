package com.doesitwork.springboot.bean;

import java.io.Serializable;

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
@ToString(includeFieldNames = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaBean implements Serializable {

    private static final long serialVersionUID = 2014686148874592548L;
    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    @JsonProperty("paramName")
    private String paramName;

    @JsonProperty("operator")
    private SearchCriteriaCondition operator;

    @JsonProperty("type")
    private SearchCriteriaType type;

    @JsonProperty("logical")
    private SearchCriteriaLogical logical;
}