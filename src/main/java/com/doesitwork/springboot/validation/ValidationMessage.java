package com.doesitwork.springboot.validation;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ValidationMessage implements Serializable {
    private static final long serialVersionUID = -287147589335488573L;

    private String message;
    private String field;
    private Object rejectedValue;

    public ValidationMessage() {
    }

    public ValidationMessage(String message, String field, Object rejectedValue) {
        this.message = message;
        this.field = field;
        this.rejectedValue = rejectedValue;
    }
}
