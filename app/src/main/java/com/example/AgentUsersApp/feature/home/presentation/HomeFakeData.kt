package com.example.AgentUsersApp.feature.home.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.Color

object HomeFakeData {
    val currentUser = HomeUser(
        fullName = "Livia Vaccaro",
        initials = "LV",
    )

    val summary = TaskSummary(
        completion = 0.85f,
        title = "Your today's task almost done!",
    )

    val inProgressTasks = listOf(
        InProgressTask(
            id = "grocery-design",
            category = "Office Project",
            title = "Grocery shopping app design",
            progress = 0.72f,
            description = "Finalize the shopping flow, clean up card layouts, and prepare the next review presentation for the product team.",
            containerColor = Color(0xFFF2EFFF),
            completed = false,
        ),
        InProgressTask(
            id = "uber-redesign",
            category = "Personal Project",
            title = "Uber Eats redesign challenge",
            progress = 0.48f,
            description = "Refine the onboarding concept, update the order-tracking states, and improve visual hierarchy for delivery status.",
            containerColor = Color(0xFFE8F7FF),
            completed = false,
        ),
        InProgressTask(
            id = "blog-wireframe",
            category = "Daily Task",
            title = "Blog post wireframe",
            progress = 0.91f,
            description = "Wrap up the final wireframe pass, confirm content placement, and prepare a quick handoff note for implementation.",
            containerColor = Color(0xFFFFF1E7),
            completed = false,
        ),
    )

    val taskGroups = listOf(
        TaskGroup(
            title = "Office Project",
            taskCount = 23,
            progress = 0.72f,
            icon = Icons.Outlined.WorkOutline,
            accentColor = Color(0xFF7C5CFA),
        ),
        TaskGroup(
            title = "Personal Project",
            taskCount = 30,
            progress = 0.52f,
            icon = Icons.Outlined.PersonOutline,
            accentColor = Color(0xFF4DA8DA),
        ),
        TaskGroup(
            title = "Daily Study",
            taskCount = 12,
            progress = 0.87f,
            icon = Icons.Outlined.BookmarkBorder,
            accentColor = Color(0xFFFFA94D),
        ),
        TaskGroup(
            title = "Meeting Notes",
            taskCount = 9,
            progress = 0.33f,
            icon = Icons.Outlined.FolderOpen,
            accentColor = Color(0xFF38B98E),
        ),
    )

    val bottomNavigationItems = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Outlined.Home,
        ),
        BottomNavItem(
            label = "Calendar",
            icon = Icons.Outlined.CalendarMonth,
        ),
        BottomNavItem(
            label = "Messages",
            icon = Icons.Outlined.ChatBubbleOutline,
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Outlined.PersonOutline,
        ),
    )
}
