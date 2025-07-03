package com.example.college

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.college.Student
import com.example.college.R


class StudentAdapter(
    private val onEditClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_data, parent, false) // Your item layout
        return StudentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val currentStudent = getItem(position)
        holder.bind(currentStudent, onEditClick, onDeleteClick)
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.sName)
        private val idTextView: TextView = itemView.findViewById(R.id.sID)
        private val classTextView: TextView = itemView.findViewById(R.id.sClass)
        private val editButton: Button = itemView.findViewById(R.id.editBtn) // Assuming ID is editBtn
        private val deleteButton: Button = itemView.findViewById(R.id.deleteBtn) // Assuming ID is deleteBtn

        fun bind(student: Student, onEdit: (Student) -> Unit, onDelete: (Student) -> Unit) {
            nameTextView.text = student.studentName
            idTextView.text = student.studentId // Displaying studentId (unique identifier)
            classTextView.text = student.studentClass

            editButton.setOnClickListener { onEdit(student) }
            deleteButton.setOnClickListener { onDelete(student) }
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id // Use the primary key for item identity
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem // Data class checks all fields
        }
    }
}