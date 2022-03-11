//
// This file was generated by the JPA Modeler
//
/* MIT License
* 
* Copyright (c) 2021 Braully Rocha
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.github.braully.domain;

import com.github.braully.constant.SysRole;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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
//@Access(AccessType.FIELD)
public class Role extends AbstractGlobalEntity implements Serializable, GrantedAuthority {

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

    @Override
    public String toString() {
        return name + " (" + this.id + ")";
    }
}
