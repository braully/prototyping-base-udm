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
package com.github.braully.persistence;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author braullyrocha
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class EntityStatus
        extends EntityBasic
        implements IEntityStatus {

    @Column(name = "status",
            columnDefinition = "integer default '0'")
    @Enumerated(EnumType.ORDINAL)
    protected Status status = Status.ACTIVE;

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    @Override
    public boolean isBlocked() {
        return this.status == Status.BLOCKED;
    }

    @Override
    public void setActive(boolean bStatus) {
        setStatus(bStatus ? Status.ACTIVE : Status.BLOCKED);
    }

    @Override
    public void block() {
        this.status = Status.BLOCKED;
    }

    @Override
    public void activate() {
        this.status = Status.ACTIVE;
    }
}
