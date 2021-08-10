package com.codinginflow.mvvmtodo.ui.deleteallcompleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    fun onConfirmClicked() {
        viewModelScope.launch {
            taskDao.deleteAllCompleted()
        }
    }
}
