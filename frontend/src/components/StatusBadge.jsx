export default function StatusBadge({ status }) {
  const styles = { OPEN:'bg-blue-50 text-blue-700', ASSIGNED:'bg-violet-50 text-violet-700', IN_PROGRESS:'bg-amber-50 text-amber-700', RESOLVED:'bg-emerald-50 text-emerald-700', CLOSED:'bg-slate-100 text-slate-600', ESCALATED:'bg-red-50 text-red-700' }
  return <span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ${styles[status] || 'bg-slate-100 text-slate-600'}`}>{(status || 'UNKNOWN').replace('_',' ')}</span>
}
