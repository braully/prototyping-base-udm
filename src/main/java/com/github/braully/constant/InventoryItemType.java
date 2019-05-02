package com.github.braully.constant;

/**
 *
 * @author braullyrocha
 */
public enum InventoryItemType {
//TODO: Translate and unify 
    PRODUTO("Produto"), SERVICO("Serviço");
    private final String descricao;

    private InventoryItemType(String descricao) {
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
