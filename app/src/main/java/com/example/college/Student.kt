package com.example.college
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    indices = [Index(value = ["student_id"], unique = true)] // Ensure student_id is unique
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val studentName: String,
    @ColumnInfo(name = "student_id")
    val studentId: String,
    @ColumnInfo(name = "class_name")
    val studentClass: String
)
