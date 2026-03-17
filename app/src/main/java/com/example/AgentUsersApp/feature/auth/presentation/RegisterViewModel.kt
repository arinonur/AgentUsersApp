package com.example.AgentUsersApp.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AgentUsersApp.feature.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(fullName: String) {
        _uiState.update {
            it.copy(
                fullName = fullName,
                error = null,
                successMessage = null,
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
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

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                error = null,
                successMessage = null,
            )
        }
    }

    fun register() {
        val currentState = _uiState.value

        val validationError = validate(currentState)
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    error = validationError,
                    successMessage = null,
                    isRegistered = false,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null,
                    isRegistered = false,
                )
            }

            try {
                val result = repository.register(
                    email = currentState.email,
                    password = currentState.password,
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        successMessage = "Account created for ${result.email}",
                        isRegistered = true,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Registration failed.",
                        successMessage = null,
                        isRegistered = false,
                    )
                }
            }
        }
    }

    private fun validate(state: RegisterUiState): String? {
        if (state.email.isBlank()) return "Email is required."
        if (!EMAIL_REGEX.matches(state.email.trim())) return "Enter a valid email address."
        if (state.password.isBlank()) return "Password is required."
        if (state.password.length < 6) return "Password must be at least 6 characters."
        if (state.confirmPassword.isBlank()) return "Confirm password is required."
        if (state.password != state.confirmPassword) return "Passwords do not match."
        return null
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
