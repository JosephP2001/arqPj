import { useState, type FormEvent } from 'react'
import Button from '@mui/material/Button'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { LendingApiError, returnCopy } from './api'
import { isConnectionProblem, type Outcome } from './outcome'
import { OutcomeAlert } from './OutcomeAlert'

function outcomeForError(error: unknown): Outcome {
  if (isConnectionProblem(error) || !(error instanceof LendingApiError)) {
    return { severity: 'warning', text: 'No pudimos conectar con el servidor. No se registró ninguna devolución.' }
  }
  switch (error.status) {
    case 409:
      return {
        severity: 'error',
        title: 'Ejemplar no prestado',
        text: 'Este ejemplar no tiene un préstamo activo.',
      }
    case 404:
      return { severity: 'error', text: 'No existe un ejemplar con ese código.' }
    default:
      return { severity: 'error', text: 'Revisa los datos: el código de ejemplar es obligatorio.' }
  }
}

/** Figma D04 "Devolver ejemplar": calls ReturnCopy (BR-02). */
export function ReturnCopyForm() {
  const [copyCode, setCopyCode] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [outcome, setOutcome] = useState<Outcome | null>(null)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setOutcome(null)
    try {
      await returnCopy(copyCode.trim())
      setOutcome({ severity: 'success', text: 'Ejemplar devuelto. Ya está disponible.' })
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
            Devolver ejemplar
          </Typography>

          <TextField
            label="Código de ejemplar"
            helperText="Escanea el código del ejemplar que el lector devuelve"
            value={copyCode}
            onChange={(e) => setCopyCode(e.target.value)}
            placeholder="EJ-00456"
            required
            autoFocus
            fullWidth
          />

          <Button type="submit" variant="contained" size="large" disabled={submitting}>
            Devolver
          </Button>

          {outcome && <OutcomeAlert outcome={outcome} />}
        </Stack>
      </CardContent>
    </Card>
  )
}
