package com.github.braully.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(schema = "financial")
public class Budget extends AbstractExpirableEntity implements Serializable {

    @Basic
    private BigDecimal total;

    @Basic
    private String typePeriod;

    @Transient
    private Double percent;

    @ManyToOne(targetEntity = Account.class)
    private Account account;

    @Basic
    private String typePeriodConsolidation;

    public Budget() {

    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTypePeriod() {
        return this.typePeriod;
    }

    public void setTypePeriod(String typePeriod) {
        this.typePeriod = typePeriod;
    }

    public Double getPercent() {
        return this.percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getTypePeriodConsolidation() {
        return this.typePeriodConsolidation;
    }

    public void setTypePeriodConsolidation(String typePeriodConsolidation) {
        this.typePeriodConsolidation = typePeriodConsolidation;
    }
}
