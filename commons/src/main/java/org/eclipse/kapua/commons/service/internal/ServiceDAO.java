/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.commons.service.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.model.AbstractKapuaUpdatableEntity;
import org.eclipse.kapua.commons.model.query.FieldSortCriteria;
import org.eclipse.kapua.commons.model.query.FieldSortCriteria.SortOrder;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.KapuaAndPredicate;
import org.eclipse.kapua.model.query.predicate.KapuaAttributePredicate;
import org.eclipse.kapua.model.query.predicate.KapuaOrPredicate;
import org.eclipse.kapua.model.query.predicate.KapuaPredicate;

/**
 * Service DAO utility methods
 * 
 * @since 1.0
 */
public class ServiceDAO {

    /**
     * Create entity utility method
     * 
     * @param em
     * @param entity
     *            to be created
     * @return
     */
    public static <E extends KapuaEntity> E create(EntityManager em, E entity) {
        //
        // Creating entity
        em.persist(entity);
        em.flush();

        return entity;
    }

    /**
     * Update entity utility method
     * 
     * @param em
     * @param clazz
     * @param entity
     *            entity to be updated
     * @return
     */
    public static <E extends KapuaUpdatableEntity> E update(EntityManager em, Class<E> clazz, E entity) {
        //
        // Checking existence
        E entityToUpdate = em.find(clazz, entity.getId());

        //
        // Updating if not null
        if (entityToUpdate != null) {
            AbstractKapuaUpdatableEntity updatableEntity = (AbstractKapuaUpdatableEntity) entity;
            updatableEntity.setCreatedOn(entityToUpdate.getCreatedOn());
            updatableEntity.setCreatedBy(entityToUpdate.getCreatedBy());
            em.merge(entity);
            em.flush();
            em.refresh(entityToUpdate);
        }

        return entityToUpdate;
    }

    /**
     * Delete entity utility method
     * 
     * @param em
     * @param clazz
     * @param entityId
     *            entity id of the entity to be deleted
     */
    public static <E extends KapuaEntity> void delete(EntityManager em, Class<E> clazz, KapuaId entityId) {
        //
        // Checking existence
        E entityToDelete = em.find(clazz, entityId);

        //
        // Deleting if not null
        if (entityToDelete != null) {
            em.remove(entityToDelete);
            em.flush();
        }
    }

    /**
     * Find entity by name utility method
     * 
     * @param em
     * @param clazz
     * @param name
     *            name of the entity to find
     * @return
     */
    public static <E extends KapuaEntity> E findByName(EntityManager em, Class<E> clazz, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> criteriaSelectQuery = cb.createQuery(clazz);

        //
        // FROM
        Root<E> userRoot = criteriaSelectQuery.from(clazz);

        //
        // SELECT
        criteriaSelectQuery.select(userRoot);

        // name
        ParameterExpression<String> pName = cb.parameter(String.class, "name");
        criteriaSelectQuery.where(cb.equal(userRoot.get("name"), pName));

        //
        // QUERY!
        TypedQuery<E> query = em.createQuery(criteriaSelectQuery);
        query.setParameter(pName.getName(), name);

        List<E> result = query.getResultList();
        E user = null;
        if (result.size() == 1) {
            user = result.get(0);
        }

        return user;
    }

