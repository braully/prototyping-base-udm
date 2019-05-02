package com.github.braully.persistence;

import com.github.braully.util.UtilComparator;
import com.github.braully.util.UtilReflection;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

@SuppressWarnings("rawtypes")
public abstract class DAO implements ICrudEntity, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(UtilComparator.class);

    /* */
    protected abstract EntityManager getEntityManager();

    protected abstract IUser getUserOperation();

    /* Save methods */
    @Override
    public void saveEntity(IEntity... args) {
        saveEntitysImpl(Arrays.asList(args));
    }

    public void saveEntitys(Collection<? extends IEntity> entidades) {
        saveEntitysImpl(entidades);
    }

    protected void saveEntitysImpl(Iterable<? extends IEntity> entidades) {
        if (entidades != null) {
            for (IEntity e : entidades) {
                if (e.isPersisted()) {
                    this.update(e);
                } else {
                    this.insert(e);
                }
            }
        }
    }

    @Override
    public void saveEntityCascade(IEntity e, Collection<? extends IEntity>... relacoes) {
        if (relacoes != null) {
            for (Collection<? extends IEntity> relacao : relacoes) {
                if (relacao != null) {
                    for (IEntity rel : relacao) {
                        this.saveEntity(rel);
                    }
                }
            }
        }
        this.saveEntity(e);
    }

    @Override
    public void saveEntityFlyWeigth(IEntity entidade, String... namePropriedades) {
        if (entidade == null || namePropriedades == null || namePropriedades.length <= 0) {
            throw new IllegalArgumentException();
        }
        if (entidade.isPersisted()) {
            IEntity entidadeTmp = this.loadEntity(entidade);
            if (!UtilComparator.equals(entidade, entidadeTmp, namePropriedades)) {
                entidade.setId(null);
                this.saveEntity(entidade);
            }
        } else {
            String prop = null;
            try {
                Object[] params = new Object[namePropriedades.length * 2];
                for (int i = 0; i < namePropriedades.length; i++) {
                    prop = namePropriedades[i];
                    int j = i * 2;
                    params[j] = prop;
                    params[j + 1] = UtilReflection.getProperty(entidade, prop);
                }
                List<IEntity> colecao = (List<IEntity>) this.loadCollectionWhere(entidade.getClass(), params);
                if (colecao != null && !colecao.isEmpty()) {
                    entidade.setId(colecao.get(0).getId());
                } else {
                    this.saveEntity(entidade);
                }
            } catch (Exception e) {
                String strErro = "Falha ao load propriedade=" + prop + " do objeto=" + entidade;
                throw new IllegalStateException(strErro, e);
            }
        }
    }

    public void insert(Object entity) {
        getEntityManager().persist(entity);
    }

    public void update(Object entity) {
        getEntityManager().merge(entity);
    }

    public void delete(Object entity) {
        EntityManager em = this.getEntityManager();
        em.merge(entity);
        em.remove(entity);
    }

    public <T> void delete(Object id, Class<T> classe) {
        Object entity = getEntityManager().getReference(classe, id);
        this.delete(entity);
    }

    public <T> T load(Object id, Class<T> classe) {
        return getEntityManager().find(classe, id);
    }


    /* */
    PagedQueryResult buildPagedQuery(Query query, int size, int page) {
        PagedQueryResult pagedQueryResult = new PagedQueryResult(size, page);
        if (query != null) {
            Set<Parameter<?>> setparams = query.getParameters();
            if (setparams != null) {
                setparams.forEach(p -> pagedQueryResult.parameters.put(p, query.getParameterValue(p)));
            }
            String queryString = query.unwrap(org.hibernate.query.Query.class).getQueryString();
            pagedQueryResult.queryString = queryString;
            String queryCountString = queryString.replaceFirst("SELECT ", "SELECT COUNT(");
            queryCountString = queryCountString.replaceFirst(" FROM ", ") FROM ");
            pagedQueryResult.queryCountString = queryCountString;
        }
        return pagedQueryResult;
    }

    public PagedQueryResult queryPaged(Query query, int size, int page) {
        PagedQueryResult pagedQueryResult = buildPagedQuery(query, size, page);
        count(pagedQueryResult);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        pagedQueryResult.result = query.getResultList();
        pagedQueryResult.page = page;
        pagedQueryResult.size = size;
        return pagedQueryResult;
    }

    public PagedQueryResult queryPaged(Query query) {
        return queryPaged(query, DEFAULT_PAGE_SIZE, 0);
    }

