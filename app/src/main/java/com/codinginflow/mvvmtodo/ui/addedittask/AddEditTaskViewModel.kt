package com.codinginflow.mvvmtodo.ui.addedittask

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.core.constants.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.core.constants.EDIT_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.data.task.TaskDao
import com.codinginflow.mvvmtodo.ui.addedittask.AddEditTaskViewModel.Event.NavigateBackWithResult
import com.codinginflow.mvvmtodo.ui.addedittask.AddEditTaskViewModel.Event.ShowInvalidInputMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class AddEditTaskViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
) : AndroidViewModel(context as Application) {

    companion object {
        private const val TASK_KEY = "task"
    }

    private val taskId: Long = state.get("task_id") ?: -1L

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    private var taskCopy = Task()
    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    init {
        viewModelScope.launch {
            val task = state.get<Task?>(TASK_KEY) ?: taskDao.getTaskById(taskId) ?: Task()
            _task.value = task
            taskCopy = task
        }
    }

    fun setName(name: String) {
        taskCopy = taskCopy.copy(name = name)
        state.set(TASK_KEY, taskCopy)
    }

    fun setImportant(important: Boolean) {
        taskCopy = taskCopy.copy(important = important)
        state.set(TASK_KEY, taskCopy)
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            val task = taskCopy
            if (task.name.isBlank()) {
                _events.emit(ShowInvalidInputMessage(context.getString(R.string.name_error)))
                return@launch
            }

            if (taskId == -1L) {
                taskDao.insert(task)
                _events.emit(NavigateBackWithResult(ADD_TASK_RESULT_OK))
            } else {
                taskDao.update(task)
                _events.emit(NavigateBackWithResult(EDIT_TASK_RESULT_OK))
            }
        }
    }

    sealed class Event {
        data class ShowInvalidInputMessage(val message: String) : Event()
        data class NavigateBackWithResult(val result: Int) : Event()
    }
}
