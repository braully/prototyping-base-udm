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
package com.github.braully.domain.util;

import com.github.braully.interfaces.Descritivel;
import com.github.braully.persistence.IEntityStatus;
import com.github.braully.persistence.Status;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author braully
 */
@Entity
@Table(schema = "base")
public class GenericProperty extends GenericType implements Descritivel, IEntityStatus, Comparable<GenericProperty> {

    public GenericProperty() {
        status = Status.ACTIVE;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getDescricao() {
        return this.nome;
    }

    @Override
    public boolean isBlocked() {
        return status == Status.BLOCKED;
    }

    @Override
    public boolean isActive() {
        return status == Status.ACTIVE || status == null;
    }

    @Override
    public void setActive(boolean bStatus) {
        this.status = bStatus ? Status.ACTIVE : Status.BLOCKED;
    }

    @Override
    public void block() {
        this.status = Status.BLOCKED;
    }

    @Override
    public void activate() {
        this.status = Status.ACTIVE;
    }

    @Override
    public void toggleBlock() {
        if (isActive()) {
            block();
        } else {
            activate();
        }
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int compareTo(GenericProperty o) {
        int ret = 0;
        if (o != null) {
            if (this.nome != null) {
                ret = this.nome.compareToIgnoreCase(o.nome);
            }
        }
        return ret;
    }
}
