package com.github.braully.domain;

import com.github.braully.constant.TypePeriod;
import com.github.braully.interfaces.IOrganiztionEntityDependent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Entity
@Table(schema = "base")
public class EmploymentContract extends AbstractEntity implements Serializable, GrantedAuthority, IOrganiztionEntityDependent {

    @ManyToOne
    protected Partner employee;

    @ManyToOne
    protected Organization organization;

    @ManyToOne
    protected Role role;

    @ManyToOne
    protected GenericType function;

    @Basic
    protected Date startDate;

    @Basic
    protected Date endDate;

    @Basic
    protected BigDecimal salarayPeriod;

    @Basic
    protected TypePeriod period;

    @Basic
    protected Integer value;

    @Override
    public String getAuthority() {
        return this.organization.getId() + " " + role.getAuthority();
    }

}
