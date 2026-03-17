package com.example.AgentUsersApp.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AgentUsersApp.feature.home.domain.TaskRepository
import com.example.AgentUsersApp.feature.home.presentation.HomeFakeData.bottomNavigationItems
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskHomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskHomeUiState())
    val uiState: StateFlow<TaskHomeUiState> = _uiState.asStateFlow()
    private var observeHomeJob: Job? = null

    init {
        observeHome()
    }

    fun observeHome() {
        observeHomeJob?.cancel()
        observeHomeJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            repository.observeHome()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.localizedMessage ?: "Unable to load tasks.",
                            user = null,
                            summary = null,
                            inProgressTasks = emptyList(),
                            taskGroups = emptyList(),
                            bottomNavigationItems = bottomNavigationItems,
                        )
                    }
                }
                .collect { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            user = data.user,
                            summary = data.summary,
                            inProgressTasks = data.inProgressTasks,
                            taskGroups = data.taskGroups,
                            bottomNavigationItems = data.bottomNavigationItems,
                        )
                    }
                }
        }
    }
}
