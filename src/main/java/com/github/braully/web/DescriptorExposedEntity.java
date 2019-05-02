package com.github.braully.web;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 *
 * @author braully
 */
public class DescriptorExposedEntity {

    static final String[] DEFAULT_HIDDEN_FORM_FIELDS = new String[]{"id", "version", "userIdModified", "lastModified"};
    static final String[] DEFAULT_HIDDEN_FILTER_FIELDS = new String[]{"version", "userIdModified"};

    Class classExposed;
    Set<String> hiddenFormProperties;
    Set<String> hiddenListProperties;
    Set<String> hiddenFilterProperties;
    Set<String> excludeProperties;

    public DescriptorExposedEntity() {
        hiddenFormProperties = new HashSet<>();
        hiddenListProperties = new HashSet<>();
        hiddenFilterProperties = new HashSet<>();
        Arrays.stream(DEFAULT_HIDDEN_FILTER_FIELDS).forEach(s -> hiddenFilterProperties.add(s));
        Arrays.stream(DEFAULT_HIDDEN_FORM_FIELDS).forEach(s -> hiddenFormProperties.add(s));
    }

    public DescriptorExposedEntity(Class aClass) {
        this();
        this.classExposed = aClass;
    }

    public String getEntityName() {
        return classExposed.getSimpleName();
    }

    public Class getClassExposed() {
        return classExposed;
    }

    public DescriptorExposedEntity hiddenForm(String... hiddenProperties) {
        if (hiddenProperties != null) {
            Arrays.stream(hiddenProperties).forEach(hp -> this.hiddenFormProperties.add(hp));
        }
        return this;
    }

    public DescriptorExposedEntity hiddenList(String... hiddenProperties) {
        if (hiddenProperties != null) {
            Arrays.stream(hiddenProperties).forEach(hp -> this.hiddenListProperties.add(hp));
        }
        return this;
    }

