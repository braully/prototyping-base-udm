package com.github.braully.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "base")
@Getter
@Setter
public class LocaleSupported extends AbstractEntity {

    @Basic
    private String language;
    @Basic
    private String country;
    @Basic
    private String variant;
    @Basic
    private String currency;
}
