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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Braully Rocha
 */
public class UtilReflection {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(UtilReflection.class);

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

    public static Object getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtils.getProperty(bean, name);
    }

    public static String getPropertyText(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtils.getProperty(bean, name);
    }

}
