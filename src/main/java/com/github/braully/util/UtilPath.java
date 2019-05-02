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
import org.apache.log4j.Logger;

/**
 * @author Braully Rocha
 */
public class UtilPath {
    
    private static final Logger log = Logger.getLogger(UtilPath.class);

    /**
     * Absolute path from relative.
     *
     * @param classLoader
     * @param relative
     * @return
     */
    public static synchronized String getPath(ClassLoader classLoader, String relative) {
        return classLoader.getResource(relative).getPath();
    }

    /**
     * Absolute path from relative.
     *
     * @param relative
     * @return
     */
    public static synchronized String getPath(String relative) {
        return getPath(Thread.currentThread().getContextClassLoader(), relative);
    }

    /**
     * Load Properties from file name, in relative path.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Properties getProperties(String fileName) throws IOException {
        Properties props = new Properties();
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
        } catch (Exception e) {
            log.debug("Not propertie file " + fileName);
            log.debug("Fail on get properties from: " + fileName, e);
        }
        return props;
    }
}
