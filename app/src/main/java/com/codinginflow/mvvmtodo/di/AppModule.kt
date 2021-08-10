package com.codinginflow.mvvmtodo.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codinginflow.mvvmtodo.data.TodoDatabase
import com.codinginflow.mvvmtodo.data.task.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        context: Context,
        callback: RoomDatabase.Callback
    ): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            TodoDatabase.DATABASE_NAME
        )
            .addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTaskDao(database: TodoDatabase): TaskDao = database.taskDao

    @ApplicationScope
    @Provides
    fun provideAppCoroutineScope(): CoroutineScope = CoroutineScope(Job())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
