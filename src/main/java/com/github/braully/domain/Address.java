package com.github.braully.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(schema = "base")
public class Address extends AbstractEntity implements Serializable {

    @Basic
    private String zip;

    @OneToMany(targetEntity = Partner.class)
    @JoinTable(schema = "base")
    private List<Partner> partner;

    @ManyToOne(targetEntity = City.class)
    City city;

    @Basic
    private String street;

    @Basic
    private String district;

    @Basic
    private String addressLine1;

    @Basic
    private String addressLine2;

    @Basic
    private String type;

    @Basic
    private String number;

    public Address() {

    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public List<Partner> getPartner() {
        return this.partner;
    }

    public void setPartner(List<Partner> partner) {
        this.partner = partner;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
