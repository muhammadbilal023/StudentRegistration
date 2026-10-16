package com.example.studentregistration.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/** Data Access Object - the only place SQL lives. */
@Dao
interface StudentDao {

    /** Inserts a student and returns the auto-generated row id (= studentId). */
    @Insert
    suspend fun insert(student: Student): Long

    /** Returns the student with this ID, or null when no such row exists. */
    @Query("SELECT * FROM students WHERE studentId = :id LIMIT 1")
    suspend fun findById(id: Int): Student?

    /** LiveData that re-emits automatically every time the table changes. */
    @Query("SELECT COUNT(*) FROM students")
    fun observeCount(): LiveData<Int>
}
