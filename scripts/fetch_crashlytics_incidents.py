from __future__ import annotations

import json
import os
from dataclasses import dataclass
from datetime import datetime, timedelta, timezone
from pathlib import Path
from typing import Any

from google.api_core.exceptions import GoogleAPIError, NotFound
from google.cloud import bigquery


OUTPUT_PATH = Path(".tmp/crash_packet.json")
LOOKBACK_DAYS = 7
ROW_SCAN_LIMIT = 20
TABLE_PRIORITY_HINTS = ("realtime", "android")


@dataclass
class CandidateTable:
    dataset_id: str
    table_id: str
    full_name: str
    timestamp_column: str | None
    priority: tuple[int, int, str]


def main() -> int:
    project_id = os.environ.get("GCP_PROJECT_ID", "").strip()
    if not project_id:
        print("GCP_PROJECT_ID is not set. Skipping crash packet generation.")
        return 0

    client = bigquery.Client(project=project_id)
    candidate_tables = discover_candidate_tables(client, project_id)
    if not candidate_tables:
        print("No Crashlytics Android export tables were discovered.")
        return 0

    packet = None
    for table in candidate_tables:
        packet = fetch_packet_from_table(client, table)
        if packet:
            break

    if not packet:
        print("No recent high-signal Android crash was found.")
        return 0

    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(json.dumps(packet, indent=2), encoding="utf-8")
    print(f"Wrote crash packet to {OUTPUT_PATH}")
    return 0


def discover_candidate_tables(
    client: bigquery.Client,
    project_id: str,
) -> list[CandidateTable]:
    candidates: list[CandidateTable] = []

    try:
        datasets = list(client.list_datasets(project=project_id))
    except GoogleAPIError as exc:
        print(f"Unable to list BigQuery datasets: {exc}")
        return candidates

    for dataset in datasets:
        dataset_id = dataset.dataset_id
        if "crash" not in dataset_id.lower():
            continue

        try:
            tables = list(client.list_tables(dataset.reference))
        except GoogleAPIError as exc:
            print(f"Unable to list tables for dataset {dataset_id}: {exc}")
            continue

        for table in tables:
            table_id = table.table_id
            if "android" not in table_id.lower():
                continue

            try:
                table_obj = client.get_table(table.reference)
            except (GoogleAPIError, NotFound) as exc:
                print(f"Unable to inspect table {project_id}.{dataset_id}.{table_id}: {exc}")
                continue

            timestamp_column = choose_timestamp_column(table_obj.schema)
            if timestamp_column is None and table_obj.num_rows == 0:
                continue

            lower_name = table_id.lower()
            priority = (
                0 if TABLE_PRIORITY_HINTS[0] in lower_name else 1,
                0 if TABLE_PRIORITY_HINTS[1] in lower_name else 1,
                table_id,
            )
            candidates.append(
                CandidateTable(
                    dataset_id=dataset_id,
                    table_id=table_id,
                    full_name=f"{project_id}.{dataset_id}.{table_id}",
                    timestamp_column=timestamp_column,
                    priority=priority,
                )
            )

    return sorted(candidates, key=lambda item: item.priority)


def choose_timestamp_column(schema: list[bigquery.SchemaField]) -> str | None:
    preferred_names = (
        "event_timestamp",
        "event_time",
        "seen_timestamp",
        "timestamp",
        "ingestion_time",
    )
    by_name = {field.name.lower(): field.name for field in schema}
    for preferred in preferred_names:
        if preferred in by_name:
            return by_name[preferred]
    return None


def fetch_packet_from_table(
    client: bigquery.Client,
    table: CandidateTable,
) -> dict[str, Any] | None:
    where_clause = ""
    if table.timestamp_column:
        threshold = datetime.now(timezone.utc) - timedelta(days=LOOKBACK_DAYS)
        where_clause = (
            f"WHERE SAFE_CAST({quote_identifier(table.timestamp_column)} AS TIMESTAMP) "
            f">= TIMESTAMP('{threshold.isoformat()}')"
        )

    query = f"""
        SELECT
          TO_JSON_STRING(t) AS payload,
          {build_timestamp_select(table.timestamp_column)}
        FROM `{table.full_name}` AS t
        {where_clause}
        ORDER BY seen_at DESC NULLS LAST
        LIMIT {ROW_SCAN_LIMIT}
    """

    try:
        rows = client.query(query).result()
    except GoogleAPIError as exc:
        print(f"Unable to query {table.full_name}: {exc}")
        return None

    best_packet = None
    best_score = -1
    for row in rows:
        payload = row.get("payload")
        if not payload:
            continue

        try:
            event = json.loads(payload)
        except json.JSONDecodeError:
            continue

        packet = build_crash_packet(event, row.get("seen_at"), table.full_name)
        if not packet:
            continue

        score = score_packet(packet)
        if score > best_score:
            best_packet = packet
            best_score = score

    return best_packet


