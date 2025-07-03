package com.example.college

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Use REPLACE for easier update/insert
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): LiveData<List<Student>> // Observe changes with LiveData

    @Query("SELECT * FROM students WHERE id = :studentDbId")
    suspend fun getStudentById(studentDbId: Int): Student?
}