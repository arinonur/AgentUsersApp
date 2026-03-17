package com.example.AgentUsersApp.feature.home.presentation

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val progress: Float = 0f,
    val completed: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isTaskCreated: Boolean = false,
)
