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
package com.github.braully.business;

import com.github.braully.constant.StatusExecutionCycle;
import com.github.braully.domain.Account;
import com.github.braully.domain.util.MoneyTO;
import java.math.BigDecimal;

/**
 *
 * @author braully
 */
public class LineGroup implements Comparable<LineGroup> {

    Account account;
    StatusExecutionCycle status;
    MoneyTO amount;
    long count;
    MoneyTO totalAmount;
    long totalCount;

    public LineGroup(Account conta, MoneyTO valor, Long quantidade, MoneyTO valorTotal, long quantidadeTotal) {
        this.account = conta;
        this.amount = valor;
        if (quantidade != null) {
            this.count = quantidade;
        }
        this.totalAmount = valorTotal;
        this.totalCount = quantidadeTotal;
    }

    public LineGroup(StatusExecutionCycle situacao, MoneyTO valor, Long quantidade, MoneyTO valorTotal, long quantidadeTotal) {
        this.status = situacao;
        this.amount = valor;
        if (quantidade != null) {
            this.count = quantidade;
        }
        this.totalAmount = valorTotal;
        this.totalCount = quantidadeTotal;
    }

    public String getDescricaoSituaca() {
        if (status == null) {
            return null;
        }
        return status.getDescricao();
    }

    public String getDescricaoConta() {
        if (account == null) {
            return null;
        }
        return account.getName();
    }

    public String getDescricaoContaTipo() {
        if (account == null) {
            return null;
        }
        return account.getTypeGL();
    }

    public MoneyTO getValor() {
        return amount;
    }

    public BigDecimal getValorBig() {
        return amount.getValorBig();
    }

    public long getQuantidade() {
        return count;
    }

    @Override
    public int compareTo(LineGroup o) {
        int ret = 0;
        if (o != null) {
            String descricaoConta = this.getDescricaoConta();
            String descricaoConta1 = o.getDescricaoConta();
            if (descricaoConta != null && descricaoConta1 != null) {
                ret = descricaoConta.compareToIgnoreCase(descricaoConta);
            }
            String descricaoSituaca = this.getDescricaoSituaca();
            String descricaoSituaca1 = o.getDescricaoSituaca();
            if (descricaoSituaca != null && descricaoSituaca1 != null) {
                ret = descricaoSituaca.compareToIgnoreCase(descricaoSituaca1);
            }
        }
        return ret;
    }
}
