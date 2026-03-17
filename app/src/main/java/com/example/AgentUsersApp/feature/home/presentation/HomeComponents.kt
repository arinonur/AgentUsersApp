package com.example.AgentUsersApp.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HomeHeader(
    user: HomeUser,
    onNotificationClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9DDFF)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = user.initials,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C5CFA),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Hello! ${user.fullName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2430),
                )
                Text(
                    text = "Let's manage your tasks today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A7F8A),
                )
            }
        }

        Surface(
            shape = CircleShape,
            color = Color.White,
            tonalElevation = 2.dp,
        ) {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = Color(0xFF2D3142),
                )
            }
        }
    }
}

@Composable
fun TaskSummaryCard(
    summary: TaskSummary,
    onViewTasksClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF7C5CFA),
                            Color(0xFFA58BFF),
                        )
                    )
                )
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = summary.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = "Keep it up. You're close to finishing all your planned work.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.84f),
                )
                Button(
                    onClick = onViewTasksClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF7C5CFA),
                    ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = "View Task",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            ProgressRing(
                progress = summary.completion,
                label = "${(summary.completion * 100).toInt()}%",
                size = 90.dp,
                stroke = 8.dp,
                progressColor = Color.White,
                trackColor = Color.White.copy(alpha = 0.20f),
                textColor = Color.White,
            )
        }
    }
}

@Composable
fun InProgressTaskCard(
    task: InProgressTask,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .size(width = 220.dp, height = 150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = task.containerColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF6C7180),
                )
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2430),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LinearTaskProgress(
                    progress = task.progress,
                    progressColor = Color(0xFF7C5CFA),
                    trackColor = Color.White.copy(alpha = 0.72f),
                )
                Text(
                    text = "${(task.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF6C7180),
                )
            }
        }
    }
}

@Composable
fun TaskGroupRow(group: TaskGroup) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(group.accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = group.icon,
                    contentDescription = group.title,
                    tint = group.accentColor,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2430),
                )
                Text(
                    text = "${group.taskCount} tasks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A7F8A),
                )
            }

            ProgressRing(
                progress = group.progress,
                label = "${(group.progress * 100).toInt()}%",
                size = 54.dp,
                stroke = 6.dp,
                progressColor = group.accentColor,
                trackColor = group.accentColor.copy(alpha = 0.16f),
                textColor = Color(0xFF1F2430),
            )
        }
    }
}

@Composable
fun HomeBottomBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(item.label)
                },
            )
        }
    }
}

@Composable
fun ProgressRing(
    progress: Float,
    label: String,
    size: Dp,
    stroke: Dp,
    progressColor: Color,
    trackColor: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = progressColor,
            trackColor = trackColor,
            strokeWidth = stroke,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}

@Composable
fun LinearTaskProgress(
    progress: Float,
    progressColor: Color,
    trackColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(trackColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(progressColor),
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2430),
    )
}
