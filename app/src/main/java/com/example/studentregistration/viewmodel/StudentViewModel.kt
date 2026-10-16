package com.example.studentregistration.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentregistration.R
import com.example.studentregistration.data.AppDatabase
import com.example.studentregistration.data.Student
import com.example.studentregistration.data.StudentRepository
import com.example.studentregistration.util.Validator
import kotlinx.coroutines.launch

/** Result of pressing "Register". */
sealed class RegisterState {
    data class Invalid(
        val nameError: String?,
        val courseError: String?,
        val yearError: String?
    ) : RegisterState()

    data class Success(val studentId: Int) : RegisterState()
}

/** Result of pressing "Search". */
sealed class SearchState {
    data class Found(val student: Student) : SearchState()
    data class NotFound(val id: Int) : SearchState()
    object InvalidInput : SearchState()
}

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepository(AppDatabase.getInstance(application).studentDao())

    /** Live count of students - the Home page observes this. */
    val studentCount: LiveData<Int> = repository.studentCount

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private fun text(resId: Int): String = getApplication<Application>().getString(resId)

    /** Validates the form; if everything is OK, saves the student in Room. */
    fun register(name: String, course: String, yearText: String) {
        val cleanName = name.trim()
        val cleanCourse = course.trim()
        val year = yearText.trim().toIntOrNull()

        val nameError = if (Validator.isValidName(cleanName)) null else text(R.string.error_name_required)
        val courseError = if (Validator.isValidCourse(cleanCourse)) null else text(R.string.error_course_required)
        val yearError = if (Validator.isValidYear(year)) null else text(R.string.error_year_range)

        if (nameError != null || courseError != null || yearError != null || year == null) {
            _registerState.value = RegisterState.Invalid(nameError, courseError, yearError)
            return
        }

        viewModelScope.launch {
            val newId = repository.addStudent(Student(name = cleanName, course = cleanCourse, year = year))
            _registerState.value = RegisterState.Success(newId.toInt())
        }
    }

    /** Looks a student up by the ID typed in the search box. */
    fun searchStudent(idText: String) {
        val id = idText.trim().toIntOrNull()
        if (id == null) {
            _searchState.value = SearchState.InvalidInput
            return
        }
        viewModelScope.launch {
            val student = repository.findStudent(id)
            _searchState.value =
                if (student != null) SearchState.Found(student) else SearchState.NotFound(id)
        }
    }
}
