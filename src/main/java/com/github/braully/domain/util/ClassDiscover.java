package com.github.braully.domain.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ClassDiscover {

    private static final Logger log = LogManager.getLogger(ClassDiscover.class);

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

    public static synchronized Class[] getClasses(String string)
            throws ClassNotFoundException, IOException {
        return getClasses(string, null);
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

    public <T> T getInstancia(String pacote, Class<T> classe, String nomeCampo, Object valorCampo) {
        ClassDiscover discovery = new ClassDiscover();
        T t = null;
        try {
            Class[] consultas = discovery.getClasses(pacote,
                    classe);
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

    public static void main(String[] args) throws Exception {
        String path = "/home/user/user/workspace/Escola/src/test/resources/classdicover/Escola.war";

        path = "jar:file:" + path + "!/WEB-INF/lib/entidade-academica.jar";
//        path = "jar:file:" + path + "!/WEB-INF/lib/entidade-academica.jar!/META-INF";

        System.out.println("PATH: " + path);

        URL url = new URL(path);
        JarURLConnection jarUrl = (JarURLConnection) url.openConnection();
        System.out.println("JAR ENTRY: " + jarUrl.getJarEntry().getName());
        JarFile jarfile = jarUrl.getJarFile();
        JarInputStream jirs = new JarInputStream(jarUrl.getInputStream());
        int numDisp = jirs.available();

        System.out.println("JAR INPUT STREAM: " + jirs);
        System.out.println("JAR INPUT STREAM - LENGTH: " + numDisp);

        JarEntry jey = null;
        while ((jey = jirs.getNextJarEntry()) != null) {
            System.out.println("JAR ENTRY: " + jey.getName());
        }

        Enumeration<JarEntry> enm = jarfile.entries();

        while (enm.hasMoreElements()) {
            JarEntry entry = enm.nextElement();
            String entryPath = entry.getName();
            System.out.println("ENTRY: " + entryPath);
        }
    }
}
