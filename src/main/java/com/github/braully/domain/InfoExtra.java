package com.github.braully.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author braully
 */
@Data
@Entity
@Table(schema = "base")
public class InfoExtra extends AbstractEntity {

    // Complex extra properties
    @OneToMany
    @JoinTable(schema = "base")
    private Set<GenericValue> genericValues;

    // Simple extra properties
    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(schema = "base",
            joinColumns = @JoinColumn(name = "fk_info_extra"))
    private Map<String, String> simpleValues = new HashMap<>();

    public InfoExtra() {
    }

    public Set<GenericValue> getGenericValues() {
        return genericValues;
    }

    public void setGenericValues(Set<GenericValue> genericValues) {
        this.genericValues = genericValues;
    }

    public Map<String, String> getExtraValues() {
        return simpleValues;
    }

    public void setExtraValues(Map<String, String> extraValues) {
        this.simpleValues = extraValues;
    }
}