    /**
     * Query entity utility method
     * 
     * @param em
     * @param interfaceClass
     *            result query interface class
     * @param implementingClass
     *            result query implementation class
     * @param resultContainer
     * @param kapuaQuery
     * @return
     * @throws KapuaException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <I extends KapuaEntity, E extends I, L extends KapuaListResult<I>> L query(EntityManager em,
            Class<I> interfaceClass,
            Class<E> implementingClass,
            L resultContainer,
            KapuaQuery<I> kapuaQuery)
            throws KapuaException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> criteriaSelectQuery = cb.createQuery(implementingClass);

        //
        // FROM
        Root<E> entityRoot = criteriaSelectQuery.from(implementingClass);
        EntityType<E> entityType = entityRoot.getModel();

        //
        // SELECT
        criteriaSelectQuery.select(entityRoot);

        //
        // WHERE
        ParameterExpression<Long> scopeIdParam = cb.parameter(Long.class);
        Expression<Boolean> scopeIdExpr = cb.equal(entityRoot.get("scopeId"), scopeIdParam);

        Map<ParameterExpression, Object> binds = new HashMap<ParameterExpression, Object>();
        binds.put(scopeIdParam, kapuaQuery.getScopeId());
        Expression<Boolean> expr = handleKapuaQueryPredicates(kapuaQuery.getPredicate(),
                binds,
                cb,
                entityRoot,
                entityRoot.getModel());

        if (expr == null) {
            criteriaSelectQuery.where(scopeIdExpr);
        } else {
            criteriaSelectQuery.where(cb.and(scopeIdExpr, expr));
        }

        //
        // ORDER BY
        if (kapuaQuery.getSortCriteria() != null) {
            FieldSortCriteria sortCriteria = (FieldSortCriteria) kapuaQuery.getSortCriteria();

            Order order;
            if (SortOrder.ASCENDING.equals(sortCriteria.getSortOrder())) {
                order = cb.asc(entityRoot.get(entityType.getSingularAttribute(sortCriteria.getAttributeName())));
            } else {
                order = cb.desc(entityRoot.get(entityType.getSingularAttribute(sortCriteria.getAttributeName())));
            }

            criteriaSelectQuery.orderBy(order);
        }

        //
        // QUERY!
        TypedQuery<E> query = em.createQuery(criteriaSelectQuery);

        // Populate query parameters
        for (ParameterExpression pe : binds.keySet()) {
            query.setParameter(pe, binds.get(pe));
        }

        // Set offset
        if (kapuaQuery.getOffset() != null) {
            query.setFirstResult(kapuaQuery.getOffset().intValue());
        }

        // Set limit
        if (kapuaQuery.getLimit() != null) {
            query.setMaxResults(kapuaQuery.getLimit().intValue() + 1);
        }

        // Finally quering!
        List<E> result = query.getResultList();

        // Check limit exceeded
        if (kapuaQuery.getLimit() != null &&
                result.size() > kapuaQuery.getLimit().intValue()) {
            result.remove(kapuaQuery.getLimit().intValue());
            resultContainer.setLimitExceeded(true);
        }

        // Set results
        resultContainer.addItems(result);
        return resultContainer;
    }

    /**
     * Count entity utility method
     * 
     * @param em
     * @param interfaceClass
     *            result query interface class
     * @param implementingClass
     *            result query implementation class
     * @param kapuaQuery
     * @return
     * @throws KapuaException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <I extends KapuaEntity, E extends I> long count(EntityManager em,
            Class<I> interfaceClass,
            Class<E> implementingClass,
            KapuaQuery<I> kapuaQuery)
            throws KapuaException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaSelectQuery = cb.createQuery(Long.class);

        //
        // FROM
        Root<E> entityRoot = criteriaSelectQuery.from(implementingClass);

        //
        // SELECT
        criteriaSelectQuery.select(cb.count(entityRoot));

        //
        // WHERE
        ParameterExpression<Long> scopeIdParam = cb.parameter(Long.class);
        Expression<Boolean> scopeIdExpr = cb.equal(entityRoot.get("scopeId"), scopeIdParam);

        Map<ParameterExpression, Object> binds = new HashMap<ParameterExpression, Object>();
        binds.put(scopeIdParam, kapuaQuery.getScopeId());
        Expression<Boolean> expr = handleKapuaQueryPredicates(kapuaQuery.getPredicate(),
                binds,
                cb,
                entityRoot,
                entityRoot.getModel());

        if (expr == null) {
            criteriaSelectQuery.where(scopeIdExpr);
        } else {
            criteriaSelectQuery.where(cb.and(scopeIdExpr, expr));
        }

        //
        // COUNT!
        TypedQuery<Long> query = em.createQuery(criteriaSelectQuery);

        // Populate query parameters
        for (ParameterExpression pe : binds.keySet()) {
            query.setParameter(pe, binds.get(pe));
        }

        return query.getSingleResult();
    }

    /**
     * Criteria for query entity utility method
     * 
     * @param qp
     * @param binds
     * @param cb
     * @param userPermissionRoot
     * @param entityType
     * @return
     * @throws KapuaException
     */
    @SuppressWarnings("rawtypes")
    protected static <E> Expression<Boolean> handleKapuaQueryPredicates(KapuaPredicate qp,
            Map<ParameterExpression, Object> binds,
            CriteriaBuilder cb,
            Root<E> userPermissionRoot,
            EntityType<E> entityType)
            throws KapuaException {
        Expression<Boolean> expr = null;
        if (qp instanceof KapuaAttributePredicate) {
            KapuaAttributePredicate attrPred = (KapuaAttributePredicate) qp;
            expr = handleAttributePredicate(attrPred, binds, cb, userPermissionRoot, entityType);
        } else if (qp instanceof KapuaAndPredicate) {
            KapuaAndPredicate andPredicate = (KapuaAndPredicate) qp;
            expr = handleAndPredicate(andPredicate, binds, cb, userPermissionRoot, entityType);
        } else if (qp instanceof KapuaOrPredicate) {
            KapuaOrPredicate andPredicate = (KapuaOrPredicate) qp;
            expr = handleOrPredicate(andPredicate, binds, cb, userPermissionRoot, entityType);
        }
        return expr;
    }

