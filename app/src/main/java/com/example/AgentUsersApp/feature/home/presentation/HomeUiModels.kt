package com.example.AgentUsersApp.feature.home.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class HomeUser(
    val fullName: String,
    val initials: String,
)

data class TaskSummary(
    val completion: Float,
    val title: String,
)

data class InProgressTask(
    val id: String,
    val category: String,
    val title: String,
    val progress: Float,
    val description: String,
    val containerColor: Color,
    val completed: Boolean = false,
)

data class TaskGroup(
    val title: String,
    val taskCount: Int,
    val progress: Float,
    val icon: ImageVector,
    val accentColor: Color,
)

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
)
