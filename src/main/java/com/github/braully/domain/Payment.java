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

import com.github.braully.domain.AbstractAuditableEntity;
import com.github.braully.domain.AccountTransaction;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author braully
 */
@Entity
@Table(schema = "financial")
public class Payment extends AbstractAuditableEntity {

    @Basic
    protected BigDecimal amountOriginal;
    @Basic
    protected String descriptionDiscount;
    @Basic
    protected BigDecimal amountDiscount;
    @Basic
    protected String descriptionDiscountExtra;
    @Basic
    protected BigDecimal amountDiscountExtra;
    @ManyToOne
    protected AccountTransaction accountTransaction;

    public BigDecimal getAmountOriginal() {
        return amountOriginal;
    }

    public void setAmountOriginal(BigDecimal amountOriginal) {
        this.amountOriginal = amountOriginal;
    }

    public String getDescriptionDiscount() {
        return descriptionDiscount;
    }

    public void setDescriptionDiscount(String descriptionDiscount) {
        this.descriptionDiscount = descriptionDiscount;
    }

    public BigDecimal getAmountDiscount() {
        return amountDiscount;
    }

    public void setAmountDiscount(BigDecimal amountDiscount) {
        this.amountDiscount = amountDiscount;
    }

    public String getDescriptionDiscountExtra() {
        return descriptionDiscountExtra;
    }

    public void setDescriptionDiscountExtra(String descriptionDiscountExtra) {
        this.descriptionDiscountExtra = descriptionDiscountExtra;
    }

    public BigDecimal getAmountDiscountExtra() {
        return amountDiscountExtra;
    }

    public void setAmountDiscountExtra(BigDecimal amountDiscountExtra) {
        this.amountDiscountExtra = amountDiscountExtra;
    }

    public AccountTransaction getAccountTransaction() {
        return accountTransaction;
    }

    public void setAccountTransaction(AccountTransaction accountTransaction) {
        this.accountTransaction = accountTransaction;
    }
    
    

}
