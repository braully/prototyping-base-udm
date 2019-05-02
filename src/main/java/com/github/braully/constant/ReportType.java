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
package com.github.braully.constant;

/**
 *
 * @author user
 */
//TODO: Translate and unify 
//TODO: Transformar em entidade gerencialvel
public enum ReportType {

    ALUNO("Relatório de Aluno"), TURMA("Relatório de Turma"), BOLETO("Relatório de Boleto"),
    VENDA("Relatório de Venda"), PAGAMENTO("Relatório de Pagamento"), MATRICULA("Relatório de Matrícula"),
    PLANEJAMENTO("Relatório de Plano de Aula"), CONTEUDO("Conteúdo Prova"),
    RECEBIMENTO_BOLETO("RecebimentoPrevisto de Boleto"), CONTAS_RECEBER("Contas a Receber");
    String valor;

    private ReportType(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return valor;
    }
}
