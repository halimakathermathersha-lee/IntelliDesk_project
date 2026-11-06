export default function Notice({ error, success }) {
  if (!error && !success) return null
  return <div className={`rounded-lg px-4 py-3 text-sm mb-4 ${error ? 'bg-red-50 text-red-700 border border-red-100' : 'bg-emerald-50 text-emerald-700 border border-emerald-100'}`}>{error || success}</div>
}
