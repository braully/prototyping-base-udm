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
package com.github.braully.util;

import com.github.braully.persistence.IEntity;
import java.util.Map;

/**
 *
 * @author braully
 * @param <T>
 */
public class GenericTO<T extends IEntity> {

    T entidade;
    private boolean selecionado;
    private String descricao;
    private Map<Object, Object> valores;

    public GenericTO() {
    }

    public GenericTO(T entidade) {
        this.entidade = entidade;
    }

    public GenericTO(T entidade, boolean selecionado) {
        this.entidade = entidade;
        this.selecionado = selecionado;
    }

    public GenericTO(T entidade, boolean selecionado, String descricao, Map<Object, Object> valores) {
        this.entidade = entidade;
        this.selecionado = selecionado;
        this.descricao = descricao;
        this.valores = valores;
    }

    public T getEntidade() {
        return entidade;
    }

    public void setEntidade(T entidade) {
        this.entidade = entidade;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Map<Object, Object> getValores() {
        return valores;
    }

    public void setValores(Map<Object, Object> valores) {
        this.valores = valores;
    }

    public Long getId() {
        return this.entidade.getId();
    }

    @Override
    public String toString() {
        return entidade != null ? entidade.toString() : "";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.entidade != null ? this.entidade.hashCode() : 0);
        hash = 17 * hash + (this.selecionado ? 1 : 0);
        hash = 17 * hash + (this.descricao != null ? this.descricao.hashCode() : 0);
        hash = 17 * hash + (this.valores != null ? this.valores.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final GenericTO<T> other = (GenericTO<T>) obj;
        if (this.entidade != other.entidade && (this.entidade == null || !this.entidade.equals(other.entidade))) {
            return false;
        }
        if (this.selecionado != other.selecionado) {
            return false;
        }
        if ((this.descricao == null) ? (other.descricao != null) : !this.descricao.equals(other.descricao)) {
            return false;
        }
        if (this.valores != other.valores && (this.valores == null || !this.valores.equals(other.valores))) {
            return false;
        }
        return true;
    }
}
