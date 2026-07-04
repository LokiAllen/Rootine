package com.example.coursework.database.subTask

import androidx.lifecycle.LiveData
import com.example.coursework.database.subTask.SubTaskDao
import com.example.coursework.database.subTask.SubTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubTaskRepository(private val subTaskDao: SubTaskDao) {

    val getCompleteSubTask: LiveData<List<SubTask>> = subTaskDao.getSubTaskByComplete()
    val getNonCompleteSubTask: LiveData<List<SubTask>> = subTaskDao.getSubTaskByComplete()

    suspend fun upsertSubTask(subTask: SubTask){
        subTaskDao.upsertSubTask(subTask)
    }
    suspend fun deleteSubTask(subTask: SubTask){
        subTaskDao.deleteSubTask(subTask)
    }

    fun getSubTaskById(subTaskId: Int): LiveData<SubTask> {
        return subTaskDao.getSubTaskById(subTaskId)
    }

    fun getSubTaskByTaskId(taskId: Int): LiveData<List<SubTask>> {
        return subTaskDao.getSubTaskByTaskId(taskId)
    }

}