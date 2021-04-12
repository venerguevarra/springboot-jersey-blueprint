package com.doesitwork.springboot.domain;

import java.math.BigInteger;

import javax.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
public class SequenceGenerator extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    private String prefix;

    private BigInteger lastSequenceNumber;

    private String generatedSequence;

    private BigInteger numberOfZeroPadding;

    public void setGeneratedSequence(String prefix, BigInteger sequenceNumber, BigInteger numberOfZeroPadding) {
        this.generatedSequence = prefix.concat(padLeftZeros(sequenceNumber.toString(), numberOfZeroPadding.intValue()));
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
