package com.example.coursework.database.task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0, // Primary key of the Task entity
    val title: String, // The display name of the task
    val description: String, // The description of the task
    val goalTime: Long?, // The Display name of the task
    val completeTime: Long?,
    val importance: Int =1, // Foreign key to the Importance entity
    val stage: Int =1 // Foreign key to the Stage entity
)