    @SuppressWarnings("rawtypes")
    private static <E> Expression<Boolean> handleAndPredicate(KapuaAndPredicate andPredicate,
            Map<ParameterExpression, Object> binds,
            CriteriaBuilder cb,
            Root<E> entityRoot,
            EntityType<E> entityType)
            throws KapuaException {
        List<Expression<Boolean>> exprs = new ArrayList<Expression<Boolean>>();
        for (KapuaPredicate pred : andPredicate.getPredicates()) {
            Expression<Boolean> expr = handleKapuaQueryPredicates(pred, binds, cb, entityRoot, entityType);
            exprs.add(expr);
        }
        return cb.and(exprs.toArray(new Predicate[] {}));
    }

    @SuppressWarnings("rawtypes")
    private static <E> Expression<Boolean> handleOrPredicate(KapuaOrPredicate andPredicate,
            Map<ParameterExpression, Object> binds,
            CriteriaBuilder cb,
            Root<E> entityRoot,
            EntityType<E> entityType)
            throws KapuaException {
        List<Expression<Boolean>> exprs = new ArrayList<Expression<Boolean>>();
        for (KapuaPredicate pred : andPredicate.getPredicates()) {
            Expression<Boolean> expr = handleKapuaQueryPredicates(pred, binds, cb, entityRoot, entityType);
            exprs.add(expr);
        }
        return cb.or(exprs.toArray(new Predicate[] {}));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <E> Expression<Boolean> handleAttributePredicate(KapuaAttributePredicate attrPred,
            Map<ParameterExpression, Object> binds,
            CriteriaBuilder cb,
            Root<E> entityRoot,
            EntityType<E> entityType)
            throws KapuaException {
        Expression<Boolean> expr;
        String attrName = attrPred.getAttributeName();
        Object attrValue = attrPred.getAttributeValue();
        if (attrValue instanceof Object[] && ((Object[]) attrValue).length == 1) {
            Object[] attrValues = (Object[]) attrValue;
            attrValue = attrValues[0];
        }
        if (attrValue instanceof Object[]) {
            Object[] attrValues = (Object[]) attrValue;
            Expression<?> inPredicate = entityRoot.get(entityType.getSingularAttribute(attrName));
            In inExpr = cb.in(inPredicate);
            for (Object value : attrValues) {
                inExpr.value(value);
            }
            expr = inExpr;
        } else {
            switch (attrPred.getOperator()) {
            case LIKE:
                ParameterExpression<String> pl = cb.parameter(String.class);
                binds.put(pl, "%" + attrValue + "%");
                expr = cb.like((Expression<String>) entityRoot.get(entityType.getSingularAttribute(attrName)), pl);
                break;

            case STARTS_WITH:
                ParameterExpression<String> psw = cb.parameter(String.class);
                binds.put(psw, attrValue + "%");
                expr = cb.like((Expression<String>) entityRoot.get(entityType.getSingularAttribute(attrName)), psw);
                break;

            case IS_NULL:
                expr = cb.isNull(entityRoot.get(entityType.getSingularAttribute(attrName)));
                break;

            case NOT_NULL:
                expr = cb.isNotNull(entityRoot.get(entityType.getSingularAttribute(attrName)));
                break;

            case NOT_EQUAL:
                expr = cb.notEqual(entityRoot.get(entityType.getSingularAttribute(attrName)), attrValue);
                break;

            default:
            case EQUAL:
                expr = cb.equal(entityRoot.get(entityType.getSingularAttribute(attrName)), attrValue);
                break;
            }
        }
        return expr;
    }
}
