//
// This file was generated by the JPA Modeler
//
package com.github.braully.domain;

import com.github.braully.constant.Attr;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "sale")
@Getter
@Setter
public class Product extends AbstractEntity implements Serializable {

    @Basic
    String typeProduct;

    @Basic
    String name;

    @Basic
    String typeUnit;

    @Basic
    protected Long unit;

    @Attr("hidden")
    @Basic
    protected Long scaleUnit;

    @ManyToOne(targetEntity = Partner.class)
    Partner manufacturer;

    @Override
    protected String preToString() {
        return name;
    }
}
