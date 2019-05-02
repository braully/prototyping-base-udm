package com.github.braully.domain.util;

public interface IParameterSystem {

    public String getNome();

    public String getGrupo();

    public String getDescricao();

    public String getNomeClasseParametro();

    public Class getClasseTipo();

    public Object[] getOpcoes();
}
