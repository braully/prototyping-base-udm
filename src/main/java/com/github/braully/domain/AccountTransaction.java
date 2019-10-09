//
// This file was generated by the JPA Modeler
//
package com.github.braully.domain;

import com.github.braully.app.logutil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.github.braully.constant.Attr;
import com.github.braully.constant.Attrs;
import com.github.braully.constant.SituacaoTransaction;
import com.github.braully.interfaces.IOrganiztionEntityDependent;
import com.github.braully.domain.util.Money;
import com.github.braully.util.UtilDate;
import com.github.braully.util.UtilValidation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(schema = "financial")
@DiscriminatorValue("0")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER, name = "type_id",
        columnDefinition = "smallint default '0'", length = 1)
public class AccountTransaction extends AbstractMigrableEntity
        implements Serializable, Comparable<AccountTransaction>, IOrganiztionEntityDependent {

    public static final CurrencyUnit CURRENCY_DEFAULT = Money.CURRENCY_DEFAULT;

    //    @ManyToOne(targetEntity = Organization.class)
    //    protected Organization organization;
    @ManyToOne(targetEntity = FinancialAccount.class)
    protected FinancialAccount financialAccount;

    @ManyToOne(targetEntity = Account.class)
    protected Account account;

    @ManyToOne(targetEntity = Partner.class)
    protected Partner partner;

    @Basic
    protected String observation;

    @Basic
    protected String memo;

    @Basic
    protected Date datePrevist;

    @Basic
    protected Date dateExecuted;

    @Basic
    protected BigDecimal debitTotal;

    @Basic
    @Attrs({
        @Attr({"converter", "converterMonetaryBigDecimal"})
    })
    protected BigDecimal creditTotal;

    @Basic
    protected String typeMethod;

    @Basic
    protected String typeOperation;

    @Basic
    protected String typeTransaction;

    @Basic
    protected BigDecimal valueExecuted;

    @Basic
    protected BigDecimal actualBalance;

    @Attr("hidden")
    @ManyToOne(targetEntity = InfoExtra.class)
    protected InfoExtra infoExtra;

    @ManyToOne(targetEntity = AccountTransaction.class)
    protected AccountTransaction parentTransaction;

    @OneToMany(mappedBy = "parentTransaction", cascade = CascadeType.ALL)
    protected Set<AccountTransaction> childTransactions;

    @Transient
    protected MonetaryAmount total;

    @Attr(name = "label", value = "Situação")
    @Transient
    protected SituacaoTransaction situation;

    @Attr("hidden")
    @Basic
    protected String status;

    public AccountTransaction() {

    }

    public SituacaoTransaction getSituation() {
        try {
            if (situation == null) {
                if (UtilValidation.isNotNull(dateExecuted, valueExecuted)) {
                    situation = SituacaoTransaction.EXECUTADO;
                    if (this.account.isCredit()) {
                        situation = SituacaoTransaction.RECEBIDO;
                    } else if (this.account.isDebit()) {
                        situation = SituacaoTransaction.PAGO;
                    }
                } else if (UtilValidation.isNotNull(datePrevist)) {
                    situation = SituacaoTransaction.PREVISTO;
                    if (UtilDate.isAntesHoje(datePrevist)) {
                        situation = SituacaoTransaction.VENCIDO;
                    }
                }
            }
        } catch (Exception e) {

        }
        return situation;
    }

    //Cached total
    public MonetaryAmount getTotal() {
        if (total == null) {
            total = org.javamoney.moneta.Money.zero(CURRENCY_DEFAULT);
            total = total.add(org.javamoney.moneta.Money.of(creditTotal, CURRENCY_DEFAULT));
            total = total.subtract(org.javamoney.moneta.Money.of(debitTotal, CURRENCY_DEFAULT));
            if (childTransactions != null) {
                for (AccountTransaction at : childTransactions) {
                    total = total.add(at.getTotal());
                }
            }
        }
        return total;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (this.id == null) {
                final AccountTransaction other = (AccountTransaction) obj;
                if (!Objects.equals(this.memo, other.memo)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Attr(name = "organization", value = "financialAccount.organization")
    public Organization getOrganization() {
        try {
            if (this.financialAccount != null) {
                return this.financialAccount.organization;
            }
        } catch (Exception e) {
            logutil.warn("Fail on load organization", e);
        }
        return null;
    }

    @Override
    public int compareTo(AccountTransaction o) {
        try {
            return this.id.compareTo(o.id);
        } catch (Exception e) {
            return 0;
        }
    }

    public void addChildTransaction(AccountTransaction discount) {
        if (this.childTransactions == null) {
            this.childTransactions = new HashSet<>();
        }
        discount.setParentTransaction(this);
        this.childTransactions.add(discount);
    }

    public void setPartnerIfNull(Partner partner) {
        if (this.partner == null) {
            this.partner = partner;
        }
    }

    public Map<String, Object> getMapAllProps() {
        Map<String, Object> map = new HashMap<>();
        map.putAll(cache().map);
        try {
            if (infoExtra != null) {
                map.putAll(infoExtra.getMapAllProps());
            }
        } catch (Exception e) {
            logutil.error("Fail map all properties", e);
        }
        return map;
    }

    //For legacy
    public void setContaBancaria(FinancialAccount conta) {
        this.financialAccount = conta;
    }

    public void setData(Date datePosted) {
        this.dateExecuted = datePosted;
    }

    public void setDescricao(String memo) {
        this.memo = memo;
    }

    public void setValor(BigDecimal valor) {
        this.valueExecuted = valor;
    }

    public void setCodigo(String transId) {
        this.uniqueCode = transId;
    }
}
