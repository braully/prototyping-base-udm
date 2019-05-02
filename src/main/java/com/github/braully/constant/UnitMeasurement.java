package com.github.braully.constant;

/**
 *
 * @author braully
 */
public enum UnitMeasurement {

    UNITY("Unity"), KG("Kg"), MT("Meter"), HR("Hour");

    private UnitMeasurement(String descricao) {
        this.descricao = descricao;
    }

    private final String descricao;

    public String getDescricao() {
        return descricao;
    }
}