//    @Override
//    public void next(PagedQueryResult queryResult) {
//        queryResult.page++;
//        paginate(queryResult);
//    }
//
//    @Override
//    public void previous(PagedQueryResult queryResult) {
//        queryResult.page--;
//        paginate(queryResult);
//    }
    @Override
    public void paginate(PagedQueryResult queryResult) {
        Query nquery = this.getEntityManager().createQuery(queryResult.queryString);
//        queryResult.parameters.forEach((k, v) -> nquery.setParameter(k, v));
        setParameters(queryResult, nquery);
        nquery.setFirstResult(queryResult.page * queryResult.size);
        nquery.setMaxResults(queryResult.size);
        queryResult.newResults(nquery.getResultList());
    }

    private void count(PagedQueryResult queryResult) {
        Query query = this.getEntityManager().createQuery(queryResult.queryCountString);
        setParameters(queryResult, query);
        queryResult.count = ((Number) query.getSingleResult()).intValue();
    }

    private void setParameters(PagedQueryResult queryResult, Query query) {
        Set<Map.Entry<Parameter, Object>> entrySet = queryResult.parameters.entrySet();
//        queryResult.parameters.forEach((k, v) -> query.setParameter(k, v));

        for (Map.Entry<Parameter, Object> e : entrySet) {
            Parameter key = e.getKey();
            String name = key.getName();
            Integer position = key.getPosition();
            if (name != null) {
                query.setParameter(name, e.getValue());
            } else if (position != null) {
                query.setParameter(position, e.getValue());
            } else {
                query.setParameter(key, e.getValue());
            }
        }
    }

    public Session getSession() {
        Object delegate = this.getEntityManager().unwrap(org.hibernate.Session.class);
        return (Session) delegate;
    }

    @SuppressWarnings("unchecked")
    public <T> int count(Class<T> classe) {
        int ret = 0;
        Query query = null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(c) FROM ");
        sb.append(classe.getSimpleName());
        sb.append(" c");
        if (IEntityStatus.class.isAssignableFrom(classe)) {
            sb.append(" WHERE c.status IS NULL OR c.status = ?");
            query = this.getEntityManager().createQuery(sb.toString());
            query.setParameter(1, Status.ACTIVE);
        } else {
            query = this.getEntityManager().createQuery(sb.toString());
        }

        Number cont = (Number) query.getSingleResult();
        if (cont != null) {
            ret = cont.intValue();
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadCollection(Class<T> classe) {
        Query query = null;
        List<T> lista = null;
        query = loadCollectionQuery(classe);
        lista = (List<T>) query.getResultList();
        return lista;
    }

    public <T> PagedQueryResult loadCollectionPagedQuery(Class<T> classe) {
        Query query = loadCollectionQuery(classe);
        return queryPaged(query);
    }

    protected <T> Query loadCollectionQuery(Class<T> classe) {
        Query query;
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c FROM ");
        sb.append(classe.getSimpleName());
        sb.append(" c");
        if (IEntityStatus.class.isAssignableFrom(classe)) {
            sb.append(" WHERE c.status IS NULL OR c.status = ?1");
            query = this.getEntityManager().createQuery(sb.toString());
            query.setParameter(1, Status.ACTIVE);
        } else {
            query = this.getEntityManager().createQuery(sb.toString());
        }
        return query;
    }

    @Override
    public List loadCollection(String nameClasse) {
        Query query = null;
        List lista = null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c FROM ");
        sb.append(nameClasse);
        sb.append(" c");
        String sqlstring = sb.toString();

        query = this.getEntityManager().createQuery(sqlstring);
        lista = query.getResultList();
        return lista;
    }

    @Override
    public <T> List<T> loadCollectionSorted(Class<T> classe, String... args) {
        List<T> lista = null;
        if (classe != null) {
            StringBuilder hql = new StringBuilder();
            try {
                hql.append("SELECT DISTINCT e FROM ").append(classe.getSimpleName()).append(" e ");
                Query q = null;
                if (args != null && args.length > 0) {
                    hql.append(" ORDER BY ");
                    for (int i = 0; i < args.length; i = i + 2) {
                        hql.append(args[i]);
                        if (i < args.length - 1) {
                            hql.append(", ");
                        }
                    }
                    q = getEntityManager().createQuery(hql.toString());
                }
                lista = (List<T>) q.getResultList();
            } catch (Exception e) {
                log.error("Erro ao busar", e);
            }
        }
        return lista;
    }

//    @Override
    public <T> List<T> loadCollectionWhere(Class<T> classe, Map parameters) {
        Object[] args = null;
        if (parameters != null) {
            args = new Object[parameters.size() * 2];
            int i = 0;
            Set<Map.Entry> entrys = parameters.entrySet();
            for (Map.Entry e : entrys) {
                args[i++] = e.getKey();
                args[i++] = e.getValue();
            }
//            parameters.forEach((k, v) -> args[i++] = k);//Only java 8
        }
        return loadCollectionWhere(classe, args);
    }

    @Override
    public <T> List<T> loadCollectionWhere(Class<T> classe, Object... args) {
        List<T> lista = null;
        if (classe != null) {

            try {
                String hql = "SELECT DISTINCT e FROM " + classe.getSimpleName() + " e ";
                Query q = null;
                if (args != null && args.length > 0 && (args.length % 2) == 0) {
                    q = buildGeneriQuery(hql, args);
                }
                lista = (List<T>) q.getResultList();
            } catch (Exception e) {
                log.error("Erro ao busar", e);
            }
        }
        return lista;
    }

    protected Query buildGeneriQuery(String hql, Object... args) {
        String where = whereClause(args);
        Query q = getEntityManager().createQuery(hql + where);
        whereCluaseParameters(args, q);
        return q;
    }

    protected String whereClause(Object[] args) {
        StringBuilder hql = new StringBuilder();
        int countind = 1;
        hql.append(" WHERE e.").append(args[0]).append(" = ?").append(countind++);

        for (int i = 2; i < args.length; i = i + 2) {
            hql.append(" AND e.").append(args[i]).append(" = ?").append(countind++);
        }
        return hql.toString();
    }

    protected void whereCluaseParameters(Object[] args, Query q) {
        int j = 1;
        for (int i = 1; i < args.length; i = i + 2) {
            q.setParameter(j++, args[i]);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadCollectionFetch(Class<T> classe, String... propriedades) {
        List<T> lista = null;
        if (classe != null) {
            StringBuilder sb = this.gerarQueryLoadFetch(classe.getSimpleName(), propriedades);

            if (IEntityStatus.class.isAssignableFrom(classe)) {
                sb.append(" WHERE e.status = ");
                sb.append(Status.ACTIVE.ordinal());
                sb.append(" OR e.status IS NULL");
            }
            lista = (List<T>) this.getEntityManager().createQuery(sb.toString()).getResultList();
        }
        return lista;
    }

    public Object queryObject(Object... args) {
        Object result = null;
        if (args != null && args.length > 0) {
            Query q = queryTemplate(args);
            result = q.getSingleResult();
        }
        return result;
    }

    @Override
    public <T> List<T> genericFulltTextSearch(Class<T> cls, String searchString, Map<String, Object> extraSearchParams) {
        List result = null;
        Query query = genericFullTextSearchQuery(cls, searchString, extraSearchParams);
        result = query.getResultList();
        return result;
    }

    protected <T> Query genericFullTextSearchQuery(Class<T> cls, String searchString,
            Map<String, Object> extraSearchParams) {
        Map<String, Object> mapSanitizedParameter = new HashMap<>();
        if (extraSearchParams != null) {
            extraSearchParams.forEach((k, v) -> {
                if (isValid(v)) {
                    mapSanitizedParameter.put(k, v);
                }
            });
        }

        String strquery = buildGenericQueryFullTextSearch(cls, searchString, mapSanitizedParameter);
        log.debug("genericFullSearch");
        log.debug(strquery);
        Query query = this.getEntityManager().createQuery(strquery);
        if (isValidString(searchString)) {
            query.setParameter("searchString", "%" + searchString.toLowerCase().trim() + "%");
        }
        mapSanitizedParameter.forEach((k, v) -> {
            if (isValid(v)) {
                query.setParameter(k.replace('.', '_'), v);
            }
        });
        return query;
    }

    public String buildGenericQueryFullTextSearch(Class<?> cls, String searchString, Map<String, Object> extraSearchParams) {
        StringBuilder where = new StringBuilder();
        String[] extraProps = null;
        if (!extraSearchParams.isEmpty()) {
            List<String> extraPorperties = new ArrayList<>();
            extraSearchParams.forEach((k, v) -> {
                if (isValid(v)) {
//                    extraPorperties.add(k);
                    where.append("lower(e.");
                    where.append(k);
                    where.append(")  like :");
                    where.append(k.replace('.', '_'));
                }
            });
            if (!extraPorperties.isEmpty()) {
                extraProps = extraPorperties.toArray(new String[0]);
            }
        }

        StringBuilder sbquery = gerarQueryLoadFetch(cls.getSimpleName(), extraProps);
        List<Field> fieldsPersisted = UtilReflection.getAllFieldsAssinableFrom(cls, String.class, Number.class);

        if (fieldsPersisted != null && fieldsPersisted.size() > 0 && isValidString(searchString)) {
            fieldsPersisted.removeIf(fld -> fld.isAnnotationPresent(Transient.class));
            for (int i = 0; i < fieldsPersisted.size(); i++) {
                Field fld = fieldsPersisted.get(i);
                where.append("lower(e.");
                where.append(fld.getName());
                where.append(")  like :");
                where.append("searchString");
                if (i < fieldsPersisted.size() - 1) {
                    where.append(" OR ");
                }
            }
        }

        if (where.length() > 0) {
            sbquery.append(" WHERE ").append(where);
        }
        String strquery = sbquery.toString();
        return strquery;
    }

    public List queryList(Object... args) {
        List result = null;
        if (args != null && args.length > 0) {
            Query q = queryTemplate(args);
            result = q.getResultList();
        }
        return result;
    }

    public List queryNativeList(Object... args) {
        List result = null;
        if (args != null && args.length > 0) {
            Query q = this.getEntityManager().createNativeQuery(
                    (String) args[0]);
            for (int i = 1; i < args.length; i++) {
                Object arg = args[i];
                if (arg != null && Date.class.isAssignableFrom(arg.getClass())) {
                    q = q.setParameter(i, (Date) arg, TemporalType.DATE);
                } else {
                    q = q.setParameter(i, arg);
                }
            }
            result = q.getResultList();
        }
        return result;
    }

    public Query createQuery(String ql) {
        return getEntityManager().createQuery(ql);
    }

    @Override
    public <T> T loadEntity(T entidade) {
        try {
            return (T) this.getEntityManager().find(entidade.getClass(), PropertyUtils.getProperty(entidade, "id"));
        } catch (Exception ex) {
            log.error("error load entity", ex);
        }
        return null;
    }

    @Override
    public <T> T loadEntityFetch(IEntity e, String... propriedades) {
        if (e == null) {
            return null;
        }
        return this.loadEntityFetch(e.getClass().getSimpleName(), e.getId(), propriedades);
    }

    @Override
    public <T> T loadEntityFetch(String nameEntity, Object id, String... propriedades) {
        T result = null;
        String sql = null;
        try {
            StringBuilder query = gerarQueryLoadFetch(nameEntity, propriedades);
            query.append(" WHERE e.id = :id");
            EntityManager em = this.getEntityManager();
            sql = query.toString();
            Query q = em.createQuery(sql);
            q.setParameter("id", id);
            result = (T) q.getSingleResult();
        } catch (RuntimeException ex) {
            log.debug(sql);
            log.error("Falha ao load entidade", ex);
        }
        return result;
    }

    public StringBuilder gerarQueryLoadFetch(String raiz, String... propriedades) {
        StringBuilder sb = new StringBuilder("SELECT DISTINCT e FROM ");
        String abrevRaiz = "e";
        sb.append(raiz);
        sb.append(" ");
        sb.append(abrevRaiz);
        Map<String, String> mapaPropriedades = new HashMap<String, String>();
        mapaPropriedades.put(raiz, abrevRaiz);
        if (propriedades != null && propriedades.length > 0) {
            for (String prop : propriedades) {
                String tmp = gerarAppend(abrevRaiz, prop, mapaPropriedades);
                sb.append(tmp);
            }
        }
        return sb;
    }

    private String gerarAppend(String pai, String prop, Map<String, String> mapaPropriedades) {
        StringBuilder sb = new StringBuilder();
        if (prop.contains(".")) {
            String filho = prop.substring(0, prop.indexOf("."));
            String propFilha = prop.substring(prop.indexOf(".") + 1);
            String paiTmp = mapaPropriedades.get(pai + "_" + filho);
            sb.append(gerarAppend(paiTmp, propFilha, mapaPropriedades));
        } else {
            sb.append(" LEFT JOIN FETCH ");
            sb.append(pai);
            sb.append(".");
            sb.append(prop);
            sb.append(" ");
            sb.append(gerarAbreviacao(pai, prop, mapaPropriedades));
        }
        return sb.toString();
    }

    private String gerarAbreviacao(String pai, String prop, Map<String, String> mapaPropriedades) {
        String chave = pai + "_" + prop;
        String ret = mapaPropriedades.get(chave);
        if (ret == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(prop.charAt(0));
            int ind = 0;
            while (mapaPropriedades.containsValue(sb.toString())) {
                sb.append(ind++);
            }
            ret = sb.toString();
            mapaPropriedades.put(chave, ret);
        }
        return ret;
    }

    //TODO: Refatorar, colocar o primeiro argumento como string
    private Query queryTemplate(Object... args) {
        Query q = this.getEntityManager().createQuery((String) args[0]);
        for (int i = 1; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null && Date.class.isAssignableFrom(arg.getClass())) {
                q = q.setParameter(i, (Date) arg, TemporalType.DATE);
            } else {
                q = q.setParameter(i, arg);
            }
        }
        return q;
    }

    public <T> PagedQueryResult genericFulltTextSearchPagedQuery(Class<T> entityClass, String searchString,
            Map<String, Object> extraSearchParams) {
        return this.queryPaged(this.genericFullTextSearchQuery(entityClass, searchString, extraSearchParams));
    }

    /* Skeleton Methods */
    public <T> PagedQueryResult loadCollectionWherePagedQuery(Class<T> entityClass, Object... args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> PagedQueryResult loadCollectionSortedPagedQuery(Class<T> entityClass, String... asc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> PagedQueryResult loadCollectionFetchPagedQuery(Class<T> entityClass, String... props) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PagedQueryResult queryListPagedQuery(Object... args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isValidString(String searchString) {
        return searchString != null && !searchString.trim().isEmpty();
    }

    public boolean isValid(Object value) {
        if (value instanceof String) {
            return isValidString((String) value);
        }
        return value != null;
    }
}
