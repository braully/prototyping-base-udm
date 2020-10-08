package com.github.braully.app;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.github.braully.app.GenericController.log;
import com.github.braully.constant.Attr;
import com.github.braully.domain.ILightRemoveEntity;
import com.github.braully.domain.Organization;
import com.github.braully.domain.OrganizationRole;
import com.github.braully.domain.Role;
import com.github.braully.domain.UserLogin;
import com.github.braully.interfaces.IOrganiztionEntityDependent;
import com.github.braully.persistence.ICrudEntity;
import com.github.braully.persistence.IEntity;
import com.github.braully.persistence.IEntityStatus;
import com.github.braully.persistence.PagedQueryResult;
import com.github.braully.util.UtilCollection;
import com.github.braully.util.UtilParse;
import com.github.braully.util.UtilReflection;
import com.github.braully.util.UtilValidation;
import com.github.braully.web.DescriptorExposedEntity;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
public abstract class CRUDGenericController<T extends IEntity> {

    protected static final Logger log = LogManager.getLogger(CRUDGenericController.class);
    /* */
    public static final String ID = "id";
    public static final String IDS = "ids";
    public static final String OPERATION = "operation";
    public static final String OP = "op";
    public static final String ID_USER = "user_id";
    public static final String NAME_PROP_INDEX_TAB = "tabIndex";
    public static final String USER_NAME_VARIABLE = "username";

    protected final Map<String, List> CACHE = new HashMap<>();
    protected final Map<String, Object> CACHE_BEAN = new HashMap<>();
    protected final Map<String, CRUDGenericController> CACHE_CRUDS = new HashMap<String, CRUDGenericController>();

    protected T entity;
    protected List<T> allEntities;
    protected Class<T> entityClass;
    protected PagedQueryResult queryResult;
    @Getter
    @Setter
    protected String jsonEntity;
    @Getter
    @Setter
    protected String jsonEntities;
    @Getter
    @Setter
    protected String searchString;
    @Getter
    protected Map<String, Object> extraSearchParams = new HashMap<>();
    @Getter
    protected Map<String, Object> extraProperties = new HashMap<>();
    @Getter
    @Setter
    protected int index;
    @Getter
    @Setter
    protected String operation;

    protected boolean validPreSave;

    @Autowired(required = false)
    protected HttpSession session;
    @Autowired(required = false)
    protected HttpServletRequest request;

    protected abstract ICrudEntity getGenericoBC();

    /*
    
     */
    public CRUDGenericController() {

    }

    public CRUDGenericController(Class<T> entidadeClass) {
        this.entityClass = entidadeClass;
    }

    @PostConstruct
    public void init() {
    }

    public List values(String entityNameField) {
        List tmp = null;
        tmp = CACHE.get(entityNameField);
        try {
            if (tmp == null) {
                Field field = exposed.getExposedEntityField(entityNameField);
                Class<?> type = null;
                if (field != null) {
                    type = field.getType();
                } else {
                    type = Class.forName(entityNameField);
                }
                tmp = new ArrayList();
                if (IEntity.class.isAssignableFrom(type)) {
                    List loadCollection = this.getGenericoBC().loadCollection(type);
                    if (loadCollection != null) {
                        tmp.addAll(loadCollection);
                    }
                } else if (Enum.class.isAssignableFrom(type)) {
                    for (Object e : Arrays.asList(type.getEnumConstants())) {
                        tmp.add(e);
                    }
                }
                CACHE.put(entityNameField, tmp);
            }
        } catch (Exception ex) {
            log.error("error values dynamic", ex);
        }
        return tmp;
    }

    public Class<T> getEntityClass() {
        try {
            if (entityClass == null) {
                entityClass = UtilReflection.getGenericTypeArgument(this.getClass(), 0);
            }
        } catch (Exception e) {
            log.debug("fail on indentify class type");
            log.trace("fail on indentify class type", e);
        }
        return entityClass;
    }

    public T getEntity() {
        if (entity == null) {
            newEntity();
        }
        return entity;
    }

    public void newEntity() {
        entity = UtilReflection.createInstance(this.getEntityClass());
    }

    public void findSilent() {
        this.find();
    }

    public CRUDGenericController<T> find() {
        try {
            this.queryResult = this.getGenericoBC()
                    .genericFulltTextSearchPagedQuery(this.getEntityClass(),
                            getFindExtraSearchString(), getFindExtraSearchParams());
            log.info("query executed");
        } catch (Exception e) {
            log.error("Fail on query generic", e);
        }
        return this;
    }

    protected PagedQueryResult findQuery() {
        PagedQueryResult ret = this.getGenericoBC()
                .genericFulltTextSearchPagedQuery(this.getEntityClass(),
                        getFindExtraSearchString(), getFindExtraSearchParams());
        return ret;
    }

