package com.example.studentregistration

import com.example.studentregistration.util.Validator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for the year rule (1-4). Run in Android Studio: right-click the file > Run. */
class ValidatorTest {

    @Test
    fun years_1_to_4_are_valid() {
        for (y in 1..4) assertTrue(Validator.isValidYear(y))
    }

    @Test
    fun year_0_is_invalid() {
        assertFalse(Validator.isValidYear(0))
    }

    @Test
    fun year_5_is_invalid() {
        assertFalse(Validator.isValidYear(5))
    }

    @Test
    fun negative_year_is_invalid() {
        assertFalse(Validator.isValidYear(-2))
    }

    @Test
    fun missing_year_is_invalid() {
        assertFalse(Validator.isValidYear(null))
    }

    @Test
    fun blank_name_and_course_are_invalid() {
        assertFalse(Validator.isValidName("   "))
        assertFalse(Validator.isValidCourse(""))
    }
}
