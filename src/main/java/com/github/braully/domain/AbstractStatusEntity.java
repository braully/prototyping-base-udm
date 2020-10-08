package com.github.braully.domain;

import com.github.braully.constant.Attr;
import com.github.braully.persistence.IEntity;
import com.github.braully.persistence.IEntityStatus;
import com.github.braully.persistence.Status;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractStatusEntity extends AbstractEntity implements IEntity, IEntityStatus, Serializable {

    @Attr("hidden")
    @Column(name = "status",
            columnDefinition = "integer default '0'")
    @Enumerated(EnumType.ORDINAL)
    protected Status status = Status.ACTIVE;

    @Transient
//    @Basic
    private Boolean removed;

    public Boolean getRemoved() {
        if (this.removed == null) {
            this.removed = this.status == Status.BLOCKED;
        }
        return this.removed;
    }

    public Boolean getActive() {
        return status == null || status == Status.ACTIVE;
    }

    public void setActive(Boolean active) {
        IEntityStatus.super.setActive(active);
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
