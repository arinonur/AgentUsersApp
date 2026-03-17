package com.example.AgentUsersApp.feature.auth.presentation

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isLoggedIn: Boolean = false,
)

