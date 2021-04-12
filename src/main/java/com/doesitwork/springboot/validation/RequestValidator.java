package com.doesitwork.springboot.validation;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {
    private final static String MESSAGE_ATTRIBUTE = "message";
    private final static String MAX_ATTRIBUTE = "max";
    private final static String MIN_ATTRIBUTE = "min";

    private final ReloadableResourceBundleMessageSource messageSource;
    private Set<ValidationMessage> errorMessages;

    public RequestValidator(ReloadableResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public <T> void validate(final T objectToValidate) {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator validator = validatorFactory.getValidator();

        final Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);
        errorMessages = new HashSet<ValidationMessage>();

        for (ConstraintViolation<T> violation : violations) {
            final String messageCode = violation.getMessage();
            final String property = violation.getPropertyPath().toString();
            final String propertyValue = String.valueOf(violation.getInvalidValue());

            if (getConstraintAttribute(violation, MESSAGE_ATTRIBUTE).equals(ValidationKey.LENGTH)) {
                errorMessages.add(new ValidationMessage(messageSource.getMessage(messageCode,
                                                                                 new Object[] { 
                                                                                   property, 
                                                                                   getConstraintAttribute(violation, MIN_ATTRIBUTE),
                                                                                   getConstraintAttribute(violation, MAX_ATTRIBUTE) 
                                                                                 },
                                                                                 Locale.US),
                                                                                 property,
                                                                                 propertyValue));
            } else if (getConstraintAttribute(violation, MESSAGE_ATTRIBUTE).equals(ValidationKey.MINIMUM_LENGTH)) {
                errorMessages.add(new ValidationMessage(messageSource.getMessage(messageCode,
                                                                                 new Object[] { getConstraintAttribute(violation, MIN_ATTRIBUTE) },
                                                                                 Locale.US),
                                                                                 property,
                                                                                 propertyValue));
            } else if (getConstraintAttribute(violation, MESSAGE_ATTRIBUTE).equals(ValidationKey.MAXIMUM_LENGTH)) {
                errorMessages.add(new ValidationMessage(messageSource.getMessage(messageCode,
                                                                                 new Object[] { getConstraintAttribute(violation, MAX_ATTRIBUTE) },
                                                                                 Locale.US),
                                                                                 property,
                                                                                 propertyValue));
            } else if (getConstraintAttribute(violation, MESSAGE_ATTRIBUTE).equals(ValidationKey.MINIMUM_NUMBER)) {
                errorMessages.add(new ValidationMessage("Invalid minimum value. Field must not be more than null characters.",
                                                                                 property,
                                                                                 propertyValue));
            } else if (getConstraintAttribute(violation, MESSAGE_ATTRIBUTE).equals(ValidationKey.MAXIMUM_NUMBER)) {
                errorMessages.add(new ValidationMessage("Invalid maximum value. Field must not be more than null characters.",
                                                                                 property,
                                                                                 propertyValue));
            } else {
                errorMessages.add(new ValidationMessage(messageSource.getMessage(messageCode, 
                                                                                 new Object[] { property }, Locale.US),
                                                                                 property,
                                                                                 propertyValue));
            }
        }
    }

    private Object getConstraintAttribute(ConstraintViolation<?> violation, String key) {
        return violation.getConstraintDescriptor().getAttributes().get(key);
    }

    public Set<ValidationMessage> getErrorMessages() {
        return errorMessages;
    }

    public boolean hasErrors() {
        if (errorMessages == null || errorMessages.size() == 0) {
            return false;
        }
        return true;
    }
}
