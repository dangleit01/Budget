package com.th1b0.budget.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtil {

    public static String formatToUSD(double number) {
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(getLocalFromISO("USD"));
        return currencyInstance.format(number);
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
