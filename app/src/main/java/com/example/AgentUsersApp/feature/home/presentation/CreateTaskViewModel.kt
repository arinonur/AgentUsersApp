package com.example.AgentUsersApp.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AgentUsersApp.feature.home.domain.CreateTaskInput
import com.example.AgentUsersApp.feature.home.domain.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    private val repository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update {
            it.copy(
                description = description,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun onCategoryChange(category: String) {
        _uiState.update {
            it.copy(
                category = category,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun onProgressChange(progress: Float) {
        _uiState.update {
            it.copy(
                progress = progress.coerceIn(0f, 1f),
                completed = if (it.completed) progress >= 1f else it.completed,
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun onCompletedChange(completed: Boolean) {
        _uiState.update {
            it.copy(
                completed = completed,
                progress = if (completed) 1f else it.progress.coerceIn(0f, 1f),
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun createTask() {
        val currentState = _uiState.value
        val validationError = validate(currentState)

        if (validationError != null) {
            _uiState.update {
                it.copy(
                    errorMessage = validationError,
                    successMessage = null,
                    isTaskCreated = false,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null,
                    isTaskCreated = false,
                )
            }

            try {
                repository.createTask(
                    task = CreateTaskInput(
                        title = currentState.title.trim(),
                        description = currentState.description.trim(),
                        category = currentState.category.trim(),
                        progress = currentState.progress,
                        completed = currentState.completed,
                    )
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        successMessage = "Task created successfully.",
                        isTaskCreated = true,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Unable to create task.",
                        successMessage = null,
                        isTaskCreated = false,
                    )
                }
            }
        }
    }

    private fun validate(state: CreateTaskUiState): String? {
        if (state.title.trim().isEmpty()) return "Title is required."
        return null
    }
}
