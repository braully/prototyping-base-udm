package com.github.braully.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Braully Rocha da Silva
 */
public interface ICrudEntity {

    /*
     * Load Methods
     */
    List loadCollection(String nameCls);

    <T> List<T> loadCollection(Class<T> entityClass);

    <T> List<T> loadCollectionWhere(Class<T> entityClass, Object... args);

    <T> List<T> loadCollectionSorted(Class<T> entityClass, String... asc);

    <T> List<T> loadCollectionFetch(Class<T> entityClass, String... props);

    <T> T loadEntity(T entity);

    <T> T loadEntity(Class<T> clz, Object id);

    <T> T loadEntityFetch(IEntity e, String... props);

    <T> T loadEntityFetch(String nameEntity, Object id, String... props);

    /* Query Mehtods */
    public Object queryObject(Object... args);

    public List queryList(Object... args);

    public <T> List<T> genericFulltTextSearch(Class<T> entityClass, String searchString, Map<String, Object> extraSearchParams);

    /*
     * Save Mehtods
     */
    void saveEntity(IEntity... e);

    void saveEntityCascade(IEntity e, Collection<? extends IEntity>... rels);

    void saveEntityFlyWeigth(IEntity entity, String... nameProps);

    void delete(Object entity);

//    /* 
//     Load Paged Mehtods
//     */
//    public <T> PagedQueryResult loadCollectionPagedQuery(Class<T> classe);
//
//    public void next(PagedQueryResult queryResult);
    public static final int DEFAULT_PAGE_SIZE = 15;

    /*
     *
     */
    <T> PagedQueryResult loadCollectionPagedQuery(Class<T> entityClass);

    <T> PagedQueryResult loadCollectionWherePagedQuery(Class<T> entityClass, Object... args);

    <T> PagedQueryResult loadCollectionSortedPagedQuery(Class<T> entityClass, String... asc);

    <T> PagedQueryResult loadCollectionFetchPagedQuery(Class<T> entityClass, String... props);

    public PagedQueryResult queryListPagedQuery(Object... args);

    public <T> PagedQueryResult genericFulltTextSearchPagedQuery(Class<T> entityClass,
            String searchString, Map<String, Object> extraSearchParams);

    void paginate(PagedQueryResult queryResult);
}
