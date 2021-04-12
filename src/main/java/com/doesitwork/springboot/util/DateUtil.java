package com.doesitwork.springboot.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final SimpleDateFormat ISO_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static LocalDate toLocalDate(String value) {
        return LocalDate.parse(value, DATE_FORMATTER);
    }

    public static LocalDateTime toLocalDateTime(String value) {
        return LocalDateTime.parse(value, ISO_DATETIME_FORMATTER);
    }

    public static String toLocalDateString(Date date) {
        return ISO_DATE_FORMATTER.format(date);
    }

    public static String toLocalDateString(LocalDate localDate) {
        return DATE_FORMATTER.format(localDate);
    }

    public static String toLocalDateTimeString(LocalDateTime localDateTime) {
        return ISO_DATETIME_FORMATTER.format(localDateTime);
    }

    public static Date toDateStartOfDay(String dateTo) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTo, ISO_DATETIME_FORMATTER);
        ZonedDateTime zdt = localDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault());
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    public static Date toDateEndOfDay(String dateTo) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTo, ISO_DATETIME_FORMATTER);
        ZonedDateTime zdt = localDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusSeconds(1);
        Date date = Date.from(zdt.toInstant());
        return date;
    }
}
