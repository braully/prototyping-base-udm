package com.github.braully.domain;

import com.github.braully.interfaces.IExportableEntity;
import com.github.braully.persistence.IEntity;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import org.apache.commons.validator.routines.checkdigit.CUSIPCheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;

@Data
@MappedSuperclass
public abstract class AbstractExportableEntity implements IEntity, IExportableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true)
    @Basic
    private String uniqueCode;

    @Override
    public String getUniqueCode() {
        return this.uniqueCode;
    }

    @Override
    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Override
    public boolean isPersisted() {
        return this.id != null && this.id > 0;
    }

    public String getCodigoExportacao() {
        StringBuilder sb = new StringBuilder();
        String sigla = getSiglaEntidade();
        Long id = this.getId();
        sb.append(sigla);
        sb.append(id);
        String dv = calcularDigitoVerificador(sigla, id);
        sb.append(dv);
        return sb.toString();
    }

    public boolean validoCodigoExportacao(String strCod) {
        return CUSIPCheckDigit.CUSIP_CHECK_DIGIT.isValid(strCod);
    }

    public Long getIdCodigoExportacao(String strCod) {
        Long id = null;
        try {
            if (strCod != null && validoCodigoExportacao(strCod)) {
                String strId = strCod.replaceAll("\\D+", "");
                strId = strId.substring(0, strId.length() - 1);
                id = Long.parseLong(strId);
            }
        } catch (Exception e) {

        }
        return id;
    }

    public String getSiglaEntidade() {
        String simpleName = this.getClass().getSimpleName();
        return simpleName.replaceAll("[aeiou]", "").toUpperCase();
    }

    public String calcularDigitoVerificador(String sigla, Long id) {
        String dv = null;
        String codigo = null;
        try {
            codigo = sigla + id;
            dv = CUSIPCheckDigit.CUSIP_CHECK_DIGIT.calculate(codigo);
        } catch (CheckDigitException ex) {
            throw new IllegalArgumentException("Falha ao calcular digito verificador para o codigo: " + codigo, ex);
        }
        return dv;
    }
}
