package com.neobank.util;

import java.util.Random;

public class CardUtil {

    private static final Random RANDOM = new Random();

    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateCvv() {
        int cvv = 100 + RANDOM.nextInt(900);
        return String.valueOf(cvv);
    }
}