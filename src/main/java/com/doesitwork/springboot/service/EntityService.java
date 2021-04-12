package com.doesitwork.springboot.service;

import static java.util.Objects.isNull;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.doesitwork.springboot.exception.EntityNotFoundException;
import com.doesitwork.springboot.exception.ServiceException;
import com.doesitwork.springboot.logging.Operation;

@Service
public class EntityService {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public <T> T getByName(String entity, String name) {
        final Operation operation = Operation.operation("getByName").with("entity", entity).with("name", name).started(this);

        try {
            final Query findByNameQuery = entityManager.createQuery(String.format("SELECT e FROM %s e WHERE e.name = :name", WordUtils.capitalizeFully(entity)));
            findByNameQuery.setParameter("name", name);

            T existingEntity = (T) findByNameQuery.getSingleResult();
            if(isNull(existingEntity)) {
                throw EntityNotFoundException.instance("no_entity_found");
            }

            operation.wasSuccessful().yielding("entity", entity).yielding("name", name).log();
            return existingEntity;

        } catch (EntityNotFoundException | NoResultException e) {
            operation.wasFailure().withDetail("entity", entity).withDetail("name", name).withMessage(e.getMessage()).withMessage(e).log();
            throw EntityNotFoundException.instance(String.format("no_entity_found_by_name: %s", name));

        } catch (Exception e) {
            operation.wasFailure().withDetail("entity", entity).withDetail("name", name).withMessage(e.getMessage()).withMessage(e).log();
            throw ServiceException.instance(String.format("failed_to_get_entity_by_name: %s", name));

        }
    }

}
