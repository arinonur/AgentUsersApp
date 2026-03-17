package com.example.AgentUsersApp.feature.users.presentation

import com.example.AgentUsersApp.feature.users.domain.User

data class UserListUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)
