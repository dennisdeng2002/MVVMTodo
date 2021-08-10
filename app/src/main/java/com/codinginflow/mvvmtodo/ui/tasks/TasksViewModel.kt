package com.codinginflow.mvvmtodo.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.data.task.TaskDao
import com.codinginflow.mvvmtodo.ui.tasks.enums.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_CREATED_AT)
    val hideCompleted = MutableStateFlow(false)

    val tasks: LiveData<List<Task>> = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ) { searchQuery, sortOrder, hideCompleted ->
        return@combine Triple(searchQuery, sortOrder, hideCompleted)
    }
        .flatMapLatest { (searchQuery, sortOrder, hideCompleted) ->
            taskDao.getTasks(searchQuery, sortOrder, hideCompleted)
        }
        .asLiveData()
}
