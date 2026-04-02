package com.example.AgentUsersApp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.AgentUsersApp.BuildConfig
import com.example.AgentUsersApp.ui.theme.AgentUsersAppTheme

@Composable
fun TaskHomeScreenRoute(
    viewModel: TaskHomeViewModel,
    onLogoutClick: () -> Unit,
    onCreateTaskClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onBottomTabSelected: (Int) -> Unit = {},
    selectedTabIndex: Int = 0,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskHomeScreen(
        uiState = uiState,
        onLogoutClick = onLogoutClick,
        onCreateTaskClick = onCreateTaskClick,
        onTaskClick = onTaskClick,
        onBottomTabSelected = onBottomTabSelected,
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
    )
}

@Composable
fun TaskHomeScreen(
    uiState: TaskHomeUiState,
    onLogoutClick: () -> Unit,
    onCreateTaskClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onBottomTabSelected: (Int) -> Unit = {},
    selectedTabIndex: Int = 0,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading || uiState.user == null || uiState.summary == null) {
        if (!uiState.isLoading && uiState.errorMessage != null) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(onClick = onLogoutClick) {
                        Text("Go to Login")
                    }
                }
            }
            return
        }

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F8FC),
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTaskClick,
                containerColor = Color(0xFF7C5CFA),
                contentColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add task",
                )
            }
        },
        bottomBar = {
            HomeBottomBar(
                items = uiState.bottomNavigationItems,
                selectedIndex = selectedTabIndex,
                onSelected = onBottomTabSelected,
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                HomeHeader(
                    user = uiState.user,
                    onNotificationClick = {},
                )
            }

            item {
                TaskSummaryCard(
                    summary = uiState.summary,
                    onViewTasksClick = {},
                )
            }

            item {
                SectionTitle(title = "In Progress")
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 4.dp),
                ) {
                    items(uiState.inProgressTasks) { task ->
                        InProgressTaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) },
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionTitle(title = "Task Groups")
                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF2D3142),
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Log out")
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                item {
                    Button(
                        onClick = {
                            try {
                                CrashTestHelper.triggerComplexCrash(
                                    selectedTabIndex = selectedTabIndex,
                                    taskCount = uiState.inProgressTasks.size,
                                )
                            } catch (e: Exception) {
                                com.google.firebase.crashlytics.FirebaseCrashlytics
                                    .getInstance().recordException(e)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Trigger Crashlytics Test Crash")
                    }
                }
                item {
                    Button(
                        onClick = {
                            // BUG: this will crash with IndexOutOfBoundsException
                            CrashTestHelper.parseTaskAssignees("My Test Task")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B00),
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Test: Parse Task Assignees")
                    }
                }
            }

            items(uiState.taskGroups) { group ->
                TaskGroupRow(group = group)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8FC)
@Composable
private fun HomeScreenPreview() {
    AgentUsersAppTheme {
        TaskHomeScreen(
            uiState = TaskHomeUiState(
                isLoading = false,
                errorMessage = null,
                user = HomeFakeData.currentUser,
                summary = HomeFakeData.summary,
                inProgressTasks = HomeFakeData.inProgressTasks,
                taskGroups = HomeFakeData.taskGroups,
                bottomNavigationItems = HomeFakeData.bottomNavigationItems,
            ),
            onLogoutClick = {},
            onCreateTaskClick = {},
            onTaskClick = {},
            onBottomTabSelected = {},
            selectedTabIndex = 0,
        )
    }
}
