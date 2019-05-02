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

import com.github.braully.domain.AbstractStatusEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.github.braully.constant.PersistenceConstant;
import com.github.braully.persistence.IEntityAuditable;
import com.github.braully.persistence.ISecurityContext;
import com.github.braully.persistence.IUser;
import javax.persistence.Table;

/**
 *
 * @author braully
 */
@MappedSuperclass
@Table(schema = "base")
public abstract class GenericType extends AbstractStatusEntity implements IEntityAuditable {

    @Column(name = "data_cadastro")
    @Temporal(javax.persistence.TemporalType.DATE)
    protected Date dataCadastro;
    @Column(nullable = false, unique = true, length = PersistenceConstant.TAM_STR_MEDIO)
    protected String nome;
    @Column(length = PersistenceConstant.TAM_STR_PEQUENO)
    protected String codigo;
    @Column(length = PersistenceConstant.TAM_STR)
    protected String valor;
    @Column(length = PersistenceConstant.TAM_STR)
    protected String descricao;

    @Column(name = "fk_usuario_alteracao")
    private Long idUsuarioAlteracao;
    @Column(name = "data_alteracao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlteracao;

    public GenericType() {
        this.dataCadastro = new Date();
    }

    public GenericType(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    @Override
    public void setDate(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Override
    public void setIdUser(Long idUsuarioAlteracao) {
        this.idUsuarioAlteracao = idUsuarioAlteracao;
    }

    public Long getIdUsuarioAlteracao() {
        return idUsuarioAlteracao;
    }

    @Override
    public void auditEntity(ISecurityContext isecurityContext) {
        if (isecurityContext != null) {
            IUser usuario = isecurityContext.getUser();
            if (usuario != null && usuario.getId() != null) {
                this.setIdUser(usuario.getId());
            }
        }
        this.setDate(new Date());
    }
}
