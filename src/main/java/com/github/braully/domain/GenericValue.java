//
// This file was generated by the JPA Modeler
//
/* MIT License
* 
* Copyright (c) 2021 Braully Rocha
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

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
