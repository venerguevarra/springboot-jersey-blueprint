package com.doesitwork.springboot.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class LocalDateTimeProvider implements ParamConverterProvider {

    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (genericType.equals(LocalDateTime.class)) {
            return (ParamConverter<T>)new DateTimeParamConverter();
        } else {
            return null;
        }
    }

    static class DateTimeParamConverter implements ParamConverter<LocalDateTime> {

        public LocalDateTime fromString(String value) {
            if (value == null) {
                return null;
            }
            try {
                return DateUtil.toLocalDateTime(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String toString(LocalDateTime value) {
            return DateUtil.toLocalDateTimeString(value);
        }

    }
}
