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

import com.github.braully.constant.FactorType;
import com.github.braully.constant.TypePeriodInterest;
import com.github.braully.domain.AbstractStatusEntity;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author braully
 */
@Entity
@Table(schema = "financial")
public class FinancialCharge extends AbstractStatusEntity {

    @Basic
    protected String description;
    @Basic
    protected String instructions;
    @Basic
    protected Long factorTrafficTicket;
    @Enumerated
    @Basic
    protected FactorType typeFactorTrafficTicket;
    @Basic
    protected Long factorInterestRating;
    @Enumerated(EnumType.ORDINAL)
    @Basic
    protected FactorType typeFactorInterestRating;
    @Enumerated(EnumType.ORDINAL)
    @Basic
    protected TypePeriodInterest typePeriodInterest;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Long getFactorTrafficTicket() {
        return factorTrafficTicket;
    }

    public void setFactorTrafficTicket(Long factorTrafficTicket) {
        this.factorTrafficTicket = factorTrafficTicket;
    }

    public FactorType getTypeFactorTrafficTicket() {
        return typeFactorTrafficTicket;
    }

    public void setTypeFactorTrafficTicket(FactorType typeFactorTrafficTicket) {
        this.typeFactorTrafficTicket = typeFactorTrafficTicket;
    }

    public Long getFactorInterestRating() {
        return factorInterestRating;
    }

    public void setFactorInterestRating(Long factorInterestRating) {
        this.factorInterestRating = factorInterestRating;
    }

    public FactorType getTypeFactorInterestRating() {
        return typeFactorInterestRating;
    }

    public void setTypeFactorInterestRating(FactorType typeFactorInterestRating) {
        this.typeFactorInterestRating = typeFactorInterestRating;
    }

    public TypePeriodInterest getTypePeriodInterest() {
        return typePeriodInterest;
    }

    public void setTypePeriodInterest(TypePeriodInterest typePeriodInterest) {
        this.typePeriodInterest = typePeriodInterest;
    }

}
