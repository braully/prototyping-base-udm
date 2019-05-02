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

import java.util.Date;
import java.util.List;

/**
 *
 * @author braully
 */
public class BankReconciliation {

    private Date dataInicio, dataFim;

    private List<BankReconciliationLine> entradas;

    public BankReconciliation(Date dataInicio, Date dataFim, List<BankReconciliationLine> entradas) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.entradas = entradas;
    }

    /* */
    public List<BankReconciliationLine> getEntradas() {
        return entradas;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public Date getDataInicio() {
        return dataInicio;
    }
}
