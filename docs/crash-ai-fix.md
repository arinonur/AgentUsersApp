# Crashlytics AI Fix Pipeline

This repository includes a conservative GitHub Actions workflow that:

- reads recent Android Crashlytics export data from BigQuery
- builds a compact crash packet at `.tmp/crash_packet.json`
- runs Codex against the repository with a minimal-fix prompt
- verifies the result with `./gradlew :app:compileDebugKotlin`
- opens a draft pull request only if code changed and verification passed

## Manual Run

Run the workflow from GitHub Actions:

1. Open the `Crashlytics AI Fix` workflow.
2. Click `Run workflow`.

The workflow also runs every 30 minutes on a schedule.

## Required Secrets

The workflow expects these repository secrets:

- `GCP_PROJECT_ID`
- `GCP_SA_KEY`
- `OPENAI_API_KEY`

## No-Crash Behavior

If no recent high-signal Android Crashlytics incident is found in BigQuery, the script exits successfully and the workflow stops without opening a pull request.

## PR Safety

- pull requests are created as drafts only
- no auto-merge is configured
- if Codex makes no safe change, or if verification fails, no pull request is opened
- the workflow does not modify Firebase config, release signing, or unrelated CI infrastructure
