package com.github.braully.web.jsf;

import com.github.braully.app.CRUDGenericController;
import com.github.braully.app.EntityRESTfulWS;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.app.SecurityDAO;
import com.github.braully.domain.Menu;
import com.github.braully.persistence.ICrudEntity;
import com.github.braully.persistence.IEntity;
import com.github.braully.web.DescriptorExposedEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
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

    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;
    @Resource(name = "securityDAO")
    private SecurityDAO securityDAO;

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
        tmp = CACHE_CRUDS.get(entityName);
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
                CACHE_CRUDS.put(entityName, tmp);
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

}
