package com.github.braully.domain;

import com.github.braully.persistence.IEntity;
import com.github.braully.persistence.IEntityStatus;
import com.github.braully.persistence.Status;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractStatusEntity implements IEntity, IEntityStatus, Serializable {

    @Column(name = "status",
            columnDefinition = "integer default '0'")
    @Enumerated(EnumType.ORDINAL)
    protected Status status = Status.ACTIVE;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Transient
//    @Basic
    private Boolean removed;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getRemoved() {
        return this.removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    @Override
    public boolean isPersisted() {
        return this.id != null && this.id > 0;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
}
