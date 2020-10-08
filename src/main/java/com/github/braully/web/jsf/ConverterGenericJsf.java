package com.github.braully.web.jsf;

import com.github.braully.app.exposed;
import com.github.braully.domain.util.GenericTO;
import com.github.braully.persistence.IEntity;
import com.github.braully.util.UtilReflection;
import com.github.braully.web.DescriptorExposedEntity;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.ByteConverter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.DoubleConverter;
import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.LongConverter;
import javax.faces.convert.NumberConverter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.MonetaryAmountDecimalFormatBuilder;
import org.javamoney.moneta.function.MoneyProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author braully
 */
@Scope("view")
@Component("converterGenericJsf")
@Qualifier("converterGenericJsf")
@FacesConverter("converterGenericJsf")
@SuppressWarnings("unchecked")
public class ConverterGenericJsf implements Converter {

    public static Logger log = LogManager.getLogger(ConverterGenericJsf.class);

    //TODO: From configuration or system
    public static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm";

    private static final boolean cacheConverter = true;
    private static final Map<String, Field> CACHE_TYPE = new HashMap<>();
    private static final Map<Field, Converter> CACHE_CONVERTER_TYPE = new HashMap<>();
    /*    
    https://docs.oracle.com/javaee/7/tutorial/jsf-page-core001.htm 
     */
    NumberConverter numberConverter = new NumberConverter();
    LongConverter longConverter = new LongConverter();
    DateTimeConverter dateTimeConverter = new DateTimeConverter();
    ByteConverter byteConverter = new ByteConverter();
    NumberConverter percentageConverter = new NumberConverter();
    DoubleConverter doubleConverter = new DoubleConverter();
    EnumConverter enumConverter = new EnumConverter();

    {
        percentageConverter.setType("percent");
    }
    /* */
    ConverterMonetary converterMonetary = new ConverterMonetary();
    ConverterValorFator converterValorFator = new ConverterValorFator();
    ConverterValorFator converterMonetaryBig = new ConverterValorFator(ConverterValorFator.Type.BIG);

    Map<String, Converter> converters = Map.of("numberConverter", numberConverter, "doubleConverter", doubleConverter,
            "dateTimeConverter", dateTimeConverter, "converterMonetary", converterMonetary,
            "converterValorFator", converterValorFator, "converterMonetaryBigDecimal", converterMonetaryBig);

