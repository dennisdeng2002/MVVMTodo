package com.codinginflow.mvvmtodo.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.preferences.PreferencesManager
import com.codinginflow.mvvmtodo.data.preferences.SortOrder
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val preferences = preferencesManager.preferencesFlow

    val tasks: LiveData<List<Task>> = combine(
        searchQuery,
        preferences
    ) { searchQuery, preferences ->
        return@combine Pair(searchQuery, preferences)
    }
        .flatMapLatest { (searchQuery, preferences) ->
            taskDao.getTasks(searchQuery, preferences.sortOrder, preferences.hideCompleted)
        }
        .asLiveData()

    fun updateSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }
    }

    fun updateHideCompleted(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }
    }

    fun onTaskSelected(task: Task) {}

    fun onTaskUpdated(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
        }
    }
}
