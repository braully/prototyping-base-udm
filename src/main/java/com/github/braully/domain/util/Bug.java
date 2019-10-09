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

import com.github.braully.constant.StatusBug;
import com.github.braully.domain.AbstractStatusEntity;
import com.github.braully.domain.App;
import com.github.braully.domain.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author braullyrocha
 */
@Entity
@Table(name = "bug", schema = "base")
public class Bug extends AbstractStatusEntity implements Serializable {

    @Column(name = "status_bug")
    @Enumerated
    private StatusBug statusBug;
    @Column(name = "num_redmine")
    private String numRedMine;
    @Column(name = "data_criacao")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateCreated;
    @Column(name = "data_finalizacao")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateEnd;
    @ManyToOne(fetch = FetchType.LAZY)
    private App app;
    @OneToOne(fetch = FetchType.LAZY)
    private LogEntryErrorView viewLog;
    @ManyToOne(fetch = FetchType.LAZY)
    private Bug parent;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;
    @Transient
    private List<LogEntryError> messages;

    public Bug() {
        this.statusBug = StatusBug.ABERTO;
        this.dateCreated = new Date();
    }

    public Bug(LogEntryErrorView vis) {
        this();
        this.viewLog = vis;
    }

    public String getNumRedMine() {
        return numRedMine;
    }

    public void setNumRedMine(String numRedMine) {
        this.numRedMine = numRedMine;
    }

    public StatusBug getStatusBug() {
        return statusBug;
    }

    public void setStatusBug(StatusBug statusBug) {
        this.statusBug = statusBug;
    }

    public LogEntryErrorView getVisaoLog() {
        return viewLog;
    }

    public void setVisaoLog(LogEntryErrorView visaoLog) {
        this.viewLog = visaoLog;
    }

    public String getAssunto() {
        StringBuilder sb = new StringBuilder();
        sb.append("Problema em ");
        sb.append(viewLog.getLocation());
        return sb.toString();
    }

    public String getDescricaoFormatada() {
        StringBuilder sb = new StringBuilder();
        sb.append("Problema identificado no projeto ");
        sb.append(viewLog.getApp());
        sb.append(" na versão ");
        sb.append(viewLog.getGreatVersion());
        sb.append(" na revisão ");
        sb.append(viewLog.getLastRevision());
        sb.append(", foram encontradas ");
        sb.append(viewLog.getCountOccurrence());
        sb.append(" ocorrencia(s), na seguinte localização ");
        sb.append(viewLog.getLocation());
        sb.append(".\n");
        return sb.toString();
    }

    public String getNotas() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    public boolean isAberto() {
        return this.statusBug == statusBug.ABERTO;
    }

    public Date getDataCriacao() {
        return dateCreated;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dateCreated = dataCriacao;
    }

    public Date getDataFinalizacao() {
        return dateEnd;
    }

    public void setDataFinalizacao(Date dataFinalizacao) {
        this.dateEnd = dataFinalizacao;
    }

    public Bug getPai() {
        return parent;
    }

    public void setPai(Bug pai) {
        this.parent = pai;
    }

    public Task getTarefa() {
        return task;
    }

    public void setTarefa(Task tarefa) {
        this.task = tarefa;
    }

    public void setDescricao(String descricao) {
        this.description = descricao;
    }

    public String getDescricao() {
        return description;
    }

    public App getProjeto() {
        return app;
    }

    public void setProjeto(App projeto) {
        this.app = projeto;
    }

    public List<LogEntryError> getMensagens() {
        return messages;
    }

    public void setMensagens(List<LogEntryError> mensagens) {
        this.messages = mensagens;
    }

    public List<Task> getCadeiaTarefas() {
        List<Task> tarefas = new ArrayList<Task>();
        if (this.task != null) {
            tarefas.add(this.task);
//            tarefas.addAll(this.task.get());
        }
        return tarefas;
    }

    public void finalizar() {
        if (this.statusBug == StatusBug.ABERTO || this.statusBug == StatusBug.REPORTADO) {
            this.statusBug = StatusBug.FINALIZADO;
            this.dateEnd = new Date();
        } else {
            throw new IllegalStateException("Bug não pode ser finalizado.");
        }
    }

    public void resolver() {
        if (this.statusBug == StatusBug.ABERTO || this.statusBug == StatusBug.REPORTADO) {
            this.statusBug = StatusBug.RESOLVIDO;
            this.dateEnd = new Date();
        } else {
            throw new IllegalStateException("Bug não pode ser resolvido.");
        }
    }

    public boolean isFinalizado() {
        return this.statusBug == StatusBug.FINALIZADO;
    }

    public boolean isReportado() {
        return statusBug == StatusBug.REPORTADO;
    }

    public boolean isResolvido() {
        return statusBug == StatusBug.RESOLVIDO;
    }
}
