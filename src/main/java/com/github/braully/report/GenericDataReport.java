package com.github.braully.report;

import com.github.braully.domain.BinaryFile;
import com.github.braully.persistence.IEntity;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author braully
 */
public class GenericDataReport {

    public BinaryFile template;
    public IEntity entity;
    public List<? extends IEntity> entities = new ArrayList();
    public String outputFileName;
    public Map propsReport = new HashMap();
    public Map propsBean = new HashMap();

    public InputStream getTemplateStream() {
        return template.getStream();
    }

    public String getTemplateName() {
        return template.getName();
    }

    public String getTemplateExtension() {
        return template.getExtensaoArquivo();
    }

    public Object getData() {
        return entity;
    }

    public Collection getCollectionData() {
        return entities;
    }

    public Map<String, Object> getAllProperties() {
        return propsReport;
    }

    public Map getParametros() {
        return propsReport;
    }

    public void mergeParameters(Map parametrosPadrao) {
        if (parametrosPadrao != null) {
            propsReport.putAll(parametrosPadrao);
        }
    }
}
