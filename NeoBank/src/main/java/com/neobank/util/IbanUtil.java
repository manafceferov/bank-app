package com.neobank.util;

import java.util.Random;

public class IbanUtil {

    private static final String COUNTRY_CODE = "AZ";
    private static final String BANK_CODE = "NEO";
    private static final Random RANDOM = new Random();

    public static String generate() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return COUNTRY_CODE + BANK_CODE + sb;
    }
}