import AppBar from '@mui/material/AppBar'
import Container from '@mui/material/Container'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import MenuBookIcon from '@mui/icons-material/MenuBook'
import { LendCopyForm } from './lending/LendCopyForm'

function App() {
  return (
    <>
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <MenuBookIcon sx={{ mr: 1.5 }} aria-hidden />
          <Typography variant="h6" component="span">
            Biblioteca UCE · Préstamos
          </Typography>
        </Toolbar>
      </AppBar>
      <Container component="main" maxWidth="sm" sx={{ py: 4 }}>
        <LendCopyForm />
      </Container>
    </>
  )
}

export default App
