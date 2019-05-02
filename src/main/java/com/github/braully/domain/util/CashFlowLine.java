package com.github.braully.domain.util;

/**
 *
 * @author braully
 */
public class CashFlowLine {

    private String name;

    private String type;

    private Object value;

    public CashFlowLine() {
    }

    public CashFlowLine(String nome, Object valor) {
        this.name = nome;
        this.value = valor;
    }

    public CashFlowLine(String nome, String tipo, Object valor) {
        this.name = nome;
        this.type = tipo;
        this.value = valor;
    }

    public String getName() {
        return name;
    }

    public void setName(String nome) {
        this.name = nome;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object valor) {
        this.value = valor;
    }

    public String getType() {
        return type;
    }

    public void setType(String tipo) {
        this.type = tipo;
    }
}
