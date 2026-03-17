package com.example.AgentUsersApp.feature.home.domain

import com.example.AgentUsersApp.feature.home.data.TaskHomeData
import com.example.AgentUsersApp.feature.home.presentation.InProgressTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun loadHome(): TaskHomeData

    fun observeHome(): Flow<TaskHomeData>

    suspend fun getTask(taskId: String): InProgressTask?

    suspend fun createTask(task: CreateTaskInput): String

    suspend fun updateTask(task: CreateTaskInput)

    suspend fun setTaskCompleted(taskId: String, completed: Boolean)

    suspend fun deleteTask(taskId: String)
}

data class CreateTaskInput(
    val id: String = "",
    val category: String,
    val title: String,
    val description: String,
    val progress: Float,
    val completed: Boolean,
)
