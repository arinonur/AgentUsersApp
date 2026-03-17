# Crashlytics MCP AI Fix Pipeline

This repository includes a conservative GitHub Actions workflow that:

- uses Firebase MCP instead of BigQuery
- polls for the newest actionable Android Crashlytics issue on a schedule
- lets Codex inspect that issue through Firebase MCP
- attempts the smallest safe fix in this repository
- verifies the result with `./gradlew :app:compileDebugKotlin`
- opens a draft pull request only if code changed and verification passed

## Required Secrets

The workflow expects these repository secrets:

- `GCP_PROJECT_ID`
- `GCP_SA_KEY`
- `OPENAI_API_KEY`

## Manual Run

Run the workflow from GitHub Actions:

1. Open the `Crashlytics MCP AI Fix` workflow.
2. Click `Run workflow`.

## Trigger Model

Scheduled runs poll every 30 minutes. This is not an instant event-driven trigger from Crashlytics.

## PR Safety

- pull requests are created as drafts only
- no auto-merge is configured
- if no actionable Crashlytics issue is found, the workflow exits successfully without opening a pull request
- if Codex cannot produce a safe fix, no pull request is opened
