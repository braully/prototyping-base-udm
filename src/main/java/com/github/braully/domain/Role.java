//
// This file was generated by the JPA Modeler
//
package com.github.braully.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "role", schema = "security")
@Access(AccessType.FIELD)
public class Role extends AbstractEntity implements Serializable, GrantedAuthority {

    //TODO: Refatorar
    public static enum SysRole {
        USER, ADM, MNG;
    }

    public Role() {

    }

    public Role(String name) {
        this.name = name;
    }

    public Role name(String name) {
        this.name = name;
        return this;
    }

    @ManyToOne
    private Role parent;

    @OneToMany(mappedBy = "parent")
    private Set<Role> childs;

    @Basic
    private String name;

    @Basic
    private String description;

    @Basic
    @Enumerated
    private SysRole sysRole;

    @ManyToMany(targetEntity = Menu.class, fetch = FetchType.EAGER)
    @JoinTable(name = "role_menu", schema = "security", joinColumns
            = @JoinColumn(name = "fk_role", referencedColumnName = "id"),
            inverseJoinColumns
            = @JoinColumn(name = "fk_menu", referencedColumnName = "id"))
    private List<Menu> menus;

    public SysRole getSysRole() {
        return sysRole;
    }

    public void setSysRole(SysRole sysRole) {
        this.sysRole = sysRole;
    }

    public Role getParent() {
        return parent;
    }

    public void setParent(Role parent) {
        this.parent = parent;
    }

    public Set<Role> getChilds() {
        return childs;
    }

    public void setChilds(Set<Role> childs) {
        this.childs = childs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Menu> getMenus() {
        return this.menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public Menu[] getMenusArray() {
        Menu[] arr = null;
        Collection<Menu> mns = this.getMenus();
        if (mns != null) {
            arr = this.menus.toArray(new Menu[0]);
        }
        return arr;
    }

    public void setMenusArray(Menu[] mns) {
        Collection<Menu> menus1 = this.getMenus();
        menus1.clear();
        if (mns != null) {
            for (Menu m : mns) {
                menus1.add(m);
            }
        }
    }

    //Spring security    @Override
    public String getAuthority() {
        return this.name;
    }
}
