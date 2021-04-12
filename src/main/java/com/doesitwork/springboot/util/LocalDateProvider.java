package com.doesitwork.springboot.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class LocalDateProvider implements ParamConverterProvider {

    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (genericType.equals(LocalDate.class)) {
            return (ParamConverter<T>)new DateTimeParamConverter();
        } else {
            return null;
        }
    }

    static class DateTimeParamConverter implements ParamConverter<LocalDate> {

        public LocalDate fromString(String value) {
            if (value == null) {
                return null;
            }
            try {
                return DateUtil.toLocalDate(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String toString(LocalDate value) {
            return DateUtil.toLocalDateString(value);
        }

    }
}
