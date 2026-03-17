Use Firebase MCP Crashlytics tools for this task.

Use the Firebase project identified by the `FIREBASE_PROJECT` environment variable for all Firebase MCP queries and issue inspection.

Process:
- first identify the newest actionable Android Crashlytics issue through Firebase MCP
- fetch representative event data and stack trace data for that issue through Firebase MCP
- inspect only the relevant Android/Kotlin code paths in this repository
- use a plan-first approach before editing

Requirements:
- apply the smallest safe fix that plausibly addresses the crash evidence
- preserve the existing MVVM, Jetpack Compose, Firebase auth/task architecture, and navigation structure
- avoid unrelated rewrites, broad refactors, dependency churn, or formatting-only changes
- do not modify secrets, workflow auth, release signing, `google-services.json`, or Firebase configuration
- if the evidence is insufficient or no actionable Android issue exists, make no code changes
- leave verification to the workflow compile step; do not run extra destructive commands

Safety bar:
- prefer one focused fix over multiple speculative fixes
- keep the diff compact and production-friendly
- if you cannot produce a safe fix from the MCP evidence, leave the repository unchanged
