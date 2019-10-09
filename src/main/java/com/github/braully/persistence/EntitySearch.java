package com.github.braully.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 *
 * @author braully
 */
@Service
public class EntitySearch {

    private static final Logger log = LogManager.getLogger(EntitySearch.class.getName());
    /**/

    @Autowired
    private ApplicationContext context;
    @Autowired
    private GenericDAO genericDAO;

    public List searchEntitys(Class entitityClass, String searchMethod, Map params) {
        if (StringUtils.isEmpty(searchMethod)) {
            return defaultSearchEntity(entitityClass, params);
        }

        String[] splits = searchMethod.split("\\.");
        if (splits == null || splits.length < 2) {
            throw new IllegalArgumentException("Invalid search method: " + searchMethod);
        }

        Object bean = context.getBean(splits[0]);

        if (bean == null) {
            throw new IllegalArgumentException("Invalid bean: " + splits[0]);
        }

        Object ret = null;
        try {
            Class beanClass = bean.getClass();

            try {
                Advised advised = (Advised) bean;
                Class cls = advised.getTargetSource().getTargetClass();
                if (cls != null) {
                    beanClass = cls;
                }
            } catch (Exception e) {
                log.warn("Bean original type not found", e);
            }

            /* Search for Default Method with Map as parameter */
            Method method = null;
            try {
                method = beanClass.getMethod(searchMethod, Map.class);
            } catch (NoSuchMethodException e) {

            }

            if (method == null) {
//                if method not found, search for first method with searchmethod as name
                Method[] methods = beanClass.getMethods();
                for (Method m : methods) {
                    if (m.getName().equalsIgnoreCase(splits[1])) {
                        method = m;
                        break;
                    }
                }
            }

            if (method == null) {
                throw new IllegalArgumentException("Method invalid " + splits[1] + " in  bean " + splits[0]);
            }

            Parameter[] parametersDescription = method.getParameters();
            Object[] paramObjects = new Object[parametersDescription.length];
            Class[] paramTypes = new Class[parametersDescription.length];

            for (int i = 0; i < parametersDescription.length; i++) {
                Parameter p = parametersDescription[i];
                String name = p.getName();
                Object pValue = params.get(name);
                Object value = convertParameter(pValue, p.getType());
                paramObjects[i] = value;
                paramTypes[i] = p.getType();
                if (p.isNamePresent()) {
                    log.error("Parameter: name not present in parameter reflection");
                }
                if (value == null) {
                    log.info("Parameter: ''{0}'' not found in query", name);
                }
            }
            ret = MethodUtils.invokeMethod(bean, splits[1], paramObjects, paramTypes);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            log.error("", ex);
        }
        return (List) ret;
    }

    private Object convertParameter(Object pValue, Class type) {
        return pValue;
    }

    public List defaultSearchEntity(Class entitityClass, Map parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return genericDAO.loadCollection(entitityClass);
        }
        return genericDAO.loadCollectionWhere(entitityClass, parameters);
    }
}
