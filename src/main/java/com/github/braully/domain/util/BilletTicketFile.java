package com.github.braully.domain.util;

import com.github.braully.domain.AbstractAuditableEntity;
import com.github.braully.domain.AbstractEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author strike
 */
@Entity
@Table(schema = "legacy", name = "arquivo_remessa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fk_convenio", "numero_sequencial"}))
public class BilletTicketFile extends AbstractAuditableEntity implements Comparable<BilletTicketFile> {

    private String nome;
    @ManyToOne
    //@JoinColumn(name = "fk_convenio")
    private BilletTicketAgreement convenio;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataGeracao;
    @Column(name = "numero_sequencial")
    private Integer numeroSequencial;
    @OneToMany(mappedBy = "remessa", fetch = FetchType.LAZY)
    private Set<BilletTicketFileDetail> remessaBoleto;

    public BilletTicketFile() {
        dataGeracao = new Date();
    }

    public BilletTicketFile(BilletTicketAgreement convenioBoleto, List<BilletTicket> boletos) {
        this();
        this.convenio = convenioBoleto;
        if (boletos != null) {
            for (BilletTicket boletoEmitido : boletos) {
                this.addBoleto(boletoEmitido);
            }
        }
    }

    public BilletTicketFile(String nome, BilletTicketAgreement convenio, Integer numeroSequencial, Set<BilletTicketFileDetail> remessaBoleto) {
        this();
        this.nome = nome;
        this.convenio = convenio;
        this.numeroSequencial = numeroSequencial;
        this.remessaBoleto = remessaBoleto;
    }

    public BilletTicketFile(String nome, BilletTicketAgreement convenio, Date dataGeracao, Integer numeroSequencial, Set<BilletTicketFileDetail> remessaBoleto) {
        this();
        this.nome = nome;
        this.convenio = convenio;
        this.dataGeracao = dataGeracao;
        this.numeroSequencial = numeroSequencial;
        this.remessaBoleto = remessaBoleto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BilletTicketAgreement getConvenio() {
        return convenio;
    }

    public void setConvenio(BilletTicketAgreement convenio) {
        this.convenio = convenio;
    }

    public Integer getNumeroSequencial() {
        return numeroSequencial;
    }

    public void setNumeroSequencial(Integer numeroSequencial) {
        this.numeroSequencial = numeroSequencial;
    }

    public Set<BilletTicketFileDetail> getRemessaBoleto() {
        return remessaBoleto;
    }

    public void setRemessaBoleto(Set<BilletTicketFileDetail> remessaBoleto) {
        this.remessaBoleto = remessaBoleto;
    }

    @Override
    public int compareTo(BilletTicketFile o) {
        int ret = 0;
        try {
            if (o != null) {
                ret = this.numeroSequencial.compareTo(o.numeroSequencial);
            }
        } catch (Exception e) {

        }
        return ret;
    }

    public void addBoleto(BilletTicket boletoEmitido) {
        if (boletoEmitido != null && boletoEmitido.isPersisted()) {
            if (this.remessaBoleto == null) {
                this.remessaBoleto = new HashSet<BilletTicketFileDetail>();
            }
            this.remessaBoleto.add(new BilletTicketFileDetail(boletoEmitido, this, remessaBoleto.size() + 1));
        }
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public List<BilletTicketFileDetail> getRemessaBoletoOrdenados() {
        List<BilletTicketFileDetail> remessas = new ArrayList<>();
        remessas.addAll(this.remessaBoleto);
        Collections.sort(remessas);
        return remessas;
    }

    @Entity
    @Table(schema = "legacy", name = "remessa_boleto")
    public static class BilletTicketFileDetail extends AbstractEntity 
            implements Comparable<BilletTicketFileDetail> {

        @ManyToOne
        protected BilletTicket boleto;
        @ManyToOne
        protected BilletTicketFile remessa;
//    @Enumerated
//    protected SituacaoBoletoRegistrado situacaoBoletoRegistrado;
        protected Integer sequencial;

        public BilletTicketFileDetail() {
        }

        public BilletTicketFileDetail(BilletTicket boleto, BilletTicketFile remessa) {
            this.boleto = boleto;
            this.remessa = remessa;
        }

        public BilletTicketFileDetail(BilletTicket boleto, BilletTicketFile remessa, Integer sequencial) {
            this.boleto = boleto;
            this.remessa = remessa;
            this.sequencial = sequencial;
        }

        public BilletTicket getBoleto() {
            return boleto;
        }

        public void setBoleto(BilletTicket boleto) {
            this.boleto = boleto;
        }

        public BilletTicketFile getRemessa() {
            return remessa;
        }

        public void setRemessa(BilletTicketFile remessa) {
            this.remessa = remessa;
        }

        public Integer getSequencial() {
            return sequencial;
        }

        public void setSequencial(Integer sequencial) {
            this.sequencial = sequencial;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + (this.boleto != null ? this.boleto.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            final BilletTicketFileDetail other = (BilletTicketFileDetail) obj;
            if (this.boleto != other.boleto && (this.boleto == null || !this.boleto.equals(other.boleto))) {
                return false;
            }
//        if (this.remessa != other.remessa && (this.remessa == null || !this.remessa.equals(other.remessa))) {
//            return false;
//        }
//        if (this.sequencial != other.sequencial && (this.sequencial == null || !this.sequencial.equals(other.sequencial))) {
//            return false;
//        }
            return true;
        }

        @Override
        public int compareTo(BilletTicketFileDetail o) {
            int ret = 0;
            try {
                ret = this.sequencial.compareTo(o.sequencial);
            } catch (Exception e) {

            }
            return ret;
        }

        @Override
        public String toString() {
            return sequencial + ": " + boleto;
        }
    }
}
