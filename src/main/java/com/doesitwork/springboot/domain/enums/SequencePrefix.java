package com.doesitwork.springboot.domain.enums;

import java.math.BigInteger;
import java.time.LocalDate;

public enum SequencePrefix {

    USER("U");

    String prefix;

    SequencePrefix(String prefix) {
        this.prefix = prefix;
    }

    public String val() {
        final String month = padLeftZeros(BigInteger.valueOf(LocalDate.now().getMonthValue()).toString(), 2);
        final String prefixWithDate = String.format("%s%s%s", this.prefix, LocalDate.now().getYear(), month);
        return prefixWithDate;
    }

    private String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }
}