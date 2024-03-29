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
/* MIT License
* 
* Copyright (c) 2021 Braully Rocha
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.github.braully.domain;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author braully
 */
public class FinancialPeriod implements Comparable<FinancialPeriod> {

    protected int mes;

    protected int ano;

    protected int indice;

    public FinancialPeriod(Date data) {
        if (data != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(data);
            mes = c.get(Calendar.MONTH);
            ano = c.get(Calendar.YEAR);
        }
    }

    public FinancialPeriod(int mes, int ano) {
        this.mes = mes;
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.mes;
        hash = 53 * hash + this.ano;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final FinancialPeriod other = (FinancialPeriod) obj;
        if (this.mes != other.mes) {
            return false;
        }
        if (this.ano != other.ano) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return mes + "/" + ano;
    }

    @Override
    public int compareTo(FinancialPeriod o) {
        int ret = 0;
        if (o != null) {
            ret = this.ano - o.ano;
            if (ret == 0) {
                ret = this.mes - o.mes;
            }
        }
        return ret;
    }

    public String getDescricaoFormatada() {
        StringBuilder sb = new StringBuilder();
        sb.append(mes);
        sb.append("/");
        sb.append(ano);
        return sb.toString();
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }
}
