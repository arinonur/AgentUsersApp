package com.example.AgentUsersApp.feature.home.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.AgentUsersApp.feature.home.domain.CreateTaskInput
import com.example.AgentUsersApp.feature.home.domain.TaskRepository
import com.example.AgentUsersApp.feature.home.presentation.HomeFakeData
import com.example.AgentUsersApp.feature.home.presentation.HomeUser
import com.example.AgentUsersApp.feature.home.presentation.InProgressTask
import com.example.AgentUsersApp.feature.home.presentation.TaskGroup
import com.example.AgentUsersApp.feature.home.presentation.TaskSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale

class FirestoreTaskRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : TaskRepository {

    override suspend fun loadHome(): TaskHomeData {
        val user = requireAuthenticatedUser()
        return buildHomeData(
            user = user,
            documents = taskCollection(user.uid)
                .get()
                .await()
                .documents,
        )
    }

    override fun observeHome(): Flow<TaskHomeData> = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            close(IllegalStateException("User is not authenticated."))
            return@callbackFlow
        }

        val registration = taskCollection(user.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val currentSnapshot = snapshot
                if (currentSnapshot == null) {
                    close(FirebaseFirestoreException("Task snapshot is unavailable.", FirebaseFirestoreException.Code.UNKNOWN))
                    return@addSnapshotListener
                }

                trySend(
                    buildHomeData(
                        user = user,
                        documents = currentSnapshot.documents,
                    )
                )
            }

        awaitClose { registration.remove() }
    }

    override suspend fun getTask(taskId: String): InProgressTask? {
        val user = auth.currentUser ?: return null
        val document = taskCollection(user.uid)
            .document(taskId)
            .get()
            .await()

        return documentToTask(0, document)
    }

    override suspend fun createTask(task: CreateTaskInput): String {
        val user = requireAuthenticatedUser()
        val tasks = taskCollection(user.uid)
        val document = if (task.id.isBlank()) {
            tasks.document()
        } else {
            tasks.document(task.id)
        }

        val safeTitle = task.title.trim()
        require(safeTitle.isNotEmpty()) { "Task title is required." }
        val isCompleted = task.completed
        val safeProgress = if (isCompleted) 1f else task.progress.coerceIn(0f, 1f)

        document.set(
            mapOf(
                "category" to task.category.trim().ifBlank { "General" },
                "title" to safeTitle,
                "description" to task.description.trim(),
                "progress" to safeProgress,
                "completed" to isCompleted,
            )
        ).await()

        return document.id
    }

    override suspend fun updateTask(task: CreateTaskInput) {
        val user = requireAuthenticatedUser()
        require(task.id.isNotBlank()) { "Task id is required." }

        val safeTitle = task.title.trim()
        require(safeTitle.isNotEmpty()) { "Task title is required." }
        val isCompleted = task.completed
        val safeProgress = if (isCompleted) 1f else task.progress.coerceIn(0f, 1f)

        taskCollection(user.uid)
            .document(task.id)
            .update(
                mapOf(
                    "category" to task.category.trim().ifBlank { "General" },
                    "title" to safeTitle,
                    "description" to task.description.trim(),
                    "progress" to safeProgress,
                    "completed" to isCompleted,
                )
            )
            .await()
    }

    override suspend fun setTaskCompleted(taskId: String, completed: Boolean) {
        val user = requireAuthenticatedUser()
        require(taskId.isNotBlank()) { "Task id is required." }

        taskCollection(user.uid)
            .document(taskId)
            .update(
                mapOf(
                    "completed" to completed,
                    "progress" to if (completed) 1f else 0f,
                )
            )
            .await()
    }

    override suspend fun deleteTask(taskId: String) {
        val user = requireAuthenticatedUser()
        require(taskId.isNotBlank()) { "Task id is required." }

        taskCollection(user.uid)
            .document(taskId)
            .delete()
            .await()
    }

    private fun requireAuthenticatedUser(): FirebaseUser {
        return requireNotNull(auth.currentUser) { "User is not authenticated." }
    }

    private fun buildHomeData(
        user: FirebaseUser,
        documents: List<DocumentSnapshot>,
    ): TaskHomeData {
        val tasks = documents.mapIndexedNotNull(::documentToTask)
        return TaskHomeData(
            user = user.toHomeUser(),
            summary = buildSummary(tasks),
            inProgressTasks = tasks,
            taskGroups = buildTaskGroups(tasks),
            bottomNavigationItems = HomeFakeData.bottomNavigationItems,
        )
    }

    private fun taskCollection(uid: String) = firestore
        .collection(USERS_COLLECTION)
        .document(uid)
        .collection(TASKS_COLLECTION)

    private fun documentToTask(index: Int, document: DocumentSnapshot): InProgressTask? {
        if (!document.exists()) return null

        val title = document.getString("title").orEmpty().trim()
        if (title.isEmpty()) return null

        val category = document.getString("category").orEmpty().trim().ifBlank { "General" }
        val description = document.getString("description").orEmpty().trim()
        val completed = document.getBoolean("completed") ?: false
        val progress = document.getDouble("progress")
            ?.toFloat()
            ?.coerceIn(0f, 1f)
            ?.let { if (completed) 1f else it }
            ?: if (completed) 1f else 0f

        return InProgressTask(
            id = document.id,
            category = category,
            title = title,
            progress = progress,
            description = description.ifBlank { "No description provided yet." },
            containerColor = taskColorFor(index),
            completed = completed,
        )
    }

    private fun buildSummary(tasks: List<InProgressTask>): TaskSummary {
        if (tasks.isEmpty()) {
            return TaskSummary(
                completion = 0f,
                title = "No tasks yet. Create your first task to get started.",
            )
        }

        val completion = tasks.map { it.progress }.average().toFloat().coerceIn(0f, 1f)
        return TaskSummary(
            completion = completion,
            title = "Your today's task almost done!",
        )
    }

    private fun buildTaskGroups(tasks: List<InProgressTask>): List<TaskGroup> {
        return tasks
            .groupBy { it.category }
            .map { (category, groupedTasks) ->
                val progress = groupedTasks
                    .map { it.progress }
                    .average()
                    .toFloat()
                    .coerceIn(0f, 1f)

                TaskGroup(
                    title = category,
                    taskCount = groupedTasks.size,
                    progress = progress,
                    icon = iconForCategory(category),
                    accentColor = accentColorForCategory(category),
                )
            }
            .sortedBy { it.title.lowercase(Locale.US) }
    }

    private fun FirebaseUser.toHomeUser(): HomeUser {
        val rawName = displayName.orEmpty().trim()
        val fullName = when {
            rawName.isNotEmpty() -> rawName
            !email.isNullOrBlank() -> email!!.substringBefore('@')
            else -> "User"
        }

        return HomeUser(
            fullName = fullName,
            initials = initialsFor(fullName),
        )
    }

    private fun initialsFor(fullName: String): String {
        val parts = fullName
            .split(" ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return parts
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifBlank { "U" }
    }

    private fun taskColorFor(index: Int): Color {
        return TASK_CARD_COLORS[index % TASK_CARD_COLORS.size]
    }

    private fun iconForCategory(category: String): ImageVector {
        return when (category.lowercase(Locale.US)) {
            "office project" -> Icons.Outlined.WorkOutline
            "personal project" -> Icons.Outlined.PersonOutline
            "daily study" -> Icons.Outlined.BookmarkBorder
            else -> Icons.Outlined.FolderOpen
        }
    }

    private fun accentColorForCategory(category: String): Color {
        return when (category.lowercase(Locale.US)) {
            "office project" -> Color(0xFF7C5CFA)
            "personal project" -> Color(0xFF4DA8DA)
            "daily study" -> Color(0xFFFFA94D)
            else -> Color(0xFF38B98E)
        }
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val TASKS_COLLECTION = "tasks"

        val TASK_CARD_COLORS = listOf(
            Color(0xFFF2EFFF),
            Color(0xFFE8F7FF),
            Color(0xFFFFF1E7),
            Color(0xFFEAF8F1),
        )
    }
}

data class TaskHomeData(
    val user: HomeUser,
    val summary: TaskSummary,
    val inProgressTasks: List<InProgressTask>,
    val taskGroups: List<TaskGroup>,
    val bottomNavigationItems: List<com.example.AgentUsersApp.feature.home.presentation.BottomNavItem>,
)
