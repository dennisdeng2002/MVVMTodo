package com.codinginflow.mvvmtodo.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.preferences.PreferencesManager
import com.codinginflow.mvvmtodo.data.preferences.SortOrder
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.data.task.TaskDao
import com.codinginflow.mvvmtodo.ui.tasks.TasksViewModel.Event.NavigateToEditTaskScreen
import com.codinginflow.mvvmtodo.ui.tasks.TasksViewModel.Event.ShowUndoDeleteTaskMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val preferences = preferencesManager.preferencesFlow

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    val tasks = combine(searchQuery, preferences) { searchQuery, preferences ->
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

    fun onTaskSelected(task: Task) {
        viewModelScope.launch {
            _events.emit(NavigateToEditTaskScreen(task))
        }
    }

    fun onTaskInserted(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun onTaskUpdated(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
        }
    }

    fun onTaskDeleted(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
            _events.emit(ShowUndoDeleteTaskMessage(task))
        }
    }

    fun onAddNewTaskClicked() {
        viewModelScope.launch {
            _events.emit(Event.NavigateToAddTaskScreen)
        }
    }

    sealed class Event {
        object NavigateToAddTaskScreen : Event()
        class NavigateToEditTaskScreen(val task: Task) : Event()
        class ShowUndoDeleteTaskMessage(val task: Task) : Event()
    }
}
