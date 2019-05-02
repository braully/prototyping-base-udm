package com.github.braully.domain.util;

import com.github.braully.domain.AbstractEntity;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TagAbstrata extends AbstractEntity implements Serializable, Comparable<TagAbstrata> {

    private static final Logger log = Logger.getLogger(TagAbstrata.class.getName());
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    protected String descricao;

    public TagAbstrata() {
    }

    public TagAbstrata(String descricao) {
        this.descricao = descricao;
    }

    public abstract TagAbstrata getPai();

    public abstract Set<? extends TagAbstrata> getFilhos();

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoFormatada() {
        StringBuilder desc = new StringBuilder();
        try {
            TagAbstrata pai = this.getPai();
            if (pai != null) {
                desc.append(pai.getDescricaoFormatada());
                desc.append("/");
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "", e);
        }
        desc.append(descricao);
        return desc.toString();
    }

    public TagAbstrata[] getFilhosAsArray() {
        TagAbstrata[] ts = null;
        Set<? extends TagAbstrata> filhos = this.getFilhos();
        if (filhos != null) {
            ts = filhos.toArray(new TagAbstrata[0]);
        }
        return ts;
    }

    @Override
    public String toString() {
        return getDescricao();
    }

    public TagAbstrata getRaiz() {
        TagAbstrata raiz = this.getPai();
        while (raiz != null && raiz.getPai() != null) {
            raiz = raiz.getPai();
        }
        return raiz;
    }

    public Set<TagAbstrata> getTodosDecendentes() {
        Set<TagAbstrata> tgs = new HashSet<TagAbstrata>();
        Set<? extends TagAbstrata> filhos = this.getFilhos();
        tgs.addAll(filhos);
        if (filhos != null) {
            for (TagAbstrata t : filhos) {
                tgs.addAll(t.getTodosDecendentes());
            }
        }
        return tgs;
    }

    @Override
    public int compareTo(TagAbstrata arg0) {
        return this.descricao.compareTo(arg0.descricao);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final TagAbstrata other = (TagAbstrata) obj;
        if ((this.descricao == null) ? (other.descricao != null) : !this.descricao.equals(other.descricao)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.descricao != null ? this.descricao.hashCode() : 0);
        return hash;
    }

    public boolean isNovo() {
        return !isPersisted();
    }
}
