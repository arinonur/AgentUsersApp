package com.example.AgentUsersApp.feature.calendar.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CalendarDayUiModel(
    val monthLabel: String,
    val dayNumber: String,
    val dayLabel: String,
    val isSelected: Boolean = false,
)

data class CalendarFilterUiModel(
    val label: String,
    val isSelected: Boolean = false,
)

data class CalendarTaskCardUiModel(
    val id: String,
    val category: String,
    val title: String,
    val timeLabel: String,
    val statusLabel: String,
    val accentColor: Color,
    val accentBackground: Color,
    val trailingIcon: ImageVector,
    val trailingIconTint: Color,
    val trailingIconBackground: Color,
)
