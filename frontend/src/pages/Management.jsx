import { useEffect, useState } from 'react'
import { request } from '../services/api'
import Notice from '../components/Notice'

export default function Management({ type }) {
  const [data, setData] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const endpoint =
    type === 'workers'
      ? '/api/admin/workers'
      : type === 'sla'
        ? '/api/admin/sla'
        : '/api/admin/history'

  useEffect(() => {
    setLoading(true)
    setError('')

    request(endpoint)
      .then(setData)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [endpoint])

  const title =
    type === 'workers'
      ? 'Worker management & workload'
      : type === 'sla'
        ? 'SLA monitoring'
        : 'Ticket history'

  return (
    <div>
      <h1 className="text-2xl font-bold mb-1">{title}</h1>

      <p className="text-slate-500 mb-6">
        Admin workspace data from the backend.
      </p>

      <Notice error={error} />

      <div className="bg-white border rounded-xl overflow-hidden">
        {loading ? (
          <p className="p-6">Loading...</p>
        ) : data.length === 0 ? (
          <p className="p-6 text-slate-500">
            No records found, or this endpoint is not available for your account.
          </p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-slate-50">
                <tr>
                  {Object.keys(data[0] || {}).map(key => (
                    <th
                      key={key}
                      className="text-left p-4 capitalize"
                    >
                      {key.replace(/([A-Z])/g, ' $1')}
                    </th>
                  ))}
                </tr>
              </thead>

              <tbody>
                {data.map((row, index) => (
                  <tr
                    key={row.id || index}
                    className="border-t"
                  >
                    {Object.entries(row).map(([key, value]) => (
                      <td key={key} className="p-4">
                        {typeof value === 'object' && value !== null
                          ? JSON.stringify(value)
                          : String(value ?? '—')}
                      </td>
                    ))}
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
