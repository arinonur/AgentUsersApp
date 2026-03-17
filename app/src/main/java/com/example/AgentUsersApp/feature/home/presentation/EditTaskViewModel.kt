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

class EditTaskViewModel(
    private val taskId: String,
    private val repository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

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

    fun loadTask() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null,
                    isTaskUpdated = false,
                )
            }

            try {
                val task = repository.getTask(taskId)
                if (task == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Task not found.",
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        title = task.title,
                        description = task.description,
                        category = task.category,
                        progress = task.progress,
                        completed = task.completed,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Unable to load task.",
                    )
                }
            }
        }
    }

    fun updateTask() {
        val currentState = _uiState.value
        val validationError = validate(currentState)
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    errorMessage = validationError,
                    successMessage = null,
                    isTaskUpdated = false,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null,
                    isTaskUpdated = false,
                )
            }

            try {
                repository.updateTask(
                    CreateTaskInput(
                        id = taskId,
                        title = currentState.title.trim(),
                        description = currentState.description.trim(),
                        category = currentState.category.trim(),
                        progress = currentState.progress,
                        completed = currentState.completed,
                    )
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = null,
                        successMessage = "Task updated successfully.",
                        isTaskUpdated = true,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.localizedMessage ?: "Unable to update task.",
                        successMessage = null,
                        isTaskUpdated = false,
                    )
                }
            }
        }
    }

    private fun validate(state: EditTaskUiState): String? {
        if (state.title.trim().isEmpty()) return "Title is required."
        return null
    }
}
