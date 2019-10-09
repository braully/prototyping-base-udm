package com.github.braully.domain.util;

import com.github.braully.domain.AbstractEntity;
import com.github.braully.domain.AccountTransaction;
import com.github.braully.domain.InfoExtra;
import com.github.braully.domain.util.BilletTicketFile.BilletTicketFileDetail;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(schema = "financial")
@DiscriminatorValue("0")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER, name = "type_id",
        columnDefinition = "smallint default '0'", length = 1)
public class BilletTicket extends AbstractEntity {

    public static final String SIGLA_CLASSE_BOLETO = "BLT";
    private static final org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(BilletTicket.class);

    /* */
    private static final long serialVersionUID = 1L;

    public BilletTicket() {

    }

    public BilletTicket(BilletTicket template, AccountTransaction account) {
        this.accountTransaction = account;
        this.instructions = template.instructions;
        this.dateMaturity = template.dateMaturity;
        this.dateProcess = template.dateProcess;
        this.agreement = template.agreement;
        this.charge = template.charge;
    }

    /*
     */
    @ManyToOne
    protected BilletTicketAgreement agreement;
    @ManyToOne
    protected FinancialCharge charge;
    @ManyToOne
    protected AccountTransaction accountTransaction;
    @OneToMany(fetch = FetchType.LAZY)
    protected Set<BilletTicketFileDetail> remessas;
    @Basic
    protected String complementPartner;
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

    @ManyToOne(fetch = FetchType.LAZY)//(cascade = CascadeType.ALL)
    protected InfoExtra infoExtra;
    @Basic
    protected Long lastSucessRegister;
    @Basic
    protected Long lastSucessReturn;

    public String getExtraValue(String key) {
        return null;
    }

    public void setExtraValue(String key, String value) {

    }

    public String getNossoNumeroFormatado() {
        return "" + id;
    }

    public Date getDataVencimento() {
        return dateMaturity;
    }

    public Date getDataAlteracao() {
        return dateProcess;
    }

    public Money getValor() {
        return new Money(this.accountTransaction.getCreditTotal());
    }

    public Long getJurosDiarios() {
        return 0l;
    }

    public Money getValorDesconto() {
        return new Money();
    }

    public Date getDataDesconto() {
        return dateMaturity;
    }

}
