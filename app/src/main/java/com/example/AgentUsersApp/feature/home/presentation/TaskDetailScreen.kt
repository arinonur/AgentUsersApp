package com.example.AgentUsersApp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TaskDetailScreenRoute(
    viewModel: TaskDetailViewModel,
    onEditClick: (String) -> Unit,
    onTaskDeleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onTaskDeleted()
        }
    }

    TaskDetailScreen(
        uiState = uiState,
        onEditClick = onEditClick,
        onToggleCompletedClick = viewModel::toggleCompleted,
        onDeleteClick = viewModel::deleteTask,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    uiState: TaskDetailUiState,
    onEditClick: (String) -> Unit,
    onToggleCompletedClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val task = uiState.task
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Task Detail") },
                actions = {
                    if (task != null) {
                        TextButton(onClick = { onEditClick(task.id) }) {
                            Text("Edit")
                        }
                    }
                },
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (task == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = uiState.errorMessage ?: "Task not found",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Text(
                text = task.category,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            AssistChip(
                onClick = {},
                enabled = false,
                label = {
                    Text(if (task.completed) "Completed" else "In Progress")
                },
                colors = AssistChipDefaults.assistChipColors(
                    disabledContainerColor = if (task.completed) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    disabledLabelColor = if (task.completed) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                ),
            )
            Text(
                text = "Progress ${(task.progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
            )
            LinearProgressIndicator(
                progress = { task.progress },
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onToggleCompletedClick,
                enabled = !uiState.isActionInProgress,
            ) {
                Text(
                    if (uiState.isActionInProgress) {
                        "Updating..."
                    } else if (task.completed) {
                        "Mark as Incomplete"
                    } else {
                        "Mark as Complete"
                    }
                )
            }
            Button(
                onClick = onDeleteClick,
                enabled = !uiState.isActionInProgress,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB3261E),
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text(if (uiState.isActionInProgress) "Deleting..." else "Delete Task")
            }
        }
    }
}