    public Map<String, String> sanitizeFilterParams(Map<String, String> params) {
        Map<String, String> sanitized = new HashMap<>();
        if (params != null) {
            for (Map.Entry<String, String> ent : params.entrySet()) {
                String key = ent.getKey();
                String value = ent.getValue();
                value = StringEscapeUtils.escapeJava(value);
                key = StringEscapeUtils.escapeJava(key);
                key = removeCharctersUnsafe(key);
                if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
                    sanitized.put(key, value);
                }
            }
        }
        return sanitized;
    }

    private String removeCharctersUnsafe(String key) {
        String ret = null;
        if (key != null) {
            ret = key.replaceAll("\\.", "");
        }
        return ret;
    }

    @Override
    public String toString() {
        return "DescriptorExposedEntity{" + "classExposed=" + classExposed.getSimpleName() + '}';
    }

    public DescriptorHtmlEntity getDescriptorHtmlEntity() {
        return new DescriptorHtmlEntity(this);
    }

    /* */
    public static class DescriptorHtmlEntity {

        static final String TYPE_SELECT_ONE = "selectone";
        static final String TYPE_SELECT_MANY = "selectmany";

        DescriptorHtmlEntity parent;
        List<DescriptorHtmlEntity> elementsForm;
        List<DescriptorHtmlEntity> elementsFilter;
        List<DescriptorHtmlEntity> elementsList;
        Set<String> hiddenFormProperties;
        Set<String> hiddenFilterProperties;
        Set<String> hiddenListProperties;
        Set<String> exclude = new HashSet<>();

        String id;
        String label;
        String type;
        String property;
        String pattern;
        String size;
        String sort;

        Class classe;

        Map<String, String> attributes;

        public DescriptorHtmlEntity() {
        }

        public DescriptorHtmlEntity(String model,
                Class classe,
                String type) {
            this.classe = classe;
            this.property = model;
            this.type = type;
            parseFieldClass(classe);
        }

        public DescriptorHtmlEntity(DescriptorHtmlEntity parent) {
            this.parent = parent;
        }

        public DescriptorHtmlEntity(Class classe) {
            this(decapitalize(classe.getSimpleName()), classe, "");
        }

        public DescriptorHtmlEntity(Class classe, String type) {
            this(decapitalize(classe.getSimpleName()), classe, type);
            parseFieldClass(classe);
        }

        private void parseFieldClass(Class classe1) {
            ReflectionUtils.doWithFields(classe1, (Field field) -> {
                addHtmlFormElement(field);
                addHtmlFilterElement(field);
                addHtmlListElement(field);
            });

            Collections.sort(elementsList, new Comparator<DescriptorHtmlEntity>() {
                @Override
                public int compare(DescriptorHtmlEntity t, DescriptorHtmlEntity t1) {
                    try {
                        return t.property.compareToIgnoreCase(t1.property);
                    } catch (Exception e) {
                    }
                    return 0;
                }
            });
        }

        public DescriptorHtmlEntity(DescriptorExposedEntity descriptorExposedEntity) {
            this(descriptorExposedEntity.classExposed);
        }

        private void addHtmlListElement(Field field) {
            if (elementsList == null) {
                elementsList = new ArrayList<>();
            }
            if (hiddenListProperties == null) {
                hiddenListProperties = new HashSet<>();
            }
            final int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)
                    && !hiddenListProperties.contains(field.getName())) {
                elementsList.add(buildDescriptorHtmlEntity(field));
            }
        }

        private void addHtmlFilterElement(Field field) {
            if (elementsFilter == null) {
                elementsFilter = new ArrayList<>();
            }
            if (hiddenFilterProperties == null) {
                hiddenFilterProperties = new HashSet<>();
            }
            final int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)
                    && !hiddenFilterProperties.contains(field.getName())) {
                elementsFilter.add(buildDescriptorHtmlEntity(field));
            }
        }

        private void addHtmlFormElement(Field field) {
            if (elementsForm == null) {
                elementsForm = new ArrayList<>();
            }
            if (hiddenFormProperties == null) {
                hiddenFormProperties = new HashSet<>();
            }
            final int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)
                    && !hiddenFormProperties.contains(field.getName())) {
                DescriptorHtmlEntity htmlElement = buildDescriptorHtmlEntity(field);
                elementsForm.add(htmlElement);
            }
        }

        public static String decapitalize(String string) {
            if (string == null || string.length() == 0) {
                return string;
            }
            char c[] = string.toCharArray();
            c[0] = Character.toLowerCase(c[0]);
            return new String(c);
        }

        DescriptorHtmlEntity buildDescriptorHtmlEntity(Field field) {
            String ltype = field.getType().getSimpleName();
            ltype = ltype.substring(0, 1).toLowerCase() + ltype.substring(1);
            String lproperty = field.getName();
            String lpattern = null;
            if (field.getAnnotation(OneToOne.class) != null
                    || field.getAnnotation(ManyToOne.class) != null) {
                lpattern = ltype;
                ltype = "entity";
            }
            if (field.getType().isAssignableFrom(Collection.class)
                    && (field.getAnnotation(ManyToMany.class) != null
                    || field.getAnnotation(OneToMany.class) != null)) {
                lpattern = ltype;
                ltype = "collection";
            }
            String llabel = lproperty.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
            llabel = WordUtils.capitalize(llabel);

            DescriptorHtmlEntity he = new DescriptorHtmlEntity(this);
            he.classe = field.getType();
            he.type = ltype;
            he.property = lproperty;
            he.label = llabel;
            he.pattern = lpattern;
            return he;
        }

        public Class getTypeClass() {
            return classe;
        }

        public String getTypeLow() {
            return type.toLowerCase();
        }

        public String toString() {
            return "DescriptorHtmlEntity{" + "id=" + id + ", label=" + label
                    + ", type=" + type + ", property="
                    + property + ", classe=" + (classe != null ? classe.getSimpleName() : null) + '}';
        }
    }
}
