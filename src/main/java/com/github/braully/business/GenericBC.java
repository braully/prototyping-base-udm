package com.github.braully.business;

import com.github.braully.persistence.GenericDAO;
import com.github.braully.persistence.ICrudEntity;
import com.github.braully.persistence.IEntity;
import com.github.braully.persistence.PagedQueryResult;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("rawtypes")
@Service("genericoBC")
public class GenericBC implements Serializable, ICrudEntity {

    private static final long serialVersionUID = 1L;

    /* */
    @Autowired
    protected GenericDAO genericDAO;

    public <T extends Comparable<? super T>> List<T> carregarColecaoOrdenada(Class<T> classe) {
        List<T> lista = null;
        if (classe != null) {
            lista = (List<T>) this.getGenericDAO().loadCollection(classe);
            if (lista != null) {
                Collections.sort(lista);
            }
        }
        return lista;
    }

    @Override
    public <T> List<T> loadCollectionSorted(Class<T> classe, String... nomePropriedade) {
        return this.getGenericDAO().loadCollectionSorted(classe, nomePropriedade);
    }

    @Override
    public <T> List<T> loadCollection(Class<T> classe) {
        return this.getGenericDAO().loadCollection(classe);
    }

    @Override
    public <T> List<T> loadCollectionWhere(Class<T> classe, Object... args) {
        return this.getGenericDAO().loadCollectionWhere(classe, args);
    }

    @Override
    public <T> List<T> loadCollectionFetch(Class<T> classe, String... propriedades) {
        return this.getGenericDAO().loadCollectionFetch(classe, propriedades);
    }

    @Override
    public <T> T loadEntity(T entidade) {
        return this.getGenericDAO().loadEntity(entidade);
    }

    @Override
    public <T> T loadEntityFetch(IEntity e, String... propriedades) {
        return this.getGenericDAO().loadEntityFetch(e, propriedades);
    }

    @Override
    public void delete(Object entidade) {
        this.getGenericDAO().delete(entidade);
    }

    @Override
    public void deleteSoft(ILightRemoveEntity entity) {
        this.getGenericDAO().delete(entity);
    }

    @Override
    public void saveEntityCascade(IEntity e, Collection<? extends IEntity>... relacoes) {
        this.getGenericDAO().saveEntityCascade(e, relacoes);
    }

    @Override
    public void saveEntityFlyWeigth(IEntity entidade, String... nomePropriedades) {
        this.getGenericDAO().saveEntityFlyWeigth(entidade, nomePropriedades);
    }

    @Override
    public void saveEntity(IEntity... args) {
        this.getGenericDAO().saveEntity(args);
    }

    protected ICrudEntity getGenericDAO() {
        return genericDAO;
    }

    @Override
    public List loadCollection(String nomeClasse) {
        return this.getGenericDAO().loadCollection(nomeClasse);
    }

    @Override
    public <T> T loadEntityFetch(String nomeEntidade, Object id, String... propriedades) {
        return this.getGenericDAO().loadEntityFetch(nomeEntidade, id, propriedades);
    }

    @Override
    public Object queryObject(Object... args) {
        return this.getGenericDAO().queryObject(args);
    }

    @Override
    public List queryList(Object... args) {
        return this.queryList(args);
    }

    @Override
    public <T> List<T> genericFulltTextSearch(Class<T> cls, String searchString, Map<String, Object> extraSearchParams) {
        return this.getGenericDAO().genericFulltTextSearch(cls, searchString, extraSearchParams);
    }

    @Override
    public <T> PagedQueryResult loadCollectionPagedQuery(Class<T> entityClass) {
        return this.getGenericDAO().loadCollectionPagedQuery(entityClass);
    }

    @Override
    public <T> PagedQueryResult loadCollectionWherePagedQuery(Class<T> entityClass, Object... args) {
        return this.getGenericDAO().loadCollectionWherePagedQuery(entityClass, args);
    }

    @Override
    public <T> PagedQueryResult loadCollectionSortedPagedQuery(Class<T> entityClass, String... asc) {
        return this.getGenericDAO().loadCollectionSortedPagedQuery(entityClass, asc);
    }

    @Override
    public <T> PagedQueryResult loadCollectionFetchPagedQuery(Class<T> entityClass, String... props) {
        return this.getGenericDAO().loadCollectionFetchPagedQuery(entityClass, props);
    }

    @Override
    public PagedQueryResult queryListPagedQuery(Object... args) {
        return this.getGenericDAO().queryListPagedQuery(args);
    }

    @Override
    public <T> PagedQueryResult genericFulltTextSearchPagedQuery(Class<T> entityClass, String searchString, Map<String, Object> extraSearchParams) {
        return this.getGenericDAO().genericFulltTextSearchPagedQuery(entityClass, searchString, extraSearchParams);
    }

    @Override
    public void paginate(PagedQueryResult queryResult) {
        this.getGenericDAO().paginate(queryResult);
    }

    @Override
    public <T> T loadEntity(Class<T> clz, Object id) {
        return this.getGenericDAO().loadEntity(clz, id);
    }

}
