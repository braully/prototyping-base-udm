package com.github.braully.app;

import static com.github.braully.app.GenericController.log;
import com.github.braully.domain.UserLogin;
import com.github.braully.persistence.ICrudEntity;
import com.github.braully.persistence.IEntity;
import com.github.braully.persistence.IEntityStatus;
import com.github.braully.persistence.PagedQueryResult;
import com.github.braully.util.UtilReflection;
import com.github.braully.web.DescriptorExposedEntity;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
public abstract class CRUDGenericController<T extends IEntity> {

    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CRUDGenericController.class);
    /* */
    public static final String NAME_PROP_INDEX_TAB = "tabIndex";

    public static final String ID_USER = "user_id";
    public static final String USER_NAME_VARIABLE = "username";

    private final Map<String, List> CACHE = new HashMap<>();

    protected T entity;
    protected Class<T> entityClass;
    protected PagedQueryResult queryResult;
    @Getter
    @Setter
    protected String searchString;
    @Getter
    protected Map<String, Object> extraSearchParams = new HashMap<>();
    @Getter
    @Setter
    protected int index;

    @Autowired(required = false)
    private HttpSession session;

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
                String entityName = entityNameField;
                String strfield = entityNameField;
                Class classExposed = this.entityClass;
                if (entityNameField.contains(".")) {
                    String[] split = entityNameField.split("\\.");
                    entityName = split[0];
                    strfield = split[1];
                }
                DescriptorExposedEntity desc = EntityRESTfulWS.EXPOSED_ENTITY.get(entityName);
                if (desc != null) {
                    classExposed = desc.getClassExposed();
                }

                Field field = classExposed.getDeclaredField(strfield);
                Class<?> type = field.getType();
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
        if (entityClass == null) {
            entityClass = UtilReflection.getGenericTypeArgument(this.getClass(), 0);
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

    public void find() {
        this.queryResult = this.getGenericoBC().genericFulltTextSearchPagedQuery(this.getEntityClass(), searchString, extraSearchParams);
    }

    public void save() {
        if (this.save(entity)) {
            this.loadAllEntities();
            this.newEntity();
        }
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

    public void loadAllEntities() {
        try {
            queryResult = this.getGenericoBC().loadCollectionPagedQuery(this.getEntityClass());
        } catch (Exception e) {
            addErro("Falha ao carregar entidades", e);
            log.error("Falha ao carregar entidades", e);
        }
    }

    public void setEntity(T entidade) {
        this.entity = entidade;
    }

    public List<T> getEntities() {
        return getQueryResult().getResult();
    }

    public boolean save(IEntity entidade) {
        return this.save(entidade, "Salvo com sucesso", "Falha ao salvar");
    }

    public boolean save(IEntity entidade, String mensagemSuc, String mensagemErro) {
        boolean ret = true;
        try {
            ret = validatePreSave(entidade);
            if (ret) {
                this.getGenericoBC().saveEntity(entidade);
                addMensagem(mensagemSuc);
            } else {
                addAlerta(mensagemErro);
            }
        } catch (RuntimeException e) {
            addErro(mensagemErro, e);
            ret = false;
            log.error("erro", e);
        }
        return ret;
    }

    public boolean validatePreSave(IEntity entidade) {
        return true;
    }

    public int getTotalEntities() {
        return queryResult.getCount();
    }

    public PagedQueryResult getQueryResult() {
        if (queryResult == null) {
            loadAllEntities();
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
                this.getGenericoBC().paginate(queryResult);
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

    //TODO: Setar a mensagem em algum paramêtro da resposta
    public void addMensagem(String mensagem) {

    }

    public void addErro(String msg, Exception e) {

    }

    public void add(String msg) {

    }

    public void addErro(String msg) {

    }

    public void addAlerta(String msg) {

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

    protected HttpSession getCurrentSession() {
        return session;
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

    protected UserLogin getUsuarioLogado() {
        UserLogin user = null;
        try {
            Object idUsuario = this.getAtributeFromSession(ID_USER);
            Object nomeUsuario = this.getAtributeFromSession(USER_NAME_VARIABLE);
            if (idUsuario != null) {
                user = new UserLogin((Long) idUsuario, (String) nomeUsuario);
            }
        } catch (Exception e) {
            log.error("Falha ao obter usuario logado", e);
        }
        return user;
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
                    log.error(null, ex);
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
            log.error(null, ex);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    /**/ }
            }
        }
    }
}
