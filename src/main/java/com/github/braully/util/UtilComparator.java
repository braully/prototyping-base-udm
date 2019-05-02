package com.github.braully.util;

import java.util.Comparator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

/**
 * @author Braully Rocha
 */
public class UtilComparator<T> implements Comparator<T> {

    private static final Logger log = Logger.getLogger(UtilComparator.class);

    /* */
    private final String[] props;

    public UtilComparator(String... props) {
        this.props = props;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public int compare(T obj1, T obj2) {
        int ret = 0;
        if (props != null) {
            for (String prop : props) {
                if (ret != 0) {
                    break;
                }
                try {
                    Object property = PropertyUtils.getProperty(obj1, prop);
                    Object property1 = PropertyUtils.getProperty(obj2, prop);
                    if (property == null) {
                        if (property1 != null) {
                            ret = 1;
                        }
                    } else {
                        if (property1 == null) {
                            ret = -1;
                        }
                        if (property instanceof Comparable) {
                            Comparable val1 = (Comparable) property;
                            Comparable val2 = (Comparable) property1;
                            ret = val1.compareTo(val2);
                        } else {
                            ret = property.toString().compareToIgnoreCase(property1.toString());
                        }
                    }
                } catch (Exception e) {
                    log.error("Sort failed", e);
                }
            }
        }
        return ret;
    }

    public static boolean equals(Object o1, Object o2, String... props) {
        try {
            for (String prop : props) {
                Object valor1 = (Comparable) PropertyUtils.getProperty(o1, prop);
                Object valor2 = (Comparable) PropertyUtils.getProperty(o2, prop);
                if (valor1 == null) {
                    if (valor2 != null) {
                        return false;
                    }
                } else if (!valor1.equals(valor2)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
