package com.example.AgentUsersApp.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditTaskScreenRoute(
    viewModel: EditTaskViewModel,
    onBackClick: () -> Unit,
    onTaskUpdated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isTaskUpdated) {
        if (uiState.isTaskUpdated) {
            onTaskUpdated()
        }
    }

    EditTaskScreen(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCategoryChange = viewModel::onCategoryChange,
        onProgressChange = viewModel::onProgressChange,
        onCompletedChange = viewModel::onCompletedChange,
        onSaveClick = viewModel::updateTask,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun EditTaskScreen(
    uiState: EditTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onProgressChange: (Float) -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TaskEditorScreen(
        title = "Edit Task",
        buttonText = "Save Changes",
        titleValue = uiState.title,
        descriptionValue = uiState.description,
        categoryValue = uiState.category,
        progressValue = uiState.progress,
        completedValue = uiState.completed,
        isLoading = uiState.isLoading,
        isSubmitting = uiState.isSaving,
        errorMessage = uiState.errorMessage,
        successMessage = uiState.successMessage,
        onTitleChange = onTitleChange,
        onDescriptionChange = onDescriptionChange,
        onCategoryChange = onCategoryChange,
        onProgressChange = onProgressChange,
        onCompletedChange = onCompletedChange,
        onSubmitClick = onSaveClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}
