package com.github.braully.util;

import com.github.braully.util.logutil;
import com.github.braully.domain.util.Money;
import java.util.Date;
import java.util.Map;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import de.odysseus.el.util.SimpleContext;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.commons.beanutils.PropertyUtils;

public class UtilExpression {

    public static Object executarExpressao(Object bean, Map<String, Object> propsExtra, String strExpr) {
        ExpressionFactory expFactory = new de.odysseus.el.ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        Map<String, Object> properties = new HashMap<>();

        try {
            context.setFunction("", "SE", UtilExpression.class.getMethod("se", boolean.class, Object.class, Object.class));
            context.setFunction("", "CONCATENAR", UtilExpression.class.getMethod("concatenar", new Object[0].getClass()));
            //
            context.setFunction("", "DINHEIRO", UtilExpression.class.getMethod("dinheiro", Number.class));
            context.setFunction("", "DINHEIRO", UtilExpression.class.getMethod("dinheiro", Money.class));
            context.setFunction("", "DINHEIRO", UtilExpression.class.getMethod("dinheiro", String.class));
            //
            context.setFunction("", "DATA", UtilExpression.class.getMethod("data", Date.class));
            context.setFunction("", "DATA", UtilExpression.class.getMethod("data", int.class, int.class, int.class));
            context.setFunction("", "ANO", UtilExpression.class.getMethod("ano", Date.class));
            context.setFunction("", "MES", UtilExpression.class.getMethod("mes", Date.class));
            context.setFunction("", "DIA", UtilExpression.class.getMethod("mes", Date.class));
            //
            context.setFunction("", "PORCENTAGEM", UtilExpression.class.getMethod("porcentagem", Number.class, Number.class));
            context.setFunction("", "PORCENTAGEM", UtilExpression.class.getMethod("porcentagem", String.class, Number.class));
            context.setFunction("", "PORCENTAGEM", UtilExpression.class.getMethod("porcentagem", Number.class, Money.class));

        } catch (Exception ex) {
            logutil.error("Falha ao carregar funções", ex);
        }

        try {
            Map<String, Object> beanMapValue = PropertyUtils.describe(bean);
            if (beanMapValue != null) {
                properties.putAll(beanMapValue);
            }
        } catch (Exception ex) {
            logutil.error("Falha ao carregar descricao", ex);
        }

        if (propsExtra != null) {
            properties.putAll(propsExtra);
        }

        for (Entry<String, Object> e : properties.entrySet()) {
            if (e.getValue() != null) {
                context.setVariable(e.getKey(),
                        expFactory.createValueExpression(e.getValue(), e.getValue().getClass()));
            }
        }

        ValueExpression e = expFactory.createValueExpression(context, "${" + strExpr + "}", Object.class);
        Object value = e.getValue(context);
        /*
        String ret = null;
        if (value != null) {
            ret = value.toString();
        }
         */
        return value;
    }

    //Metodos
    public static String concatenar(Object... args) {
        String ret = null;
        if (args != null && args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (Object arg : args) {
                if (arg != null) {
                    if (arg instanceof Date) {
                        sb.append(UtilDate.formatData((Date) arg));
                    } else {
                        sb.append(arg);
                    }
                }
            }
            ret = sb.toString();
        }
        return ret;
    }

    public static Object se(boolean se, Object t, Object f) {
        if (se) {
            return t;
        } else {
            return f;
        }
    }

    public static Money dinheiro(String valor) {
        if (valor == null || valor.isEmpty()) {
            return new Money();
        }
        Money ret = null;
        try {
            ret = new Money(Long.parseLong(valor));
        } catch (Exception e) {
            ret = new Money(valor);
        }
        return ret;
    }

    public static Money dinheiro(Money valor) {
        if (valor == null) {
            return new Money();
        }
        return valor;
    }

    public static Money dinheiro(Number valor) {
        if (valor == null) {
            return new Money();
        }
        return new Money(valor.longValue());
    }

    public static String data(Date data) {
        if (data != null) {
            return UtilDate.formatData(data);
        }
        return null;
    }

    public static int dia(Date data) {
        return UtilDate.getDiaData(data);
    }

    public static int ano(Date data) {
        return UtilDate.getAnoData(data);
    }

    public static int mes(Date data) {
        return UtilDate.getMesData(data);
    }

    public static Date data(int ano, int mes, int dia) {
        Date data = null;
        data = UtilDate.parseData(ano, mes, dia);
        return data;
    }

    public static Number porcentagem(Number percent, Number valor) {
        Number ret = valor.doubleValue() * (percent.doubleValue() / 100.0);
        return ret;
    }

    public static Number porcentagem(String percent, Number valor) {
        Number ret = valor.doubleValue() * (Double.parseDouble(percent) / 100.0);
        return ret;
    }

    public static Money porcentagem(Number percent, Money valor) {
        Money porcentagem = valor.porcentagem(percent);
        return porcentagem;
    }
}
