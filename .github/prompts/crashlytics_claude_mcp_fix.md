Use Firebase MCP Crashlytics tools for this task. Do not use BigQuery.

Use the Firebase project identified by the `FIREBASE_PROJECT` environment variable for all Firebase MCP queries and issue inspection.

Goal:
- Find the newest actionable Android Crashlytics issue
- Inspect the issue details and relevant evidence
- Make the smallest safe Android/Kotlin fix if a safe app-code fix is possible

Process:
1. Use Firebase MCP tools to list recent Crashlytics issues for the Android app.
2. Select the newest actionable issue with a clear stack trace.
3. Inspect the issue details, stack trace, and the most relevant event details available through Firebase MCP.
4. Read only the narrowest relevant Kotlin/Android source files.
5. Apply the smallest safe fix only if the evidence supports it.
6. If no safe app-code fix is possible from the available evidence, make no changes.

Fix guidelines:
- Prefer null-safety guards, bounds checks, lifecycle safety, and defensive parsing
- Preserve existing architecture, navigation, MVVM structure, and design patterns
- Avoid speculative refactors, dependency changes, formatting-only edits, or unrelated cleanup
- Do not modify secrets, workflow files, release signing, google-services.json, or Firebase configuration
- Do not run git commit or git push — the CI workflow handles version control

Verification:
- After making changes, run: ./gradlew :app:compileDebugKotlin
- If compilation fails, either fix the issue minimally or revert all changes
- Leave the repository unchanged if there is no safe fix or no passing compilation

Target stack:
- Android app
- Kotlin
- Jetpack Compose
- Gradle Kotlin DSL

Output expectations:
- Start your final message with a single line in this exact format:
  `PR title: <specific draft PR title>`
- Summarize the selected Crashlytics issue
- Summarize the changed files, if any
- Summarize the safety rationale for the fix or for making no change
- Keep the diff compact and production-safe
- If no changes were made, explain why
