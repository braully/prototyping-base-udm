package com.github.braully.web.jsf;

import com.github.braully.app.CRUDGenericController;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.app.SecurityService;
import com.github.braully.domain.Menu;
import com.github.braully.persistence.ICrudEntity;
import com.github.braully.persistence.IEntity;
import java.io.IOException;
import java.util.List;
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
//public class GenericMB extends CRUDGenericController {
public class GenericMB extends CRUDGenericMB {

    public static final String NOME_PROPRIEDADE_INDICE_TAB = "tabIndex";

    @Resource(name = "genericDAO")
    protected GenericDAO genericDAO;
    @Resource(name = "securityService")
    protected SecurityService securityDAO;

    protected int index;

    @PostConstruct
    @Override
    public void init() {
        super.init();
        /*
        try {
            String value = (String) this.getAtributeFromRequest(NOME_PROPRIEDADE_INDICE_TAB);
            if (value != null && !value.trim().isEmpty()) {
                Integer i = getInt(value);
                if (i != null && i > 0) {index = i;}
            }
        } catch (Exception e) {} 
         */
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
    public List<Menu> getUserMenus() {
        List<Menu> menusUser = (List<Menu>) this.getAtributeFromSession("menus");
        if (menusUser == null) {
            menusUser = securityDAO.menusUser();
            this.setAtributeInSession("menus", menusUser);
        }
        return menusUser;
    }

    @Override
    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    @Override
    protected ICrudEntity getGenericoBC() {
        return genericDAO;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    public void reload() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    @Override
    protected void addMensagem(String title, String detail, String type) {
        MessageUtilJSF.addMensagem(title, detail, type);
    }

    @Override
    public void addAlerta(String msg) {
        MessageUtilJSF.addAlertaMensagem(msg);
    }

    @Override
    public void addErro(String msg, Exception e) {
        MessageUtilJSF.addErroMensagem(msg, e);
    }

    @Override
    public void addErro(String msg) {
        MessageUtilJSF.addErroMensagem(msg);
    }

    @Override
    public void addMensagem(String mensagem) {
        MessageUtilJSF.addMensagem(mensagem);
    }
}
