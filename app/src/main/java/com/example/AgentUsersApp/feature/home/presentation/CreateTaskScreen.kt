package com.example.AgentUsersApp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateTaskScreenRoute(
    viewModel: CreateTaskViewModel,
    onBackClick: () -> Unit,
    onTaskCreated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isTaskCreated) {
        if (uiState.isTaskCreated) {
            onTaskCreated()
        }
    }

    CreateTaskScreen(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCategoryChange = viewModel::onCategoryChange,
        onProgressChange = viewModel::onProgressChange,
        onCompletedChange = viewModel::onCompletedChange,
        onCreateClick = viewModel::createTask,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    uiState: CreateTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onProgressChange: (Float) -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TaskEditorScreen(
        title = "Create Task",
        buttonText = "Create Task",
        titleValue = uiState.title,
        descriptionValue = uiState.description,
        categoryValue = uiState.category,
        progressValue = uiState.progress,
        completedValue = uiState.completed,
        isLoading = false,
        isSubmitting = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        successMessage = uiState.successMessage,
        onTitleChange = onTitleChange,
        onDescriptionChange = onDescriptionChange,
        onCategoryChange = onCategoryChange,
        onProgressChange = onProgressChange,
        onCompletedChange = onCompletedChange,
        onSubmitClick = onCreateClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    title: String,
    buttonText: String,
    titleValue: String,
    descriptionValue: String,
    categoryValue: String,
    progressValue: Float,
    completedValue: Boolean,
    isLoading: Boolean,
    isSubmitting: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onProgressChange: (Float) -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(title) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (isLoading) {
                Text(
                    text = "Loading task...",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            OutlinedTextField(
                value = titleValue,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                placeholder = { Text("Enter task title") },
                singleLine = true,
                enabled = !isLoading && !isSubmitting,
            )

            OutlinedTextField(
                value = descriptionValue,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                placeholder = { Text("Enter description") },
                enabled = !isLoading && !isSubmitting,
                minLines = 3,
            )

            OutlinedTextField(
                value = categoryValue,
                onValueChange = onCategoryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Category") },
                placeholder = { Text("General") },
                singleLine = true,
                enabled = !isLoading && !isSubmitting,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Progress ${((if (completedValue) 1f else progressValue) * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Slider(
                    value = if (completedValue) 1f else progressValue,
                    onValueChange = onProgressChange,
                    enabled = !isLoading && !isSubmitting && !completedValue,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Switch(
                    checked = completedValue,
                    onCheckedChange = onCompletedChange,
                    enabled = !isLoading && !isSubmitting,
                )
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            successMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Button(
                onClick = onSubmitClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && !isSubmitting,
            ) {
                Text(if (isSubmitting) "Saving..." else buttonText)
            }

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
            ) {
                Text("Cancel")
            }
        }
    }
}
