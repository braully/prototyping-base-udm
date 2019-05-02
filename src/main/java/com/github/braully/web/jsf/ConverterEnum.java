package com.github.braully.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("view")
@Component("converterEnum")
@Qualifier("converterEnum")
@FacesConverter("converterEnum")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ConverterEnum implements Converter {

    private static final String SEPARATOR = "::";
    public static final String CONVERTER_FOR_CLASS = "java.lang.Enum";

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) throws ConverterException {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String[] sa = value.split(SEPARATOR);

        if (sa == null || sa.length != 2) {
            throw new ConverterException("Couldn't split input in two pieces: "
                    + value);
        }

        String className = sa[0];
        String enumName = sa[1];

        if (className == null || className.isEmpty()) {
            throw new ConverterException("Empty class name");
        }
        if (enumName == null || enumName.isEmpty()) {
            throw new ConverterException("Empty enum name");
        }

        Class enumClass;
        try {
            enumClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ConverterException("Class for enum type " + className
                    + " not found", e);
        }

        return Enum.valueOf(enumClass, enumName);
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) throws ConverterException {
        if (value == null || value instanceof String) {
            return "";
        } else if (value instanceof Enum) {
            Enum e = (Enum) value;
            Class declaringClass = e.getDeclaringClass();
            String className = declaringClass.getName();
            return className.toString() + SEPARATOR + e.name();
        } else {
            throw new ConverterException("Expected Enum type, got "
                    + value.getClass());
        }
    }
}
