Use Firebase MCP Crashlytics tools for this task.

Use the Firebase project identified by the `FIREBASE_PROJECT` environment variable for all Firebase MCP queries and issue inspection.

STRICT EXECUTION:

1. Always fetch the latest Android Crashlytics issue.
2. Always fetch at least one representative event and stack trace for that issue.
3. If any Android crash exists:
   - you must attempt a fix
   - even if partial, produce a minimal defensive fix
4. If the root cause is unclear:
   - add null checks, safeguards, default values, or try/catch where appropriate
   - prevent the crash rather than waiting for a perfect fix

RULES:

- never exit without making a code change if crash data exists
- prefer the smallest safe fix
- avoid unrelated rewrites, refactors, dependency churn, or formatting-only changes
- inspect only the relevant Android/Kotlin code paths for the fetched crash
- preserve the existing MVVM, Jetpack Compose, Firebase auth/task architecture, and navigation structure
- do not modify secrets, workflow auth, release signing, `google-services.json`, or Firebase configuration
- leave verification to the workflow compile step

SKIP CONDITION:

- only skip making code changes if absolutely no Android Crashlytics issue or event data exists

PROCESS:

- use a plan-first approach before editing
- fetch the latest Android Crashlytics issue through Firebase MCP
- fetch at least one event and stack trace through Firebase MCP
- identify the narrowest relevant code path
- apply the smallest safe defensive fix that reduces or prevents the crash

OUTPUT EXPECTATION:

- always produce a diff if crash data exists so a draft PR can be created
- keep the diff compact, production-friendly, and easy to review
