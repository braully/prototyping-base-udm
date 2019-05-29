package com.github.braully.web.jsf;

import com.github.braully.app.CRUDGenericController;
import com.github.braully.app.EntityRESTfulWS;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.app.SecurityDAO;
import com.github.braully.domain.Menu;
import com.github.braully.persistence.ICrudEntity;
import com.github.braully.persistence.IEntity;
import com.github.braully.web.DescriptorExposedEntity;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author raiz
 */
@Qualifier("genericMB")
@Component("genericMB")
@Scope("view")
public class GenericMB extends CRUDGenericController {

    public static final String NOME_PROPRIEDADE_INDICE_TAB = "tabIndex";

    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;
    @Resource(name = "securityDAO")
    private SecurityDAO securityDAO;

    private int index;

    @PostConstruct
    public void init() {
        super.init();
        try {
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(NOME_PROPRIEDADE_INDICE_TAB);
            if (value != null && !value.trim().isEmpty()) {
                Integer i = getInt(value);
                if (i != null && i > 0) {
                    index = i;
                }
            }
        } catch (Exception e) {

        }
    }

    public void paginacaoTabelaLazy(PageEvent event) {
        int pagina = event.getPage();
        try {
            DataTable tabela = ((DataTable) event.getSource());
            int numeroLinhs = tabela.getRows();
            List<? super IEntity> dados = (List<? super IEntity>) tabela.getValue();
            recarregarDados(dados, pagina, numeroLinhs);
        } catch (Exception e) {
            log.debug("Falha ao recarregar beans da pagina", e);
            MessageUtilJSF.addAlertaMensagem("Alguns elementos desta pagina não foram carregados por completo, verifique sua conexão!");
        }
    }

    /*
        Codigo ficou duplicando com Generic Controller, inevitavel
     */
    private final Map<String, CRUDGenericController> CACHE_CRUDS = new HashMap<String, CRUDGenericController>();

    public CRUDGenericController crud(String entityName) {
        CRUDGenericController tmp = null;
        String entityNameLower = entityName.toLowerCase();
        tmp = CACHE_CRUDS.get(entityNameLower);
        if (tmp == null) {
            DescriptorExposedEntity desc = EntityRESTfulWS.EXPOSED_ENTITY.get(entityName);
            if (desc != null) {
                Class classExposed = desc.getClassExposed();
                tmp = new CRUDGenericController(classExposed) {
                    @Override
                    protected ICrudEntity getGenericoBC() {
                        return genericDAO;
                    }
                };
                CACHE_CRUDS.put(entityNameLower, tmp);
            }
        }
        return tmp;
    }

    public List<Menu> getUserMenus() {
        List<Menu> menusUser = (List<Menu>) this.getAtributeFromSession("menus");
        if (menusUser == null) {
            menusUser = securityDAO.menusUser();
            this.setAtributeInSession("menus", menusUser);
        }
        return menusUser;
    }

    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    protected ICrudEntity getGenericoBC() {
        return genericDAO;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void reload() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }
}
