package com.karine.utils;

import com.karine.data.IUseSessionData;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class GeneratorUtils {

    private final static IUseSessionData holder = new IUseSessionData() {
    };

    public static void generateRandom(int from, int to, String key) {
        holder.sessionData().put(key, ThreadLocalRandom.current().nextInt(from, to) + 1);
    }

    public static <T> T oneOf(T[] o) {
        int index = ThreadLocalRandom.current().nextInt(o.length);
        return o[index];
    }

    private static String[] currencies() {
        return new String[]{"USD", "EUR", "RUR"};
    }

    public static String generateCurrency() {
        return oneOf(currencies());
    }

    public static String generateCurrency(String currency) {
        String[] currencies = currencies();
        String[] tmp = new String[currencies.length - 1];
        int index = 0;
        for (String cur : currencies) {
            if (!cur.equals(currency)) {
                tmp[index++] = cur;
            }
        }
        return oneOf(tmp);
    }
}
