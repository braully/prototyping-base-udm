package com.github.braully.app;

import com.github.braully.persistence.GenericDAO;
import com.github.braully.domain.Menu;
import com.github.braully.persistence.ICrudEntity;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author braully
 */
@RestController("genericController")
@RequestMapping(path = "/gc")
@Controller("genericController")
@Scope("session")
public class GenericController extends CRUDGenericController {

    protected static final org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(GenericController.class);

    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;
    @Resource(name = "securityService")
    private SecurityService securityDAO;

    @PostConstruct
    public void init() {

    }

    protected ICrudEntity getGenericoBC() {
        return genericDAO;
    }

    @RequestMapping(value = {"/user/menu"},
            method = {RequestMethod.GET, RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    //public String getUserMenusJson() { return UtilConversor.toJsonIgnoreFields(getUserMenus(), "childs"); }
    public List<Menu> getUserMenus() {
        List<Menu> menusUser = (List<Menu>) this.getAtributeFromSession("menus");

        if (menusUser == null) {
            menusUser = securityDAO.menusUser();
            this.setAtributeInSession("menus", menusUser);
        }
        return menusUser;
    }
}
