package com.schedulemaker.utils;

public class GeneralUtility {

    public static boolean isEmptyOrNull(String data) {
        return (data == null || data.isBlank());
    }

    public static boolean hasLessCharactersThan(String data, int length) {
        return (data.length() >= length);
    }
}
