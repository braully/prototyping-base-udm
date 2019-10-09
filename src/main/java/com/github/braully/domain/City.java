package com.github.braully.domain;

import com.github.braully.interfaces.IFormatable;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"country", "name", "state"}), schema = "base")
@DiscriminatorValue("0")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER, name = "type_id",
        columnDefinition = "smallint default '0'", length = 1)
public class City extends AbstractGlobalEntity implements Serializable, IFormatable {

    @Basic
    private String country;

    @Basic
    private String phoneticName;

    @Basic
    private String name;

    @Basic
    private String state;

    public City() {

    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneticName() {
        return this.phoneticName;
    }

    public void setPhoneticName(String phoneticName) {
        this.phoneticName = phoneticName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isValida() {
        return true;
    }

    public String getNomeFormatado() {
        return this.toString();
    }

    public void setNomeFormatado(String nome) {

    }

    @Override
    public String toString() {
        return format();
    }

    public String format() {
        return name + " (" + state + ')';
    }
}
