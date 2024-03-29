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

import com.github.braully.util.UtilValidation;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Table(schema = "base",
        uniqueConstraints = @UniqueConstraint(name = "uk_partner_partner",
                columnNames = {"fk_partner_target", "fk_partner_source", "type"})
)
@Entity
@Getter
@Setter
public class PartnerPartner extends AbstractEntity implements Serializable {

    @ManyToOne(targetEntity = Partner.class)
    private Partner partnerSource;
    @Basic
    private String type;

    @ManyToOne(targetEntity = Partner.class)
    private Partner partnerTarget;


    /*@Id
    private Long id;
     */
    public PartnerPartner() {

    }

    public void setPartnerTargetSeNull(Partner partner) {
        if (this.partnerTarget == null) {
            this.partnerTarget = partner;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            if (UtilValidation.isStringValid(partnerSource.getName())) {
                sb.append(partnerSource.getName());
            }
        } catch (Exception e) {

        }
        sb.append(" (");
        sb.append(type);
        if (this.isPersisted()) {
            sb.append(" #").append(this.id);
        }
        sb.append(')');
        return sb.toString();
    }
}
