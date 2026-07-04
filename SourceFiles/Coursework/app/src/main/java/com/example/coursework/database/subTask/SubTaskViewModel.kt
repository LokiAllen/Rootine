package com.example.coursework.database.subTask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.coursework.database.TaskDatabase // Use TaskDatabase instead of SubTaskDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubTaskViewModel(application: Application): AndroidViewModel(application) {

    val getNonCompleteSubTask: LiveData<List<SubTask>>
    val getCompleteSubTask: LiveData<List<SubTask>>
    private val repository: SubTaskRepository

    init{
        val subTaskDao = TaskDatabase.getDatabase(application).subTaskDao() // Changed to TaskDatabase
        repository = SubTaskRepository(subTaskDao)
        getNonCompleteSubTask = repository.getNonCompleteSubTask
        getCompleteSubTask = repository.getCompleteSubTask
    }

    fun upsertSubTask(subTask: SubTask){
        viewModelScope.launch(Dispatchers.IO){
            repository.upsertSubTask(subTask)
        }
    }
    fun deleteSubTask(subTask: SubTask){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteSubTask(subTask)
        }
    }
    fun getSubTaskById(subTaskId: Int): LiveData<SubTask> {
        return repository.getSubTaskById(subTaskId)
    }
    fun getSubTasksByTaskId(taskId: Int): LiveData<List<SubTask>> {
        return repository.getSubTaskByTaskId(taskId)
    }

}