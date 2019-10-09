package com.github.braully.constant;

/**
 *
 * @author braullyrocha
 */
public enum PriorityLevel {

    LOW("Baixo"), NORMAL("Normal"), HIGH("Alta");
    String descricao;

    private PriorityLevel(String descricao) {
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
