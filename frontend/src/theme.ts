import { createTheme } from '@mui/material/styles'

// Tokens from Figma ("03 Tokens") and Confluence page "4 · Línea gráfica".
export const theme = createTheme({
  palette: {
    primary: { main: '#1F3A93', dark: '#172C70' },
    success: { main: '#137333' },
    error: { main: '#C5221F' },
    warning: { main: '#F9AB00', contrastText: '#1F1F1F' },
    text: { primary: '#1F1F1F', secondary: '#5F6368' },
    divider: '#DADCE0',
    background: { default: '#F8F9FA', paper: '#FFFFFF' },
  },
  shape: { borderRadius: 4 },
})
