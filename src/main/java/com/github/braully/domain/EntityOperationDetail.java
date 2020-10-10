/*
 Copyright 2109 Braully Rocha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 
 */
package com.github.braully.domain;

import com.github.braully.domain.AbstractEntity;
import com.github.braully.domain.UserLogin;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author braully
 */
@Entity
@Table(schema = "base")
public class EntityOperationDetail extends AbstractEntity {

    @Basic
    private String classe;
    @Basic
    private String operation;
    @Basic
    private Integer count;
    @ManyToOne(targetEntity = UserLogin.class)
    private UserLogin userLogin;

    public EntityOperationDetail() {
    }

    public EntityOperationDetail(String classe, String operation, UserLogin userLogin, Integer count) {
        this.classe = classe;
        this.operation = operation;
        this.userLogin = userLogin;
        this.count = count;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public UserLogin getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
