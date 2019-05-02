package com.github.braully.domain.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "Tag")
@Access(AccessType.FIELD)
@Table(name = "tag", schema = "base")
public class Tag extends TagAbstrata implements Serializable {
    // TODO: Melhorar essa solução, muito porca

    @Transient
    private static Set<Tag> modificadas;
    /*
     * pertinente as funções genericas
     */
    @Transient
    private boolean selecionada;
    @Transient
    private int quantidade;

    /*
     * usado somente no cadastro de tags - TODO: mover esse controle para a tela
     */
    @Transient
    private boolean edita;
    // Objeto apensado, para uso generico
    @Transient
    private Object objeto;
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag pai;
    @OneToMany(mappedBy = "pai", fetch = FetchType.EAGER)
    private Set<Tag> filhos;

    public Tag() {
    }

    public Tag(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public Tag getPai() {
        return pai;
    }

    public void setPai(Tag pai) {
        this.pai = pai;
    }

    @Override
    public Set<Tag> getFilhos() {
        return filhos;
    }

    public void setFilhos(Set<Tag> filhos) {
        this.filhos = filhos;
    }

    public void setSelecionada(boolean selecionada) {
        this.selecionada = selecionada;
    }

    public boolean isSelecionada() {
        return selecionada;
    }

    public static Set<Tag> getModificadas() {
        return modificadas;
    }

    public void novaTag() {
        if (this.filhos == null) {
            this.filhos = new HashSet<Tag>();
        }
        Tag t = new Tag("...");
        t.setPai(this);
        t.edita = true;
        this.filhos.add(t);
    }

    public void addFilho(Tag filho) {
        if (filho != null) {
            if (this.filhos == null) {
                this.filhos = new HashSet<Tag>();
            }
            filho.setPai(this);
            filho.edita = false;
            this.filhos.add(filho);
            filho.editaTag();
        }
    }

    public void editaTag() {
        this.edita = !edita;
        if (modificadas == null) {
            modificadas = new HashSet<Tag>();
        }
        modificadas.add(this);
    }

    public void setEdita(boolean edita) {
        this.edita = edita;
    }

    public boolean isEdita() {
        return edita;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public Tag novoFilho() {
        Tag tf = new Tag();
        this.addFilho(tf);
        return tf;
    }
}
