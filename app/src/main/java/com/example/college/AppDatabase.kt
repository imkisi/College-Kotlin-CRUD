package com.example.college

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not covered in this basic example.
                    .fallbackToDestructiveMigration() // Be careful with this in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}