package com.github.braully.constant;

import java.util.Arrays;
import java.util.List;

public enum ContactPhoneType {

    //TODO: Translate and unify 
    CELULAR("Celular"), RESIDENCIAL("Residencial"), COMERCIAL("Comercial"), RECADO("Recado"), GERAL("Geral");

    private final String descricao;

    private ContactPhoneType(String descricao) {
        this.descricao = descricao;
    }

    public static List<ContactPhoneType> getTipos() {
        return Arrays.asList(ContactPhoneType.values());
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
