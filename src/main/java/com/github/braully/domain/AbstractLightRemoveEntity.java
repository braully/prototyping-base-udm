//
// This file was generated by the JPA Modeler
//
package com.github.braully.domain;

import com.github.braully.persistence.IEntity;
import com.github.braully.persistence.ILightRemoveEntity;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

@MappedSuperclass
@Where(clause = "removed = false")//jpa hibernate soft delete
public abstract class AbstractLightRemoveEntity
        extends AbstractEntity
        implements IEntity, Serializable, ILightRemoveEntity {

    @Basic
    @Column(columnDefinition = "boolean DEFAULT false")
    @Getter
    @Setter
    protected Boolean removed;

    public void toggleRemoved() {
        if (this.removed == null) {
            this.removed = false;
        }
        this.removed = !this.removed;
    }

    public Boolean getActive() {
        return getRemoved() == null || !getRemoved();
    }

    public void setActive(Boolean set) {
        setRemoved(!set);
    }
}
