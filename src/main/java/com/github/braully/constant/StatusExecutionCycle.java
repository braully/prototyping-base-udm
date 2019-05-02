package com.github.braully.constant;

/**
 *
 * @author braullyrocha
 */
public enum StatusExecutionCycle {

    READY, RUNNING,
    DONE, BLOCKED,
    CANCELED;

    public String getDescricao() {
        return name();
    }

}
