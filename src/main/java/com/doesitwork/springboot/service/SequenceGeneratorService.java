package com.doesitwork.springboot.service;

import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doesitwork.springboot.domain.SequenceGenerator;
import com.doesitwork.springboot.domain.enums.EntityStatus;
import com.doesitwork.springboot.domain.enums.SequencePrefix;
import com.doesitwork.springboot.exception.ServiceException;
import com.doesitwork.springboot.repository.AbstractRepository;
import com.doesitwork.springboot.repository.SequenceGeneratorRepository;

@Service
public class SequenceGeneratorService extends AbstractService<SequenceGenerator, UUID> {
    @Autowired
    private SequenceGeneratorRepository sequenceGeneratorRepository;
    
    public String generateSequenceNumber(SequencePrefix sequencePrefix, BigInteger numberOfPadding) {
        SequenceGenerator sequenceGenerator = null;

        try {
            final String prefix = sequencePrefix.val();
            sequenceGenerator = sequenceGeneratorRepository.findByPrefix(prefix);

            if (Objects.isNull(sequenceGenerator)) {
                sequenceGenerator = new SequenceGenerator();
                sequenceGenerator.setPrefix(prefix);
                sequenceGenerator.setLastSequenceNumber(BigInteger.ZERO);
                sequenceGenerator.setActive(EntityStatus.ACTIVE);
                sequenceGenerator.setGeneratedSequence(prefix, BigInteger.ZERO, numberOfPadding);
                sequenceGenerator.setNumberOfZeroPadding(numberOfPadding);
                sequenceGeneratorRepository.save(sequenceGenerator);
            }

            final BigInteger lastNumber = sequenceGenerator.getLastSequenceNumber().add(BigInteger.ONE);
            sequenceGenerator.setLastSequenceNumber(lastNumber);
            sequenceGenerator.setGeneratedSequence(prefix, lastNumber, numberOfPadding);
            sequenceGenerator.setNumberOfZeroPadding(numberOfPadding);

            sequenceGeneratorRepository.save(sequenceGenerator);

        } catch (Exception e) {
            throw ServiceException.instance("failed_to_generate_sequenceNumber");

        }

        return sequenceGenerator.getGeneratedSequence();
    }

    @Override
    protected Class<SequenceGenerator> getTypeClass() {
        return SequenceGenerator.class;
    }

    @Override
    protected AbstractRepository<SequenceGenerator, UUID> getRepository() {
        return sequenceGeneratorRepository;
    }

    @Override
    protected Object getServiceClass() {
        return this;
    }
}