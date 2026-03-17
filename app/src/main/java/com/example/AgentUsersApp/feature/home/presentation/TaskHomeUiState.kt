package com.example.AgentUsersApp.feature.home.presentation

data class TaskHomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val user: HomeUser? = null,
    val summary: TaskSummary? = null,
    val inProgressTasks: List<InProgressTask> = emptyList(),
    val taskGroups: List<TaskGroup> = emptyList(),
    val bottomNavigationItems: List<BottomNavItem> = emptyList(),
)
