package com.codinginflow.mvvmtodo.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    taskDao: TaskDao
) : ViewModel() {

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val tasks: LiveData<List<Task>> = _searchQuery
        .flatMapLatest { query -> taskDao.getTasks("%${query}%") }
        .asLiveData()
}
