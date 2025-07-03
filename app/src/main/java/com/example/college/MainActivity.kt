package com.example.college

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var studentDao: StudentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val database = AppDatabase.getDatabase(applicationContext)
        studentDao = database.studentDao()

        recyclerView = findViewById(R.id.mainRecyclerView) // Assuming this is your RecyclerView ID
        fab = findViewById(R.id.fab) // Your FAB ID

        setupRecyclerView()
        observeStudentData()

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, DataActivity::class.java)
            // No need to pass data for new student entry
            dataActivityResultLauncher.launch(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            onEditClick = { student ->
                // Handle Edit: Navigate to DataActivity with student data
                val intent = Intent(this@MainActivity, DataActivity::class.java)
                intent.putExtra(DataActivity.EXTRA_STUDENT_ID_PK, student.id) // Pass DB Primary Key
                intent.putExtra(DataActivity.EXTRA_STUDENT_NAME, student.studentName)
                intent.putExtra(DataActivity.EXTRA_STUDENT_NUMBER, student.studentId)
                intent.putExtra(DataActivity.EXTRA_STUDENT_CLASS, student.studentClass)
                dataActivityResultLauncher.launch(intent)
            },
            onDeleteClick = { student ->
                // Handle Delete
                deleteStudent(student)
            }
        )
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeStudentData() {
        studentDao.getAllStudents().observe(this) { students ->
            students?.let {
                studentAdapter.submitList(it)
            }
        }
    }

    private fun deleteStudent(student: Student) {
        lifecycleScope.launch { // Use lifecycleScope for coroutines in Activity
            withContext(Dispatchers.IO) { // Perform database operations on IO dispatcher
                studentDao.deleteStudent(student)
            }
            // LiveData will automatically update the UI
        }
    }

    // You might need an ActivityResultLauncher if DataActivity needs to signal back
    // (e.g., to force a refresh, though LiveData often handles this)
    private val dataActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Data changed, LiveData should pick it up.
                // You could explicitly re-fetch if needed, but LiveData is preferred.
            }
        }
}