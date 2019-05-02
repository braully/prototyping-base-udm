package com.github.braully.web.jsf;

import com.github.braully.persistence.GenericDAO;
import com.github.braully.domain.AbstractEntity;
import java.io.Serializable;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("converterEntityBD")
@Scope("view")
@Qualifier("converterEntityBD")
public class ConverterEntityDataBase implements Converter, Serializable {

    private static final Logger log = Logger.getLogger(ConverterEntityDataBase.class);
    /**
     *
     */
    @Resource(name = "genericDAO")
    private GenericDAO genericoDAO;

    public ConverterEntityDataBase() {
    }

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        String split[] = arg2.split(":");
        Object o = null;
        if (split.length > 2) {
            String nomeClasse = split[0];
            try {
                Long id = Long.parseLong(split[1]);
                o = genericoDAO.loadEntityFetch(nomeClasse, id);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return o;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component,
            Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return value.getClass().getName() + ":"
                    + String.valueOf(((AbstractEntity) value).getId());
        }
    }
}
