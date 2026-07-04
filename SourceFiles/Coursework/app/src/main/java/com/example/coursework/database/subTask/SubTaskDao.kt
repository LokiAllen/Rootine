package com.example.coursework.database.subTask

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SubTaskDao {

    @Upsert
    suspend fun upsertSubTask(subTask: SubTask)

    @Delete
    suspend fun deleteSubTask(subTask: SubTask)

    @Query("SELECT * FROM sub_tasks WHERE completeTime = 0")
    fun getSubTaskByNotComplete(): LiveData<List<SubTask>>

    @Query("SELECT * FROM sub_tasks WHERE completeTime != 0")
    fun getSubTaskByComplete(): LiveData<List<SubTask>>

    @Query("SELECT * FROM sub_tasks WHERE taskId = :taskId")
    fun getSubTaskByTaskId(taskId: Int): LiveData<List<SubTask>>

    @Query("SELECT * FROM sub_tasks WHERE subTaskId = :subTaskId")
    fun getSubTaskById(subTaskId: Int): LiveData<SubTask>
}

