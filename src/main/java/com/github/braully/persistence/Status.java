package com.github.braully.persistence;

public enum Status {

    ACTIVE("Ativo"), BLOCKED("Inativo");

    private final String description;

    private Status(String descricao) {
        this.description = descricao;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
