package com.example.AgentUsersApp.feature.home.presentation

data class EditTaskUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val progress: Float = 0f,
    val completed: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isTaskUpdated: Boolean = false,
)
