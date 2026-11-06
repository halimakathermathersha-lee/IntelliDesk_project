import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { request, getUser } from '../services/api'
import StatusBadge from '../components/StatusBadge'
import Notice from '../components/Notice'

export default function Dashboard() {
  const [tickets,setTickets] = useState([])
  const [error,setError] = useState('')
  const [loading,setLoading] = useState(true)
  const user = getUser()

  useEffect(() => {
     request('/api/tickets')
     .then(setTickets)
     .catch(e=>setError(e.message))
     .finally(()=>setLoading(false)) }, [])

  const counts = ['OPEN','IN_PROGRESS','RESOLVED']
  .map(status=>({status,count:tickets
    .filter(t=>t.status===status).length}))


  return(
   <div>
    <div className="flex flex-wrap justify-between gap-4 mb-7">
      <div>
        <h1 className="text-2xl font-bold">Good to see you, {user?.name?.split(' ')[0] || 'there'}</h1>
        <p className="text-slate-500 mt-1">Here is the latest from your support workspace.</p></div>

        {user?.role==='USER' && <Link to="/raise" className="bg-brand text-white rounded-lg px-4 py-2.5 text-sm font-semibold">+ Raise an issue</Link>}</div>

    <Notice error={error}/>
    <div className="grid sm:grid-cols-2 xl:grid-cols-3 gap-4 mb-7">
      {counts.map(c=><div key={c.status} className="bg-white border border-slate-200 rounded-xl p-5">
        <div className="text-sm text-slate-500">
          {c.status.replace('_',' ')} tickets
          </div>
          <div className="text-3xl font-bold mt-3">
            {c.count}</div></div>)}
            </div>

    <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
      <div className="p-5 border-b flex justify-between">
        <h2 className="font-semibold">Recent tickets</h2>
        <Link to="/tickets" className="text-brand text-sm font-semibold">View all</Link>
        </div>
    {loading ? <p className="p-6 text-slate-500">Loading tickets…</p> : tickets.length===0 ? <div className="p-10 text-center"><div className="font-semibold">No tickets yet</div>
    <p className="text-slate-500 text-sm mt-1">Issues you can access will appear here.</p>
    </div> : <div className="overflow-x-auto"><table className="w-full text-sm">
      <thead className="bg-slate-50 text-slate-500">
        <tr><th className="text-left p-4">Ticket</th>
      <th className="text-left p-4">Category</th>
      <th className="text-left p-4">Status</th>
      <th className="text-left p-4">Priority</th>
      </tr></thead>
      
      <tbody>
        {tickets.slice(0,8).map(t=><tr key={t.id} className="border-t">
          <td className="p-4">
            <Link className="font-semibold text-brand" to={`/tickets/${t.id}`}>#{t.id} · {t.title}</Link>
            </td>
            <td className="p-4">{t.category}</td>
            <td className="p-4"><StatusBadge status={t.status}/></td>
            <td className="p-4">{t.priority}</td></tr>)}
            
            </tbody>
            </table></div>}
            
            </div>
  </div>
  )
}
