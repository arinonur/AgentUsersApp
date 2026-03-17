package com.example.AgentUsersApp.feature.home.presentation

data class TaskDetailUiState(
    val isLoading: Boolean = true,
    val isActionInProgress: Boolean = false,
    val isDeleted: Boolean = false,
    val task: InProgressTask? = null,
    val errorMessage: String? = null,
)