    protected String getFindExtraSearchString() {
        return this.searchString;
    }

    protected Map<String, Object> getFindExtraSearchParams() {
        return this.extraSearchParams;
    }

    public void save() {
        if (this.save(entity)) {
            /*this.loadAllEntities();*/
            this.clearCacheLoadAllEntities();
            this.newEntity();
        }
    }

    public boolean saveSilent(IEntity entity) {
        return this.save(entity, null, "Verifique todos os campos obrigatórios");
    }

    public boolean saveSilent() {
        return this.saveSilent(entity);
    }

    public void block(IEntityStatus entidadeStatus) {
        try {
            entidadeStatus.block();
            if (this.save(entidadeStatus)) {
                this.loadAllEntities();
                this.newEntity();
            }

        } catch (RuntimeException e) {
        }
    }

    public void activate(IEntityStatus entidadeStatus) {
        try {
            entidadeStatus.activate();
            if (this.save(entidadeStatus)) {
                this.loadAllEntities();
                this.newEntity();
            }

        } catch (RuntimeException e) {
        }
    }

    public List<T> getAllEntities() {
        try {
            if (allEntities == null || allEntities.isEmpty()) {
                loadAllEntitiesList();
            }
        } catch (Exception e) {
            log.error("Falha ao carregar entidades", e);
        }
        return allEntities;
    }

    public void clearCacheLoadAllEntities() {
        //just clear cache
        this.clearCache();
        this.allEntities = null;
        this.loadAllEntities();
    }

    public void clearCache() {
        //just clear cache
        this.CACHE.clear();
    }

    public void clearCacheFind() {
        //just clear cache
        this.clearCache();
        this.find();
    }

    public void lodEntity(Long id) {
        this.entity = this.getGenericoBC().loadEntity(this.entityClass, id);
    }

    public CRUDGenericController<T> lodEntity(IEntity ientity) {
        if (ientity != null && ientity.isPersisted()) {
            //this.entity = this.getGenericoBC().loadEntity((Class<T>) ientity.getClass(), ientity.getId());
            this.entity = this.getGenericoBC().loadEntity(this.entityClass, ientity.getId());
        }
        return this;
    }

    public void loadAllEntities() {
        this.loadAllEntitiesPaged();
        //just clear cache
        this.allEntities = null;
    }

    public void loadAllEntitiesList() {
        try {
            this.allEntities = this.getGenericoBC().loadCollection(this.getEntityClass());
            boolean sortIfComparable = UtilCollection.sortIfComparable(this.allEntities);
        } catch (Exception e) {
            log.error("Falha ao carregar entidades", e);
        }
    }

    public void loadAllEntitiesPaged() {
        Class eclass = this.getEntityClass();
        try {
            if (IOrganiztionEntityDependent.class.isAssignableFrom(eclass)) {
                Organization org = this.getMonoPermitedInstituicao();
                String attrOrganization = "organization";
                if (org != null) {
                    try {
                        Method method = eclass.getMethod("getOrganization");
                        if (method != null) {
                            Attr annotation = method.getAnnotation(Attr.class);
                            if (annotation != null && annotation.name().equals("organization")) {
                                attrOrganization = annotation.val();
                            }
                        }
                    } catch (Exception e) {
                        log.debug("no default propertie getOrganization", e);
                    }
                    this.extraSearchParams.put(attrOrganization, org);
                    find();
                    return;
                }
            }
            queryResult = this.getGenericoBC().loadCollectionPagedQuery(eclass);
        } catch (Exception e) {
            addErro("Falha ao carregar entidades", e);
            log.error("Falha ao carregar entidades", e);
        }
    }

    public String removeEntityAndRedirect(String redirect) {
        try {
            if (this.remove(this.entity, "Removido com sucesso", "Falha ao remover")) {
                this.clearCacheLoadAllEntities();
                this.newEntity();
                return "redirect:" + redirect;
            }
        } catch (Exception e) {
            log.error("", e);
            addErro("Falha ao tentar remover: " + this.entity, e);

        }
        return null;
    }

    public void removeEntity() {
        this.remove(this.entity);
    }

    public void remove(T entidade) {
        if (this.remove(entidade, "Removido com sucesso", "Falha ao remover")) {
            this.loadAllEntities();
            this.newEntity();
        }
    }

