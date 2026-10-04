#!/usr/bin/env bash
set -euo pipefail

readonly STATSD_HOST="127.0.0.1"
readonly STATSD_PORT="8125"
readonly STATE_DIR="/var/lib/sysbench-monitor"
readonly SWAP_STATE="${STATE_DIR}/swap.prev"

send_metric() {
  printf '%s\n' "$1" >"/dev/udp/${STATSD_HOST}/${STATSD_PORT}"
}

load1="$(awk '{print $1}' /proc/loadavg)"
send_metric "system_load1:${load1}|g"

# EC2 images commonly have no swap. In that case no swap metrics are emitted,
# so two permanently-zero custom metric series are not billed.
swap_total_kib="$(awk '$1 == "SwapTotal:" {print $2}' /proc/meminfo)"
if [[ "${swap_total_kib:-0}" -gt 0 ]]; then
  install -d -m 0755 "$STATE_DIR"

  now="$(date +%s)"
  current_in="$(awk '$1 == "pswpin" {print $2}' /proc/vmstat)"
  current_out="$(awk '$1 == "pswpout" {print $2}' /proc/vmstat)"
  page_size="$(getconf PAGESIZE)"

  if [[ -r "$SWAP_STATE" ]]; then
    read -r previous_time previous_in previous_out < "$SWAP_STATE" || true
    elapsed=$((now - previous_time))
    delta_in=$((current_in - previous_in))
    delta_out=$((current_out - previous_out))

    if ((elapsed > 0)); then
      ((delta_in < 0)) && delta_in=0
      ((delta_out < 0)) && delta_out=0

      in_rate="$(awk -v d="$delta_in" -v p="$page_size" -v t="$elapsed" \
        'BEGIN {printf "%.3f", d * p / t}')"
      out_rate="$(awk -v d="$delta_out" -v p="$page_size" -v t="$elapsed" \
        'BEGIN {printf "%.3f", d * p / t}')"

      send_metric "swap_in_bytes_per_sec:${in_rate}|g"
      send_metric "swap_out_bytes_per_sec:${out_rate}|g"
    fi
  fi

  printf '%s %s %s\n' "$now" "$current_in" "$current_out" > "$SWAP_STATE"
fi
