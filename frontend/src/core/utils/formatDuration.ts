import { parse } from "tinyduration";

/**
 * Format an ISO duration (e.g. "PT1H30M") into a human readable short string.
 * Examples:
 *  - "PT1H30M" -> "1h 30m"
 *  - "P1DT2H" -> "1d 2h"
 *  - invalid -> empty string
 */
export function formatDuration(iso: string): string {
  if (!iso) return "";
  try {
    const d = parse(iso);
    const parts: string[] = [];

    if (d.days && d.days > 0) parts.push(`${d.days}d`);
    if (d.hours && d.hours > 0) parts.push(`${d.hours}h`);
    if (d.minutes && d.minutes > 0) parts.push(`${d.minutes}m`);
    if (d.seconds && d.seconds > 0) parts.push(`${d.seconds}s`);

    if (parts.length === 0) return "0m";
    return parts.join(" ");
  } catch {
    return "";
  }
}
