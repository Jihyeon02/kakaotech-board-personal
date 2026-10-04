#!/usr/bin/env bash
set -euo pipefail

readonly MODE="${1:-}"
readonly CONFIG_FILE="${CONFIG_FILE:-$HOME/sysbench-stress.conf}"
readonly RESULT_ROOT="${RESULT_ROOT:-$HOME/sysbench-results}"
readonly PREPARE_THREADS="${PREPARE_THREADS:-8}"
readonly WARMUP_SECONDS="${WARMUP_SECONDS:-120}"
readonly RUN_SECONDS="${RUN_SECONDS:-300}"
readonly COOLDOWN_SECONDS="${COOLDOWN_SECONDS:-120}"
readonly THREADS_LIST="${THREADS_LIST:-1 2 4 8 16 32 64 128}"

usage() {
  cat <<'USAGE'
Usage:
  DB_INSTANCE_TYPE=m7i.large LOADGEN_INSTANCE_TYPE=c7i.large \
    ./run-sysbench-stress.sh prepare

  DB_INSTANCE_TYPE=m7i.large LOADGEN_INSTANCE_TYPE=c7i.large \
    ./run-sysbench-stress.sh run

  ./run-sysbench-stress.sh cleanup

Optional environment variables:
  CONFIG_FILE, RESULT_ROOT, PREPARE_THREADS, WARMUP_SECONDS,
  RUN_SECONDS, COOLDOWN_SECONDS, THREADS_LIST
USAGE
}

if [[ ! -r "$CONFIG_FILE" ]]; then
  echo "Config is not readable: $CONFIG_FILE" >&2
  exit 1
fi

if grep -q 'REPLACE_WITH_' "$CONFIG_FILE"; then
  echo "Replace all REPLACE_WITH_ values in $CONFIG_FILE first." >&2
  exit 1
fi

run_sysbench() {
  sysbench --config-file="$CONFIG_FILE" oltp_read_write "$@"
}

case "$MODE" in
  prepare)
    echo "PREPARE_START_UTC=$(date -u -Iseconds)"
    run_sysbench --threads="$PREPARE_THREADS" cleanup >/dev/null 2>&1 || true
    run_sysbench --threads="$PREPARE_THREADS" prepare
    echo "PREPARE_END_UTC=$(date -u -Iseconds)"
    ;;

  cleanup)
    echo "CLEANUP_START_UTC=$(date -u -Iseconds)"
    run_sysbench --threads="$PREPARE_THREADS" cleanup
    echo "CLEANUP_END_UTC=$(date -u -Iseconds)"
    ;;

  run)
    : "${DB_INSTANCE_TYPE:?Set DB_INSTANCE_TYPE, for example m7i.large}"
    : "${LOADGEN_INSTANCE_TYPE:?Set LOADGEN_INSTANCE_TYPE, for example c7i.large}"

    safe_db_type="${DB_INSTANCE_TYPE//[^a-zA-Z0-9._-]/_}"
    safe_load_type="${LOADGEN_INSTANCE_TYPE//[^a-zA-Z0-9._-]/_}"
    run_id="$(date -u +%Y%m%dT%H%M%SZ)_db-${safe_db_type}_load-${safe_load_type}"
    result_dir="${RESULT_ROOT}/${run_id}"
    windows_file="${result_dir}/cloudwatch-windows.csv"
    mkdir -p "$result_dir"

    {
      echo "run_id=$run_id"
      echo "db_instance_type=$DB_INSTANCE_TYPE"
      echo "loadgen_instance_type=$LOADGEN_INSTANCE_TYPE"
      echo "threads_list=$THREADS_LIST"
      echo "warmup_seconds=$WARMUP_SECONDS"
      echo "run_seconds=$RUN_SECONDS"
      echo "cooldown_seconds=$COOLDOWN_SECONDS"
      echo "created_utc=$(date -u -Iseconds)"
      sysbench --version
      uname -a
      echo "logical_cpus=$(nproc)"
      free -b
      echo
      echo "Sanitized sysbench configuration:"
      grep -v '^mysql-password=' "$CONFIG_FILE"
    } > "${result_dir}/metadata.txt"

    printf '%s\n' \
      'run_id,db_instance_type,loadgen_instance_type,threads,start_epoch,end_epoch,start_utc,end_utc,start_kst,end_kst,exit_code,log_file' \
      > "$windows_file"

    echo "RESULT_DIR=$result_dir"

    for threads in $THREADS_LIST; do
      warmup_log="${result_dir}/warmup_threads-${threads}.log"
      run_log="${result_dir}/stress_threads-${threads}.log"

      echo
      echo "===== WARMUP threads=$threads seconds=$WARMUP_SECONDS ====="
      run_sysbench \
        --threads="$threads" \
        --time="$WARMUP_SECONDS" \
        --report-interval=10 \
        --percentile=99 \
        --events=0 \
        run 2>&1 | tee "$warmup_log"

      start_epoch="$(date +%s)"
      start_utc="$(date -u -Iseconds)"
      start_kst="$(TZ=Asia/Seoul date -Iseconds)"

      echo
      echo "===== STRESS threads=$threads ====="
      echo "MEASURE_START_UTC=$start_utc"
      echo "MEASURE_START_KST=$start_kst"

      set +e
      run_sysbench \
        --threads="$threads" \
        --time="$RUN_SECONDS" \
        --report-interval=10 \
        --percentile=99 \
        --events=0 \
        run 2>&1 | tee "$run_log"
      sysbench_status="${PIPESTATUS[0]}"
      set -e

      end_epoch="$(date +%s)"
      end_utc="$(date -u -Iseconds)"
      end_kst="$(TZ=Asia/Seoul date -Iseconds)"

      echo "MEASURE_END_UTC=$end_utc"
      echo "MEASURE_END_KST=$end_kst"
      echo "SYSBENCH_EXIT_CODE=$sysbench_status"

      printf '%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n' \
        "$run_id" "$DB_INSTANCE_TYPE" "$LOADGEN_INSTANCE_TYPE" \
        "$threads" "$start_epoch" "$end_epoch" \
        "$start_utc" "$end_utc" "$start_kst" "$end_kst" \
        "$sysbench_status" "$run_log" >> "$windows_file"

      if ((sysbench_status != 0)); then
        echo "Sysbench failed for threads=$threads; stopping the scenario." >&2
        exit "$sysbench_status"
      fi

      echo "===== COOLDOWN seconds=$COOLDOWN_SECONDS ====="
      sleep "$COOLDOWN_SECONDS"
    done

    echo
    echo "DONE_RESULT_DIR=$result_dir"
    echo "CLOUDWATCH_WINDOWS=$windows_file"
    ;;

  *)
    usage
    exit 2
    ;;
esac
