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

import com.github.braully.constant.Attr;
import com.github.braully.constant.Attrs;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Braully Rocha
 */
public class UtilReflection {

    private static org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(UtilReflection.class);

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGenericTypeArgument(final Class<?> clazz, final int idx) {
        final Type type = clazz.getGenericSuperclass();

        ParameterizedType paramType;
        try {
            paramType = (ParameterizedType) type;
        } catch (ClassCastException cause) {
            paramType = (ParameterizedType) ((Class<T>) type).getGenericSuperclass();
        }

        return (Class<T>) paramType.getActualTypeArguments()[idx];
    }

    public static List<Field> getAllFieldsAssinableFrom(Class<?> root, Class<?>... childs) {
        List<Field> flds = new ArrayList<>();
        Field[] declaredFields = root.getDeclaredFields();
        for (Field fld : declaredFields) {
            if (childs == null) {
                flds.add(fld);
            } else {
                for (Class cl : childs) {
                    Class<?> type = fld.getType();
                    if (cl.isAssignableFrom(type)) {
                        flds.add(fld);
                        break;
                    }
                }
            }
        }
        return flds;
    }

    public static synchronized <T> T createInstance(Class<T> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException ex) {
            log.error("Failed instance", ex);
        } catch (IllegalAccessException ex) {
            log.error("Failed instance", ex);
        }
        return null;
    }

    public static Object getPrivateField(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        Field field = bean.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(bean);
    }

    public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtils.setProperty(bean, name, value);
    }

    public static Object getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtils.getProperty(bean, name);
    }

    public static void setPropertyIfNull(Object bean, String propery, Object newValue) {
        if (bean == null) {
            return;
        }
        if (newValue == null) {
            return;
        }
        Object value = null;
        try {
            value = PropertyUtils.getProperty(bean, propery);
        } catch (Exception ex) {
            throw new IllegalStateException("Fail on getproperty: " + propery, ex);
        }
        if (value == null) {
            try {
                PropertyUtils.setProperty(bean, propery, newValue);
            } catch (Exception ex) {
                throw new IllegalStateException("Fail on setproperty: " + propery, ex);
            }
        }
    }

    public static void setPropertyIfNullIgnoreException(Object bean, String propery, Object newValue) {
        try {
            setPropertyIfNull(bean, propery, newValue);
        } catch (Exception ex) {
            //log.debug("Fail on setproperty: " + propery + " --ignored", ex);
            log.debug("Fail on setproperty: " + propery + " --ignored");
        }
    }

    public static String getPropertyText(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtils.getProperty(bean, name);
    }

    public static synchronized Boolean isExtraAttribute(Field fieldType, String nameAttribute) {
        return isExtraAttribute(fieldType, nameAttribute, false);
    }

    public static synchronized Boolean isExtraAttribute(Field fieldType, String nameAttribute, Boolean defau) {
        String attr = getMapExtraAttributesField(fieldType).get(nameAttribute);
        Boolean ret = defau;
        if (attr != null) {
            ret = Boolean.parseBoolean(attr);
        }
        return ret;
    }

    public static synchronized String getExtraAttribute(Field fieldType, String nameAttribute) {
        return getMapExtraAttributesField(fieldType).get(nameAttribute);
    }

    //TODO: Refatorar para um utilitario
    public static synchronized Map<String, String> getMapExtraAttributesField(Field fieldType) {
        Map<String, String> param = new HashMap<>();

        if (fieldType != null && fieldType.isAnnotationPresent(Attrs.class)
                || fieldType.isAnnotationPresent(Attr.class)) {
            Attr[] value = null;
            Attrs attrs = fieldType.getAnnotation(Attrs.class);
            if (attrs == null) {
                value = fieldType.getAnnotationsByType(Attr.class);
            } else {
                value = attrs.value();
            }

            if (value != null) {
                for (Attr atr : value) {
                    String nomeAtributo = null;
                    String valorAtributo = null;
                    try {
                        String attrName = atr.name();
                        if (attrName != null && !attrName.isEmpty()) {
                            nomeAtributo = attrName;
                            valorAtributo = atr.val();
                        }
                        String string0 = atr.value()[0];
                        if (string0 != null && !string0.isEmpty()) {
                            nomeAtributo = string0;
                            if (atr.value().length > 1) {
                                valorAtributo = atr.value()[1];
                            } else {
                                valorAtributo = Boolean.TRUE.toString();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Falha ao selecionar attr", e);
                    }
                    param.put(nomeAtributo, valorAtributo);
                }
            }
        }
        return param;
    }

    public static Field getDeclaredFieldAscending(Class classe, String attrib) {
        Field field = null;
        while (classe != null && field == null) {
            try {
                field = classe.getDeclaredField(attrib);
            } catch (Exception e) {
            }
            classe = classe.getSuperclass();
        }
        return field;
    }
}