    public boolean remove(IEntity entidade, String mensagemSuc, String mensagemErro) {
        boolean ret = true;
        String strEntidade = "" + entidade;
        try {
            ret = CRUDGenericController.this.isValidPreSave(entidade);
            if (ret) {
                this.getGenericoBC().delete(entidade);
                addMensagem(mensagemSuc + ": " + strEntidade);
            } else {
                addAlerta(mensagemErro + ": " + strEntidade);
            }
        } catch (DataAccessException | ConstraintViolationException dex) {
            log.debug("Fail on hard delete, try soft if possible");
            try {
                if (entidade instanceof ILightRemoveEntity) {
                    this.getGenericoBC().deleteSoft((ILightRemoveEntity) entidade);
                    addMensagem(mensagemSuc + ": " + strEntidade);
                } else {
                    addErro(mensagemErro + ": " + strEntidade, dex);
                    ret = false;
                }
            } catch (RuntimeException e) {
                addErro(mensagemErro + ": " + strEntidade, e);
                ret = false;
            }
        } catch (RuntimeException e) {
            addErro(mensagemErro + ": " + strEntidade, e);
            ret = false;
            log.error("erro", e);
        }
        return ret;
    }

    public boolean validatePreRemove(IEntity entidade) {
        return true;
    }

    public void setEntity(T entidade) {
        this.entity = entidade;
    }

    public List subAllEntities(String attribute) {
        String nameSub = "subAll-" + attribute;
        List list = this.CACHE.get(nameSub);
        if (list == null || list.isEmpty()) {

            List<T> collections = this.getAllEntities();
            Collection sets = subAttributeCollection(collections, attribute);
            list = new ArrayList(sets);
            this.CACHE.put(nameSub, list);
        }
        return list;
    }

    public Collection subAttributeCollection(List<T> collections, String attribute) {
        Set sets = new LinkedHashSet();
        for (IEntity e : collections) {
            try {
                Object property = UtilReflection.getProperty(e, attribute);
                Object var = property;
                if (property != null) {
                    try {
                        if (property instanceof IEntity) {
                            var = this.getGenericoBC().loadEntity((IEntity) property);
                        }
                    } catch (Exception ex) {
                        log.debug("Fail on load fetch subentities", ex);
                    }
                    sets.add(var);
                }
            } catch (Exception ex) {
                log.debug("Fail on load subentities", ex);
            }
        }
        return sets;
    }

    public List subEntities(String attribute) {
        String nameSub = "sub-" + attribute;
        List list = this.CACHE.get(nameSub);
        if (list == null || list.isEmpty()) {
            Collection sets = subAttributeCollection(this.getEntities(), attribute);
            list = new ArrayList(sets);
            this.CACHE.put(nameSub, list);
        }
        return list;
    }

    public List<T> getEntities() {
        return getQueryResult().getResult();
    }

    public boolean save(IEntity entidade) {
        return this.save(entidade, "Salvo com sucesso", "Verifique todos os campos obrigatórios");
    }

    public boolean save(IEntity entidade, String mensagemSuc, String mensagemErro) {
        boolean ret = true;
        try {
            ret = CRUDGenericController.this.isValidPreSave(entidade);
            if (ret) {
                this.getGenericoBC().saveEntity(entidade);
                addMensagem(mensagemSuc);
            } else {
                addAlerta(mensagemErro);
            }
        } catch (RuntimeException e) {
            addErro("Falha ao salvar: " + e.getMessage(), e);
            ret = false;
            log.error("erro", e);
        }
        return ret;
    }

    public boolean isValidPreSave() {
        this.validPreSave = true;
        validatePreSave(entity);
        return this.validPreSave;
    }

    public boolean isValidPreSave(IEntity entidade) {
        this.validPreSave = true;
        validatePreSave(entidade);
        return this.validPreSave;
    }

    /* Alias for crud */
    public <E extends IEntity> CRUDGenericController<E> crud(Class<E> classEntity) {
        if (classEntity == null) {
            return null;
        }
        CRUDGenericController<E> crud = crud(classEntity.getSimpleName());
        if (crud == null) {
            crud = crudBuild(classEntity.getSimpleName().toLowerCase(), classEntity);
        }
        return crud;
    }

    /**
     * Alias para o metodo crud(Class)
     *
     * @param <E>
     * @param classEntit
     * @return
     */
    public <E extends IEntity> CRUDGenericController<E> c(Class<E> classEntit) {
        return crud(classEntit);
    }

    public CRUDGenericController c(String entityName) {
        return crud(entityName);
    }

    public CRUDGenericController crud(String crudPath) {
        return crud(null, crudPath);
    }

    public CRUDGenericController crud(String alias, Class entityclass) {
        return crud(alias, entityclass.getSimpleName());
    }

