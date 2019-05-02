package com.github.braully.util;

import java.util.Collection;

/**
 *
 * @author braullyrocha
 */
public class UtilValidation {

    public static synchronized boolean isContemDados(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean isStringEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isStringValid(String... strs) {
        boolean ret = false;
        if (strs != null && strs.length > 0) {
            ret = true;
            for (String str : strs) {
                if (str == null || str.trim().isEmpty()) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }
}
