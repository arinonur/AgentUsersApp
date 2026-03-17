package com.example.AgentUsersApp.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AgentUsersApp.feature.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.update {
            it.copy(
                username = username,
                error = null,
                successMessage = null,
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                error = null,
                successMessage = null,
            )
        }
    }

    fun login() {
        val currentState = _uiState.value
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.update {
                it.copy(error = "Username and password are required.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null,
                )
            }

            try {
                val result = repository.login(
                    email = currentState.username.trim(),
                    password = currentState.password,
                )

                val displayName = result.displayName.ifBlank { result.email }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error = null,
                        successMessage = "Welcome, $displayName",
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = e.localizedMessage ?: "Login failed.",
                    )
                }
            }
        }
    }
}
