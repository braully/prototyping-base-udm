package com.github.braully.persistence;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author braully
 */
@Repository("genericDAO")
public class GenericDAO extends DAO {

    @Autowired
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    protected IUser getUserOperation() {
        return null;
    }

    @Transactional
    @Override
    public void saveEntityFlyWeigth(IEntity entidade, String... namePropriedades) {
        super.saveEntityFlyWeigth(entidade, namePropriedades); //To change body of generated methods, choose Tools | Templates.
    }

    @Transactional
    @Override
    public void saveEntity(IEntity... e) {
        super.saveEntity(e);
    }

    @Transactional
    @Override
    public void update(Object entity) {
        super.update(entity);
    }

    @Transactional
    public int updateEntityQuery(IEntity entity, Object... propvalue) {
        return super.updateEntityQuery(entity, propvalue);
    }

    @Transactional
    @Override
    public void insert(Object entity) {
        super.insert(entity);
    }

    @Transactional
    @Override
    public void delete(Object entity) {
        super.delete(entity);
    }

    @Transactional
    @Override
    public <T> void delete(Object id, Class<T> classe) {
        super.delete(id, classe);
    }

//    public void test() {
//        PagingAndSortingRepository t = null;
//        Pageable pageable = null;
//        SimpleJpaRepository t1 = null;
//        FilterChainProxy t0 = null;
//        t.findAll(pageable);
//    }
    public Session getSession() {
        Object delegate = entityManager.unwrap(org.hibernate.Session.class);
        return (Session) delegate;
    }

    @Transactional
    public void insertHibernate(IEntity e) {
        this.getSession().save(e);
    }

    @Transactional
    public void updateHibernate(IEntity e) {
        this.getSession().update(e);
    }

    @Transactional
    public void deleteHibernate(IEntity e) {
        this.getSession().delete(e);
    }

    @Transactional
    public void saveEntityHibernate(IEntity e) {
        if (e.isPersisted()) {
            this.updateHibernate(e);
        } else {
            this.insertHibernate(e);
        }
    }

    @Transactional
    @Override
    public int count(Class entiClass) {
        return super.count(entiClass);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void flush() {
        this.getEntityManager().flush();
    }

    @Override
    public <T> Query genericFullTextSearchQuery(Class<T> cls, String searchString, Map<String, Object> extraSearchParams) {
        return super.genericFullTextSearchQuery(cls, searchString, extraSearchParams); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> void parameterGenericQueryFullTextSearch(Class<T> cls, String searchString, Map<String, Object> mapSanitizedParameter, Query query) {
        super.parameterGenericQueryFullTextSearch(cls, searchString, mapSanitizedParameter, query); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> sanitizeParameters(Map<String, Object> extraSearchParams) {
        return super.sanitizeParameters(extraSearchParams); //To change body of generated methods, choose Tools | Templates.
    }

}
