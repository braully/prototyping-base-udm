package com.github.braully.web.jsf;

/**
 *
 * @author braully
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.Scope;
import org.springframework.web.context.request.AbstractRequestAttributesScope;
import org.springframework.web.context.request.RequestAttributes;

public class ViewScope extends AbstractRequestAttributesScope implements Scope {

    private static final Logger log = LogManager.getLogger(ViewScope.class);

    //Not Working, bean not injected
    @Value("${standalone}")
    String standalone;

    //https://www.baeldung.com/spring-custom-scope
    private Map<String, Object> scopedObjects
            = Collections.synchronizedMap(new HashMap<String, Object>());
    private Map<String, Runnable> destructionCallbacks
            = Collections.synchronizedMap(new HashMap<String, Runnable>());

    @Override
    public Object get(String name, ObjectFactory objectFactory) {
        Object o = null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            Map<String, Object> viewMap = null;
            if (facesContext.getViewRoot() != null) {
                viewMap = facesContext.getViewRoot().getViewMap();
            }
            if (viewMap == null) {
                o = super.get(name, objectFactory);
            } else if (viewMap.containsKey(name)) {
                o = viewMap.get(name);
            } else {
                Object object = objectFactory.getObject();
                viewMap.put(name, object);
                o = object;
            }
        } else { //Para fins de testes e execuções desktops de beans scope("view")
            try {
                log.debug("Não existe Contexto JSF ativo, utilizando Spring Request");
                o = super.get(name, objectFactory);
            } catch (Exception e) {
                log.debug("Não existe Contexto JSF ativo, usando local");
                if (scopedObjects.containsKey(name)) {
                    o = scopedObjects.get(name);
                } else {
                    o = scopedObjects.put(name, objectFactory.getObject());
                }
                //log.error("Erro ao carregar bean no scopo visao", e);
            }

        }

        return o;
    }

    @Override
    public Object remove(String name) {
        Object o = null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            o = facesContext.getViewRoot().getViewMap().remove(name);
        }
        try {
            log.debug("Não existe Contexto JSF ativo, utilizando Spring Request");
            o = super.remove(name);
        } catch (Exception e) {
            destructionCallbacks.remove(name);
            o = scopedObjects.remove(name);
            log.debug("Não existe Contexto JSF ativo, usando local");
        }
        return o;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        if (FacesContext.getCurrentInstance() == null) {
            try {
                log.debug("Não existe Contexto JSF ativo, utilizando Spring Request");
                super.registerDestructionCallback(name, callback);
            } catch (Exception e) {
                this.destructionCallbacks.put(name, callback);
                log.debug("Não existe Contexto JSF ativo, usando local");
            }
        }
        //Not supported
    }

    public Object resolveContextualObject(String key) {
        // Unsupported feature
        return null;
    }

    public String getConversationId() {
        try {
            if (FacesContext.getCurrentInstance() != null) {
                return FacesContext.getCurrentInstance().getViewRoot().getViewId();
            }
        } catch (Exception e) {
            log.debug("Fail on get view id");
        }
        // Unsupported feature
        return null;
    }

    @Override
    protected int getScope() {
        return RequestAttributes.SCOPE_REQUEST;
    }
}
