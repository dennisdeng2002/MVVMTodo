package com.codinginflow.mvvmtodo.data.task

import androidx.room.*
import com.codinginflow.mvvmtodo.ui.tasks.enums.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(
        searchQuery: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Task>> {
        val cleanedQuery = "%${searchQuery}%"
        return if (hideCompleted) when (sortOrder) {
            SortOrder.BY_NAME -> getUncompletedTasksByName(cleanedQuery)
            SortOrder.BY_CREATED_AT -> getUncompletedTasksByCreatedAt(cleanedQuery)
        } else when (sortOrder) {
            SortOrder.BY_NAME -> getTasksByName(cleanedQuery)
            SortOrder.BY_CREATED_AT -> getTasksByCreatedAt(cleanedQuery)
        }
    }

    // Assumes that searchQuery is passed in like "%foo%"
    @Query("SELECT * from task WHERE completed == 0 AND name LIKE :searchQuery ORDER BY important, name DESC")
    fun getUncompletedTasksByName(searchQuery: String): Flow<List<Task>>

    // Assumes that searchQuery is passed in like "%foo%"
    @Query("SELECT * from task WHERE completed == 0 AND name LIKE :searchQuery ORDER BY important, createdAt DESC")
    fun getUncompletedTasksByCreatedAt(searchQuery: String): Flow<List<Task>>

    // Assumes that searchQuery is passed in like "%foo%"
    @Query("SELECT * from task WHERE name LIKE :searchQuery ORDER BY important, name DESC")
    fun getTasksByName(searchQuery: String): Flow<List<Task>>

    // Assumes that searchQuery is passed in like "%foo%"
    @Query("SELECT * from task WHERE name LIKE :searchQuery ORDER BY important, createdAt DESC")
    fun getTasksByCreatedAt(searchQuery: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
