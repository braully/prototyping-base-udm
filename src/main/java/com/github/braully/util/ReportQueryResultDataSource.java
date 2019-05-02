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
package com.github.braully.util;

import java.util.Iterator;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ReportQueryResultDataSource implements JRDataSource {

    private String[] fields;
    private Iterator iterator;
    private Object currentValue;

    public ReportQueryResultDataSource(List list, String[] fields) {
        this.fields = fields;
        this.iterator = list.iterator();
    }

    public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
        int index = getFieldIndex(field.getName());
        if (index > -1) {
            Object[] values = (Object[]) currentValue;
            value = values[index];
        }
        return value;
    }

    public boolean next() throws JRException {
        currentValue = iterator.hasNext() ? iterator.next() : null;
        return (currentValue != null);
    }

    private int getFieldIndex(String field) {
        int index = -1;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].equals(field)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