    @Override
    public synchronized Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        Field type = type = getTypeByComponent(uic);
        if (type == null) {
            type = getTypeByComponentContext(uic, fc);
        }
        return getAsObject(fc, uic, string, type);
    }

    @Override
    public synchronized String getAsString(FacesContext fc, UIComponent uic, Object t) {
        Field type = type = getTypeByComponent(uic);
        if (type == null) {
            type = getTypeByComponentContext(uic, fc);
        }
        return getAsString(fc, uic, t, type);
    }

    private Object getAsObject(FacesContext fc, UIComponent uic, String string, Field type) {
        Object ret = null;
        Converter converter = getConverter(type);
        if (converter != null) {
            ret = converter.getAsObject(fc, uic, string);
        }
        return ret;
    }

    private String getAsString(FacesContext fc, UIComponent uic, Object t, Field type) {
        String ret = null;
        Converter converter = getConverter(type);
        if (converter != null) {
            ret = converter.getAsString(fc, uic, t);
        } else {
            ret = t.toString();
        }
        return ret;
    }

    protected Field getTypeByComponentContext(UIComponent uic, FacesContext fc) {
        Field type = null;
        ValueExpression valueExpression = uic.getValueExpression("value");
        String expressionString = valueExpression.getExpressionString();
        int lastIndexOf = expressionString.lastIndexOf(".");
        String strBean = expressionString.substring(2, lastIndexOf);
        String attrib = expressionString.substring(lastIndexOf + 1, expressionString.length() - 1);
        strBean = "#{" + strBean + "}";
        Object evaled = fc.getApplication().evaluateExpressionGet(fc, strBean, Object.class);
        if (evaled != null) {
            try {
                Class classe = evaled.getClass();
                type = UtilReflection.getDeclaredFieldAscending(classe, attrib);
            } catch (Exception ex) {
                log.debug("falha 13131", ex);
            }
        }
        return type;
    }

    protected Field getTypeByComponent(UIComponent uic) {
        Field type = null;
        String entityProperty = (String) uic.getAttributes().get("entityProperty");
        if (entityProperty != null && !entityProperty.isEmpty()) {
            type = type(entityProperty);
        }
        if (type == null) {
            ValueExpression valueExpression = uic.getValueExpression("value");
            String expressionString = valueExpression.getExpressionString();
            type = getTypeByExpression(expressionString);
        }
        return type;
    }

    public Field getTypeByExpression(String expressionString) {
        Field ret = null;
        if (expressionString.contains("genericMB.crud(")) {
            try {
                int ini = expressionString.lastIndexOf(".");
                int end = expressionString.lastIndexOf("}");
                String entity = expressionString.substring(expressionString.indexOf("'") + 1, expressionString.lastIndexOf("'"));
                String attr = expressionString.substring(ini, end);
                ret = type(entity + attr);
            } catch (Exception e) {
                log.debug("Falha ao identificar expressão: " + expressionString, e);
            }
        } else {
            //TODO: Recuperar o tipo de dados pela expressão
            //Tentei via a linguagem de expressão do spring, mas sem sucesso:
            //https://docs.spring.io/spring/docs/4.3.15.RELEASE/spring-framework-reference/html/expressions.html
            //https://www.baeldung.com/spring-expression-language
            //https://stackoverflow.com/questions/11616316/programmatically-evaluate-a-bean-expression-with-spring-expression-language
//            @Autowired
//            private ApplicationContext applicationContext;
//            boolean containsBean = applicationContext.containsBean("financialController");
//            boolean containsBeanDefinition = applicationContext.containsBeanDefinition("financialController");
//            log.info();

//            throw new IllegalStateException("Não é possivel determinar o tipo de dados para a expressão: " + expressionString);
        }
        return ret;
    }

    public Field type(String entityNameField) {
        Field tmp = null;
        tmp = CACHE_TYPE.get(entityNameField);
        try {
            if (tmp == null) {
                String entityName = entityNameField;
                String strfield = entityNameField;
                Class classExposed = null;
                if (entityNameField.contains(".")) {
                    String[] split = entityNameField.split("\\.");
                    entityName = split[0];
                    strfield = split[1];
                }
                DescriptorExposedEntity desc = exposed.getExposedEntity(entityName);
                if (desc != null) {
                    classExposed = desc.getClassExposed();
                }
                tmp = classExposed.getDeclaredField(strfield);
                CACHE_TYPE.put(entityNameField, tmp);
            }
        } catch (Exception ex) {
            log.error("error values dynamic", ex);
        }
        return tmp;
    }

    private Converter getConverter(Field fieldType) {
        Converter converter = null;
        if (cacheConverter) {
            converter = CACHE_CONVERTER_TYPE.get(fieldType);
        }
        if (converter != null) {
            return converter;
        }
        /*The call of method.setAccessible(true) allows us to execute the private method.*/
        Map<String, String> param = UtilReflection.getMapExtraAttributesField(fieldType);

        String pattern = param.get("pattern");
        String converterId = param.get("converter");

        if (converterId != null) {
            converter = this.converters.get(converterId);
        }

        if (converter == null) {
            Class<?> atributeType = fieldType.getType();
            if (Date.class.isAssignableFrom(atributeType)) {
                dateTimeConverter.setType("date");
                dateTimeConverter.setPattern(DEFAULT_DATE_PATTERN);
                Temporal annotation = fieldType.getAnnotation(Temporal.class);
                if (annotation != null) {
                    TemporalType temptype = annotation.value();
                    if (temptype == TemporalType.TIME) {
                        dateTimeConverter.setType("time");
                        dateTimeConverter.setPattern(DEFAULT_TIME_PATTERN);
                    } else if (temptype == TemporalType.TIMESTAMP) {
                        dateTimeConverter.setType("both");
                    }
                }
                converter = dateTimeConverter;
                if (pattern != null) {
                    dateTimeConverter.setPattern(pattern);
                }
            } else if (Number.class.isAssignableFrom(atributeType)) {
                converter = numberConverter;
                if (pattern != null) {
                    numberConverter.setPattern(pattern);
                }
            }
        }
        this.CACHE_CONVERTER_TYPE.put(fieldType, converter);
        return converter;
    }

    /* */
    public NumberConverter getNumberConverter() {
        return numberConverter;
    }

    public DateTimeConverter getDateTimeConverter() {
        return dateTimeConverter;
    }

    public ConverterValorFator getConverterValorFator() {
        return converterValorFator;
    }

    public ConverterMonetary getConverterMonetary() {
        return converterMonetary;
    }

    public ConverterValorFator getConverterMonetaryBig() {
        return converterMonetaryBig;
    }

    public ByteConverter getByteConverter() {
        return byteConverter;
    }

    public LongConverter getLongConverter() {
        return longConverter;
    }

    public NumberConverter getPercentageConverter() {
        return percentageConverter;
    }

    public EnumConverter getEnumConverter() {
        return enumConverter;
    }

    @Scope("view")
    @Component("converterEnum")
    @Qualifier("converterEnum")
    @FacesConverter("converterEnum")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class ConverterEnum implements Converter {

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

    @Scope("view")
    @Component("converterEntity")
    @Qualifier("converterEntity")
    @FacesConverter("converterEntity")
    @SuppressWarnings("unchecked")
    public static class ConverterEntity implements Converter {

        private static final Logger log = LogManager.getLogger(ConverterEntity.class);

        @Override
        public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
            IEntity ret = null;
            UIComponent fonte = arg1;
            Collection<IEntity> values = null;
            if (fonte != null) {
                Collection<UIComponent> childs = fonte.getChildren();
                UISelectItems itens = null;
                if (childs != null) {
                    for (UIComponent ui : childs) {
                        if (ui instanceof UISelectItems) {
                            itens = (UISelectItems) ui;
                            break;
                        } else if (ui instanceof UISelectItem) {
                            UISelectItem item = (UISelectItem) ui;
                            try {
                                IEntity val = (IEntity) item.getItemValue();
                                if (arg2.equals("" + val.getId())) {
                                    ret = val;
                                    break;
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }

                if (itens != null) {
                    values = (Collection<IEntity>) itens.getValue();
                    if (values != null) {
                        try {
                            for (IEntity val : values) {
                                if (arg2.equals("" + val.getId())) {
                                    ret = val;
                                    break;
                                }
                            }
                        } catch (ClassCastException e) {
                            log.debug("Falha ao converter entidades", e);
                            try {
                                for (SelectItemGroup is : (Collection<SelectItemGroup>) itens.getValue()) {
                                    for (SelectItem i : is.getSelectItems()) {
                                        IEntity val = (IEntity) i.getValue();
                                        if (arg2.equals("" + val.getId())) {
                                            ret = val;
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                log.error("Falha ao converter selctgroupitems", ex);
                            }
                        }
                    }
                }
            }
            return ret;
        }

        @Override
        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
            String ret = "";
            try {
                if (arg2 != null) {
                    Object id = null;
                    if (arg2 instanceof IEntity) {
                        IEntity m = (IEntity) arg2;
                        id = m.getId();
                        if (id != null) {
                            ret = id.toString();
                        }
                    } else if (arg2 instanceof String) {
                        ret = (String) arg2;
                    } else {
                        id = PropertyUtils.getProperty(arg2, "id");
                    }
                    if (id != null) {
                        ret = id.toString();
                    }
                }
            } catch (java.lang.NoSuchMethodException e) {
            } catch (Exception e) {
                log.debug("Não foi possivel converter: " + arg2, e);
            }
            return ret;
        }
    }

    @Scope("view")
    @Component("converterGenericTO")
    @Qualifier("converterGenericTO")
    @FacesConverter("converterGenericTO")
    @SuppressWarnings("unchecked")
    public static class ConverterGenericTO implements Converter {

        @Override
        public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
            GenericTO ret = null;
            UIComponent fonte = arg1;
            if (fonte != null) {
                List<UIComponent> childs = fonte.getChildren();
                UISelectItems itens = null;
                if (childs != null) {
                    for (UIComponent ui : childs) {
                        if (ui instanceof UISelectItems) {
                            itens = (UISelectItems) ui;
                            break;
                        } else if (ui instanceof UISelectItem) {
                            UISelectItem item = (UISelectItem) ui;
                            try {
                                GenericTO val = (GenericTO) item.getItemValue();
                                if (arg2.equals("" + val.getId())) {
                                    ret = val;
                                    break;
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }

                if (itens != null) {
                    List<GenericTO> values = (List<GenericTO>) itens.getValue();
                    if (values != null) {
                        for (GenericTO val : values) {
                            if (arg2.equals("" + val.getId())) {
                                ret = val;
                                break;
                            }
                        }
                    }
                }
            }
            return ret;
        }

        @Override
        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
            String ret = "";
            if (arg2 != null) {
                if (arg2 instanceof GenericTO) {
                    GenericTO m = (GenericTO) arg2;
                    if (m != null) {
                        Long id = m.getId();
                        if (id != null) {
                            ret = id.toString();
                        }
                    }
                } else if (arg2 instanceof IEntity) {
                    IEntity e = (IEntity) arg2;
                    if (e != null && e.getId() != null) {
                        ret = e.getId().toString();
                    }
                }
            }
            return ret;
        }
    }

    /**
     *
     * @author braully
     */
    @Scope("view")
    @Component("converterMonetary")
    @Qualifier("converterMonetary")
    @FacesConverter("converterMonetary")
    public static class ConverterMonetary implements Converter {

        CurrencyUnit currency = Monetary.getCurrency("BRL");

        MonetaryAmountFormat defaultFormat = MonetaryAmountDecimalFormatBuilder.of(new Locale("pt", "BR")).
                withCurrencyUnit(currency).withProducer(new MoneyProducer()).build();

        @Override
        public Object getAsObject(FacesContext context, UIComponent component,
                String value) {
            if (Objects.isNull(value)) {
                return null;
            }
            return Money.parse(value, defaultFormat);
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component,
                Object value) {
            String ret = null;
            if (!Objects.isNull(value)) {
                ret = value.toString();
                if (value instanceof MonetaryAmount) {
                    ret = defaultFormat.format((MonetaryAmount) value);
                }
            }
            return ret;
        }
    }

    @FacesConverter(value = "converterValorFator")
    public static class ConverterValorFator implements Converter {

        Type type = null;

        public ConverterValorFator() {
        }

        public ConverterValorFator(Type type) {
            this.type = type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        @Override
        public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
            com.github.braully.domain.util.Money m = ((com.github.braully.domain.util.Money) superGetAsObject(arg0, arg1, arg2));
            if (m != null) {
                if (type == Type.BIG) {
                    return m.getValorBig();
                } else {
                    return m.getValor();
                }
            }
            return null;
        }

        @Override
        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
            if (arg2 != null && arg2 instanceof Number) {
                if (arg2 instanceof BigDecimal) {
                    return new com.github.braully.domain.util.Money((BigDecimal) arg2).formatarValor();
                }
                return new com.github.braully.domain.util.Money(((Number) arg2).longValue()).formatarValor();
            }
            return "";

        }

        public Object superGetAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
            if (arg2 != null && !arg2.trim().isEmpty()) {
                return new com.github.braully.domain.util.Money(arg2);
            }
            return null;
        }

        public String superGetAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
            String ret = "";
            if (arg2 != null) {
                com.github.braully.domain.util.Money m = (com.github.braully.domain.util.Money) arg2;
                ret = m.toString();
            }
            return ret;
        }

        public static enum Type {
            BIG, MONEY, DOUBLE
        }
    }
}
