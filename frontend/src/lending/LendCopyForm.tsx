import { useState, type FormEvent } from 'react'
import Alert, { type AlertColor } from '@mui/material/Alert'
import AlertTitle from '@mui/material/AlertTitle'
import Button from '@mui/material/Button'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { lendCopy, LendCopyError } from './lendCopy'

const DEFAULT_LOAN_DAYS = 15

interface Outcome {
  severity: AlertColor
  title?: string
  text: string
}

function todayPlusDays(days: number): string {
  const date = new Date()
  date.setDate(date.getDate() + days)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

/** "2026-12-10" -> "10/12/2026" */
function formatDate(isoDate: string): string {
  const [year, month, day] = isoDate.split('-')
  return `${day}/${month}/${year}`
}

function outcomeForError(error: unknown): Outcome {
  const notConnected: Outcome = {
    severity: 'warning',
    text: 'No pudimos conectar con el servidor. No se registró ningún préstamo.',
  }
  // fetch failed (TypeError) or the dev proxy/server answered 5xx: nothing was saved.
  if (!(error instanceof LendCopyError) || error.status >= 500) {
    return notConnected
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
            value={memberId}
            onChange={(e) => setMemberId(e.target.value)}
            placeholder="1712345678"
            required
            fullWidth
          />

          <TextField
            label="Fecha límite de devolución"
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

          {outcome && (
            <Alert severity={outcome.severity} role={outcome.severity === 'success' ? 'status' : 'alert'}>
              {outcome.title && <AlertTitle>{outcome.title}</AlertTitle>}
              {outcome.text}
            </Alert>
          )}
        </Stack>
      </CardContent>
    </Card>
  )
}
