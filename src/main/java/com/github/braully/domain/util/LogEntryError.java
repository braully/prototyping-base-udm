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
package com.github.braully.domain.util;

import com.github.braully.domain.AbstractEntity;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author braullyrocha
 */
@Entity
@Table(schema = "base")
public class LogEntryError extends AbstractEntity {

    @Column(length = 30)
    private String app;
    @Column(length = 10)
    private String ver;
    @Column
    private Integer revision;
    @Column(length = 10)
    private String level;
    @Column(length = 150)
    private String file;
    @Column(length = 150)
    private String location;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(length = 255)
    private String message;
    @Column(columnDefinition = "TEXT")
    private String throwable;

    public LogEntryError() {
    }

    public LogEntryError(String app, String version, Integer revision, String level, String file, String location, Date date, String message) {
        this.app = app;
        this.ver = version;
        this.revision = revision;
        this.level = level;
        this.file = file;
        this.location = location;
        this.date = date;
        this.message = message;
    }

    public String getThrowable() {
        return throwable;
    }

    public void setThrowable(String throwable) {
        this.throwable = throwable;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
}
