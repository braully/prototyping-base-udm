package com.github.braully.web.jsf;

import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ViewResource;
import javax.faces.context.FacesContext;

/**
 *
 * @author braully
 */
public class JsfResourceHandler extends ResourceHandlerWrapper {

    public static final String DEFAULT_JSF_DIRECTORY_PREFIX = "/jsf";

    private ResourceHandler wrapped;

    public JsfResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ViewResource createViewResource(FacesContext context, String resourceName) {
        //Default resource view
        ViewResource resource = super.createViewResource(context, resourceName);

        //Dynamic generate resource 
        if (resource == null && AutoGenResourceJSF.isAutoGenResource(resourceName)) {
            resource = new AutoGenResourceJSF(resourceName, wrapped);
        }

        //Offset Folder resource view
        if (resource == null && !resourceName.startsWith(DEFAULT_JSF_DIRECTORY_PREFIX)
                && !resourceName.startsWith(DEFAULT_JSF_DIRECTORY_PREFIX)
                && resourceName.endsWith("html")) {
            resource = super.createViewResource(context, DEFAULT_JSF_DIRECTORY_PREFIX + resourceName);
        }

        return resource;
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }
}
