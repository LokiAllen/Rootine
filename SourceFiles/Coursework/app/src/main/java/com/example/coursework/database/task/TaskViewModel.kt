package com.example.coursework.database.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.coursework.database.TaskDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    val getNonComplete: LiveData<List<Task>>
    val getComplete: LiveData<List<Task>>
    private val repository: TaskRepository

    init{
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        getNonComplete = repository.getNonComplete
        getComplete = repository.getComplete
    }


    fun upsertTask(task: Task){
        viewModelScope.launch(Dispatchers.IO){
            repository.upsertTask(task)
        }
    }
    fun deleteTask(task: Task){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteTask(task)
        }
    }
    fun getTaskById(taskId: Int): LiveData<Task> {
        return repository.getTaskById(taskId)
    }

}