package com.github.braully.constant;

/**
 *
 * @author braully
 */
public enum SituacaoTransaction {
    PREVISTO("Previsto"), EXECUTADO("Executado"), VENCIDO("Vencido"), PAGO("Pago"), RECEBIDO("Recebido");
    String descricao;

    private SituacaoTransaction(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
