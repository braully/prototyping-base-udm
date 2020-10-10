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

import com.github.braully.constant.ReportType;
import com.github.braully.domain.AbstractEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author braullyrocha
 */
@Entity
@Table(name = "relatorio_propriedade", schema = "legacy")
public class ReportProperty extends AbstractEntity implements Comparable<ReportProperty> {

    //TODO: Trocar por uma entidade
//    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_relatorio")
    String tipoRelatorio;

    @Column(name = "propriedade_relatorio")
    String propriedadeRelatorio;
    @Column(name = "propriedade_bean")
    String propriedadeBean;
    @Column(name = "propriedade_alternativa_bean")
    String propriedadeAlternativaBean;
    String descricao;
    @Transient
    public Set<String> apelidosPropridade = new HashSet<>();

    public ReportProperty() {
    }

    public ReportProperty(ReportType tipoRelatorio, String propriedadeRelatorio, String propriedadeBean, String descricao) {
        this.tipoRelatorio = tipoRelatorio.name();
        this.propriedadeRelatorio = propriedadeRelatorio;
        this.propriedadeBean = propriedadeBean;
        this.descricao = descricao;
    }

    ReportProperty(Class aClass) {
        this.tipoRelatorio = aClass.getName();
    }

    public String getPropriedadeBean() {
        return propriedadeBean;
    }

    public void setPropriedadeBean(String propriedadeBean) {
        this.propriedadeBean = propriedadeBean;
    }

    public String getPropriedadeRelatorio() {
        return propriedadeRelatorio;
    }

    public void setPropriedadeRelatorio(String propriedadeRelatorio) {
        this.propriedadeRelatorio = propriedadeRelatorio;
    }

    public ReportType getTipoRelatorio() {
        return ReportType.valueOf(tipoRelatorio);
    }

    public void setTipoRelatorio(ReportType tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio.name();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPropriedadeAlternativaBean() {
        return propriedadeAlternativaBean;
    }

    public void setPropriedadeAlternativaBean(String propriedadeAlternativaBean) {
        this.propriedadeAlternativaBean = propriedadeAlternativaBean;
    }

    @Override
    public int compareTo(ReportProperty t) {
        int ret = 0;
        if (t != null && this.tipoRelatorio != null) {
            ret = this.tipoRelatorio.compareTo(t.tipoRelatorio);
            if (ret == 0 && this.descricao != null && t.descricao != null) {
                ret = this.descricao.compareToIgnoreCase(t.descricao);
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return "PropRel{" + tipoRelatorio + "pR=" + propriedadeRelatorio + "->pB=" + propriedadeBean + '}';
    }

    ReportProperty propridade(String propRelatorio) {
        this.propriedadeRelatorio = propRelatorio;
        return this;
    }

    ReportProperty propridadeBean(String propbean) {
        this.propriedadeBean = propbean;
        return this;
    }

    ReportProperty propridadeAlternativaBean(String alt) {
        this.propriedadeAlternativaBean = alt;
        return this;
    }

    ReportProperty nome(String nome) {
        this.descricao = nome;
        return this;
    }

    ReportProperty apelidoPropridade(String apelido) {
        this.apelidosPropridade.add(apelido);
        return this;
    }
}
