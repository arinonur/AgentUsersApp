package com.example.AgentUsersApp.feature.home.presentation

import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashTestHelper {

    /**
     * Simulates a realistic crash: parsing task assignees from a
     * server response where the assignee list can be empty.
     */
    fun parseTaskAssignees(taskTitle: String): String {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log("Parsing assignees for task: $taskTitle")
        crashlytics.setCustomKey("task_title", taskTitle)

        // Simulates data coming from Firestore where assignees might be empty
        val assignees: List<String> = emptyList()

        // Fixed: safely access first element with null-safety guard
        val primaryAssignee = assignees.firstOrNull() ?: "Unassigned"
        return primaryAssignee
    }

    fun triggerComplexCrash(selectedTabIndex: Int, taskCount: Int) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log("Starting complex HomeScreen crash test")
        crashlytics.setCustomKey("crash_test_type", "complex_ui_flow")
        crashlytics.setCustomKey("screen_name", "HomeScreen")
        crashlytics.setCustomKey("user_state", "logged_in")
        crashlytics.setCustomKey("selected_tab", selectedTabIndex)
        crashlytics.setCustomKey("task_count", taskCount)

        fun buildFakeSession(tabIndex: Int): FakeSession {
            crashlytics.log("Building fake dashboard session")
            return FakeSession(
                sessionId = "home-debug-session-$tabIndex",
                selectedTab = tabIndex,
            )
        }

        fun buildFakeTaskPayload(session: FakeSession, count: Int): FakeTaskPayload {
            crashlytics.log("Building fake task payload")
            val ownerId = if (count > 0) null else "owner-${session.selectedTab}"
            return FakeTaskPayload(
                taskIds = List(count) { index -> "task-${session.selectedTab}-$index" },
                primaryOwnerId = ownerId,
            )
        }

        fun resolvePrimaryTaskOwner(session: FakeSession, payload: FakeTaskPayload): String {
            crashlytics.log("Resolving primary task owner")
            return requireNotNull(payload.primaryOwnerId) {
                "Missing primary owner while parsing dashboard state for ${session.sessionId}"
            }
        }

        val session = buildFakeSession(selectedTabIndex)
        val payload = buildFakeTaskPayload(session, taskCount)
        resolvePrimaryTaskOwner(session, payload)
        throw IllegalStateException("Crash test should have failed during HomeScreen state processing")
    }
}

private data class FakeSession(
    val sessionId: String,
    val selectedTab: Int,
)

private data class FakeTaskPayload(
    val taskIds: List<String>,
    val primaryOwnerId: String?,
)
