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

import com.github.braully.domain.Account;
import com.github.braully.domain.AccountTransaction;
import com.github.braully.domain.FinancialAccount;
import com.github.braully.util.UtilComparator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author braully
 */
public class CashFlow extends CashFlowFinancialAccountTransaction {

    private static final String TIPO_ENTRADA = "ENTRADA";
    private static final String TIPO_SAIDA = "SAIDA";
    private static final String TIPO_SALDO = "SALDO";

    /*
    
     */
    protected List<FinancialPeriod> periods;
    protected Map<Account, MoneyTO[]> credits = new HashMap<>();
    protected Map<Account, MoneyTO[]> debits = new HashMap<>();

    protected MoneyTO[] balanceInis;
    protected MoneyTO[] balanceEnds;
    protected MoneyTO[] totalCredits;
    protected MoneyTO[] totalDebits;

    public CashFlow(List<FinancialAccount> caixas, Date dataInicio, Date dataFim, List<AccountTransaction> transactions) {
        this.transactions = transactions;
        this.dateIni = dataInicio;
        this.dateEnd = dataFim;
        if (caixas != null) {
            this.financialAccounts.addAll(caixas);
        }
        processarRegistrosFinanceiros();
    }

    private void processarRegistrosFinanceiros() {
        this.periods = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(dateIni);
        int mes = c.get(Calendar.MONTH);
        int offsetMes = mes;
        int i = 0;
        while (c.getTime().compareTo(dateEnd) <= 0) {
            mes = c.get(Calendar.MONTH);
            int ano = c.get(Calendar.YEAR);
            FinancialPeriod periodoFinanceiro = new FinancialPeriod(mes, ano);
            periodoFinanceiro.setIndice(i++);
            this.periods.add(periodoFinanceiro);
            c.add(Calendar.MONTH, 1);
        }
        int quantidadePeriodos = this.periods.size();
        this.balanceInis = new MoneyTO[quantidadePeriodos + 1];
        this.balanceEnds = new MoneyTO[quantidadePeriodos + 1];
        this.totalCredits = new MoneyTO[quantidadePeriodos + 1];
        this.totalDebits = new MoneyTO[quantidadePeriodos + 1];

        for (i = 0; i <= quantidadePeriodos; i++) {
            this.balanceInis[i] = new MoneyTO();
            this.balanceEnds[i] = new MoneyTO();
            this.totalCredits[i] = new MoneyTO();
            this.totalDebits[i] = new MoneyTO();
        }

        for (AccountTransaction caixaRegistro : transactions) {
            Date data;
            data = caixaRegistro.getDateExecuted();
            Account conta;
            conta = caixaRegistro.getAccount();
            Account contaPai = null;
            if (conta != null) {
                contaPai = conta.getParentAccount();
            }

            MoneyTO credito = new MoneyTO(caixaRegistro.getCreditTotal());
            MoneyTO debito = new MoneyTO(caixaRegistro.getDebitTotal());

            FinancialPeriod periodo = new FinancialPeriod(data);
            int ordinalMes = periodo.getMes();
            ordinalMes = ordinalMes - offsetMes;
            if (ordinalMes < 0) {
                ordinalMes = ordinalMes + 12;
            }

            if (credito != null && credito.getValor() > 0) {
                addAmountInPeriod(credits, conta, contaPai, ordinalMes, credito);
                if (totalCredits[ordinalMes] == null) {
                    totalCredits[ordinalMes] = new MoneyTO();
                }
                totalCredits[ordinalMes] = totalCredits[ordinalMes].adicionaValorAbsoluto(credito);
                totalCredits[quantidadePeriodos] = totalCredits[quantidadePeriodos].adicionaValorAbsoluto(credito);
            }

            if (debito != null && debito.getValor() > 0) {
                addAmountInPeriod(debits, conta, contaPai, ordinalMes, debito);
                if (totalDebits[ordinalMes] == null) {
                    totalDebits[ordinalMes] = new MoneyTO();
                }
                totalDebits[ordinalMes] = totalDebits[ordinalMes].adicionaValorAbsoluto(debito);
                totalDebits[quantidadePeriodos] = totalDebits[quantidadePeriodos].adicionaValorAbsoluto(debito);
            }
        }

        balanceInis[0] = new MoneyTO();

        for (FinancialAccount cx : financialAccounts) {
            MoneyTO saldoInicial = new MoneyTO();
            balanceInis[0] = balanceInis[0].adiciona(saldoInicial);
            balanceInis[quantidadePeriodos] = balanceInis[0];
        }

        balanceEnds[0] = balanceInis[0].adiciona(totalCredits[0]).subtraiValorAbsoluto(totalDebits[0]);
        balanceEnds[quantidadePeriodos] = balanceEnds[0];

        for (i = 1; i < quantidadePeriodos; i++) {
            balanceInis[i] = balanceEnds[i - 1];
            balanceEnds[i] = balanceInis[i].adiciona(totalCredits[i]).subtraiValorAbsoluto(totalDebits[i]);

            balanceInis[quantidadePeriodos] = balanceInis[quantidadePeriodos].adiciona(balanceInis[i]);
            balanceEnds[quantidadePeriodos] = balanceEnds[quantidadePeriodos].adiciona(balanceEnds[i]);
        }

    }

