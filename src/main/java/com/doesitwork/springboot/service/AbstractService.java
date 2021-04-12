package com.doesitwork.springboot.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.doesitwork.springboot.bean.FindPageableBean;
import com.doesitwork.springboot.bean.ListResultBean;
import com.doesitwork.springboot.bean.PagedResultBean;
import com.doesitwork.springboot.bean.SearchCriteriaBean;
import com.doesitwork.springboot.bean.SearchCriteriaCondition;
import com.doesitwork.springboot.bean.SearchCriteriaLogical;
import com.doesitwork.springboot.bean.SearchCriteriaType;
import com.doesitwork.springboot.domain.AbstractEntity;
import com.doesitwork.springboot.domain.enums.EntityStatus;
import com.doesitwork.springboot.exception.EntityConflictException;
import com.doesitwork.springboot.exception.EntityNotFoundException;
import com.doesitwork.springboot.exception.ServiceException;
import com.doesitwork.springboot.logging.Operation;
import com.doesitwork.springboot.repository.AbstractRepository;
import com.google.common.base.Preconditions;


@Service
public abstract class AbstractService<T extends AbstractEntity, ID> {

    private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public List<T> findAllActive() {
        final Operation operation = Operation.operation("findAllActive").with("entity", getEntityName()).started(this);

        try {
            final Query findAllActiveQuery = entityManager.createQuery(String .format("SELECT e FROM %s e WHERE e.active = 1 ORDER BY e.createdDate DESC", getEntityName()));

            List<T> activeEntityList = findAllActiveQuery.getResultList();
            if(Objects.isNull(activeEntityList)) {
                activeEntityList =  new ArrayList<>();
            }

            operation.wasSuccessful().yielding("entity", getEntityName()).log();
            return activeEntityList;

        } catch (NoResultException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw EntityNotFoundException.instance("no_entity_result_found_by_status: active");

        } catch (Exception e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw ServiceException.instance("failed_to_get_entity_by_status: active");

        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public synchronized PagedResultBean<T> findAll(Pageable pageable, List<SearchCriteriaBean> criteriaList) {
        final Operation operation = Operation.operation("findAll(pageable, criteria)").with("entity", getEntityName()).started(this);

        try {
            StringBuilder queryStringBuilder = new StringBuilder();
            queryStringBuilder.append(String .format("SELECT e FROM %s e", getEntityName()));

            for(int index=0; index < criteriaList.size(); index++) {
                final SearchCriteriaBean searchCriteriaBean = criteriaList.get(index);
         
                // check first
                if(index == 0) {
                    queryStringBuilder.append(" WHERE ");
                }

                String paramName = StringUtils.isEmpty(searchCriteriaBean.getParamName()) ? searchCriteriaBean.getName() : searchCriteriaBean.getParamName();
                if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.EQ) {
                    queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append("=:").append(paramName);

                } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.GT) {
                    queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append(">:").append(paramName);

                } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.GE) {
                    queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append(">=:").append(paramName);

                } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.LT) {
                    queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append("<:").append(paramName);

                } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.LE) {
                    queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append("<=:").append(paramName);

                } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.LIKE) {
                    queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append(" like :").append(paramName).append(" ");

                } 

                // check last
                if(criteriaList.size() > 1 && (index >= 0 && index < (criteriaList.size() - 1))) {
                    if(searchCriteriaBean.getLogical() == null || searchCriteriaBean.getLogical() == SearchCriteriaLogical.AND) {
                        queryStringBuilder.append(" AND ");
                    } else {
                        queryStringBuilder.append(" OR ");
                    }
                    
                }
            }
            queryStringBuilder.append(" ORDER BY createdDate DESC");

            final Query findAllActiveQuery = entityManager.createQuery(queryStringBuilder.toString())
                                                          .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                                                          .setMaxResults(pageable.getPageSize());

            for(SearchCriteriaBean searchCriteriaBean: criteriaList) {

                String paramName = StringUtils.isEmpty(searchCriteriaBean.getParamName()) ? searchCriteriaBean.getName() : searchCriteriaBean.getParamName();

                if(searchCriteriaBean.getType() == SearchCriteriaType.STRING) {
                    String stringValue = searchCriteriaBean.getOperator() == SearchCriteriaCondition.LIKE ? "%" + searchCriteriaBean.getValue() + "%": searchCriteriaBean.getValue();
                    findAllActiveQuery.setParameter(paramName, stringValue);

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.DECIMAL) {
                    findAllActiveQuery.setParameter(paramName, new BigDecimal(searchCriteriaBean.getValue()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.DATE) {
                    findAllActiveQuery.setParameter(paramName, new java.sql.Date(DATE_FORMATTER.parse(searchCriteriaBean.getValue()).getTime()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.INTEGER) {
                    findAllActiveQuery.setParameter(paramName, Integer.parseInt(searchCriteriaBean.getValue()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.FLOAT) {
                    findAllActiveQuery.setParameter(paramName, Float.parseFloat(searchCriteriaBean.getValue()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.DOUBLE) {
                    findAllActiveQuery.setParameter(paramName, Double.parseDouble(searchCriteriaBean.getValue()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.ENUM) {
                    enumPredicates(findAllActiveQuery, searchCriteriaBean, paramName);

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.UUID) {
                    findAllActiveQuery.setParameter(paramName, UUID.fromString(searchCriteriaBean.getValue()));

                }

            }

            List<T> activeEntityList = findAllActiveQuery.getResultList();
            if(Objects.isNull(activeEntityList)) {
                activeEntityList =  new ArrayList<>();
            }

            TypedQuery<T> findAllActiveCountQuery = entityManager.createQuery(queryStringBuilder.toString(), getTypeClass());

            for(SearchCriteriaBean searchCriteriaBean: criteriaList) {
                String paramName = StringUtils.isEmpty(searchCriteriaBean.getParamName()) ? searchCriteriaBean.getName() : searchCriteriaBean.getParamName();
                if(searchCriteriaBean.getType() == SearchCriteriaType.STRING) {
                    String stringValue = searchCriteriaBean.getOperator() == SearchCriteriaCondition.LIKE ? "%" + searchCriteriaBean.getValue() + "%": searchCriteriaBean.getValue();
                    findAllActiveCountQuery.setParameter(paramName, stringValue);

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.DECIMAL) {
                    findAllActiveCountQuery.setParameter(paramName, new BigDecimal(searchCriteriaBean.getValue()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.DATE) {
                    findAllActiveCountQuery.setParameter(paramName, new java.sql.Date(DATE_FORMATTER.parse(searchCriteriaBean.getValue()).getTime()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.INTEGER) {
                    findAllActiveCountQuery.setParameter(paramName, Integer.parseInt(searchCriteriaBean.getValue()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.FLOAT) {
                    findAllActiveCountQuery.setParameter(paramName, Float.parseFloat(searchCriteriaBean.getValue()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.DOUBLE) {
                    findAllActiveCountQuery.setParameter(paramName, Double.parseDouble(searchCriteriaBean.getValue()));

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.ENUM) {
                    enumPredicates(findAllActiveCountQuery, searchCriteriaBean, paramName);

                } else if(searchCriteriaBean.getType() == SearchCriteriaType.UUID) {
                    findAllActiveCountQuery.setParameter(paramName, UUID.fromString(searchCriteriaBean.getValue()));

                }
            }

            long countResult = findAllActiveCountQuery.getResultList().size();

            int totalPages = 0;
            if(activeEntityList.size() > 0) {
                totalPages =  (int) (Math.ceil(countResult) / pageable.getPageSize() + 1);
            }
            final PagedResultBean<T> pageResultBean = new PagedResultBean<T>(pageable.getPageNumber(), pageable.getPageSize(), countResult, activeEntityList, totalPages);

            operation.wasSuccessful().yielding("entity", getEntityName()).log();
            return pageResultBean;

        } catch (NoResultException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw EntityNotFoundException.instance("no_entity_result");

        } catch (Exception e) {
            e.printStackTrace();
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw ServiceException.instance("failed_to_get_entity");

        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public ListResultBean<T> findAll(FindPageableBean bean) {
        final Operation operation = Operation.operation("findAll(pageable, criteria)").with("entity", getEntityName()).started(this);

        try {
            StringBuilder queryStringBuilder = new StringBuilder();
            queryStringBuilder.append(String .format("SELECT e FROM %s e", getEntityName()));

            if(Objects.nonNull(bean) && Objects.nonNull(bean.getCriteria()) && bean.getCriteria().size() > 0) {
                List<SearchCriteriaBean> criteriaList =  bean.getCriteria();
                for(int index=0; index < criteriaList.size(); index++) {
                    final SearchCriteriaBean searchCriteriaBean = criteriaList.get(index);
             
                    // check first
                    if(index == 0) {
                        queryStringBuilder.append(" WHERE ");
                    }

                    String paramName = StringUtils.isEmpty(searchCriteriaBean.getParamName()) ? searchCriteriaBean.getName() : searchCriteriaBean.getParamName();
                    if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.EQ) {
                        queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append("=:").append(paramName);

                    } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.GT) {
                        queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append(">:").append(paramName);

                    } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.GE) {
                        queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append(">=:").append(paramName);

                    } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.LT) {
                        queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append("<:").append(paramName);

                    } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.LE) {
                        queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append("<=:").append(paramName);

                    } else if (searchCriteriaBean.getOperator() == SearchCriteriaCondition.LIKE) {
                        queryStringBuilder.append("e.").append(searchCriteriaBean.getName()).append(" like :").append(paramName).append(" ");

                    } 

                    // check last
                    if(criteriaList.size() > 1 && (index >= 0 && index < (criteriaList.size() - 1))) {
                        if(searchCriteriaBean.getLogical() == null || searchCriteriaBean.getLogical() == SearchCriteriaLogical.AND) {
                            queryStringBuilder.append(" AND ");
                        } else {
                            queryStringBuilder.append(" OR ");
                        }
                        
                    }
                }
            }
            queryStringBuilder.append(" ORDER BY createdDate DESC");

            final Query findAllActiveQuery = entityManager.createQuery(queryStringBuilder.toString());

            if(Objects.nonNull(bean) && Objects.nonNull(bean.getCriteria()) && bean.getCriteria().size() > 0) {
                List<SearchCriteriaBean> criteriaList =  bean.getCriteria();
                for(SearchCriteriaBean searchCriteriaBean: criteriaList) {

                    String paramName = StringUtils.isEmpty(searchCriteriaBean.getParamName()) ? searchCriteriaBean.getName() : searchCriteriaBean.getParamName();

                    if(searchCriteriaBean.getType() == SearchCriteriaType.STRING) {
                        String stringValue = searchCriteriaBean.getOperator() == SearchCriteriaCondition.LIKE ? "%" + searchCriteriaBean.getValue() + "%": searchCriteriaBean.getValue();
                        findAllActiveQuery.setParameter(paramName, stringValue);

                    } else if(searchCriteriaBean.getType() == SearchCriteriaType.DECIMAL) {
                        findAllActiveQuery.setParameter(paramName, new BigDecimal(searchCriteriaBean.getValue()));

                    } else if(searchCriteriaBean.getType() == SearchCriteriaType.DATE) {
                        findAllActiveQuery.setParameter(paramName, new java.sql.Date(DATE_FORMATTER.parse(searchCriteriaBean.getValue()).getTime()));

                    } else if(searchCriteriaBean.getType() == SearchCriteriaType.INTEGER) {
                        findAllActiveQuery.setParameter(paramName, Integer.parseInt(searchCriteriaBean.getValue()));

                    } else if(searchCriteriaBean.getType() == SearchCriteriaType.FLOAT) {
                        findAllActiveQuery.setParameter(paramName, Float.parseFloat(searchCriteriaBean.getValue()));

                    } else if(searchCriteriaBean.getType() == SearchCriteriaType.DOUBLE) {
                        findAllActiveQuery.setParameter(paramName, Double.parseDouble(searchCriteriaBean.getValue()));

                    } else if(searchCriteriaBean.getType() == SearchCriteriaType.ENUM) {
                        enumPredicates(findAllActiveQuery, searchCriteriaBean, paramName);

                    } else if(searchCriteriaBean.getType() == SearchCriteriaType.UUID) {
                        findAllActiveQuery.setParameter(paramName, UUID.fromString(searchCriteriaBean.getValue()));

                    }
                }
            }

            List<T> activeEntityList = findAllActiveQuery.getResultList();
            if(Objects.isNull(activeEntityList)) {
                activeEntityList =  new ArrayList<>();
            }

            
            final ListResultBean<T> resuListResultBean = new ListResultBean<T>(activeEntityList);

            operation.wasSuccessful().yielding("entity", getEntityName()).log();
            return resuListResultBean;

        } catch (NoResultException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw EntityNotFoundException.instance("no_entity_result");

        } catch (Exception e) {
            e.printStackTrace();
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw ServiceException.instance("failed_to_get_entity");

        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public PagedResultBean<T> findAllActive(Pageable pageable) {
        final Operation operation = Operation.operation("findAllActive(pageable)").with("entity", getEntityName()).started(this);

        try {
            Query queryTotal = entityManager.createQuery(String .format("SELECT count(e.id) FROM %s e WHERE e.active = 1 ORDER BY e.createdDate DESC", getEntityName()));
            long countResult = (long)queryTotal.getSingleResult();


            List<T> activeEntityList = entityManager.createQuery(String.format("SELECT e FROM %s e WHERE e.active = 1 ORDER BY e.createdDate DESC", getEntityName()))
                                                    .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                                                    .setMaxResults(pageable.getPageSize())
                                                    .getResultList();
            if(Objects.isNull(activeEntityList)) {
                activeEntityList =  new ArrayList<>();
            }

            int totalPages = 0;
            if(activeEntityList.size() > 0) {
                totalPages =  (int) (Math.ceil(countResult) / pageable.getPageSize() + 1);
            }
            final PagedResultBean<T> pageResultBean = new PagedResultBean<T>(pageable.getPageNumber(), pageable.getPageSize(), activeEntityList.size(), activeEntityList, totalPages);

            operation.wasSuccessful().yielding("entity", getEntityName()).log();
            return pageResultBean;

        } catch (NoResultException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw EntityNotFoundException.instance("no_entity_result_found_by_status: active");

        } catch (Exception e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw ServiceException.instance("failed_to_get_entity_by_status: active");

        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public PagedResultBean<T> findAllInactive(Pageable pageable) {
        final Operation operation = Operation.operation("findAllActive").with("entity", getEntityName()).started(this);

        try {
            Query queryTotal = entityManager.createQuery(String .format("SELECT count(e.id) FROM %s e WHERE e.active = 0 ORDER BY e.createdDate DESC", getEntityName()));
            long countResult = (long)queryTotal.getSingleResult();

            final Query findAllInctiveQuery = entityManager.createQuery(String.format("SELECT e FROM %s e WHERE e.active = 0 ORDER BY e.createdDate DESC", getEntityName()))
                                                           .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                                                           .setMaxResults(pageable.getPageSize());

            List<T> activeEntityList = findAllInctiveQuery.getResultList();
            if(Objects.isNull(activeEntityList)) {
                activeEntityList =  new ArrayList<>();
            }

            int totalPages = 0;
            if(activeEntityList.size() > 0) {
                totalPages =  (int) (Math.ceil(countResult) / pageable.getPageSize());
            }
            final PagedResultBean<T> pageResultBean = new PagedResultBean<T>(pageable.getPageNumber(), pageable.getPageSize(), activeEntityList.size(), activeEntityList, totalPages);

            operation.wasSuccessful().yielding("entity", getEntityName()).log();
            return pageResultBean;

        } catch (NoResultException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw EntityNotFoundException.instance("no_entity_result_found_by_status: inactive");

        } catch (Exception e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e.getMessage()).withMessage(e).log();
            throw ServiceException.instance("failed_to_get_entity_by_status: inactive");

        }
    }

    public T save(T entity) {
        if (Objects.isNull(entity)) {
            throw new IllegalArgumentException(String.format("%s_cannot_be_null", getEntityName()));

        }

        final Operation operation = Operation.operation("save").with("entity", getEntityName()).started(getServiceClass());
        try {
            entity.setActive(EntityStatus.ACTIVE);
            final T persistedEntity = getRepository().saveAndFlush(entity);
            operation.wasSuccessful().yielding("id", persistedEntity.getId()).yielding("entity", persistedEntity).log();

            return persistedEntity;

        } catch (EntityConflictException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityConflictException.instance(e.getMessage());

        } catch (EntityNotFoundException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityNotFoundException.instance(e.getMessage());

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityConflictException.instance(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    @Transactional
    public void saveAll(List<T> entities) {
        if (Objects.isNull(entities)) {
            throw new IllegalArgumentException(String.format("%s_cannot_be_null", getEntityName()));

        }

        final Operation operation = Operation.operation("save").with("entity", getEntityName()).started(getServiceClass());
        try {
            for(T entity: entities) {
                entity.setActive(EntityStatus.ACTIVE);
                final T persistedEntity = getRepository().saveAndFlush(entity);
                operation.wasSuccessful().yielding("id", persistedEntity.getId()).yielding("entity", persistedEntity).log();
            }
        } catch (EntityConflictException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityConflictException.instance(e.getMessage());

        } catch (EntityNotFoundException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityNotFoundException.instance(e.getMessage());

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityConflictException.instance(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    public T update(final UUID id, final T entity) {
        if (Objects.isNull(entity)) {
            throw new IllegalArgumentException(String.format("cannot_be_null: %s", getEntityName()));

        }

        final Operation operation = Operation.operation("update").with("entity", getEntityName()).started(getServiceClass());
        try {
            final Optional<T> existingEntity = getRepository().findById(id);
            if (existingEntity.isEmpty()) {
                throw new EntityNotFoundException(String.format("entity_not_found: %s", entity.getId().toString()));

            }

            T entityToPersist = dataTransfer(existingEntity.get(), entity);
            entityToPersist.setModifiedDate(new Date());
            final T persistedEntity = getRepository().saveAndFlush(entityToPersist);

            operation.wasSuccessful().yielding("id", persistedEntity.getId()).yielding("entity", persistedEntity).log();

            return persistedEntity;

        } catch (EntityConflictException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityConflictException.instance(e.getMessage());

        } catch (DataIntegrityViolationException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityConflictException.instance(e.getMessage());

        } catch (EntityNotFoundException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withDetail("id", entity.getId().toString()).log();
            throw e;

        } catch (Exception e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    public T delete(final UUID id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException(String.format("cannot_be_null: %s", getEntityName()));

        }

        final Operation operation = Operation.operation("delete").with("entity", getEntityName()).started(getServiceClass());
        try {
            final Optional<T> existingEntity = getRepository().findById(id);
            if (existingEntity.isEmpty()) {
                throw new EntityNotFoundException(String.format("entity_not_found: %s", id));

            }

            T persistedEntity = existingEntity.get();
            getRepository().delete(persistedEntity);

            operation.wasSuccessful().yielding("id", persistedEntity.getId()).yielding("entity", persistedEntity).log();

            return persistedEntity;

        } catch (DataIntegrityViolationException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw EntityConflictException.instance(e.getMessage());

        } catch (EntityNotFoundException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withDetail("id", id).log();
            throw ServiceException.instance(e.getMessage());

        } catch (Exception e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    public List<T> findAll() {
        final Operation operation = Operation.operation("findAll").with("entity", getEntityName()).started(getServiceClass());

        try {
            final List<T> entityList = getRepository().findAll(Sort.by("createdDate"));
            operation.wasSuccessful().log();

            return entityList;

        } catch (Exception e) {
            operation.wasFailure().log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    public Page<T> findAll(int page, int size) {
        final Operation operation = Operation.operation("findAll").with("entity", getEntityName()).started(getServiceClass());

        try {
            final Page<T> entityList = getRepository().findAll(PageRequest.of(page, size, Sort.by("createdDate").descending()));
            operation.wasSuccessful().log();

            return entityList;

        } catch (Exception e) {
            e.printStackTrace();
            operation.wasFailure().log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    public T findById(UUID id) {
        Operation operation = Operation.operation("findById").with("id", id).with("entity", getEntityName()).started(getServiceClass());
        try {
            final Optional<T> entity = getRepository().findById(id);
            if (entity.isEmpty()) {
                throw EntityNotFoundException.instance(String.format("entity_not_found: %s", id.toString()));
            }
            operation.wasSuccessful().yielding("id", id).log();

            final T existingEntity = entity.get();
            return existingEntity;

        } catch (EntityNotFoundException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withDetail("id", id.toString()).log();
            throw e;

        } catch (Exception e) {
            operation.wasFailure().log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    public T findByIdActive(UUID id) {
        Operation operation = Operation.operation("findByIdActive").with("id", id).with("entity", getEntityName()).started(getServiceClass());
        try {
            T persistedEntity = null;

            final Optional<T> entity = getRepository().findById(id);
            if (entity.isPresent() && entity.get().getActive() == EntityStatus.ACTIVE) {
                persistedEntity = entity.get();
            }

            return persistedEntity;

        } catch (Exception e) {
            operation.wasFailure().log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    public T activate(final UUID id, final UUID modifiedByUserId) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException(String.format("cannot_be_null: %s", String.valueOf(id)));
        }

        final Operation operation = Operation.operation("activate").with("entity", getEntityName()).started(getServiceClass());
        try {
            final Optional<T> existingEntity = getRepository().findById(id);
            if (existingEntity.isEmpty()) {
                throw new EntityNotFoundException(String.format("entity_not_found: %s", id.toString()));
            }

            final T entity = existingEntity.get();
            entity.setModifiedBy(modifiedByUserId);
            entity.setModifiedDate(new Date());
            entity.setActive(EntityStatus.ACTIVE);

            final T persistedEntity = getRepository().saveAndFlush(entity);
            operation.wasSuccessful().yielding("id", persistedEntity.getId()).yielding("entity", persistedEntity).log();

            return persistedEntity;

        } catch (EntityNotFoundException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withDetail("id", id.toString()).log();
            throw e;

        } catch (Exception e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    public T deactivate(final UUID id, final UUID modifiedByUserId) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException(String.format("cannot_be_null: %s", String.valueOf(id)));
        }

        final Operation operation = Operation.operation("deactivate").with("entity", getEntityName()).started(getServiceClass());
        try {
            final Optional<T> existingEntity = getRepository().findById(id);
            if (existingEntity.isEmpty()) {
                throw new EntityNotFoundException(String.format("entity_not_found: %s", id.toString()));
            }

            final T entity = existingEntity.get();
            entity.setModifiedBy(modifiedByUserId);
            entity.setModifiedDate(new Date());
            entity.setActive(EntityStatus.INACTIVE);

            final T persistedEntity = getRepository().saveAndFlush(entity);
            operation.wasSuccessful().yielding("id", persistedEntity.getId()).yielding("entity", persistedEntity).log();

            return persistedEntity;

        } catch (EntityNotFoundException e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withDetail("id", id.toString()).log();
            throw e;

        } catch (Exception e) {
            operation.wasFailure().withDetail("entity", getEntityName()).withMessage(e).log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    protected T dataTransfer(T existingEntity, T entityRequest) {
        copyNonNullProperties(entityRequest, existingEntity);
        return existingEntity;
    }

    protected void copyNonNullProperties(Object source, Object destination) {
        BeanUtils.copyProperties(source, destination, getNullPropertyNames(source));
    }

    private String getEntityName() {
        return getTypeClass().getName();
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public T preCreateOrUpdate(T entity, boolean create) {
        Preconditions.checkNotNull(entity, String.format("%s_cannot_be_null", getTypeClass().toString().toLowerCase()));
        return entity;

    }

    public void deleteAll() {
        final Operation operation = Operation.operation("deleteAll").started(this);

        try {
            getRepository().deleteAll();
            operation.wasSuccessful().log();

        } catch (Exception e) {
            operation.wasFailure().withMessage(e.getMessage()).withMessage(e).log();
            throw ServiceException.instance(e.getMessage());

        }
    }

    protected abstract Class<T> getTypeClass();

    protected abstract AbstractRepository<T, UUID> getRepository();

    protected abstract Object getServiceClass();

    protected void enumPredicates(Query typedQuery, SearchCriteriaBean searchCriteriaBean, String paramName) {
        if (searchCriteriaBean.getName().equals("active")) {
            typedQuery.setParameter(paramName, EntityStatus.valueOf(searchCriteriaBean.getValue()));
        }
    }
}
