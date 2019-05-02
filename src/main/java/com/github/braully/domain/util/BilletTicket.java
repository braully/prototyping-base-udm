package com.github.braully.domain.util;

import com.github.braully.domain.Payment;
import com.github.braully.domain.AbstractEntity;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(schema = "financial")
public class BilletTicket extends AbstractEntity {

    public static final String SIGLA_CLASSE_BOLETO = "BLT";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BilletTicket.class);

    /* */
    private static final long serialVersionUID = 1L;
    /*
     */
    @ManyToOne
    protected Payment payment;
    @Basic
    protected String complementPartner;
    @ManyToOne
    protected BilletTicketAgreement agreement;
    @ManyToOne
    protected FinancialCharge charge;
    @Basic
    protected String instructions;
    @Basic
    protected String instruction1;
    @Basic
    protected String instruction2;
    @Basic
    protected String instruction3;
    @Basic
    protected String instruction4;
    @Basic
    protected String instruction5;
    @Basic
    protected String instruction6;
    @Basic
    protected String instruction7;
    @Basic
    protected String instruction8;
    /* */
    @Temporal(TemporalType.DATE)
    @Basic
    protected Date dateMaturity;
    @Temporal(TemporalType.DATE)
    @Basic
    protected Date dateProcess;
}
