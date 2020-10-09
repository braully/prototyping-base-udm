/*
 Copyright 2109 Braully Rocha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 */
package com.github.braully.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author braullyrocha
 */
@Entity
//@Table(name = "vw_log_consolidated", schema = "base")
@Table(schema = "base")
public class LogEntryErrorView implements Serializable {

    @Id
    private int id;
    @Basic
    private String app;
    @Basic
    private String location;
    @Column
    private int countOccurrence;
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastOccurrence;
    @Column
    private String greatVersion;
    @Column
    private Long lastRevision;

    public LogEntryErrorView() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCountOccurrence() {
        return countOccurrence;
    }

    public void setCountOccurrence(int countOccurrence) {
        this.countOccurrence = countOccurrence;
    }

    public Date getLastOccurrence() {
        return lastOccurrence;
    }

    public void setLastOccurrence(Date lastOccurrence) {
        this.lastOccurrence = lastOccurrence;
    }

    public String getGreatVersion() {
        return greatVersion;
    }

    public void setGreatVersion(String greatVersion) {
        this.greatVersion = greatVersion;
    }

    public Long getLastRevision() {
        return lastRevision;
    }

    public void setLastRevision(Long lastRevision) {
        this.lastRevision = lastRevision;
    }
}
