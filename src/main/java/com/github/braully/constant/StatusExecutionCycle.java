package com.github.braully.constant;

/**
 *
 * @author braullyrocha
 */
public enum StatusExecutionCycle {

    READY("Novo"), RUNNING("Em execução"),
    DONE("Finalizado"), BLOCKED("Pausado"),
    CANCELED("Cancelado");

    String label;

    private StatusExecutionCycle(String label) {
        this.label = label;
    }

    private StatusExecutionCycle() {
    }

    public String getDescricao() {
        return name();
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
