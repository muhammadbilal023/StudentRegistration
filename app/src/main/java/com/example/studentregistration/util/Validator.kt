package com.example.studentregistration.util

/** Pure Kotlin validation rules (no Android classes, so they are easy to unit test). */
object Validator {
    const val MIN_YEAR = 1
    const val MAX_YEAR = 4

    /** A year is valid only if it is a whole number from 1 to 4. */
    fun isValidYear(year: Int?): Boolean = year != null && year in MIN_YEAR..MAX_YEAR

    fun isValidName(name: String): Boolean = name.isNotBlank()

    fun isValidCourse(course: String): Boolean = course.isNotBlank()
}
