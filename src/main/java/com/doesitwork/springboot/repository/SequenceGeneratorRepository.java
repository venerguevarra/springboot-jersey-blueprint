package com.doesitwork.springboot.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.doesitwork.springboot.domain.SequenceGenerator;

@Repository
public interface SequenceGeneratorRepository extends AbstractRepository<SequenceGenerator, UUID> {
    SequenceGenerator  findByPrefix(String prefix);
}