    public CRUDGenericController crud(String alias, String entityCrudPath) {
        String entityName = entityCrudPath;
        int indexOf = entityCrudPath.indexOf('.');
        String subpath = null;
        if (indexOf > 0) {
            entityName = entityCrudPath.substring(0, indexOf);
            subpath = entityCrudPath.substring(indexOf + 1, entityCrudPath.length());
        }
        String entityNameLower = entityName.toLowerCase();
        if (UtilValidation.isStringEmpty(alias)) {
            alias = entityNameLower;
        }
        CRUDGenericController tmp = CACHE_CRUDS.get(alias);
        if (tmp == null) {
            DescriptorExposedEntity desc = exposed.getExposedEntity(entityName);
            Class classExposed = null;
            if (desc != null) {
                classExposed = desc.getClassExposed();
                tmp = crudBuild(alias, classExposed);
            } else {
                log.debug("Not found class: " + entityName + " trying atribute");
                Field field = UtilReflection.getDeclaredFieldAscending(this.getEntityClass(), entityName);
                if (field != null) {
                    Class<?> type = field.getType();
                    tmp = crudBuild(entityName, type);
                }
            }
        }
        if (tmp != null) {
            if (subpath != null) {
                return tmp.crud(subpath);
            }
        } else {
            log.debug("fail on create crud: " + entityCrudPath);
        }
        return tmp;
    }

    protected CRUDGenericController crudBuild(String alias, Class classExposed) {
        if (classExposed == null) {
            return null;
        }
        CRUDGenericController tmp = new CRUDGenericController(classExposed) {
            public ICrudEntity getGenericoBC() {
                return CRUDGenericController.this.getGenericoBC();
            }

            protected HttpSession getCurrentSession() {
                return CRUDGenericController.this.getCurrentSession();
            }

            protected HttpServletRequest getCurrentRequest() {
                return CRUDGenericController.this.getCurrentRequest();
            }
        };
        CACHE_CRUDS.put(alias, tmp);
        return tmp;
    }

    public CRUDGenericController<T> crudType(String type) {
        if (type == null) {
            return null;
        }
        String typeLow = type.toLowerCase();
        if (!CACHE_CRUDS.containsKey(typeLow)) {
            Class<T> entityClass1 = CRUDGenericController.this.getEntityClass();
            CRUDGenericController tmpCrud = new CRUDGenericController(entityClass1) {
                final String localtype = typeLow;
                final String normaltype = type;

                public ICrudEntity getGenericoBC() {
                    return CRUDGenericController.this.getGenericoBC();
                }

                protected HttpServletRequest getCurrentRequest() {
                    return CRUDGenericController.this.getCurrentRequest();
                }

                @Override
                public void newEntity() {
                    super.newEntity();
                    try {
                        UtilReflection.setProperty(this.entity, "type", normaltype);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                        log.debug("Fail on set type on variable", ex);
                    }
                }

                @Override
                public void loadAllEntitiesPaged() {
                    this.find();
                }

                @Override
                public Map getFindExtraSearchParams() {
                    Map extraParams = super.getFindExtraSearchParams();
                    extraParams.put("type", localtype);
                    //extraParams.put("ORDER BY", "id DESC");
                    return extraParams;
                }
            };
            //TODO: Do map case insenstive on key
            CACHE_CRUDS.put(typeLow, tmpCrud);
        }
        return crud(typeLow);
    }

    public void loadCrudPathCascade(String crudPath) {
        String attributeName = crudPath;
        int indexOf = crudPath.indexOf('.');
        String subpath = null;
        CRUDGenericController crud = null;
        if (indexOf > 0) {
            attributeName = crudPath.substring(0, indexOf);
            subpath = crudPath.substring(indexOf + 1, crudPath.length());
        }
        Field field = UtilReflection.getDeclaredFieldAscending(this.getEntityClass(), attributeName);
        try {
            IEntity attrValue = (IEntity) UtilReflection.getProperty(this.getEntity(), attributeName);
            if (attrValue != null) {
                if (attrValue.isPersisted()) {
                    //Conflito Aluno e Partner, diferente CRUDs, feito para unificar.
                    crud = this.crud(attributeName.toLowerCase(), (Class<? extends IEntity>) field.getType()).lodEntity(attrValue);
                }
            } //Renovar a entidade para não reutilizar uma entidade cacheada de casos anteriores
            else {
                this.crud(attributeName.toLowerCase(), (Class<? extends IEntity>) field.getType()).newEntity();
            }
            if (subpath != null && crud != null) {
                crud.loadCrudPathCascade(subpath);
            }
        } catch (Exception ex) {
            log.error("Fail on load cascade", ex);
        }
    }

    public void validatePreSave(IEntity entidade) {

    }

    protected void validatePreSave(boolean valid, String msgValid) {
        if (!valid) {
            this.addAlerta(msgValid);
            this.validPreSave = false;
        }
    }

