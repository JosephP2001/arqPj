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

export class LendCopyError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

export async function lendCopy(request: LendCopyRequest): Promise<Loan> {
  const response = await fetch('/api/loans', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  if (!response.ok) {
    const body = (await response.json().catch(() => null)) as { message?: string } | null
    throw new LendCopyError(response.status, body?.message ?? `Unexpected error (${response.status})`)
  }

  return (await response.json()) as Loan
}
