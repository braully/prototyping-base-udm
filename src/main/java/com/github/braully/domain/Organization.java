package com.github.braully.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.github.braully.interfaces.VisualIdentity;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(schema = "base")
public class Organization extends AbstractEntity implements VisualIdentity {

    /*
     *
     */
    @Basic
    private String name;
    @Basic
    private String description;
    @Basic
    private String oficialName;
    @Basic
    private String fiscalCode;
    @ManyToOne
    private BinaryFile logo;
    @ManyToOne
    private Phone phone;
    @ManyToOne
    private Address address;

    public Organization() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOficialName() {
        return oficialName;
    }

    public void setOficialName(String oficialName) {
        this.oficialName = oficialName;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public byte[] getImagem() {
        return logo.getFileBinary();
    }

    @Override
    public String getVisualDescription() {
        return "Logo";
    }
}
