package com.github.braully.web.jsf;

import com.github.braully.app.exposed;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.persistence.IEntity;
import com.github.braully.web.DescriptorExposedEntity;
import java.io.Serializable;
import java.lang.reflect.Field;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("converterEntityBD")
@Scope("view")
@Qualifier("converterEntityBD")
public class ConverterEntityDataBase implements Converter, Serializable {

    private static final Logger log = LogManager.getLogger(ConverterEntityDataBase.class);
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
        if (split.length >= 2) {
            String nomeClasse = split[0];
            DescriptorExposedEntity exposedEntity = exposed.getExposedEntity(nomeClasse);
            Field exposedEntityField = null;
            try {
                Long id = Long.parseLong(split[1]);
                if (exposedEntity != null) {
                    o = genericoDAO.loadEntityFetch(exposedEntity.getClassExposed(), id);
                } else if (null != (exposedEntityField = exposed.getExposedEntityField(nomeClasse))) {
                    Class type = exposedEntityField.getType();
                    o = genericoDAO.loadEntityFetch(type, id);
                } else {
                    //TODO: Trhows exception if not exposed entity
                    //Raw type, security warning
                    o = genericoDAO.loadEntityFetch(nomeClasse, id);
                }
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
            Class classe = value.getClass();
            try {
                if (value instanceof HibernateProxy) {
                    classe = value.getClass().getSuperclass();
//                    LazyInitializer lazyInitializer
//                            = ((HibernateProxy) value).getHibernateLazyInitializer();
//                    classe = lazyInitializer.getPersistentClass();
                }
            } catch (Exception e) {

            }
            return classe.getName() + ":"
                    + String.valueOf(((IEntity) value).getId());
        }
    }
}
