package com.github.braully.util;

/**
 *
 * @author braullyrocha
 */
public class GenericValue {

    String nome, valor;

    public GenericValue() {
    }

    public GenericValue(String nome, String valor) {
        this.nome = nome;
        this.valor = valor;
    }

    public GenericValue(GenericColumn det, Object valor) {
        this.nome = det.getName();
        if (valor != null) {
            this.valor = det.format(valor);
        } else {
            valor = "";
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
