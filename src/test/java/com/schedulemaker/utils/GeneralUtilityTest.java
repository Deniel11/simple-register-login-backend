package com.schedulemaker.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneralUtilityTest {

    @Test
    void isEmptyOrNull_withValidData_returnsFalse() {
        Assertions.assertFalse(GeneralUtility.isEmptyOrNull("data"));
    }

    @Test
    void isEmptyOrNull_withEmptyData_returnsTrue() {
        Assertions.assertTrue(GeneralUtility.isEmptyOrNull(""));
    }

    @Test
    void isEmptyOrNull_withNull_returnsTrue() {
        Assertions.assertTrue(GeneralUtility.isEmptyOrNull(null));
    }

    @Test
    void isLongEnough_withValidDataAndLength_returnsTrue() {
        Assertions.assertTrue(GeneralUtility.hasLessCharactersThan("data", 3));
    }

    @Test
    void isLongEnough_withValidDataAndBiggerLength_returnsFalse() {
        Assertions.assertFalse(GeneralUtility.hasLessCharactersThan("data", 10));
    }
}
