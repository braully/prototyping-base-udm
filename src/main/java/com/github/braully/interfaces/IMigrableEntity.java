package com.github.braully.interfaces;

import com.github.braully.persistence.IEntity;

/**
 *
 * @author braully
 */
public interface IMigrableEntity extends IEntity {

    String getUniqueCode();

    void setUniqueCode(String uniqueCode);

}
