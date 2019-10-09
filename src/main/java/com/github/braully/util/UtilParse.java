package com.github.braully.util;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author braully
 */
public class UtilParse {

    public static List<String> parseList(String str, String delimit) {
        String[] excludes = new String[]{str};
        if (str.contains(delimit)) {
            excludes = str.split(delimit);
        }
        return Arrays.asList(excludes);
    }

}
