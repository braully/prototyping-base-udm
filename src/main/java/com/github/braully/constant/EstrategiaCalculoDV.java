package com.github.braully.constant;

/**
 *
 * @author strike
 */
public enum EstrategiaCalculoDV {

    MODULO_11("Módulo 11"), MODULO_10("Módulo 10");

    private EstrategiaCalculoDV(String descrciao) {
        this.descrciao = descrciao;
    }

    private final String descrciao;

    public String getDescrciao() {
        return descrciao;
    }

    @Override
    public String toString() {
        return descrciao;
    }
}
