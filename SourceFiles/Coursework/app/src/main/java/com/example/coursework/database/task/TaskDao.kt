package com.example.coursework.database.task

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert


@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM task WHERE completeTime = 0 ORDER BY importance DESC")
    fun getTaskByNotComplete(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE completeTime != 0 ORDER BY importance DESC")
    fun getTaskByComplete(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE taskId = :taskId")
    fun getTaskByID(taskId: Int): LiveData<Task>
}