    private void addAmountInPeriod(Map<Account, MoneyTO[]> entradas, Account conta, Account contaPai, int ordinalMes, MoneyTO credito) {
        MoneyTO[] ents = entradas.get(conta);
        if (ents == null) {
            ents = entradas.get(contaPai);
            if (ents == null) {
                ents = new MoneyTO[periods.size() + 1];
                ents[periods.size()] = new MoneyTO();
                entradas.put(conta, ents);
            }
        }

        if (ents[ordinalMes] == null) {
            ents[ordinalMes] = new MoneyTO();
        }
        ents[ordinalMes] = ents[ordinalMes].adicionaValorAbsoluto(credito);
        ents[periods.size()] = ents[periods.size()].adicionaValorAbsoluto(credito);
    }

    public List<Account> getAccountsCredit() {
        List<Account> ents = new ArrayList<>();
        Set<Account> keySet = this.credits.keySet();
        if (keySet != null) {
            ents.addAll(keySet);
            Collections.sort(ents, new UtilComparator<Account>("numero", "nome", "descricao"));
        }
        return ents;
    }

    public List<Account> getAccountsDebit() {
        List<Account> ents = new ArrayList<>();
        Set<Account> keySet = this.debits.keySet();
        if (keySet != null) {
            ents.addAll(keySet);
            Collections.sort(ents, new UtilComparator<Account>("numero", "nome", "descricao"));
        }
        return ents;
    }

    public List<FinancialPeriod> getPeriodss() {
        return periods;
    }

    public Map<Account, MoneyTO[]> getCredits() {
        return credits;
    }

    public Map<Account, MoneyTO[]> getDebits() {
        return debits;
    }

    public MoneyTO[] getTotalCredits() {
        return totalCredits;
    }

    public MoneyTO[] getTotalDebits() {
        return totalDebits;
    }

    public MoneyTO[] getBalanceInis() {
        return balanceInis;
    }

    public MoneyTO[] getBalanceEnds() {
        return balanceEnds;
    }

    public int getPeriods() {
        if (periods != null) {
            return periods.size();
        }
        return 1;
    }

    public List<CashFlowLine> getLinesRepSynthetic() {
        List<CashFlowLine> entradas = new ArrayList<CashFlowLine>();
        List<Account> contasEntrada = this.getAccountsCredit();
        if (contasEntrada != null) {
            for (Account conta : contasEntrada) {
                if (conta != null) {
                    MoneyTO[] ent = this.credits.get(conta);
                    CashFlowLine fluxoCaixaEntrada = new CashFlowLine(conta.getName(), TIPO_ENTRADA, transformar(ent));
                    entradas.add(fluxoCaixaEntrada);
                }
            }
        }

        contasEntrada = this.getAccountsDebit();
        if (contasEntrada != null) {
            for (Account conta : contasEntrada) {
                if (conta != null) {
                    MoneyTO[] ent = this.debits.get(conta);
                    CashFlowLine fluxoCaixaEntrada = new CashFlowLine(conta.getName(), TIPO_SAIDA, transformar(ent));
                    entradas.add(fluxoCaixaEntrada);
                }
            }
        }

        CashFlowLine fluxoCaixaEntrada = new CashFlowLine("Saldo Inicial", TIPO_SALDO, transformar(balanceInis));
        entradas.add(fluxoCaixaEntrada);

        fluxoCaixaEntrada = new CashFlowLine("Total Entradas", TIPO_SALDO, transformar(totalCredits));
        entradas.add(fluxoCaixaEntrada);

        fluxoCaixaEntrada = new CashFlowLine("Total Saidas", TIPO_SALDO, transformar(totalDebits));
        entradas.add(fluxoCaixaEntrada);

        fluxoCaixaEntrada = new CashFlowLine("Saldo Final", TIPO_SALDO, transformar(balanceEnds));
        entradas.add(fluxoCaixaEntrada);

        return entradas;
    }

    private String[] transformar(MoneyTO[] ent) {
        String[] ret = null;
        if (ent != null) {
            ret = new String[ent.length];
            int i = 0;
            for (MoneyTO m : ent) {
                if (m != null) {
                    ret[i] = m.getDescricaoFormatada();
                } else {
                    ret[i] = "--";
                }
                i++;
            }
        }
        return ret;
    }

    public List<FinancialAccount> getFinancialAccounts() {
        return financialAccounts;
    }
}
