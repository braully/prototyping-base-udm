//
// This file was generated by the JPA Modeler
//
package com.github.braully.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = "base")
public class GenericValue extends AbstractEntity implements Serializable {

    //TODO: Implementar Flyweight
    @ManyToOne
    private GenericType genericType;

    @Basic
    private String stringValue;

    @Basic
    private Long intValue;

    public GenericValue() {

    }

    public GenericValue(Object det, Object prop) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Long getIntValue() {
        return this.intValue;
    }

    public void setIntValue(Long intValue) {
        this.intValue = intValue;
    }

    public Boolean isBooleanValue() {
        return Boolean.parseBoolean(stringValue);
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.stringValue = booleanValue.toString();
    }

    public GenericType getGenericType() {
        return genericType;
    }

    public void setGenericType(GenericType genericType) {
        this.genericType = genericType;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Double getDoubleValue() {
        return Double.parseDouble(stringValue);
    }

    public void setDoubleValue(Double doubleValue) {
        this.stringValue = doubleValue.toString();
    }

    public Object getValue() {
        Object value = null;
        if (stringValue != null) {
            value = stringValue;
        } else if (intValue != null) {
            value = intValue;
        }
        return value;
    }
}
