package com.example.coursework.database.task

import androidx.lifecycle.LiveData
import com.example.coursework.database.task.Task
import com.example.coursework.database.task.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(private val taskDao: TaskDao) {

    val getNonComplete: LiveData<List<Task>> = taskDao.getTaskByNotComplete()
    val getComplete: LiveData<List<Task>> = taskDao.getTaskByComplete()

    suspend fun upsertTask(task: Task){
        taskDao.upsertTask(task)
    }
    suspend fun deleteTask(task: Task){
        taskDao.deleteTask(task)
    }

    fun getTaskById(taskId: Int): LiveData<Task> {
        return taskDao.getTaskByID(taskId)
    }

}