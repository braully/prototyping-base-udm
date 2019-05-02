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
import com.github.braully.domain.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author braullyrocha
 */
@Entity
@Table(name = "relatorio_propriedade", schema = "base")
public class ReportProperty extends AbstractEntity implements Comparable<ReportProperty> {

    //TODO: Trocar por uma entidade
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_relatorio")
    private ReportType tipoRelatorio;
    @Column(name = "propriedade_relatorio")
    private String propriedadeRelatorio;
    @Column(name = "propriedade_bean")
    private String propriedadeBean;
    @Column(name = "propriedade_alternativa_bean")
    private String propriedadeAlternativaBean;
    private String descricao;

    public ReportProperty() {
    }

    public ReportProperty(ReportType tipoRelatorio, String propriedadeRelatorio, String propriedadeBean, String descricao) {
        this.tipoRelatorio = tipoRelatorio;
        this.propriedadeRelatorio = propriedadeRelatorio;
        this.propriedadeBean = propriedadeBean;
        this.descricao = descricao;
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
        return tipoRelatorio;
    }

    public void setTipoRelatorio(ReportType tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
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
}
