//
// This file was generated by the JPA Modeler
//
package com.github.braully.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "base")
@Getter
@Setter
public class UserMessage extends AbstractLightRemoveEntity implements Serializable {

    @ManyToOne(targetEntity = UserLogin.class)
    private UserLogin userFrom;

    @Basic
    private String title;

    @Basic
    private String message;

    @Basic
    private Date date;

    @Basic
    private Date dateView;

    @ManyToOne(targetEntity = UserLogin.class)
    private UserLogin userTo;

    public UserMessage() {

    }

}
