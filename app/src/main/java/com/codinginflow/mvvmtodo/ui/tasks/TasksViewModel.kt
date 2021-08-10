package com.codinginflow.mvvmtodo.ui.tasks

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.core.constants.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.core.constants.EDIT_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.data.preferences.PreferencesManager
import com.codinginflow.mvvmtodo.data.preferences.SortOrder
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.data.task.TaskDao
import com.codinginflow.mvvmtodo.ui.tasks.TasksViewModel.Event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class TasksViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : AndroidViewModel(context as Application) {

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
            _events.emit(NavigateToAddTaskScreen)
        }
    }

    fun onAddedOrEditedTask(result: Int) {
        viewModelScope.launch {
            when (result) {
                ADD_TASK_RESULT_OK -> _events.emit(
                    ShowTaskSavedConfirmationMessage(
                        context.getString(R.string.task_deleted)
                    )
                )

                EDIT_TASK_RESULT_OK -> _events.emit(
                    ShowTaskSavedConfirmationMessage(
                        context.getString(R.string.task_edited)
                    )
                )
            }
        }
    }

    sealed class Event {
        object NavigateToAddTaskScreen : Event()
        class NavigateToEditTaskScreen(val task: Task) : Event()
        class ShowUndoDeleteTaskMessage(val task: Task) : Event()
        class ShowTaskSavedConfirmationMessage(val message: String) : Event()
    }
}
