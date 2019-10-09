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

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Braully Rocha
 */
public class UtilProperty {

    public static final String DEFAULT_FILE_PROPERTY = "application.properties";
    private static final Logger log = LogManager.getLogger(UtilProperty.class);

    /**
     * @param nameProp
     * @return
     */
    public static synchronized boolean isProperty(String nameProp) {
        return isProperty(DEFAULT_FILE_PROPERTY, nameProp);
    }

    /**
     * Property name from default properties
     *
     * @param name
     * @return
     */
    public static synchronized String getProperty(String name) {
        return getProperty(DEFAULT_FILE_PROPERTY, name);
    }

    /**
     * @param filename
     * @param name
     * @return
     */
    public static synchronized String getProperty(String filename, String name) {
        String ret = null;
        try {
            Properties props = UtilPath.getProperties(filename);
            ret = props.getProperty(name);
        } catch (Exception e) {
            log.warn("FAIL ON LOAD PROPERTIES FILE: " + filename, e);
        }
        return ret;
    }

    public static synchronized boolean isProperty(String filename, String nameProp) {
        boolean ret = false;
        try {
            Properties props = UtilPath.getProperties(filename);
            String mens = props.getProperty(nameProp);
            ret = Boolean.parseBoolean(mens);
        } catch (Exception e) {
            log.warn("FALHA AO CARREGAR ARQUIVO DE PROPRIEDADE", e);
        }
        return ret;
    }

    public static synchronized String[] tokenizerProperty(String filename, String nameProp, String token) {
        String[] ret = null;
        try {
            Properties props = UtilPath.getProperties(filename);
            ret = props.getProperty(nameProp).split(token);
            if (ret != null) {
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = ret[i].trim();
                }
            }
        } catch (Exception e) {
            log.warn("Fail on strip property: " + nameProp + " : " + e.getMessage());
        }
        return ret;
    }

    public static synchronized Integer getPropertyInt(String nameProp) {
        return getPropertyInt(DEFAULT_FILE_PROPERTY, nameProp);
    }

    public static synchronized Integer getPropertyInt(String filename, String nameProp) {
        Integer ing = null;
        try {
            Properties props = UtilPath.getProperties(filename);
            String mens = props.getProperty(nameProp);
            ing = Integer.parseInt(mens);
        } catch (Exception e) {
            log.error("FALHA AO CARREGAR ARQUIVO DE PROPRIEDADE", e);
        }
        return ing;
    }

    public static Properties getProperties(String nomeArquivoPropriedades) throws IOException {
        Properties props = new Properties();
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(nomeArquivoPropriedades));
        return props;
    }
}
