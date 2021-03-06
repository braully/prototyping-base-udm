package com.github.braully.interfaces;

import com.github.braully.persistence.IEntity;
import java.util.Date;

/**
 *
 * @author braully
 */
public interface IAuditableEntity extends IEntity {

    void setLastModified(Date lastModified);

    void setUserIdModified(Long userIdModified);

}