    protected boolean validateNotEmpty(Object object, String campo, String msgValid) {
        boolean ret = true;
        try {
            Object property = UtilReflection.getProperty(object, campo);
            ret = property != null;
            if (ret) {
                if (property instanceof String) {
                    ret = ret && !UtilValidation.isStringEmpty((String) property);
                } else if (property instanceof Collection) {
                    ret = ret && !((Collection) property).isEmpty();
                }
            }
        } catch (Exception ex) {
            ret = false;
            log.warn("Fail on property", ex);
        }
        validatePreSave(ret, msgValid);
        return ret;
    }

    public int getTotalEntities() {
        return queryResult.getCount();
    }

    public PagedQueryResult getQueryResult() {
        if (queryResult == null) {
            loadAllEntitiesPaged();
        }
        return queryResult;
    }

    public boolean hasNextPageQueryResult() {
        return this.getQueryResult().hasNext();
    }

    public void nextPageQueryResult() {
        try {
            if (hasNextPageQueryResult()) {
                this.getQueryResult().next();
                this.getGenericoBC().paginate(this.getQueryResult());
            }
        } catch (Exception e) {
            log.error("Fail in next page query", e);
        }
    }

    public void previousPageQueryResult() {
        try {
            if (hasPreviousPageQueryResult()) {
                this.getQueryResult().previous();
                this.getGenericoBC().paginate(this.getQueryResult());
            }
        } catch (Exception e) {
            log.error("Fail in next page query", e);
        }
    }

    public List<Integer> getWindowPages() {
        return this.getQueryResult().getWindowPages();
    }

    public boolean hasWindowPageQueryResult() {
        return this.getQueryResult().hasWindowPageQueryResult();
    }

    public boolean hasPreviousPageQueryResult() {
        return this.getQueryResult().hasPrevious();
    }

    public void paginate(Integer toPage) {
        try {
            if (toPage == null || toPage < 0 || toPage > getTotalPages()) {
                String msg = "Page out of range: " + toPage + " 0/" + getTotalPages();
                addErro(msg);
                log.error(msg);
                return;
            }
            this.getQueryResult().setPage(toPage);
            this.getGenericoBC().paginate(this.getQueryResult());
        } catch (Exception e) {
            log.error("Fail in next page query", e);
        }
    }

    public int getWindowIni() {
        return this.getQueryResult().getWindowIni();
    }

    public int getWindowEnd() {
        return this.getQueryResult().getWindowEnd();
    }

    public int getCurrentPage() {
        return this.getQueryResult().getPage();
    }

    public int getTotalPages() {
        return this.getQueryResult().getTotalPages();
    }

    protected Integer getInt(String value) {
        Integer i = null;
        try {
            i = Integer.parseInt(value.trim().replaceAll("\\D+", ""));
        } catch (Exception e) {

        }
        return i;
    }

    public Locale getUserLocale() {
        Locale userLocale = null;
        try {
            //http://www.java2s.com/Tutorial/Java/0400__Servlet/LocaleSessionServlet.htm
            userLocale = (Locale) this.session.getAttribute("userLocale");
        } catch (Exception e) {

        }
        if (userLocale == null) {
            userLocale = i18n.pt_BR;
        }
        return userLocale;
    }

    public List<T> saveJsonListObjects() {
        List listObjects = convertJsonToObjects(jsonEntities, entityClass);
        this.jsonEntities = null;
        if (listObjects != null) {
            for (Object o : listObjects) {
                this.saveSilent((IEntity) o);
            }
        }
        return listObjects;
    }

    public List<T> getJsonListObjects() {
        List listObjects = convertJsonToObjects(jsonEntities, entityClass);
        this.jsonEntities = null;
        return listObjects;
    }

