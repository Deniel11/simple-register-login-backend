package com.simpleregisterlogin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralUtility {

    private static final String DATE_PATTERN =
            "(^0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4}$)";

    private static final String EMAIL_PATTERN =
            "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public static boolean isEmptyOrNull(String data) {
        return (data == null || data.isBlank());
    }

    public static boolean hasLessCharactersThan(String data, int length) {
        return (data.length() >= length);
    }

    public static boolean isValidDate(String date) {
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
