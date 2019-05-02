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

import com.github.braully.constant.ReportType;
import com.github.braully.domain.AbstractStatusEntity;
import com.github.braully.persistence.Status;
import java.util.Date;
import javax.persistence.Column;
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
public class ReportTemplate extends AbstractStatusEntity implements Comparable<ReportTemplate> {

    protected String descricao;
    @Column(name = "nome_arquivo")
    protected String nomeArquivo;
    protected String caminhoLocal;
    protected byte[] relatorio;
    @Enumerated(EnumType.STRING)
    //Trocar por entidade
    @Column(name = "tipo_relatorio", length = 25)
    protected ReportType tipoRelatorio;
    @Column(name = "data_cadastro")
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Transient
    private boolean statico;
    @Transient
    private String classeStatica;

    public ReportTemplate() {
    }

    public ReportTemplate(String descricao, String nomeArquivo, byte[] relatorio, ReportType tipoRelatorio, Status status, Date dataCadastro) {
        this.descricao = descricao;
        this.nomeArquivo = nomeArquivo;
        this.relatorio = relatorio;
        this.tipoRelatorio = tipoRelatorio;
        this.status = status;
        this.dataCadastro = dataCadastro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public byte[] getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(byte[] relatorio) {
        this.relatorio = relatorio;
    }

    public ReportType getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(ReportType tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getExtensaoArquivo() {
        String ret = null;
        if (this.nomeArquivo != null) {
            ret = this.nomeArquivo.substring(this.nomeArquivo.lastIndexOf('.'), this.nomeArquivo.length());
        }
        return ret;
    }

    public String getNomeSaida() {
        String ret = nomeArquivo;
        if (this.nomeArquivo != null) {
            ret = this.nomeArquivo.replaceAll(".jasper", ".pdf");
        }
        return ret;
    }

    @Override
    public int compareTo(ReportTemplate t) {
        int ret = 0;
        if (t != null && this.tipoRelatorio != null) {
            ret = this.tipoRelatorio.compareTo(t.tipoRelatorio);
            if (ret == 0 && this.descricao != null) {
                ret = this.descricao.compareToIgnoreCase(t.descricao);
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
        return caminhoLocal;
    }

    public void setCaminhoLocal(String caminhoLocal) {
        this.caminhoLocal = caminhoLocal;
    }

}
