package com.codinginflow.mvvmtodo.ui.addedittask

import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = state.get("task_id") ?: -1L

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    init {
        viewModelScope.launch {
            val task = state.get<Task?>("task") ?: taskDao.getTaskById(taskId) ?: Task()
            _task.value = task
        }
    }
}
