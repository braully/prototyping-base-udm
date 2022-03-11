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

import com.github.braully.constant.ReportType;
import com.github.braully.domain.AbstractStatusEntity;
import com.github.braully.persistence.Status;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author user
 */
@Entity
@Table(schema = "base")
@DiscriminatorValue("0")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER, name = "type_id",
        columnDefinition = "smallint default '0'", length = 1)
public class ReportTemplate extends AbstractStatusEntity implements Comparable<ReportTemplate> {

    //TODO: Migrate to BinaryFile
    protected String description;
    protected String name;
    protected String localPath;
    protected byte[] report;
    @Enumerated(EnumType.STRING)
    //Trocar por entidade
    @Column
    protected ReportType reportType;
    @Column
    @Temporal(TemporalType.DATE)
    private Date created;
    @Transient
    private boolean statico;
    @Transient
    private String classeStatica;

    public ReportTemplate() {
    }

    public ReportTemplate(String descricao, String nomeArquivo, byte[] relatorio, ReportType tipoRelatorio, Status status, Date dataCadastro) {
        this.description = descricao;
        this.name = nomeArquivo;
        this.report = relatorio;
        this.reportType = tipoRelatorio;
        this.status = status;
        this.created = dataCadastro;
    }

    public String getDescricao() {
        return description;
    }

    public void setDescricao(String descricao) {
        this.description = descricao;
    }

    public String getNomeArquivo() {
        return name;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.name = nomeArquivo;
    }

    public byte[] getRelatorio() {
        return report;
    }

    public void setRelatorio(byte[] relatorio) {
        this.report = relatorio;
    }

    public ReportType getTipoRelatorio() {
        return reportType;
    }

    public void setTipoRelatorio(ReportType tipoRelatorio) {
        this.reportType = tipoRelatorio;
    }

    public Date getDataCadastro() {
        return created;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.created = dataCadastro;
    }

    public String getExtensaoArquivo() {
        String ret = null;
        if (this.name != null) {
            ret = this.name.substring(this.name.lastIndexOf('.'), this.name.length());
        }
        return ret;
    }

    public String getNomeSaida() {
        String ret = name;
        if (this.name != null) {
            ret = this.name.replaceAll(".jasper", ".pdf");
        }
        return ret;
    }

    @Override
    public int compareTo(ReportTemplate t) {
        int ret = 0;
        if (t != null && this.reportType != null) {
            ret = this.reportType.compareTo(t.reportType);
            if (ret == 0 && this.description != null) {
                ret = this.description.compareToIgnoreCase(t.description);
            }
        }
        return ret;
    }

    public String getClasseStatica() {
        return classeStatica;
    }

    public void setClasseStatica(String classeStatica) {
        this.classeStatica = classeStatica;
    }

    public boolean isStatico() {
        return statico;
    }

    public void setStatico(boolean statico) {
        this.statico = statico;
    }

    public String getCaminhoLocal() {
        return localPath;
    }

    public void setCaminhoLocal(String caminhoLocal) {
        this.localPath = caminhoLocal;
    }

}
