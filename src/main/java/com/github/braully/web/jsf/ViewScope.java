package com.github.braully.web.jsf;

/**
 *
 * @author braully
 */
import java.util.Map;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.web.context.request.AbstractRequestAttributesScope;
import org.springframework.web.context.request.RequestAttributes;

public class ViewScope extends AbstractRequestAttributesScope implements Scope {

    private static final Logger log = Logger.getLogger(ViewScope.class);

    @Override
    public Object get(String name, ObjectFactory objectFactory) {
        Object o = null;
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                Map<String, Object> viewMap = facesContext.getViewRoot().getViewMap();
                if (viewMap.containsKey(name)) {
                    o = viewMap.get(name);
                } else {
                    Object object = objectFactory.getObject();
                    viewMap.put(name, object);
                    o = object;
                }
            } else {
                log.debug("Não existe Contexto JSF ativo, utilizando Spring Request");
                o = super.get(name, objectFactory);
            }
        } catch (Exception e) {
            log.error("Erro ao carregar bean no scopo visao", e);
        }
        return o;
    }

    @Override
    public Object remove(String name) {
        Object o = null;
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                o = facesContext.getViewRoot().getViewMap().remove(name);
            } else {
                o = super.remove(name);
            }
        } catch (Exception e) {
            log.error("Erro ao remover bean no scopo visao", e);
        }
        return o;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        if (FacesContext.getCurrentInstance() == null) {
            super.registerDestructionCallback(name, callback);
        }
        //Not supported
    }

    public Object resolveContextualObject(String key) {
        // Unsupported feature
        return null;
    }

    public String getConversationId() {
        // Unsupported feature
        return null;
    }

    @Override
    protected int getScope() {
        return RequestAttributes.SCOPE_REQUEST;
    }
}
