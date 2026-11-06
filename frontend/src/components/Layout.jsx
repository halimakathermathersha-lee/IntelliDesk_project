import { NavLink, useNavigate } from 'react-router-dom'
import { getUser, logout } from '../services/api'

const links = {
  USER: [['Overview','/'],['Raise an issue','/raise'],['My tickets','/tickets'],['Notifications','/notifications'],['My profile','/profile']],
  WORKER: [['Overview','/'],['Assigned tickets','/tickets'],['My profile','/profile']],
  ADMIN: [['Overview','/'],['All tickets','/tickets'],['Workers & workload','/workers'],['SLA monitoring','/sla'],['Ticket history','/history'],['My profile','/profile']]
}
export default function Layout({ children }) {
  const user = getUser()
  const navigate = useNavigate()
  const menu = links[user?.role] || links.USER
  return <div className="min-h-screen md:flex">
    <aside className="w-full md:w-64 bg-white border-r border-slate-200 md:min-h-screen p-5">
      <div className="flex items-center gap-3 mb-8"><div className="h-10 w-10 rounded-xl bg-brand text-white grid place-items-center font-bold">ID</div><div><div className="font-bold text-lg">IntelliDesk</div><div className="text-xs text-slate-500">Issue resolution</div></div></div>
      <div className="text-xs uppercase tracking-wider text-slate-400 mb-3">Workspace</div>
      <nav className="space-y-1">{menu.map(([label,to]) => <NavLink key={to} to={to} end={to==='/'} className={({isActive}) => `block rounded-lg px-3 py-2.5 text-sm ${isActive ? 'bg-blue-50 text-brand font-semibold' : 'text-slate-600 hover:bg-slate-50'}`}>{label}</NavLink>)}</nav>
      <div className="mt-8 rounded-xl bg-slate-50 p-3"><div className="font-semibold text-sm">{user?.name || 'Guest'}</div><div className="text-xs text-slate-500 mt-1">{user?.role || 'Not signed in'}</div><button onClick={() => { logout(); navigate('/login') }} className="mt-3 text-sm text-red-600">Sign out</button></div>
    </aside>
    <main className="flex-1 min-w-0"><header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-6"><div className="font-semibold text-slate-700">Workspace</div><div className="text-sm text-slate-500">Employee support portal</div></header><div className="p-5 md:p-8 max-w-7xl mx-auto">{children}</div></main>
  </div>
}
