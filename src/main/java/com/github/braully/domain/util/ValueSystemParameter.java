package com.github.braully.domain.util;

import com.github.braully.domain.AbstractStatusEntity;
import com.github.braully.interfaces.IValorParametroSistema;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author braully
 */
@Entity
@Table(schema = "base", name = "valor_parametro_sistema")
public class ValueSystemParameter extends AbstractStatusEntity implements IValorParametroSistema {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ValueSystemParameter.class);
    /**
     */

    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    @Column(name = "nome_classe_parametro")
    private String nomeClasseParametro;
    @Column(name = "grupo_parametro")
    private String grupoParametro;
    @Column(name = "nome_parametro")
    private String nomeParametro;
    @Column(name = "descricao_parametro")
    private String descricaoParametro;
    @Column(name = "nome_classe_valor_parametro")
    private String nomeClasseValorParametro;
    @Column(name = "valor_boolean")
    private Boolean valorBoolean;
    @Column(name = "valor_string")
    private String valorString;
    @Column(name = "valor_inteiro")
    private Integer valorInteiro;
    @Column(name = "valor_fracionario")
    private Double valorFracionario;
    /*
    
     */
    @Transient
    private IParameterSystem cacheParametroSistema;

    public ValueSystemParameter() {
    }

    public ValueSystemParameter(IParameterSystem p) {
        nomeClasseParametro = p.getNomeClasseParametro();
        descricaoParametro = p.getDescricao();
        nomeParametro = p.getNome();
        Class classeTipo = p.getClasseTipo();
        if (classeTipo != null) {
            nomeClasseValorParametro = classeTipo.getName();
        }
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getNomeClasseParametro() {
        return nomeClasseParametro;
    }

    public void setNomeClasseParametro(String nomeClasseParametro) {
        this.nomeClasseParametro = nomeClasseParametro;
    }

    public String getNomeParametro() {
        return nomeParametro;
    }

    public void setNomeParametro(String nomeParametro) {
        this.nomeParametro = nomeParametro;
    }

    @Override
    public String getValorString() {
        return valorString;
    }

    public void setValorString(String valorString) {
        this.valorString = valorString;
    }

    @Override
    public Integer getValorInteiro() {
        return valorInteiro;
    }

    public void setValorInteiro(Integer valorInteiro) {
        this.valorInteiro = valorInteiro;
    }

    @Override
    public Double getValorFracionario() {
        return valorFracionario;
    }

    public void setValorFracionario(Double valorFracionario) {
        this.valorFracionario = valorFracionario;
    }

    public boolean isBolean() {
        return Boolean.class.getName().equals(nomeClasseValorParametro);
    }

    public boolean isString() {
        return String.class.getName().equals(nomeClasseValorParametro);
    }

    public boolean isInteiro() {
        return Integer.class.getName().equals(nomeClasseValorParametro);
    }

    public boolean isFracionario() {
        return Double.class.getName().equals(nomeClasseValorParametro);
    }

    public boolean isEnumeracao() {
        return !(this.isBolean() || this.isString() || this.isInteiro() || this.isFracionario());
    }

    public Object getValorEnum() {
        if (this.valorInteiro != null) {
            return this.getParametro().getOpcoes()[valorInteiro];
        }
        return null;
    }

    public void setValorEnum(Object o) {
        if (o != null) {
            Object[] opcoes = this.getParametro().getOpcoes();
            if (opcoes != null) {
                for (int i = 0; i < opcoes.length; i++) {
                    Object otmp = opcoes[i];
                    if (o.equals(otmp)) {
                        this.valorInteiro = i;
                    }
                }
            }
        } else {
            this.valorInteiro = null;
        }
    }

    @Override
    public IParameterSystem getParametro() {
        if (cacheParametroSistema != null) {
            return cacheParametroSistema;
        }
        try {
            Class classe = Class.forName(nomeClasseParametro);
            return (IParameterSystem) Enum.valueOf(classe, nomeParametro);
        } catch (Exception e) {
            log.error("erro", e);
        }
        return null;
    }

    public String getDescricaoParametro() {
        return descricaoParametro;
    }

    public void setDescricaoParametro(String descricaoParametro) {
        this.descricaoParametro = descricaoParametro;
    }

    public String getNomeClasseValorParametro() {
        return nomeClasseValorParametro;
    }

    public void setNomeClasseValorParametro(String nomeClasseValorParametro) {
        this.nomeClasseValorParametro = nomeClasseValorParametro;
    }

    public Boolean getValorBoolean() {
        return valorBoolean;
    }

    public void setValorBoolean(Boolean valorBoolean) {
        this.valorBoolean = valorBoolean;
    }

    public String getGrupoParametro() {
        return grupoParametro;
    }

    public void setGrupoParametro(String grupoParametro) {
        this.grupoParametro = grupoParametro;
    }

    @Override
    public String toString() {
        return "ValorParametroSistema{" + "nomeParametro=" + nomeParametro + ", valorBoolean=" + valorBoolean + ", valorString=" + valorString + ", valorInteiro=" + valorInteiro + ", valorFracionario=" + valorFracionario + '}';
    }

}
