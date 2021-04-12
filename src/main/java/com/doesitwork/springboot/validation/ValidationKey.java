package com.doesitwork.springboot.validation;

public interface ValidationKey {

    String EMAIL = "validation.email";
    String REQUIRED = "validation.required";
    String LENGTH = "validation.length";
    String MINIMUM_LENGTH = "validation.length.min";
    String MAXIMUM_LENGTH = "validation.length.max";
    String MINIMUM_NUMBER = "validation.number.min";
    String MAXIMUM_NUMBER = "validation.number.max";
    String URL = "validation.url";
    String SENDER_ID = "validation.sender.id";

}
