package com.doesitwork.springboot.validation;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(includeFieldNames = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 684655188816540898L;

    @JsonProperty("status")
    private String status;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("responseCode")
    private int responseCode;

    public static ErrorResponse instance(String status) {
        ErrorResponse errorResponse = ErrorResponse.builder().status(status).build();
        return errorResponse;
    }

    public static ErrorResponse instance(String errorMessage, int responseCode) {
        ErrorResponse errorResponse = ErrorResponse.builder().errorMessage(errorMessage).responseCode(responseCode).build();
        return errorResponse;
    }
}

