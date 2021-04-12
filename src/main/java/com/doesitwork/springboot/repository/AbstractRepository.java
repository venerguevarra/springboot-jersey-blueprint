package com.doesitwork.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doesitwork.springboot.domain.AbstractEntity;

@Repository
public interface AbstractRepository<T extends AbstractEntity, ID> extends JpaRepository<T, ID> {

}
