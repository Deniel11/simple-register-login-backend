package com.schedulemaker.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneralUtilityTest {

    @Test
    void isEmptyOrNull_WithValidData_ReturnsFalse() {
        Assertions.assertFalse(GeneralUtility.isEmptyOrNull("data"));
    }

    @Test
    void isEmptyOrNull_WithEmptyData_ReturnsTrue() {
        Assertions.assertTrue(GeneralUtility.isEmptyOrNull(""));
    }

    @Test
    void isEmptyOrNull_WithNull_ReturnsTrue() {
        Assertions.assertTrue(GeneralUtility.isEmptyOrNull(null));
    }

    @Test
    void isLongEnough_WithValidDataAndLength_ReturnsTrue() {
        Assertions.assertTrue(GeneralUtility.hasLessCharactersThan("data", 3));
    }

    @Test
    void isLongEnough_WithValidDataAndBiggerLength_ReturnsFalse() {
        Assertions.assertFalse(GeneralUtility.hasLessCharactersThan("data", 10));
    }

    @Test
    void isValidDate_WithValidDate_ReturnTrue() {
        Assertions.assertTrue(GeneralUtility.isValidDate("01-01-1900"));
    }

    @Test
    void isValidDate_WithInvalidDate_ReturnFalse() {
        Assertions.assertFalse(GeneralUtility.isValidDate("01-01-19000"));
    }

    @Test
    void isValidEmail_WithValidEmail_ReturnTrue() {
        Assertions.assertTrue(GeneralUtility.isValidEmail("valid@email.com"));
    }

    @Test
    void isValidEmail_WithInvalidEmail_ReturnFalse() {
        Assertions.assertFalse(GeneralUtility.isValidEmail("invalid@email"));
    }
}
