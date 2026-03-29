# Crashlytics Claude MCP Fix Pipeline

This repository includes a GitHub Actions workflow that uses **Claude Code** (via `anthropics/claude-code-base-action`) and **Firebase MCP** to automatically detect, analyze, and fix Crashlytics crashes.

## How It Works

1. Authenticates to GCP and configures Firebase MCP
2. Runs Claude Code with Firebase MCP tools to find the newest actionable Crashlytics issue
3. Claude inspects the crash stack trace and reads the relevant Kotlin source code
4. Applies the smallest safe fix if the evidence supports it
5. Verifies the fix compiles with `./gradlew clean :app:compileDebugKotlin --no-build-cache`
6. Opens a draft pull request only if code changed and verification passed

## Required Secrets

| Secret | Purpose |
|--------|---------|
| `ANTHROPIC_API_KEY` | Claude Code API authentication |
| `GCP_PROJECT_ID` | Firebase/GCP project identifier |
| `GCP_SA_KEY` | GCP service account JSON credentials |
| `FIREBASE_TOKEN` | Firebase CLI auth token |
| `GOOGLE_SERVICES_JSON` | App's google-services.json for compilation |
| `GH_PAT` | GitHub PAT for creating pull requests |

## Triggers

- **Scheduled**: Runs every 6 hours (`0 */6 * * *`)
- **Manual**: Can be triggered via `workflow_dispatch` from GitHub Actions UI

## Manual Run

1. Open the **Crashlytics Claude MCP Fix** workflow in GitHub Actions.
2. Click **Run workflow**.

## PR Safety

- Pull requests are created as **drafts only**
- No auto-merge is configured
- If no actionable Crashlytics issue is found, the workflow exits without opening a PR
- If Claude cannot produce a safe fix, no PR is opened
- Claude's tools are restricted to file operations and Firebase MCP only (no git push)
- A 30-minute timeout prevents runaway execution
