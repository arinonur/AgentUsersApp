package com.example.AgentUsersApp.feature.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.AgentUsersApp.feature.home.presentation.BottomNavItem
import com.example.AgentUsersApp.feature.home.presentation.HomeBottomBar
import com.example.AgentUsersApp.feature.home.presentation.HomeFakeData
import com.example.AgentUsersApp.feature.home.presentation.InProgressTask
import com.example.AgentUsersApp.feature.home.presentation.TaskHomeViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.AgentUsersApp.ui.theme.AgentUsersAppTheme

private val CalendarScreenBackground = Color(0xFFFCFCFF)
private val CalendarSurface = Color(0xFFFFFFFF)
private val CalendarTextPrimary = Color(0xFF25243A)
private val CalendarTextSecondary = Color(0xFF8D8AA8)
private val CalendarPurple = Color(0xFF6F42F5)
private val CalendarPurpleSoft = Color(0xFFEDE7FF)

@Composable
fun CalendarTabScreenRoute(
    viewModel: TaskHomeViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onBottomTabSelected: (Int) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val errorMessage = uiState.errorMessage
    if (errorMessage != null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
        }
        return
    }

    CalendarTabScreen(
        modifier = modifier,
        tasks = uiState.inProgressTasks.mapIndexed(::mapTaskToCalendarCard),
        bottomNavigationItems = uiState.bottomNavigationItems.ifEmpty { HomeFakeData.bottomNavigationItems },
        onBackClick = onBackClick,
        onNotificationClick = onNotificationClick,
        onAddClick = onAddClick,
        onTaskClick = onTaskClick,
        onBottomTabSelected = onBottomTabSelected,
    )
}

@Composable
fun CalendarTabScreen(
    modifier: Modifier = Modifier,
    days: List<CalendarDayUiModel> = CalendarFakeData.days,
    filters: List<CalendarFilterUiModel> = CalendarFakeData.filters,
    tasks: List<CalendarTaskCardUiModel> = CalendarFakeData.tasks,
    bottomNavigationItems: List<BottomNavItem> = HomeFakeData.bottomNavigationItems,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onBottomTabSelected: (Int) -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CalendarScreenBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = CalendarPurple,
                contentColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add calendar task",
                )
            }
        },
        bottomBar = {
            HomeBottomBar(
                items = bottomNavigationItems,
                selectedIndex = 1,
                onSelected = onBottomTabSelected,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFEFF),
                            Color(0xFFF8F5FF),
                            Color(0xFFFFFEFF),
                        )
                    )
                )
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 18.dp, bottom = 108.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                CalendarTopBar(
                    onBackClick = onBackClick,
                    onNotificationClick = onNotificationClick,
                )
            }

            item {
                CalendarDayStrip(days = days)
            }

            item {
                CalendarFilterRow(filters = filters)
            }

            items(tasks, key = { it.id }) { task ->
                CalendarTaskCard(
                    task = task,
                    onClick = { onTaskClick(task.id) },
                )
            }
        }
    }
}

@Composable
private fun CalendarTopBar(
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = CalendarTextPrimary,
            )
        }

        Text(
            text = "Today's Tasks",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = CalendarTextPrimary,
            textAlign = TextAlign.Center,
        )

        Box {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = CalendarTextPrimary,
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 10.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CalendarPurple),
            )
        }
    }
}

@Composable
private fun CalendarDayStrip(
    days: List<CalendarDayUiModel>,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(days) { day ->
            CalendarDayCard(day = day)
        }
    }
}

@Composable
private fun CalendarDayCard(
    day: CalendarDayUiModel,
) {
    val isSelected = day.isSelected
    val cardColors = if (isSelected) {
        listOf(Color(0xFF7C5CFA), Color(0xFF5E35F2))
    } else {
        listOf(Color.White, Color.White)
    }

    Card(
        modifier = Modifier.size(width = 64.dp, height = 112.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(cardColors))
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = day.monthLabel,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White.copy(alpha = 0.88f) else CalendarTextSecondary,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = day.dayNumber,
                style = MaterialTheme.typography.headlineMedium,
                color = if (isSelected) Color.White else CalendarTextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = day.dayLabel,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.White else CalendarTextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun CalendarFilterRow(
    filters: List<CalendarFilterUiModel>,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = filter.isSelected,
                onClick = {},
                label = {
                    Text(
                        text = filter.label,
                        fontWeight = if (filter.isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CalendarPurple,
                    selectedLabelColor = Color.White,
                    containerColor = CalendarPurpleSoft,
                    labelColor = CalendarPurple,
                ),
                border = null,
            )
        }
    }
}

@Composable
private fun CalendarTaskCard(
    task: CalendarTaskCardUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CalendarSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CalendarTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(task.trailingIconBackground),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = task.trailingIcon,
                        contentDescription = task.title,
                        tint = task.trailingIconTint,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineSmall,
                color = CalendarTextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = task.accentColor,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = task.timeLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = task.accentColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = task.accentBackground,
                ) {
                    Text(
                        text = task.statusLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = task.accentColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFCFCFF)
@Composable
private fun CalendarTabScreenPreview() {
    AgentUsersAppTheme {
        CalendarTabScreen()
    }
}

private fun mapTaskToCalendarCard(
    index: Int,
    task: InProgressTask,
): CalendarTaskCardUiModel {
    val (statusLabel, accentColor, accentBackground) = when {
        task.completed -> Triple("Done", Color(0xFF9B87F5), Color(0xFFF3EEFF))
        task.progress > 0f -> Triple("In Progress", Color(0xFFFF9B6A), Color(0xFFFFEEE5))
        else -> Triple("To-do", Color(0xFF7E8BFF), Color(0xFFEFF1FF))
    }

    val (icon, iconTint, iconBackground) = when {
        task.category.contains("grocery", ignoreCase = true) -> Triple(
            Icons.Outlined.ShoppingBag,
            Color(0xFFF26CB4),
            Color(0xFFFDECF5),
        )
        task.category.contains("uber", ignoreCase = true) -> Triple(
            Icons.Outlined.AutoAwesomeMosaic,
            Color(0xFF8B78FF),
            Color(0xFFF1EFFF),
        )
        else -> Triple(
            Icons.AutoMirrored.Outlined.MenuBook,
            Color(0xFFFFA552),
            Color(0xFFFFF0E1),
        )
    }

    return CalendarTaskCardUiModel(
        id = task.id,
        category = task.category,
        title = task.title,
        timeLabel = CALENDAR_TIME_SLOTS[index % CALENDAR_TIME_SLOTS.size],
        statusLabel = statusLabel,
        accentColor = accentColor,
        accentBackground = accentBackground,
        trailingIcon = icon,
        trailingIconTint = iconTint,
        trailingIconBackground = iconBackground,
    )
}

private val CALENDAR_TIME_SLOTS = listOf(
    "09:00 AM",
    "10:00 AM",
    "12:00 PM",
    "03:00 PM",
    "07:00 PM",
)
