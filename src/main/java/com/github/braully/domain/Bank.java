package com.github.braully.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bank", schema = "financial")
public class Bank extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /*
     * 
     */
    @Basic
    private String name;
    @Basic
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
