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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author braully
 */
public class UtilCollection {

    public static <K, V> Map<K, V> mapOf(Object... args) {
        Map<K, V> map = new HashMap<>();
        try {
            if (args != null) {
                K key = null;
                V val = null;
                for (int i = 0; i < args.length; i = i + 2) {
                    key = (K) args[i];
                    val = (V) args[i + 1];
                    map.put(key, val);
                }
            }
        } catch (Exception e) {
        }
        return map;
    }

    public static Map merge(Map... props) {
        Map<String, Object> mapAllProps = new HashMap<>();
        if (props != null) {
            for (Map pps : props) {
                mapAllProps.putAll(pps);
            }
        }
        return mapAllProps;
    }

    public static boolean sortIfComparable(Collection colection) {
        try {
            Collections.sort((List) colection);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String printArray(Object[] arr) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            sb.append("| ");
            for (Object o : arr) {
                try {
                    sb.append(o).append(" | ");
                } catch (Exception e) {

                }
            }
        }
        return sb.toString();
    }

    public synchronized static List<Integer> list(int windowIni, int windowEnd) {
        List<Integer> list = new ArrayList<>();
        for (int i = windowIni; i < windowEnd; i++) {
            list.add(i);
        }
        return list;
    }
}
