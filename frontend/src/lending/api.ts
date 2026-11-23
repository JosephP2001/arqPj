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

export interface Return {
  copyCode: string
  memberId: string
  returnedOn: string
}

/** Error answered by the lending API: status code, English message and, for BR-01 (409), the due date of the active Loan. */
export class LendingApiError extends Error {
  readonly status: number
  readonly dueDate: string | null

  constructor(status: number, message: string, dueDate: string | null = null) {
    super(message)
    this.status = status
    this.dueDate = dueDate
  }
}

async function post<T>(url: string, body?: unknown): Promise<T> {
  const response = await fetch(url, {
    method: 'POST',
    headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body),
  })

  if (!response.ok) {
    const error = (await response.json().catch(() => null)) as { message?: string; dueDate?: string | null } | null
    throw new LendingApiError(
      response.status,
      error?.message ?? `Unexpected error (${response.status})`,
      error?.dueDate ?? null,
    )
  }

  return (await response.json()) as T
}

/** LendCopy: POST /api/loans (BR-01). */
export function lendCopy(request: LendCopyRequest): Promise<Loan> {
  return post<Loan>('/api/loans', request)
}

/** ReturnCopy: POST /api/loans/{copyCode}/return (BR-02). */
export function returnCopy(copyCode: string): Promise<Return> {
  return post<Return>(`/api/loans/${encodeURIComponent(copyCode)}/return`)
}
