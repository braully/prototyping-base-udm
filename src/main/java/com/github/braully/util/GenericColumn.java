package com.github.braully.util;

/**
 *
 * @author braullyrocha
 */
public class GenericColumn {

    String name;
    String property;
    Class type;
    String defaultValue;
    int length;
    boolean groupable;

    public GenericColumn() {
    }

    public GenericColumn(String nomeColuna, String propriedade, Class tipo, String padrao, int tamanho, boolean agrupar) {
        this.name = nomeColuna;
        this.property = propriedade;
        this.type = tipo;
        this.defaultValue = padrao;
        this.length = tamanho;
        this.groupable = agrupar;
    }

    public GenericColumn(String nomeColuna, String propriedade, Class tipo, String padrao) {
        this.name = nomeColuna;
        this.property = propriedade;
        this.type = tipo;
        this.defaultValue = padrao;
    }

    public GenericColumn(String nomeColuna, String propriedade, Class tipo) {
        this.name = nomeColuna;
        this.property = propriedade;
        this.type = tipo;
    }

    public GenericColumn(String nomeColuna, String propriedade, Class tipo, int tamanho) {
        this.name = nomeColuna;
        this.property = propriedade;
        this.type = tipo;
        this.length = tamanho;
    }

    public GenericColumn(String nomeColuna, String propriedade, Class tipo, int tamanho, String padrao) {
        this.name = nomeColuna;
        this.property = propriedade;
        this.type = tipo;
        this.length = tamanho;
        this.defaultValue = padrao;
    }

    public GenericColumn(String nomeColuna, String propriedade) {
        this(nomeColuna, propriedade, String.class);
    }

    public GenericColumn(String nomeColuna, String propriedade, boolean agrgupar) {
        this(nomeColuna, propriedade, String.class);
        this.groupable = agrgupar;
    }

    public String getName() {
        return name;
    }

    public String getProperty() {
        return property;
    }

    public Class getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public int getLength() {
        return length;
    }

    public boolean isGroupable() {
        return groupable;
    }

    String format(Object valor) {
        return valor.toString();
    }

}
