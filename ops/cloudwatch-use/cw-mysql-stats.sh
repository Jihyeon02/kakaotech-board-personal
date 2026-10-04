#!/usr/bin/env bash
set -euo pipefail

: "${MYSQL_CONTAINER:?MYSQL_CONTAINER is required}"
: "${MYSQL_USER:?MYSQL_USER is required}"
: "${MYSQL_PASSWORD:?MYSQL_PASSWORD is required}"

send_metric() {
  printf '%s\n' "$1" > /dev/udp/127.0.0.1/8125
}

read -r -d '' SQL <<'SQL' || true
SHOW GLOBAL STATUS
WHERE Variable_name IN (
  'Innodb_buffer_pool_reads',
  'Innodb_buffer_pool_read_requests',
  'Innodb_buffer_pool_pages_free',
  'Innodb_buffer_pool_pages_dirty',
  'Innodb_buffer_pool_pages_total',
  'Innodb_buffer_pool_wait_free',
  'Innodb_data_pending_reads',
  'Innodb_data_pending_writes',
  'Innodb_data_pending_fsyncs',
  'Innodb_log_waits',
  'Threads_running',
  'Threads_connected',
  'Innodb_row_lock_current_waits',
  'Innodb_row_lock_waits',
  'Innodb_row_lock_time'
);
SELECT 'lock_deadlocks', COUNT
FROM INFORMATION_SCHEMA.INNODB_METRICS
WHERE NAME = 'lock_deadlocks';
SQL

rows="$(
  MYSQL_PWD="$MYSQL_PASSWORD" docker exec --env MYSQL_PWD "$MYSQL_CONTAINER" \
    mysql --batch --skip-column-names --user="$MYSQL_USER" --execute="$SQL"
)"

while IFS=$'\t' read -r source_name value; do
  [[ -z "${source_name:-}" || -z "${value:-}" ]] && continue

  case "$source_name" in
    Innodb_buffer_pool_reads) metric="mysql_buffer_pool_reads" ;;
    Innodb_buffer_pool_read_requests) metric="mysql_buffer_pool_read_requests" ;;
    Innodb_buffer_pool_pages_free) metric="mysql_buffer_pool_pages_free" ;;
    Innodb_buffer_pool_pages_dirty) metric="mysql_buffer_pool_pages_dirty" ;;
    Innodb_buffer_pool_pages_total) metric="mysql_buffer_pool_pages_total" ;;
    Innodb_buffer_pool_wait_free) metric="mysql_buffer_pool_wait_free" ;;
    Innodb_data_pending_reads) metric="mysql_data_pending_reads" ;;
    Innodb_data_pending_writes) metric="mysql_data_pending_writes" ;;
    Innodb_data_pending_fsyncs) metric="mysql_data_pending_fsyncs" ;;
    Innodb_log_waits) metric="mysql_log_waits" ;;
    Threads_running) metric="mysql_threads_running" ;;
    Threads_connected) metric="mysql_threads_connected" ;;
    Innodb_row_lock_current_waits) metric="mysql_row_lock_current_waits" ;;
    Innodb_row_lock_waits) metric="mysql_row_lock_waits" ;;
    Innodb_row_lock_time) metric="mysql_row_lock_time_ms" ;;
    lock_deadlocks) metric="mysql_deadlocks" ;;
    *) continue ;;
  esac

  # Cumulative MySQL counters are intentionally sent as gauges. The dashboard
  # uses RATE() to isolate the benchmark interval and handles server restarts.
  send_metric "${metric}:${value}|g"
done <<< "$rows"
