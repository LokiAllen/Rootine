package com.example.coursework.database.subTask

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.coursework.database.task.Task
import java.util.Date

@Entity(
    tableName = "sub_tasks",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = arrayOf("taskId"),
            childColumns = arrayOf("taskId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"])] // Add this line to create an index on taskId
)
data class SubTask(
    @PrimaryKey(autoGenerate = true) val subTaskId: Int = 0,
    val taskId: Int,
    val title: String,
    val description: String,
    val goalTime: Long?,
    val completeTime: Long,
)
