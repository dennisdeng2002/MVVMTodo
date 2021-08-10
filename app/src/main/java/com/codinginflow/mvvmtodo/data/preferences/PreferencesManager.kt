package com.codinginflow.mvvmtodo.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codinginflow.mvvmtodo.data.preferences.PreferencesManager.Companion.PreferencesKeys.HIDE_COMPLETED
import com.codinginflow.mvvmtodo.data.preferences.PreferencesManager.Companion.PreferencesKeys.SORT_ORDER
import com.codinginflow.mvvmtodo.data.preferences.SortOrder.BY_CREATED_AT
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class SortOrder {
    BY_NAME, BY_CREATED_AT
}

data class FilterPreferences(
    val sortOrder: SortOrder,
    val hideCompleted: Boolean
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {

    companion object {
        const val DATA_STORE_FILE_NAME = "user_preferences"
        private const val TAG = "PreferencesManager"

        object PreferencesKeys {
            val SORT_ORDER = stringPreferencesKey("sort_order")
            val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
        }

    }

    private val Context.dataStore by preferencesDataStore(name = DATA_STORE_FILE_NAME)
    private val dataStore = context.dataStore

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(preferences[SORT_ORDER] ?: BY_CREATED_AT.name)
            val hideCompleted = preferences[HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit {
            it[SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit {
            it[HIDE_COMPLETED] = hideCompleted
        }
    }
}
