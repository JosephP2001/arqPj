import { useState, type FormEvent } from 'react'
import { lendCopy, LendCopyError, type Loan } from './lendCopy'
import './LendCopyForm.css'

const DEFAULT_LOAN_DAYS = 15

function todayPlusDays(days: number): string {
  const date = new Date()
  date.setDate(date.getDate() + days)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

export function LendCopyForm() {
  const [copyCode, setCopyCode] = useState('')
  const [memberId, setMemberId] = useState('')
  const [dueDate, setDueDate] = useState(() => todayPlusDays(DEFAULT_LOAN_DAYS))
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [loan, setLoan] = useState<Loan | null>(null)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)
    setLoan(null)
    try {
      setLoan(await lendCopy({ copyCode: copyCode.trim(), memberId: memberId.trim(), dueDate }))
    } catch (e) {
      setError(e instanceof LendCopyError ? e.message : 'Could not reach the server')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form className="lend-copy" onSubmit={handleSubmit}>
      <h1>Lend copy</h1>

      <label>
        Copy code
        <input value={copyCode} onChange={(e) => setCopyCode(e.target.value)} placeholder="UCE-0001" required />
      </label>

      <label>
        Member ID
        <input value={memberId} onChange={(e) => setMemberId(e.target.value)} placeholder="1712345678" required />
      </label>

      <label>
        Due date
        <input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} required />
      </label>

      <button type="submit" disabled={submitting}>
        {submitting ? 'Lending…' : 'Lend'}
      </button>

      {error && (
        <p className="lend-copy__message lend-copy__message--error" role="alert">
          {error}
        </p>
      )}
      {loan && (
        <p className="lend-copy__message lend-copy__message--success" role="status">
          Copy {loan.copyCode} lent to Member {loan.memberId} until {loan.dueDate}.
        </p>
      )}
    </form>
  )
}
