package com.github.braully.web.jsf;

import java.util.Locale;
import java.util.Objects;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
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
@Component("converterMonetary")
@Qualifier("converterMonetary")
@FacesConverter("converterMonetary")
public class ConverterMonetary implements Converter {

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
