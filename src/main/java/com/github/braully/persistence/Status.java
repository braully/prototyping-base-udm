package com.github.braully.persistence;

public enum Status {

    ACTIVE("Active"), BLOCKED("Blocked");

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
