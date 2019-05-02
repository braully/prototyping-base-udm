package com.github.braully.constant;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;

public interface LocalizedConfiguration {

    public static final Locale LOCALE = new Locale("pt", "BR");
    public static final Currency CURRENCY = Currency.getInstance(LOCALE);
    public static final String CURRENCY_CODE = CURRENCY.getCurrencyCode();
    public static final DecimalFormatSymbols DECIMAL_FORMAT = new DecimalFormatSymbols(LOCALE);
//    public static final DecimalFormat FORMATADOR_MOEDA = new DecimalFormat("¤ ###,###,##0.00", FORMATADOR_MOEDA_SIMBOLOS);
    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("###,###,##0.00", DECIMAL_FORMAT);
    public static final DecimalFormat FORMAT_BANK_NUMBER = new DecimalFormat("000");
}