def build_timestamp_select(timestamp_column: str | None) -> str:
    if not timestamp_column:
        return "CURRENT_TIMESTAMP() AS seen_at"
    quoted = quote_identifier(timestamp_column)
    return f"SAFE_CAST({quoted} AS TIMESTAMP) AS seen_at"


def quote_identifier(identifier: str) -> str:
    return f"`{identifier}`"


def build_crash_packet(
    event: dict[str, Any],
    seen_at: datetime | None,
    source_table: str,
) -> dict[str, Any] | None:
    exception_type = first_value(
        event,
        "exception_type",
        "exceptionType",
        "type",
        "class",
        "errorType",
        "signal_name",
    )
    stack_trace = extract_stack_trace(event)
    if not exception_type and not stack_trace:
        return None

    issue_id = first_value(
        event,
        "issue_id",
        "issueId",
        "variant_id",
        "variantId",
        "event_id",
        "eventId",
    )
    app_version = first_value(
        event,
        "app_version",
        "appVersion",
        "version_name",
        "versionName",
        "display_version",
        "displayVersion",
    )
    build_version = first_value(
        event,
        "build_version",
        "buildVersion",
        "version_code",
        "versionCode",
        "build_id",
        "buildId",
    )
    message = first_value(
        event,
        "message",
        "reason",
        "exception_message",
        "exceptionMessage",
    )

    title_parts = [part for part in (exception_type, message) if part]
    title = ": ".join(title_parts[:2]) if title_parts else "Android Crashlytics incident"

    packet = {
        "title": title,
        "issue_identifier": issue_id,
        "exception_type": exception_type,
        "stack_trace": stack_trace,
        "app_version": app_version,
        "build": build_version,
        "platform": "android",
        "seen_timestamp": seen_at.isoformat() if seen_at else None,
        "source_table": source_table,
    }
    return {key: value for key, value in packet.items() if value not in (None, "")}


def score_packet(packet: dict[str, Any]) -> int:
    score = 0
    if packet.get("exception_type"):
        score += 3
    if packet.get("stack_trace"):
        score += 3
    if packet.get("issue_identifier"):
        score += 1
    if packet.get("seen_timestamp"):
        score += 1
    return score


def extract_stack_trace(event: Any) -> str | None:
    explicit = first_value(
        event,
        "stack_trace",
        "stackTrace",
        "stacktrace",
        "trace",
    )
    if explicit:
        return compact_text(explicit)

    frames = find_first_list(event, "frames")
    if frames:
        rendered = []
        for frame in frames[:12]:
            if not isinstance(frame, dict):
                continue
            symbol = first_value(frame, "symbol", "file", "method", "function", "class")
            line = first_value(frame, "line", "line_number", "lineNumber")
            if symbol and line:
                rendered.append(f"{symbol}:{line}")
            elif symbol:
                rendered.append(symbol)
        if rendered:
            return "\n".join(rendered)

    nested_exception = find_first_mapping(event, "exception")
    if nested_exception and nested_exception is not event:
        return extract_stack_trace(nested_exception)

    return None


def first_value(data: Any, *keys: str) -> str | None:
    key_set = {key.lower() for key in keys}
    for key, value in walk_items(data):
        if key.lower() not in key_set:
            continue
        text = compact_text(value)
        if text:
            return text
    return None


def find_first_list(data: Any, key_name: str) -> list[Any] | None:
    for key, value in walk_items(data):
        if key.lower() == key_name.lower() and isinstance(value, list):
            return value
    return None


def find_first_mapping(data: Any, key_name: str) -> dict[str, Any] | None:
    for key, value in walk_items(data):
        if key.lower() == key_name.lower() and isinstance(value, dict):
            return value
    return None


def walk_items(data: Any):
    if isinstance(data, dict):
        for key, value in data.items():
            yield key, value
            yield from walk_items(value)
    elif isinstance(data, list):
        for item in data:
            yield from walk_items(item)


def compact_text(value: Any) -> str | None:
    if value is None:
        return None
    if isinstance(value, (dict, list)):
        return None
    text = str(value).strip()
    if not text:
        return None
    return "\n".join(line.rstrip() for line in text.splitlines()[:20]).strip()


if __name__ == "__main__":
    raise SystemExit(main())
