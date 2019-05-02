package com.github.braully.domain.util;

import com.github.braully.domain.AbstractStatusEntity;
import com.github.braully.domain.FinancialAccount;
import com.github.braully.domain.Organization;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = "financial")
public class BilletTicketAgreement extends AbstractStatusEntity {

    private static final long serialVersionUID = 1L;
    /* */

    @ManyToOne
    private Organization organization;
    @ManyToOne
    private FinancialAccount bankAccount;
    @ManyToOne
    private FinancialCharge charge;
    @Basic
    private String codClient;
    @Basic
    private String codOperation;
    @Basic
    private String wallet;
    @Basic
    private String maskNumber;
    @Basic
    private String instruction;
    @Basic
    private Boolean defaultAgreement;
    @Basic
    private Boolean registerRequeried;
    @Basic
    private String modalidade;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public FinancialAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(FinancialAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public FinancialCharge getCharge() {
        return charge;
    }

    public void setCharge(FinancialCharge charge) {
        this.charge = charge;
    }

    public String getCodClient() {
        return codClient;
    }

    public void setCodClient(String codClient) {
        this.codClient = codClient;
    }

    public String getCodOperation() {
        return codOperation;
    }

    public void setCodOperation(String codOperation) {
        this.codOperation = codOperation;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getMaskNumber() {
        return maskNumber;
    }

    public void setMaskNumber(String maskNumber) {
        this.maskNumber = maskNumber;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Boolean getDefaultAgreement() {
        return defaultAgreement;
    }

    public void setDefaultAgreement(Boolean defaultAgreement) {
        this.defaultAgreement = defaultAgreement;
    }

    public Boolean getRegisterRequeried() {
        return registerRequeried;
    }

    public void setRegisterRequeried(Boolean registerRequeried) {
        this.registerRequeried = registerRequeried;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

}
