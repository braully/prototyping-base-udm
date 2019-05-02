package com.github.braully.app;

import com.github.braully.persistence.GenericDAO;
import com.github.braully.domain.Menu;
import com.github.braully.persistence.ICrudEntity;
import com.github.braully.web.DescriptorExposedEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author braully
 */
@RestController("genericController")
@RequestMapping(path = "/app")
@Controller("genericController")
@Scope("session")
public class GenericController extends CRUDGenericController {

    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GenericController.class);

    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;
    @Resource(name = "securityDAO")
    private SecurityDAO securityDAO;

    @PostConstruct
    public void init() {

    }

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

    protected ICrudEntity getGenericoBC() {
        return genericDAO;
    }

    @RequestMapping(value = {"/user/menu"},
            method = RequestMethod.GET)
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

}
