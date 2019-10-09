package com.github.braully.domain.util;

import com.github.braully.constant.Attr;
import com.github.braully.constant.EstrategiaCalculoDV;
import com.github.braully.domain.AbstractStatusEntity;
import com.github.braully.domain.Bank;
import com.github.braully.domain.FinancialAccount;
import com.github.braully.domain.Organization;
import com.github.braully.interfaces.IOrganiztionEntityDependent;
import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "financial")
@DiscriminatorValue("0")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER, name = "type_id",
        columnDefinition = "smallint default '0'", length = 1)

public class BilletTicketAgreement extends AbstractStatusEntity implements IOrganiztionEntityDependent {
    /* */
    protected static final long serialVersionUID = 1L;
    public static final String CODG_REAL = "9";

    /* //Really necessary?
    @ManyToOne
    protected Organization organization;
     */
    @ManyToOne
    protected FinancialAccount bankAccount;
    @ManyToOne
    protected FinancialCharge charge;
    @Basic
    @Attr({"label", "Código Cedente"})
    protected String codeClient;
    @Basic
    protected String codeAgreement;
    @Basic
    protected String wallet;
    @Basic
    protected String maskNumber;
    @Basic
    protected String instruction;
    @Basic
    protected Boolean defaultAgreement;
    @Basic
    protected Boolean registerRequeried;
    @Basic
    protected String modalVariant;

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
        return codeClient;
    }

    public void setCodClient(String codClient) {
        this.codeClient = codClient;
    }

    public String getCodOperation() {
        return codeAgreement;
    }

    public void setCodOperation(String codOperation) {
        this.codeAgreement = codOperation;
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
        return modalVariant;
    }

    public void setModalidade(String modalidade) {
        this.modalVariant = modalidade;
    }

    @Override
    protected String preToString() {

        return "Convênio " + codeAgreement;
    }

    @Override
    protected String posToString() {
        StringBuilder sb = new StringBuilder();
        if (wallet != null) {
            sb.append(" Carteira ").append(this.wallet);
        }
        if (this.bankAccount != null) {
            sb.append(this.bankAccount.preToString()).append(" - ").append(this.bankAccount.posToString());
        }
        return sb.toString();
    }

    /* Temporario
    TODO: Remover quando estabilizado 
     */
    public String getAgenciaCodCedenteFormatted() {
        StringBuilder sb = new StringBuilder();
        String agencia = this.getAgencia();
        boolean ant = false;
        if (agencia != null && !agencia.isEmpty()) {
            sb.append(agencia);
            ant = true;
        }
        String conta = this.getConta();
        if (conta != null && !conta.isEmpty()) {
            if (ant) {
                sb.append("/");
            }
            sb.append(conta);
            ant = true;
        }
        return sb.toString();
    }

    public String getNumConvenio() {
        return codeClient;
    }

    public String getLocalPagmento() {
        return instruction;
    }

    public String getCedente() {
        return this.getInstituicao() != null ? this.getInstituicao().getNome() : "";
    }

    public String getCarteira() {
        return wallet;
    }

    public String getMoeda() {
        return CODG_REAL;
    }

    public Organization getInstituicao() {
        if (bankAccount == null) {
            return null;
        }
        return bankAccount.getOrganization();
    }

    public Boolean getRegistrado() {
        return true;
    }

    public String getAgencia() {
        return this.bankAccount.getAgencia();
    }

    public String getConta() {
        return this.bankAccount.getConta();
    }

    public FinancialAccount getContaBancaria() {
        return this.bankAccount;
    }

    public String getCodigoPessoa() {
        return this.codeClient;
    }

    public String getMascaraNossoNumero() {
        return this.maskNumber;
    }

    public EstrategiaCalculoDV getEstrategiaDv() {
        return EstrategiaCalculoDV.MODULO_11;
    }

    public String getCodigoPessoaFormatado() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Bank getBanco() {
        return this.bankAccount.getBank();
    }

    public String getNomeArquivoRemessa() {
        return "";
    }

    public String getAgenciaSomenteNumero() {
        return this.bankAccount.getAgenciaSomenteNumero();
    }

    public String getAgenciaSemDv() {
        return this.bankAccount.getAgenciaSemDv();
    }

    public String getAgenciaDv() {
        return this.bankAccount.getAgenciaDv();
    }

    public String getContaDv() {
        return this.bankAccount.getContaDv();
    }

    @Override
    public Organization getOrganization() {
        if (this.bankAccount == null) {
            return null;
        }
        return this.bankAccount.getOrganization();
    }
}
