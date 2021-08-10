package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.data.TodoDatabase.Companion.DATABASE_VERSION
import com.codinginflow.mvvmtodo.data.task.Task
import com.codinginflow.mvvmtodo.data.task.TaskDao
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = DATABASE_VERSION)
abstract class TodoDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "todo"
    }

    abstract val taskDao: TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TodoDatabase>,
        @ApplicationScope private val appCoroutineScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val taskDao = database.get().taskDao
            appCoroutineScope.launch {
                taskDao.insert(Task("Wash the dishes"))
                taskDao.insert(Task("Do the laundry"))
                taskDao.insert(Task("Buy groceries", important = true))
                taskDao.insert(Task("Prepare food", completed = true))
                taskDao.insert(Task("Call mom"))
                taskDao.insert(Task("Visit grandma", completed = true))
                taskDao.insert(Task("Repair my bike"))
                taskDao.insert(Task("Call Elon Musk"))
            }
        }
    }
}
