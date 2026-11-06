import { useEffect, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { request, getUser } from '../services/api'
import StatusBadge from '../components/StatusBadge'
import Notice from '../components/Notice'

export default function Tickets() {
  const [tickets, setTickets] = useState([])
  const [workers, setWorkers] = useState([])
  const [selectedWorkers, setSelectedWorkers] = useState({})
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(true)
  const [assigningId, setAssigningId] = useState(null)

  const role = getUser()?.role

  const loadTickets = useCallback(async () => {
    const data = await request('/api/tickets')
    setTickets(data)
  }, [])

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true)
        setError('')

        const data = await request('/api/tickets')
        setTickets(data)

        if (role === 'ADMIN') {
          const workerData = await request('/api/admin/workers')
          setWorkers(workerData.filter(worker => worker.available))
        }
      } catch (e) {
        setError(e.message)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [role])

  async function handleAssign(ticketId) {
    const workerId = selectedWorkers[ticketId]

    if (!workerId) {
      setError('Please select a worker first.')
      setMessage('')
      return
    }

    try {
      setAssigningId(ticketId)
      setError('')
      setMessage('')

      await request(`/api/tickets/${ticketId}/assign`, {
        method: 'POST',
        body: JSON.stringify({
          workerId: Number(workerId)
        })
      })

      await loadTickets()
      setMessage(`Ticket #${ticketId} assigned successfully!`)
    } catch (e) {
      setError(e.message)
    } finally {
      setAssigningId(null)
    }
  }

  return (
    <div>
      <h1 className="text-2xl font-bold mb-1">
        {role === 'ADMIN'
          ? 'All tickets'
          : role === 'WORKER'
            ? 'Assigned tickets'
            : 'My tickets'}
      </h1>

      <p className="text-slate-500 mb-6">
        Track issue ownership, priority and progress.
      </p>

      <Notice error={error} />

      {message && (
        <div className="mb-4 rounded-lg bg-green-50 border border-green-200 p-3 text-green-700 text-sm">
          {message}
        </div>
      )}

      <div className="bg-white border rounded-xl overflow-hidden">
        {loading ? (
          <p className="p-6">Loading...</p>
        ) : tickets.length === 0 ? (
          <p className="p-8 text-slate-500">No tickets available.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-slate-50 text-slate-500">
                <tr>
                  {[
                    'Ticket',
                    'Category',
                    'Priority',
                    'Assigned worker',
                    'Status',
                    'SLA deadline',
                    ...(role === 'ADMIN' ? ['Action'] : [])
                  ].map(column => (
                    <th
                      key={column}
                      className="p-4 text-left font-medium"
                    >
                      {column}
                    </th>
                  ))}
                </tr>
              </thead>

              <tbody>
                {tickets.map(ticket => (
                  <tr
                    key={ticket.id}
                    className="border-t hover:bg-slate-50"
                  >
                    <td className="p-4">
                      <Link
                        to={`/tickets/${ticket.id}`}
                        className="font-semibold text-brand"
                      >
                        #{ticket.id} · {ticket.title}
                      </Link>
                    </td>

                    <td className="p-4">{ticket.category}</td>

                    <td className="p-4">{ticket.priority}</td>

                    <td className="p-4">
                      {ticket.assignedWorkerName || 'Unassigned'}
                    </td>

                    <td className="p-4">
                      <StatusBadge status={ticket.status} />
                    </td>

                    <td className="p-4">
                      {ticket.slaDeadline
                        ? new Date(ticket.slaDeadline).toLocaleString()
                        : '—'}
                    </td>

                    {role === 'ADMIN' && (
                      <td className="p-4 min-w-64">
                        {ticket.assignedWorkerName ||ticket.status === 'CLOSED' ||
                        ticket.status === 'RESOLVED' ? (
                          <span className="text-slate-400">
                            Assignment unavailable
                          </span>
                        ) : (
                          <div className="flex flex-col gap-2">
                            <select
                              className="border rounded-lg p-2 w-full"
                              value={selectedWorkers[ticket.id] || ''}
                              onChange={e =>
                                setSelectedWorkers(previous => ({
                                  ...previous,
                                  [ticket.id]: e.target.value
                                }))
                              }
                            >
                              <option value="">Select worker</option>

                              {workers.map(worker => (
                                <option
                                  key={worker.id}
                                  value={worker.id}
                                >
                                  {worker.name} — {worker.jobRole}
                                </option>
                              ))}
                            </select>

                            <button
                              onClick={() => handleAssign(ticket.id)}
                              disabled={assigningId === ticket.id}
                              className="bg-brand text-white rounded-lg px-3 py-2 font-medium disabled:opacity-50"
                            >
                              {assigningId === ticket.id
                                ? 'Assigning...'
                                : 'Assign Worker'}
                            </button>
                          </div>
                        )}
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}