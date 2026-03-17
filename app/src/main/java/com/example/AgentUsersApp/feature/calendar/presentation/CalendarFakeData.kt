package com.example.AgentUsersApp.feature.calendar.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.Color

object CalendarFakeData {
    val days = listOf(
        CalendarDayUiModel(
            monthLabel = "May",
            dayNumber = "23",
            dayLabel = "Fri",
        ),
        CalendarDayUiModel(
            monthLabel = "May",
            dayNumber = "24",
            dayLabel = "Sat",
        ),
        CalendarDayUiModel(
            monthLabel = "May",
            dayNumber = "25",
            dayLabel = "Sun",
            isSelected = true,
        ),
        CalendarDayUiModel(
            monthLabel = "May",
            dayNumber = "26",
            dayLabel = "Mon",
        ),
        CalendarDayUiModel(
            monthLabel = "May",
            dayNumber = "27",
            dayLabel = "Tue",
        ),
    )

    val filters = listOf(
        CalendarFilterUiModel(
            label = "All",
            isSelected = true,
        ),
        CalendarFilterUiModel(label = "To do"),
        CalendarFilterUiModel(label = "In Progress"),
        CalendarFilterUiModel(label = "Completed"),
    )

    val tasks = listOf(
        CalendarTaskCardUiModel(
            id = "market-research",
            category = "Grocery shopping app design",
            title = "Market Research",
            timeLabel = "10:00 AM",
            statusLabel = "Done",
            accentColor = Color(0xFF9B87F5),
            accentBackground = Color(0xFFF3EEFF),
            trailingIcon = Icons.Outlined.ShoppingBag,
            trailingIconTint = Color(0xFFF26CB4),
            trailingIconBackground = Color(0xFFFDECF5),
        ),
        CalendarTaskCardUiModel(
            id = "competitive-analysis",
            category = "Grocery shopping app design",
            title = "Competitive Analysis",
            timeLabel = "12:00 PM",
            statusLabel = "In Progress",
            accentColor = Color(0xFF9B87F5),
            accentBackground = Color(0xFFF3EEFF),
            trailingIcon = Icons.Outlined.ShoppingBag,
            trailingIconTint = Color(0xFFF26CB4),
            trailingIconBackground = Color(0xFFFDECF5),
        ),
        CalendarTaskCardUiModel(
            id = "wireframe",
            category = "Uber Eats redesign challenge",
            title = "Create Low-fidelity Wireframe",
            timeLabel = "07:00 PM",
            statusLabel = "To-do",
            accentColor = Color(0xFF8B78FF),
            accentBackground = Color(0xFFF1EFFF),
            trailingIcon = Icons.Outlined.AutoAwesomeMosaic,
            trailingIconTint = Color(0xFF8B78FF),
            trailingIconBackground = Color(0xFFF1EFFF),
        ),
        CalendarTaskCardUiModel(
            id = "design-sprint",
            category = "About design sprint",
            title = "How to pitch a Design Sprint",
            timeLabel = "09:00 PM",
            statusLabel = "To-do",
            accentColor = Color(0xFF9B87F5),
            accentBackground = Color(0xFFF3EEFF),
            trailingIcon = Icons.AutoMirrored.Outlined.MenuBook,
            trailingIconTint = Color(0xFFFFA552),
            trailingIconBackground = Color(0xFFFFF0E1),
        ),
    )
}
