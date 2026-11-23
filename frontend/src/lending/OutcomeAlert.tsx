import Alert from '@mui/material/Alert'
import AlertTitle from '@mui/material/AlertTitle'
import type { Outcome } from './outcome'

export function OutcomeAlert({ outcome }: { outcome: Outcome }) {
  return (
    <Alert severity={outcome.severity} role={outcome.severity === 'success' ? 'status' : 'alert'}>
      {outcome.title && <AlertTitle>{outcome.title}</AlertTitle>}
      {outcome.text}
    </Alert>
  )
}
