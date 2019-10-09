//
// This file was generated by the JPA Modeler
//
package com.github.braully.domain;

import com.github.braully.constant.Attr;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class PurchaseOrderItem extends AbstractEntity implements Serializable {

    @Basic
    private String attribute;

    @Basic
    private String observation;

    @Basic
    private BigDecimal priceUnit;

    @Basic
    private Double quantity;

    @Attr({"hidden", "true"})
    @ManyToOne(targetEntity = PurchaseOrder.class)
    private PurchaseOrder purchaseOrder;

    public PurchaseOrderItem() {

    }

    @Override
    public String toString() {
        return "Item-" + this.id + " " + attribute + " (" + quantity + "x" + priceUnit + ')';
    }

}
