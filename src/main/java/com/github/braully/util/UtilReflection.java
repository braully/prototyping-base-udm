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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

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

    public static <T> T getInstancia(String pacote, Class<T> classe, String nomeCampo, Object valorCampo) {
        T t = null;
        try {
            Class[] consultas = getClasses(pacote, classe);
            if (consultas != null) {
                for (Class consulta : consultas) {
                    try {
                        Field field = consulta.getDeclaredField(nomeCampo);
                        if (field != null && valorCampo.equals(field.get(null))) {
                            t = (T) consulta.newInstance();
                        }
                    } catch (Exception e) {
                        log.error("erro", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("erro", e);
        }
        return t;
    }

    public synchronized static Class[] getClasses(String packageName,
            Class classe) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            List<Class> tmpClasses = findClasses(directory, packageName);
            if (classe != null) {
                for (Class cls : tmpClasses) {
                    if (classe.isAssignableFrom(cls)) {
                        classes.add(cls);
                    }
                }
            } else {
                classes.addAll(tmpClasses);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static synchronized List<Class> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file,
                            packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName
                            + '.'
                            + file.getName().substring(0,
                                    file.getName().length() - 6)));
                }
            }
        } else {
            try {
                String caminho = directory.getPath();
                if (caminho.contains(".war")) {
                    classes.addAll(findClassesWar(directory, packageName));
                } else {
                    classes.addAll(findClassesJar(directory, packageName));
                }
            } catch (IOException e) {
                log.error("erro", e);
            }
        }
        return classes;
    }

    private static List<Class> findClassesWar(File dir, String packageName) throws ClassNotFoundException, IOException {
        String path = dir.getPath();
        String caminhoWar = path.substring(0, path.indexOf(".war") + 4);
        if (new File(caminhoWar).isDirectory()) {
            return findClassesJar(dir, packageName);
        } else {
            List<Class> classes = new ArrayList<Class>();
            caminhoWar = "jar:file:" + path.replace(".war", ".war!");
            caminhoWar = caminhoWar.substring(0, caminhoWar.indexOf(".jar") + 4);
            URL url = new URL(caminhoWar);
            JarURLConnection jarUrl = (JarURLConnection) url.openConnection();
            JarInputStream jirs = new JarInputStream(jarUrl.getInputStream());

            JarEntry jey;
            String dirPackage = packageName.replace(".", "/");
            while ((jey = jirs.getNextJarEntry()) != null) {
                System.out.println("JAR ENTRY: " + jey.getName());
                Class classe = classFromJarEntry(jey, dirPackage, packageName);
                if (classe != null) {
                    classes.add(classe);
                }
            }
            return classes;
        }
    }

    private static List<Class> findClassesJar(File jar, String packageName)
            throws ClassNotFoundException, IOException {
        List<Class> classes = new ArrayList<Class>();
        String path = jar.getPath();
        if (!path.startsWith("file:")) {
            path = "file:" + path;
        }
        if (!path.contains(".jar!")) {
            path = path.replace(".jar", ".jar!");
        }
        path = "jar:" + path;
        URL url = new URL(path);
        JarURLConnection jarUrl = (JarURLConnection) url.openConnection();
        JarFile jarfile = jarUrl.getJarFile();
        Enumeration<JarEntry> enm = jarfile.entries();
        String dirPackage = packageName.replace(".", "/");
        while (enm.hasMoreElements()) {
            JarEntry entry = enm.nextElement();
            Class classe = classFromJarEntry(entry, dirPackage, packageName);
            if (classe != null) {
                classes.add(classe);
            }
        }
        return classes;
    }

    private static synchronized Class classFromJarEntry(JarEntry entry, String dirPackage, String packageName) throws ClassNotFoundException {
        String entryPath = entry.getName();
        Class classe = null;
        if (entryPath.startsWith(dirPackage)
                && entryPath.endsWith(".class")) {
            String[] paths = entry.getName().split("/");
            String fileName = paths[paths.length - 1];
            classe = Class.forName(packageName + '.'
                    + fileName.substring(0, fileName.length() - 6));
        }
        return classe;
    }
}
