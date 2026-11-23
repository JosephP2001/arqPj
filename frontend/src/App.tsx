import { useState } from 'react'
import AppBar from '@mui/material/AppBar'
import Container from '@mui/material/Container'
import Tab from '@mui/material/Tab'
import Tabs from '@mui/material/Tabs'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import AssignmentReturnIcon from '@mui/icons-material/AssignmentReturn'
import LibraryAddIcon from '@mui/icons-material/LibraryAdd'
import MenuBookIcon from '@mui/icons-material/MenuBook'
import { LendCopyForm } from './lending/LendCopyForm'
import { ReturnCopyForm } from './lending/ReturnCopyForm'

type Screen = 'lend' | 'return'

function App() {
  const [screen, setScreen] = useState<Screen>('lend')

  return (
    <>
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <MenuBookIcon sx={{ mr: 1.5 }} aria-hidden />
          <Typography variant="h6" component="span" sx={{ flexGrow: 1 }}>
            Biblioteca UCE · Préstamos
          </Typography>
        </Toolbar>
        <Tabs
          value={screen}
          onChange={(_, value: Screen) => setScreen(value)}
          textColor="inherit"
          sx={{ px: 2, '& .MuiTabs-indicator': { backgroundColor: 'common.white' } }}
          aria-label="Operaciones de préstamo"
        >
          <Tab value="lend" label="Prestar" icon={<LibraryAddIcon />} iconPosition="start" />
          <Tab value="return" label="Devolver" icon={<AssignmentReturnIcon />} iconPosition="start" />
        </Tabs>
      </AppBar>
      <Container component="main" maxWidth="sm" sx={{ py: 4 }}>
        {screen === 'lend' ? <LendCopyForm /> : <ReturnCopyForm />}
      </Container>
    </>
  )
}

export default App
