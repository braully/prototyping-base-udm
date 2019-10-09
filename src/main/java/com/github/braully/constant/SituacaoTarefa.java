package com.github.braully.constant;

public enum SituacaoTarefa {

    PLANEJADA("Planejada"), ANDAMENTO("Em andamento"),
    FINALIZADA("Finalizada"), ESPERA("Em espera"),
    CANCELADA("Cancelada");
    String descricao;

    private SituacaoTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
