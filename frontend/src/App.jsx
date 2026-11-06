import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import Auth from './pages/Auth'
import Dashboard from './pages/Dashboard'
import Tickets from './pages/Tickets'
import RaiseIssue from './pages/RaiseIssue'
import TicketDetails from './pages/TicketDetails'
import Profile from './pages/Profile'
import Notifications from './pages/Notifications'
import Management from './pages/Management'
import { getUser } from './services/api'

function Protected({ children, roles }) {
  const user = getUser()
  if (!localStorage.getItem('intellidesk_token') || !user) return <Navigate to="/login" replace />
  if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />
  return <Layout>{children}</Layout>
}
export default function App(){return <BrowserRouter><Routes>
  <Route path="/login" element={<Auth/>}/><Route path="/register" element={<Auth register/>}/>
  <Route path="/" element={<Protected><Dashboard/></Protected>}/>
  <Route path="/raise" element={<Protected roles={['USER']}><RaiseIssue/></Protected>}/>
  <Route path="/tickets" element={<Protected><Tickets/></Protected>}/>
  <Route path="/tickets/:id" element={<Protected><TicketDetails/></Protected>}/>
  <Route path="/profile" element={<Protected><Profile/></Protected>}/>
  <Route path="/notifications" element={<Protected><Notifications/></Protected>}/>
  <Route path="/workers" element={<Protected roles={['ADMIN']}><Management type="workers"/></Protected>}/>
  <Route path="/sla" element={<Protected roles={['ADMIN']}><Management type="sla"/></Protected>}/>
  <Route path="/history" element={<Protected roles={['ADMIN']}><Management type="history"/></Protected>}/>
  <Route path="*" element={<Navigate to="/" replace/>}/>
</Routes></BrowserRouter>}
