package com.example.studentregistration.data

import androidx.lifecycle.LiveData
import com.example.studentregistration.util.Validator

/**
 * Repository sits between the ViewModel and the DAO.
 * It also re-checks the year rule, so invalid data can never reach the database
 * even if a future screen forgets to validate (defence in depth).
 */
class StudentRepository(private val dao: StudentDao) {

    val studentCount: LiveData<Int> = dao.observeCount()

    suspend fun addStudent(student: Student): Long {
        require(Validator.isValidYear(student.year)) {
            "Year must be between ${Validator.MIN_YEAR} and ${Validator.MAX_YEAR}"
        }
        return dao.insert(student)
    }

    suspend fun findStudent(id: Int): Student? = dao.findById(id)
}
