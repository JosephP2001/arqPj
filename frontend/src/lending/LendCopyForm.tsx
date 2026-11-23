import { useState, type FormEvent } from 'react'
import Button from '@mui/material/Button'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { lendCopy, LendingApiError } from './api'
import { formatDate, isConnectionProblem, type Outcome } from './outcome'
import { OutcomeAlert } from './OutcomeAlert'

const DEFAULT_LOAN_DAYS = 15

function todayPlusDays(days: number): string {
  const date = new Date()
  date.setDate(date.getDate() + days)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

function outcomeForError(error: unknown): Outcome {
  if (isConnectionProblem(error) || !(error instanceof LendingApiError)) {
    return { severity: 'warning', text: 'No pudimos conectar con el servidor. No se registró ningún préstamo.' }
  }
  switch (error.status) {
    case 409:
      return {
        severity: 'error',
        title: 'Ejemplar no disponible',
        text: error.dueDate
          ? `Este ejemplar no está disponible: está prestado hasta el ${formatDate(error.dueDate)}.`
          : 'Este ejemplar no está disponible.',
      }
    case 404:
      return { severity: 'error', text: 'No existe un ejemplar con ese código.' }
    default:
      return {
        severity: 'error',
        text: 'Revisa los datos: todos los campos son obligatorios y la fecha límite debe ser posterior a hoy.',
      }
  }
}

export function LendCopyForm() {
  const [copyCode, setCopyCode] = useState('')
  const [memberId, setMemberId] = useState('')
  const [dueDate, setDueDate] = useState(() => todayPlusDays(DEFAULT_LOAN_DAYS))
  const [submitting, setSubmitting] = useState(false)
  const [outcome, setOutcome] = useState<Outcome | null>(null)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setOutcome(null)
    try {
      const loan = await lendCopy({ copyCode: copyCode.trim(), memberId: memberId.trim(), dueDate })
      setOutcome({
        severity: 'success',
        text: `Préstamo registrado. Devolver hasta el ${formatDate(loan.dueDate)}.`,
      })
    } catch (e) {
      setOutcome(outcomeForError(e))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card variant="outlined">
      <CardContent>
        <Stack component="form" spacing={3} onSubmit={handleSubmit}>
          <Typography variant="h5" component="h1">
            Prestar ejemplar
          </Typography>

          <TextField
            label="Código de ejemplar"
            helperText="Escanea o escribe el código"
            value={copyCode}
            onChange={(e) => setCopyCode(e.target.value)}
            placeholder="EJ-00123"
            required
            autoFocus
            fullWidth
          />

          <TextField
            label="Lector (cédula o código)"
            helperText="Cédula o código del lector"
            value={memberId}
            onChange={(e) => setMemberId(e.target.value)}
            placeholder="1712345678"
            required
            fullWidth
          />

          <TextField
            label="Fecha límite de devolución"
            helperText="Por defecto: hoy + 15 días"
            type="date"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            required
            fullWidth
            slotProps={{ inputLabel: { shrink: true } }}
          />

          <Button type="submit" variant="contained" size="large" disabled={submitting}>
            Prestar
          </Button>

          {outcome && <OutcomeAlert outcome={outcome} />}
        </Stack>
      </CardContent>
    </Card>
  )
}
