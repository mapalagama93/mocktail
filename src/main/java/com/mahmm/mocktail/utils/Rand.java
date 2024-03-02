package com.mahmm.mocktail.utils;

import java.util.UUID;

public class Rand {

    public static String random() {
        return UUID.randomUUID().toString().replace("-", ":");
    }

    public static String small() {
        return UUID.randomUUID().toString().split("-")[4] + "-" + UUID.randomUUID().toString().split("-")[3];
    }
}
