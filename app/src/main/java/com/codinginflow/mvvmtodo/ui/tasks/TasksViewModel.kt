package com.codinginflow.mvvmtodo.ui.tasks

import androidx.lifecycle.ViewModel
import com.codinginflow.mvvmtodo.data.task.TaskDao

class TasksViewModel(
    private val taskDao: TaskDao
) : ViewModel()
