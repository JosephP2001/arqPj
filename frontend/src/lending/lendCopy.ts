export interface LendCopyRequest {
  copyCode: string
  memberId: string
  dueDate: string
}

export interface Loan {
  copyCode: string
  memberId: string
  lentOn: string
  dueDate: string
}

/** Error answered by the API: status code, English message and, for BR-01 (409), the due date of the active Loan. */
export class LendCopyError extends Error {
  readonly status: number
  readonly dueDate: string | null

  constructor(status: number, message: string, dueDate: string | null = null) {
    super(message)
    this.status = status
    this.dueDate = dueDate
  }
}

export async function lendCopy(request: LendCopyRequest): Promise<Loan> {
  const response = await fetch('/api/loans', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  if (!response.ok) {
    const body = (await response.json().catch(() => null)) as { message?: string; dueDate?: string | null } | null
    throw new LendCopyError(
      response.status,
      body?.message ?? `Unexpected error (${response.status})`,
      body?.dueDate ?? null,
    )
  }

  return (await response.json()) as Loan
}
