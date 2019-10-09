package com.github.braully.constant;

/**
 *
 * @author braully
 */
public enum OperationType {
//TODO: Translate and unify 
    ABRIR("Abrir", "Abertura"), 
    FECHAR("Fechar", "Fechamento"), 
    TRANSFERIR("Transferir", "Transferência");
    final String nome, operacao;

    private OperationType(String nome, String operacao) {
        this.nome = nome;
        this.operacao = operacao;
    }

    public String getOperacao() {
        return operacao;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
