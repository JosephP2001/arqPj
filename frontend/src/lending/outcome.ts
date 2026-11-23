import type { AlertColor } from '@mui/material/Alert'
import { LendingApiError } from './api'

/** What the screen shows after an operation: an Alert with an optional title. */
export interface Outcome {
  severity: AlertColor
  title?: string
  text: string
}

/** "2026-12-10" -> "10/12/2026" */
export function formatDate(isoDate: string): string {
  const [year, month, day] = isoDate.split('-')
  return `${day}/${month}/${year}`
}

/**
 * The request never reached the use case: fetch failed (TypeError) or the dev proxy/server answered 5xx.
 * Nothing was saved, so the screen says so.
 */
export function isConnectionProblem(error: unknown): boolean {
  return !(error instanceof LendingApiError) || error.status >= 500
}
