package com.example.college

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var numberEditText: EditText // For studentId (unique identifier)
    private lateinit var classEditText: EditText
    private lateinit var saveButton: Button // Assuming squareBtn is your save button

    private lateinit var studentDao: StudentDao
    private var currentStudentDbId: Int? = null // To store the DB Primary Key of the student being edited

    companion object {
        const val EXTRA_STUDENT_ID_PK = "com.example.college.EXTRA_STUDENT_ID_PK"
        const val EXTRA_STUDENT_NAME = "com.example.college.EXTRA_STUDENT_NAME"
        const val EXTRA_STUDENT_NUMBER = "com.example.college.EXTRA_STUDENT_NUMBER"
        const val EXTRA_STUDENT_CLASS = "com.example.college.EXTRA_STUDENT_CLASS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data)

        nameEditText = findViewById(R.id.nameText)
        numberEditText = findViewById(R.id.numberText)
        classEditText = findViewById(R.id.classText)
        saveButton = findViewById(R.id.squareBtn) // Your save button ID

        val database = AppDatabase.getDatabase(applicationContext)
        studentDao = database.studentDao()

        // Check if we are in edit mode
        if (intent.hasExtra(EXTRA_STUDENT_ID_PK)) {
            title = "Edit Student" // Change activity title for edit mode
            currentStudentDbId = intent.getIntExtra(EXTRA_STUDENT_ID_PK, -1)
            nameEditText.setText(intent.getStringExtra(EXTRA_STUDENT_NAME))
            numberEditText.setText(intent.getStringExtra(EXTRA_STUDENT_NUMBER))
            classEditText.setText(intent.getStringExtra(EXTRA_STUDENT_CLASS))
        } else {
            title = "Add New Student"
        }

        saveButton.setOnClickListener {
            saveOrUpdateStudent()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveOrUpdateStudent() {
        val name = nameEditText.text.toString().trim()
        val studentIdNumber = numberEditText.text.toString().trim() // This is the unique student ID
        val className = classEditText.text.toString().trim()

        if (name.isEmpty() || studentIdNumber.isEmpty() || className.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (currentStudentDbId != null && currentStudentDbId != -1) {
                        // Update existing student
                        val studentToUpdate = Student(
                            id = currentStudentDbId!!, // Use the existing DB PK
                            studentName = name,
                            studentId = studentIdNumber,
                            studentClass = className
                        )
                        studentDao.updateStudent(studentToUpdate)
                    } else {
                        // Insert new student
                        // Check if studentIdNumber already exists (optional, but good practice if not relying solely on DB index)
                        // This check is more complex if you need to show specific UI error before DB attempts insert
                        val newStudent = Student(
                            studentName = name,
                            studentId = studentIdNumber,
                            studentClass = className
                        )
                        studentDao.insertStudent(newStudent)
                    }
                }
                setResult(Activity.RESULT_OK) // Signal success
                finish() // Go back to MainActivity
            } catch (e: Exception) {
                // This catch block is important, especially for the unique index constraint on student_id
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DataActivity,
                        "Error saving data. Student ID might already exist.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                // Log.e("DataActivity", "Error saving student", e)
            }
        }
    }
}