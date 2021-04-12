package com.doesitwork.springboot.validation;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(includeFieldNames = true)
public class ValidationErrorResponse implements Serializable {

    private static final long serialVersionUID = 6351008724569667177L;

    @JsonProperty("errors")
    private final Set<ValidationMessage> errors;

    public ValidationErrorResponse(Set<ValidationMessage> errors) {
        this.errors = errors;
    }

}
