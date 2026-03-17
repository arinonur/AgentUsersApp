package com.example.AgentUsersApp.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AgentUsersApp.feature.home.domain.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskId: String,
    private val repository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    fun loadTask() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isDeleted = false,
                    errorMessage = null,
                )
            }

            try {
                val task = repository.getTask(taskId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isActionInProgress = false,
                        task = task,
                        errorMessage = if (task == null) "Task not found" else null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isActionInProgress = false,
                        task = null,
                        errorMessage = e.localizedMessage ?: "Unable to load task.",
                    )
                }
            }
        }
    }

    fun toggleCompleted() {
        val task = _uiState.value.task ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isActionInProgress = true,
                    errorMessage = null,
                )
            }

            try {
                repository.setTaskCompleted(
                    taskId = task.id,
                    completed = !task.completed,
                )
                loadTask()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isActionInProgress = false,
                        errorMessage = e.localizedMessage ?: "Unable to update task.",
                    )
                }
            }
        }
    }

    fun deleteTask() {
        val task = _uiState.value.task ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isActionInProgress = true,
                    errorMessage = null,
                )
            }

            try {
                repository.deleteTask(task.id)
                _uiState.update {
                    it.copy(
                        isActionInProgress = false,
                        isDeleted = true,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isActionInProgress = false,
                        errorMessage = e.localizedMessage ?: "Unable to delete task.",
                    )
                }
            }
        }
    }
}
