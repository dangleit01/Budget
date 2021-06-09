package com.th1b0.budget.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

public final class CurrencyUtil {
    public static SortedMap<Currency, Locale> currencyLocaleMap;

    static {
        currencyLocaleMap = new TreeMap<Currency, Locale>(new Comparator<Currency>() {
            public int compare(Currency c1, Currency c2) {
                return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
            }
        });
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                currencyLocaleMap.put(currency, locale);
            } catch (Exception e) {
            }
        }
    }

    public static String formatToUSD(double number) {
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(getLocalFromISO("USD"));
        return currencyInstance.format(number);
    }

    public static String removeCurrencySymbol(String value) {
        return value.replace(getCurrencySymbol(Currency.getInstance(Locale.US).getCurrencyCode()), "");
    }

    public static String getCurrencySymbol(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        return currency.getSymbol(currencyLocaleMap.get(currency));
    }

    private static Locale getLocalFromISO(String iso4217code){
        Locale toReturn = null;
        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).
                    getCurrency().getCurrencyCode();
            if (iso4217code.equals(code)) {
                toReturn = locale;
                break;
            }
        }
        return toReturn;
    }
}