    public List convertJsonToObjects(String json, Class classe) {
        List result = null;
        if (UtilValidation.isStringValid(json)) {
            try {
                log.debug("JSON Received");
                log.debug(json);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                result = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, classe));
            } catch (Exception ex) {
                throw new IllegalStateException("Falha ao deserializar json: " + json, ex);
            }
        }
        return result;
    }

    public String msg(String msg) {
        return i18n(msg);
    }

    public String i18n(String msg) {
        return i18n.getMessage(getUserLocale(), msg);
    }

    //For future use
    @Deprecated
    public List<Map<String, String>> getMessages() {
        List<Map<String, String>> mensagens = (List<Map<String, String>>) CACHE_BEAN.get("messages");
        if (mensagens == null) {
            mensagens = new ArrayList<>();
            CACHE_BEAN.put("messages", mensagens);
        }
        //Clear previous?
        //mensagens.clear();
        return mensagens;
    }

    //protected abstract void addMensagem(String title, String detail, String type) ;
    protected void addMensagem(String title, String detail, String type) {
        //Map.of("title", title, "detail", detail, "type", type) error on any atrib null
        this.getMessages().add(UtilCollection.mapOf("title", title, "detail", detail, "type", type));
    }

    //TODO: Setar a mensagem em algum paramêtro da resposta
    public void addMensagem(String mensagem) {
        this.addMensagem(mensagem, null, "info");
    }

    public void addErro(String msg) {
        this.addMensagem(msg, null, "error");
    }

    public void addErro(String msg, Exception e) {
        this.addMensagem(msg, exceptionToDetailMsg(e), "error");
    }

    public void add(String msg) {
        this.addMensagem(msg, null, "info");
    }

    public void addAlerta(String msg) {
        this.addMensagem(msg, null, "warn");
    }

    /*public void loadExtraPropertiesFromRequest(String attrid, String entity) {
        if (UtilValidation.isStringEmpty(attrid)) {
            return;
        }
        //String lowatr = attr.toLowerCase();
        //Object obj = this.extraProperties.get(lowatr);
        Object obj = this.extraProperties.get(entity);

        if (obj != null) {
            return;
        }
        obj = this.getAtributeFromRequest(attrid);        
        this.extraProperties.put(attrid, obj);
    }*/

    public void loadOperationFromRequestIfPresent() {
        Object op = this.getAtributeFromRequest(OPERATION);
        if (op != null) {
            this.operation = op.toString();
        } else if ((op = this.getAtributeFromRequest(OP))
                != null) {
            this.operation = op.toString();
        }
    }

    public void loadEntityFromRequestIfPresent() {
        T entityFromRequest = this.getEntityFromRequest();
        if (entityFromRequest != null) {
            log.debug("Entitty Loade From Request");
            this.entity = entityFromRequest;
        }
    }

    public void loadEntitiesFromRequestIfPresent() {
        List<T> entitiesFromRequest = this.getEntitiesFromRequest();
        if (entitiesFromRequest != null) {
            log.debug("Entities Loaded From Request");
            this.allEntities = entitiesFromRequest;
        }
    }

    public List<T> getEntitiesFromRequest() {
        List<T> ret = null;
        List<Long> idsFromRequest = getIdsFromRequest();
        if (idsFromRequest != null) {
            ret = new ArrayList<>();
            for (Long id : idsFromRequest) {
                try {
                    T loadEntity = this.getGenericoBC().loadEntity(this.getEntityClass(), id);
                    ret.add(loadEntity);
                } catch (Exception e) {
                    log.debug("Falha ao carregar entidade do request", e);
                }
            }
        }
        return ret;
    }

    public T getEntityFromRequest() {
        //Object atributeFromRequest = this.getAtributeFromRequest(ID);
        T ret = null;
        Object idFromRequest = getIdFromRequest();
        if (idFromRequest != null) {
            try {
                ret = this.getGenericoBC().loadEntity(this.getEntityClass(), idFromRequest);
            } catch (Exception e) {
                log.debug("Falha ao carregar entidade do request", e);
            }
        }
        return ret;
    }

    protected <U extends IEntity> U loadEntityFromRequestIfPresent(Class<U> classe, String parameter) {
        U ret = null;
        Long id = this.parseLong(this.getAtributeFromRequest(parameter));
        if (id != null) {
            try {
                ret = this.getGenericoBC().loadEntity(classe, id);
            } catch (Exception e) {
                log.debug("Falha ao carregar entidade do request", e);
            }
        }
        return ret;
    }

    protected Long parseLong(Object att) {
        Long ret = null;
        if (att instanceof Number) {
            if (att instanceof Long) {
                ret = (Long) att;
            } else {
                ret = ((Number) att).longValue();
            }
        } else if (att != null) {
            String parse = att.toString();
            ret = Long.parseLong(parse);
        }
        return ret;
    }

    protected Object getIdFromRequest() {
        return this.parseLong(this.getAtributeFromRequest(ID));
    }

    protected Object getAtributeFromRequest(String nomeAtt) {
        Object ret = null;
        try {
            HttpServletRequest currentRequest = getCurrentRequest();
            if (currentRequest == null) {
                log.debug("No Request Scope");
                return null;
            }
            currentRequest.getAttributeNames();
            Enumeration<String> parameterNames = currentRequest.getParameterNames();

            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                logutil.info(paramName);
                String[] paramValues = currentRequest.getParameterValues(paramName);
                for (int i = 0; i < paramValues.length; i++) {
                    String paramValue = paramValues[i];
                    logutil.info("\t" + paramValue);
                }
            }
            ret = currentRequest.getAttribute(nomeAtt);
            if (ret == null) {
                ret = currentRequest.getParameter(nomeAtt);
            }
        } catch (Exception e) {
            log.warn("Fail on load atrribute from request", e);
        }
        return ret;
    }

    protected Object getAtributeFromSession(String nomeAtt) {
        return this.getCurrentSession().getAttribute(nomeAtt);
    }

    protected Object setAtributeInSession(String nomeAtt, Object obj) {
        this.getCurrentSession().setAttribute(nomeAtt, obj);
        return obj;
    }

    protected void removeAtributeInSession(String nomeAtt) {
        this.getCurrentSession().removeAttribute(nomeAtt);
    }

    protected Object popSession(String nomeBean) {
        Object ret = null;
        ret = this.getAtributeFromSession(nomeBean);
        if (ret != null) {
            removeAtributeInSession(nomeBean);
        }
        return ret;
    }

    protected Object pushSession(String nomeBean, Object obj) {
        Object ret = obj;
        setAtributeInSession(nomeBean, obj);
        return ret;
    }

    protected HttpServletRequest getCurrentRequest() {
        return request;
    }

    protected HttpSession getCurrentSession() {
        return session;
    }

    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    public Long getUserId() {
        Long id = null;
        try {
            id = (Long) this.getAtributeFromSession(ID_USER);
            if (id == null) {
                id = this.getUsuarioLogado().getId();
                this.setAtributeInSession(ID_USER, id);
            }
        } catch (Exception e) {
            log.error("fail on user id recover from session", e);
        }
        return id;
    }

    public String getUserName() {
        String username = null;
        username = (String) this.getAtributeFromSession(USER_NAME_VARIABLE);
        if (username == null) {
            try {
                username = this.getAuthentication().getName();
                this.setAtributeInSession(USER_NAME_VARIABLE, username);
            } catch (Exception e) {

            }
        }
        return username;
    }

    protected List<Role> getUsuarioLogadoPapelGlobal() {
        List<Role> papeis = CACHE.get("getUsuarioLogadoPapelGlobal");

        if (papeis == null) {
            try {
                Object idUsuario = this.getUserId();
                papeis = this.getGenericoBC().queryList(
                        "SELECT o FROM UserLogin u "
                        + "JOIN u.roles o "
                        + "WHERE u.id = ?1", idUsuario
                );
            } catch (Exception e) {
                log.error("Falha ao obter usuario logado", e);
            }
            CACHE.put("getUsuarioLogadoPapelGlobal", papeis);
        }
        return papeis;
    }

    public boolean isMonoInstituicao() {
        Boolean permGlobal = (Boolean) this.getAtributeFromSession("USUARIO_MONO_INSTITUICAO");
        try {
            if (permGlobal == null) {
                if (permGlobal == null) {
                    permGlobal = this.getPermitedOrganizations().size() <= 1;
                    this.setAtributeInSession("USUARIO_MONO_INSTITUICAO", permGlobal);
                }
            }
        } catch (Exception e) {

        }
        if (permGlobal == null) {
            permGlobal = true;
        }
        return permGlobal;
    }

    public boolean isUsuarioPermissaoGlobal() {
        Boolean permGlobal = (Boolean) this.getAtributeFromSession("USUARIO_PERMISSAO_GLOBAL");
        try {
            if (permGlobal == null) {
                permGlobal = !this.getUsuarioLogadoPapelGlobal().isEmpty();
                this.setAtributeInSession("USUARIO_PERMISSAO_GLOBAL", permGlobal);
            }
        } catch (Exception e) {
            log.debug("Fail on check perm globla", e);
        }
        if (permGlobal == null) {
            permGlobal = false;
        }
        return permGlobal;
    }

    public List<Organization> getPermitedOrganizations() {
        return getUsuarioLogadoInstiuicao();
    }

    protected List<Organization> getUsuarioLogadoInstiuicao() {
        List<Organization> papeis = CACHE.get("getUsuarioLogadoInstiuicao");
        if (papeis == null) {
            try {
                if (isUsuarioPermissaoGlobal()) {
                    papeis = this.getGenericoBC().loadCollection(Organization.class);
                } else {
                    Object idUsuario = this.getUserId();
                    papeis = this.getGenericoBC().queryList(
                            "SELECT DISTINCT o FROM UserLogin u "
                            + "JOIN u.organizationRole org "
                            + "JOIN org.organization o "
                            + "WHERE u.id = ?1", idUsuario
                    );
                }
            } catch (Exception e) {
                log.error("Falha ao obter usuario logado", e);
            }
            CACHE.put("getUsuarioLogadoInstiuicao", papeis);
        }
        return papeis;
    }

    protected List<OrganizationRole> getUsuarioLogadoPapeisInstiuicao() {
        List<OrganizationRole> papeis = CACHE.get("getUsuarioLogadoPapeisInstiuicao");
        if (papeis == null) {;
            try {
                Object idUsuario = this.getUserId();
                papeis = this.getGenericoBC().queryList("SELECT o FROM UserLogin u "
                        + "JOIN u.organizationRole o "
                        + "WHERE u.id = ?1", idUsuario);
            } catch (Exception e) {
                log.error("Falha ao obter usuario logado", e);
            }
            CACHE.put("getUsuarioLogadoPapeisInstiuicao", papeis);
        }
        return papeis;
    }

    protected UserLogin getUsuarioLogado() {
        UserLogin user = null;
        try {
            Object idUsuario = this.getAtributeFromSession(ID_USER);
            Object nomeUsuario = this.getAtributeFromSession(USER_NAME_VARIABLE);
            if (idUsuario != null) {
                user = new UserLogin((Long) idUsuario, (String) nomeUsuario);
            } else {
                Authentication authentication = this.getAuthentication();
                user = (UserLogin) authentication.getPrincipal();
                this.setAtributeInSession(ID_USER, user.getId());
                this.setAtributeInSession(USER_NAME_VARIABLE, user.getUserName());
            }
        } catch (Exception e) {
            log.error("Falha ao obter usuario logado", e);
        }
        return user;
    }

    @Transactional
    public void recarregarDados(List<? super IEntity> dados, int pagina, int numeroLinhs) {
        int ini = pagina * numeroLinhs;
        int fim = Math.min((pagina + 1) * numeroLinhs, dados.size() - 1);
        for (int i = ini; i <= fim; i++) {
            try {
                IEntity get = (IEntity) dados.get(i);
                IEntity tmp = this.getGenericoBC().loadEntity(get);
                dados.set(i, tmp);
            } catch (Exception e) {
                log.debug("Falha ao recarregar beans da pagina", e);
            }
        }
    }

    private static final int DEFAULT_BUFFER_SIZE = 512;

    public synchronized static void enviarMultiplosArquivoZipado(String nomeArquivoFinal, String offsetDir, Iterable<String> caminhos) {
        ZipOutputStream output = null;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        try {
//            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = null;//(HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivoFinal + ".zip\"");
            output = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE));
            response.setContentType("application/zip");

            for (String fileId : caminhos) {
                InputStream input = null;
                try {
                    String fileName = fileId;
                    File file = new File(offsetDir, fileId);
                    input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
                    output.putNextEntry(new ZipEntry(fileName));
                    for (int length = 0; (length = input.read(buffer)) > 0;) {
                        output.write(buffer, 0, length);
                    }
                    output.closeEntry();
                } catch (IOException ex) {
                    log.error("", ex);
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException logOrIgnore) {
                            /**/ }
                    }
                }
            }
        } catch (IOException ex) {
            log.error("", ex);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    /**/ }
            }
        }
    }

    public Organization getMonoPermitedInstituicao() {
        Organization ret = (Organization) this.CACHE_BEAN.get("getMonoInstituicao");
        if (ret == null) {
            try {
                List<Organization> permitedOrganizations = this.getPermitedOrganizations();
                if (permitedOrganizations != null && permitedOrganizations.size() == 1) {
                    ret = permitedOrganizations.get(0);
                }
            } catch (Exception e) {

            }
        }
        return ret;
    }

    public CRUDGenericController<T> extraSearchParam(String param, Object value) {
        this.getExtraSearchParams().put(param, value);
        return this;
    }

    public CRUDGenericController<T> extraFetchParam(String... fetch) {
        //TODO: Refatorar esse parametro ou protege-lo
        this.getExtraSearchParams().put("fetch", fetch);
        return this;
    }

    public static String exceptionToDetailMsg(Exception e) {
        String message = "";
        if (e != null) {
            message = e.getMessage();
        }
        return message;
    }

    public boolean isAttr(String nameAtribute) {
        boolean ret = false;
        try {
            ret = (Boolean) this.CACHE_BEAN.get(nameAtribute);
        } catch (Exception e) {

        }
        return ret;
    }

    public Boolean isAttribute(String nameAtribute) {
        Boolean ret = null;
        try {
            ret = (Boolean) this.CACHE_BEAN.get(nameAtribute);
        } catch (Exception e) {

        }
        return ret;
    }

    public void setAttr(String nameAtribute, Object value) {
        try {
            this.CACHE_BEAN.put(nameAtribute, value);
        } catch (Exception e) {

        }
    }

    public Number getCount(String nameCount) {
        Number count = 0;
        try {
            count = (Number) this.CACHE_BEAN.get(nameCount);
        } catch (Exception e) {

        }
        return count;
    }

    public void addCount(String nameCount, Number incOffset) {
        Number count = (Number) this.CACHE_BEAN.get(nameCount);
        if (count != null) {
            if (count instanceof Double) {
                count = ((Double) count) + incOffset.doubleValue();
            } else if (count instanceof Float) {
                count = ((Float) count) + incOffset.floatValue();
            } else {
                count = count.longValue() + incOffset.longValue();
            }
        } else {
            count = incOffset;
        }
        this.CACHE_BEAN.put(nameCount, count);
    }
}
