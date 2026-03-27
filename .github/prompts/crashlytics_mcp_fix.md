Use Firebase MCP Crashlytics tools for this task. Do not use BigQuery.

Use the Firebase project identified by the `FIREBASE_PROJECT` environment variable for all Firebase MCP queries and issue inspection.

Goal:
- find the newest actionable Android Crashlytics issue
- inspect the issue details and relevant evidence
- make the smallest safe Android/Kotlin fix if a safe app-code fix is possible

Rules:
- use Firebase MCP Crashlytics tools, not BigQuery
- inspect only the narrowest relevant Android/Kotlin code paths
- prefer null-safety, guard clauses, bounds checks, lifecycle safety, and defensive parsing
- avoid speculative refactors, dependency churn, formatting-only changes, or unrelated cleanup
- do not modify secrets, workflow auth, release signing, `google-services.json`, or Firebase configuration
- make no changes if no safe app-code fix is possible from the available evidence

Process:
1. Find the newest actionable Android Crashlytics issue.
2. Inspect the issue, including stack trace and the most relevant event details available through Firebase MCP.
3. Identify the narrowest relevant Kotlin/Android code path.
4. Apply the smallest safe fix only if the evidence supports it.
5. Leave the repository unchanged if there is no safe fix.

Output expectations:
- summarize the selected Crashlytics issue
- summarize the changed files, if any
- summarize the safety rationale for the fix or for making no change
- keep the diff compact and production-safe
