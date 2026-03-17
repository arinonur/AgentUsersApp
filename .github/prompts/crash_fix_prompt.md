Read `.tmp/crash_packet.json` first, then inspect only the relevant Android/Kotlin code needed to address that crash.

Your task:
- make the smallest safe fix that plausibly addresses the crash described in the packet
- preserve the existing app architecture, navigation, MVVM structure, and design patterns
- avoid unrelated rewrites, broad refactors, dependency churn, or formatting-only changes
- do not modify secrets, workflow authentication, release signing, CI infrastructure outside this workflow, or Firebase configuration
- do not change `google-services.json`
- do not add risky behavior or speculative fixes if the root cause is unclear

Process requirements:
- use a plan-first approach before editing
- inspect the crash packet and the narrowest relevant code paths
- if the crash cannot be fixed safely from the available evidence, make no code changes
- after changes, run verification with at least `./gradlew :app:compileDebugKotlin`
- if verification fails, fix the issue if the fix is still safe and minimal; otherwise leave no changes

Output expectations:
- keep the diff compact and production-friendly
- prefer one focused fix over multiple speculative fixes
- leave the repository unchanged if there is no recent crash packet, no safe fix, or no passing verification

Target stack:
- Android app
- Kotlin
- Jetpack Compose
- Gradle Kotlin DSL

