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

import com.github.braully.domain.AccountTransaction;
import com.github.braully.domain.FinancialAccount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author braully
 */
public class CashFlowFinancialAccountTransaction {

    protected List<FinancialAccount> financialAccounts = new ArrayList<FinancialAccount>();
    protected List<AccountTransaction> transactions;
    protected Date dateIni, dateEnd;
    protected BigDecimal balanceIniPeriod, balanceCurrentPeriod, totalDebitPeriod, totalCreditPeriod;

    public CashFlowFinancialAccountTransaction() {
    }

    public CashFlowFinancialAccountTransaction(FinancialAccount caixa, Date dataInicio, Date dataFim,
            MoneyTO saldoInicio, List<AccountTransaction> caixaRegistros) {
        this.financialAccounts.add(caixa);
        this.dateIni = dataInicio;
        this.dateEnd = dataFim;
        this.balanceIniPeriod = saldoInicio.getValorBig();
    }

    public List<FinancialAccount> getFinancialAccounts() {
        return financialAccounts;
    }

    public void setFinancialAccounts(List<FinancialAccount> financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

    public List<AccountTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<AccountTransaction> transactions) {
        this.transactions = transactions;
    }

    public Date getDateIni() {
        return dateIni;
    }

    public void setDateIni(Date dateIni) {
        this.dateIni = dateIni;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public BigDecimal getBalanceIniPeriod() {
        return balanceIniPeriod;
    }

    public void setBalanceIniPeriod(BigDecimal balanceIniPeriod) {
        this.balanceIniPeriod = balanceIniPeriod;
    }

    public BigDecimal getBalanceCurrentPeriod() {
        return balanceCurrentPeriod;
    }

    public void setBalanceCurrentPeriod(BigDecimal balanceCurrentPeriod) {
        this.balanceCurrentPeriod = balanceCurrentPeriod;
    }

    public BigDecimal getTotalDebitPeriod() {
        return totalDebitPeriod;
    }

    public void setTotalDebitPeriod(BigDecimal totalDebitPeriod) {
        this.totalDebitPeriod = totalDebitPeriod;
    }

    public BigDecimal getTotalCreditPeriod() {
        return totalCreditPeriod;
    }

    public void setTotalCreditPeriod(BigDecimal totalCreditPeriod) {
        this.totalCreditPeriod = totalCreditPeriod;
    }

}
